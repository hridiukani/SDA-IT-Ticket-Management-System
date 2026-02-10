# IT Office Ticket Management System - Backend

A comprehensive enterprise-grade ticket management system built with Spring Boot 3.2.2 and Java 21.

## Features

- User Authentication & Authorization (JWT-based)
- Role-Based Access Control (Admin, Agent, User)
- Ticket Lifecycle Management
- Department & Category Organization
- Email Notifications
- File Attachments Support
- SLA Tracking
- Audit Logging
- RESTful API with OpenAPI/Swagger Documentation

## Tech Stack

- **Java**: 21
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA + Hibernate
- **Migration**: Flyway
- **Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven
- **Additional**: Lombok, MapStruct, Jackson

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 14+
- SMTP server (for email notifications)

## Project Structure

```
ticket-system-backend/
├── src/
│   ├── main/
│   │   ├── java/com/itoffice/ticketsystem/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Spring Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   │   └── impl/        # Service implementations
│   │   │   ├── security/        # Security configurations
│   │   │   │   ├── jwt/         # JWT utilities
│   │   │   │   └── filter/      # Security filters
│   │   │   ├── exception/       # Custom exceptions & handlers
│   │   │   ├── mapper/          # DTO mappers
│   │   │   ├── util/            # Utility classes
│   │   │   └── audit/           # Audit configurations
│   │   └── resources/
│   │       ├── db/migration/    # Flyway migrations
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/                    # Test classes
├── uploads/                     # File upload directory
└── pom.xml
```

## Getting Started

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE ticket_system_dev;
```

### 2. Configuration

Update `src/main/resources/application-dev.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticket_system_dev
    username: your_username
    password: your_password
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Environment Variables

### Development
- `DB_USERNAME`: Database username (default: postgres)
- `DB_PASSWORD`: Database password (default: postgres)
- `MAIL_HOST`: SMTP host (default: smtp.gmail.com)
- `MAIL_USERNAME`: Email username
- `MAIL_PASSWORD`: Email password

### Production
- `DATABASE_URL`: Full database URL
- `DB_USERNAME`: Database username (required)
- `DB_PASSWORD`: Database password (required)
- `JWT_SECRET`: JWT secret key (required, min 256 bits)
- `JWT_EXPIRATION`: Token expiration time in ms
- `CORS_ALLOWED_ORIGINS`: Allowed CORS origins
- `MAIL_HOST`: SMTP host (required)
- `MAIL_USERNAME`: Email username (required)
- `MAIL_PASSWORD`: Email password (required)

## API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Endpoints

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## Security

- JWT-based authentication
- Role-based authorization (ADMIN, AGENT, USER)
- Password encryption using BCrypt
- CORS configuration
- SQL injection protection via JPA

## Development

### Profiles

- `dev`: Development profile with debug logging
- `prod`: Production profile with optimized settings

### Testing

```bash
mvn test
```

## Database Migrations

Flyway manages database migrations. Migration scripts are located in:
`src/main/resources/db/migration/`

Naming convention: `V{version}__{description}.sql`

Example: `V1__Initial_schema.sql`

## License

Proprietary - IT Office

## Version

1.0.0
