# Quick Start Guide

## Prerequisites Check

Before running the application, ensure you have:

```bash
# Check Java version (requires Java 25+)
java -version

# Check Gradle (optional - wrapper included)
./gradlew --version

# Check Docker (for containerized deployment)
docker --version
docker-compose --version
```

## Option 1: Local Development (Fastest)

### 1. Start PostgreSQL
```bash
docker-compose up -d postgres
```

### 2. Run Application
```bash
# Linux/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### 3. Test the API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Create shortened URL (will fail without OAuth2 token - see Security Setup below)
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"longUrl": "https://www.example.com"}'
```

## Option 2: Full Docker Stack

### Run Everything with Docker
```bash
# Build and start all services (PostgreSQL + Zipkin + Application)
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f app

# Stop everything
docker-compose down
```

### Access Services
- **Application**: http://localhost:8080
- **Zipkin UI**: http://localhost:9411
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

## Security Setup (Important!)

### For Development/Testing

The application now requires OAuth2/JWT authentication. You have two options:

#### Option A: Disable Security for Testing (Quick)

Create a test security configuration:

```java
// src/main/java/ind/shubhamn/precisrest/security/TestSecurityConfig.java
@Configuration
@Profile("dev")
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
```

Then run with dev profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Option B: Use OAuth2 Provider (Production-Ready)

1. **Set up Auth0** (recommended for quick start):
   - Sign up at https://auth0.com
   - Create an API
   - Get your domain and audience

2. **Configure application.yml**:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-domain.auth0.com/
```

3. **Get a JWT token** from Auth0 and use it in requests:
```bash
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com"}'
```

## Testing

### Run Tests
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests UrlShortenerServiceTest

# Run with coverage
./gradlew test jacocoTestReport
```

Tests are configured to:
- Use H2 in-memory database
- Disable security
- Disable Flyway
- Run independently

## Database Management

### View Flyway Migration Status
```bash
./gradlew flywayInfo
```

### Manually Run Migrations
```bash
./gradlew flywayMigrate
```

### Repair Flyway (if needed)
```bash
./gradlew flywayRepair
```

## Monitoring & Observability

### Health Checks
```bash
# General health
curl http://localhost:8080/actuator/health

# Liveness probe
curl http://localhost:8080/actuator/health/liveness

# Readiness probe
curl http://localhost:8080/actuator/health/readiness
```

### Metrics
```bash
# All metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Prometheus format
curl http://localhost:8080/actuator/prometheus
```

### Distributed Tracing
1. Start Zipkin: `docker-compose up -d zipkin`
2. Access UI: http://localhost:9411
3. Make API requests
4. View traces in Zipkin UI

## Common Issues & Solutions

### Issue: Port 8080 already in use
```bash
# Find process using port 8080
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080

# Change port in application.yml
server:
  port: 8081
```

### Issue: Database connection refused
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check logs
docker logs precis-postgres

# Restart PostgreSQL
docker-compose restart postgres
```

### Issue: Flyway migration failed
```bash
# Check migration status
./gradlew flywayInfo

# Repair Flyway
./gradlew flywayRepair

# Clean and rebuild
./gradlew clean build
```

### Issue: OAuth2 authentication errors
- Verify JWT token is valid
- Check issuer-uri is correct
- Ensure token has required scopes
- For testing, use dev profile with security disabled

## Environment Variables

### Required
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/precis
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
```

### Optional
```bash
export PORT=8080
export SPRING_PROFILES_ACTIVE=prod
export ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans
export JWT_ISSUER_URI=https://your-auth-server.com
```

## Next Steps

1. ✅ Application is running
2. 🔒 Configure OAuth2 for production
3. 📊 Set up Prometheus + Grafana for metrics
4. 🚀 Deploy to Render (see DEPLOYMENT.md)
5. 📝 Review IMPLEMENTATION_SUMMARY.md for all features

## Additional Resources

- **Full Deployment Guide**: [DEPLOYMENT.md](DEPLOYMENT.md)
- **Implementation Details**: [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
- **Project README**: [README.md](README.md)

