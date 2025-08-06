#!/bin/bash

# Debug script for Davon Library Backend
echo "Starting Davon Library Backend in debug mode..."

# Change to backend directory
cd backend

# Set debug environment variables
export QUARKUS_PROFILE=dev
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Start Quarkus in development mode with debug
./mvnw quarkus:dev -Dquarkus.log.level=DEBUG -Dquarkus.log.category."com.davonlibrary".level=DEBUG

echo "Backend debug server started on port 5005"
echo "You can now attach your debugger to localhost:5005" 