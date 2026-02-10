#!/bin/bash

# IT Ticket Management System - Docker Start Script
# Linux/Mac Shell Script

echo "============================================"
echo "IT Ticket Management System"
echo "Starting Docker Services..."
echo "============================================"
echo ""

# Check if .env exists
if [ ! -f ".env" ]; then
    echo "[INFO] .env file not found. Creating from .env.example..."
    cp .env.example .env
    echo "[SUCCESS] Created .env file. Please review and update if needed."
    echo ""
fi

# Start Docker Compose
echo "[INFO] Starting PostgreSQL and pgAdmin..."
docker-compose up -d

# Wait a moment
sleep 5

# Check status
echo ""
echo "[INFO] Checking service status..."
docker-compose ps

echo ""
echo "============================================"
echo "Services Started Successfully!"
echo "============================================"
echo ""
echo "PostgreSQL:"
echo "  URL: postgresql://localhost:5432/ticketdb"
echo "  Username: ticketuser"
echo "  Password: ticketpass"
echo ""
echo "pgAdmin:"
echo "  URL: http://localhost:5050"
echo "  Email: admin@admin.com"
echo "  Password: admin"
echo ""
echo "============================================"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
echo ""
