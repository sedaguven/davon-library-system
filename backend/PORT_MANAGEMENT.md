# Port Management Guide

## Why Port Conflicts Occur

Port conflicts happen when multiple processes try to use the same port. In your development environment, this commonly occurs due to:

1. **Multiple Application Instances**: Starting the app multiple times
2. **Development Mode Issues**: Quarkus dev mode not shutting down properly
3. **Test vs Application Conflicts**: Tests trying to start while app is running
4. **Database Containers**: MSSQL Docker container using port 1433

## Port Configuration

| Profile | Port | Purpose |
|---------|------|---------|
| Default | 8080 | Main application (H2 database) |
| MSSQL   | 8081 | Application with MSSQL database |
| Test    | Random | Tests use random available port |
| Debug   | 5005 | Remote debugging |
| MSSQL DB| 1433 | MSSQL Server container |

## Quick Solutions

### 1. Use the Port Management Script

```bash
# Check what's using the ports
./scripts/manage-ports.sh check

# Kill all processes on relevant ports
./scripts/manage-ports.sh clean

# Start application with specific profile
./scripts/manage-ports.sh start-mssql
./scripts/manage-ports.sh start-dev

# Run tests with clean ports
./scripts/manage-ports.sh test
```

### 2. Manual Port Management

```bash
# Check what's using port 8081
lsof -ti:8081

# Kill process on specific port
lsof -ti:8081 | xargs kill -9

# Check all relevant ports
lsof -i:8080,8081,5005,1433
```

### 3. Use Different Ports

If you need to run multiple instances, modify `application.properties`:

```properties
# For a third profile
%custom.quarkus.http.port=8082
```

## Prevention Tips

### 1. Always Stop Previous Instances
- Use `Ctrl+C` to properly stop Quarkus dev mode
- Don't just close the terminal window

### 2. Use Profile-Specific Ports
- Default profile: 8080
- MSSQL profile: 8081
- Tests: Random port (port=0)

### 3. Check Ports Before Starting
```bash
# Quick check
./scripts/manage-ports.sh check

# Or manually
lsof -i:8080,8081
```

### 4. Use the Management Script
The script automatically:
- Checks port availability
- Offers to kill conflicting processes
- Starts the application safely

## Common Error Messages

### "Port already bound: 8081"
**Solution**: Kill the process using port 8081
```bash
./scripts/manage-ports.sh clean
```

### "Address already in use"
**Solution**: Check what's using the port and kill it
```bash
lsof -ti:8081 | xargs kill -9
```

### "Failed to start quarkus"
**Solution**: Usually a port conflict, clean ports and restart
```bash
./scripts/manage-ports.sh clean
./scripts/manage-ports.sh start-mssql
```

## Development Workflow

1. **Before starting development**:
   ```bash
   ./scripts/manage-ports.sh check
   ```

2. **If ports are busy**:
   ```bash
   ./scripts/manage-ports.sh clean
   ```

3. **Start your application**:
   ```bash
   ./scripts/manage-ports.sh start-mssql  # For MSSQL development
   ./scripts/manage-ports.sh start-dev    # For H2 development
   ```

4. **Run tests**:
   ```bash
   ./scripts/manage-ports.sh test
   ```

## Troubleshooting

### MSSQL Container Issues
If MSSQL container is causing conflicts:
```bash
# Check MSSQL container
docker ps | grep mssql

# Restart MSSQL container
docker restart library-sql-server
```

### Persistent Port Issues
If ports remain busy after killing processes:
```bash
# Wait a moment for ports to be released
sleep 5

# Check again
./scripts/manage-ports.sh check
```

### Multiple Development Sessions
If you need to run multiple development sessions:
1. Use different profiles with different ports
2. Use the port management script to coordinate
3. Consider using different workspaces 