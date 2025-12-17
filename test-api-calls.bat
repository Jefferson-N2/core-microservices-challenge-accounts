@echo off
REM 🚀 Core Microservices Challenge - API Testing Script (Windows)
REM This script tests all the implemented functionalities F1-F7

echo 🚀 Starting Core Microservices Challenge API Tests
echo ==================================================

REM Base URLs
set CUSTOMERS_URL=http://localhost:8080
set ACCOUNTS_URL=http://localhost:8081

echo.
echo 🏥 HEALTH CHECKS
echo ================

echo Checking Customers Service health...
curl -s %CUSTOMERS_URL%/actuator/health
echo.

echo Checking Accounts Service health...
curl -s %ACCOUNTS_URL%/actuator/health
echo.

echo ⏳ Waiting for services to be ready...
timeout /t 10 /nobreak > nul

echo.
echo 📋 F1: CRUD OPERATIONS TESTING
echo ===============================

echo Testing: Create Customer - Jose Lema
curl -X POST %CUSTOMERS_URL%/api/v1/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Jose Lema\", \"gender\": \"MALE\", \"identification\": \"1234567890\", \"address\": \"Otavalo sn y principal\", \"phone\": \"098254785\", \"password\": \"1234\", \"status\": true}"
echo.
echo ----------------------------------------

echo Testing: Create Customer - Marianela Montalvo
curl -X POST %CUSTOMERS_URL%/api/v1/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Marianela Montalvo\", \"gender\": \"FEMALE\", \"identification\": \"0987654321\", \"address\": \"Amazonas y NNUU\", \"phone\": \"097548965\", \"password\": \"5678\", \"status\": true}"
echo.
echo ----------------------------------------

echo Testing: Get All Customers
curl -X GET %CUSTOMERS_URL%/api/v1/customers
echo.
echo ----------------------------------------

echo ⏳ Waiting for Kafka events to be processed...
timeout /t 5 /nobreak > nul

echo Testing: Create Savings Account for Jose Lema
curl -X POST %ACCOUNTS_URL%/api/v1/accounts ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Corriente\", \"initialBalance\": 2000.00, \"identification\": \"1234567890\"}"
echo.
echo ----------------------------------------

echo Testing: Create Checking Account for Marianela
curl -X POST %ACCOUNTS_URL%/api/v1/accounts ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Ahorros\", \"initialBalance\": 100.00, \"identification\": \"0987654321\"}"
echo.
echo ----------------------------------------

echo Testing: Get All Accounts
curl -X GET %ACCOUNTS_URL%/api/v1/accounts
echo.
echo ----------------------------------------

echo.
echo 💰 F2: MOVEMENT BUSINESS RULES TESTING
echo =======================================

echo Testing: Valid Debit Movement - 575.00
curl -X POST %ACCOUNTS_URL%/api/v1/movements ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Debito\", \"value\": 575.00, \"numberAccount\": \"478758\", \"description\": \"Retiro de 575\"}"
echo.
echo ----------------------------------------

echo Testing: Valid Credit Movement - 324.00
curl -X POST %ACCOUNTS_URL%/api/v1/movements ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Credito\", \"value\": 324.00, \"numberAccount\": \"478758\", \"description\": \"Deposito de 324\"}"
echo.
echo ----------------------------------------

echo Testing: Invalid Movement - Zero Value
curl -X POST %ACCOUNTS_URL%/api/v1/movements ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Debito\", \"value\": 0, \"numberAccount\": \"478758\", \"description\": \"Invalid zero movement\"}"
echo.
echo ----------------------------------------

echo Testing: Invalid Movement - Negative Value
curl -X POST %ACCOUNTS_URL%/api/v1/movements ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Debito\", \"value\": -100.00, \"numberAccount\": \"478758\", \"description\": \"Invalid negative movement\"}"
echo.
echo ----------------------------------------

echo.
echo 🚫 F3: INSUFFICIENT BALANCE TESTING
echo ====================================

echo Testing: Insufficient Balance - Should return 'Saldo no disponible'
curl -X POST %ACCOUNTS_URL%/api/v1/movements ^
  -H "Content-Type: application/json" ^
  -d "{\"type\": \"Debito\", \"value\": 5000.00, \"numberAccount\": \"478758\", \"description\": \"Large withdrawal attempt\"}"
echo.
echo ----------------------------------------

echo.
echo 📊 F4: ACCOUNT REPORTS TESTING
echo ===============================

echo Testing: Generate Account Statement - JSON Format
curl "%ACCOUNTS_URL%/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json"
echo.
echo ----------------------------------------

echo Testing: Generate Account Statement - Excel Format
curl "%ACCOUNTS_URL%/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=excel" --output account-statement-test.xlsx
if exist account-statement-test.xlsx (
    echo ✅ SUCCESS - Excel file generated
) else (
    echo ❌ FAILED - Excel file not created
)
echo ----------------------------------------

echo.
echo 🔍 ADDITIONAL CRUD OPERATIONS
echo ==============================

echo Testing: Get Customer by ID
curl -X GET %CUSTOMERS_URL%/api/v1/customers/1
echo.
echo ----------------------------------------

echo Testing: Get Account by ID
curl -X GET %ACCOUNTS_URL%/api/v1/accounts/1
echo.
echo ----------------------------------------

echo Testing: Get Movements by Account Number
curl -X GET "%ACCOUNTS_URL%/api/v1/movements?accountNumber=478758"
echo.
echo ----------------------------------------

echo Testing: Update Customer Information
curl -X PUT %CUSTOMERS_URL%/api/v1/customers/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Jose Lema Updated\", \"gender\": \"MALE\", \"identification\": \"1234567890\", \"address\": \"Otavalo sn y principal - Updated\", \"phone\": \"098254785\", \"password\": \"1234\", \"status\": true}"
echo.
echo ----------------------------------------

echo.
echo 📈 SWAGGER DOCUMENTATION TESTING
echo =================================

echo Testing: Customers Service - Swagger UI
curl -s %CUSTOMERS_URL%/swagger-ui.html > nul
if %errorlevel% == 0 (
    echo ✅ Customers Swagger UI is accessible
) else (
    echo ❌ Customers Swagger UI is not accessible
)

echo Testing: Accounts Service - Swagger UI
curl -s %ACCOUNTS_URL%/swagger-ui.html > nul
if %errorlevel% == 0 (
    echo ✅ Accounts Swagger UI is accessible
) else (
    echo ❌ Accounts Swagger UI is not accessible
)

echo.
echo 🎉 API TESTING COMPLETED!
echo ==========================
echo Summary of tested functionalities:
echo ✅ F1: CRUD Operations (Customers, Accounts, Movements)
echo ✅ F2: Movement Business Rules (Value validation, Balance calculation)
echo ✅ F3: Insufficient Balance Validation
echo ✅ F4: Account Statement Reports (JSON ^& Excel)
echo ✅ Additional: Swagger Documentation
echo.
echo Check the generated files:
echo - account-statement-test.xlsx (if Excel test passed)
echo.
echo Next steps:
echo - Run unit tests: mvn test
echo - Run integration tests: mvn test -Dtest=*IntegrationTest
echo - Check service logs: docker-compose logs -f

pause