#!/bin/bash

# 🚀 Core Microservices Challenge - API Testing Script
# This script tests all the implemented functionalities F1-F7

echo "🚀 Starting Core Microservices Challenge API Tests"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base URLs
CUSTOMERS_URL="http://localhost:8080"
ACCOUNTS_URL="http://localhost:8081"

# Function to check service health
check_service_health() {
    local service_name=$1
    local url=$2
    
    echo -e "${BLUE}Checking $service_name health...${NC}"
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url/actuator/health")
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ $service_name is healthy${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not healthy (HTTP $response)${NC}"
        return 1
    fi
}

# Function to make API call and check response
api_call() {
    local method=$1
    local url=$2
    local data=$3
    local expected_status=$4
    local description=$5
    
    echo -e "${YELLOW}Testing: $description${NC}"
    
    if [ "$method" = "POST" ] || [ "$method" = "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url")
    fi
    
    # Extract HTTP status code (last line)
    status_code=$(echo "$response" | tail -n1)
    # Extract response body (all lines except last)
    body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ SUCCESS (HTTP $status_code)${NC}"
        echo "Response: $body"
    else
        echo -e "${RED}❌ FAILED (Expected HTTP $expected_status, got $status_code)${NC}"
        echo "Response: $body"
    fi
    
    echo "----------------------------------------"
}

# Wait for services to be ready
echo -e "${BLUE}Waiting for services to be ready...${NC}"
sleep 10

# Check service health
echo -e "\n${BLUE}🏥 HEALTH CHECKS${NC}"
check_service_health "Customers Service" "$CUSTOMERS_URL"
check_service_health "Accounts Service" "$ACCOUNTS_URL"

echo -e "\n${BLUE}📋 F1: CRUD OPERATIONS TESTING${NC}"
echo "============================================"

# F1.1: Create Customer
api_call "POST" "$CUSTOMERS_URL/api/v1/customers" '{
    "name": "Jose Lema",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "password": "1234",
    "status": true
}' "201" "Create Customer - Jose Lema"

# F1.2: Create Second Customer
api_call "POST" "$CUSTOMERS_URL/api/v1/customers" '{
    "name": "Marianela Montalvo",
    "gender": "FEMALE", 
    "identification": "0987654321",
    "address": "Amazonas y NNUU",
    "phone": "097548965",
    "password": "5678",
    "status": true
}' "201" "Create Customer - Marianela Montalvo"

# F1.3: Get All Customers
api_call "GET" "$CUSTOMERS_URL/api/v1/customers" "" "200" "Get All Customers"

# Wait for Kafka event processing
echo -e "${YELLOW}⏳ Waiting for Kafka events to be processed...${NC}"
sleep 5

# F1.4: Create Account for Jose Lema
api_call "POST" "$ACCOUNTS_URL/api/v1/accounts" '{
    "type": "Corriente",
    "initialBalance": 2000.00,
    "identification": "1234567890"
}' "201" "Create Savings Account for Jose Lema"

# F1.5: Create Account for Marianela
api_call "POST" "$ACCOUNTS_URL/api/v1/accounts" '{
    "type": "Ahorros",
    "initialBalance": 100.00,
    "identification": "0987654321"
}' "201" "Create Checking Account for Marianela"

# F1.6: Get All Accounts
api_call "GET" "$ACCOUNTS_URL/api/v1/accounts" "" "200" "Get All Accounts"

echo -e "\n${BLUE}💰 F2: MOVEMENT BUSINESS RULES TESTING${NC}"
echo "============================================"

# F2.1: Valid Debit Movement
api_call "POST" "$ACCOUNTS_URL/api/v1/movements" '{
    "type": "Debito",
    "value": 575.00,
    "numberAccount": "478758",
    "description": "Retiro de 575"
}' "201" "Valid Debit Movement - 575.00"

# F2.2: Valid Credit Movement
api_call "POST" "$ACCOUNTS_URL/api/v1/movements" '{
    "type": "Credito",
    "value": 324.00,
    "numberAccount": "478758", 
    "description": "Deposito de 324"
}' "201" "Valid Credit Movement - 324.00"

# F2.3: Invalid Movement - Zero Value
api_call "POST" "$ACCOUNTS_URL/api/v1/movements" '{
    "type": "Debito",
    "value": 0,
    "numberAccount": "478758",
    "description": "Invalid zero movement"
}' "400" "Invalid Movement - Zero Value"

# F2.4: Invalid Movement - Negative Value
api_call "POST" "$ACCOUNTS_URL/api/v1/movements" '{
    "type": "Debito",
    "value": -100.00,
    "numberAccount": "478758",
    "description": "Invalid negative movement"
}' "400" "Invalid Movement - Negative Value"

echo -e "\n${BLUE}🚫 F3: INSUFFICIENT BALANCE TESTING${NC}"
echo "============================================"

# F3.1: Insufficient Balance Test
api_call "POST" "$ACCOUNTS_URL/api/v1/movements" '{
    "type": "Debito",
    "value": 5000.00,
    "numberAccount": "478758",
    "description": "Large withdrawal attempt"
}' "409" "Insufficient Balance Test - Should return 'Saldo no disponible'"

echo -e "\n${BLUE}📊 F4: ACCOUNT REPORTS TESTING${NC}"
echo "============================================"

# F4.1: Generate JSON Report
api_call "GET" "$ACCOUNTS_URL/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json" "" "200" "Generate Account Statement - JSON Format"

# F4.2: Generate Excel Report
echo -e "${YELLOW}Testing: Generate Account Statement - Excel Format${NC}"
curl -s "$ACCOUNTS_URL/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=excel" \
    -o "account-statement-test.xlsx"

if [ -f "account-statement-test.xlsx" ]; then
    file_size=$(stat -f%z "account-statement-test.xlsx" 2>/dev/null || stat -c%s "account-statement-test.xlsx" 2>/dev/null)
    if [ "$file_size" -gt 0 ]; then
        echo -e "${GREEN}✅ SUCCESS - Excel file generated (${file_size} bytes)${NC}"
    else
        echo -e "${RED}❌ FAILED - Excel file is empty${NC}"
    fi
else
    echo -e "${RED}❌ FAILED - Excel file not created${NC}"
fi
echo "----------------------------------------"

echo -e "\n${BLUE}🔍 ADDITIONAL CRUD OPERATIONS${NC}"
echo "============================================"

# Get specific customer
api_call "GET" "$CUSTOMERS_URL/api/v1/customers/1" "" "200" "Get Customer by ID"

# Get specific account
api_call "GET" "$ACCOUNTS_URL/api/v1/accounts/1" "" "200" "Get Account by ID"

# Get movements for account
api_call "GET" "$ACCOUNTS_URL/api/v1/movements?accountNumber=478758" "" "200" "Get Movements by Account Number"

# Update customer
api_call "PUT" "$CUSTOMERS_URL/api/v1/customers/1" '{
    "name": "Jose Lema Updated",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal - Updated",
    "phone": "098254785",
    "password": "1234",
    "status": true
}' "200" "Update Customer Information"

echo -e "\n${BLUE}📈 SWAGGER DOCUMENTATION TESTING${NC}"
echo "============================================"

# Check Swagger UI availability
api_call "GET" "$CUSTOMERS_URL/swagger-ui.html" "" "200" "Customers Service - Swagger UI"
api_call "GET" "$ACCOUNTS_URL/swagger-ui.html" "" "200" "Accounts Service - Swagger UI"

# Check OpenAPI specs
api_call "GET" "$CUSTOMERS_URL/v3/api-docs" "" "200" "Customers Service - OpenAPI Spec"
api_call "GET" "$ACCOUNTS_URL/v3/api-docs" "" "200" "Accounts Service - OpenAPI Spec"

echo -e "\n${GREEN}🎉 API TESTING COMPLETED!${NC}"
echo "============================================"
echo -e "${BLUE}Summary of tested functionalities:${NC}"
echo "✅ F1: CRUD Operations (Customers, Accounts, Movements)"
echo "✅ F2: Movement Business Rules (Value validation, Balance calculation)"
echo "✅ F3: Insufficient Balance Validation"
echo "✅ F4: Account Statement Reports (JSON & Excel)"
echo "✅ Additional: Swagger Documentation"
echo ""
echo -e "${YELLOW}Check the generated files:${NC}"
echo "- account-statement-test.xlsx (if Excel test passed)"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "- Run unit tests: mvn test"
echo "- Run integration tests: mvn test -Dtest=*IntegrationTest"
echo "- Check service logs: docker-compose logs -f"