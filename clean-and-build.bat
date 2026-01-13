@echo off
REM Clean and build script for Precis REST API
REM This script cleans the build directory and rebuilds the project

echo ========================================
echo Precis REST - Clean and Build
echo ========================================
echo.

REM Check if JAVA_HOME is set
if "%JAVA_HOME%"=="" (
    echo ERROR: JAVA_HOME is not set!
    echo.
    echo Please set JAVA_HOME to your Java 25 installation directory.
    echo Example:
    echo   set JAVA_HOME=C:\Program Files\Java\jdk-25
    echo.
    echo Or add it to your system environment variables.
    echo.
    pause
    exit /b 1
)

echo JAVA_HOME is set to: %JAVA_HOME%
echo.

REM Display Java version
echo Checking Java version...
"%JAVA_HOME%\bin\java" -version
echo.

REM Clean build directory
echo Cleaning build directory...
if exist build (
    rmdir /s /q build
    echo Build directory cleaned.
) else (
    echo No build directory found.
)
echo.

REM Clean Gradle cache for this project (optional)
echo Cleaning Gradle cache...
if exist .gradle (
    rmdir /s /q .gradle
    echo Gradle cache cleaned.
)
echo.

REM Run Gradle clean
echo Running Gradle clean...
call gradlew.bat clean
if errorlevel 1 (
    echo.
    echo ERROR: Gradle clean failed!
    pause
    exit /b 1
)
echo.

REM Run Gradle build
echo Running Gradle build...
call gradlew.bat build
if errorlevel 1 (
    echo.
    echo ERROR: Gradle build failed!
    echo.
    echo Check the error messages above for details.
    pause
    exit /b 1
)
echo.

echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo JAR file location: build\libs\precis-rest-0.0.1-SNAPSHOT.jar
echo.
pause

