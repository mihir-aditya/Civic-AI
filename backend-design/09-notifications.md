# 09 - Notification Center & FCM

NagarRakshak uses **Firebase Cloud Messaging (FCM)** to broadcast push notifications. This document outlines token registration, message categorization, and the JSON payload formats required for notifications.

---

## 1. Device Token Registry
* **Endpoint**: `POST /api/v1/notifications/register-token`
* **Request JSON**:
  ```json
  {
    "device_token": "fcm_registration_token_string_here",
    "platform": "android"
  }
  ```
* **Success Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Token registered successfully"
  }
  ```

---

## 2. Notification Types & Categories

We support five notification pipelines:

1. **`nearby_hazard`**: Triggered when a new critical hazard is reported within the user's active/last-known area radius (5km).
2. **`report_verified`**: Notifies the citizen when their report receives community verification votes.
3. **`emergency_broadcast`**: Broadcast alert triggered by admins for critical city-wide safety events (e.g. flash floods, road closures).
4. **`ride_alert`**: Navigation updates (e.g., detour suggested due to new hazard on active path).
5. **`department_update`**: Notifies bookmarked users when a department post progress logs or marks a hazard as resolved.

---

## 3. FCM Payload Structures

All FCM push payloads are sent from the Laravel backend as **Data Messages** (rather than simple Notification Messages) to allow the Android client background service to parse coordinates, determine local relevance, and show custom notifications.

### FCM Data Payload JSON
```json
{
  "to": "fcm_device_token_or_topic",
  "priority": "high",
  "data": {
    "notification_id": "893",
    "type": "nearby_hazard",
    "title": "⚠️ New Critical Hazard Nearby",
    "body": "An open drain has been reported 400m from your current location.",
    "hazard_id": "501",
    "latitude": "25.21000000",
    "longitude": "75.86000000",
    "severity": "Critical",
    "sound": "emergency_alert.mp3"
  }
}
```

### Emergency Broadcast Payload JSON
```json
{
  "to": "/topics/emergency_kota",
  "priority": "high",
  "data": {
    "notification_id": "894",
    "type": "emergency_broadcast",
    "title": "🚨 Emergency: Waterlogging Warning",
    "body": "Talwandi underpass is completely flooded. Seek alternative routes immediately.",
    "latitude": "25.17600000",
    "longitude": "75.83000000",
    "sound": "siren.mp3"
  }
}
```
