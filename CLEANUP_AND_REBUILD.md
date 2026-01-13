# Complete Docker Cleanup and Rebuild Guide

## What We've Done

1. ✅ Removed PostgreSQL init scripts
2. ✅ Removed custom PostgreSQL configuration files
3. ✅ Simplified Dockerfile.postgres to minimal working version
4. ✅ Simplified docker-compose.yml
5. ✅ Created cleanup script

## Step-by-Step Cleanup Process

### Option 1: Use the Cleanup Script (Easiest)

```bash
# Run the cleanup script
.\cleanup-docker.bat
```

This will:
- Stop all containers
- Remove all precis-related containers
- Remove all precis-related images
- Remove all volumes
- Clean up Docker system

### Option 2: Manual Cleanup (Step-by-Step)

```bash
# Step 1: Stop and remove containers
docker-compose down -v

# Step 2: Remove specific containers (if they exist)
docker rm -f precis-postgres precis-app precis-zipkin

# Step 3: Remove images
docker rmi -f precis-rest-postgres precis-rest-app

# Step 4: Remove volumes
docker volume rm precis-rest_postgres_data
docker volume prune -f

# Step 5: Clean up everything (CAUTION: removes all unused Docker resources)
docker system prune -a -f --volumes
```

### Option 3: Nuclear Option (Complete Docker Reset)

⚠️ **WARNING**: This removes ALL Docker images, containers, and volumes on your system!

```bash
# Stop all running containers
docker stop $(docker ps -aq)

# Remove all containers
docker rm $(docker ps -aq)

# Remove all images
docker rmi $(docker images -q) -f

# Remove all volumes
docker volume rm $(docker volume ls -q)

# Clean up everything
docker system prune -a -f --volumes
```

## Rebuild PostgreSQL

After cleanup, rebuild with the simplified configuration:

```bash
# Rebuild and start PostgreSQL
docker-compose up --build postgres
```

You should see:
```
precis-postgres  | PostgreSQL init process complete; ready for start up.
precis-postgres  | LOG:  database system is ready to accept connections
```

## Fix DBeaver Connection

**The timezone error is coming from DBeaver, not PostgreSQL!**

### Solution: Configure DBeaver to Use UTC

1. **Open DBeaver**
2. **Right-click** your PostgreSQL connection
3. **Select** "Edit Connection"
4. **Go to** "Driver properties" tab
5. **Find or Add** property:
   - Name: `TimeZone`
   - Value: `UTC`
6. **Click** "Test Connection"
7. **Click** "OK" to save

### Alternative: Modify JDBC URL

In the "Main" tab, change the URL to:
```
jdbc:postgresql://localhost:5432/precis?TimeZone=UTC
```

## Verify Everything Works

### 1. Check PostgreSQL is Running
```bash
docker ps
```
Should show `precis-postgres` with status "healthy"

### 2. Check PostgreSQL Timezone
```bash
docker exec -it precis-postgres psql -U postgres -d precis -c "SHOW timezone;"
```
Should output: `UTC`

### 3. Check PostgreSQL Logs
```bash
docker logs precis-postgres
```
Should NOT show any "Asia/Calcutta" errors

### 4. Test Connection from Command Line
```bash
docker exec -it precis-postgres psql -U postgres -d precis
```
Should connect successfully. Type `\q` to exit.

### 5. Test DBeaver Connection
After configuring TimeZone=UTC in DBeaver:
- Right-click connection → "Edit Connection"
- Click "Test Connection"
- Should see "Connected" message

## Start the Full Application

Once PostgreSQL is working:

```bash
# Start all services
docker-compose up --build

# Or in detached mode
docker-compose up -d --build
```

Access:
- Application: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Zipkin: http://localhost:9411

## Troubleshooting

### PostgreSQL Still Shows Timezone Error

**Check where the error is coming from:**
```bash
docker logs precis-postgres | grep -i "calcutta"
```

If you see errors:
1. Make sure you ran the cleanup script
2. Verify no old volumes exist: `docker volume ls`
3. Rebuild with `--no-cache`: `docker-compose build --no-cache postgres`

### DBeaver Still Can't Connect

1. **Close DBeaver completely**
2. **Clear DBeaver cache**:
   - Windows: Delete `%APPDATA%\.dbeaver\`
   - Mac/Linux: Delete `~/.dbeaver/`
3. **Restart DBeaver**
4. **Recreate the connection** with `TimeZone=UTC` property

### Port Already in Use

```bash
# Windows - Find process using port 5432
netstat -ano | findstr :5432

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

## Summary of Changes

### Simplified Dockerfile.postgres
- Minimal configuration
- Only sets TZ and PGTZ environment variables
- No custom config files
- No init scripts

### Simplified docker-compose.yml
- Uses command line parameters for timezone
- Environment variables for TZ and PGTZ
- Clean and simple configuration

### Key Point
**The "Asia/Calcutta" error is from DBeaver sending the timezone in the connection string.**
**Fix it in DBeaver by setting TimeZone=UTC in the driver properties!**

