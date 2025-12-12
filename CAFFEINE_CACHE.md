# Caffeine Cache Integration

## Overview

This project integrates Caffeine, a high-performance in-memory caching library, to cache application configurations. Configurations are stored in a MySQL database and loaded into Caffeine cache on application startup.

## Features

- **Automatic Cache Loading**: On application startup, all configurations are loaded from the database into Caffeine cache
- **Lazy Loading**: If a configuration is not in cache, it's automatically loaded from the database
- **CRUD Operations**: Full support for Create, Read, Update, Delete operations on configurations
- **Cache Management**: APIs to refresh and clear cache
- **Cache Statistics**: Monitor cache performance with hit/miss statistics

## Database Schema

The `config` table stores all application configurations:

```sql
CREATE TABLE `config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `config_key` VARCHAR(100) NOT NULL,
    `config_value` VARCHAR(500) NOT NULL,
    `description` VARCHAR(200) DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT(1) DEFAULT 0,
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## Cache Configuration

Caffeine cache is configured with the following settings (see `CaffeineConfig.java`):

- **Maximum Size**: 1000 entries
- **Expire After Write**: 24 hours
- **Expire After Access**: 12 hours
- **Statistics**: Enabled for monitoring

## API Endpoints

All configuration management endpoints are under `/api/config` and require JWT authentication.

### Get All Configurations
```bash
GET /api/config/all
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success",
  "data": {
    "system.name": "Java Web Template",
    "system.version": "1.0.0",
    "cache.enabled": "true"
  }
}
```

### Get Specific Configuration
```bash
GET /api/config/{key}
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success",
  "data": "Java Web Template"
}
```

### Update Configuration
```bash
PUT /api/config?key={key}&value={value}&description={description}
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success"
}
```

### Delete Configuration
```bash
DELETE /api/config/{key}
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success"
}
```

### Refresh Cache
Reload all configurations from database into cache:
```bash
POST /api/config/refresh
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success"
}
```

### Clear Cache
Remove all entries from cache:
```bash
POST /api/config/clear
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success"
}
```

### Get Cache Statistics
```bash
GET /api/config/stats
Authorization: Bearer {jwt_token}

Response:
{
  "code": 0,
  "message": "Success",
  "data": {
    "cachedConfigCount": 4,
    "cacheStats": "CacheStats{hitCount=10, missCount=2, loadSuccessCount=2, ...}"
  }
}
```

## Usage Example

### 1. Login to get JWT token
```bash
curl -X POST http://localhost:8080/auth/jwt/login \
  -H "Content-Type: application/json" \
  -d '{"username": "apiuser", "password": "apipass"}'
```

### 2. Get configuration value
```bash
curl -X GET http://localhost:8080/api/config/system.name \
  -H "Authorization: Bearer {your_jwt_token}"
```

### 3. Add/Update configuration
```bash
curl -X PUT "http://localhost:8080/api/config?key=app.timeout&value=30&description=Timeout%20in%20seconds" \
  -H "Authorization: Bearer {your_jwt_token}"
```

## Components

### Core Classes

- **Config.java**: Entity class representing configuration table
- **ConfigMapper.java**: MyBatis-Plus mapper for database operations
- **ConfigService.java**: Service for database CRUD operations
- **ConfigCacheService.java**: Service managing Caffeine cache
- **CaffeineConfig.java**: Caffeine cache configuration
- **ApplicationStartupListener.java**: Loads configs into cache on startup
- **ConfigController.java**: REST API controller for config management

## Cache Behavior

1. **On Startup**: All configurations are loaded from database into cache
2. **Cache Hit**: Configuration exists in cache, returned immediately
3. **Cache Miss**: Configuration not in cache, loaded from database and cached
4. **Update**: Configuration updated in both database and cache
5. **Delete**: Configuration removed from both database and cache
6. **Refresh**: All cache entries invalidated and reloaded from database

## Performance

Caffeine provides high-performance caching with:
- Near-optimal hit rate with W-TinyLFU eviction policy
- Low memory overhead
- Thread-safe operations
- Automatic eviction based on size and time

## Initial Sample Data

The database is initialized with sample configurations:

| Key | Value | Description |
|-----|-------|-------------|
| system.name | Java Web Template | System name |
| system.version | 1.0.0 | System version |
| cache.enabled | true | Whether cache is enabled |
| max.upload.size | 10485760 | Maximum upload file size in bytes |

## Testing

Run the application and use the API endpoints to test:

1. Start infrastructure: `docker compose up -d`
2. Create config table: Run SQL script from `src/main/resources/db/migration/V1__create_config_table.sql`
3. Build: `mvn clean package`
4. Run: `mvn spring-boot:run`
5. Test with curl or Swagger UI at `http://localhost:8080/swagger-ui.html`
