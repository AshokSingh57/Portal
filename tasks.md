# Tasks: Portal Conversion to Thin Web Frontend

## Step 1: Strip POM dependencies
- [ ] 1.1 Remove `spring-boot-starter-data-jpa` from pom.xml
- [ ] 1.2 Remove `spring-boot-starter-data-mongodb` from pom.xml
- [ ] 1.3 Remove `spring-boot-starter-security` from pom.xml
- [ ] 1.4 Remove `spring-boot-starter-oauth2-client` from pom.xml
- [ ] 1.5 Remove `h2` database dependency from pom.xml
- [ ] 1.6 Remove `bcprov-jdk18on` (Bouncy Castle) from pom.xml
- [ ] 1.7 Remove `bcpkix-jdk18on` (Bouncy Castle PKIX) from pom.xml
- [ ] 1.8 Remove `spring-security-core` from pom.xml
- [ ] 1.9 Remove `spring-security-crypto` from pom.xml
- [ ] 1.10 Remove `springdoc-openapi-starter-webmvc-ui` from pom.xml
- [ ] 1.11 Remove `thymeleaf-extras-springsecurity6` from pom.xml
- [ ] 1.12 Verify remaining dependencies: `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-validation`, `spring-boot-starter-test`, `jackson-databind`, `spring-boot-devtools`

---

## Step 2: Delete backend Java files
- [ ] 2.1 Delete `src/main/java/com/example/portal/controller/AuthController.java`
- [ ] 2.2 Delete `src/main/java/com/example/portal/controller/AdminController.java`
- [ ] 2.3 Delete `src/main/java/com/example/portal/controller/JwtController.java`
- [ ] 2.4 Delete `src/main/java/com/example/portal/controller/CryptoController.java`
- [ ] 2.5 Delete `src/main/java/com/example/portal/service/UserRegistrationService.java`
- [ ] 2.6 Delete `src/main/java/com/example/portal/service/CustomAuthenticationService.java`
- [ ] 2.7 Delete `src/main/java/com/example/portal/service/AdminService.java`
- [ ] 2.8 Delete `src/main/java/com/example/portal/repository/UserRepository.java`
- [ ] 2.9 Delete `src/main/java/com/example/portal/model/User.java`
- [ ] 2.10 Delete `src/main/java/com/example/portal/utilities/JwtUtils.java`
- [ ] 2.11 Delete `src/main/java/com/example/portal/utilities/CryptoUtils.java`
- [ ] 2.12 Delete `src/main/java/com/example/portal/config/JwtAuthenticationFilter.java`
- [ ] 2.13 Delete `src/main/java/com/example/portal/config/OAuth2LoginSuccessHandler.java`
- [ ] 2.14 Delete `src/main/java/com/example/portal/config/OpenApiConfig.java`
- [ ] 2.15 Delete `src/main/java/com/example/portal/config/SecurityConfig.java`
- [ ] 2.16 Delete `src/main/resources/privatekey.pem`
- [ ] 2.17 Delete `src/main/resources/publickey.pem`
- [ ] 2.18 Delete `src/test/java/com/example/portal/junit5/TestCryptoUtils.java`
- [ ] 2.19 Delete `src/test/java/com/example/portal/junit5/TestJwtUtils.java`
- [ ] 2.20 Delete `src/test/java/com/example/portal/service/UserRegistrationServiceStartupTest.java`
- [ ] 2.21 Delete `src/test/java/com/example/portal/service/TestAdminServicePublicMethods.java`
- [ ] 2.22 Delete `src/test/java/com/example/portal/service/TestAdminServiceIntegration.java`
- [ ] 2.23 Delete `src/test/java/com/example/portal/service/AdminServiceIntegrationTest.java`
- [ ] 2.24 Delete `src/test/java/com/example/portal/service/TestAdminService.java`
- [ ] 2.25 Remove empty directories: `service/`, `repository/`, `model/`, `utilities/`, `junit5/`

---

## Step 3: Create DTO classes
- [ ] 3.1 Create directory `src/main/java/com/example/portal/dto/`
- [ ] 3.2 Create `LoginRequest.java` with fields: `String email, String password`
- [ ] 3.3 Create `RegisterRequest.java` with fields: `String email, String fullName, String password`
- [ ] 3.4 Create `AuthResponse.java` with fields: `boolean success, String token, String userId, String email, String fullName, String role, String lastLoginTime, String message`
- [ ] 3.5 Create `TokenValidationResponse.java` with fields: `boolean success, String message, String userId, String email, String fullName, String role`
- [ ] 3.6 Create `UserDto.java` with fields: `String id, String email, String fullName, String role, String authenticationType, String lastLoginTime, String createdAt, boolean enabled`
- [ ] 3.7 Create `UserRequest.java` with fields: `String email, String fullName, String password, String role`
- [ ] 3.8 Create `UsersResponse.java` with fields: `boolean success, List<UserDto> users, int count`
- [ ] 3.9 Create `StatsResponse.java` with fields: `boolean success, Map<String,Object> stats`
- [ ] 3.10 Create `MessageResponse.java` with fields: `boolean success, String message`

---

## Step 4: Create RestClient configuration
- [ ] 4.1 Create `src/main/java/com/example/portal/config/RestClientConfig.java`
- [ ] 4.2 Define `RestClient` bean with base URL from `provisioner.base-url` property
- [ ] 4.3 Set default `Content-Type: application/json` header
- [ ] 4.4 Configure connection timeout (5 seconds)
- [ ] 4.5 Configure read timeout (30 seconds)

---

## Step 5: Create ProvisionerClient service
- [ ] 5.1 Create directory `src/main/java/com/example/portal/client/`
- [ ] 5.2 Create `ProvisionerClient.java` as `@Service`
- [ ] 5.3 Inject `RestClient` bean
- [ ] 5.4 Implement `register(RegisterRequest request)` → POST `/auth/register` → returns `AuthResponse`
- [ ] 5.5 Implement `login(LoginRequest request)` → POST `/auth/login` → returns `AuthResponse`
- [ ] 5.6 Implement `validateToken(String token)` → POST `/auth/validate` with `{token: token}` → returns `TokenValidationResponse`
- [ ] 5.7 Implement `getAllUsers(String token)` → GET `/admin/users` with Bearer header → returns `UsersResponse`
- [ ] 5.8 Implement `getSystemStats(String token)` → GET `/admin/stats` with Bearer header → returns `StatsResponse`
- [ ] 5.9 Implement `createUser(String token, UserRequest request)` → POST `/admin/users` with Bearer header → returns `AuthResponse`
- [ ] 5.10 Implement `updateUser(String token, String userId, UserRequest request)` → PUT `/admin/users/{userId}` with Bearer header → returns `AuthResponse`
- [ ] 5.11 Implement `deleteUser(String token, String userId)` → DELETE `/admin/users/{userId}` with Bearer header → returns `MessageResponse`
- [ ] 5.12 Add try-catch for connection errors, throw `ProvisionerUnavailableException`

---

## Step 6: Create session-based security interceptor
- [ ] 6.1 Create `src/main/java/com/example/portal/config/SecurityConfig.java` implementing `WebMvcConfigurer`
- [ ] 6.2 Define `AuthInterceptor` as inner class implementing `HandlerInterceptor`
- [ ] 6.3 Allow public access to: `/`, `/login`, `/register`, `/error`, `/oauth2/callback`
- [ ] 6.4 For protected routes (`/dashboard`, `/admin`, `/proxy/**`): check `HttpSession` for `authToken` attribute
- [ ] 6.5 Redirect to `/login` if session has no `authToken`
- [ ] 6.6 For `/admin` route: additionally check session `userRole` equals `Admin`
- [ ] 6.7 Redirect to `/dashboard` if non-admin tries to access `/admin`
- [ ] 6.8 Register interceptor in `addInterceptors()` method

---

## Step 7: Rewrite WebController
- [ ] 7.1 Remove Spring Security imports from `WebController.java`
- [ ] 7.2 Inject `ProvisionerClient` into WebController
- [ ] 7.3 Keep `GET /` → render `index.html`
- [ ] 7.4 Rewrite `GET /login` → check session for token, redirect to `/dashboard` if present, else render `login.html`
- [ ] 7.5 Rewrite `GET /register` → check session for token, redirect to `/dashboard` if present, else render `register.html`
- [ ] 7.6 Add `POST /login` → call `provisionerClient.login()`, store token + user info in session on success, redirect to `/dashboard`; on failure add error to model and render `login.html`
- [ ] 7.7 Add `POST /register` → call `provisionerClient.register()`, redirect to `/login?registered` on success; on failure add error to model and render `register.html`
- [ ] 7.8 Rewrite `GET /dashboard` → get token from session, call `provisionerClient.validateToken()`, populate model with `fullName`, `email`, `role`, `loginTime`, render `dashboard.html`
- [ ] 7.9 Rewrite `GET /admin` → same as dashboard but verify admin role from session, render `admin.html`
- [ ] 7.10 Add `GET /logout` → invalidate session, redirect to `/login?logout`
- [ ] 7.11 Add `GET /oauth2/callback` → extract `token` query param, call `provisionerClient.validateToken()`, store in session, redirect to `/dashboard`
- [ ] 7.12 Add error handling: catch `ProvisionerUnavailableException`, redirect to `/login` with error message

---

## Step 8: Create API proxy controller
- [ ] 8.1 Create `src/main/java/com/example/portal/controller/ApiController.java` as `@RestController`
- [ ] 8.2 Set `@RequestMapping("/proxy")`
- [ ] 8.3 Inject `ProvisionerClient`
- [ ] 8.4 Create helper method to extract `authToken` from `HttpSession`
- [ ] 8.5 Implement `GET /proxy/admin/users` → `provisionerClient.getAllUsers(token)` → return JSON
- [ ] 8.6 Implement `GET /proxy/admin/stats` → `provisionerClient.getSystemStats(token)` → return JSON
- [ ] 8.7 Implement `POST /proxy/admin/users` → `provisionerClient.createUser(token, body)` → return JSON
- [ ] 8.8 Implement `PUT /proxy/admin/users/{id}` → `provisionerClient.updateUser(token, id, body)` → return JSON
- [ ] 8.9 Implement `DELETE /proxy/admin/users/{id}` → `provisionerClient.deleteUser(token, id)` → return JSON
- [ ] 8.10 Add error handling for unauthenticated proxy requests (return 401 JSON)

---

## Step 9: Update templates

### 9A: login.html
- [ ] 9A.1 Replace JavaScript `fetch('/api/auth/login')` with standard HTML form `POST` action to `th:action="@{/login}"`
- [ ] 9A.2 Remove `localStorage.setItem('authToken', ...)` code
- [ ] 9A.3 Remove `localStorage.setItem('userInfo', ...)` code
- [ ] 9A.4 Add Thymeleaf error display: `th:if="${error}"` with `th:text="${error}"`
- [ ] 9A.5 Add Thymeleaf success display for `?registered` and `?logout` query params
- [ ] 9A.6 Update Google OAuth2 link from `/api/oauth2/authorization/google` to `http://localhost:8081/api/oauth2/authorization/google`
- [ ] 9A.7 Remove JavaScript `checkForOAuth2Token()` and URL parameter processing

### 9B: register.html
- [ ] 9B.1 Replace JavaScript `fetch('/api/auth/register')` with standard HTML form `POST` action to `th:action="@{/register}"`
- [ ] 9B.2 Keep client-side password validation JavaScript
- [ ] 9B.3 Remove `localStorage` usage
- [ ] 9B.4 Add Thymeleaf error display: `th:if="${error}"` with `th:text="${error}"`
- [ ] 9B.5 Update Google OAuth2 link to point to Provisioner

### 9C: dashboard.html
- [ ] 9C.1 Remove all `localStorage` token management code (`loadUserInfo()`, `validateTokenWithServer()`)
- [ ] 9C.2 Replace `id="navUsername"` with Thymeleaf: `th:text="${fullName}"`
- [ ] 9C.3 Replace `id="userEmail"` with Thymeleaf: `th:text="${email}"`
- [ ] 9C.4 Replace `id="userRole"` with Thymeleaf: `th:text="${role}"`
- [ ] 9C.5 Replace `id="loginTime"` with Thymeleaf: `th:text="${loginTime}"`
- [ ] 9C.6 Show admin nav item with `th:if="${role == 'Admin'}"` instead of JS `style="display: none;"`
- [ ] 9C.7 Show admin card with `th:if="${role == 'Admin'}"` instead of JS `style="display: none;"`
- [ ] 9C.8 Update Swagger links to `http://localhost:8081/api/swagger-ui/index.html`
- [ ] 9C.9 Change logout from JavaScript `logout()` function to `<a th:href="@{/logout}">`
- [ ] 9C.10 Remove `checkForOAuth2Token()` JavaScript function
- [ ] 9C.11 Remove `updateUIWithUserData()` JavaScript function

### 9D: admin.html
- [ ] 9D.1 Remove `localStorage` token management (`checkAdminAccess()`, `validateAdminTokenWithServer()`)
- [ ] 9D.2 Replace `id="navUsername"` with Thymeleaf: `th:text="${fullName}"`
- [ ] 9D.3 Change `fetch('/api/admin/users')` to `fetch('/api/proxy/admin/users')`
- [ ] 9D.4 Change `fetch('/api/admin/users/${userId}')` (PUT) to `fetch('/api/proxy/admin/users/${userId}')`
- [ ] 9D.5 Change `fetch('/api/admin/users/${userId}')` (DELETE) to `fetch('/api/proxy/admin/users/${userId}')`
- [ ] 9D.6 Change `fetch('/api/admin/users')` (POST) to `fetch('/api/proxy/admin/users')`
- [ ] 9D.7 Remove all `Authorization: Bearer ${authToken}` headers from fetch calls
- [ ] 9D.8 Remove `localStorage.getItem('authToken')` calls
- [ ] 9D.9 Change logout from JavaScript `logout()` function to `<a th:href="@{/logout}">`
- [ ] 9D.10 Remove `DOMContentLoaded` listener calling `checkAdminAccess()`
- [ ] 9D.11 Replace with `DOMContentLoaded` listener calling `loadUsers()` directly

---

## Step 10: Update application.properties
- [ ] 10.1 Remove all MongoDB properties from `application.properties`
- [ ] 10.2 Remove all H2/JPA/datasource properties from `application.properties`
- [ ] 10.3 Remove all crypto properties from `application.properties`
- [ ] 10.4 Remove all JWT properties from `application.properties`
- [ ] 10.5 Remove all OAuth2 properties from `application.properties`
- [ ] 10.6 Remove all actuator/management properties from `application.properties`
- [ ] 10.7 Add `provisioner.base-url=http://localhost:8081/api`
- [ ] 10.8 Add `server.servlet.session.timeout=24h`
- [ ] 10.9 Keep `server.port=8080`, `server.servlet.context-path=/api`, `spring.application.name=portal`
- [ ] 10.10 Keep logging properties for `com.example.portal` and `org.springframework.web`
- [ ] 10.11 Rewrite `src/test/resources/application-test.properties` with minimal config

---

## Step 11: Create error handling
- [ ] 11.1 Create directory `src/main/java/com/example/portal/exception/`
- [ ] 11.2 Create `ProvisionerUnavailableException.java` extending `RuntimeException`
- [ ] 11.3 Add constructor with message parameter
- [ ] 11.4 Add constructor with message and cause parameters
- [ ] 11.5 Handle `ProvisionerUnavailableException` in WebController (redirect to login with error)
- [ ] 11.6 Handle `ProvisionerUnavailableException` in ApiController (return 503 JSON response)

---

## Step 12: Compile and verify
- [ ] 12.1 Run `mvn clean compile`
- [ ] 12.2 Fix any compilation errors
- [ ] 12.3 Verify all deleted files are gone
- [ ] 12.4 Verify all new files are in place
- [ ] 12.5 Verify no references to deleted classes remain

---

## Step 13: Integration test
- [ ] 13.1 Start Provisioner application on port 8081
- [ ] 13.2 Start Portal application on port 8080
- [ ] 13.3 Verify `http://localhost:8080/api/` renders index page
- [ ] 13.4 Verify `http://localhost:8080/api/login` renders login page
- [ ] 13.5 Login with `admin@myexample.com` / `Mynewcadillac1@`
- [ ] 13.6 Verify redirect to dashboard with correct user info displayed
- [ ] 13.7 Verify admin nav link is visible for admin user
- [ ] 13.8 Navigate to admin panel, verify user list loads
- [ ] 13.9 Test create user from admin panel
- [ ] 13.10 Test edit user from admin panel
- [ ] 13.11 Test delete user from admin panel
- [ ] 13.12 Test logout and verify redirect to login page
- [ ] 13.13 Verify protected routes redirect to login when not authenticated
