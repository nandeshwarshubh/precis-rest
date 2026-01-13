# Deployment Guide

## Table of Contents
- [Local Development](#local-development)
- [Docker Deployment](#docker-deployment)
- [Render Deployment](#render-deployment)
- [Environment Variables](#environment-variables)
- [Security Configuration](#security-configuration)

## Local Development

### Prerequisites
- Java 25+
- PostgreSQL 17+
- Gradle 9.1+ (included via wrapper)

### Steps
1. Start PostgreSQL:
```bash
docker-compose up -d postgres
```

2. Run the application:
```bash
./gradlew bootRun
```

3. Access the application:
- API: http://localhost:8080/app/rest
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics

## Docker Deployment

### Build and Run with Docker Compose

```bash
# Build and start all services (PostgreSQL, Zipkin, Application)
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Individual Docker Commands

```bash
# Build the Docker image
docker build -t precis-rest:latest .

# Run the container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/precis \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e ZIPKIN_ENDPOINT=http://host.docker.internal:9411/api/v2/spans \
  precis-rest:latest
```

## Render Deployment

### Prerequisites
- Render account (https://render.com)
- GitHub repository connected to Render

### Deployment Steps

1. **Push code to GitHub**:
```bash
git add .
git commit -m "Add deployment configuration"
git push origin main
```

2. **Create New Web Service on Render**:
   - Go to Render Dashboard
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml`

3. **Configure Environment Variables** (if not using render.yaml):
   - `DATABASE_URL`: PostgreSQL connection string
   - `DATABASE_USERNAME`: postgres
   - `DATABASE_PASSWORD`: (auto-generated or custom)
   - `ZIPKIN_ENDPOINT`: Zipkin service URL
   - `SPRING_PROFILES_ACTIVE`: prod
   - `PORT`: 8080

4. **Deploy**:
   - Render will automatically build and deploy
   - Monitor deployment logs in Render dashboard

### Manual Render Configuration (Alternative to render.yaml)

If not using `render.yaml`, configure manually:

**Database (PostgreSQL)**:
- Type: PostgreSQL
- Name: precis-postgres
- Database: precis
- User: postgres
- Region: Oregon (or nearest)
- Plan: Free

**Web Service**:
- Type: Web Service
- Name: precis-rest
- Environment: Docker
- Build Command: `./gradlew clean build -x test`
- Start Command: `java -jar build/libs/precis-rest-0.0.1-SNAPSHOT.jar`
- Health Check Path: `/actuator/health`
- Plan: Free

## Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/precis` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `your-secure-password` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `ZIPKIN_ENDPOINT` | Zipkin tracing endpoint | `http://localhost:9411/api/v2/spans` |
| `JWT_ISSUER_URI` | OAuth2 JWT issuer URI | (none) |
| `JWT_JWK_SET_URI` | OAuth2 JWK Set URI | (none) |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |

## Security Configuration

### OAuth2 / JWT Configuration

For production, configure OAuth2 with a real identity provider:

**Option 1: Auth0**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-domain.auth0.com/
```

**Option 2: Keycloak**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-keycloak.com/realms/your-realm
```

**Option 3: Custom JWT (Development Only)**
For development/testing without OAuth2 server, you can disable security temporarily.

## Health Checks

### Endpoints

- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`
- **General Health**: `/actuator/health`

### Kubernetes Probes (if deploying to K8s)

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
```

## Monitoring

### Zipkin Tracing

Access Zipkin UI:
- Local: http://localhost:9411
- Docker: http://localhost:9411
- Production: Configure based on your Zipkin deployment

### Prometheus Metrics

Metrics endpoint: `/actuator/prometheus`

Example Prometheus scrape config:
```yaml
scrape_configs:
  - job_name: 'precis-rest'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

## Troubleshooting

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check PostgreSQL logs
docker logs precis-postgres

# Test connection
psql -h localhost -U postgres -d precis
```

### Application Won't Start

```bash
# Check application logs
docker logs precis-app

# Check if port is in use
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac
```

### Flyway Migration Errors

```bash
# Manually run Flyway repair
./gradlew flywayRepair

# Check migration status
./gradlew flywayInfo
```

