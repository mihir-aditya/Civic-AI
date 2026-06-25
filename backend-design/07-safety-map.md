# 07 - Safety Map & Spatial Queries

The **Safety Map** screen lists public hazards dynamically within the user's active viewport or nearby radius. To prevent network overhead and ensure smooth rendering, the API supports bounding-box coordinates queries, category/severity filtering, and marker clustering.

---

## 1. Map Viewport Bounds Query
To load markers dynamically as the user scrolls or zooms, the Android map screen queries the backend by sending coordinates bounding the active screen view.

* **Endpoint**: `GET /api/v1/maps/hazards`
* **Query Parameters**:
  * `north_east_lat`: NE corner latitude (e.g. `25.22000000`)
  * `north_east_lng`: NE corner longitude (e.g. `75.87000000`)
  * `south_west_lat`: SW corner latitude (e.g. `25.17000000`)
  * `south_west_lng`: SW corner longitude (e.g. `75.81000000`)
  * `categories`: Comma-separated category IDs (e.g. `1,3`) - *Optional*
  * `severities`: Comma-separated severity strings (e.g. `high,critical`) - *Optional*
  * `status`: Filter by status name (e.g. `Pending`, `Verified`) - *Optional*

* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": [
      {
        "id": 501,
        "latitude": 25.21000000,
        "longitude": 75.86000000,
        "category_id": 3,
        "severity": "High",
        "status": "Verified"
      },
      {
        "id": 502,
        "latitude": 25.17400000,
        "longitude": 75.83600000,
        "category_id": 5,
        "severity": "Medium",
        "status": "Pending"
      }
    ]
  }
  ```

---

## 2. Radius Search (Nearby Queries)
Finds active hazards within a specific distance radius (meters) from the user's current GPS position.

* **Endpoint**: `GET /api/v1/maps/nearby`
* **Query Parameters**:
  * `latitude`: User's latitude (e.g. `25.18200000`)
  * `longitude`: User's longitude (e.g. `75.82800000`)
  * `radius_meters`: Distance limit in meters (default: `5000`)
* **Success Response (200 OK)**: Returns a list of Standard Hazard DTOs within the radius, sorted by distance ascending.

---

## 3. Server-Side Clustering & Pagination
When zoomed out, loading thousands of individual pins freezes mobile maps. The server supports viewport clustering:
* **Clustering Trigger**: If the bounding box zoom level is low (e.g., zoom < 12, query parameter `zoom_level=10`), the server groups points within a geo-grid (using Geohashes or spatial indexing) and returns cluster summaries instead of individual markers.
* **Cluster JSON Format**:
  ```json
  {
    "success": true,
    "data": {
      "clusters": [
        {
          "center_lat": 25.18000000,
          "center_lng": 75.83000000,
          "count": 14,
          "highest_severity": "Critical"
        }
      ]
    }
  }
  ```

---

## 4. Real-time Map Updates
* When a new hazard is successfully submitted or resolved:
  * The backend triggers an FCM silent push data payload to all active device registries in the municipal area.
  * The Android client receives the payload and adds/removes the marker pin dynamically without requiring a full manual refresh.
