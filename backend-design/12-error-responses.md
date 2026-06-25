# 12 - Standard API Error Responses

This document outlines the standardized error JSON structures returned by the **NagarRakshak** backend APIs. All error responses share a consistent envelope to simplify client-side deserialization.

---

## 1. Global Error Schema
```json
{
  "success": false,
  "message": "Human readable summary of the error.",
  "errors": {}
}
```
* `success`: Always `false`.
* `message`: A user-friendly error message.
* `errors`: Detailed field validation lists or context parameters (optional).

---

## 2. HTTP Error Payloads

### 400 Bad Request
Triggered by invalid request parameters or operations that break logic (e.g. attempting to verify a resolved case).
```json
{
  "success": false,
  "message": "This hazard has already been marked as resolved and cannot receive verification votes."
}
```

### 401 Unauthorized
Access token missing, invalid, or expired.
```json
{
  "success": false,
  "message": "Unauthenticated or access token has expired."
}
```

### 403 Forbidden
The authenticated user does not have permission to execute this operation (e.g., a citizen attempting to delete a hazard).
```json
{
  "success": false,
  "message": "This action is unauthorized for your role class."
}
```

### 404 Not Found
Resource does not exist.
```json
{
  "success": false,
  "message": "The requested hazard report does not exist."
}
```

### 409 Conflict
Operation conflicts with the current database state (e.g., trying to re-register an email).
```json
{
  "success": false,
  "message": "A resource conflict occurred. This Google account is already linked."
}
```

### 422 Unprocessable Entity (Validation Errors)
The payload structure was correct, but input validation rules failed.
```json
{
  "success": false,
  "message": "The given data was invalid.",
  "errors": {
    "latitude": ["The latitude field must be a valid coordinate between -90 and 90."],
    "category_id": ["The selected category id is invalid."]
  }
}
```

### 500 Internal Server Error
Unhandled server-side runtime crashes.
```json
{
  "success": false,
  "message": "An unexpected error occurred on our servers. Please try again later."
}
```
