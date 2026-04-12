# Plan: Convert Portal to Thin Web Frontend Calling Provisioner API

## Context
The Portal application is currently a full-stack monolith with its own REST controllers, services, repositories, MongoDB/H2 databases, crypto utilities, and JWT handling — all duplicating the Provisioner application. The goal is to strip Portal down to a **web-only frontend** that serves Thymeleaf HTML pages and proxies all backend operations to the Provisioner service at `http://localhost:8081/api`. This eliminates code duplication and establishes a clean frontend/backend separation.

The existing templates (login.html, admin.html, dashboard.html) use **client-side JavaScript** with `localStorage` for JWT tokens and `fetch()` with `Authorization: Bearer` headers for AJAX calls. The conversion will shift to **server-side session** storage for the JWT token, with the Portal acting as a proxy between the browser and Provisioner.

---

## Step 1: Strip POM dependencies

**File:** `pom.xml`

Remove these dependencies:
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-client`
- `h2`
- `bcprov-jdk18on`, `bcpkix-jdk18on`
- `spring-security-core`, `spring-security-crypto`
- `springdoc-openapi-starter-webmvc-ui`
- `thymeleaf-extras-springsecurity6`

Keep: `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-validation`, `spring-boot-starter-test`, `jackson-databind`, `spring-boot-devtools`

---

## Step 2: Delete backend Java files

Delete these files entirely:

**Controllers:**
- `src/main/java/com/example/portal/controller/AuthController.java`
- `src/main/java/com/example/portal/controller/AdminController.java`
- `src/main/java/com/example/portal/controller/JwtController.java`
- `src/main/java/com/example/portal/controller/CryptoController.java`

**Services:**
- `src/main/java/com/example/portal/service/UserRegistrationService.java`
- `src/main/java/com/example/portal/service/CustomAuthenticationService.java`
- `src/main/java/com/example/portal/service/AdminService.java`

**Repository:**
- `src/main/java/com/example/portal/repository/UserRepository.java`

**Model:**
- `src/main/java/com/example/portal/model/User.java`

**Utilities:**
- `src/main/java/com/example/portal/utilities/JwtUtils.java`
- `src/main/java/com/example/portal/utilities/CryptoUtils.java`

**Config (delete):**
- `src/main/java/com/example/portal/config/JwtAuthenticationFilter.java`
- `src/main/java/com/example/portal/config/OAuth2LoginSuccessHandler.java`
- `src/main/java/com/example/portal/config/OpenApiConfig.java`
- `src/main/java/com/example/portal/config/SecurityConfig.java`

**Crypto keys:**
- `src/main/resources/privatekey.pem`
- `src/main/resources/publickey.pem`

**All test files:**
- `src/test/java/com/example/portal/junit5/TestCryptoUtils.java`
- `src/test/java/com/example/portal/junit5/TestJwtUtils.java`
- `src/test/java/com/example/portal/service/UserRegistrationServiceStartupTest.java`
- `src/test/java/com/example/portal/service/TestAdminServicePublicMethods.java`
- `src/test/java/com/example/portal/service/TestAdminServiceIntegration.java`
- `src/test/java/com/example/portal/service/AdminServiceIntegrationTest.java`
- `src/test/java/com/example/portal/service/TestAdminService.java`

Remove empty directories: `service/`, `repository/`, `model/`, `utilities/`, `junit5/`

---

## Step 3: Create DTO classes

**Directory:** `src/main/java/com/example/portal/dto/`

Simple POJOs (no annotations needed beyond Jackson defaults):

| Class | Fields |
|-------|--------|
| `LoginRequest` | `String email, String password` |
| `RegisterRequest` | `String email, String fullName, String password` |
| `AuthResponse` | `boolean success, String token, String userId, String email, String fullName, String role, String lastLoginTime, String message` |
| `TokenValidationResponse` | `boolean success, String message, String userId, String email, String fullName, String role` |
| `UserDto` | `String id, String email, String fullName, String role, String authenticationType, String lastLoginTime, String createdAt, boolean enabled` |
| `UserRequest` | `String email, String fullName, String password, String role` |
| `UsersResponse` | `boolean success, List<UserDto> users, int count` |
| `StatsResponse` | `boolean success, Map<String,Object> stats` |
| `MessageResponse` | `boolean success, String message` |

---

## Step 4: Create RestClient configuration

**New file:** `src/main/java/com/example/portal/config/RestClientConfig.java`

- Define a `RestClient` bean with base URL from `provisioner.base-url` property
- Set `Content-Type: application/json` default header
- Configure connection timeout (5s) and read timeout (30s)

---

## Step 5: Create ProvisionerClient service

**New file:** `src/main/java/com/example/portal/client/ProvisionerClient.java`

A `@Service` using `RestClient` to call Provisioner. All authenticated methods accept a `String token` parameter and set `Authorization: Bearer <token>` header.

Methods:
- `AuthResponse register(RegisterRequest request)` → POST `/auth/register`
- `AuthResponse login(LoginRequest request)` → POST `/auth/login`
- `TokenValidationResponse validateToken(String token)` → POST `/auth/validate`
- `UsersResponse getAllUsers(String token)` → GET `/admin/users`
- `StatsResponse getSystemStats(String token)` → GET `/admin/stats`
- `AuthResponse createUser(String token, UserRequest request)` → POST `/admin/users`
- `AuthResponse updateUser(String token, String userId, UserRequest request)` → PUT `/admin/users/{userId}`
- `MessageResponse deleteUser(String token, String userId)` → DELETE `/admin/users/{userId}`

Wrap connection failures in a `ProvisionerUnavailableException` (new file in `exception/` package).

---

## Step 6: Create session-based security interceptor

**New file:** `src/main/java/com/example/portal/config/SecurityConfig.java`

Implement `WebMvcConfigurer` with a `HandlerInterceptor` that:
- Allows public access to: `/`, `/login`, `/register`, `/error`, `/oauth2/callback`
- For `/dashboard`, `/admin`, `/proxy/**`: checks `HttpSession` for `authToken` attribute; redirects to `/login` if absent
- For `/admin`: additionally checks session `userRole` is `Admin`; redirects to `/dashboard` if not

---

## Step 7: Rewrite WebController

**Modify:** `src/main/java/com/example/portal/controller/WebController.java`

| Route | Action |
|-------|--------|
| `GET /` | Render `index.html` |
| `GET /login` | If session has token, redirect to `/dashboard`. Otherwise render `login.html` |
| `GET /register` | If session has token, redirect to `/dashboard`. Otherwise render `register.html` |
| `POST /login` | Call `provisionerClient.login()`. On success: store token + user info in session, redirect to `/dashboard`. On failure: add error to model, render `login.html` |
| `POST /register` | Call `provisionerClient.register()`. On success: redirect to `/login?registered`. On failure: add error to model, render `register.html` |
| `GET /dashboard` | Get token from session, call `provisionerClient.validateToken()`, populate model with user info, render `dashboard.html` |
| `GET /admin` | Same as dashboard but verify admin role, render `admin.html` |
| `GET /logout` | Invalidate session, redirect to `/login?logout` |
| `GET /oauth2/callback` | Extract `token` param, validate via `provisionerClient.validateToken()`, store in session, redirect to `/dashboard` |

---

## Step 8: Create API proxy controller

**New file:** `src/main/java/com/example/portal/controller/ApiController.java`

`@RestController` with `@RequestMapping("/proxy")` — AJAX endpoints that templates call.

Each method retrieves `authToken` from `HttpSession` and delegates to `ProvisionerClient`:

- `GET /proxy/admin/users` → `provisionerClient.getAllUsers(token)`
- `GET /proxy/admin/stats` → `provisionerClient.getSystemStats(token)`
- `POST /proxy/admin/users` → `provisionerClient.createUser(token, body)`
- `PUT /proxy/admin/users/{id}` → `provisionerClient.updateUser(token, id, body)`
- `DELETE /proxy/admin/users/{id}` → `provisionerClient.deleteUser(token, id)`

Using `/proxy` prefix avoids collision with the page routes at `/admin`.

---

## Step 9: Update templates

### login.html
- Change JS `fetch('/api/auth/login')` to HTML form `POST` to `/login` (server-side handling)
- **Chosen approach:** Convert to standard form POST. Remove `localStorage` usage. Server handles redirect.
- Update Google OAuth2 link from `/api/oauth2/authorization/google` to `http://localhost:8081/api/oauth2/authorization/google` (Provisioner)
- Add Thymeleaf `th:if="${error}"` for server-side error display

### register.html
- Convert JS fetch to standard form POST to `/register`
- Keep client-side password validation JS
- Remove `localStorage` usage

### dashboard.html
- Remove all `localStorage` token management code
- Populate user info via Thymeleaf model attributes (`th:text="${fullName}"`, `th:text="${role}"`, etc.)
- Show/hide admin elements via Thymeleaf `th:if="${role == 'Admin'}"`
- Update Swagger links to point to Provisioner: `http://localhost:8081/api/swagger-ui/index.html`
- Remove `validateTokenWithServer()` JS — server already validated before rendering
- Change logout from JS to link: `<a href="/logout">`

### admin.html
- Remove `localStorage` token management and `checkAdminAccess()` JS
- Populate nav username via Thymeleaf: `th:text="${fullName}"`
- Change AJAX `fetch('/api/admin/users')` to `fetch('/proxy/admin/users')` (no Auth header needed — session-based)
- Change AJAX `fetch('/api/admin/users/${userId}')` to `fetch('/proxy/admin/users/${userId}')`
- Remove all `Authorization: Bearer` headers from fetch calls
- Change logout to `<a href="/logout">`

---

## Step 10: Update application.properties

**Rewrite** `src/main/resources/application.properties`:
```properties
server.port=8080
server.servlet.context-path=/api
spring.application.name=portal
provisioner.base-url=http://localhost:8081/api
logging.level.com.example.portal=DEBUG
logging.level.org.springframework.web=DEBUG
server.servlet.session.timeout=24h
```

Remove all MongoDB, H2, JPA, crypto, JWT, OAuth2, and actuator properties.

**Rewrite** `src/test/resources/application-test.properties` similarly.

---

## Step 11: Create error handling

**New file:** `src/main/java/com/example/portal/exception/ProvisionerUnavailableException.java`
- Runtime exception thrown when Provisioner is unreachable

Add `@ControllerAdvice` or handle in WebController: catch `ProvisionerUnavailableException` and redirect to login with error message.

---

## Step 12: Compile and verify

```bash
mvn clean compile
```

---

## Step 13: Integration test

1. Start Provisioner on port 8081
2. Start Portal on port 8080
3. Verify: `http://localhost:8080/api/login` renders
4. Login with `admin@myexample.com` / `Mynewcadillac1@`
5. Verify redirect to dashboard with user info
6. Verify admin panel loads users
7. Verify logout works

---

## Final file structure
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
│   ├── UserDto.java
│   ├── UserRequest.java
│   ├── UsersResponse.java
│   ├── StatsResponse.java
│   └── MessageResponse.java
└── exception/
    └── ProvisionerUnavailableException.java
```
