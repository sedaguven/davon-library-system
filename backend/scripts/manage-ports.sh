#!/bin/bash

# Davon Library System - Port Management Script
# This script helps manage port conflicts during development

echo "🔧 Davon Library System - Port Management"
echo "=========================================="

# Function to check if a port is in use
check_port() {
    local port=$1
    local process=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$process" ]; then
        echo "❌ Port $port is in use by process(es): $process"
        return 1
    else
        echo "✅ Port $port is available"
        return 0
    fi
}

# Function to kill processes on a port
kill_port() {
    local port=$1
    local process=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$process" ]; then
        echo "🔄 Killing process(es) on port $port: $process"
        echo $process | xargs kill -9
        sleep 2
        check_port $port
    else
        echo "✅ Port $port is already free"
    fi
}

# Function to show all relevant ports
show_ports() {
    echo ""
    echo "📊 Current Port Status:"
    echo "======================="
    check_port 8080  # Default Quarkus
    check_port 8081  # MSSQL profile
    check_port 5005  # Debug port
    check_port 1433  # MSSQL database
}

# Function to clean all ports
clean_all() {
    echo ""
    echo "🧹 Cleaning all ports..."
    echo "======================="
    kill_port 8080
    kill_port 8081
    kill_port 5005
    echo ""
    echo "✅ All ports cleaned!"
}

# Function to start application with specific profile
start_app() {
    local profile=$1
    local port=$2
    
    echo ""
    echo "🚀 Starting application with profile: $profile"
    echo "=============================================="
    
    # Check if port is available
    if ! check_port $port; then
        echo "⚠️  Port $port is in use. Do you want to kill the process? (y/n)"
        read -r response
        if [[ "$response" =~ ^[Yy]$ ]]; then
            kill_port $port
        else
            echo "❌ Cannot start application. Port $port is still in use."
            exit 1
        fi
    fi
    
    echo "✅ Starting application on port $port..."
    case $profile in
        "dev")
            ./mvnw quarkus:dev
            ;;
        "mssql")
            ./mvnw quarkus:dev -Dquarkus.profile=mssql
            ;;
        "prod")
            ./mvnw quarkus:dev -Dquarkus.profile=prod
            ;;
        *)
            echo "❌ Unknown profile: $profile"
            echo "Available profiles: dev, mssql, prod"
            exit 1
            ;;
    esac
}

# Main script logic
case "${1:-help}" in
    "check"|"status")
        show_ports
        ;;
    "clean"|"kill")
        clean_all
        ;;
    "start-dev")
        start_app "dev" 8080
        ;;
    "start-mssql")
        start_app "mssql" 8081
        ;;
    "start-prod")
        start_app "prod" 8080
        ;;
    "test")
        echo ""
        echo "🧪 Running tests..."
        echo "=================="
        clean_all
        ./mvnw test
        ;;
    "help"|*)
        echo ""
        echo "📖 Usage: $0 [command]"
        echo "======================"
        echo ""
        echo "Commands:"
        echo "  check, status    - Show current port status"
        echo "  clean, kill      - Kill all processes on relevant ports"
        echo "  start-dev        - Start application in dev mode (port 8080)"
        echo "  start-mssql      - Start application with MSSQL profile (port 8081)"
        echo "  start-prod       - Start application in production mode (port 8080)"
        echo "  test             - Clean ports and run tests"
        echo "  help             - Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0 check         # Check which ports are in use"
        echo "  $0 clean         # Kill all processes on ports 8080, 8081, 5005"
        echo "  $0 start-mssql   # Start application with MSSQL profile"
        echo "  $0 test          # Run tests with clean ports"
        ;;
esac 