# 04 - Request & Response Payload Models

This document details the exact JSON payloads, headers, validation rules, and success/error status structures for each **NagarRakshak** REST API endpoint.

---

## 1. Global Request Headers
Unless stated otherwise, all write APIs or authenticated endpoints require:
```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer <JWT_Access_Token>
```

---

## 2. Authentication Payloads

### POST /auth/register
* **Auth Required**: No
* **Request JSON**:
  ```json
  {
    "name": "John Doe",
    "email": "johndoe@example.com",
    "password": "SecurePassword123",
    "phone_number": "+919876543210",
    "role": "citizen"
  }
  ```
* **Success Response (201 Created)**:
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "data": {
      "user": {
        "id": 101,
        "name": "John Doe",
        "email": "johndoe@example.com",
        "role": "citizen",
        "reputation_points": 0
      },
      "tokens": {
        "access_token": "eyJhbGciOi...",
        "refresh_token": "rr_rf_938fd..."
      }
    }
  }
  ```
* **Validation Failure (422 Unprocessable Entity)**:
  ```json
  {
    "success": false,
    "message": "Validation failed",
    "errors": {
      "email": ["The email has already been taken."],
      "password": ["The password must be at least 8 characters."]
    }
  }
  ```

### POST /auth/login
* **Auth Required**: No
* **Request JSON (Email/Password)**:
  ```json
  {
    "email": "johndoe@example.com",
    "password": "SecurePassword123"
  }
  ```
* **Request JSON (Google OAuth)**:
  ```json
  {
    "google_id_token": "id_token_from_google_play_services"
  }
  ```
* **Success Response (200 OK)**:
  Same structure as Registration Response.

### POST /auth/logout
* **Auth Required**: Yes
* **Request JSON**: None
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Logged out successfully"
  }
  ```

### POST /auth/refresh
* **Auth Required**: No (Uses Refresh Token)
* **Request JSON**:
  ```json
  {
    "refresh_token": "rr_rf_938fd..."
  }
  ```
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "access_token": "eyJhbGciOi...",
      "refresh_token": "rr_rf_new94..."
    }
  }
  ```

---

## 3. AI Service Payloads

### POST /ai/analyze
* **Auth Required**: Yes
* **Request Encoding**: `multipart/form-data`
* **Request Params**:
  * `image`: Binary file (JPEG/PNG)
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "ai_analysis_id": 402,
      "predicted_category": "Pothole",
      "predicted_severity": "High",
      "confidence_score": 94.50,
      "generated_summary": "Large pothole detected in center lane causing traffic slowdown.",
      "petition_draft": "To,\nThe Municipal Commissioner,\nKota Municipal Corporation...\nSubject: Complaint regarding deep pothole..."
    }
  }
  ```

---

## 4. Hazards API Payloads

### Detailed Hazard Model Object (Standard DTO)
All GET/POST responses for a single hazard return the following schema:
```json
{
  "id": 501,
  "title": "Open Drain",
  "description": "Large open drain on Mahaveer Road corner posing safety risk to commuters.",
  "issue_type": {
    "id": 3,
    "name": "Open Drain"
  },
  "severity": {
    "id": 4,
    "name": "High"
  },
  "status": "Verified",
  "location": {
    "latitude": 25.21000000,
    "longitude": 75.86000000,
    "address": "Mahaveer Nagar, Kota"
  },
  "images": [
    {
      "id": 12,
      "image_path": "https://storage.googleapis.com/nagarrakshak/hazards/12739.jpg"
    }
  ],
  "reported_by": {
    "id": 101,
    "name": "John Doe",
    "avatar_url": "https://storage.googleapis.com/nagarrakshak/avatars/101.png"
  },
  "verification_count": 14,
  "created_at": "2026-06-25T11:00:00Z",
  "updated_at": "2026-06-25T11:30:00Z"
}
```

### POST /hazards
* **Auth Required**: Yes
* **Request JSON**:
  ```json
  {
    "title": "Open Drain",
    "description": "Large open drain on Mahaveer Road corner",
    "category_id": 3,
    "latitude": 25.21000000,
    "longitude": 75.86000000,
    "address": "Mahaveer Nagar, Kota",
    "ai_analysis_id": 402
  }
  ```
* **Success Response (201 Created)**: Returns the Standard DTO schema.

### GET /hazards
* **Auth Required**: Yes
* **Query Params**:
  * `category_id`: Filter by category (integer)
  * `status`: Filter by status string (e.g. 'Pending', 'Verified')
  * `severity`: Filter by severity string (e.g. 'High')
  * `page`: Pagination page index (default: 1)
  * `limit`: Page size limit (default: 20)
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": [
      { "id": 501, "title": "Open Drain", ... }
    ],
    "meta": {
      "current_page": 1,
      "last_page": 5,
      "per_page": 20,
      "total": 92
    }
  }
  ```

---

## 5. Comments, Bookmarks & Likes Payloads

### POST /hazards/{id}/comments
* **Auth Required**: Yes
* **Request JSON**:
  ```json
  {
    "content": "Work has started on this road segment. Caution remains."
  }
  ```
* **Success Response (201 Created)**:
  ```json
  {
    "success": true,
    "data": {
      "id": 1502,
      "hazard_id": 501,
      "user": {
        "id": 101,
        "name": "John Doe"
      },
      "content": "Work has started on this road segment. Caution remains.",
      "created_at": "2026-06-25T11:45:00Z"
    }
  }
  ```

### POST /hazards/{id}/like (Upvote Verification)
* **Auth Required**: Yes
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Hazard upvoted / verified successfully",
    "verification_count": 15
  }
  ```

### POST /hazards/{id}/bookmark
* **Auth Required**: Yes
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Hazard added to bookmarks"
  }
  ```

---

## 6. Maps & Navigation Payloads

### POST /maps/safe-route
* **Auth Required**: Yes
* **Request JSON**:
  ```json
  {
    "origin": {
      "latitude": 25.18200000,
      "longitude": 75.82800000
    },
    "destination": {
      "latitude": 25.16600000,
      "longitude": 75.85800000
    }
  }
  ```
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "routes": [
        {
          "route_id": 1,
          "name": "Safest Route",
          "polyline": "a~_rF`psbO...",
          "distance_meters": 12400,
          "duration_seconds": 1680,
          "risk_score": 0,
          "hazards_count": 0
        },
        {
          "route_id": 2,
          "name": "Alternative Route (Fastest)",
          "polyline": "u|`rFhnsbO...",
          "distance_meters": 14200,
          "duration_seconds": 1920,
          "risk_score": 8,
          "hazards_count": 3
        }
      ]
    }
  }
  ```

### POST /ride/start
* **Auth Required**: Yes
* **Request JSON**:
  ```json
  {
    "route_id": 1,
    "polyline": "a~_rF`psbO...",
    "origin_lat": 25.18200000,
    "origin_lng": 75.82800000,
    "dest_lat": 25.16600000,
    "dest_lng": 75.85800000
  }
  ```
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "session_id": 703,
    "message": "Navigation session started successfully"
  }
  ```
