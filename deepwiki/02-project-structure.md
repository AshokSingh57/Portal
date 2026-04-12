# Project Structure

```
portal/
├── pom.xml                          # Maven build configuration
├── Dockerfile                       # Multi-stage Docker build
├── agentinfrastructure.tf           # Terraform GCP infrastructure
├── application.properties           # (root-level copy, if present)
├── specifications.md                # Conversion specifications
├── plan.md                          # Implementation plan
├── tasks.md                         # Task checklist
├── claude_instructions.md           # Rename instructions (historical)
│
├── src/
│   ├── main/
│   │   ├── java/com/example/portal/
│   │   │   ├── PortalApplication.java          # Spring Boot entry point
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── WebController.java          # Thymeleaf page routes + form handling
│   │   │   │   └── ApiController.java          # AJAX proxy endpoints (/proxy/*)
│   │   │   │
│   │   │   ├── client/
│   │   │   │   └── ProvisionerClient.java      # REST client for Provisioner API
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java         # Session-based auth interceptor
│   │   │   │   └── RestClientConfig.java       # RestClient bean configuration
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java           # Login form data
│   │   │   │   ├── RegisterRequest.java        # Registration form data
│   │   │   │   ├── AuthResponse.java           # Login/register response from Provisioner
│   │   │   │   ├── TokenValidationResponse.java # Token validation response
│   │   │   │   ├── UserDto.java                # User data transfer object
│   │   │   │   ├── UserRequest.java            # Create/update user request
│   │   │   │   ├── UsersResponse.java          # User list response
│   │   │   │   ├── StatsResponse.java          # System statistics response
│   │   │   │   └── MessageResponse.java        # Generic success/message response
│   │   │   │
│   │   │   └── exception/
│   │   │       └── ProvisionerUnavailableException.java  # Connection failure exception
│   │   │
│   │   └── resources/
│   │       ├── application.properties          # Application configuration
│   │       └── templates/
│   │           ├── index.html                  # Home/landing page
│   │           ├── layout.html                 # Base layout (unused after conversion)
│   │           ├── login.html                  # Login page with form POST
│   │           ├── register.html               # Registration page with validation
│   │           ├── dashboard.html              # User dashboard
│   │           └── admin.html                  # Admin panel with user management
│   │
│   └── test/
│       └── resources/
│           └── application-test.properties     # Test configuration
│
└── target/                                     # Maven build output
```

## Package Responsibilities

| Package | Purpose |
|---------|---------|
| `controller` | HTTP request handling — page rendering (WebController) and AJAX proxy (ApiController) |
| `client` | Outbound HTTP calls to the Provisioner service |
| `config` | Application configuration — RestClient bean and security interceptor |
| `dto` | Data transfer objects matching Provisioner API request/response formats |
| `exception` | Custom exception for Provisioner connectivity failures |
