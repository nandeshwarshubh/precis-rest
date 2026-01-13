# Schema Configuration - Using `precis` Schema

## Overview

All database objects (tables, indexes, etc.) are now created in the `precis` schema instead of the default `public` schema.

## Changes Made

### 1. Flyway Configuration

**Files**: `application.yml`, `application-prod.yml`

Added schema configuration:
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
    schemas: precis              # Schema to manage
    default-schema: precis       # Default schema for Flyway operations
```

**What this does:**
- `schemas: precis` - Tells Flyway to manage the `precis` schema
- `default-schema: precis` - Sets `precis` as the default schema for all Flyway operations

### 2. Migration SQL

**File**: `src/main/resources/db/migration/V1__Initial_schema.sql`

Updated to create and use the `precis` schema:
```sql
-- Create the precis schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS precis;

-- Create the URL_SHORTEN table in the precis schema
CREATE TABLE IF NOT EXISTS precis.url_shorten (
    short_url VARCHAR(8) PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

-- Create indexes in the precis schema
CREATE INDEX IF NOT EXISTS idx_long_url ON precis.url_shorten(long_url);
CREATE INDEX IF NOT EXISTS idx_created_at ON precis.url_shorten(created_at);
CREATE INDEX IF NOT EXISTS idx_expires_at ON precis.url_shorten(expires_at);
```

### 3. JPA Entity

**File**: `src/main/java/ind/shubhamn/precisrest/model/ShortenedUrl.java`

Updated `@Table` annotation to specify schema:
```java
@Entity
@Table(name = "url_shorten", schema = "precis", indexes = {
    @Index(name = "idx_long_url", columnList = "long_url"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class ShortenedUrl {
    // ... entity fields
}
```

**Changes:**
- Added `schema = "precis"` to `@Table` annotation
- Changed table name from `URL_SHORTEN` to `url_shorten` (lowercase, matches SQL)
- Added missing indexes for `created_at` and `expires_at`

### 4. DataSource Configuration

**File**: `src/main/java/ind/shubhamn/precisrest/dao/config/JpaConfiguration.java`

Updated datasource URL to set default schema:
```java
@Bean
@Primary
public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");

    // Add timezone and schema parameters
    String url = databaseConfig.getUrl();
    
    if (!url.contains("?")) {
        url += "?TimeZone=UTC&currentSchema=precis";
    } else {
        if (!url.contains("TimeZone")) {
            url += "&TimeZone=UTC";
        }
        if (!url.contains("currentSchema")) {
            url += "&currentSchema=precis";
        }
    }

    dataSource.setUrl(url);
    // ... rest of configuration
}
```

**What this does:**
- Adds `currentSchema=precis` to the JDBC URL
- Sets `precis` as the default schema for all database operations
- Application doesn't need to prefix table names with `precis.` in queries

## Benefits

### 1. **Namespace Isolation**
- Application tables are isolated in the `precis` schema
- Avoids conflicts with other applications using the same database
- Cleaner separation from PostgreSQL system tables in `public` schema

### 2. **Better Organization**
- All application objects are grouped together
- Easier to manage permissions (grant access to entire schema)
- Easier to backup/restore (can target specific schema)

### 3. **Production Best Practice**
- Follows PostgreSQL best practices
- Easier to manage multiple applications in same database
- Better security (can restrict access to specific schemas)

## Database Structure

After migration, the database will have:

```
precis (database)
├── public (schema) - PostgreSQL default, empty
└── precis (schema) - Application schema
    ├── flyway_schema_history (table)
    └── url_shorten (table)
        ├── idx_long_url (index)
        ├── idx_created_at (index)
        └── idx_expires_at (index)
```

## Verification

### Check Schemas
```bash
docker exec -it precis-postgres psql -U postgres -d precis -c "\dn"
```

Expected output:
```
  Name  |  Owner
--------+----------
 precis | postgres
 public | postgres
```

### Check Tables in precis Schema
```bash
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

### Check Table Structure
```bash
docker exec -it precis-postgres psql -U postgres -d precis -c "\d precis.url_shorten"
```

### Use Verification Script
```bash
.\verify-flyway.bat
```

## Testing

```bash
# Clean start
docker-compose down -v

# Rebuild and start
docker-compose up --build

# Verify schema and tables
.\verify-flyway.bat
```

## Summary

✅ **Flyway** creates and manages the `precis` schema  
✅ **Migration SQL** creates tables in `precis` schema  
✅ **JPA Entity** references `precis.url_shorten` table  
✅ **DataSource** sets `currentSchema=precis` as default  
✅ **Application** uses `precis` schema for all operations  

All database objects are now properly organized in the `precis` schema! 🎯

