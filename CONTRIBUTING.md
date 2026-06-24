# Contributing to NagarRakshak

Thank you for choosing to contribute to **NagarRakshak**! We appreciate your support in making cities safer.

Please take a moment to review these guidelines before submitting code.

---

## 🛠️ Development Requirements

### Android App
- **JDK**: Java 17
- **IDE**: Android Studio Koala+
- **Min SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)

### Laravel Backend
- **PHP**: v8.2+
- **Composer**: v2.0+
- **Node.js**: v18+ (with npm)
- **Database**: SQLite (default for local setups)

---

## 🚦 Workflow Process

### 1. Finding or Reporting Issues
- Search the issue registry before creating a new ticket.
- Use the **Bug Report** or **Feature Request** templates.
- Be descriptive and provide reproduction steps.

### 2. Branching Strategy
- Branch out from the `main` branch.
- Use a descriptive branch naming scheme:
  - `feature/your-feature-name`
  - `bugfix/issue-id-short-description`
  - `docs/update-documentation`

### 3. Commit Guidelines
- Write clear, concise commit messages in imperative mood:
  - `feat: Integrate Leaflet Webview inside MapScreen`
  - `fix: Resolve null pointer error in DetailScreen`
  - `docs: Update installation guide in README`

### 4. Pull Requests
- Keep your changes focused. Avoid bundling unrelated fixes together.
- Update tests if modifying core backend logic.
- Ensure the PR passes all linters and tests.
- Reference the issue number (e.g. `Closes #12`).

---

## 🎨 Code Quality Standards

### Android Kotlin
- Adhere to the official [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html).
- Use Jetpack Compose Material 3 components.
- Keep composables focused and state hoisted.

### Laravel PHP
- Follow [PSR-12](https://www.php-fig.org/psr/psr-12/) coding standards.
- Use Eloquent query builders instead of raw SQL where possible.
- Write feature tests inside the `tests/Feature/` directory.
