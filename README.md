# JWT Authentication with Spring Boot, MongoDB, and Google Login

This project demonstrates JWT authentication with Spring Boot, MongoDB for user storage, and Google OAuth integration.

## Features

- User login with email and password
- Social login with Google
- JWT-based authentication
- MongoDB for user data storage
- Role-based authorization (SITE_ADMIN, ADMIN, USER)
- Secure page access
- Registration functionality

## Project Structure

- `src/com/example/helloworld/` - Source code directory
  - `model/` - Data models
  - `repository/` - MongoDB repositories
  - `service/` - Service layer including authentication
  - `controller/` - Web controllers
  - `config/` - Spring Security and application configuration
- `src/main/resources/`
  - `templates/` - Thymeleaf HTML templates
  - `application.properties` - Application configuration

## JWT Implementation

The JWT implementation is based on the `PingController` class which provides:
- JWT generation with RSA encryption
- JWT validation using public/private key pairs
- Claim extraction

## Prerequisites

- JDK 11+
- Maven
- MongoDB (local or cloud instance)
- Google OAuth credentials (for Google sign-in)

## Running the Application

1. Make sure MongoDB is running
2. Configure your Google OAuth client ID in `login.html`
3. Build the application:
   ```sh
   mvn clean package
   ```
4. Run the application:
   ```sh
   java -jar target/helloworld-0.0.1-SNAPSHOT.jar
   ```
5. Access the application at `http://localhost:8080/login`

## Security Considerations

- JWT tokens are stored in HTTP-only cookies
- Password encryption using BCrypt
- CSRF protection is disabled for demo purposes (enable in production)

---

**Note:** This is a demonstration project. For production use, additional security measures should be implemented.

------
Additional notes:
Swagger UI http://localhost:8080/api/swagger-ui/index.html
H2 console http://localhost:8080/api/h2-console

