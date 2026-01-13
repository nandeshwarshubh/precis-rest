@echo off
echo ========================================
echo Flyway Verification Script
echo ========================================
echo.

echo Step 1: Checking if PostgreSQL is running...
docker ps | findstr precis-postgres
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: PostgreSQL container is not running!
    echo Please start it with: docker-compose up -d postgres
    pause
    exit /b 1
)
echo PostgreSQL is running!
echo.

echo Step 2: Checking for schemas...
echo.
docker exec -it precis-postgres psql -U postgres -d precis -c "\dn"
echo.

echo Step 3: Checking for tables in precis schema...
echo.
docker exec -it precis-postgres psql -U postgres -d precis -c "\dt precis.*"
echo.

echo Step 4: Checking for Flyway schema history table...
echo.
docker exec -it precis-postgres psql -U postgres -d precis -c "SELECT * FROM precis.flyway_schema_history;"
echo.

echo Step 5: Checking for url_shorten table structure...
echo.
docker exec -it precis-postgres psql -U postgres -d precis -c "\d precis.url_shorten"
echo.

echo ========================================
echo Verification Complete!
echo ========================================
echo.
echo If you see the tables above, Flyway is working correctly.
echo If not, check the application logs with: docker logs precis-app
pause

