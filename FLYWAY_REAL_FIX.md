# Flyway Real Fix - Explicit Flyway Bean Configuration

## The REAL Problem

Flyway was **NEVER running** because:

1. **Custom DataSource Bean**: `JpaConfiguration.java` defines a custom `@Bean public DataSource dataSource()`
2. **Spring Boot Auto-Configuration Disabled**: When Spring Boot detects a custom DataSource bean, it **disables** Flyway auto-configuration
3. **No Flyway Bean**: Without auto-configuration, Flyway bean was never created
4. **No Migrations**: Flyway never ran, tables were never created

## Evidence from Logs

```
# NO Flyway logs at all - Flyway never initialized
2026-01-13 12:56:00 - Starting PrecisApplication
2026-01-13 12:56:03 - HHH10001005: Database info
2026-01-13 12:56:04 - Started PrecisApplication

# When trying to use the table:
ERROR: relation "precis.url_shorten" does not exist
```

**Notice**: No Flyway logs = Flyway never ran!

## The Solution

**Explicitly create a Flyway bean** that uses the custom DataSource.

### Changes Made

**File**: `src/main/java/ind/shubhamn/precisrest/dao/config/JpaConfiguration.java`

#### 1. Added Import

```java
import org.flywaydb.core.Flyway;
```

#### 2. Created Flyway Bean

```java
@Bean(initMethod = "migrate")
public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .schemas("precis")
            .defaultSchema("precis")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .load();
}
```

**Key points:**
- `@Bean(initMethod = "migrate")` - Automatically runs migrations when bean is created
- `.dataSource(dataSource)` - Uses our custom DataSource bean
- `.locations("classpath:db/migration")` - Where migration files are located
- `.schemas("precis")` - Schema to manage
- `.defaultSchema("precis")` - Default schema for operations
- `.baselineOnMigrate(true)` - Baseline existing schemas
- `.validateOnMigrate(true)` - Validate migrations before running

## How It Works Now

```
Spring Boot starts
  ↓
Custom DataSource bean created (@Primary)
  ↓
Flyway bean created (explicitly configured)
  ↓
Flyway.migrate() called automatically (initMethod)
  ↓
Flyway connects to database using custom DataSource
  ↓
Flyway reads configuration from application.yml
  ↓
Flyway creates precis schema
  ↓
Flyway runs V1__Initial_schema.sql
  ↓
Tables created ✅
  ↓
JPA/Hibernate initializes
  ↓
Application ready ✅
```

## Configuration

Flyway configuration is now **hardcoded in the Flyway bean** in `JpaConfiguration.java`:

- `locations`: `classpath:db/migration`
- `schemas`: `precis`
- `defaultSchema`: `precis`
- `baselineOnMigrate`: `true`
- `validateOnMigrate`: `true`

The `spring.flyway.*` properties in `application.yml` are no longer used (but can remain for documentation purposes).

## Testing

```bash
# Clean everything
docker-compose down -v

# Rebuild and start
docker-compose up --build

# Watch logs for Flyway
docker logs -f precis-app
```

## Expected Logs

You should now see:

```
2026-01-13 XX:XX:XX - Starting PrecisApplication
2026-01-13 XX:XX:XX - Flyway Community Edition by Redgate
2026-01-13 XX:XX:XX - Database: jdbc:postgresql://postgres:5432/precis (PostgreSQL 17.0)
2026-01-13 XX:XX:XX - Successfully validated 1 migration
2026-01-13 XX:XX:XX - Creating Schema History table "precis"."flyway_schema_history"
2026-01-13 XX:XX:XX - Current version of schema "precis": << Empty Schema >>
2026-01-13 XX:XX:XX - Migrating schema "precis" to version "1 - Initial schema"
2026-01-13 XX:XX:XX - Successfully applied 1 migration to schema "precis"
2026-01-13 XX:XX:XX - HHH10001005: Database info
2026-01-13 XX:XX:XX - Started PrecisApplication
```

## Verification

```bash
# Run verification script
.\verify-flyway.bat

# Or manually check
docker exec -it precis-postgres psql -U postgres -d precis -c "\dt precis.*"
```

Expected output:
```
           List of relations
 Schema |         Name          | Type  |  Owner
--------+-----------------------+-------+----------
 precis | flyway_schema_history | table | postgres
 precis | url_shorten           | table | postgres
```

## Why Previous Attempts Failed

1. **Adding `@Primary`** - Not enough, Flyway bean still not created
2. **Adding `spring.datasource.*`** - Ignored because custom DataSource bean exists
3. **Adding `@FlywayDataSource`** - Annotation doesn't exist in this Spring Boot version

## The Correct Solution

**Explicitly create the Flyway bean** and configure it to use the custom DataSource. This is the standard approach when you have custom DataSource configuration.

## Summary

✅ **Added** explicit Flyway bean in `JpaConfiguration`
✅ **Configured** Flyway to use custom DataSource
✅ **Hardcoded** Flyway configuration (locations, schemas, etc.)
✅ **Set** `initMethod = "migrate"` to run migrations automatically

**Flyway will now run on application startup!** 🚀

