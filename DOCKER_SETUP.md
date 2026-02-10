# Docker Setup Guide

Quick guide to get the development environment up and running with Docker.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose v3.8+ (included with Docker Desktop)

## Quick Start

### 1. Create Environment File

```bash
# Copy the example environment file
cp .env.example .env

# (Optional) Edit .env with your preferred values
# The defaults work fine for local development
```

### 2. Start the Services

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# View logs (optional)
docker-compose logs -f
```

### 3. Verify Services are Running

```bash
# Check service status
docker-compose ps

# Should show:
# ticketdb-postgres  running  healthy
# ticketdb-pgadmin   running
```

### 4. Access pgAdmin

1. Open browser: http://localhost:5050
2. Login:
   - Email: `admin@admin.com`
   - Password: `admin`
3. The server "Ticket System Database" should be pre-configured
4. Click on it and enter password: `ticketpass`

### 5. Run Spring Boot Application

Now you can start the Spring Boot backend:

```bash
cd ticket-system-backend

# Using Maven
mvn spring-boot:run

# Or if you have Maven wrapper
./mvnw spring-boot:run
```

The application will connect to PostgreSQL at `localhost:5432`.

## Docker Commands Reference

### Start Services
```bash
docker-compose up -d              # Start in background
docker-compose up                 # Start with logs in foreground
```

### Stop Services
```bash
docker-compose stop               # Stop containers
docker-compose down               # Stop and remove containers
docker-compose down -v            # Stop, remove containers and volumes (⚠️ deletes data)
```

### View Logs
```bash
docker-compose logs               # All logs
docker-compose logs -f            # Follow logs
docker-compose logs postgres      # PostgreSQL logs only
docker-compose logs pgadmin       # pgAdmin logs only
```

### Restart Services
```bash
docker-compose restart            # Restart all services
docker-compose restart postgres   # Restart PostgreSQL only
```

### Check Status
```bash
docker-compose ps                 # Service status
docker ps                         # All running containers
```

## Database Connection Details

### From Spring Boot Application
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketdb
    username: ticketuser
    password: ticketpass
```

### From Database Client (DBeaver, DataGrip, etc.)
- **Host**: localhost
- **Port**: 5432
- **Database**: ticketdb
- **Username**: ticketuser
- **Password**: ticketpass

### From pgAdmin
Already configured! Just login and enter the database password.

## Troubleshooting

### Port Already in Use

If you get a port conflict error:

```bash
# Windows - Check what's using port 5432
netstat -ano | findstr :5432

# Linux/Mac
lsof -i :5432

# Change port in .env file
POSTGRES_PORT=5433
PGADMIN_PORT=5051

# Restart
docker-compose down
docker-compose up -d
```

### PostgreSQL Not Ready

If Spring Boot can't connect:

```bash
# Check PostgreSQL health
docker-compose ps

# Wait for "healthy" status
# Or check logs
docker-compose logs postgres
```

### Reset Database

To start with a clean database:

```bash
# WARNING: This deletes all data!
docker-compose down -v
docker-compose up -d
```

### pgAdmin Can't Connect to PostgreSQL

1. Ensure both containers are running: `docker-compose ps`
2. Use `ticketpass` as the password
3. The host inside Docker is `postgres`, not `localhost`
4. Check PostgreSQL logs: `docker-compose logs postgres`

## Data Persistence

Your database data is stored in Docker volumes:

```bash
# List volumes
docker volume ls | grep ticketdb

# Inspect volume
docker volume inspect ticketdb_postgres_data

# Backup database
docker exec ticketdb-postgres pg_dump -U ticketuser ticketdb > backup.sql

# Restore database
docker exec -i ticketdb-postgres psql -U ticketuser ticketdb < backup.sql
```

## Clean Up Everything

To completely remove all containers, networks, and volumes:

```bash
docker-compose down -v --rmi all --remove-orphans
```

## Next Steps

1. ✅ PostgreSQL and pgAdmin are running
2. 🔄 Start the Spring Boot backend
3. 📋 Run Flyway migrations (automatic on first run)
4. 🧪 Create test data
5. 🚀 Start frontend development

## Support

For issues with Docker setup, check:
- Docker logs: `docker-compose logs`
- Docker status: `docker-compose ps`
- [docker/README.md](docker/README.md) for detailed configuration
