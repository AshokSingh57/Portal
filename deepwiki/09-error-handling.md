# Error Handling

## Exception Classes

### ProvisionerUnavailableException

**File:** `src/main/java/com/example/portal/exception/ProvisionerUnavailableException.java`

Runtime exception thrown by `ProvisionerClient` when it cannot reach the Provisioner service. Wraps the underlying `RestClientException`.

## Error Handling by Layer

### ProvisionerClient Layer
Every API method catches `RestClientException` and wraps it:

```java
catch (RestClientException e) {
    throw new ProvisionerUnavailableException(
        "Failed to connect to provisioner service", e);
}
```

This covers: connection refused, DNS resolution failure, read timeout, HTTP 4xx/5xx errors.

### WebController Layer (Page Routes)
Catches `ProvisionerUnavailableException` in login and register handlers:

| Scenario | Behavior |
|----------|----------|
| Login fails (Provisioner down) | Re-renders `login.html` with error "Service unavailable. Please try again later." |
| Register fails (Provisioner down) | Re-renders `register.html` with same error |
| OAuth2 callback fails | Redirects to `/login?error=Service unavailable` |
| Login returns `success: false` | Re-renders `login.html` with Provisioner's error message |
| Register returns `success: false` | Re-renders `register.html` with Provisioner's error message |

### ApiController Layer (AJAX Proxy)
Returns structured JSON error responses:

| Scenario | HTTP Status | Response |
|----------|-------------|----------|
| No session token | 401 Unauthorized | `{"success": false, "message": "Not authenticated"}` |
| Provisioner unreachable | 503 Service Unavailable | `{"success": false, "message": "Provisioner service unavailable"}` |

### SecurityConfig Interceptor
Not exception-based — simply redirects:

| Scenario | Redirect Target |
|----------|----------------|
| No session on protected route | `/login` |
| Non-admin accessing `/admin` | `/dashboard` |

## User-Visible Error Scenarios

| What Happens | What User Sees |
|-------------|----------------|
| Provisioner is down, user tries to login | Login page with "Service unavailable. Please try again later." |
| Wrong email/password | Login page with Provisioner's error (e.g., "Invalid credentials") |
| Duplicate email on registration | Register page with "Email already registered" |
| Session expires, user clicks admin link | Redirect to login page |
| Non-admin visits `/admin` directly | Redirect to dashboard |
| Admin panel AJAX fails (Provisioner down) | JavaScript alert: "Failed to load users: ..." |
