# Provisioner Client

**File:** `src/main/java/com/example/portal/client/ProvisionerClient.java`

## Purpose

`ProvisionerClient` is a `@Service` that encapsulates all HTTP communication with the Provisioner backend. It uses Spring's `RestClient` to make REST API calls, handling JSON serialization/deserialization via Jackson and DTOs.

## Dependency

Injected with a `RestClient` bean configured in `RestClientConfig.java` with the base URL from `provisioner.base-url`.

## API Methods

### Authentication (no Bearer token required)

| Method | HTTP Call | Request | Response |
|--------|-----------|---------|----------|
| `register(RegisterRequest)` | `POST /auth/register` | `{email, fullName, password}` | `AuthResponse` |
| `login(LoginRequest)` | `POST /auth/login` | `{email, password}` | `AuthResponse` |
| `validateToken(String token)` | `POST /auth/validate` | `{token: "..."}` | `TokenValidationResponse` |

### Admin Operations (Bearer token required)

| Method | HTTP Call | Request | Response |
|--------|-----------|---------|----------|
| `getAllUsers(String token)` | `GET /admin/users` | — | `UsersResponse` |
| `getSystemStats(String token)` | `GET /admin/stats` | — | `StatsResponse` |
| `createUser(String token, UserRequest)` | `POST /admin/users` | `{email, fullName, password, role}` | `AuthResponse` |
| `updateUser(String token, String userId, UserRequest)` | `PUT /admin/users/{userId}` | `{fullName, role, password?}` | `AuthResponse` |
| `deleteUser(String token, String userId)` | `DELETE /admin/users/{userId}` | — | `MessageResponse` |

## Error Handling

Every method wraps `RestClientException` in a `ProvisionerUnavailableException`:

```java
try {
    return restClient.post()
            .uri("/auth/login")
            .body(request)
            .retrieve()
            .body(AuthResponse.class);
} catch (RestClientException e) {
    throw new ProvisionerUnavailableException(
        "Failed to connect to provisioner service", e);
}
```

This allows controllers to catch a single exception type and present user-friendly error messages.

## Authentication Header Pattern

For endpoints requiring authentication, the JWT token is passed as a Bearer token:

```java
restClient.get()
    .uri("/admin/users")
    .header("Authorization", "Bearer " + token)
    .retrieve()
    .body(UsersResponse.class);
```

The token is retrieved from the HTTP session by the calling controller and passed as a method parameter.
