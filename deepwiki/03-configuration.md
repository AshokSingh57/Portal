# Configuration

## application.properties

```properties
# Spring Boot Application Configuration
spring.application.name=portal

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Provisioner Backend
provisioner.base-url=http://localhost:8081/api

# Logging Configuration
logging.level.com.example.portal=DEBUG
logging.level.org.springframework.web=DEBUG

# Session Configuration
server.servlet.session.timeout=24h
```

### Property Reference

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP port for the Portal web server |
| `server.servlet.context-path` | `/api` | URL prefix for all routes (e.g., `/api/login`) |
| `provisioner.base-url` | `http://localhost:8081/api` | Base URL of the Provisioner backend API |
| `server.servlet.session.timeout` | `24h` | HTTP session timeout (matches Provisioner JWT expiry) |

### Overriding at Runtime

All properties can be overridden via:

1. **Command-line argument**: `--provisioner.base-url=http://host:port/api`
2. **Environment variable**: `PROVISIONER_BASE_URL=http://host:port/api`
3. **System property**: `-Dprovisioner.base-url=http://host:port/api`

Spring Boot's property resolution order applies (command-line > env var > properties file).

## application-test.properties

```properties
spring.application.name=portal-test
provisioner.base-url=http://localhost:8081/api
logging.level.com.example.portal=INFO
server.servlet.session.timeout=1h
```

## pom.xml Dependencies

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-web` | Embedded Tomcat, Spring MVC, RestClient |
| `spring-boot-starter-thymeleaf` | Thymeleaf template engine |
| `spring-boot-starter-validation` | Bean validation (JSR-380) |
| `jackson-databind` | JSON serialization/deserialization for DTOs |
| `spring-boot-starter-test` | Test framework (JUnit 5, Mockito) |
| `spring-boot-devtools` | Hot reload during development |

### Dependencies Removed (from original monolith)

The following were stripped during the conversion to thin frontend:

- `spring-boot-starter-data-jpa` — no local database
- `spring-boot-starter-data-mongodb` — no local database
- `spring-boot-starter-security` — no local auth
- `spring-boot-starter-oauth2-client` — OAuth2 handled by Provisioner
- `h2` — no local database
- `bouncycastle` (bcprov, bcpkix) — crypto handled by Provisioner
- `spring-security-core`, `spring-security-crypto` — no local auth
- `springdoc-openapi-starter-webmvc-ui` — no local API docs
- `thymeleaf-extras-springsecurity6` — no Spring Security integration

## RestClient Configuration

Defined in `RestClientConfig.java`:

| Setting | Value | Purpose |
|---------|-------|---------|
| Base URL | `${provisioner.base-url}` | All requests are relative to this |
| Connect timeout | 5 seconds | Fail fast if Provisioner is unreachable |
| Read timeout | 30 seconds | Allow time for complex operations |
| Default Content-Type | `application/json` | All requests send JSON |
