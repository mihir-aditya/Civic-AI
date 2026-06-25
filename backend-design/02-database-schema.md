# 02 - Database Schema Design

This document details the relational database schema for **NagarRakshak**. It targets a MySQL 8.0+ or PostgreSQL 15+ engine. All spatial coordinates are stored with high precision.

---

## 1. Schema Tables

### users
* **Purpose**: Stores citizen, volunteer, department officer, and admin accounts.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `name` | VARCHAR(255) | No | None | |
  | `email` | VARCHAR(255) | No | None | Unique |
  | `email_verified_at`| TIMESTAMP | Yes | NULL | |
  | `password` | VARCHAR(255) | Yes | NULL | Nullable for OAuth |
  | `google_id` | VARCHAR(255) | Yes | NULL | OAuth Identity |
  | `avatar_url` | VARCHAR(512) | Yes | NULL | |
  | `phone_number` | VARCHAR(20) | Yes | NULL | |
  | `role` | ENUM('citizen', 'volunteer', 'department', 'admin') | No | 'citizen' | |
  | `reputation_points`| INT | No | 0 | Contributions score |
  | `remember_token` | VARCHAR(100) | Yes | NULL | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
  | `updated_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * UNIQUE `users_email_unique` (`email`)
  * INDEX `users_role_index` (`role`)
* **Relationships**:
  * Has many `hazards` via `hazards.reported_by`
  * Has many `ride_sessions` via `ride_sessions.user_id`

### hazards
* **Purpose**: Core entity for public safety hazard reports.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `title` | VARCHAR(255) | No | None | |
  | `description` | TEXT | Yes | NULL | |
  | `latitude` | DECIMAL(10, 8) | No | None | Spatial Latitude |
  | `longitude` | DECIMAL(11, 8) | No | None | Spatial Longitude |
  | `address` | VARCHAR(512) | Yes | NULL | Reverse-geocoded |
  | `category_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `status_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `reported_by` | BIGINT UNSIGNED | Yes | NULL | Foreign Key (users.id) |
  | `department_id` | BIGINT UNSIGNED | Yes | NULL | Foreign Key (departments.id) |
  | `verification_count`| INT | No | 0 | Verification votes |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
  | `updated_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * INDEX `hazards_coordinates_idx` (`latitude`, `longitude`)
  * INDEX `hazards_status_idx` (`status_id`)
* **Relationships / Foreign Keys**:
  * Foreign Key `hazards_category_id_foreign` (`category_id`) references `hazard_categories` (`id`) ON DELETE RESTRICT
  * Foreign Key `hazards_status_id_foreign` (`status_id`) references `hazard_status` (`id`) ON DELETE RESTRICT
  * Foreign Key `hazards_reported_by_foreign` (`reported_by`) references `users` (`id`) ON DELETE SET NULL
  * Foreign Key `hazards_department_id_foreign` (`department_id`) references `departments` (`id`) ON DELETE SET NULL

### hazard_images
* **Purpose**: Maps photo evidence attachments to reported hazards.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `hazard_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `image_path` | VARCHAR(512) | No | None | Absolute GCS/Local URL|
  | `uploaded_by` | BIGINT UNSIGNED | Yes | NULL | Foreign Key (users.id) |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `hazard_images_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE CASCADE
  * Foreign Key `hazard_images_uploaded_by_foreign` (`uploaded_by`) references `users` (`id`) ON DELETE SET NULL

### hazard_categories
* **Purpose**: Defines type categories (Pothole, Open Drain, Garbage, etc.).
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `name` | VARCHAR(100) | No | None | Unique |
  | `icon_slug` | VARCHAR(100) | No | 'default' | Code mapping slug |
  | `default_severity` | ENUM('low', 'medium', 'high', 'critical') | No | 'medium' | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * UNIQUE `categories_name_unique` (`name`)

### hazard_status
* **Purpose**: Defines processing status codes (Pending, Verified, Escalated, Resolved).
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `name` | VARCHAR(50) | No | None | Unique |
  | `description` | VARCHAR(255) | Yes | NULL | |
* **Indexes**:
  * UNIQUE `status_name_unique` (`name`)

### hazard_comments
* **Purpose**: Renders community discussions and updates on specific hazards.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `hazard_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `user_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `content` | TEXT | No | None | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
  | `updated_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `comments_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE CASCADE
  * Foreign Key `comments_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### hazard_likes
* **Purpose**: Implements hazard upvotes, acting as soft verifications.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `hazard_id` | BIGINT UNSIGNED | No | None | Compound Primary Key |
  | `user_id` | BIGINT UNSIGNED | No | None | Compound Primary Key |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `likes_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE CASCADE
  * Foreign Key `likes_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### hazard_bookmarks
* **Purpose**: Allows users to save hazards to watch status timelines.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `hazard_id` | BIGINT UNSIGNED | No | None | Compound Primary Key |
  | `user_id` | BIGINT UNSIGNED | No | None | Compound Primary Key |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `bookmarks_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE CASCADE
  * Foreign Key `bookmarks_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### notifications
* **Purpose**: Stores app alerts, emergency notices, and dispatch logs.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `user_id` | BIGINT UNSIGNED | Yes | NULL | Target user (null for broadcast) |
  | `title` | VARCHAR(255) | No | None | |
  | `message` | TEXT | No | None | |
  | `type` | VARCHAR(50) | No | 'general' | hazard, emergency, system |
  | `data` | JSON | Yes | NULL | Arbitrary context payload |
  | `read_at` | TIMESTAMP | Yes | NULL | Read status |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * INDEX `notifications_user_unread_idx` (`user_id`, `read_at`)
* **Relationships / Foreign Keys**:
  * Foreign Key `notifications_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### ai_analysis
* **Purpose**: Records Gemini classification results, drafts, and prompt metadata.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `hazard_id` | BIGINT UNSIGNED | Yes | NULL | Link to confirmed hazard |
  | `image_path` | VARCHAR(512) | No | None | Path analyzed |
  | `predicted_category`| VARCHAR(100) | No | None | |
  | `predicted_severity`| ENUM('low', 'medium', 'high', 'critical') | No | 'medium' | |
  | `confidence_score` | DECIMAL(5, 2) | No | 0.00 | Confidence % (e.g. 94.50) |
  | `generated_summary` | TEXT | No | None | Brief summary description |
  | `petition_draft` | TEXT | No | None | Generated letter draft |
  | `tokens_used` | INT | No | 0 | API cost tracking |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `ai_analysis_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE SET NULL

### ride_sessions
* **Purpose**: Tracks active safe navigation rides and sessions.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `user_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `origin_lat` | DECIMAL(10, 8) | No | None | |
  | `origin_lng` | DECIMAL(11, 8) | No | None | |
  | `origin_address` | VARCHAR(512) | Yes | NULL | |
  | `dest_lat` | DECIMAL(10, 8) | No | None | |
  | `dest_lng` | DECIMAL(11, 8) | No | None | |
  | `dest_address` | VARCHAR(512) | Yes | NULL | |
  | `polyline` | TEXT | No | None | Chosen route polyline string |
  | `initial_risk_score`| INT | No | 0 | |
  | `status` | ENUM('active', 'completed', 'cancelled') | No | 'active' | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
  | `ended_at` | TIMESTAMP | Yes | NULL | |
* **Relationships / Foreign Keys**:
  * Foreign Key `ride_sessions_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### route_history
* **Purpose**: Logs real-time navigation telemetry and coordinate updates.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `session_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `latitude` | DECIMAL(10, 8) | No | None | |
  | `longitude` | DECIMAL(11, 8) | No | None | |
  | `speed_kmh` | DECIMAL(5, 2) | No | 0.00 | Speed check |
  | `timestamp` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `route_history_session_id_foreign` (`session_id`) references `ride_sessions` (`id`) ON DELETE CASCADE

### saved_places
* **Purpose**: User saved quick destinations (Home, Work, etc.).
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `user_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `label` | VARCHAR(100) | No | None | 'Home', 'Work' |
  | `address` | VARCHAR(512) | No | None | |
  | `latitude` | DECIMAL(10, 8) | No | None | |
  | `longitude` | DECIMAL(11, 8) | No | None | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `saved_places_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### departments
* **Purpose**: Municipality service divisions (e.g. Nagar Nigam, Traffic Police).
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `name` | VARCHAR(150) | No | None | Unique |
  | `email` | VARCHAR(255) | No | None | Escalation mailbox |
  | `phone` | VARCHAR(20) | Yes | NULL | |
  | `contact_person` | VARCHAR(255) | Yes | NULL | |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * UNIQUE `departments_name_unique` (`name`)

### department_updates
* **Purpose**: Official progress timeline logs posted by municipal departments.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `hazard_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `department_id` | BIGINT UNSIGNED | No | None | Foreign Key |
  | `update_title` | VARCHAR(255) | No | None | |
  | `update_content` | TEXT | No | None | |
  | `status_to_id` | BIGINT UNSIGNED | No | None | Status state transitioned to |
  | `created_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `dep_updates_hazard_id_foreign` (`hazard_id`) references `hazards` (`id`) ON DELETE CASCADE
  * Foreign Key `dep_updates_dept_id_foreign` (`department_id`) references `departments` (`id`) ON DELETE CASCADE

### device_tokens
* **Purpose**: Registers unique device FCM tokens mapping push targets.
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | BIGINT UNSIGNED | No | Auto-Increment | Primary Key |
  | `user_id` | BIGINT UNSIGNED | Yes | NULL | Target user (null for guest devices)|
  | `device_token` | VARCHAR(512) | No | None | FCM registration token |
  | `platform` | ENUM('android', 'ios', 'web')| No | 'android' | |
  | `last_active_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Indexes**:
  * UNIQUE `device_tokens_token_unique` (`device_token`)
  * INDEX `device_tokens_user_idx` (`user_id`)
* **Relationships / Foreign Keys**:
  * Foreign Key `device_tokens_user_id_foreign` (`user_id`) references `users` (`id`) ON DELETE CASCADE

### settings
* **Purpose**: Key-value system parameters (Gemini prompts, thresholds).
* **Columns**:
  | Column Name | Data Type | Nullable | Default | Notes / Key |
  | :--- | :--- | :--- | :--- | :--- |
  | `key` | VARCHAR(100) | No | None | Primary Key |
  | `value` | TEXT | Yes | NULL | |
  | `description` | VARCHAR(255) | Yes | NULL | |
  | `updated_by` | BIGINT UNSIGNED | Yes | NULL | Admin ID (users.id) |
  | `updated_at` | TIMESTAMP | No | CURRENT_TIMESTAMP | |
* **Relationships / Foreign Keys**:
  * Foreign Key `settings_updated_by_foreign` (`updated_by`) references `users` (`id`) ON DELETE SET NULL
