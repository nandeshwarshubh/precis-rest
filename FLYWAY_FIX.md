# Flyway Fix - Making Migrations Work

## Problem
Flyway was not creating tables because it wasn't connected to the datasource properly.

## Root Cause
The application had a custom `database.*` configuration in `application.yml`, but Flyway requires the standard Spring Boot `spring.datasource.*` configuration to work automatically.

## What Was Fixed

### 1. Added Spring DataSource Configuration

**File**: `src/main/resources/application.yml`

Added:
```yaml
spring:
  # DataSource Configuration (required for Flyway)
  datasource:
    url: jdbc:postgresql://localhost:5432/precis?TimeZone=UTC
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

**File**: `src/main/resources/application-prod.yml`

Added:
```yaml
spring:
  # DataSource Configuration (required for Flyway)
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/precis}?TimeZone=UTC
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
```

### 2. Added Flyway Logging

Added `org.flywaydb: DEBUG` to logging configuration to see Flyway execution details.

### 3. Kept Custom Database Config

The custom `database.*` configuration is still used by `JpaConfiguration.java` for JPA/Hibernate, while Flyway uses the standard `spring.datasource.*` configuration.

## How to Verify Flyway is Working

### Option 1: Use the Verification Script

```bash
.\verify-flyway.bat
```

This will:
1. Check if PostgreSQL is running
2. List all tables in the database
3. Show Flyway schema history
4. Show the url_shorten table structure

### Option 2: Manual Verification

#### Step 1: Rebuild and Start the Application

```bash
# Stop everything
docker-compose down -v

# Rebuild and start
docker-compose up --build
```

#### Step 2: Check Application Logs

Look for Flyway messages in the logs:
```bash
docker logs precis-app | findstr -i flyway
```

You should see:
```
Flyway Community Edition by Redgate
Database: jdbc:postgresql://postgres:5432/precis (PostgreSQL 17.0)
Successfully validated 1 migration (execution time 00:00.015s)
Creating Schema History table "public"."flyway_schema_history" ...
Current version of schema "public": << Empty Schema >>
Migrating schema "public" to version "1 - Initial schema"
Successfully applied 1 migration to schema "public" (execution time 00:00.123s)
```

#### Step 3: Check Database Tables

```bash
# Connect to PostgreSQL
docker exec -it precis-postgres psql -U postgres -d precis

# List all tables
\dt

# Should show:
# public | flyway_schema_history | table | postgres
# public | url_shorten           | table | postgres

# Check Flyway history
SELECT * FROM flyway_schema_history;

# Check url_shorten table structure
\d url_shorten

# Exit
\q
```

## Expected Results

### Tables Created by Flyway

1. **flyway_schema_history** - Tracks migration history
   - Columns: installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success

2. **url_shorten** - Your application table
   - Columns: short_url (PK), long_url, created_at, expires_at
   - Indexes: idx_long_url, idx_created_at, idx_expires_at

### Flyway Logs

When the application starts, you should see:
```
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Database: jdbc:postgresql://postgres:5432/precis (PostgreSQL 17.0)
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Successfully validated 1 migration (execution time 00:00.015s)
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Creating Schema History table "public"."flyway_schema_history" ...
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Current version of schema "public": << Empty Schema >>
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Migrating schema "public" to version "1 - Initial schema"
2026-01-13 12:30:00 [main] INFO  org.flywaydb.core.FlywayExecutor - Successfully applied 1 migration to schema "public" (execution time 00:00.123s)
```

## Troubleshooting

### Flyway Still Not Running?

1. **Check if datasource is configured**:
   ```bash
   docker logs precis-app | findstr -i datasource
   ```

2. **Check for errors**:
   ```bash
   docker logs precis-app | findstr -i error
   ```

3. **Verify Flyway is enabled**:
   ```bash
   docker logs precis-app | findstr -i "flyway.enabled"
   ```

4. **Check migration files exist**:
   ```bash
   # In the container
   docker exec -it precis-app ls -la /app/BOOT-INF/classes/db/migration/
   ```

### Tables Already Exist?

If you ran the application before with Hibernate auto-DDL, the tables might already exist. Flyway will baseline them:

```bash
# Check Flyway history
docker exec -it precis-postgres psql -U postgres -d precis -c "SELECT * FROM flyway_schema_history;"
```

You should see a baseline entry.

### Clean Start

To start completely fresh:
```bash
# Remove everything
docker-compose down -v

# Rebuild
docker-compose up --build
```

## Summary

✅ **Fixed**: Added `spring.datasource.*` configuration for Flyway  
✅ **Fixed**: Added Flyway debug logging  
✅ **Kept**: Custom `database.*` configuration for JPA  
✅ **Created**: Verification script (`verify-flyway.bat`)  

Now Flyway should run automatically on application startup and create the database schema! 🚀

