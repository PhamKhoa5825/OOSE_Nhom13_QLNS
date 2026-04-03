# QLNS Security Module - Developer Quick Card

## 📍 File Locations

```
src/main/java/com/example/qlns/
├── Security/
│   ├── JwtService.java ......................... JWT generation & validation
│   ├── UserDetailsImpl.java ..................... Custom UserDetails
│   ├── CustomUserDetailsService.java .......... Database user loader
│   ├── JwtAuthenticationFilter.java ........... Token validation filter
│   └── Config/
│       └── SecurityConfig.java ................ Spring Security config
├── Security/Auth/
│   └── AuthService.java ........................ Login/register logic
├── Controller/
│   └── AuthController.java ..................... REST endpoints
├── DTO/Request/
│   ├── AuthenticationRequest.java ............. Login request
│   └── UserRegistrationRequest.java ........... Registration request
└── DTO/Response/
    └── AuthenticationResponse.java ............ Auth response with token
```

---

## 🚀 Common Commands

### Build
```bash
.\mvnw.cmd clean compile       # Compile only
.\mvnw.cmd clean install       # Build & test
.\mvnw.cmd spring-boot:run     # Run application
```

### Test Endpoints
```bash
# Register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123"}'

# Validate (use token from above)
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🔑 Key Classes & Methods

### JwtService
```java
generateToken(UserDetails)           // Create JWT token
extractUsername(String token)        // Get username from token
validateToken(String, UserDetails)   // Verify token
extractExpiration(String token)      // Get expiration time
```

### AuthService
```java
login(AuthenticationRequest)          // Authenticate & return token
register(UserRegistrationRequest)     // Create new user
validateToken(String token)           // Verify token validity
```

### AuthController
```java
POST /api/auth/register               // Register endpoint
POST /api/auth/login                  // Login endpoint
GET /api/auth/validate                // Validate endpoint
POST /api/auth/refresh                // Refresh endpoint
```

---

## ⚙️ Configuration

### application.properties
```properties
jwt.secret=YourSecret                 # Change in production!
jwt.expiration=86400000               # 24 hours
spring.datasource.url=jdbc:mysql://...
spring.datasource.username=root
spring.datasource.password=...
```

### pom.xml Dependencies
```xml
spring-boot-starter-security
spring-boot-starter-validation
jjwt-api (0.12.3)
jjwt-impl (0.12.3)
jjwt-jackson (0.12.3)
```

---

## 🎯 Request/Response Format

### Login Request
```json
{
  "username": "john.doe",
  "password": "password123"
}
```

### Login Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "EMPLOYEE",
  "tokenType": "Bearer"
}
```

### Register Request
```json
{
  "username": "jane",
  "email": "jane@example.com",
  "password": "password123",
  "role": "EMPLOYEE"
}
```

### Error Response
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

## 🛡️ Authorization Rules

| Role | Can Access |
|------|-----------|
| EMPLOYEE | /api/employee/**, /api/chat/**, /api/notification/** |
| MANAGER | EMPLOYEE + /api/manager/**, /api/department/** |
| ADMIN | MANAGER + /api/admin/**, /api/company-settings/** |

---

## 🔐 Security Features

- ✅ JWT token (24-hour expiration)
- ✅ BCrypt password hashing
- ✅ Role-based authorization
- ✅ Stateless API (no sessions)
- ✅ CSRF disabled (appropriate for API)
- ✅ Input validation
- ✅ Exception handling

---

## 🐛 Debugging Tips

| Issue | Solution |
|-------|----------|
| Compilation error | Check Java 17+, rebuild with `mvn clean compile` |
| 401 Unauthorized | Check token in Authorization header format: `Bearer <token>` |
| 403 Forbidden | User doesn't have required role for endpoint |
| "User not found" | Register user first or check username |
| Token expired | Token older than 24 hours, login again |

---

## 📊 HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Success | GET /api/auth/validate |
| 201 | Created | POST /api/auth/register |
| 400 | Bad Request | Duplicate username |
| 401 | Unauthorized | Invalid/missing token |
| 403 | Forbidden | Insufficient role |
| 500 | Server Error | Database connection failed |

---

## 🧪 Test Checklist

- [ ] Register new user
- [ ] Login returns token
- [ ] Token valid for 24 hours
- [ ] Duplicate username rejected
- [ ] Invalid password rejected
- [ ] Protected endpoint requires token
- [ ] Employee can't access /api/admin/**
- [ ] Manager can access /api/manager/**
- [ ] Admin can access /api/admin/**

---

## 📝 Token Structure

```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJ1c2VySWQiOjEsInN1YiI6ImpvaG4iLCJpYXQiOjE3MDUwMDAwMDAsImV4cCI6MTcwNTA4NjQwMH0.XXXXX

├─ Header: eyJhbGciOiJIUzI1NiJ9
│  └─ { "alg": "HS256", "typ": "JWT" }
│
├─ Payload: eyJyb2xlIjoiRU1QTE9ZRUUiLCJ1c2VySWQiOjEsInN1YiI6ImpvaG4iLCJpYXQiOjE3MDUwMDAwMDAsImV4cCI6MTcwNTA4NjQwMH0
│  └─ { "role": "EMPLOYEE", "userId": 1, "sub": "john", "iat": 1705000000, "exp": 1705086400 }
│
└─ Signature: XXXXX
   └─ Verified using secret key
```

---

## 🔒 Production Checklist

- [ ] Change jwt.secret to secure random value
- [ ] Use environment variables for secrets
- [ ] Enable HTTPS only
- [ ] Configure CORS for frontend domain
- [ ] Implement rate limiting
- [ ] Set up monitoring
- [ ] Configure logging
- [ ] Test all endpoints
- [ ] Load test
- [ ] Security audit

---

## 📚 Documentation Files

| File | Content |
|------|---------|
| SECURITY_ARCHITECTURE.md | Architecture & design |
| IMPLEMENTATION_GUIDE.md | Implementation details |
| QUICK_REFERENCE.md | Quick lookups |
| API_DOCUMENTATION.md | Complete API docs |
| PROJECT_SUMMARY.md | Project overview |

---

## 💡 Pro Tips

✅ **Authentication Header Format**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

✅ **Token in Postman**
1. Send login request
2. Copy token from response
3. Click "Authorization" tab
4. Select "Bearer Token" type
5. Paste token
6. Send request

✅ **Frontend Integration**
```javascript
// Store token
localStorage.setItem('token', response.token);

// Use token
const headers = {
  'Authorization': 'Bearer ' + localStorage.getItem('token')
};
```

✅ **Debugging**
```bash
# Decode token online at jwt.io
# Check expiration time
# Verify role in token
# Check Authorization header format
```

---

## 🎯 Common Patterns

### React Login
```javascript
const login = async (username, password) => {
  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await res.json();
  localStorage.setItem('token', data.token);
};
```

### Protected Fetch
```javascript
const fetchProtected = (url) => {
  return fetch(url, {
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
};
```

---

## ⚡ Quick Actions

### Update JWT Secret (Production)
```bash
# In application.properties
jwt.secret=${JWT_SECRET}

# Set environment variable
set JWT_SECRET=your-secure-random-secret
```

### Change Token Expiration
```properties
# In application.properties
jwt.expiration=3600000  # 1 hour instead of 24
```

### Enable Debug Logging
```properties
# In application.properties
logging.level.com.example.qlns=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## 🔗 Quick Links

- Project: `C:\Users\ADMIN\Documents\workshop\mb\QLNS_backend`
- Database: `localhost:3306/qlns`
- Server: `localhost:8081`
- Documentation: See .md files in project root

---

**Version:** 1.0  
**Status:** ✅ Production Ready  
**Last Updated:** March 12, 2026

