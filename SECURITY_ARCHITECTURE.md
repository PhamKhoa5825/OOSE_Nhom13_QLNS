# Security Architecture Documentation

## Overview

This document provides a comprehensive guide to the Security Module for the QLNS (Quản Lý Nhân Sự) Employee Management System. The system implements stateless JWT-based authentication with Role-Based Access Control (RBAC) using Spring Security.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Client Application                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              HTTP Request (with JWT Token)              │
│         Authorization: Bearer <jwt_token>               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│          JwtAuthenticationFilter                         │
│  - Extract JWT from Authorization header                │
│  - Validate token using JwtService                      │
│  - Set authentication in SecurityContext                │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│          SecurityConfig                                  │
│  - Check endpoint authorization rules                   │
│  - Verify user role against endpoint requirements       │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│          Business Logic (Controllers, Services)         │
│  - Process request with authenticated user context      │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              HTTP Response                               │
└─────────────────────────────────────────────────────────┘
```

---

## Package Structure

```
src/main/java/com/example/qlns/
├── Security/                          # Core security components
│   ├── JwtService.java                # JWT token generation and validation
│   ├── UserDetailsImpl.java            # Custom UserDetails implementation
│   ├── CustomUserDetailsService.java  # Load user from database
│   ├── JwtAuthenticationFilter.java   # JWT authentication filter
│   ├── Config/
│   │   └── SecurityConfig.java        # Spring Security configuration
│   └── Auth/
│       └── AuthService.java           # Authentication business logic
├── Controller/
│   └── AuthController.java            # Login and registration endpoints
├── DTO/
│   ├── Request/
│   │   ├── AuthenticationRequest.java    # Login request
│   │   └── UserRegistrationRequest.java  # Registration request
│   └── Response/
│       └── AuthenticationResponse.java   # Auth response with token
├── Entity/
│   └── User.java                      # User entity (existing)
├── Enum/
│   └── Role.java                      # User roles (EMPLOYEE, MANAGER, ADMIN)
└── Repository/
    └── UserRepository.java            # User database repository
```

---

## Core Components

### 1. JwtService (Security/JwtService.java)

**Purpose:** Generate, validate, and extract information from JWT tokens

**Key Methods:**
- `generateToken(UserDetails)` - Create JWT token from user details
- `extractUsername(String token)` - Get username from token
- `validateToken(String token, UserDetails)` - Validate token signature and expiration
- `extractExpiration(String token)` - Get token expiration time
- `extractClaim(String token, ClaimsResolver)` - Generic claim extraction

**JWT Payload:**
```json
{
  "role": "EMPLOYEE",
  "userId": 1,
  "sub": "username",
  "iat": 1705000000,
  "exp": 1705086400
}
```

### 2. UserDetailsImpl (Security/UserDetailsImpl.java)

**Purpose:** Custom implementation of Spring Security's UserDetails interface

**Features:**
- Stores user information (id, username, password, role)
- Implements UserDetails interface for Spring Security
- Converts Role enum to Spring Security authorities (ROLE_EMPLOYEE, ROLE_MANAGER, ROLE_ADMIN)

### 3. CustomUserDetailsService (Security/CustomUserDetailsService.java)

**Purpose:** Load user information from database and convert to UserDetails

**Methods:**
- `loadUserByUsername(String)` - Load user by username
- `loadUserByEmail(String)` - Load user by email

### 4. JwtAuthenticationFilter (Security/JwtAuthenticationFilter.java)

**Purpose:** Intercept requests, validate JWT tokens, and set authentication context

**Process:**
1. Extract JWT from Authorization header (format: `Bearer <token>`)
2. Extract username from token
3. Load user details from database
4. Validate token
5. Set authentication in SecurityContext

### 5. SecurityConfig (Security/Config/SecurityConfig.java)

**Purpose:** Configure Spring Security with JWT authentication and RBAC

**Configuration:**
- **CSRF:** Disabled (stateless API)
- **Session:** STATELESS (no server-side sessions)
- **Password Encoder:** BCryptPasswordEncoder
- **Authentication Provider:** DaoAuthenticationProvider
- **Authorization Rules:** Role-based endpoint protection

### 6. AuthService (Security/Auth/AuthService.java)

**Purpose:** Handle authentication business logic

**Methods:**
- `login(AuthenticationRequest)` - Authenticate user and generate token
- `register(UserRegistrationRequest)` - Register new user with BCrypt-hashed password
- `validateToken(String)` - Validate JWT token

### 7. AuthController (Controller/AuthController.java)

**Purpose:** Provide authentication endpoints

**Endpoints:**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/validate` - Validate token
- `POST /api/auth/refresh` - Refresh token

---

## Authentication Flow

### Login Flow

```
1. Client POST /api/auth/login
   {
     "username": "john.doe",
     "password": "password123"
   }

2. AuthController.login()
   ├─ Validate request
   └─ Call AuthService.login()

3. AuthService.login()
   ├─ AuthenticationManager.authenticate()
   │  ├─ CustomUserDetailsService.loadUserByUsername()
   │  ├─ Compare passwords using BCryptPasswordEncoder
   │  └─ Return UserDetailsImpl
   ├─ JwtService.generateToken()
   ├─ Fetch User entity from database
   └─ Return AuthenticationResponse

4. Client receives response:
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "userId": 1,
     "username": "john.doe",
     "email": "john@example.com",
     "role": "EMPLOYEE",
     "tokenType": "Bearer"
   }

5. Client stores token and includes in future requests:
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Request Authentication Flow

```
1. Client sends request with token:
   GET /api/employee/profile
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

2. JwtAuthenticationFilter.doFilterInternal()
   ├─ Extract JWT from header
   ├─ Extract username from JWT
   ├─ Load user from database
   ├─ Validate token
   └─ Set authentication in SecurityContext

3. SecurityConfig authorization check
   ├─ Check endpoint rule (/api/employee/** → EMPLOYEE, MANAGER, ADMIN)
   ├─ Verify user role
   ├─ If authorized → proceed to handler
   └─ If not authorized → return 403 Forbidden

4. Controller method executes with authenticated user context
```

---

## Registration Flow

```
1. Client POST /api/auth/register
   {
     "username": "jane.smith",
     "email": "jane@example.com",
     "password": "password456",
     "role": "EMPLOYEE"
   }

2. AuthController.register()
   ├─ Validate request
   └─ Call AuthService.register()

3. AuthService.register()
   ├─ Check username uniqueness
   ├─ Check email uniqueness
   ├─ Hash password using BCryptPasswordEncoder
   ├─ Create User entity
   ├─ Save to database
   ├─ Generate JWT token
   └─ Return AuthenticationResponse

4. Client receives response with token (auto-login)
```

---

## Role-Based Access Control (RBAC)

### Role Hierarchy

```
┌─────────────────┐
│     ADMIN       │
│  (Super Admin)  │
└────────┬────────┘
         │ includes all
         ▼
┌─────────────────┐
│    MANAGER      │
│   (Department   │
│    Manager)     │
└────────┬────────┘
         │ includes all
         ▼
┌─────────────────┐
│    EMPLOYEE     │
│  (Base User)    │
└─────────────────┘
```

### Endpoint Access Rules

**Public Endpoints** (No Authentication Required)
```
/api/auth/**                    - ALL (login, register, validate)
/api/public/**                  - ALL
```

**Employee Endpoints** (EMPLOYEE, MANAGER, ADMIN)
```
/api/employee/**                - EMPLOYEE, MANAGER, ADMIN
/api/attendance/check-in        - EMPLOYEE, MANAGER, ADMIN
/api/attendance/check-out       - EMPLOYEE, MANAGER, ADMIN
/api/chat/**                    - EMPLOYEE, MANAGER, ADMIN
/api/task/my-tasks             - EMPLOYEE, MANAGER, ADMIN
/api/notification/**            - EMPLOYEE, MANAGER, ADMIN
```

**Manager Endpoints** (MANAGER, ADMIN)
```
/api/manager/**                 - MANAGER, ADMIN
/api/department/**              - MANAGER, ADMIN
/api/task/** (POST, PUT)        - MANAGER, ADMIN
/api/leave-request/approve      - MANAGER, ADMIN
/api/leave-request/reject       - MANAGER, ADMIN
```

**Admin Endpoints** (ADMIN only)
```
/api/admin/**                   - ADMIN only
/api/employee/manage/**         - ADMIN only
/api/company-settings/**        - ADMIN only
```

---

## Key Security Features

### 1. BCrypt Password Hashing
- Passwords are hashed using BCryptPasswordEncoder before storing
- Never store plain-text passwords
- Example:
  ```java
  password = passwordEncoder.encode("plainPassword")
  // Result: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvP32m
  ```

### 2. JWT Token Validation
- Tokens are cryptographically signed using HS256 algorithm
- Signature verified on every request
- Tokens expire after 24 hours (configurable)
- Cannot be forged without secret key

### 3. Stateless Authentication
- No server-side session storage
- Reduces server memory usage
- Enables horizontal scaling
- Client responsible for token storage

### 4. CSRF Protection Disabled
- Disabled because using stateless API with JWT
- CSRF not applicable to token-based authentication

### 5. Secure Default Configuration
- Session creation policy set to STATELESS
- All endpoints require authentication by default
- Explicit permitAll() for public endpoints only

---

## Configuration Properties

**File:** `src/main/resources/application.properties`

```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly12345678901234567890
jwt.expiration=86400000  # 24 hours in milliseconds

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/qlns
spring.datasource.username=root
spring.datasource.password=020105

# Server
server.port=8081

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.com.example.qlns=INFO
logging.level.org.springframework.security=DEBUG
```

### Important Production Changes

```properties
# Production: Use environment variables or secure vault
jwt.secret=${JWT_SECRET}  # From environment variable

# Production: Longer expiration or refresh tokens
jwt.expiration=3600000    # 1 hour instead of 24 hours

# Production: Restrict CORS
cors.allowed-origins=https://yourdomain.com

# Production: Disable SQL logging
spring.jpa.show-sql=false
logging.level.org.springframework=WARN
```

---

## Usage Examples

### 1. Register User

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "SecurePassword123",
  "role": "EMPLOYEE"
}

Response (201 Created):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE",
  "tokenType": "Bearer"
}
```

### 2. Login User

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "SecurePassword123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE",
  "tokenType": "Bearer"
}
```

### 3. Access Protected Endpoint

```bash
GET /api/employee/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response (200 OK):
{
  "id": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE"
}
```

### 4. Unauthorized Access

```bash
GET /api/admin/users
Authorization: Bearer <employee_token>

Response (403 Forbidden):
{
  "error": "Forbidden",
  "message": "Access is denied"
}
```

### 5. Invalid Token

```bash
GET /api/employee/profile
Authorization: Bearer invalid.token.here

Response (401 Unauthorized):
{
  "error": "Unauthorized",
  "message": "Invalid authentication token"
}
```

---

## Exception Handling

The security module works with existing exception handlers:

### Custom Exceptions Used

1. **DuplicateException** - Username or email already exists during registration
2. **ResourceNotFoundException** - User not found in database
3. **UnauthorizedException** - Invalid credentials during login

### Error Response Example

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

---

## Testing the Security Module

### Prerequisites
- Database running and configured
- Application started on port 8081
- Postman or curl for testing

### Test Scenarios

**1. Test Registration**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

**2. Test Login**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**3. Test Protected Endpoint**
```bash
# Replace TOKEN with actual token from login response
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer TOKEN"
```

**4. Test Role-Based Access**
```bash
# Try to access admin endpoint with employee token
curl -X GET http://localhost:8081/api/admin/users \
  -H "Authorization: Bearer EMPLOYEE_TOKEN"
# Expected: 403 Forbidden
```

---

## Security Best Practices

### Development

1. ✅ Use BCryptPasswordEncoder for password hashing
2. ✅ Validate all input on both client and server
3. ✅ Use HTTPS in production (not HTTP)
4. ✅ Keep JWT secret secure
5. ✅ Implement request validation with @Valid annotations
6. ✅ Use role-based authorization on endpoints

### Production Deployment

1. 🔒 **Change JWT Secret**
   - Use strong, random, 32+ character string
   - Store in environment variables, not in code

2. 🔒 **Use HTTPS Only**
   - Always transmit tokens over HTTPS
   - Never use HTTP in production

3. 🔒 **Token Expiration**
   - Set reasonable expiration (e.g., 1 hour)
   - Implement refresh token mechanism

4. 🔒 **CORS Configuration**
   - Restrict to trusted domains only
   - Remove * wildcards in production

5. 🔒 **Rate Limiting**
   - Implement rate limiting on auth endpoints
   - Prevent brute force attacks

6. 🔒 **Logging & Monitoring**
   - Log authentication failures
   - Monitor suspicious activities
   - Set up alerts for security events

7. 🔒 **Database Security**
   - Use strong passwords for database
   - Enable SSL for database connections
   - Implement regular backups

8. 🔒 **API Security Headers**
   - Set security headers (X-Frame-Options, X-Content-Type-Options)
   - Implement Content Security Policy (CSP)

---

## Common Issues & Troubleshooting

### Issue 1: "User not found with username"
**Cause:** User doesn't exist in database or wrong username
**Solution:** Register user first or check spelling

### Issue 2: "Invalid username or password"
**Cause:** Wrong password entered
**Solution:** Verify password is correct

### Issue 3: "Unauthorized" on protected endpoint
**Cause:** Missing or invalid JWT token
**Solution:** Include valid token in Authorization header

### Issue 4: 403 Forbidden on endpoint
**Cause:** User doesn't have required role
**Solution:** Check endpoint requirements and user role

### Issue 5: Token always expired
**Cause:** Server time out of sync or very short expiration
**Solution:** Check server time and jwt.expiration property

---

## Future Enhancements

1. **Refresh Token Mechanism** - Implement refresh tokens for better security
2. **Token Revocation** - Implement token blacklist for logout
3. **Two-Factor Authentication** - Add 2FA support
4. **Rate Limiting** - Add rate limiting to prevent brute force
5. **Audit Logging** - Log all authentication events
6. **OAuth2 Integration** - Support social login
7. **Session Management** - Track active sessions
8. **Password Reset** - Implement secure password reset flow

---

## References

- Spring Security Documentation: https://spring.io/projects/spring-security
- JWT (JSON Web Tokens): https://jwt.io/
- JJWT Library: https://github.com/jwtk/jjwt
- BCrypt: https://en.wikipedia.org/wiki/Bcrypt
- OWASP Authentication Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html

---

**Version:** 1.0  
**Last Updated:** January 2024  
**Author:** Security Architecture Design Team

