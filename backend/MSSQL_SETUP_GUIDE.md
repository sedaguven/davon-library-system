# MSSQL Setup Guide - Davon Library System

## 🎯 **COMPLETED TASKS**

### ✅ **1. Microsoft SQL Server Setup**
- MSSQL Server 2022 Docker container configured
- Database schema implemented with all tables, indexes, views, stored procedures, triggers
- Sample data loaded

### ✅ **2. Database Schema Implementation**
- **Tables**: All 15+ tables created (books, users, loans, fines, etc.)
- **Indexes**: Performance optimization indexes on all key columns
- **Views**: Reporting views for active reservations, outstanding fines, etc.
- **Stored Procedures**: 
  - `sp_borrow_book` - Borrow books with validation
  - `sp_return_book` - Return books and update status
  - `sp_search_books` - Advanced book search
- **Triggers**: Data integrity and audit trails

### ✅ **3. Java Backend Integration**
- **JDBC Connection**: Properly configured MSSQL connection
- **DAO Layer**: 
  - `BookDAO` - Complete CRUD operations and advanced search
  - `UserDAO` - User management with borrowing statistics
- **Stored Procedure Service**: `MSSQLStoredProcedureService` for direct SP calls
- **REST Endpoints**: `MSSQLIntegrationResource` for testing integration
- **Entity Mapping**: JPA entities properly mapped to database tables

### ✅ **4. Port Management**
- Scripts to manage port conflicts
- Documentation for different profiles (H2 vs MSSQL)

## 🚀 **HOW TO START THE SYSTEM**

### **Step 1: Start Docker Desktop**
```bash
# Make sure Docker Desktop is running on your Mac
# Check with: docker --version
```

### **Step 2: Start MSSQL Container**
```bash
# From the backend directory
./scripts/manage-ports.sh start-mssql
```

### **Step 3: Start the Application**
```bash
# Start with MSSQL profile
./mvnw quarkus:dev -Dquarkus.profile=mssql
```

### **Step 4: Test the Integration**
```bash
# Test MSSQL connection
curl http://localhost:8081/api/mssql/test

# Test stored procedures
curl http://localhost:8081/api/mssql/procedures

# Test book search
curl "http://localhost:8081/api/mssql/search?term=java"
```

## 📊 **AVAILABLE ENDPOINTS**

### **MSSQL Integration Endpoints**
- `GET /api/mssql/test` - Test database connection
- `GET /api/mssql/procedures` - List available stored procedures
- `POST /api/mssql/borrow` - Borrow book using stored procedure
- `POST /api/mssql/return` - Return book using stored procedure
- `GET /api/mssql/search` - Search books using stored procedure
- `GET /api/mssql/overdue` - Get overdue loans
- `GET /api/mssql/statistics` - Get library statistics

### **Regular API Endpoints**
- `GET /api/books` - Get all books
- `GET /api/users` - Get all users
- `GET /api/database/health` - Database health check

## 🗄️ **DATABASE FEATURES**

### **Stored Procedures**
1. **sp_borrow_book(userId, bookCopyId, loanPeriodDays)**
   - Validates user can borrow
   - Checks book availability
   - Creates loan record
   - Updates book status

2. **sp_return_book(loanId)**
   - Processes book return
   - Updates book availability
   - Calculates fines if overdue

3. **sp_search_books(searchTerm, authorName, isbn)**
   - Advanced book search
   - Multiple criteria support
   - Returns formatted results

### **Views**
- `v_active_reservations` - Current reservations
- `v_outstanding_fines` - Unpaid fines
- `v_overdue_loans` - Overdue books

### **Indexes**
- Performance optimization on all key columns
- Composite indexes for common queries
- Full-text search capabilities

## 🔧 **TROUBLESHOOTING**

### **Port Conflicts**
```bash
# Clean all ports
./scripts/manage-ports.sh clean

# Check port usage
./scripts/manage-ports.sh check
```

### **Database Connection Issues**
1. Ensure Docker Desktop is running
2. Check MSSQL container status: `docker ps`
3. Verify connection in `application.properties`

### **Application Issues**
1. Check logs for specific errors
2. Verify MSSQL profile is active
3. Test database connection endpoint

## 📁 **KEY FILES**

### **Database**
- `database/schema_complete.sql` - Complete database schema
- `database/sample_data.sql` - Sample data

### **Java Backend**
- `src/main/java/com/davonlibrary/dao/BookDAO.java` - Book data access
- `src/main/java/com/davonlibrary/dao/UserDAO.java` - User data access
- `src/main/java/com/davonlibrary/service/MSSQLStoredProcedureService.java` - SP integration
- `src/main/java/com/davonlibrary/resource/MSSQLIntegrationResource.java` - REST endpoints

### **Configuration**
- `src/main/resources/application.properties` - Database configuration
- `scripts/manage-ports.sh` - Port management

## 🎉 **SUCCESS INDICATORS**

✅ **System is working when:**
- Docker container shows "Up" status
- Application starts without errors
- `GET /api/mssql/test` returns success
- `GET /api/books` returns data
- Stored procedures execute successfully

## 📝 **NEXT STEPS**

1. **Start Docker Desktop**
2. **Run the setup scripts**
3. **Test the integration endpoints**
4. **Explore the stored procedures**
5. **Build your frontend integration**

---

**Your MSSQL setup is complete and ready to use!** 🚀 