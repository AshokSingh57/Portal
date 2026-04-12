# Security Model

## Overview

Portal uses **server-side HTTP sessions** for authentication. When a user logs in, the Provisioner returns a JWT token which Portal stores in the `HttpSession` — never in browser `localStorage` or cookies accessible to JavaScript. This eliminates XSS-based token theft.

## Authentication Flow

```
User                    Portal                      Provisioner
 │                        │                              │
 │  POST /login           │                              │
 │  (email, password)     │                              │
 │───────────────────────>│                              │
 │                        │  POST /auth/login            │
 │                        │  {email, password}           │
 │                        │─────────────────────────────>│
 │                        │                              │
 │                        │  {success, token, role, ...} │
 │                        │<─────────────────────────────│
 │                        │                              │
 │                        │  Store in HttpSession:       │
 │                        │    authToken = JWT            │
 │                        │    userRole = "Admin"         │
 │                        │    userFullName = "Joe"       │
 │                        │    userEmail = "joe@..."      │
 │                        │                              │
 │  302 Redirect          │                              │
 │  /dashboard            │                              │
 │<───────────────────────│                              │
 │                        │                              │
 │  GET /dashboard        │                              │
 │  (session cookie)      │                              │
 │───────────────────────>│                              │
 │                        │  Check session → valid       │
 │                        │  Render dashboard.html       │
 │  200 OK (HTML)         │  with user data from session │
 │<───────────────────────│                              │
```

## Route Protection

Implemented via `SecurityConfig.AuthInterceptor` (a Spring `HandlerInterceptor`):

### Public Routes (no authentication required)
| Route | Purpose |
|-------|---------|
| `GET /` | Home/landing page |
| `GET /login` | Login page |
| `POST /login` | Login form submission |
| `GET /register` | Registration page |
| `POST /register` | Registration form submission |
| `GET /oauth2/callback` | OAuth2 return from Provisioner |
| `GET /error` | Error page |

### Protected Routes (require valid session)
| Route | Requirement | Redirect if failed |
|-------|-------------|-------------------|
| `GET /dashboard` | `authToken` in session | → `/login` |
| `GET /admin` | `authToken` in session AND `userRole == "Admin"` | → `/login` or `/dashboard` |
| `* /proxy/**` | `authToken` in session | → `/login` |

### Interceptor Logic

```
Request arrives at /dashboard, /admin, or /proxy/**
    │
    ▼
Session exists with authToken?
    │
    ├─ NO → Redirect to /login
    │
    └─ YES → Is path /admin or /proxy/admin/**?
                │
                ├─ YES → Is userRole == "Admin"?
                │           │
                │           ├─ NO → Redirect to /dashboard
                │           └─ YES → Allow request
                │
                └─ NO → Allow request
```

## Session Attributes

| Attribute | Type | Set By | Used By |
|-----------|------|--------|---------|
| `authToken` | String | `WebController.handleLogin()` | `ApiController` (forwarded as Bearer token) |
| `userId` | String | `WebController.handleLogin()` | Model population |
| `userEmail` | String | `WebController.handleLogin()` | Thymeleaf templates |
| `userFullName` | String | `WebController.handleLogin()` | Thymeleaf templates, nav bar |
| `userRole` | String | `WebController.handleLogin()` | Interceptor (admin check), Thymeleaf conditionals |

## OAuth2 Flow

Google OAuth2 is handled entirely by the Provisioner. Portal's role:

1. Login page links to `http://localhost:8081/api/oauth2/authorization/google`
2. Provisioner completes OAuth2 with Google
3. Provisioner redirects back to Portal: `http://localhost:8080/api/oauth2/callback?token=<jwt>`
4. `WebController.oauth2Callback()` validates the token via Provisioner and stores it in session

## Logout

`GET /logout` invalidates the entire HTTP session and redirects to `/login?logout`.
