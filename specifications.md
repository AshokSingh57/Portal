# Portal Application Specifications

## Overview

Convert the Portal application from a full-stack monolith into a **thin web frontend** that serves Thymeleaf HTML pages and delegates all backend operations to the **Provisioner** service at `http://localhost:8081/api`.

Portal retains: HTML templates, page routing, client-side security (JWT cookie/session management), and a service client layer for calling the Provisioner API.

Portal deletes: all local REST controllers, services, repositories, utility classes, MongoDB/H2 dependencies, and cryptographic libraries — the Provisioner owns all business logic and data.

---

## 1. Code to Delete

### 1.1 REST Controllers (delete entirely)
| File | Reason |
|------|--------|
| `src/main/java/com/example/portal/controller/AuthController.java` | Auth handled by Provisioner `/auth/**` |
| `src/main/java/com/example/portal/controller/AdminController.java` | Admin handled by Provisioner `/admin/**` |
| `src/main/java/com/example/portal/controller/JwtController.java` | JWT ops handled by Provisioner `/jwt/**` |
| `src/main/java/com/example/portal/controller/CryptoController.java` | Crypto ops handled by Provisioner `/crypto/**` |

### 1.2 Services (delete entirely)
| File | Reason |
|------|--------|
| `src/main/java/com/example/portal/service/UserRegistrationService.java` | Provisioner handles registration |
| `src/main/java/com/example/portal/service/CustomAuthenticationService.java` | Provisioner handles authentication |
| `src/main/java/com/example/portal/service/AdminService.java` | Provisioner handles admin operations |

### 1.3 Repositories (delete entirely)
| File | Reason |
|------|--------|
| `src/main/java/com/example/portal/repository/UserRepository.java` | No local database |

### 1.4 Models (delete entirely)
| File | Reason |
|------|--------|
| `src/main/java/com/example/portal/model/User.java` | No local database; replaced by DTO classes |

### 1.5 Utilities (delete entirely)
| File | Reason |
|------|--------|
| `src/main/java/com/example/portal/utilities/JwtUtils.java` | JWT operations delegated to Provisioner |
| `src/main/java/com/example/portal/utilities/CryptoUtils.java` | Crypto operations delegated to Provisioner |

### 1.6 Configuration (delete or modify)
| File | Action |
|------|--------|
| `src/main/java/com/example/portal/config/JwtAuthenticationFilter.java` | **Delete** — no local JWT validation |
| `src/main/java/com/example/portal/config/OAuth2LoginSuccessHandler.java` | **Delete** — OAuth2 handled by Provisioner |
| `src/main/java/com/example/portal/config/OpenApiConfig.java` | **Delete** — no local API to document |
| `src/main/java/com/example/portal/config/SecurityConfig.java` | **Rewrite** — see §3.2 |

### 1.7 Cryptographic Key Files (delete)
| File | Reason |
|------|--------|
| `src/main/resources/privatekey.pem` | Provisioner owns cryptographic keys |
| `src/main/resources/publickey.pem` | Provisioner owns cryptographic keys |

### 1.8 Test Files (delete or rewrite)
All test files under `src/test/java/com/example/portal/` that reference deleted services, repositories, or controllers should be deleted. New tests will target the client layer and web controllers.

### 1.9 POM Dependencies to Remove
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-security` (replaced with simpler session-based approach)
- `spring-boot-starter-oauth2-client`
- `h2` database
- `bouncycastle` (bcprov-jdk18on, bcpkix-jdk18on)
- `spring-security-core`, `spring-security-crypto`
- `springdoc-openapi-starter-webmvc-ui`
- `thymeleaf-extras-springsecurity6`

---

## 2. New Code to Create

### 2.1 Provisioner Client Service

**File:** `src/main/java/com/example/portal/client/ProvisionerClient.java`

A `@Service` class using Spring's `RestClient` (or `RestTemplate`) to call the Provisioner API at the configured base URL.

**Configuration property:**
```properties
provisioner.base-url=http://localhost:8081/api
```

**Methods (mirror Provisioner endpoints):**

#### Authentication
| Method | HTTP Call | Returns |
|--------|-----------|---------|
| `register(String email, String fullName, String password)` | `POST /auth/register` | `AuthResponse` |
| `login(String email, String password)` | `POST /auth/login` | `AuthResponse` (includes JWT token) |
| `validateToken(String token)` | `POST /auth/validate` | `TokenValidationResponse` |

#### Admin
| Method | HTTP Call | Returns |
|--------|-----------|---------|
| `getAllUsers(String token)` | `GET /admin/users` | `UsersResponse` |
| `getSystemStats(String token)` | `GET /admin/stats` | `StatsResponse` |
| `createUser(String token, UserRequest request)` | `POST /admin/users` | `AuthResponse` |
| `updateUser(String token, String userId, UserRequest request)` | `PUT /admin/users/{userId}` | `AuthResponse` |
| `deleteUser(String token, String userId)` | `DELETE /admin/users/{userId}` | `MessageResponse` |
| `getUsersByRole(String token, String role)` | `GET /admin/users/role/{role}` | `UsersResponse` |

#### JWT Operations
| Method | HTTP Call | Returns |
|--------|-----------|---------|
| `generateJwt(String token, Map<String,Object> attributes)` | `POST /jwt/generate` | `JwtResponse` |
| `generateUserJwt(String token, String username, List<String> roles)` | `POST /jwt/generate-user` | `JwtResponse` |
| `validateJwt(String token, String jwt)` | `POST /jwt/validate` | `JwtValidationResponse` |
| `extractClaims(String token, String jwt)` | `POST /jwt/claims` | `ClaimsResponse` |
| `extractPrincipal(String token, String jwt)` | `POST /jwt/principal` | `PrincipalResponse` |
| `jwtDemo(String token, String username, List<String> roles)` | `POST /jwt/demo` | `JwtDemoResponse` |

#### Cryptographic Operations
| Method | HTTP Call | Returns |
|--------|-----------|---------|
| `rsaEncrypt(String token, String plainText)` | `POST /crypto/encrypt` | `String` |
| `rsaDecrypt(String token, String cipherText)` | `POST /crypto/decrypt` | `String` |
| `rsaTest(String token, String plainText)` | `POST /crypto/test` | `String` |
| `aesEncrypt(String token, String plainText)` | `POST /crypto/symmetric-encrypt` | `String` |
| `aesDecrypt(String token, String cipherText)` | `POST /crypto/symmetric-decrypt` | `String` |
| `aesTest(String token, String plainText)` | `POST /crypto/test-symmetric` | `String` |
| `secureJwt(String token, String jwt)` | `POST /crypto/secure-jwt` | `String` |
| `unsecureJwt(String token, String encryptedJwt)` | `POST /crypto/unsecure-jwt` | `String` |
| `testJwtSecurity(String token, String jwt)` | `POST /crypto/test-jwt-security` | `String` |

All authenticated methods pass the JWT token via the `Authorization: Bearer <token>` header.

### 2.2 DTO Classes

**Package:** `com.example.portal.dto`

| Class | Fields |
|-------|--------|
| `LoginRequest` | `String email, String password` |
| `RegisterRequest` | `String email, String fullName, String password` |
| `AuthResponse` | `boolean success, String token, String userId, String email, String fullName, String role, String lastLoginTime` |
| `TokenValidationResponse` | `boolean success, String message, String userId, String email, String fullName, String role` |
| `UsersResponse` | `boolean success, List<UserDto> users, int count` |
| `UserDto` | `String id, String email, String fullName, String role, String authenticationType, String lastLoginTime, String createdAt, boolean enabled` |
| `UserRequest` | `String email, String fullName, String password, String role` |
| `StatsResponse` | `boolean success, StatsDto stats` |
| `StatsDto` | `int totalUsers, int adminUsers, int regularUsers, int activeToday` |
| `MessageResponse` | `boolean success, String message` |
| `JwtResponse` | `String jwt, String username, String status` |
| `JwtValidationResponse` | `boolean valid, boolean expired, String message, String status` |
| `ClaimsResponse` | `Map<String,Object> claims, String status` |
| `PrincipalResponse` | `String principal, String status` |
| `JwtDemoResponse` | `String jwt, boolean valid, boolean expired, Map<String,Object> claims, String principal, String status` |

### 2.3 Rewritten Web Controller

**File:** `src/main/java/com/example/portal/controller/WebController.java` (rewrite)

The web controller serves Thymeleaf pages and handles form submissions by calling the Provisioner through `ProvisionerClient`.

| Route | Method | Behavior |
|-------|--------|----------|
| `GET /` | `homePage()` | Render `index.html` |
| `GET /login` | `loginPage()` | Render `login.html` |
| `GET /register` | `registerPage()` | Render `register.html` |
| `POST /login` | `handleLogin(email, password)` | Call `provisionerClient.login()`, store JWT token in HTTP session, redirect to `/dashboard` |
| `POST /register` | `handleRegister(email, fullName, password)` | Call `provisionerClient.register()`, redirect to `/login` with success message |
| `GET /dashboard` | `dashboardPage(session, model)` | Retrieve JWT from session, call `provisionerClient.validateToken()` to get user info, populate Thymeleaf model, render `dashboard.html` |
| `GET /admin` | `adminPage(session, model)` | Retrieve JWT from session, call `provisionerClient.validateToken()` to verify admin role, render `admin.html` |
| `GET /logout` | `logout(session)` | Invalidate HTTP session, redirect to `/login` |

### 2.4 API Proxy Controller

**File:** `src/main/java/com/example/portal/controller/ApiController.java` (new)

AJAX endpoints consumed by the HTML templates (admin.html, dashboard.html). These proxy calls to the Provisioner, passing the JWT token from the HTTP session.

| Route | Method | Proxies to |
|-------|--------|------------|
| `GET /api/admin/users` | `getUsers(session)` | `provisionerClient.getAllUsers(token)` |
| `GET /api/admin/stats` | `getStats(session)` | `provisionerClient.getSystemStats(token)` |
| `POST /api/admin/users` | `createUser(session, body)` | `provisionerClient.createUser(token, body)` |
| `PUT /api/admin/users/{id}` | `updateUser(session, id, body)` | `provisionerClient.updateUser(token, id, body)` |
| `DELETE /api/admin/users/{id}` | `deleteUser(session, id)` | `provisionerClient.deleteUser(token, id)` |
| `POST /api/jwt/generate` | `generateJwt(session, body)` | `provisionerClient.generateJwt(token, body)` |
| `POST /api/jwt/validate` | `validateJwt(session, body)` | `provisionerClient.validateJwt(token, body)` |
| `POST /api/jwt/claims` | `extractClaims(session, body)` | `provisionerClient.extractClaims(token, body)` |
| `POST /api/crypto/encrypt` | `rsaEncrypt(session, body)` | `provisionerClient.rsaEncrypt(token, body)` |
| `POST /api/crypto/decrypt` | `rsaDecrypt(session, body)` | `provisionerClient.rsaDecrypt(token, body)` |
| `POST /api/crypto/symmetric-encrypt` | `aesEncrypt(session, body)` | `provisionerClient.aesEncrypt(token, body)` |
| `POST /api/crypto/symmetric-decrypt` | `aesDecrypt(session, body)` | `provisionerClient.aesDecrypt(token, body)` |

### 2.5 Security Configuration (Rewrite)

**File:** `src/main/java/com/example/portal/config/SecurityConfig.java` (rewrite)

Replace the current JWT filter + OAuth2 configuration with simple session-based security:

```
Public pages:   /, /login, /register, /error, /css/**, /js/**
Protected pages: /dashboard, /admin, /api/**
```

- No JWT filter (the JWT is stored in the HTTP session and forwarded to Provisioner)
- No OAuth2 configuration (Provisioner handles OAuth2 directly — see §4)
- Session-based authentication: `WebController.handleLogin()` stores the JWT and user info in the HTTP session after a successful Provisioner login call
- A simple `HandlerInterceptor` or `Filter` checks for a valid session on protected routes; redirects to `/login` if absent

### 2.6 RestClient Configuration

**File:** `src/main/java/com/example/portal/config/RestClientConfig.java` (new)

Configure a `RestClient` bean with:
- Base URL from `provisioner.base-url` property
- Default `Content-Type: application/json`
- Connection timeout: 5 seconds
- Read timeout: 30 seconds
- Error handler that converts Provisioner error responses into meaningful exceptions

---

## 3. Configuration Changes

### 3.1 application.properties (rewrite)

Remove all database, MongoDB, cryptographic, JWT secret, and OAuth2 properties. Replace with:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Application
spring.application.name=portal

# Provisioner Backend
provisioner.base-url=http://localhost:8081/api

# Logging
logging.level.com.example.portal=DEBUG
logging.level.org.springframework.web=DEBUG

# Session (store JWT token server-side)
server.servlet.session.timeout=24h
```

### 3.2 application-test.properties (rewrite)

```properties
spring.application.name=portal-test
provisioner.base-url=http://localhost:8081/api
server.servlet.session.timeout=1h
```

### 3.3 pom.xml Dependencies (final set)

**Keep:**
- `spring-boot-starter-web`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`
- `jackson-databind`
- `spring-boot-devtools`

**Add:**
- (none — `RestClient` is included in `spring-boot-starter-web`)

**Remove:**
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-client`
- `h2`
- `bouncycastle` (both artifacts)
- `spring-security-core`
- `spring-security-crypto`
- `springdoc-openapi-starter-webmvc-ui`
- `thymeleaf-extras-springsecurity6`

---

## 4. Template Changes

### 4.1 login.html
- **Form submission**: Change from JavaScript `fetch('/api/auth/login')` to a standard HTML form `POST /login` handled by `WebController.handleLogin()`
- **Google OAuth2 button**: Change the link to point to the Provisioner's Google OAuth2 endpoint: `http://localhost:8081/api/oauth2/authorization/google`. After Provisioner completes OAuth2, it should redirect back to Portal with a token (requires Provisioner to support a `redirect_uri` parameter — see §5)
- **Error display**: Show server-side error messages via Thymeleaf `th:if` / `th:text` attributes from the model

### 4.2 register.html
- **Form submission**: Change from JavaScript `fetch('/api/auth/register')` to a standard HTML form `POST /register` handled by `WebController.handleRegister()`
- **Client-side validation**: Keep the existing JavaScript password validation
- **Google OAuth2 button**: Same as login — link to Provisioner's OAuth2 endpoint

### 4.3 dashboard.html
- **User info**: Populate from Thymeleaf model attributes (set by `WebController.dashboardPage()` after calling `provisionerClient.validateToken()`)
- **AJAX calls**: Update JavaScript `fetch()` URLs to use Portal's proxy API endpoints (`/api/jwt/*`, `/api/crypto/*`) instead of calling Provisioner directly
- **Token header**: Remove `Authorization: Bearer` header from JavaScript fetch calls — the Portal proxy handles authentication via the server-side session
- **Swagger link**: Point to Provisioner's Swagger UI: `http://localhost:8081/api/swagger-ui.html`

### 4.4 admin.html
- **AJAX calls**: Update JavaScript `fetch()` URLs to use Portal's proxy API endpoints (`/api/admin/*`)
- **Token header**: Remove `Authorization: Bearer` header from JavaScript fetch calls
- **User info**: Populate from Thymeleaf model attributes

### 4.5 layout.html
- No functional changes needed; navigation links remain the same

### 4.6 index.html
- No functional changes needed

---

## 5. OAuth2 Consideration

Currently, Google OAuth2 is handled entirely within the Portal application (OAuth2LoginSuccessHandler). After this conversion, OAuth2 is owned by the Provisioner.

**Approach**: The Portal login page links to the Provisioner's OAuth2 authorization endpoint. The Provisioner completes the Google OAuth2 flow and redirects back to the Portal with a JWT token as a query parameter:

```
Provisioner redirects to: http://localhost:8080/api/oauth2/callback?token=<jwt>
```

**Portal needs:** A new route `GET /oauth2/callback` in `WebController` that:
1. Extracts the `token` query parameter
2. Calls `provisionerClient.validateToken(token)` to get user info
3. Stores the token and user info in the HTTP session
4. Redirects to `/dashboard`

**Provisioner needs:** Modification to `OAuth2LoginSuccessHandler` to accept a `redirect_uri` parameter and redirect there with the token instead of always redirecting to its own dashboard. (This is a Provisioner-side change, documented here for completeness.)

---

## 6. Error Handling

### 6.1 Provisioner Unavailable
When the Provisioner is unreachable, the `ProvisionerClient` should catch connection exceptions and throw a custom `ProvisionerUnavailableException`. The `WebController` catches this and renders an error page or redirects to login with an error message.

### 6.2 Authentication Failures
When login or token validation fails (4xx from Provisioner), the `WebController` displays the error message on the login page via Thymeleaf model attributes.

### 6.3 Authorization Failures
When a non-admin tries to access `/admin`, the `WebController` checks the role from the session and redirects to `/dashboard` with an "access denied" message.

---

## 7. File Structure After Conversion

```
src/main/java/com/example/portal/
├── PortalApplication.java
├── client/
│   └── ProvisionerClient.java
├── config/
│   ├── RestClientConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── WebController.java
│   └── ApiController.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── AuthResponse.java
│   ├── TokenValidationResponse.java
│   ├── UsersResponse.java
│   ├── UserDto.java
│   ├── UserRequest.java
│   ├── StatsResponse.java
│   ├── StatsDto.java
│   ├── MessageResponse.java
│   ├── JwtResponse.java
│   ├── JwtValidationResponse.java
│   ├── ClaimsResponse.java
│   ├── PrincipalResponse.java
│   └── JwtDemoResponse.java
└── exception/
    └── ProvisionerUnavailableException.java

src/main/resources/
├── application.properties
├── templates/
│   ├── index.html
│   ├── layout.html
│   ├── login.html      (modified)
│   ├── register.html   (modified)
│   ├── dashboard.html  (modified)
│   └── admin.html      (modified)
└── static/             (if any CSS/JS assets)
```

---

## 8. Implementation Order

1. **Remove POM dependencies** — strip out database, security, crypto, and OpenAPI dependencies
2. **Delete files** — remove all files listed in §1
3. **Create DTOs** — define all request/response classes (§2.2)
4. **Create RestClientConfig** — configure the HTTP client (§2.6)
5. **Create ProvisionerClient** — implement all Provisioner API calls (§2.1)
6. **Rewrite SecurityConfig** — session-based route protection (§2.5)
7. **Rewrite WebController** — page rendering with form handling (§2.3)
8. **Create ApiController** — AJAX proxy endpoints (§2.4)
9. **Update templates** — modify login, register, dashboard, admin HTML (§4)
10. **Update application.properties** — new minimal configuration (§3.1)
11. **Create error handling** — ProvisionerUnavailableException and error pages (§6)
12. **Write tests** — test WebController and ProvisionerClient with mocked HTTP
13. **Compile and verify** — `mvn compile` succeeds
14. **Integration test** — start Provisioner on 8081, start Portal on 8080, verify full flow
