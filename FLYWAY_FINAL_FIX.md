# Flyway Final Fix - The Real Solution

## The Real Problem

Flyway was not running because:

1. **Custom DataSource Bean**: `JpaConfiguration.java` defines a custom `@Bean public DataSource dataSource()`
2. **Bean Override**: This custom bean **overrides** Spring Boot's auto-configured datasource
3. **Flyway Disconnected**: Flyway couldn't use the `spring.datasource.*` configuration because the custom bean took precedence
4. **No Connection**: Flyway had no datasource to connect to, so it never ran

## The Solution

### Mark the Custom DataSource as Primary

Added the `@Primary` annotation to the custom datasource bean:

```java
@Bean
@Primary                    // Makes this the primary datasource (used by Flyway and JPA)
public DataSource dataSource() {
    // ... existing code
}
```

**What this annotation does:**
- `@Primary` - Makes this the default datasource when multiple beans exist
- Flyway will automatically use the primary datasource bean
- JPA/Hibernate will also use this same datasource

### Removed Redundant Configuration

Removed `spring.datasource.*` from `application.yml` and `application-prod.yml` since Flyway now uses the custom datasource bean.

## Files Changed

### 1. `src/main/java/ind/shubhamn/precisrest/dao/config/JpaConfiguration.java`

**Added import:**
```java
import org.springframework.context.annotation.Primary;
```

**Updated dataSource() method:**
```java
@Bean
@Primary
public DataSource dataSource() {
    // ... existing code
}
```

### 2. `src/main/resources/application.yml`

**Removed:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/precis?TimeZone=UTC
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

**Kept:**
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
```

### 3. `src/main/resources/application-prod.yml`

Same changes as `application.yml`.

## How to Test

### Step 1: Clean Everything

```bash
# Stop and remove all containers and volumes
docker-compose down -v
```

### Step 2: Rebuild and Start

```bash
# Rebuild and start all services
docker-compose up --build
```

### Step 3: Watch the Logs

```bash
# In another terminal, watch the app logs
docker logs -f precis-app
```

Look for these Flyway messages:
```
Flyway Community Edition by Redgate
Database: jdbc:postgresql://postgres:5432/precis (PostgreSQL 17.0)
Successfully validated 1 migration (execution time 00:00.015s)
Creating Schema History table "public"."flyway_schema_history" ...
Current version of schema "public": << Empty Schema >>
Migrating schema "public" to version "1 - Initial schema"
Successfully applied 1 migration to schema "public" (execution time 00:00.123s)
```

### Step 4: Verify Tables Created

```bash
# Run the verification script
.\verify-flyway.bat
```

Or manually:
```bash
# Connect to PostgreSQL
docker exec -it precis-postgres psql -U postgres -d precis

# List tables
\dt

# Should show:
# public | flyway_schema_history | table | postgres
# public | url_shorten           | table | postgres

# Check Flyway history
SELECT * FROM flyway_schema_history;

# Exit
\q
```

## Expected Results

### Tables Created

1. **flyway_schema_history**
   - Tracks all migrations
   - Shows version, description, execution time, etc.

2. **url_shorten**
   - Your application table
   - Columns: short_url, long_url, created_at, expires_at
   - Indexes: idx_long_url, idx_created_at, idx_expires_at

### Application Logs

You should see:
```
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Flyway Community Edition by Redgate
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Database: jdbc:postgresql://postgres:5432/precis (PostgreSQL 17.0)
2026-01-13 12:45:00 [main] DEBUG org.flywaydb.core.internal.scanner.Scanner - Scanning for resources in 'classpath:db/migration'
2026-01-13 12:45:00 [main] DEBUG org.flywaydb.core.internal.scanner.Scanner - Found resource: db/migration/V1__Initial_schema.sql
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Successfully validated 1 migration
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Creating Schema History table "public"."flyway_schema_history"
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Migrating schema "public" to version "1 - Initial schema"
2026-01-13 12:45:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Successfully applied 1 migration to schema "public"
```

## Why This Works

### Before (Broken)
```
Spring Boot starts
  ↓
Custom DataSource bean created (from JpaConfiguration)
  ↓
Spring Boot's auto-configured datasource is NOT created
  ↓
Flyway looks for a datasource
  ↓
Flyway can't find spring.datasource.* (doesn't exist)
  ↓
Flyway can't find the custom bean (not marked for Flyway)
  ↓
Flyway SKIPS execution ❌
```

### After (Fixed)
```
Spring Boot starts
  ↓
Custom DataSource bean created with @Primary and @FlywayDataSource
  ↓
Flyway looks for a datasource
  ↓
Flyway finds the custom bean (marked with @FlywayDataSource)
  ↓
Flyway connects to database
  ↓
Flyway runs migrations ✅
  ↓
Tables created ✅
```

## Troubleshooting

### Still Not Working?

1. **Check for compilation errors:**
   ```bash
   docker-compose build app
   ```

2. **Check application logs for Flyway:**
   ```bash
   docker logs precis-app | findstr -i flyway
   ```

3. **Check for datasource errors:**
   ```bash
   docker logs precis-app | findstr -i datasource
   ```

4. **Verify migration file exists in container:**
   ```bash
   docker exec -it precis-app ls -la /app/BOOT-INF/classes/db/migration/
   ```

## Summary

✅ **Added** `@Primary` annotation to custom datasource bean
✅ **Removed** redundant `spring.datasource.*` configuration
✅ **Kept** `spring.flyway.*` configuration
✅ **Kept** custom `database.*` configuration

**Now Flyway will automatically use your custom @Primary datasource and run migrations!** 🚀

