# Docker Configuration

This directory contains Docker configuration files for the IT Ticket Management System.

## Directory Structure

```
docker/
├── postgres/
│   └── init/
│       └── 01-init.sql          # PostgreSQL initialization script
├── pgadmin/
│   └── servers.json             # pgAdmin pre-configured server
└── README.md
```

## PostgreSQL Initialization

The `postgres/init/01-init.sql` script automatically runs when the PostgreSQL container is first created. It:

- Creates the `uuid-ossp` extension for UUID generation
- Creates the `pg_trgm` extension for full-text search
- Grants necessary privileges to the database user
- Logs initialization success

## pgAdmin Configuration

The `pgadmin/servers.json` file pre-configures the connection to PostgreSQL, so you don't need to manually add the server in pgAdmin.

### Pre-configured Settings:
- **Server Name**: Ticket System Database
- **Host**: postgres (Docker service name)
- **Port**: 5432
- **Database**: ticketdb
- **Username**: ticketuser

**Note**: The password is NOT stored in servers.json for security. You'll be prompted to enter it on first connection.

## Quick Start

From the project root directory:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v
```

## Connecting to Services

### PostgreSQL
- **Host**: localhost
- **Port**: 5432
- **Database**: ticketdb
- **Username**: ticketuser
- **Password**: ticketpass

**Connection String**:
```
jdbc:postgresql://localhost:5432/ticketdb
```

### pgAdmin
- **URL**: http://localhost:5050
- **Email**: admin@admin.com
- **Password**: admin

## Health Checks

The PostgreSQL container includes a health check that runs every 10 seconds. The pgAdmin service waits for PostgreSQL to be healthy before starting.

Check health status:
```bash
docker-compose ps
```

## Data Persistence

Data is persisted using Docker volumes:
- `ticketdb_postgres_data` - PostgreSQL data
- `ticketdb_pgadmin_data` - pgAdmin configuration

These volumes survive container restarts.

## Troubleshooting

### PostgreSQL won't start
```bash
# Check logs
docker-compose logs postgres

# Ensure port 5432 is not in use
netstat -ano | findstr :5432  # Windows
lsof -i :5432                 # Linux/Mac
```

### pgAdmin can't connect
1. Ensure PostgreSQL is healthy: `docker-compose ps`
2. Use password: `ticketpass`
3. Host must be `postgres` (not localhost) inside Docker network

### Reset everything
```bash
# Stop and remove containers, networks, and volumes
docker-compose down -v

# Remove images (optional)
docker-compose down -v --rmi all

# Start fresh
docker-compose up -d
```

## Security Notes

⚠️ **Development Only**: The default credentials are for local development only. Never use these in production!

For production:
- Use strong, randomly generated passwords
- Store credentials in environment variables or secrets management
- Enable SSL/TLS connections
- Restrict network access
- Use Docker secrets instead of environment variables
