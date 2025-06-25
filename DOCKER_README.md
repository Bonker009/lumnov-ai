# Docker Setup for RentHouse Application

This document provides instructions for running the RentHouse application using Docker and Docker Compose.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)
- At least 4GB of available RAM

## Technology Stack

- **Backend**: Spring Boot 3.2.0 with Java 21
- **Frontend**: Next.js 15.3.4 with Node.js 20
- **Database**: PostgreSQL 15
- **Container Runtime**: Docker with Docker Compose

## Project Structure

```
lumnov-ai-integrated/
├── house-renting/          # Spring Boot Backend (Java 21)
│   ├── Dockerfile
│   ├── .dockerignore
│   └── ...
├── renthouse-ui/           # Next.js Frontend (Node.js 20)
│   ├── Dockerfile
│   ├── .dockerignore
│   └── ...
├── docker-compose.yml      # Main orchestration file
├── .dockerignore
└── DOCKER_README.md        # This file
```

## Quick Start

1. **Clone and navigate to the project directory:**
   ```bash
   cd lumnov-ai-integrated
   ```

2. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the applications:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - PostgreSQL: localhost:5432

## Services

### 1. PostgreSQL Database
- **Container:** `renthouse-postgres`
- **Port:** 5432
- **Database:** lumnov
- **Username:** postgres
- **Password:** 1234
- **Volume:** `postgres_data` (persistent data storage)

### 2. Spring Boot Backend
- **Container:** `renthouse-backend`
- **Port:** 8080
- **Features:**
  - RESTful API
  - JWT Authentication
  - File upload handling
  - Swagger documentation
- **Volume:** `./house-renting/uploads` (file uploads)

### 3. Next.js Frontend
- **Container:** `renthouse-frontend`
- **Port:** 3000
- **Features:**
  - Modern React UI
  - Responsive design
  - User and Owner dashboards

## Docker Commands

### Start Services
```bash
# Start all services in background
docker-compose up -d

# Start with build (force rebuild)
docker-compose up --build

# Start specific service
docker-compose up backend
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop specific service
docker-compose stop frontend
```

### View Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs
docker-compose logs -f backend
```

### Rebuild Services
```bash
# Rebuild specific service
docker-compose build backend

# Rebuild all services
docker-compose build --no-cache
```

### Access Containers
```bash
# Access backend container
docker-compose exec backend bash

# Access frontend container
docker-compose exec frontend sh

# Access database
docker-compose exec postgres psql -U postgres -d lumnov
```

## Environment Variables

### Backend Environment Variables
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Hibernate DDL mode

### Frontend Environment Variables
- `NEXT_PUBLIC_API_URL`: Backend API URL

## Volumes

- `postgres_data`: PostgreSQL data persistence
- `./house-renting/uploads`: File upload storage

## Networks

All services are connected through the `renthouse-network` bridge network.

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Check what's using the port
   netstat -ano | findstr :8080
   
   # Kill the process or change port in docker-compose.yml
   ```

2. **Database connection issues:**
   ```bash
   # Check if PostgreSQL is running
   docker-compose ps postgres
   
   # Check PostgreSQL logs
   docker-compose logs postgres
   ```

3. **Build failures:**
   ```bash
   # Clean and rebuild
   docker-compose down
   docker system prune -f
   docker-compose up --build
   ```

4. **Memory issues:**
   - Increase Docker Desktop memory limit
   - Close unnecessary applications

### Reset Everything
```bash
# Stop all containers and remove volumes
docker-compose down -v

# Remove all images
docker system prune -a

# Rebuild from scratch
docker-compose up --build
```

## Development Workflow

### Backend Development
1. Make changes to Java files in `house-renting/src/`