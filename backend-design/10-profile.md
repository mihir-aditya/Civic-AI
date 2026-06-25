# 10 - Profile & Contribution Dashboard

This document details the user Profile and Leaderboard API models. Reputation points are calculated based on citizens' reports, verifications, and completed resolutions.

---

## 1. Reputation Points System (Gamification)
* **Report Submitted**: +10 Points
* **Report Verified by community**: +5 Points
* **Report marked Resolved**: +15 Points
* **Fake/False Report Rejected**: -20 Points

---

## 2. API Endpoints

### GET /profile/stats
* **Auth Required**: Yes
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "user_id": 101,
      "name": "John Doe",
      "avatar_url": "https://storage.googleapis.com/nagarrakshak/avatars/101.png",
      "member_since": "2026-06-24T12:00:00Z",
      "reputation_points": 140,
      "stats": {
        "hazards_reported": 12,
        "hazards_verified": 45,
        "hazards_resolved": 3
      },
      "badges": [
        {
          "name": "Verified Citizen",
          "color": "blue",
          "issued_at": "2026-06-24T15:00:00Z"
        },
        {
          "name": "Active Citizen",
          "color": "green",
          "issued_at": "2026-06-25T09:00:00Z"
        }
      ]
    }
  }
  ```

### GET /profile/leaderboard
Returns top-performing citizens sorted by reputation score. Used to motivate community reporting.
* **Auth Required**: Yes
* **Query Params**:
  * `period`: Filter range (`weekly`, `monthly`, `all_time`)
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "current_user_rank": 4,
      "leaderboard": [
        {
          "rank": 1,
          "name": "Aarav Sharma",
          "reputation_points": 450,
          "badge": "Community Guardian"
        },
        {
          "rank": 2,
          "name": "Priya Patel",
          "reputation_points": 380,
          "badge": "Safety Reporter"
        },
        {
          "rank": 3,
          "name": "Amit Singh",
          "reputation_points": 210,
          "badge": "Active Citizen"
        },
        {
          "rank": 4,
          "name": "John Doe",
          "reputation_points": 140,
          "badge": "Verified Citizen"
        }
      ]
    }
  }
  ```
