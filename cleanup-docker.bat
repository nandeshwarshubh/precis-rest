@echo off
echo ========================================
echo Docker Cleanup Script
echo ========================================
echo.

echo Step 1: Stopping all containers...
docker-compose down -v
echo.

echo Step 2: Removing precis-related containers...
docker rm -f precis-postgres precis-app precis-zipkin 2>nul
echo.

echo Step 3: Removing precis-related images...
docker rmi -f precis-rest-postgres precis-rest-app 2>nul
echo.

echo Step 4: Removing all volumes...
docker volume rm precis-rest_postgres_data 2>nul
docker volume prune -f
echo.

echo Step 5: Removing unused images...
docker image prune -a -f
echo.

echo Step 6: System cleanup...
docker system prune -a -f --volumes
echo.

echo ========================================
echo Cleanup Complete!
echo ========================================
echo.
echo You can now rebuild with: docker-compose up --build
pause

