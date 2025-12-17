@echo off
echo ========================================
echo Running Core Accounts Microservice Tests
echo ========================================

cd core-accounts-microservice

echo.
echo Running Unit Tests...
mvn test -Dtest=*UnitTest

echo.
echo Running Integration Tests...
mvn test -Dtest=*IntegrationTest

echo.
echo Running All Tests...
mvn test

echo.
echo Tests completed!
pause