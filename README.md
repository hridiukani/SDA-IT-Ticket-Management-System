# IT Office Ticket Management System

A comprehensive enterprise-grade IT ticket management system built with Spring Boot and modern web technologies for Sun Devil Athletics.

## 🚀 Features

### Core Functionality
- **User Management**: Role-based access control (Admin, Agent, User)
- **Ticket Management**: Create, assign, track, and resolve IT support tickets
- **Department & Categories**: Organize tickets by department and category
- **Priority & SLA**: Automatic SLA tracking based on priority levels
- **Email Notifications**: Automated email notifications for ticket updates
- **File Attachments**: Upload and manage ticket-related documents
- **Audit Logging**: Complete audit trail for all ticket activities
- **Search & Filters**: Advanced search and filtering capabilities

### Technical Features
- RESTful API with OpenAPI/Swagger documentation
- JWT-based authentication
- PostgreSQL database with Flyway migrations
- Docker-based development environment
- Comprehensive error handling
- Data validation
- Caching support
- Health checks and metrics

## 🏗️ Architecture

```
SDA-IT-Ticket-Management-System/
├── ticket-system-backend/       # Spring Boot REST API
├── ticket-system-frontend/      # React/Angular frontend (TBD)
├── docker/                      # Docker configuration files
│   ├── postgres/                # PostgreSQL init scripts
│   └── pgadmin/                 # pgAdmin configuration
├── docker-compose.yml           # Docker services configuration
├── .env.example                 # Environment variables template
└── DOCKER_SETUP.md             # Docker setup guide
```

## 🛠️ Tech Stack

### Backend
- **Java**: 21
- **Spring Boot**: 3.2.2
- **Spring Security**: JWT authentication
- **Spring Data JPA**: Database access
- **PostgreSQL**: 15
- **Flyway**: Database migrations
- **Maven**: Build tool
- **Lombok**: Code generation
- **MapStruct**: DTO mapping
- **SpringDoc**: OpenAPI/Swagger

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **pgAdmin**: Database management

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker Desktop (for local development)
- Git

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd SDA-IT-Ticket-Management-System
```

### 2. Start Docker Services

**Windows:**
```cmd
docker-start.bat
```

**Linux/Mac:**
```bash
./docker-start.sh
```

Or manually:
```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- pgAdmin on port 5050

### 3. Configure Environment

```bash
# Copy environment template
cp .env.example .env

# Edit .env with your configuration (optional for development)
```

### 4. Run the Backend

```bash
cd ticket-system-backend
mvn spring-boot:run
```

The API will be available at: http://localhost:8080

### 5. Access Services

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **pgAdmin**: http://localhost:5050 (admin@admin.com / admin)
- **Health Check**: http://localhost:8080/actuator/health

## 📚 Documentation

- [Backend Documentation](ticket-system-backend/README.md)
- [Docker Setup Guide](DOCKER_SETUP.md)
- [Docker Configuration](docker/README.md)

## 🗄️ Database

### Connection Details (Development)

```
Host: localhost
Port: 5432
Database: ticketdb
Username: ticketuser
Password: ticketpass
```

### pgAdmin Access

1. Open http://localhost:5050
2. Login: admin@admin.com / admin
3. Server is pre-configured as "Ticket System Database"
4. Enter password: ticketpass

## 🔧 Development

### Project Structure

```
ticket-system-backend/
├── src/main/java/com/itoffice/ticketsystem/
│   ├── config/          # Spring configurations
│   ├── controller/      # REST controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── repository/      # Spring Data repositories
│   ├── service/         # Business logic
│   ├── security/        # Security & JWT
│   ├── exception/       # Exception handling
│   ├── mapper/          # DTO mappers
│   └── util/            # Utility classes
└── src/main/resources/
    ├── application.yml          # Base configuration
    ├── application-dev.yml      # Development config
    ├── application-prod.yml     # Production config
    └── db/migration/            # Flyway migrations
```

### Available Scripts

```bash
# Backend
cd ticket-system-backend
mvn clean install          # Build
mvn spring-boot:run        # Run application
mvn test                   # Run tests

# Docker
docker-compose up -d       # Start services
docker-compose down        # Stop services
docker-compose logs -f     # View logs
docker-compose ps          # Check status
```

## 🧪 API Testing

### Using Swagger UI

Navigate to http://localhost:8080/swagger-ui.html to:
- View all available endpoints
- Test API endpoints interactively
- See request/response schemas

### Using cURL

```bash
# Health check
curl http://localhost:8080/actuator/health

# Example: Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 🔐 Security

- JWT-based authentication
- BCrypt password encryption
- Role-based authorization
- CORS configuration
- SQL injection protection
- XSS protection

**Default Roles:**
- `ADMIN`: Full system access
- `AGENT`: Manage assigned tickets
- `USER`: Create and view own tickets

## 📊 Monitoring

- **Actuator Endpoints**: http://localhost:8080/actuator
- **Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## 🚢 Deployment

### Production Checklist

- [ ] Set strong `JWT_SECRET` (min 256 bits)
- [ ] Configure production database credentials
- [ ] Set up SMTP for email notifications
- [ ] Configure CORS for production domain
- [ ] Enable HTTPS/SSL
- [ ] Set `SPRING_PROFILE=prod`
- [ ] Configure backup strategy
- [ ] Set up monitoring and logging
- [ ] Review security settings
- [ ] Test database migrations

### Environment Variables

See [.env.example](.env.example) for all required environment variables.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is proprietary software developed for IT Office use at Sun Devil Athletics.

## 👥 Team

IT Office - Sun Devil Athletics

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check documentation in `/docs`

## 🗺️ Roadmap

- [x] Project setup and structure
- [x] Docker development environment
- [ ] Database schema and migrations
- [ ] Entity models
- [ ] Authentication & authorization
- [ ] Core ticket management APIs
- [ ] Email notifications
- [ ] File upload functionality
- [ ] Frontend application
- [ ] Unit and integration tests
- [ ] API documentation
- [ ] Deployment configuration

## 📅 Version History

- **v1.0.0** (Current)
  - Initial project setup
  - Docker environment configuration
  - Spring Boot application structure
  - Database configuration

---

**Built with ❤️ by the IT Office Team - Sun Devil Athletics**
