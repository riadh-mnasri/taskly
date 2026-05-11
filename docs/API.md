# Taskly API Reference

**Base URL (local):** `http://localhost:8081`

**Interactive Docs (Swagger UI):** http://localhost:8081/swagger-ui.html

All endpoints (except auth) require the header:
```
Authorization: Bearer <access_token>
```

---

## Authentication

### POST /api/v1/auth/sign-up
Register a new user.

**Request:**
```json
{
  "email": "alice@example.com",
  "password": "SecurePass1!"
}
```

**Response 201:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "alice@example.com"
}
```

**Errors:** `400` invalid input, `409` email already used

---

### POST /api/v1/auth/sign-in
Authenticate and receive tokens.

**Request:**
```json
{
  "email": "alice@example.com",
  "password": "SecurePass1!"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 900
}
```

**Errors:** `401` invalid credentials

---

### POST /api/v1/auth/refresh
Exchange a refresh token for a new access token.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 900
}
```

---

## Tasks

All task endpoints require authentication. Tasks are scoped to the authenticated user.

### GET /api/v1/tasks
List tasks for the current user.

**Query params:**
| Param | Values | Description |
|-------|--------|-------------|
| `priority` | `HIGH`, `MEDIUM`, `LOW` | Filter by priority |
| `status` | `TODO`, `IN_PROGRESS`, `DONE` | Filter by status |
| `subject` | string | Filter by subject (contains, case-insensitive) |
| `sort` | `dueDate`, `priority` | Sort field |
| `direction` | `asc`, `desc` | Sort direction (default: `asc`) |

**Response 200:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "title": "Math homework",
    "description": "Exercises 3.1 to 3.5",
    "subject": "Mathematics",
    "priority": "HIGH",
    "status": "TODO",
    "type": "HOMEWORK",
    "dueDate": "2026-05-12",
    "estimatedDurationMinutes": 45,
    "createdAt": "2026-05-10T10:00:00Z",
    "updatedAt": "2026-05-10T10:00:00Z"
  }
]
```

---

### POST /api/v1/tasks
Create a new task.

**Request:**
```json
{
  "title": "Math homework",
  "description": "Exercises 3.1 to 3.5",
  "subject": "Mathematics",
  "priority": "HIGH",
  "type": "HOMEWORK",
  "dueDate": "2026-05-12",
  "estimatedDurationMinutes": 45
}
```

**Response 201:** — same as task object above

**Errors:** `400` validation error (e.g., past due date, blank title)

---

### GET /api/v1/tasks/{id}
Get a single task by ID.

**Response 200:** — task object

**Errors:** `404` not found or not owned by user

---

### PUT /api/v1/tasks/{id}
Update a task (full update).

**Request:** same shape as POST

**Response 200:** — updated task object

---

### DELETE /api/v1/tasks/{id}
Delete a task.

**Response 204:** no content

---

### PATCH /api/v1/tasks/{id}/status
Update task status only.

**Request:**
```json
{
  "status": "IN_PROGRESS"
}
```

**Response 200:** — updated task object

---

## Domain Enums

### Priority
| Value | Meaning | UI Color |
|-------|---------|----------|
| `HIGH` | Urgent | Red |
| `MEDIUM` | Normal | Orange |
| `LOW` | Low priority | Green |

### TaskType
| Value | Meaning |
|-------|---------|
| `HOMEWORK` | Regular homework |
| `EXAM` | Test or exam preparation |
| `PROJECT` | Long-term project |
| `PERSONAL` | Personal task |
| `HEALTH` | Health-related |

### TaskStatus
| Value | Meaning |
|-------|---------|
| `TODO` | Not started |
| `IN_PROGRESS` | Currently working on it |
| `DONE` | Completed |

---

## Error Format (RFC 7807 Problem Details)

```json
{
  "type": "https://taskly.app/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Due date must be in the future",
  "instance": "/api/v1/tasks"
}
```
