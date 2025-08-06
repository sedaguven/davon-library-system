#!/bin/bash

# Debug script for Davon Library Full Stack
echo "Starting Davon Library Full Stack in debug mode..."

# Function to start backend
start_backend() {
    echo "Starting backend in debug mode..."
    cd backend
    export QUARKUS_PROFILE=dev
    export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    ./mvnw quarkus:dev -Dquarkus.log.level=DEBUG -Dquarkus.log.category."com.davonlibrary".level=DEBUG
}

# Function to start frontend
start_frontend() {
    echo "Starting frontend in debug mode..."
    cd davon-library-webui
    export NODE_OPTIONS="--inspect"
    npm run dev
}

# Start backend in background
start_backend &
BACKEND_PID=$!

# Wait a moment for backend to start
sleep 5

# Start frontend in background
start_frontend &
FRONTEND_PID=$!

echo "Full stack debug servers started:"
echo "- Backend debug port: 5005"
echo "- Frontend debug port: 9229 (Node.js inspector)"
echo "- Frontend URL: http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop both servers"

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID

# Cleanup on exit
trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null" EXIT 