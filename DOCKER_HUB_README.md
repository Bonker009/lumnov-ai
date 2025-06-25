# RentHouse Docker Hub Images

This project's Docker images are now available on Docker Hub under the `seyha2023` namespace.

## Available Images

- **Backend API**: `seyha2023/renthouse-backend:latest`
- **Frontend UI**: `seyha2023/renthouse-frontend:latest`

## Quick Start

### Option 1: Using Docker Compose (Recommended)

1. Clone this repository
2. Navigate to the project directory
3. Run the services:

```bash
# Start all services
./run-docker.sh start

# Or use docker-compose directly
docker-compose up -d
```

### Option 2: Using Docker Hub Images Directly

```bash
# Pull the images
docker pull seyha2023/renthouse-backend:latest
docker pull seyha2023/renthouse-frontend:latest

# Run the backend
docker run -d \
  --name renthouse-backend \
  -p 9024:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/lumnov \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=1234 \
  seyha2023/renthouse-backend:latest

# Run the frontend
docker run -d \
  --name renthouse-frontend \
  -p 9025:3000 \
  -e NEXT_PUBLIC_API_URL=http://your-backend-host:9024 \
  seyha2023/renthouse-frontend:latest
```

## Port Configuration

- **Frontend**: http://localhost:9025
- **Backend API**: http://localhost:9024
- **Swagger UI**: http://localhost:9024/swagger-ui.html
- **Database**: localhost:9023

## Environment Variables

### Backend
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Database schema update mode

### Frontend
- `NEXT_PUBLIC_API_URL`: Backend API URL

## Docker Hub Repository

- **Backend**: https://hub.docker.com/r/seyha2023/renthouse-backend
- **Frontend**: https://hub.docker.com/r/seyha2023/renthouse-frontend

## Building and Pushing New Versions

To build and push new versions of the images:

```bash
# Build the images
docker-compose build

# Tag the images
docker tag lumnov-ai-integrated-backend:latest seyha2023/renthouse-backend:latest
docker tag lumnov-ai-integrated-frontend:latest seyha2023/renthouse-frontend:latest

# Push to Docker Hub
docker push seyha2023/renthouse-backend:latest
docker push seyha2023/renthouse-frontend:latest
```

## Production Deployment

For production deployment, use the `docker-compose.prod.yml` file which uses the Docker Hub images:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Support

For issues or questions, please check the main project documentation or create an issue in the repository. 