# Java Web Template

A production-ready Spring Boot web application template with dual authentication support (JWT + Session), Redis caching, MyBatis-Plus ORM, and comprehensive API documentation.

## Features

- **Build**: Maven
- **Java**: JDK 21
- **Framework**: Spring Boot 3.5.8
- **Database**: MyBatis-Plus + MySQL
- **Authentication**: 
  - JWT for `/api/**` endpoints (stateless)
  - Session for `/admin/**` endpoints (stateful, Redis-backed)
- **Cache/Session**: Redis (spring-data-redis + spring-session-data-redis)
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Health Check**: Spring Boot Actuator

## Architecture

### Dual Authentication Strategy

- **JWT Authentication** (`/api/**`): Stateless authentication using JWT tokens
- **Session Authentication** (`/admin/**`): Stateful authentication with Redis-backed sessions
- **Public Endpoints**: `/health`, `/auth/**`, `/swagger-ui/**`, `/actuator/**`

### Key Components

- **Unified Response**: `ApiResponse<T>` with code/message/data structure
- **Error Handling**: 
  - `ErrorCode` enum for standardized error codes
  - `BizException` for business exceptions
  - `GlobalExceptionHandler` for centralized exception handling
- **MyBatis-Plus**: 
  - Pagination plugin
  - Auto-fill for createdAt/updatedAt fields
- **Redis Service**: Wrapper for common Redis operations
- **Security Handlers**: Custom authentication entry point and access denied handler

## Prerequisites

- JDK 21
- Maven 3.6+
- Docker & Docker Compose (for MySQL and Redis)

## Quick Start

### 1. Start Infrastructure

Start MySQL and Redis using Docker Compose:

```bash
docker-compose up -d
```

Wait for services to be healthy:

```bash
docker-compose ps
```

### 2. Build the Application

```bash
mvn clean package -DskipTests
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/java-web-template-1.0.0.jar
```

The application will start on http://localhost:8080

### 4. Access API Documentation

Open Swagger UI: http://localhost:8080/swagger-ui.html

## API Testing

### Test Users

The application includes two in-memory test users:

1. **API User** (for JWT authentication)
   - Username: `apiuser`
   - Password: `apipass`
   - Role: USER

2. **Admin User** (for Session authentication)
   - Username: `admin`
   - Password: `adminpass`
   - Role: ADMIN

### JWT Authentication Flow

#### 1. Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/auth/jwt/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apiuser",
    "password": "apipass"
  }'
```

Response:
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "apiuser"
  }
}
```

#### 2. Access Protected API Endpoint

Use the token from step 1:

```bash
curl -X GET http://localhost:8080/api/hello \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Response:
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "message": "Hello from API endpoint!",
    "user": "apiuser",
    "authorities": [{"authority": "ROLE_USER"}],
    "authenticationType": "JWT"
  }
}
```

### Session Authentication Flow

#### 1. Login with Session

```bash
curl -X POST http://localhost:8080/auth/session/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "admin",
    "password": "adminpass"
  }'
```

Response:
```json
{
  "code": 0,
  "message": "Login successful",
  "data": {
    "username": "admin",
    "sessionId": "xxx-xxx-xxx"
  }
}
```

The session cookie (JSESSIONID) is saved in `cookies.txt`.

#### 2. Access Protected Admin Endpoint

```bash
curl -X GET http://localhost:8080/admin/hello \
  -b cookies.txt
```

Response:
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "message": "Hello from Admin endpoint!",
    "user": "admin",
    "authorities": [{"authority": "ROLE_ADMIN"}],
    "authenticationType": "Session"
  }
}
```

#### 3. Logout

```bash
curl -X POST http://localhost:8080/auth/session/logout \
  -b cookies.txt
```

### Public Endpoints

#### Health Check

```bash
curl -X GET http://localhost:8080/health
```

Response:
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00",
    "service": "java-web-template"
  }
}
```

#### Actuator Health

```bash
curl -X GET http://localhost:8080/actuator/health
```

## Configuration

Key configuration in `src/main/resources/application.yml`:

### Database

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/web_template?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

### Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

### Session

```yaml
spring:
  session:
    store-type: redis
    timeout: 30m
```

### JWT

```yaml
jwt:
  secret: your-256-bit-secret-key-change-this-in-production-environment-make-it-long-enough
  expiration: 86400000  # 24 hours in milliseconds
```

**⚠️ Important**: Change the JWT secret in production!

## Project Structure

```
src/main/java/com/example/template/
├── JavaWebTemplateApplication.java     # Main application class
├── common/                             # Common utilities
│   ├── exception/                      # Exception handling
│   │   ├── BizException.java
│   │   └── GlobalExceptionHandler.java
│   └── response/                       # Response wrappers
│       ├── ApiResponse.java
│       └── ErrorCode.java
├── config/                             # Configuration classes
│   ├── MyBatisPlusConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/                         # REST controllers
│   ├── AdminHelloController.java
│   ├── ApiHelloController.java
│   ├── AuthController.java
│   └── HealthController.java
├── dto/                                # Data Transfer Objects
│   ├── request/
│   │   └── LoginRequest.java
│   └── response/
│       └── JwtResponse.java
├── security/                           # Security components
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   ├── handler/
│   │   ├── CustomAccessDeniedHandler.java
│   │   └── CustomAuthenticationEntryPoint.java
│   └── jwt/
│       └── JwtTokenUtil.java
└── service/                            # Business services
    └── RedisService.java
```

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -Pprod
```

### Stopping Infrastructure

```bash
docker-compose down
```

To remove volumes:

```bash
docker-compose down -v
```

## Error Codes

| Code | Description |
|------|-------------|
| 0 | Success |
| 1000 | System error |
| 1001 | Parameter error |
| 1002 | Validation error |
| 2001 | Unauthorized |
| 2002 | Invalid token |
| 2003 | Token expired |
| 2004 | Access forbidden |
| 2005 | Authentication failed |
| 3001 | User not found |
| 3003 | Invalid credentials |

## Security Notes

1. **Change JWT Secret**: Update `jwt.secret` in production with a strong, random key
2. **Use HTTPS**: Always use HTTPS in production
3. **Session Security**: Redis session store provides distributed session management
4. **Password Encoding**: BCrypt password encoder is used by default
5. **CORS**: Configure CORS settings for production use

## License

Apache License 2.0

## Support

For issues and questions, please open an issue on GitHub.