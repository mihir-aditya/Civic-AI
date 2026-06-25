# 11 - Settings & Preferences

This document defines user setting schemas and preferences synchronizations. Settings are stored on the Android client locally via `DataStore` and synced back to the backend.

---

## 1. Preferences JSON Schema
The following JSON structure maps the user's localized configuration settings:

```json
{
  "language": "en",
  "theme": "system",
  "notifications": {
    "push_enabled": true,
    "nearby_hazard_alerts": true,
    "emergency_broadcasts": true,
    "status_timeline_updates": true
  },
  "navigation": {
    "voice_guidance_enabled": true,
    "voice_volume": "high",
    "tts_language": "en-IN",
    "safe_route_priority": "safest"
  },
  "emergency_contacts": [
    {
      "name": "Jane Doe",
      "phone_number": "+919876543211",
      "relationship": "Spouse"
    },
    {
      "name": "Emergency Services",
      "phone_number": "112",
      "relationship": "Police"
    }
  ]
}
```

---

## 2. API Endpoints

### GET /settings
* **Auth Required**: Yes
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "language": "en",
      "theme": "system",
      "notifications": {
        "push_enabled": true,
        "nearby_hazard_alerts": true,
        "emergency_broadcasts": true,
        "status_timeline_updates": true
      },
      "navigation": {
        "voice_guidance_enabled": true,
        "voice_volume": "high",
        "tts_language": "en-IN",
        "safe_route_priority": "safest"
      },
      "emergency_contacts": [
        {
          "name": "Jane Doe",
          "phone_number": "+919876543211",
          "relationship": "Spouse"
        }
      ]
    }
  }
  ```

### PUT /settings
Updates user-specific app settings and synchronizes preferences.
* **Auth Required**: Yes
* **Request JSON**: Same structure as the Preferences JSON Schema.
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Preferences synchronized successfully"
  }
  ```
