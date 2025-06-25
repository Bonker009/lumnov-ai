@echo off
setlocal enabledelayedexpansion

REM RentHouse Docker Runner Script for Windows

echo 🏠 RentHouse Docker Setup
echo ==========================

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo ✅ Docker is running

REM Function to show usage
:show_usage
if "%1"=="help" goto usage
if "%1"=="--help" goto usage
if "%1"=="-h" goto usage
goto :eof

:usage
echo Usage: %0 [COMMAND]
echo.
echo Commands:
echo   start     - Start all services
echo   stop      - Stop all services
echo   restart   - Restart all services
echo   build     - Build all services
echo   logs      - Show logs
echo   clean     - Stop and remove everything
echo   status    - Show service status
echo.
echo Examples:
echo   %0 start
echo   %0 logs backend
echo   %0 clean
goto :eof

REM Function to start services
:start_services
echo 🚀 Starting RentHouse services...
docker-compose up -d
echo.
echo ✅ Services started successfully!
echo.
echo 🌐 Access your applications:
echo    Frontend: http://localhost:3000
echo    Backend API: http://localhost:8080
echo    Swagger UI: http://localhost:8080/swagger-ui.html
echo    Database: localhost:5432
goto :eof

REM Function to stop services
:stop_services
echo 🛑 Stopping RentHouse services...
docker-compose down
echo ✅ Services stopped
goto :eof

REM Function to restart services
:restart_services
echo 🔄 Restarting RentHouse services...
docker-compose down
docker-compose up -d
echo ✅ Services restarted
goto :eof

REM Function to build services
:build_services
echo 🔨 Building RentHouse services...
docker-compose build --no-cache
echo ✅ Services built successfully
goto :eof

REM Function to show logs
:show_logs
if "%2"=="" (
    echo 📋 Showing logs for all services...
    docker-compose logs
) else (
    echo 📋 Showing logs for %2...
    docker-compose logs %2
)
goto :eof

REM Function to clean everything
:clean_everything
echo 🧹 Cleaning up everything...
docker-compose down -v
docker system prune -f
echo ✅ Cleanup completed
goto :eof

REM Function to show status
:show_status
echo 📊 Service Status:
docker-compose ps
goto :eof

REM Main script logic
if "%1"=="" goto start_services
if "%1"=="start" goto start_services
if "%1"=="stop" goto stop_services
if "%1"=="restart" goto restart_services
if "%1"=="build" goto build_services
if "%1"=="logs" goto show_logs
if "%1"=="clean" goto clean_everything
if "%1"=="status" goto show_status
if "%1"=="help" goto usage
if "%1"=="--help" goto usage
if "%1"=="-h" goto usage

echo ❌ Unknown command: %1
echo.
goto usage 