@echo off
set JAVA_HOME=C:\Users\shubh\.jdks\openjdk-25.0.1
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew.bat clean build -x test

