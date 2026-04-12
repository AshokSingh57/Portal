# Thymeleaf Templates

**Directory:** `src/main/resources/templates/`

All templates use Bootstrap 5.3.0 and Font Awesome 6.0.0 loaded from CDN.

## Template Map

| Template | Route | Auth | Description |
|----------|-------|------|-------------|
| `index.html` | `GET /` | No | Landing page with feature cards and CTA buttons |
| `login.html` | `GET /login` | No | Login form (email/password) + Google OAuth button |
| `register.html` | `GET /register` | No | Registration form with client-side password validation |
| `dashboard.html` | `GET /dashboard` | Yes | User dashboard with feature links and user info |
| `admin.html` | `GET /admin` | Admin | User management table, create/edit modals, statistics |
| `layout.html` | — | — | Base layout template (not currently used by other templates) |

---

## index.html (Landing Page)

- Static content, no Thymeleaf expressions
- Hero section with "Get Started" and "Sign In" buttons
- 6 feature cards: RSA, JWT, OAuth2, AES, Role Management, REST API
- Links use hardcoded `/api/login` and `/api/register` paths

---

## login.html

### Server-Side Rendering
- Error messages: `th:if="${error}"` with `th:text="${error}"`
- Success messages: `th:if="${success}"` with `th:text="${success}"` (logout, registration)
- Form action: `th:action="@{/login}"` method POST
- Links: `th:href="@{/register}"`, `th:href="@{/}"`

### Form Submission
Standard HTML form POST — no JavaScript required for login. The server handles:
- Success → 302 redirect to `/dashboard`
- Failure → re-render `login.html` with error message in model

### Google OAuth
Link points to Provisioner: `http://localhost:8081/api/oauth2/authorization/google`

---

## register.html

### Server-Side Rendering
- Error messages: `th:if="${error}"` with `th:text="${error}"`
- Form action: `th:action="@{/register}"` method POST

### Client-Side Validation (JavaScript)
Retained from original application — validates in real-time before enabling submit:
- Password minimum 8 characters
- At least 1 number
- At least 1 special character
- Password confirmation matches

The submit button is disabled until all validations pass.

---

## dashboard.html

### Thymeleaf Expressions

| Expression | Source | Purpose |
|------------|--------|---------|
| `th:text="${fullName}"` | Session → model | Display name in nav and header |
| `th:text="${email}"` | Session → model | Display email in user info |
| `th:text="${role}"` | Session → model | Display role badge |
| `th:if="${role == 'Admin'}"` | Session → model | Show/hide admin nav link and card |
| `th:href="@{/dashboard}"` | — | Navigation links |
| `th:href="@{/admin}"` | — | Admin panel link |
| `th:href="@{/logout}"` | — | Logout link |

### Feature Cards
- Crypto, JWT, API Documentation links → Provisioner Swagger UI at `http://localhost:8081/api/swagger-ui/index.html`
- Admin Panel → `/admin` (visible only to Admin role)
- Security Features, OAuth2 → JavaScript `alert()` info dialogs

### JavaScript
Minimal — only sets login time display and info dialogs. No authentication logic.

---

## admin.html

### Thymeleaf Expressions

| Expression | Source | Purpose |
|------------|--------|---------|
| `th:text="${fullName}"` | Session → model | Nav bar username |
| `th:href="@{/dashboard}"` | — | Dashboard link |
| `th:href="@{/admin}"` | — | Active admin link |
| `th:href="@{/logout}"` | — | Logout link |

### JavaScript AJAX Calls

All fetch calls target the Portal proxy (no direct Provisioner calls, no Auth headers):

| Action | Fetch Call |
|--------|-----------|
| Load users | `GET /api/proxy/admin/users` |
| Create user | `POST /api/proxy/admin/users` with JSON body |
| Update user | `PUT /api/proxy/admin/users/${userId}` with JSON body |
| Delete user | `DELETE /api/proxy/admin/users/${userId}` |

### Features
- **Statistics row**: Total users, admin users, regular users, active today (computed client-side from user list)
- **User table**: Scrollable with sticky header, shows name, email, role badge, auth type, last login, edit/delete buttons
- **Create User modal**: Full name, email, password, role selector
- **Edit User modal**: Full name (editable), email (readonly), role, password (disabled for Google users)
- **Delete confirmation**: Browser `confirm()` dialog
- **Auto-refresh**: User list reloads after every create/update/delete
