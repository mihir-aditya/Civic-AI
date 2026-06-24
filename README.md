# NagarRakshak (Community Safety & Hazard Alert Platform)

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android)](android)
[![Laravel](https://img.shields.io/badge/Platform-Laravel-red.svg?logo=laravel)](backend)

**NagarRakshak** (Report. Alert. Prevent.) is a modern, premium civic-technology platform designed to help citizens report local public hazards, receive real-time proximity alerts, verify community reports, and collaborate with municipal authorities to make cities safer.

---

## 🚀 Key Features

### 📱 Android Application
* **Real-time Safety Map**: A free, keyless map powered by Leaflet.js and OpenStreetMap that plots active hazard zones and color-coded risk markers.
* **AI-Powered Hazard Classifier**: Built-in **Gemini AI simulation** that auto-classifies hazards, suggests categories, and estimates risk levels on photo uploads.
* **Dynamic Proximity Alerts**: Tracks and broadcasts nearby warning signals (e.g. *"⚠ Open Drain 150m Ahead"*).
* **Citizen Reputations**: Reward points, impact index, and badge status (*"Civic Champion"*, *"Safety Reporter"*) for active contributors.

### 💻 Laravel Administrative Portal
* **Enterprise Management**: Built on Laravel 11, Blade layouts, and Bootstrap 5.
* **Dynamic Analytics Widgets**: Risk donuts, monthly counts, resolution averages, and category pies using Chart.js.
* **Interactive Leaflet Map**: Real-time maps showing hazard clusters, severity status filters, and interactive popups with navigation bridges.
* **Administrative Directories**: Unified hubs to suspend/promote accounts, merge duplicate reports, manage AI configurations, and resolve conflicting audits.

---

## 🛠️ Tech Stack & Directory Structure

```text
d:\civicAI\
├── android/            # Jetpack Compose Kotlin Mobile Application
│   ├── app/src/        # Compose UI Themes, Navigation Graphs, and Screens
│   └── gradle/         # Gradle version catalogs and wrapper scripts
├── backend/            # Laravel 11 Administrative Dashboard Portal
│   ├── app/Models/     # Hazard, Verification, and User models
│   ├── database/       # DB Migrations and Seeders
│   └── resources/      # Blade HTML views and layouts (Bootstrap 5, Leaflet, Chart.js)
├── .github/            # GitHub bug/feature issue and PR templates
└── README.md           # Project introduction and setup guide
```

---

## 🚀 Fast-Track Local Installation

### 💻 Running the Backend Dashboard
1. Ensure you have **PHP 8.2+**, **Composer**, and **Node.js** installed.
2. Navigate to the backend directory:
   ```bash
   cd backend
   ```
3. Copy environment configurations and install composer packages:
   ```bash
   composer install
   ```
4. Build Webpack assets:
   ```bash
   npm install && npm run build
   ```
5. Initialize the database schema and load seed data:
   ```bash
   php artisan migrate:fresh --seed
   ```
6. Start the local server:
   ```bash
   php artisan serve
   ```
7. Open `http://localhost:8000` in your web browser. Access the admin dashboard using:
   - **User**: `admin@nagarrakshak.org`
   - **Password**: `password`

### 📱 Running the Android App
1. Open **Android Studio** (Koala or newer recommended).
2. Select **Open an Existing Project** and choose the `android/` directory.
3. Android Studio will automatically sync the Gradle properties and download dependencies.
4. Run the application on an emulator or an Android device (API 26+).
5. The map runs keyless out of the box using Leaflet.js WebViews!

---

## 🤝 Contributing

We welcome contributions! Please review our [Contributing Guidelines](CONTRIBUTING.md) and our [Code of Conduct](CODE_OF_CONDUCT.md).

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for details.
