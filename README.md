# [Precis](https://precis-rest.onrender.com/) - URL Shortening Service

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue.svg)](https://www.postgresql.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.1-green.svg)](https://gradle.org/)

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [High-Level Architecture](#-high-level-architecture)
- [Tech Stack](#-tech-stack)
- [Module Breakdown](#-module-breakdown)
- [API Documentation](#-api-documentation)
- [Configuration & Environment Setup](#-configuration--environment-setup)
- [Database Design](#-database-design)
- [Security](#-security)
- [Error Handling & Logging](#-error-handling--logging)
- [Testing Strategy](#-testing-strategy)
- [Build & Run Instructions](#-build--run-instructions)
- [Deployment](#-deployment)
- [Non-Functional Requirements](#-non-functional-requirements)
- [Assumptions & Design Decisions](#-assumptions--design-decisions)
- [Future Enhancements](#-future-enhancements)

---

## 🔷 Project Overview

### Application Name
**Precis** - A production-grade URL shortening service

### Business Summary
Precis is a RESTful microservice that provides URL shortening capabilities, allowing users to convert long URLs into compact, shareable short URLs. The service generates deterministic short URLs using SHA-256 hashing and Base64 encoding, ensuring consistent results for identical input URLs. Additionally, users can specify custom aliases for personalized short URLs, with automatic validation to prevent duplicates.

### Key Responsibilities
- Generate short URLs from long URLs using cryptographic hashing (SHA-256 + Base64)
- Support custom aliases for personalized short URLs
- Validate custom alias availability and prevent duplicates
- Store URL mappings persistently in PostgreSQL database with timestamps
- Retrieve original URLs from short URL identifiers
- Provide RESTful API endpoints for URL operations
- Handle concurrent requests with request-scoped controllers
- Comprehensive logging with correlation IDs for request tracing
- Input validation and error handling with proper HTTP status codes

### Target Users
- **Frontend Applications**: Web and mobile clients requiring URL shortening
- **API Consumers**: Third-party services integrating URL shortening functionality
- **Internal Services**: Microservices within the same ecosystem

---

## 🔷 High-Level Architecture

### Architecture Pattern
Precis follows a **Layered Architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                     Client / UI Layer                        │
│              (Web Apps, Mobile Apps, APIs)                   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Presentation Layer                          │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UrlShortenerController (REST API)                   │  │
│  │  - POST /app/rest/shorten                            │  │
│  │  - POST /app/rest/long                               │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UrlShortenerService                                 │  │
│  │  - Business Logic                                    │  │
│  │  - SHA-256 Hashing                                   │  │
│  │  - Base64 Encoding                                   │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                 Data Access Layer                            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UrlShortenerDAO (Spring Data JPA)                   │  │
│  │  - CRUD Operations                                   │  │
│  │  - Custom Queries                                    │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │ JPA/Hibernate
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Database Layer                             │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  PostgreSQL 18.1                                     │  │
│  │  - URL_SHORTEN Table                                 │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Deployment Model
**Monolithic Microservice** - Single deployable unit with focused responsibility

### Communication Patterns
- **Synchronous REST**: HTTP/JSON for client-server communication
- **Request-Scoped Controllers**: Ensures thread safety and isolation
- **CORS Enabled**: Supports cross-origin requests from web clients

---

## 🔷 Tech Stack

| Layer                | Technology                          | Version | Purpose                                    |
|----------------------|-------------------------------------|---------|-------------------------------------------|
| **Language**         | Java                                | 25      | Primary programming language              |
| **Framework**        | Spring Boot                         | 4.0.0   | Application framework                     |
| **Web**              | Spring Web MVC                      | 7.0.1   | REST API implementation                   |
| **ORM**              | Hibernate / JPA                     | 7.1.8   | Object-relational mapping                 |
| **Database**         | PostgreSQL                          | 18.1    | Primary data store                        |
| **Migration**        | Flyway                              | 11.1.0  | Database schema versioning                |
| **Build Tool**       | Gradle                              | 9.1     | Dependency management & build automation  |
| **Testing**          | JUnit 5                             | 5.11.x  | Unit testing framework                    |
| **Mocking**          | Mockito                             | 5.14.x  | Test mocking framework                    |
| **Test DB**          | H2 Database                         | 2.3.x   | In-memory database for testing            |
| **Containerization** | Docker / Docker Compose             | 3.8     | Local development environment             |
| **JSON Processing**  | Jackson                             | 3.0.x   | JSON serialization/deserialization        |
| **Validation**       | Hibernate Validator (Bean Validation)| 8.0.x  | Input validation (JSR-380)                |
| **Mapping**          | MapStruct                           | 1.6.3   | Entity-DTO mapping                        |
| **Logging**          | SLF4J / Logback                     | 2.0.x   | Application logging                       |
| **Tracing**          | Micrometer Tracing + Zipkin         | 1.4.x   | Distributed tracing                       |

---

## 🔷 Module Breakdown

### Package Structure

```
ind.shubhamn.precisrest
├── PrecisApplication.java              # Spring Boot entry point
├── constants/                           # Application constants
│   └── ErrorCodes.java                 # Error code definitions
├── dto/                                 # Data Transfer Objects
│   ├── ErrorResponse.java              # Error response DTO
│   ├── GetLongUrlRequestDTO.java       # Get long URL request
│   ├── GetLongUrlResponseDTO.java      # Get long URL response
│   ├── ShortenUrlRequestDTO.java       # Shorten URL request
│   └── ShortenUrlResponseDTO.java      # Shorten URL response
├── exception/                           # Exception handling
│   ├── GlobalExceptionHandler.java     # Global exception handler
│   └── ShortUrlAlreadyExistsException.java # Custom exception
├── logging/                             # Logging infrastructure
│   └── RequestLoggingFilter.java       # Request/response logging with correlation IDs
├── mapper/                              # Object mapping
│   └── UrlMapper.java                  # MapStruct entity-DTO mapper
├── model/                               # Domain entities
│   └── ShortenedUrl.java               # URL entity (JPA)
├── rest/                                # Presentation layer
│   ├── UrlShortenerController.java     # REST endpoints
│   ├── WelcomeController.java          # Welcome page endpoint
│   ├── ResponseEntityHelper.java       # Response utilities
│   └── config/                          # Web configuration
│       ├── RestConfig.java             # CORS configuration
│       └── SimpleCorsFilter.java       # CORS filter
├── service/                             # Business logic layer
│   └── UrlShortenerService.java        # URL shortening logic
├── dao/                                 # Data access layer
│   ├── UrlShortenerDAO.java            # JPA repository
│   └── config/                          # Data configuration
│       ├── DatabaseConfig.java         # Database properties
│       └── JpaConfiguration.java       # JPA/Hibernate setup
└── validation/                          # Input validation
    ├── UrlValidator.java               # URL validation interface
    └── UrlValidatorImpl.java           # URL validation implementation
```

### Module Responsibilities

#### **1. Presentation Layer (`rest`)**
- **UrlShortenerController**: Exposes REST endpoints for URL operations
  - `POST /app/rest/shorten` - Create shortened URL
  - `POST /app/rest/long` - Retrieve original URL
  - Request-scoped for thread safety
  - Comprehensive logging at all levels (TRACE, DEBUG, INFO)
- **WelcomeController**: Serves welcome page at root endpoint
  - `GET /` - Returns HTML welcome page
- **ResponseEntityHelper**: Standardizes HTTP responses (success/error)
  - Success responses with 200 OK or custom status
  - Error responses with appropriate HTTP status codes
  - Comprehensive logging for all responses
- **RestConfig**: Configures CORS policies for cross-origin requests
- **SimpleCorsFilter**: Implements CORS filtering at servlet level

#### **2. Service Layer (`service`)**
- **UrlShortenerService**:
  - Implements URL shortening algorithm (SHA-256 + Base64)
  - Supports custom aliases for short URLs
  - Validates custom alias availability
  - Orchestrates business logic
  - Manages transactions
  - Comprehensive logging at all levels

#### **3. Data Access Layer (`dao`)**
- **UrlShortenerDAO**: Spring Data JPA repository interface
  - Extends `JpaRepository` for CRUD operations
  - Custom query: `findByShortUrl(String shortUrl)`
- **DatabaseConfig**: Externalizes database connection properties
- **JpaConfiguration**: Configures EntityManager, DataSource, and Hibernate

#### **4. Domain Layer (`model`)**
- **ShortenedUrl**: JPA entity representing URL mappings
  - Maps to `URL_SHORTEN` table
  - Fields: `shortUrl` (PK), `longUrl`, `createdAt`, `expiresAt`
  - Automatic timestamp management

#### **5. DTO Layer (`dto`)**
- **ShortenUrlRequestDTO**: Request for creating shortened URL
  - Validates `longUrl` (required, max 2048 chars, valid URL format)
  - Optional `customAlias` field
- **ShortenUrlResponseDTO**: Response with shortened URL details
- **GetLongUrlRequestDTO**: Request for retrieving original URL
  - Validates `shortUrl` (required, not blank)
- **GetLongUrlResponseDTO**: Response with original URL details
- **ErrorResponse**: Standardized error response format
  - Fields: `errorCode`, `message`, `statusCode`

#### **6. Exception Handling (`exception`)**
- **GlobalExceptionHandler**: Centralized exception handling
  - Handles `ShortUrlAlreadyExistsException` (409 Conflict)
  - Handles `MethodArgumentNotValidException` (400 Bad Request)
  - Handles `NoSuchElementException` (404 Not Found)
  - Handles generic `Exception` (500 Internal Server Error)
  - Comprehensive logging at all levels
- **ShortUrlAlreadyExistsException**: Custom exception for duplicate aliases

#### **7. Validation Layer (`validation`)**
- **UrlValidator**: Interface for URL validation
- **UrlValidatorImpl**: Implementation of URL validation logic
  - Validates URL format and structure

#### **8. Logging Infrastructure (`logging`)**
- **RequestLoggingFilter**: Request/response logging filter
  - Generates correlation IDs for request tracing
  - Logs incoming requests with method, URI, remote address
  - Logs outgoing responses with status code and duration
  - Uses MDC (Mapped Diagnostic Context) for correlation ID propagation

#### **9. Mapper Layer (`mapper`)**
- **UrlMapper**: MapStruct interface for entity-DTO mapping
  - `toEntity()` - Converts DTO to entity
  - `toShortenUrlResponseDto()` - Converts entity to response DTO
  - `toGetLongUrlResponseDto()` - Converts entity to response DTO
  - Auto-generated implementation at compile time

#### **10. Constants (`constants`)**
- **ErrorCodes**: Centralized error code definitions
  - `NOT_FOUND` - Resource not found
  - `ALIAS_ALREADY_EXISTS` - Custom alias already in use
  - `VALIDATION_ERROR` - Input validation failure
  - `INTERNAL_SERVER_ERROR` - Unexpected server error

---

## 🔷 API Documentation

### Base URL
```
http://localhost:8080/app/rest
```

### Endpoints

| Method | Endpoint       | Description                    | Request Body                | Response Body                |
|--------|----------------|--------------------------------|-----------------------------|------------------------------|
| GET    | `/`            | Welcome page                   | None                        | HTML                         |
| POST   | `/shorten`     | Create a shortened URL         | `ShortenUrlRequestDTO`      | `ShortenUrlResponseDTO`      |
| POST   | `/long`        | Retrieve original URL          | `GetLongUrlRequestDTO`      | `GetLongUrlResponseDTO`      |

### Request/Response Models

#### ShortenUrlRequestDTO
```json
{
  "longUrl": "string (required, max 2048 chars, valid URL format)",
  "customAlias": "string (optional, custom short URL alias)"
}
```

**Validation Rules:**
- `longUrl`: Required, not blank, max 2048 characters, must be valid URL format
- `customAlias`: Optional, if provided will be used as the short URL instead of auto-generated hash

#### ShortenUrlResponseDTO
```json
{
  "shortUrl": "string (8 chars or custom alias)",
  "longUrl": "string (original URL)",
  "createdAt": "timestamp (ISO 8601 format)",
  "expiresAt": "timestamp (ISO 8601 format, nullable)"
}
```

#### GetLongUrlRequestDTO
```json
{
  "shortUrl": "string (required, not blank)"
}
```

**Validation Rules:**
- `shortUrl`: Required, not blank

#### GetLongUrlResponseDTO
```json
{
  "shortUrl": "string (short URL identifier)",
  "longUrl": "string (original URL)",
  "createdAt": "timestamp (ISO 8601 format)",
  "expiresAt": "timestamp (ISO 8601 format, nullable)"
}
```

#### ErrorResponse
```json
{
  "errorCode": "string (error code constant)",
  "message": "string (error description)",
  "statusCode": "integer (HTTP status code)"
}
```

### API Examples

#### 1. Create Shortened URL (Auto-Generated)

**Request:**
```bash
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://www.example.com/very/long/url/path?param1=value1&param2=value2"
  }'
```

**Success Response (200 OK):**
```json
{
  "shortUrl": "GRNHv-Vd",
  "longUrl": "https://www.example.com/very/long/url/path?param1=value1&param2=value2",
  "createdAt": "2026-01-21T00:00:00Z",
  "expiresAt": null
}
```

#### 2. Create Shortened URL (Custom Alias)

**Request:**
```bash
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://www.example.com/very/long/url/path",
    "customAlias": "my-custom-url"
  }'
```

**Success Response (200 OK):**
```json
{
  "shortUrl": "my-custom-url",
  "longUrl": "https://www.example.com/very/long/url/path",
  "createdAt": "2026-01-21T00:00:00Z",
  "expiresAt": null
}
```

**Error Response - Alias Already Exists (409 Conflict):**
```json
{
  "errorCode": "ALIAS_ALREADY_EXISTS",
  "message": "Short URL alias 'my-custom-url' already exists",
  "statusCode": 409
}
```

#### 3. Retrieve Original URL

**Request:**
```bash
curl -X POST http://localhost:8080/app/rest/long \
  -H "Content-Type: application/json" \
  -d '{
    "shortUrl": "GRNHv-Vd"
  }'
```

**Success Response (200 OK):**
```json
{
  "shortUrl": "GRNHv-Vd",
  "longUrl": "https://www.example.com/very/long/url/path?param1=value1&param2=value2",
  "createdAt": "2026-01-21T00:00:00Z",
  "expiresAt": null
}
```

**Error Response - Not Found (404 Not Found):**
```json
{
  "errorCode": "NOT_FOUND",
  "message": "No value present",
  "statusCode": 404
}
```

#### 4. Validation Error Example

**Request:**
```bash
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": ""
  }'
```

**Error Response (400 Bad Request):**
```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed: longUrl - must not be blank; ",
  "statusCode": 400
}
```

### HTTP Status Codes

| Status Code | Description                                      | Error Code               |
|-------------|--------------------------------------------------|--------------------------|
| 200         | Success - Request processed successfully         | N/A                      |
| 400         | Bad Request - Validation error                   | `VALIDATION_ERROR`       |
| 404         | Not Found - Short URL not found                  | `NOT_FOUND`              |
| 409         | Conflict - Custom alias already exists           | `ALIAS_ALREADY_EXISTS`   |
| 500         | Internal Server Error - Unexpected error         | `INTERNAL_SERVER_ERROR`  |

### Validation & Error Handling

- **Input Validation**: Bean Validation (JSR-380) with comprehensive constraints on all DTOs
- **URL Validation**: Custom `UrlValidator` implementation for URL format validation
- **Error Responses**: Standardized JSON format with error code, message, and HTTP status code
- **Exception Handling**: Global exception handling via `@RestControllerAdvice` in `GlobalExceptionHandler`
- **Correlation IDs**: Request tracing with correlation IDs for debugging and monitoring

---

## 🔷 Configuration & Environment Setup

### Required Environment Variables

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/precis
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# Optional: JVM Options
JAVA_OPTS=-Xmx512m -Xms256m
```

### Application Configuration

#### `application.yml` (Production)

```yaml
database:
  url: "jdbc:postgresql://localhost:5432/precis"
  username: "postgres"
  password: "postgres"
```

#### `application-test.yml` (Testing)

```yaml
database:
  url: "jdbc:h2:mem:testdb"
  username: "sa"
  password: ""

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

### Configuration Profiles

| Profile | Purpose                          | Database | Auto-DDL |
|---------|----------------------------------|----------|----------|
| default | Production/Development           | PostgreSQL | update |
| test    | Unit & Integration Testing       | H2 (in-memory) | create-drop |

### Externalized Configuration

Configuration is externalized using Spring Boot's `@ConfigurationProperties`:

```java
@Configuration
@ConfigurationProperties("database")
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    // getters and setters
}
```

This allows configuration via:
- `application.yml` files
- Environment variables
- Command-line arguments
- External configuration servers (Spring Cloud Config)

---

## 🔷 Database Design

### Entity-Relationship Model

```
┌──────────────────────────────────────────┐
│           URL_SHORTEN                    │
├──────────────────────────────────────────┤
│ short_url   VARCHAR(255)         [PK]    │
│ long_url    VARCHAR(2048)        [NOT NULL] │
│ created_at  TIMESTAMP            [NOT NULL] │
│ expires_at  TIMESTAMP            [NULL]  │
└──────────────────────────────────────────┘
```

### Table Schema

#### URL_SHORTEN

| Column     | Type         | Constraints | Description                    |
|------------|--------------|-------------|--------------------------------|
| short_url  | VARCHAR(255) | PRIMARY KEY | Short URL identifier (8 chars or custom alias) |
| long_url   | VARCHAR(2048)| NOT NULL    | Original long URL              |
| created_at | TIMESTAMP    | NOT NULL    | Timestamp when URL was created |
| expires_at | TIMESTAMP    | NULL        | Optional expiration timestamp  |

### JPA Entity Mapping

```java
@Entity
@Table(name = "URL_SHORTEN")
public class ShortenedUrl {
    @Id
    @Column(name = "short_url", length = 255)
    private String shortUrl;

    @Column(name = "long_url", length = 2048, nullable = false)
    private String longUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Indexing Strategy

- **Primary Key Index**: Automatic index on `short_url` (PK)
- **Performance**: O(1) lookup for short URL retrieval
- **Custom Alias Support**: Variable-length short URLs (up to 255 chars)
- **Future Consideration**: Add index on `long_url` for duplicate detection

### Database Migration Approach

**Current**: Flyway for versioned schema migrations
- Migration scripts in `src/main/resources/db/migration/`
- Automatic schema versioning and migration on startup
- Rollback capabilities and audit trail
- Production-ready approach

**Migration Files**:
```
db/migration/
└── V1__create_url_shorten_table.sql
```

**Sample Migration**:
```sql
CREATE TABLE URL_SHORTEN (
    short_url VARCHAR(255) PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP
);
```

### Data Retention

- **Current**: Timestamp tracking with `created_at` and `expires_at` columns
- **Lifecycle Management**: Optional expiration support
- **Future Enhancement**: Implement scheduled cleanup job for expired URLs

---

## 🔷 Security

### Current Security Posture

⚠️ **Note**: Security features are currently disabled for development purposes.

```java
// Spring Security is commented out in build.gradle
// implementation 'org.springframework.boot:spring-boot-starter-security'
```

### CORS Configuration

**Enabled** - Allows cross-origin requests from web clients

```java
@Configuration
public class RestConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // ... additional headers
    }
}
```

### Authentication Mechanism

**Current**: None (all endpoints are publicly accessible)

**Recommended for Production**:
- **API Key Authentication**: Simple token-based auth for API consumers
- **OAuth 2.0**: For user-based authentication
- **JWT Tokens**: Stateless authentication with claims

### Authorization Model

**Current**: No role-based access control

**Recommended**:
```
Roles:
- ANONYMOUS: Read-only access (retrieve URLs)
- USER: Create and retrieve URLs
- ADMIN: Full CRUD + analytics
```

### Security Best Practices Implemented

✅ **CORS Protection**: Configured to allow specific origins
✅ **Request Scoping**: Controllers are request-scoped to prevent state leakage
✅ **SQL Injection Protection**: JPA/Hibernate parameterized queries
✅ **Input Validation**: Bean Validation (JSR-380) with comprehensive constraints
✅ **URL Validation**: Custom URL validator to prevent malicious URLs
✅ **Request Logging**: Correlation IDs for request tracing and audit trail
✅ **Exception Handling**: Centralized error handling with proper HTTP status codes

### Input Validation

**Bean Validation (JSR-380)** implemented across all DTOs:

```java
public class ShortenUrlRequestDTO {
    @NotBlank(message = "Long URL must not be blank")
    @Size(max = 2048, message = "Long URL must not exceed 2048 characters")
    @Pattern(regexp = "^https?://.*", message = "Long URL must be a valid URL")
    private String longUrl;

    @Size(max = 255, message = "Custom alias must not exceed 255 characters")
    private String customAlias;
}

public class GetLongUrlRequestDTO {
    @NotBlank(message = "Short URL must not be blank")
    private String shortUrl;
}
```

**Custom URL Validation**:
- `UrlValidator` interface with `UrlValidatorImpl` implementation
- Validates URL format and structure
- Prevents malicious URLs and invalid formats

### Security Recommendations

🔒 **For Production Deployment**:
1. Enable Spring Security
2. Use HTTPS/TLS for all communications
3. Implement API key rotation mechanism
4. Implement CAPTCHA for public endpoints
5. Add rate limiting to prevent abuse
6. Implement URL blacklist for known malicious domains
7. Add content security policy headers

---

## 🔷 Error Handling & Logging

### Global Exception Handling

Centralized error handling via `@RestControllerAdvice` in `GlobalExceptionHandler`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUrlAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleShortUrlAlreadyExists(
            ShortUrlAlreadyExistsException ex) {
        return ResponseEntityHelper.failureResponseEntity(
                ex, ErrorCodes.ALIAS_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // Builds detailed validation error message
        return ResponseEntityHelper.failureResponseEntity(
                ErrorCodes.VALIDATION_ERROR, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException ex) {
        return ResponseEntityHelper.failureResponseEntity(
                ex, ErrorCodes.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntityHelper.failureResponseEntity(
                ErrorCodes.INTERNAL_SERVER_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Exception Handling Strategy

| Exception Type                      | Error Code               | HTTP Status | Description                          |
|-------------------------------------|--------------------------|-------------|--------------------------------------|
| ShortUrlAlreadyExistsException      | ALIAS_ALREADY_EXISTS     | 409         | Custom alias already in use          |
| MethodArgumentNotValidException     | VALIDATION_ERROR         | 400         | Request validation failed            |
| NoSuchElementException              | NOT_FOUND                | 404         | Short URL not found in database      |
| Exception (generic)                 | INTERNAL_SERVER_ERROR    | 500         | Unexpected server error              |

### Error Response Format

```json
{
  "errorCode": "NOT_FOUND",
  "message": "Detailed error message",
  "statusCode": 404
}
```

### Logging Framework

**SLF4J with Logback** (Spring Boot default)

All classes implement comprehensive logging at multiple levels:

```java
private static final Logger logger = LoggerFactory.getLogger(ClassName.class);
```

### Logging Implementation

#### **Comprehensive Logging Across All Layers**

**1. Controller Layer** (`UrlShortenerController`, `WelcomeController`)
```java
logger.trace("Received shorten URL request: longUrl={}, customAlias={}", longUrl, customAlias);
logger.debug("Processing URL shortening request for: {}", longUrl);
logger.info("Creating shortened URL for: {}", longUrl);
logger.warn("Client attempted to use existing alias: {}", alias);
logger.error("Error processing request: {}", exception.getMessage(), exception);
```

**2. Service Layer** (`UrlShortenerService`)
```java
logger.trace("shortenUrl called: longUrl={}, customAlias={}", longUrl, customAlias);
logger.debug("Computing SHA-256 hash for URL");
logger.info("Generating auto-generated short URL using SHA-256");
logger.warn("Custom alias already exists: {}", customAlias);
```

**3. Exception Handler** (`GlobalExceptionHandler`)
```java
logger.trace("Handling ShortUrlAlreadyExistsException: message={}", ex.getMessage(), ex);
logger.debug("Custom alias conflict detected: errorCode={}, message={}", errorCode, message);
logger.info("Short URL alias already exists, returning 409 Conflict");
logger.warn("Client attempted to use existing alias");
logger.error("Unhandled exception occurred: errorCode={}, exceptionType={}", errorCode, type, ex);
```

**4. Response Helper** (`ResponseEntityHelper`)
```java
logger.trace("Creating success response entity with HTTP 200 OK");
logger.debug("Success response created: status=200, body={}", body);
logger.info("Returning successful response with HTTP 200 OK");
logger.warn("Client error occurred: errorCode={}, status={}", errorCode, status);
logger.error("Server error occurred: errorCode={}, status={}", errorCode, status, exception);
```

**5. Request Logging Filter** (`RequestLoggingFilter`)
```java
logger.info("Incoming request: method={}, uri={}, remoteAddr={}, correlationId={}",
    method, uri, remoteAddr, correlationId);
logger.info("Outgoing response: method={}, uri={}, status={}, duration={}ms, correlationId={}",
    method, uri, status, duration, correlationId);
```

### Log Levels

| Level | Usage                                          | Implementation                    |
|-------|------------------------------------------------|-----------------------------------|
| TRACE | Method entry/exit, detailed debugging          | All method entries, parameter values |
| DEBUG | Detailed flow information, intermediate steps  | Processing steps, computed values |
| INFO  | Key events, successful operations              | Request/response, operation success |
| WARN  | Client errors, potential issues                | 4xx errors, validation failures   |
| ERROR | Server errors, exceptions                      | 5xx errors, exception stack traces |

### Logging Configuration

**application.yml**:
```yaml
logging:
  level:
    root: INFO
    ind.shubhamn.precisrest: DEBUG
    org.springframework.web: DEBUG
    org.flywaydb: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### Correlation IDs / Request Tracing

**Implemented**: `RequestLoggingFilter` with correlation ID support

```java
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // Generate or extract correlation ID
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add to MDC for logging
        MDC.put("correlationId", correlationId);

        // Add to response header
        response.setHeader("X-Correlation-ID", correlationId);

        // Log request and response with correlation ID
        logger.info("Incoming request: method={}, uri={}, correlationId={}",
            method, uri, correlationId);
    }
}
```

**Features**:
- Automatic correlation ID generation
- MDC (Mapped Diagnostic Context) integration
- Request/response logging with timing
- Client IP address tracking
- Correlation ID propagation via HTTP headers

### Distributed Tracing

**Configured**: Zipkin integration for distributed tracing

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: ${ZIPKIN_ENDPOINT:http://localhost:9411/api/v2/spans}
```

**Dependencies**:
- `io.micrometer:micrometer-tracing-bridge-brave`
- `io.zipkin.reporter2:zipkin-reporter-brave`

---

## 🔷 Testing Strategy

### Test Coverage

**Current Test Suite**: 20 tests across 6 test classes

```
src/test/java/ind/shubhamn/precisrest/
├── PrecisApplicationTests.java                          # Integration test (1 test)
├── annotations/
│   └── ControllerTest.java                              # Custom test annotation
├── rest/
│   ├── UrlShortenerControllerTest.java                  # Controller tests (7 tests)
│   ├── UrlShortenerControllerCustomAliasTest.java       # Custom alias tests (4 tests)
│   └── WelcomeControllerTest.java                       # Welcome page test (1 test)
└── service/
    ├── UrlShortenerServiceTest.java                     # Service tests (4 tests)
    └── UrlShortenerServiceCustomAliasTest.java          # Custom alias service tests (3 tests)
```

### Testing Layers

#### 1. Unit Tests

**Service Layer Tests** (`UrlShortenerServiceTest`)
- Framework: JUnit 5 + Mockito
- Mocks: `UrlShortenerDAO`, `UrlMapper`
- Coverage: Business logic isolation
- Tests: 4 tests covering URL shortening and retrieval

```java
@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {
    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Mock
    private UrlShortenerDAO urlShortenerDAO;

    @Mock
    private UrlMapper urlMapper;

    @Test
    public void testShortenUrl_Success() {
        // Test auto-generated URL shortening
    }

    @Test
    public void testGetLongUrl_Success() {
        // Test URL retrieval
    }
}
```

**Custom Alias Service Tests** (`UrlShortenerServiceCustomAliasTest`)
- Tests: 3 tests covering custom alias functionality
- Scenarios: Success, duplicate alias, null alias

```java
@Test
public void testShortenUrl_WithCustomAlias_Success() {
    // Test custom alias creation
}

@Test
public void testShortenUrl_WithCustomAlias_AlreadyExists() {
    // Test duplicate alias handling
}
```

#### 2. Integration Tests

**Controller Tests** (`UrlShortenerControllerTest`)
- Framework: Spring Boot Test + MockMvc
- Annotation: `@ControllerTest` (custom annotation)
- Mocks: `@MockBean` for service layer
- Tests: 7 tests covering all controller endpoints

```java
@ControllerTest
public class UrlShortenerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    public void testShortenUrl_Success() throws Exception {
        // Test POST /shorten endpoint
    }

    @Test
    public void testGetLongUrl_Success() throws Exception {
        // Test POST /long endpoint
    }
}
```

**Custom Alias Controller Tests** (`UrlShortenerControllerCustomAliasTest`)
- Tests: 4 tests covering custom alias scenarios
- Scenarios: Success, duplicate alias, validation errors

**Welcome Controller Tests** (`WelcomeControllerTest`)
- Tests: 1 test for welcome page endpoint
- Validates HTML response

**Application Context Test** (`PrecisApplicationTests`)
- Validates Spring Boot application context loads successfully
- Uses H2 in-memory database
- Profile: `test`

#### 3. Custom Test Annotations

**@ControllerTest** - Composite annotation for controller tests:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public @interface ControllerTest {
}
```

#### 4. Test Scenarios Covered

| Test Case                                      | Class                                  | Type        | Purpose                          |
|------------------------------------------------|----------------------------------------|-------------|----------------------------------|
| `contextLoads()`                               | PrecisApplicationTests                 | Integration | Spring context initialization    |
| `testShortenUrl_Success()`                     | UrlShortenerControllerTest             | Controller  | Successful URL shortening        |
| `testShortenUrl_ValidationError()`             | UrlShortenerControllerTest             | Controller  | Validation error handling        |
| `testShortenUrl_ServiceException()`            | UrlShortenerControllerTest             | Controller  | Service exception handling       |
| `testGetLongUrl_Success()`                     | UrlShortenerControllerTest             | Controller  | Successful URL retrieval         |
| `testGetLongUrl_NotFound()`                    | UrlShortenerControllerTest             | Controller  | Not found error handling         |
| `testGetLongUrl_ValidationError()`             | UrlShortenerControllerTest             | Controller  | Validation error handling        |
| `testGetLongUrl_ServiceException()`            | UrlShortenerControllerTest             | Controller  | Service exception handling       |
| `testShortenUrl_WithCustomAlias_Success()`     | UrlShortenerControllerCustomAliasTest  | Controller  | Custom alias creation            |
| `testShortenUrl_WithCustomAlias_AlreadyExists()`| UrlShortenerControllerCustomAliasTest | Controller  | Duplicate alias handling         |
| `testShortenUrl_WithCustomAlias_ValidationError()`| UrlShortenerControllerCustomAliasTest| Controller  | Custom alias validation          |
| `testShortenUrl_WithCustomAlias_NullAlias()`   | UrlShortenerControllerCustomAliasTest  | Controller  | Null alias handling              |
| `testWelcomePage()`                            | WelcomeControllerTest                  | Controller  | Welcome page rendering           |
| `testShortenUrl_Success()`                     | UrlShortenerServiceTest                | Service     | Service layer shortening logic   |
| `testShortenUrl_NullLongUrl()`                 | UrlShortenerServiceTest                | Service     | Null URL handling                |
| `testGetLongUrl_Success()`                     | UrlShortenerServiceTest                | Service     | Service layer retrieval logic    |
| `testGetLongUrl_NotFound()`                    | UrlShortenerServiceTest                | Service     | Not found handling               |
| `testShortenUrl_WithCustomAlias_Success()`     | UrlShortenerServiceCustomAliasTest     | Service     | Custom alias service logic       |
| `testShortenUrl_WithCustomAlias_AlreadyExists()`| UrlShortenerServiceCustomAliasTest    | Service     | Duplicate alias service logic    |
| `testShortenUrl_WithCustomAlias_NullAlias()`   | UrlShortenerServiceCustomAliasTest     | Service     | Null alias service logic         |

### Test Database Configuration

**H2 In-Memory Database** for testing:

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

### Running Tests

```bash
# Run all tests
./gradlew test --configuration-cache

# Run with coverage report
./gradlew test jacocoTestReport --configuration-cache

# Run specific test class
./gradlew test --tests UrlShortenerControllerTest --configuration-cache
```

### Code Coverage Expectations

**Current**: Not measured
**Recommended Target**:
- **Line Coverage**: ≥ 80%
- **Branch Coverage**: ≥ 70%
- **Critical Paths**: 100% (URL shortening algorithm)

### Test Folder Structure

```
src/test/
├── java/
│   └── ind/shubhamn/precisrest/
│       ├── PrecisApplicationTests.java
│       ├── annotations/
│       │   └── ControllerTest.java
│       ├── rest/
│       │   ├── UrlShortenerControllerTest.java
│       │   ├── UrlShortenerControllerCustomAliasTest.java
│       │   └── WelcomeControllerTest.java
│       └── service/
│           ├── UrlShortenerServiceTest.java
│           └── UrlShortenerServiceCustomAliasTest.java
└── resources/
    └── application-test.yml
```

### Testing Recommendations

**Future Enhancements**:
1. **Contract Testing**: Pact for API contract validation
2. **Performance Testing**: JMeter/Gatling for load testing
3. **Mutation Testing**: PIT for test quality assessment
4. **E2E Testing**: TestContainers for full-stack integration tests

---

## 🔷 Build & Run Instructions

### Prerequisites

| Requirement | Version | Installation |
|-------------|---------|--------------|
| Java JDK    | 25+     | [OpenJDK](https://openjdk.org/) or [Microsoft Build of OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download) |
| Gradle      | 9.1+    | Included via Gradle Wrapper |
| PostgreSQL  | 18.1+   | [PostgreSQL](https://www.postgresql.org/download/) or Docker |
| Docker      | 20.10+  | [Docker Desktop](https://www.docker.com/products/docker-desktop) (optional) |

### Local Development Setup

#### 1. Clone Repository

```bash
git clone <repository-url>
cd precis-rest
```

#### 2. Start PostgreSQL Database

**Option A: Using Docker Compose (Recommended)**

```bash
docker-compose up -d
```

This starts PostgreSQL 17 with:
- Database: `precis`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

**Option B: Local PostgreSQL Installation**

```sql
CREATE DATABASE precis;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE precis TO postgres;
```

#### 3. Build Application

```bash
# Linux/macOS
./gradlew clean build --configuration-cache

# Windows
gradlew.bat clean build --configuration-cache
```

**Build Output**:
```
BUILD SUCCESSFUL in 14s
9 actionable tasks: 9 executed
```

#### 4. Run Application

**Option A: Using Gradle**

```bash
# Linux/macOS
./gradlew bootRun --configuration-cache

# Windows
gradlew.bat bootRun --configuration-cache
```

**Option B: Using JAR**

```bash
java -jar build/libs/precis-rest-0.0.1-SNAPSHOT.jar
```

**Option C: Using IDE**

Run `PrecisApplication.java` as a Spring Boot application

#### 5. Verify Application

```bash
# Health check (if actuator is enabled)
curl http://localhost:8080/actuator/health

# Test URL shortening
curl -X POST http://localhost:8080/app/rest/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com"}'
```

### Build Commands Reference

All commands use `--configuration-cache` for improved build performance (20-40% faster). See `GRADLE_CONFIGURATION_CACHE.md` for details.

```bash
# Clean build artifacts
./gradlew clean --configuration-cache

# Compile source code
./gradlew compileJava --configuration-cache

# Run tests
./gradlew test --configuration-cache

# Build without tests
./gradlew build -x test --configuration-cache

# Generate test coverage report
./gradlew jacocoTestReport --configuration-cache

# Check for dependency updates
./gradlew dependencyUpdates --configuration-cache

# View project dependencies
./gradlew dependencies --configuration-cache
```

**Performance Tip**: First build creates cache (~14s), subsequent builds are 30-40% faster (~8-10s)!

### Docker Build (Future Enhancement)

```dockerfile
# Dockerfile (example)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY build/libs/precis-rest-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build Docker image
docker build -t precis-rest:latest .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/precis \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  precis-rest:latest
```

### Troubleshooting

**Issue**: `Connection refused` to PostgreSQL

**Solution**:
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check PostgreSQL logs
docker logs precis-postgres

# Restart PostgreSQL
docker-compose restart postgres
```

**Issue**: `Port 8080 already in use`

**Solution**:
```bash
# Find process using port 8080
# Linux/macOS
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# Kill the process or change application port
# In application.yml:
server:
  port: 8081
```

**Issue**: `FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"`

**Solution**:
This issue occurs when the system's default timezone is not recognized by PostgreSQL 17+. The application automatically sets the JVM timezone to UTC at startup to prevent this error. If you still encounter this issue:

```bash
# Option 1: Run with explicit timezone (recommended - already implemented in code)
# The application sets TimeZone.setDefault(TimeZone.getTimeZone("UTC")) in main()

# Option 2: Set JVM timezone via command line
java -Duser.timezone=UTC -jar build/libs/precis-rest-0.0.1-SNAPSHOT.jar

# Option 3: Use PostgreSQL 16 or earlier (better timezone compatibility)
# Update docker-compose.yml: image: postgres:16
```

---

## 🔷 Deployment

### CI/CD Overview

**Recommended Pipeline**:

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Commit  │───▶│  Build   │───▶│   Test   │───▶│ Package  │───▶│  Deploy  │
│  Code    │    │  (Gradle)│    │ (JUnit)  │    │  (JAR)   │    │  (K8s)   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
```

**Tools**:
- **GitHub Actions** / **GitLab CI** / **Jenkins**
- **SonarQube**: Code quality analysis
- **Nexus/Artifactory**: Artifact repository
- **Kubernetes**: Container orchestration

### Environment Promotion Strategy

```
Development ──▶ Staging ──▶ Production
    (auto)        (manual)     (manual)
```

| Environment | Trigger      | Database        | Approval Required |
|-------------|--------------|-----------------|-------------------|
| Development | Auto on push | Dev PostgreSQL  | No                |
| Staging     | Manual       | Stage PostgreSQL| QA Sign-off       |
| Production  | Manual       | Prod PostgreSQL | Release Manager   |

### Deployment Strategies

#### Blue-Green Deployment

```
┌─────────────┐
│ Load Balancer│
└──────┬──────┘
       │
   ┌───┴───┐
   │       │
┌──▼──┐ ┌──▼──┐
│Blue │ │Green│
│(v1) │ │(v2) │
└─────┘ └─────┘
```

1. Deploy new version (Green)
2. Run smoke tests
3. Switch traffic to Green
4. Keep Blue for rollback

#### Rolling Deployment (Kubernetes)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: precis-rest
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
```

### Rollback Strategy

**Automated Rollback Triggers**:
- Health check failures
- Error rate > 5%
- Response time > 2s (p95)

**Manual Rollback**:
```bash
# Kubernetes
kubectl rollout undo deployment/precis-rest

# Docker
docker service update --rollback precis-rest
```

### Health Checks & Readiness Probes

**Spring Boot Actuator** (recommended addition):

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  health:
    db:
      enabled: true
```

**Kubernetes Probes**:

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

### Configuration Management

**Environment-Specific Configs**:
- **Development**: `application-dev.yml`
- **Staging**: `application-staging.yml`
- **Production**: `application-prod.yml`

**Secrets Management**:
- **Kubernetes Secrets**: For database credentials
- **HashiCorp Vault**: For sensitive configuration
- **AWS Secrets Manager**: For cloud deployments

---

## 🔷 Non-Functional Requirements

### Performance

| Metric                  | Target          | Current Status |
|-------------------------|-----------------|----------------|
| Response Time (p50)     | < 100ms         | Not measured   |
| Response Time (p95)     | < 500ms         | Not measured   |
| Response Time (p99)     | < 1s            | Not measured   |
| Throughput              | 1000 req/s      | Not measured   |
| Database Query Time     | < 50ms          | Not measured   |

**Optimization Strategies**:
- Database connection pooling (HikariCP - Spring Boot default)
- Index on `short_url` (primary key)
- Request-scoped controllers for thread safety
- Stateless design for horizontal scaling

### Scalability

**Horizontal Scaling**:
- ✅ Stateless application design
- ✅ Database-backed persistence
- ✅ Request-scoped controllers
- ⚠️ No distributed caching (future enhancement)

**Vertical Scaling**:
- JVM heap tuning: `-Xmx` and `-Xms` flags
- Connection pool sizing based on load

**Bottlenecks**:
- Database write operations (single PostgreSQL instance)
- No caching layer for frequently accessed URLs

### Availability

**Target**: 99.9% uptime (8.76 hours downtime/year)

**Strategies**:
- Multi-instance deployment (3+ replicas)
- Database replication (primary-replica setup)
- Health checks and auto-restart
- Circuit breaker pattern (future enhancement)

### Observability

**Metrics** (via Spring Boot Actuator + Micrometer):
- JVM metrics (heap, GC, threads)
- HTTP request metrics (count, duration)
- Database connection pool metrics
- Custom business metrics (URLs created, retrieved)

**Monitoring Stack** (recommended):
```
Application ──▶ Micrometer ──▶ Prometheus ──▶ Grafana
                                    │
                                    ▼
                              AlertManager
```

**Logging**:
- Structured JSON logging
- Centralized log aggregation (ELK/Splunk)
- Log retention: 30 days

**Tracing**:
- Distributed tracing with Spring Cloud Sleuth
- Trace visualization with Zipkin/Jaeger

### Fault Tolerance

**Current**:
- Basic exception handling
- Database connection retry (HikariCP default)

**Recommended**:
- **Circuit Breaker**: Resilience4j for database failures
- **Retry Logic**: Exponential backoff for transient failures
- **Bulkhead Pattern**: Isolate critical resources
- **Timeout Configuration**: Prevent cascading failures

```java
@CircuitBreaker(name = "database", fallbackMethod = "fallbackMethod")
public String getLongUrl(String shortUrl) {
    // Database operation
}
```

---

## 🔷 Assumptions & Design Decisions

### Key Assumptions

1. **Deterministic Hashing**: Same long URL always produces the same short URL
   - **Rationale**: Simplifies deduplication and ensures consistency
   - **Trade-off**: Potential hash collisions (mitigated by 8-character Base64)

2. **No URL Expiration**: Shortened URLs are permanent
   - **Rationale**: Simplifies initial implementation
   - **Trade-off**: Unbounded database growth

3. **No User Authentication**: Public API without access control
   - **Rationale**: Faster development and testing
   - **Trade-off**: Vulnerable to abuse and spam

4. **Single Database Instance**: No replication or sharding
   - **Rationale**: Sufficient for initial deployment
   - **Trade-off**: Single point of failure

### Design Decisions

#### 1. SHA-256 + Base64 for Short URL Generation

**Decision**: Use SHA-256 hash of long URL, encode with Base64, take first 8 characters

**Rationale**:
- **Deterministic**: Same input always produces same output
- **Collision Resistance**: SHA-256 provides strong collision resistance
- **URL-Safe**: Base64 URL encoding ensures safe characters
- **Fixed Length**: 8 characters provide 64^8 = 281 trillion combinations

**Trade-offs**:
- **Not Sequential**: Cannot predict next short URL
- **Potential Collisions**: Theoretical (extremely low probability)
- **No Custom Aliases**: Users cannot choose custom short URLs

**Alternative Considered**: Auto-incrementing ID + Base62 encoding
- **Pros**: Guaranteed uniqueness, sequential
- **Cons**: Predictable, requires distributed ID generation for scaling

#### 2. PostgreSQL as Primary Database

**Decision**: Use PostgreSQL 17 for persistence

**Rationale**:
- **ACID Compliance**: Ensures data consistency
- **Mature Ecosystem**: Well-supported by Spring Boot/Hibernate
- **Scalability**: Supports replication and partitioning
- **JSON Support**: Future-proof for metadata storage
- **Timezone Compatibility**: Better support for legacy timezone names (e.g., "Asia/Calcutta")

**Trade-offs**:
- **Write Bottleneck**: Single instance limits write throughput
- **Operational Overhead**: Requires database management

**Alternative Considered**: NoSQL (Redis, DynamoDB)
- **Pros**: Higher write throughput, built-in TTL
- **Cons**: Eventual consistency, less mature Spring integration

**Note**: PostgreSQL 18 was initially considered but downgraded to 17 due to stricter timezone validation that caused compatibility issues with legacy timezone identifiers.

#### 3. Layered Architecture

**Decision**: Separate Controller, Service, DAO, and Model layers

**Rationale**:
- **Separation of Concerns**: Clear responsibility boundaries
- **Testability**: Easy to mock dependencies
- **Maintainability**: Changes isolated to specific layers
- **Industry Standard**: Familiar to most Java developers

**Trade-offs**:
- **Boilerplate**: More classes and interfaces
- **Indirection**: Additional layers add complexity

#### 4. Request-Scoped Controllers

**Decision**: Use `@Scope(WebApplicationContext.SCOPE_REQUEST)`

**Rationale**:
- **Thread Safety**: Each request gets a new controller instance
- **Isolation**: Prevents state leakage between requests

**Trade-offs**:
- **Memory Overhead**: More object creation
- **Performance**: Slight overhead vs. singleton

#### 5. Hibernate Auto-DDL

**Decision**: Use `hibernate.hbm2ddl.auto=update` for schema management

**Rationale**:
- **Rapid Development**: Automatic schema creation
- **Simplicity**: No migration scripts needed initially

**Trade-offs**:
- **Production Risk**: Schema changes can be destructive
- **No Rollback**: Cannot revert schema changes
- **Recommended**: Migrate to Flyway/Liquibase for production

#### 6. UTC Timezone Enforcement

**Decision**: Set JVM default timezone to UTC at application startup

**Rationale**:
- **Database Compatibility**: Prevents timezone-related errors with PostgreSQL 17+
- **Consistency**: All timestamps stored and processed in UTC
- **Portability**: Application works regardless of system timezone
- **Best Practice**: Industry standard for server applications

**Implementation**:
```java
// In PrecisApplication.main()
TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
```

**Trade-offs**:
- **Local Time Conversion**: Clients must convert UTC to local time
- **Logging**: All logs use UTC timestamps

**Alternative Considered**: JDBC URL parameter `?TimeZone=UTC`
- **Pros**: Database-specific configuration
- **Cons**: Doesn't affect JVM-level operations, less comprehensive

---

## 🔷 Future Enhancements

### Planned Improvements

#### 1. Custom Short URL Aliases
**Priority**: High
**Description**: Allow users to specify custom short URLs (e.g., `/mylink`)

```java
POST /app/rest/shorten
{
  "longUrl": "https://example.com",
  "customAlias": "mylink"  // Optional
}
```

**Benefits**: Better branding, memorable links

#### 2. URL Analytics & Tracking
**Priority**: High
**Description**: Track click counts, geographic data, referrers

**Schema Addition**:
```sql
CREATE TABLE url_analytics (
  id BIGSERIAL PRIMARY KEY,
  short_url VARCHAR(8) REFERENCES url_shorten(short_url),
  clicked_at TIMESTAMP,
  ip_address VARCHAR(45),
  user_agent TEXT,
  referrer TEXT,
  country VARCHAR(2)
);
```

**Benefits**: Insights into URL usage, ROI measurement

#### 3. URL Expiration & TTL
**Priority**: Medium
**Description**: Support time-to-live for shortened URLs

**Schema Addition**:
```sql
ALTER TABLE url_shorten ADD COLUMN created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE url_shorten ADD COLUMN expires_at TIMESTAMP;
```

**Benefits**: Automatic cleanup, temporary campaigns

#### 4. Caching Layer
**Priority**: High
**Description**: Implement Redis cache for frequently accessed URLs

```java
@Cacheable(value = "urls", key = "#shortUrl")
public String getLongUrl(String shortUrl) {
    // Database lookup
}
```

**Benefits**: Reduced database load, faster response times

#### 5. API Versioning
**Priority**: Medium
**Description**: Version API endpoints for backward compatibility

```
/app/rest/v1/shorten
/app/rest/v2/shorten
```

**Benefits**: Safe API evolution, gradual migration

#### 6. Batch URL Shortening
**Priority**: Low
**Description**: Support bulk URL shortening in single request

```java
POST /app/rest/shorten/batch
{
  "urls": [
    "https://example1.com",
    "https://example2.com"
  ]
}
```

**Benefits**: Improved efficiency for bulk operations

#### 7. QR Code Generation
**Priority**: Low
**Description**: Generate QR codes for shortened URLs

```java
GET /app/rest/qr/{shortUrl}
```

**Benefits**: Mobile-friendly sharing

### Technical Debt Areas

1. **Security**: Enable Spring Security, implement authentication
2. **Error Handling**: Implement proper HTTP status codes (404 for not found)
3. **Input Validation**: Add JSR-303 validation annotations
4. **Database Migrations**: Migrate from Hibernate Auto-DDL to Flyway
5. **Observability**: Add Spring Boot Actuator and metrics
6. **Documentation**: Generate OpenAPI/Swagger documentation
7. **Testing**: Increase test coverage to 80%+
8. **Logging**: Implement structured logging with correlation IDs

### Scalability Roadmap

**Phase 1: Vertical Scaling** (Current)
- Single instance, single database
- Target: 100 req/s

**Phase 2: Horizontal Scaling**
- Multiple application instances behind load balancer
- Database connection pooling optimization
- Target: 1,000 req/s

**Phase 3: Distributed Architecture**
- Redis caching layer
- Database read replicas
- CDN for static content
- Target: 10,000 req/s

**Phase 4: Global Distribution**
- Multi-region deployment
- Database sharding by geographic region
- Edge caching with CloudFlare/Fastly
- Target: 100,000+ req/s

---

## 📚 Additional Resources

### Documentation
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)

### Related Projects
- [Bitly](https://bitly.com/) - Commercial URL shortener
- [TinyURL](https://tinyurl.com/) - Simple URL shortener
- [YOURLS](https://yourls.org/) - Open-source URL shortener

### Contributing
Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### License
This project is licensed under the MIT License - see the LICENSE file for details.

### Contact
For questions or support, please contact:
- **Email**: [your-email@example.com]
- **GitHub Issues**: [repository-url/issues]

---

**Last Updated**: January 2026
**Version**: 0.0.1-SNAPSHOT
**Maintained By**: Shubham N.
