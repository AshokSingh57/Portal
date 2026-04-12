# Data Transfer Objects (DTOs)

**Package:** `com.example.portal.dto`

All DTOs are simple POJOs with getters/setters. Jackson handles JSON serialization/deserialization automatically.

## Request DTOs

### LoginRequest
Sent to `POST /auth/login` on the Provisioner.

| Field | Type | Description |
|-------|------|-------------|
| `email` | String | User's email address |
| `password` | String | User's password |

### RegisterRequest
Sent to `POST /auth/register` on the Provisioner.

| Field | Type | Description |
|-------|------|-------------|
| `email` | String | User's email address |
| `fullName` | String | User's full name |
| `password` | String | User's password (min 8 chars, 1 number, 1 special char) |

### UserRequest
Sent to `POST /admin/users` (create) or `PUT /admin/users/{id}` (update) on the Provisioner.

| Field | Type | Description |
|-------|------|-------------|
| `email` | String | User's email address |
| `fullName` | String | User's full name |
| `password` | String | Password (optional on update) |
| `role` | String | `"User"` or `"Admin"` |

## Response DTOs

### AuthResponse
Returned from login, register, and user create/update operations.

| Field | Type | Description |
|-------|------|-------------|
| `success` | boolean | Whether the operation succeeded |
| `token` | String | JWT token (login only) |
| `userId` | String | User's unique ID |
| `email` | String | User's email |
| `fullName` | String | User's full name |
| `role` | String | `"User"` or `"Admin"` |
| `lastLoginTime` | String | ISO timestamp of last login |
| `message` | String | Error/status message |

### TokenValidationResponse
Returned from `POST /auth/validate`.

| Field | Type | Description |
|-------|------|-------------|
| `success` | boolean | Whether the token is valid |
| `message` | String | Validation message |
| `userId` | String | User's unique ID |
| `email` | String | User's email |
| `fullName` | String | User's full name |
| `role` | String | `"User"` or `"Admin"` |

### UsersResponse
Returned from `GET /admin/users`.

| Field | Type | Description |
|-------|------|-------------|
| `success` | boolean | Whether the operation succeeded |
| `users` | List\<UserDto\> | List of user objects |
| `count` | int | Total user count |

### UserDto
Individual user within `UsersResponse.users`.

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | Unique user ID |
| `email` | String | Email address |
| `fullName` | String | Full name |
| `role` | String | `"User"` or `"Admin"` |
| `authenticationType` | String | `"Custom"` or `"Google"` |
| `lastLoginTime` | String | ISO timestamp |
| `createdAt` | String | ISO timestamp |
| `enabled` | boolean | Whether the account is active |

### StatsResponse
Returned from `GET /admin/stats`.

| Field | Type | Description |
|-------|------|-------------|
| `success` | boolean | Whether the operation succeeded |
| `stats` | Map\<String, Object\> | Key-value statistics (totalUsers, adminUsers, regularUsers, activeToday) |

### MessageResponse
Returned from `DELETE /admin/users/{id}` and other simple operations.

| Field | Type | Description |
|-------|------|-------------|
| `success` | boolean | Whether the operation succeeded |
| `message` | String | Status/error message |
