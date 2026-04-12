# Provisioner API Reference

This documents the Provisioner endpoints that Portal depends on. The Provisioner runs at the URL configured in `provisioner.base-url` (default: `http://localhost:8081/api`).

## Authentication Endpoints (Public)

### POST /auth/register
Register a new user.

**Request:**
```json
{
  "email": "user@example.com",
  "fullName": "John Doe",
  "password": "MyPassword1@"
}
```

**Response (success):**
```json
{
  "success": true,
  "userId": "abc123",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "User"
}
```

**Password Requirements:** min 8 characters, 1 number, 1 special character.

---

### POST /auth/login
Authenticate user and receive JWT token.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "MyPassword1@"
}
```

**Response (success):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "userId": "abc123",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "Admin",
  "lastLoginTime": "2026-03-23T13:00:00"
}
```

**Response (failure):**
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

---

### POST /auth/validate
Validate a JWT token and extract user info.

**Request:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

**Response (valid):**
```json
{
  "success": true,
  "message": "Token is valid",
  "userId": "abc123",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "Admin"
}
```

---

## Admin Endpoints (Require Bearer Token + Admin Role)

All requests require header: `Authorization: Bearer <jwt-token>`

### GET /admin/users
List all users.

**Response:**
```json
{
  "success": true,
  "users": [
    {
      "id": "abc123",
      "email": "admin@myexample.com",
      "fullName": "Joe Brown",
      "role": "Admin",
      "authenticationType": "Custom",
      "lastLoginTime": "2026-03-23T13:00:00",
      "createdAt": "2026-01-01T00:00:00",
      "enabled": true
    }
  ],
  "count": 1
}
```

### GET /admin/stats
System statistics.

**Response:**
```json
{
  "success": true,
  "stats": {
    "totalUsers": 5,
    "adminUsers": 1,
    "regularUsers": 4,
    "activeToday": 2
  }
}
```

### POST /admin/users
Create a new user.

**Request:**
```json
{
  "email": "newuser@example.com",
  "fullName": "New User",
  "password": "Password1@",
  "role": "User"
}
```

### PUT /admin/users/{userId}
Update an existing user.

**Request:**
```json
{
  "fullName": "Updated Name",
  "role": "Admin",
  "password": "NewPassword1@"
}
```

Password is optional — omit to keep current password.

### DELETE /admin/users/{userId}
Delete a user.

**Response:**
```json
{
  "success": true,
  "message": "User deleted successfully"
}
```

---

## JWT Operations (Require Bearer Token)

These are accessed via Provisioner's Swagger UI, not through Portal:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/jwt/generate` | POST | Generate JWT with custom claims |
| `/jwt/generate-user` | POST | Generate JWT for a user |
| `/jwt/validate` | POST | Validate JWT |
| `/jwt/claims` | POST | Extract claims |
| `/jwt/principal` | POST | Extract principal |
| `/jwt/demo` | POST | Full JWT workflow demo |

## Cryptographic Operations (Require Bearer Token)

Also accessed via Provisioner's Swagger UI:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/crypto/encrypt` | POST | RSA encrypt |
| `/crypto/decrypt` | POST | RSA decrypt |
| `/crypto/test` | POST | RSA round-trip test |
| `/crypto/symmetric-encrypt` | POST | AES encrypt |
| `/crypto/symmetric-decrypt` | POST | AES decrypt |
| `/crypto/test-symmetric` | POST | AES round-trip test |
| `/crypto/secure-jwt` | POST | Encrypt JWT with AES |
| `/crypto/unsecure-jwt` | POST | Decrypt JWT |
| `/crypto/test-jwt-security` | POST | JWT security round-trip |
