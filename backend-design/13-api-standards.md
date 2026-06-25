# 13 - API Standards & Conventions

This document outlines the API design standards for **NagarRakshak**. It ensures consistency across developers, controllers, and clients.

---

## 1. REST Resource Naming & Versioning
* **Plural Nouns**: Resource paths must use plural nouns (e.g., `/api/v1/hazards` instead of `/api/v1/hazard`).
* **HTTP Verbs**: Standard REST actions must map to their respective HTTP verbs:
  * `GET`: Fetch resource(s).
  * `POST`: Create a new resource.
  * `PUT`/`PATCH`: Update an existing resource.
  * `DELETE`: Remove a resource.
* **URL-based Versioning**: All paths are prefixed with the active API version:
  `/api/v1/...`

---

## 2. JSON Key Conventions
* **snake_case**: All JSON request and response keys must be formatted in `snake_case` (e.g. `verification_count`, `avatar_url`, `created_at`).
* **ISO 8601 Timestamps**: Dates and times must be returned as ISO 8601 strings in UTC format: `YYYY-MM-DDTHH:mm:ssZ`.

---

## 3. Response Wrapping Envelope
Every response returned by the server must be wrapped in a consistent top-level JSON envelope:

### Success Envelopes
* **For single objects or actions**:
  ```json
  {
    "success": true,
    "message": "Resource action succeeded", // Optional
    "data": { ... }
  }
  ```
* **For collection arrays**:
  ```json
  {
    "success": true,
    "data": [
      { "id": 1, ... },
      { "id": 2, ... }
    ]
  }
  ```

### Error Envelopes
See `12-error-responses.md` for details.

---

## 4. Pagination & Sorting
* **Standard Params**: Collection endpoints supporting lists must support the `page` (integer) and `limit` (integer) query parameters.
* **Response Pagination Wrapper**: Pagination metadata is nested under the `meta` key alongside the `data` array:
  ```json
  {
    "success": true,
    "data": [...],
    "meta": {
      "current_page": 1,
      "last_page": 5,
      "per_page": 20,
      "total": 92
    }
  }
  ```
* **Sorting Parameters**: Use the `sort_by` (field name) and `sort_order` (`asc` / `desc`) query parameters to sort collection lists (e.g., `GET /api/v1/hazards?sort_by=created_at&sort_order=desc`).
