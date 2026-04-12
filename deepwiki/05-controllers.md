# Controllers

## WebController

**File:** `src/main/java/com/example/portal/controller/WebController.java`

Serves Thymeleaf HTML pages and handles form submissions. All authentication state is managed through `HttpSession`.

### Route Map

| Method | Path | Auth Required | Description |
|--------|------|---------------|-------------|
| GET | `/` | No | Renders `index.html` (landing page) |
| GET | `/login` | No | Renders `login.html`. Redirects to `/dashboard` if already logged in. |
| POST | `/login` | No | Processes login form. Calls `ProvisionerClient.login()`. Stores JWT in session on success. |
| GET | `/register` | No | Renders `register.html`. Redirects to `/dashboard` if already logged in. |
| POST | `/register` | No | Processes registration form. Calls `ProvisionerClient.register()`. Redirects to `/login?registered` on success. |
| GET | `/dashboard` | Yes | Renders `dashboard.html` with user data from session. |
| GET | `/admin` | Yes (Admin) | Renders `admin.html` with user data from session. |
| GET | `/logout` | No | Invalidates session, redirects to `/login?logout`. |
| GET | `/oauth2/callback` | No | Receives JWT from Provisioner OAuth2 flow, validates and stores in session. |

### Login Flow Detail

```java
@PostMapping("/login")
public String handleLogin(email, password, session, model) {
    // 1. Call Provisioner
    AuthResponse response = provisionerClient.login(new LoginRequest(email, password));

    // 2. Success → store in session, redirect
    if (response.isSuccess()) {
        session.setAttribute("authToken", response.getToken());
        session.setAttribute("userRole", response.getRole());
        // ... other attributes
        return "redirect:/dashboard";
    }

    // 3. Failure → re-render login with error
    model.addAttribute("error", response.getMessage());
    return "login";
}
```

### Error Handling

All methods catch `ProvisionerUnavailableException` and display "Service unavailable" to the user via Thymeleaf model attributes.

---

## ApiController

**File:** `src/main/java/com/example/portal/controller/ApiController.java`

`@RestController` that proxies AJAX calls from the admin panel JavaScript to the Provisioner API. Extracts the JWT token from the HTTP session and forwards it as a Bearer token.

### Route Map

| Method | Path | Proxies To | Description |
|--------|------|------------|-------------|
| GET | `/proxy/admin/users` | `GET /admin/users` | List all users |
| GET | `/proxy/admin/stats` | `GET /admin/stats` | System statistics |
| POST | `/proxy/admin/users` | `POST /admin/users` | Create user |
| PUT | `/proxy/admin/users/{id}` | `PUT /admin/users/{id}` | Update user |
| DELETE | `/proxy/admin/users/{id}` | `DELETE /admin/users/{id}` | Delete user |

### Proxy Pattern

```
Browser JS                  ApiController              ProvisionerClient           Provisioner
    │                            │                            │                         │
    │ fetch('/api/proxy/         │                            │                         │
    │   admin/users')            │                            │                         │
    │  (no Auth header)          │                            │                         │
    │───────────────────────────>│                            │                         │
    │                            │ getToken(session)          │                         │
    │                            │ → "eyJhbG..."              │                         │
    │                            │                            │                         │
    │                            │ getAllUsers(token)          │                         │
    │                            │───────────────────────────>│                         │
    │                            │                            │ GET /admin/users        │
    │                            │                            │ Authorization: Bearer…  │
    │                            │                            │────────────────────────>│
    │                            │                            │                         │
    │                            │                            │  {success, users, count}│
    │                            │                            │<────────────────────────│
    │                            │   UsersResponse            │                         │
    │                            │<───────────────────────────│                         │
    │  200 OK (JSON)             │                            │                         │
    │<───────────────────────────│                            │                         │
```

### Error Responses

| Condition | HTTP Status | Response Body |
|-----------|-------------|---------------|
| No session/token | 401 | `{"success": false, "message": "Not authenticated"}` |
| Provisioner unreachable | 503 | `{"success": false, "message": "Provisioner service unavailable"}` |
