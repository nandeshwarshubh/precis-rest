# DBeaver Connection Guide - Fixing "Asia/Calcutta" Timezone Error

## Problem
When connecting to PostgreSQL 17 from DBeaver, you may encounter:
```
FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"
```

This happens because:
1. PostgreSQL 17 removed support for the legacy timezone name "Asia/Calcutta"
2. DBeaver automatically detects your system timezone and sends it to PostgreSQL
3. The correct timezone name is now "Asia/Kolkata" or use "UTC"

## Solution 1: Configure DBeaver Connection (Recommended)

### Step 1: Edit Connection Properties
1. In DBeaver, right-click your PostgreSQL connection
2. Select **"Edit Connection"**
3. Go to the **"Driver properties"** tab

### Step 2: Add/Modify TimeZone Property
4. Look for the property `TimeZone` in the list
5. If it exists, change its value to: `UTC`
6. If it doesn't exist, click **"Add"** and create:
   - **Name**: `TimeZone`
   - **Value**: `UTC`

### Step 3: Alternative - Modify JDBC URL
Or modify the JDBC URL directly in the "Main" tab:
```
jdbc:postgresql://localhost:5432/precis?TimeZone=UTC
```

### Step 4: Test Connection
7. Click **"Test Connection"** to verify
8. Click **"OK"** to save

## Solution 2: Use Asia/Kolkata Instead
If you prefer to use Indian timezone:
- Set `TimeZone` property to: `Asia/Kolkata` (not "Asia/Calcutta")

## Solution 3: Rebuild PostgreSQL Container

We've configured the PostgreSQL container to default to UTC timezone.

### Rebuild Steps:
```bash
# Stop and remove existing container and volumes
docker-compose down -v

# Rebuild and start PostgreSQL
docker-compose up --build postgres
```

### What's Configured:
1. ✅ Environment variables: `TZ=UTC`, `PGTZ=UTC`
2. ✅ PostgreSQL config file: `timezone = 'UTC'`
3. ✅ Command line parameter: `-c timezone=UTC`
4. ✅ Database-level setting via init script

## Solution 4: DBeaver Global Settings

### Change DBeaver's Default Timezone:
1. Go to **Window** → **Preferences** (or **DBeaver** → **Preferences** on Mac)
2. Navigate to **Connections** → **Drivers** → **PostgreSQL**
3. Click on **"PostgreSQL"** driver
4. Go to **"Connection properties"** tab
5. Add property: `TimeZone` = `UTC`
6. Click **"OK"**

This will apply to all new PostgreSQL connections.

## Verification

After applying any solution, test the connection:

### From DBeaver:
1. Right-click connection → **"Edit Connection"**
2. Click **"Test Connection"**
3. Should see: "Connected" message

### From Command Line:
```bash
# Connect to PostgreSQL
docker exec -it precis-postgres psql -U postgres -d precis

# Check timezone setting
SHOW timezone;
# Should output: UTC

# Exit
\q
```

## Troubleshooting

### Still Getting Error?
1. **Clear DBeaver cache**:
   - Close DBeaver
   - Delete: `~/.dbeaver/` or `%APPDATA%\.dbeaver\` (Windows)
   - Restart DBeaver

2. **Check PostgreSQL logs**:
   ```bash
   docker logs precis-postgres
   ```

3. **Verify PostgreSQL timezone**:
   ```bash
   docker exec -it precis-postgres psql -U postgres -c "SHOW timezone;"
   ```

4. **Check DBeaver connection properties**:
   - Edit Connection → Driver properties
   - Look for any timezone-related properties
   - Remove or change to UTC

### Connection String Examples

**Correct URLs:**
```
jdbc:postgresql://localhost:5432/precis?TimeZone=UTC
jdbc:postgresql://localhost:5432/precis?TimeZone=Asia/Kolkata
```

**Incorrect URLs:**
```
jdbc:postgresql://localhost:5432/precis?TimeZone=Asia/Calcutta  ❌
```

## Additional Notes

- **Why UTC?** It's the standard for databases and avoids timezone conversion issues
- **Application Compatibility**: The Java application is already configured to use UTC
- **Data Consistency**: Using UTC ensures consistent timestamps across all systems

## Quick Reference

| Setting | Location | Value |
|---------|----------|-------|
| DBeaver Driver Property | TimeZone | UTC |
| JDBC URL Parameter | TimeZone | UTC |
| PostgreSQL Config | timezone | UTC |
| Environment Variable | TZ | UTC |
| Environment Variable | PGTZ | UTC |

