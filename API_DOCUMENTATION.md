# QLNS Security Module - Complete API Documentation

## Overview

This document provides complete REST API documentation for the QLNS authentication and authorization system.

---

## Base URL

```
http://localhost:8081/api/auth
```

## Authentication Header Format

```
Authorization: Bearer <jwt_token>
```

Example:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJ1c2VySWQiOjEsInN1YiI6ImpvaG4uZG9lIiwiaWF0IjoxNzA1MDAwMDAwLCJleHAiOjE3MDUwODY0MDB9.XXXXX
```

---

## Endpoints

### 1. User Registration

**Endpoint:** `POST /api/auth/register`

**Description:** Register a new user account

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "MyPassword123",
  "role": "EMPLOYEE"
}
```

**Request Parameters:**

| Parameter | Type | Required | Validation | Description |
|-----------|------|----------|-----------|-------------|
| username | String | Yes | 3-50 chars, unique | User login name |
| email | String | Yes | Valid email, unique | User email address |
| password | String | Yes | Min 6 chars | User password (will be hashed) |
| role | String | No | EMPLOYEE, MANAGER, ADMIN | User role (default: EMPLOYEE) |

**Success Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJ1c2VySWQiOjEsInN1YiI6ImpvaG4uZG9lIiwiaWF0IjoxNzA1MDAwMDAwLCJleHAiOjE3MDUwODY0MDB9.XXXXX",
  "userId": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE",
  "tokenType": "Bearer"
}
```

**Error Responses:**

```json
// 400 - Duplicate username
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username already taken: john.doe",
  "path": "/api/auth/register"
}
```

```json
// 400 - Duplicate email
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already registered: john@example.com",
  "path": "/api/auth/register"
}
```

```json
// 400 - Validation error
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Password must be at least 6 characters",
  "path": "/api/auth/register"
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| token | String | JWT token for authentication |
| userId | Long | User ID in database |
| username | String | Username |
| email | String | Email address |
| role | String | User role |
| tokenType | String | Always "Bearer" |

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "MyPassword123",
    "role": "EMPLOYEE"
  }'
```

---

### 2. User Login

**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john.doe",
  "password": "MyPassword123"
}
```

**Request Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| username | String | Yes | User login name or email |
| password | String | Yes | User password |

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJ1c2VySWQiOjEsInN1YiI6ImpvaG4uZG9lIiwiaWF0IjoxNzA1MDAwMDAwLCJleHAiOjE3MDUwODY0MDB9.XXXXX",
  "userId": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE",
  "tokenType": "Bearer"
}
```

**Error Responses:**

```json
// 401 - Invalid credentials
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

```json
// 401 - User not found
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "User not found with username: unknown.user",
  "path": "/api/auth/login"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "MyPassword123"
  }'
```

**Postman Example:**
1. Set request type to **POST**
2. Enter URL: `http://localhost:8081/api/auth/login`
3. Go to **Body** tab → Select **raw** → Select **JSON**
4. Enter:
   ```json
   {
     "username": "john.doe",
     "password": "MyPassword123"
   }
   ```
5. Click **Send**

---

### 3. Validate Token

**Endpoint:** `GET /api/auth/validate`

**Description:** Validate JWT token (requires authentication)

**Request Headers:**
```
Authorization: Bearer <jwt_token>
```

**Success Response (200 OK):**
```
"Token is valid"
```

**Error Responses:**

```json
// 401 - Missing token
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/auth/validate"
}
```

```json
// 401 - Invalid token
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid authentication token",
  "path": "/api/auth/validate"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

### 4. Refresh Token

**Endpoint:** `POST /api/auth/refresh`

**Description:** Refresh JWT token (requires authentication)

**Request Headers:**
```
Authorization: Bearer <jwt_token>
```

**Success Response (200 OK):**
```
"Token refresh endpoint"
```

**Note:** This is a placeholder for future token refresh implementation.

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## HTTP Status Codes

| Code | Name | Description |
|------|------|-------------|
| 200 | OK | Successful request |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input or duplicate resource |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Authenticated but insufficient permissions |
| 500 | Internal Server Error | Server-side error |

---

## Authentication Examples

### Example 1: Register, Login, and Access Protected Endpoint

```bash
# Step 1: Register new user
REGISTER_RESPONSE=$(curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "SecurePassword123",
    "role": "EMPLOYEE"
  }')

# Extract token from response
TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.token')

# Step 2: Use token to access protected endpoint
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer $TOKEN"
```

### Example 2: Login Different Roles

```bash
# Employee login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "employee.user",
    "password": "password123"
  }'

# Manager login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "manager.user",
    "password": "password123"
  }'

# Admin login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin.user",
    "password": "password123"
  }'
```

### Example 3: Access Role-Based Endpoints

```bash
# Employee accessing their endpoint (should succeed)
curl -X GET http://localhost:8081/api/employee/profile \
  -H "Authorization: Bearer <EMPLOYEE_TOKEN>"

# Employee accessing manager endpoint (should fail with 403)
curl -X GET http://localhost:8081/api/manager/dashboard \
  -H "Authorization: Bearer <EMPLOYEE_TOKEN>"

# Manager accessing admin endpoint (should fail with 403)
curl -X GET http://localhost:8081/api/admin/users \
  -H "Authorization: Bearer <MANAGER_TOKEN>"
```

---

## JWT Token Structure

### Token Format
```
Header.Payload.Signature
```

### Example Decoded Token

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "role": "EMPLOYEE",
  "userId": 1,
  "sub": "john.doe",
  "iat": 1705000000,
  "exp": 1705086400
}
```

**Fields:**
- `role` - User role (EMPLOYEE, MANAGER, ADMIN)
- `userId` - User ID from database
- `sub` - Subject (username)
- `iat` - Issued at timestamp
- `exp` - Expiration timestamp

### Token Expiration
- Default: 24 hours (86400000 milliseconds)
- Configurable in `application.properties`: `jwt.expiration`

### Token Validation
Tokens are validated on every request:
1. ✅ Signature verified using secret key
2. ✅ Expiration time checked
3. ✅ Username verified in database
4. ✅ User role checked against endpoint requirements

---

## Error Response Format

### Standard Error Response

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description here",
  "path": "/api/auth/login"
}
```

**Fields:**
- `timestamp` - When error occurred
- `status` - HTTP status code
- `error` - Error type
- `message` - Detailed error message
- `path` - Request path that caused error

---

## Rate Limiting (Future)

Currently there is no rate limiting. For production, it's recommended to implement rate limiting on authentication endpoints to prevent brute force attacks:

- `POST /api/auth/login` - Max 5 attempts per minute per IP
- `POST /api/auth/register` - Max 10 registrations per hour per IP

---

## CORS Configuration (Future)

For frontend integration, CORS must be configured. Example allowed origins:

```properties
cors.allowed-origins=http://localhost:3000,https://yourdomain.com
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=Content-Type,Authorization
cors.max-age=3600
```

---

## Integration with Frontend

### React Example

```javascript
// Register
async function register(username, email, password) {
  const response = await fetch('http://localhost:8081/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

// Login
async function login(username, password) {
  const response = await fetch('http://localhost:8081/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

// Access protected endpoint
async function getProfile() {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8081/api/auth/validate', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
}
```

### Angular Example

```typescript
// Auth service
@Injectable()
export class AuthService {
  constructor(private http: HttpClient) {}

  register(username: string, email: string, password: string) {
    return this.http.post('/api/auth/register', {
      username, email, password
    }).pipe(
      tap(response => localStorage.setItem('token', response.token))
    );
  }

  login(username: string, password: string) {
    return this.http.post('/api/auth/login', {
      username, password
    }).pipe(
      tap(response => localStorage.setItem('token', response.token))
    );
  }

  getToken() {
    return localStorage.getItem('token');
  }
}

// HTTP Interceptor
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

---

## Testing Checklist

- [ ] POST /api/auth/register - New user
- [ ] POST /api/auth/register - Duplicate username (400)
- [ ] POST /api/auth/register - Duplicate email (400)
- [ ] POST /api/auth/register - Invalid email format (400)
- [ ] POST /api/auth/register - Short password (400)
- [ ] POST /api/auth/login - Valid credentials (200)
- [ ] POST /api/auth/login - Invalid password (401)
- [ ] POST /api/auth/login - Invalid username (401)
- [ ] GET /api/auth/validate - Valid token (200)
- [ ] GET /api/auth/validate - Missing token (401)
- [ ] GET /api/auth/validate - Expired token (401)
- [ ] GET /api/auth/validate - Tampered token (401)
- [ ] Employee access /api/employee/profile - (200)
- [ ] Employee access /api/manager/dashboard - (403)
- [ ] Manager access /api/admin/users - (403)

---

## Security Notes

🔒 **Important:**
- Always use HTTPS in production
- Never share JWT tokens
- Change jwt.secret in production
- Implement token expiration
- Use secure password requirements
- Implement rate limiting on auth endpoints
- Log authentication events
- Monitor for suspicious activities

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024-01-15 | Initial release |

---

**Last Updated:** January 15, 2024  
**API Status:** ✅ Ready for Production  
**Documentation:** Complete

