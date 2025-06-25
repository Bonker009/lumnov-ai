#!/bin/bash

# RentHouse Docker Runner Script

echo "🏠 RentHouse Docker Setup"
echo "=========================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop first."
    exit 1
fi

echo "✅ Docker is running"

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start     - Start all services"
    echo "  stop      - Stop all services"
    echo "  restart   - Restart all services"
    echo "  build     - Build all services"
    echo "  pull      - Pull latest images from Docker Hub"
    echo "  logs      - Show logs"
    echo "  clean     - Stop and remove everything"
    echo "  status    - Show service status"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 pull"
    echo "  $0 logs backend"
    echo "  $0 clean"
}

# Function to start services
start_services() {
    echo "🚀 Starting RentHouse services..."
    docker-compose up -d
    echo ""
    echo "✅ Services started successfully!"
    echo ""
    echo "🌐 Access your applications:"
    echo "   Frontend: http://localhost:9025"
    echo "   Backend API: http://localhost:9024"
    echo "   Swagger UI: http://localhost:9024/swagger-ui.html"
    echo "   Database: localhost:9023"
}

# Function to stop services
stop_services() {
    echo "🛑 Stopping RentHouse services..."
    docker-compose down
    echo "✅ Services stopped"
}

# Function to restart services
restart_services() {
    echo "🔄 Restarting RentHouse services..."
    docker-compose down
    docker-compose up -d
    echo "✅ Services restarted"
}

# Function to build services
build_services() {
    echo "🔨 Building RentHouse services..."
    docker-compose build --no-cache
    echo "✅ Services built successfully"
}

# Function to pull latest images
pull_images() {
    echo "📥 Pulling latest images from Docker Hub..."
    echo "Pulling backend image..."
    docker pull seyha2023/renthouse-backend:latest
    echo "Pulling frontend image..."
    docker pull seyha2023/renthouse-frontend:latest
    echo "✅ Images pulled successfully"
}

# Function to show logs
show_logs() {
    if [ -z "$1" ]; then
        echo "📋 Showing logs for all services..."
        docker-compose logs
    else
        echo "📋 Showing logs for $1..."
        docker-compose logs "$1"
    fi
}

# Function to clean everything
clean_everything() {
    echo "🧹 Cleaning up everything..."
    docker-compose down -v
    docker system prune -f
    echo "✅ Cleanup completed"
}

# Function to show status
show_status() {
    echo "📊 Service Status (Ports):"
    echo "   Frontend: http://localhost:9025"
    echo "   Backend API: http://localhost:9024"
    echo "   Database: localhost:9023"
    docker-compose ps
}

# Main script logic
case "${1:-start}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    build)
        build_services
        ;;
    pull)
        pull_images
        ;;
    logs)
        show_logs "$2"
        ;;
    clean)
        clean_everything
        ;;
    status)
        show_status
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        echo "❌ Unknown command: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac 