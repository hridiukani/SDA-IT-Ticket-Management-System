-- ============================================
-- IT Ticket Management System
-- PostgreSQL Initialization Script
-- ============================================

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create extension for full-text search
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Display connection info
SELECT version();

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE ticketdb TO ticketuser;

-- Create schema if needed (optional)
-- CREATE SCHEMA IF NOT EXISTS ticket_system;

-- Log success
DO $$
BEGIN
  RAISE NOTICE '✓ Database initialized successfully';
  RAISE NOTICE '✓ Extensions created: uuid-ossp, pg_trgm';
  RAISE NOTICE '✓ Ready for Flyway migrations';
END $$;
