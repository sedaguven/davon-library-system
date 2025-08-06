# Debug Setup for Davon Library System

This document provides comprehensive instructions for debugging both the backend (Java/Quarkus) and frontend (React/Next.js) components of the Davon Library System.

## Table of Contents

1. [Backend Debugging](#backend-debugging)
2. [Frontend Debugging](#frontend-debugging)
3. [Full Stack Debugging](#full-stack-debugging)
4. [VS Code Configuration](#vs-code-configuration)
5. [Debug Scripts](#debug-scripts)
6. [Troubleshooting](#troubleshooting)

## Backend Debugging

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- VS Code with Java Extension Pack

### Debug Configuration

The backend is configured with the following debug settings:

- **Debug Port**: 5005
- **Profile**: dev
- **Log Level**: DEBUG
- **Hot Code Replace**: Enabled

### Starting Backend in Debug Mode

#### Option 1: Using VS Code
1. Open the project in VS Code
2. Go to Run and Debug panel (Ctrl+Shift+D)
3. Select "Debug Backend Only" or "Debug Quarkus Backend"
4. Press F5 to start debugging

#### Option 2: Using Command Line
```bash
# Navigate to backend directory
cd backend

# Start in debug mode
./mvnw quarkus:dev -Dquarkus.log.level=DEBUG
```

#### Option 3: Using Debug Script
```bash
# From project root
./debug-backend.sh
```

### Backend Debug Features

- **Breakpoints**: Set breakpoints in any Java file
- **Step Through**: Step into, over, and out of methods
- **Variable Inspection**: Inspect local variables and object state
- **Call Stack**: View the complete call stack
- **Hot Code Replace**: Make code changes without restarting

### Debugging REST Endpoints

1. Set breakpoints in your resource classes (e.g., `BookResource.java`)
2. Use tools like Postman or curl to make HTTP requests
3. The debugger will pause at your breakpoints

Example:
```bash
curl -X GET http://localhost:8080/api/books
```

## Frontend Debugging

### Prerequisites
- Node.js 18+
- npm or yarn
- Chrome or Edge browser

### Debug Configuration

The frontend is configured with the following debug settings:

- **Debug Port**: 9229 (Node.js inspector)
- **Source Maps**: Enabled
- **Hot Reload**: Enabled

### Starting Frontend in Debug Mode

#### Option 1: Using VS Code
1. Open the project in VS Code
2. Go to Run and Debug panel (Ctrl+Shift+D)
3. Select "Next.js: debug server-side" or "Next.js: debug full stack"
4. Press F5 to start debugging

#### Option 2: Using Command Line
```bash
# Navigate to frontend directory
cd davon-library-webui

# Start in debug mode
NODE_OPTIONS="--inspect" npm run dev
```

#### Option 3: Using Debug Script
```bash
# From project root
./debug-frontend.sh
```

### Frontend Debug Features

- **Browser DevTools**: Use Chrome DevTools for client-side debugging
- **Server-Side Debugging**: Debug Next.js server-side code
- **React DevTools**: Install React DevTools extension for React debugging
- **Network Tab**: Monitor API calls to backend

### Debugging React Components

1. Set breakpoints in your React components
2. Use React DevTools for component inspection
3. Monitor state changes and props

## Full Stack Debugging

### Starting Full Stack Debug

#### Option 1: Using VS Code
1. Select "Debug Backend + Frontend" configuration
2. Press F5 to start both servers

#### Option 2: Using Debug Script
```bash
# From project root
./debug-full-stack.sh
```

### Full Stack Debug Features

- **Cross-Platform Debugging**: Debug both backend and frontend simultaneously
- **API Integration**: Debug API calls between frontend and backend
- **End-to-End Testing**: Test complete user workflows

## VS Code Configuration

### Extensions Required

- **Java Extension Pack**: For Java/Quarkus debugging
- **Debugger for Java**: Java debugging support
- **Node.js Extension Pack**: For Node.js/Next.js debugging
- **Chrome Debugger**: For browser debugging

### Launch Configurations

The project includes several VS Code launch configurations:

1. **Debug Backend Only**: Debug only the Quarkus backend
2. **Debug Frontend Only**: Debug only the Next.js frontend
3. **Debug Full Stack**: Debug both backend and frontend
4. **Attach to Backend**: Attach to running backend process

### Tasks

Available VS Code tasks:

- `start-backend`: Start backend in development mode
- `start-frontend`: Start frontend in development mode
- `build-backend`: Build backend project
- `build-frontend`: Build frontend project
- `test-backend`: Run backend tests
- `test-frontend`: Run frontend tests

## Debug Scripts

### Available Scripts

1. **debug-backend.sh**: Start backend in debug mode
2. **debug-frontend.sh**: Start frontend in debug mode
3. **debug-full-stack.sh**: Start both backend and frontend in debug mode

### Using Scripts

```bash
# Make scripts executable (if not already done)
chmod +x debug-*.sh

# Start backend debug
./debug-backend.sh

# Start frontend debug
./debug-frontend.sh

# Start full stack debug
./debug-full-stack.sh
```

## Troubleshooting

### Common Issues

#### Backend Debug Issues

1. **Port 5005 already in use**:
   ```bash
   # Find process using port 5005
   lsof -i :5005
   # Kill the process
   kill -9 <PID>
   ```

2. **Java version issues**:
   ```bash
   # Check Java version
   java -version
   # Should be Java 17 or higher
   ```

3. **Maven build issues**:
   ```bash
   # Clean and rebuild
   cd backend
   ./mvnw clean compile
   ```

#### Frontend Debug Issues

1. **Port 3000 already in use**:
   ```bash
   # Find process using port 3000
   lsof -i :3000
   # Kill the process
   kill -9 <PID>
   ```

2. **Node.js version issues**:
   ```bash
   # Check Node.js version
   node --version
   # Should be 18 or higher
   ```

3. **Dependencies issues**:
   ```bash
   # Reinstall dependencies
   cd davon-library-webui
   rm -rf node_modules package-lock.json
   npm install
   ```

### Debug Ports

- **Backend**: 5005 (JDWP)
- **Frontend**: 9229 (Node.js inspector)
- **Frontend URL**: http://localhost:3000
- **Backend URL**: http://localhost:8080

### Log Files

- **Backend logs**: Check console output or `backend/target/quarkus.log`
- **Frontend logs**: Check browser console and terminal output

### Performance Tips

1. **Use conditional breakpoints** for better performance
2. **Limit log output** in production-like scenarios
3. **Use watch expressions** instead of frequent breakpoints
4. **Enable source maps** for better debugging experience

## Advanced Debugging

### Remote Debugging

For debugging on remote servers:

1. **Backend**: Use the JDWP configuration in `application.properties`
2. **Frontend**: Use Chrome DevTools remote debugging

### Debugging Tests

1. **Backend Tests**: Use "Debug Quarkus Tests" configuration
2. **Frontend Tests**: Use browser DevTools for component testing

### Memory Profiling

1. **Backend**: Use JVM profiling tools
2. **Frontend**: Use Chrome DevTools Memory tab

## Support

For additional debugging support:

1. Check the Quarkus documentation: https://quarkus.io/guides/debugging
2. Check the Next.js documentation: https://nextjs.org/docs/debugging
3. Review VS Code debugging documentation: https://code.visualstudio.com/docs/editor/debugging 