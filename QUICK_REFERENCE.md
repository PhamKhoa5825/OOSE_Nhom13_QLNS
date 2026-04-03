# Security Module - Quick Reference Guide

## 🔐 Core Components at a Glance

| Component | Location | Purpose |
|-----------|----------|---------|
| **JwtService** | `Security/JwtService.java` | Generate & validate JWT tokens |
| **SecurityConfig** | `Security/Config/SecurityConfig.java` | Configure Spring Security |
| **AuthService** | `Security/Auth/AuthService.java` | Handle login/register logic |
| **AuthController** | `Controller/AuthController.java` | REST endpoints |
| **JwtAuthenticationFilter** | `Security/JwtAuthenticationFilter.java` | Token validation filter |
| **CustomUserDetailsService** | `Security/CustomUserDetailsService.java` | Load user from database |

---

## 🚀 Quick API Reference

### Register User
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

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "MyPassword123"
  }'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8081/api/employee/profile \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 🔑 JWT Token Format

```
Header.Payload.Signature

Example decoded payload:
{
  "role": "EMPLOYEE",
  "userId": 1,
  "sub": "john.doe",
  "iat": 1705000000,
  "exp": 1705086400
}
```

---

## 🛡️ Role-Based Access Control

| Endpoint Pattern | Required Role | Users |
|------------------|---------------|-------|
| `/api/auth/**` | NONE | All |
| `/api/employee/**` | EMPLOYEE+ | Employees, Managers, Admins |
| `/api/manager/**` | MANAGER+ | Managers, Admins |
| `/api/admin/**` | ADMIN | Admins only |

**Role Hierarchy:** `EMPLOYEE` → `MANAGER` → `ADMIN`

---

## 🔄 Authentication Flow

```
┌─────────────┐
│  Client     │
└──────┬──────┘
       │
       ▼ POST /api/auth/login
┌──────────────────────┐
│ AuthController       │
│ ├─ Validate input    │
│ └─ Call AuthService  │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ AuthService.login()  │
│ ├─ Authenticate user │
│ ├─ Generate JWT      │
│ └─ Return token      │
└──────┬───────────────┘
       │
       ▼ { token, userId, role, ... }
┌─────────────────────┐
│ Client stores token │
└──────┬──────────────┘
       │
       │ GET /api/employee/profile
       │ Authorization: Bearer <token>
       ▼
┌──────────────────────────────┐
│ JwtAuthenticationFilter      │
│ ├─ Extract token             │
│ ├─ Validate signature        │
│ ├─ Check expiration          │
│ └─ Set authentication        │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ SecurityConfig               │
│ ├─ Check endpoint rule       │
│ ├─ Verify user role         │
│ └─ Allow/Deny access        │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ Controller method executes    │
│ User context available        │
└──────────────────────────────┘
```

---

## 📝 Configuration Properties

```properties
# JWT Token
jwt.secret=YourSecretKeyHere                 # Change in production!
jwt.expiration=86400000                      # 24 hours (ms)

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/qlns
spring.datasource.username=root
spring.datasource.password=020105

# Server
server.port=8081
```

---

## ⚙️ Spring Security Configuration

**CSRF:** ❌ Disabled (stateless API)
**Sessions:** 🚫 STATELESS
**Password Encoder:** 🔒 BCryptPasswordEncoder
**Token Validation:** ✅ JWT signature + expiration
**Authorization:** ✅ Role-based (@PreAuthorize optional)

---

## 🔍 HTTP Status Codes

| Status | Scenario |
|--------|----------|
| 200 | Successful login / Protected endpoint accessed |
| 201 | Registration successful |
| 400 | Invalid input / Duplicate username or email |
| 401 | Missing or invalid token |
| 403 | Valid token but insufficient role |
| 500 | Server error |

---

## 🚨 Common Errors & Solutions

### ❌ "User not found with username"
**Solution:** Register user first or check spelling

### ❌ "Invalid username or password"
**Solution:** Verify credentials are correct

### ❌ "Could not set user authentication in security context"
**Solution:** Check token is valid and not expired

### ❌ "Unauthorized" (401)
**Solution:** Include valid token in Authorization header: `Bearer <token>`

### ❌ "Forbidden" (403)
**Solution:** User doesn't have required role. Check endpoint requirements.

### ❌ "Duplicate username"
**Solution:** Choose different username or login with existing account

---

## 🧪 Testing with cURL

### Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","role":"EMPLOYEE"}'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Validate Token (copy token from response)
```bash
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Test Role Authorization
```bash
# Employee accessing manager endpoint (should fail)
curl -X GET http://localhost:8081/api/manager/dashboard \
  -H "Authorization: Bearer <EMPLOYEE_TOKEN>"

# Should return 403 Forbidden
```

---

## 🔐 Security Best Practices

✅ **DO:**
- Use HTTPS in production
- Keep JWT secret secure (environment variable)
- Validate all input
- Hash passwords (BCrypt used)
- Log authentication events
- Expire tokens appropriately
- Use role-based authorization

❌ **DON'T:**
- Store JWT secret in code
- Use HTTP in production
- Trust client-side validation only
- Store passwords in plain text
- Share JWT secret
- Hardcode credentials

---

## 📦 Dependencies

```xml
<!-- Spring Security -->
spring-boot-starter-security

<!-- JWT -->
jjwt-api (0.12.3)
jjwt-impl (0.12.3)
jjwt-jackson (0.12.3)

<!-- Validation -->
spring-boot-starter-validation
```

---

## 🎯 Verification Checklist

- [ ] Application compiles without errors
- [ ] MySQL database is running
- [ ] User table has columns: id, username, email, password_hash, role
- [ ] Can register new user
- [ ] Can login with valid credentials
- [ ] Login returns JWT token
- [ ] Can access protected endpoint with token
- [ ] Cannot access protected endpoint without token
- [ ] Cannot access admin endpoint with employee role
- [ ] Token expires after 24 hours
- [ ] Password is hashed in database

---

## 🚀 Deployment Checklist

1. **Change JWT Secret**
   ```bash
   export JWT_SECRET="your-secure-random-secret-32-chars"
   ```

2. **Update Database Credentials**
   ```properties
   spring.datasource.password=${DB_PASSWORD}
   ```

3. **Set Production Logging**
   ```properties
   logging.level.org.springframework.security=WARN
   spring.jpa.show-sql=false
   ```

4. **Enable HTTPS**
   - Configure SSL certificate
   - Update allowed origins for CORS

5. **Reduce Token Expiration**
   ```properties
   jwt.expiration=3600000  # 1 hour instead of 24
   ```

6. **Enable Rate Limiting**
   - Add rate limiter on /api/auth/login
   - Prevent brute force attacks

---

## 📚 Additional Resources

- **SECURITY_ARCHITECTURE.md** - Detailed architecture documentation
- **IMPLEMENTATION_GUIDE.md** - Complete implementation details
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [JWT Introduction](https://jwt.io/)

---

**Version:** 1.0  
**Last Updated:** March 12, 2026  
**Status:** ✅ Production Ready

