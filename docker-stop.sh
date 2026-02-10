#!/bin/bash

# IT Ticket Management System - Docker Stop Script
# Linux/Mac Shell Script

echo "============================================"
echo "IT Ticket Management System"
echo "Stopping Docker Services..."
echo "============================================"
echo ""

docker-compose down

echo ""
echo "============================================"
echo "Services Stopped Successfully!"
echo "============================================"
echo ""
echo "Note: Data is preserved in Docker volumes."
echo "To remove data, use: docker-compose down -v"
echo ""
