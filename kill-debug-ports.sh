#!/bin/bash

# Kill processes using debug ports
echo "Killing processes using debug ports..."

# Kill processes on port 5005 (default debug port)
echo "Checking port 5005..."
lsof -ti:5005 | xargs kill -9 2>/dev/null && echo "Killed processes on port 5005" || echo "No processes on port 5005"

# Kill processes on port 61468 (alternative debug port)
echo "Checking port 61468..."
lsof -ti:61468 | xargs kill -9 2>/dev/null && echo "Killed processes on port 61468" || echo "No processes on port 61468"

echo "Debug ports cleared!" 