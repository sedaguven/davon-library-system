#!/bin/bash

# Debug script for Davon Library Frontend
echo "Starting Davon Library Frontend in debug mode..."

# Change to frontend directory
cd davon-library-webui

# Set debug environment variables
export NODE_OPTIONS="--inspect"

# Start Next.js in development mode with debug
npm run dev

echo "Frontend debug server started"
echo "You can now debug the frontend in your browser's developer tools" 