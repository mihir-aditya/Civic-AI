# 03 - API Endpoints Specification

This document details the REST API endpoints exposed by the **NagarRakshak** backend. All endpoints are prefixed with `/api/v1`.

---

## 1. Authentication Service
* **`POST /auth/login`**: Authenticates user via email/password or Google ID token, returning JWT tokens.
* **`POST /auth/register`**: Registers a new citizen or volunteer account.
* **`POST /auth/logout`**: Revokes the active access token.
* **`POST /auth/refresh`**: Exchanges a valid Refresh Token for a new Access Token.
* **`GET /auth/profile`**: Returns current authenticated user metadata.

---

## 2. AI Processing Service
* **`POST /ai/analyze`**: Receives an uploaded photo, queries the Gemini API, and returns issue classification, confidence, severity, summary, and petition letter drafts.
* **`POST /ai/regenerate-description`**: Regenerates description and petition letters with additional user input context.

---

## 3. Hazard Reports Core API
* **`POST /hazards`**: Submits a confirmed hazard report (typically with an `ai_analysis_id` link and photos).
* **`GET /hazards`**: Lists all hazards with options to filter by category, status, severity, and bounding coordinates.
* **`GET /hazards/{id}`**: Returns detailed info for a single hazard report.
* **`PUT /hazards/{id}`**: Updates description, coordinates, or category. (Restricted to report owner or Admin).
* **`DELETE /hazards/{id}`**: Removes a report. (Restricted to Admin).

---

## 4. Community Comments API
* **`GET /hazards/{id}/comments`**: Lists discussions, timeline events, and evidence updates for a hazard.
* **`POST /hazards/{id}/comments`**: Adds a new comment text thread.
* **`DELETE /comments/{id}`**: Deletes a specific comment (Owner/Admin only).

---

## 5. Bookmark Monitoring API
* **`POST /hazards/{id}/bookmark`**: Flags a hazard to receive timeline updates.
* **`DELETE /hazards/{id}/bookmark`**: Removes a hazard from the user's bookmarks list.

---

## 6. Reputation & Like Upvoting API
* **`POST /hazards/{id}/like`**: Upvotes the hazard. Automatically increments verification scores.
* **`DELETE /hazards/{id}/like`**: Removes the user's upvote.

---

## 7. Maps & Geographic Queries API
* **`GET /maps/hazards`**: Fetches simplified hazard markers inside map viewport coordinate bounds.
* **`GET /maps/nearby`**: Fetches active hazard markers in a given radius from the user's current GPS position.
* **`POST /maps/safe-route`**: Queries Google Directions/Routes API, computes hazard Risk Scores for alternative polyline routes, and returns prioritized safe routes.
* **`GET /maps/live-alerts`**: Queries active/approaching hazards within a safety buffer zone (e.g. 500m radius of active navigation path).

---

## 8. Safe Ride & Navigation Session API
* **`POST /ride/start`**: Initializes an active safe ride navigation session.
* **`POST /ride/end`**: Concludes navigation session, logging total distance, time, and safety incidents.
* **`POST /ride/share-location`**: Publishes active ride telemetry coordinates for family sharing or tracking.

---

## 9. Notification Center API
* **`GET /notifications`**: Fetches user-specific or broadcast notifications.
* **`PUT /notifications/{id}/read`**: Marks a notification message as read.
* **`DELETE /notifications/{id}`**: Clears notification.

---

## 10. Profile & Contribution Dashboard API
* **`GET /profile/stats`**: Compiles total reports, verification count, reputation points, badges, and user impact metrics.
* **`PUT /profile`**: Updates name, avatar icon, phone, and target preferences.

---

## 11. System Configuration & Settings API
* **`GET /settings`**: Fetches default system preferences (languages, voice configurations).
* **`PUT /settings`**: Synchronizes personal app preferences back to user settings schema.
