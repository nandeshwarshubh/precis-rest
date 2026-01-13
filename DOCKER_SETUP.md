# Docker Setup Guide

## Dockerfile Structure

This project uses separate Dockerfiles for different services:

### 1. `Dockerfile.postgres` - PostgreSQL Database
- **Base Image**: `postgres:17`
- **Purpose**: Custom PostgreSQL container with optional initialization scripts
- **Features**:
  - PostgreSQL 17 with contrib extensions
  - Health checks configured
  - Optional custom init scripts support
  - Environment variables for database configuration

### 2. `Dockerfile.app` - Java Spring Boot Application
- **Base Image**: `eclipse-temurin:25-jre-alpine`
- **Purpose**: Multi-stage build for optimized Java application
- **Features**:
  - Stage 1: Build with Gradle 9.1 and JDK 25
  - Stage 2: Runtime with JRE 25 (Alpine Linux)
  - Non-root user for security
  - Health checks via Spring Actuator
  - Optimized JVM settings for containers

## Docker Compose Services

### PostgreSQL Service
```yaml
postgres:
  build:
    context: .
    dockerfile: Dockerfile.postgres
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

### Application Service
```yaml
app:
  build:
    context: .
    dockerfile: Dockerfile.app
  ports:
    - "8080:8080"
  depends_on:
    postgres:
      condition: service_healthy
```

## Usage Commands

### Build and Run All Services
```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v
```

### Build Individual Services
```bash
# Build only PostgreSQL
docker-compose build postgres

# Build only the application
docker-compose build app

# Run only PostgreSQL
docker-compose up postgres

# Run only the application (requires postgres running)
docker-compose up app
```

### Rebuild After Changes
```bash
# Rebuild specific service
docker-compose up --build postgres
docker-compose up --build app

# Force rebuild (no cache)
docker-compose build --no-cache postgres
docker-compose build --no-cache app
```

## Custom PostgreSQL Initialization Scripts

To add custom initialization scripts to PostgreSQL:

1. Create the directory structure:
```bash
mkdir -p docker/postgres/init-scripts
```

2. Add your SQL scripts:
```bash
# Example: Create additional tables or seed data
echo "CREATE TABLE example (id SERIAL PRIMARY KEY);" > docker/postgres/init-scripts/01-custom.sql
```

3. Uncomment the COPY line in `Dockerfile.postgres`:
```dockerfile
COPY docker/postgres/init-scripts/ /docker-entrypoint-initdb.d/
```

4. Rebuild the PostgreSQL container:
```bash
docker-compose build postgres
docker-compose up postgres
```

**Note**: Init scripts only run when the database is created for the first time. To re-run:
```bash
docker-compose down -v  # Remove volumes
docker-compose up --build postgres
```

## Environment Variables

### PostgreSQL (Dockerfile.postgres)
- `POSTGRES_DB=precis` - Database name
- `POSTGRES_USER=postgres` - Database user
- `POSTGRES_PASSWORD=postgres` - Database password

### Application (docker-compose.yml)
- `DATABASE_URL` - JDBC connection string
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `ZIPKIN_ENDPOINT` - Distributed tracing endpoint
- `SPRING_PROFILES_ACTIVE` - Spring profile (prod/dev)

## Health Checks

### PostgreSQL
- **Command**: `pg_isready -U postgres -d precis`
- **Interval**: 10 seconds
- **Timeout**: 5 seconds
- **Retries**: 5

### Application
- **Endpoint**: `http://localhost:8080/actuator/health`
- **Interval**: 30 seconds
- **Timeout**: 3 seconds
- **Start Period**: 40 seconds
- **Retries**: 3

## Troubleshooting

### PostgreSQL won't start
```bash
# Check logs
docker-compose logs postgres

# Check if port is in use
netstat -ano | findstr :5432  # Windows
lsof -i :5432                 # Linux/Mac

# Remove and recreate
docker-compose down -v
docker-compose up postgres
```

### Application won't connect to database
```bash
# Verify postgres is healthy
docker-compose ps

# Check network connectivity
docker-compose exec app ping postgres

# Verify environment variables
docker-compose exec app env | grep DATABASE
```

### Rebuild from scratch
```bash
# Remove everything
docker-compose down -v
docker system prune -a

# Rebuild
docker-compose up --build
```

