# Portal Application - Overview

## What is Portal?

Portal is a **thin web frontend** built with Spring Boot 3.2.0 and Thymeleaf that provides a user-facing web interface for authentication, user management, and administrative operations. It delegates all backend business logic to the **Provisioner** microservice via REST API calls.

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Browser                           │
│  (HTML pages, Bootstrap 5, JavaScript AJAX calls)    │
└────────────────────┬────────────────────────────────┘
                     │  HTTP (port 8080)
                     ▼
┌─────────────────────────────────────────────────────┐
│                Portal (this app)                     │
│                                                      │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │WebController  │  │ApiController │                 │
│  │(Thymeleaf     │  │(/proxy/*     │                 │
│  │ page routes)  │  │ AJAX proxy)  │                 │
│  └──────┬───────┘  └──────┬───────┘                 │
│         │                  │                         │
│         ▼                  ▼                         │
│  ┌──────────────────────────────┐                   │
│  │      ProvisionerClient       │                   │
│  │    (RestClient HTTP calls)   │                   │
│  └──────────────┬───────────────┘                   │
│                 │                                    │
│  ┌──────────────┴───────────────┐                   │
│  │      SecurityConfig          │                   │
│  │  (Session-based interceptor) │                   │
│  └──────────────────────────────┘                   │
└─────────────────┬───────────────────────────────────┘
                  │  HTTP (port 8081)
                  ▼
┌─────────────────────────────────────────────────────┐
│             Provisioner (backend)                     │
│                                                      │
│  Auth, Admin, JWT, Crypto, MongoDB, OAuth2           │
│  All business logic, data storage, and security      │
└─────────────────────────────────────────────────────┘
```

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Server-side sessions** for JWT storage | JWT token stored in `HttpSession`, never exposed to browser JavaScript. Eliminates `localStorage` XSS risk. |
| **Proxy pattern** for AJAX calls | Admin panel JavaScript calls Portal's `/proxy/*` endpoints, which forward to Provisioner with the JWT token from the session. Browser never talks directly to Provisioner. |
| **No Spring Security dependency** | Route protection is handled by a simple `HandlerInterceptor` since all authentication logic lives in Provisioner. |
| **Standard HTML form POST** for login/register | Replaces client-side `fetch()` calls. Server handles redirect on success, re-renders page with error on failure. |
| **Thymeleaf server-side rendering** for user data | User info (name, role, email) is injected into templates by the controller, not fetched by JavaScript after page load. |

## Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.2.0 |
| Java | 17 |
| Template Engine | Thymeleaf |
| HTTP Client | Spring RestClient |
| CSS Framework | Bootstrap 5.3.0 |
| Icons | Font Awesome 6.0.0 |
| Build Tool | Maven |
| Container | Docker (multi-stage) |
| Infrastructure | Terraform (GCP Cloud Run) |

## Running the Application

```bash
# Default (Provisioner at localhost:8081)
mvn spring-boot:run

# Custom Provisioner URL
mvn spring-boot:run -Dspring-boot.run.arguments=--provisioner.base-url=http://myhost:9090/api

# Via environment variable
PROVISIONER_BASE_URL=http://myhost:9090/api mvn spring-boot:run
```

Access: `http://localhost:8080/api/login`
