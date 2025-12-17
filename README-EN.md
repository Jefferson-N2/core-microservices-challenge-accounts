# Core Microservices Challenge - Practical Case

Complete microservices architecture solution for account and customer management, implementing all F1-F7 functionalities.

## 🏗️ Architecture

### Microservices
- **Customers Service** (Port 8080): Customer and person management
- **Accounts Service** (Port 8081): Account and movement management
- **Asynchronous Communication**: Kafka for inter-service events

### Technologies
- **Java 21** + Spring Boot 3.5.8 + WebFlux
- **MySQL** (Database)
- **Apache Kafka** (Asynchronous communication)
- **Docker Compose** (Orchestration)
- **OpenAPI 3.0** (Contract-First)
- **JUnit 5** + Mockito (Testing)

## 🚀 Deployment

### Requirements
- Docker & Docker Compose
- Java 21 (local development)

### Installation Steps

#### 1. Clone Repository
```bash
git clone <repository-url>
cd core-microservices-challenge-accounts
```

#### 2. Configure Environment Variables
```bash
# Copy configuration file
cp .env.example .env

# Optional: Edit variables if needed
# nano .env
```

#### 3. Initialize Database
```bash
# BaseDatos.sql script runs automatically
# when MySQL starts for the first time
```

#### 4. Deploy Services
```bash
# Build and start all services
docker-compose up --build -d

# Verify all services are running
docker-compose ps

# View logs in real time
docker-compose logs -f
```

#### 5. Verify Deployment
```bash
# Health check services
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### Service URLs
- **Customers**: http://localhost:8080
- **Accounts**: http://localhost:8081
- **Swagger UI Customers**: http://localhost:8080/swagger-ui.html
- **Swagger UI Accounts**: http://localhost:8081/swagger-ui.html
- **MySQL**: localhost:3306 (admin/admin123)
- **Kafka**: localhost:9092

### Useful Commands
```bash
# Stop services
docker-compose down

# Stop and clean volumes
docker-compose down -v

# Rebuild services
docker-compose build --no-cache

# View logs of specific service
docker-compose logs -f customers-service
docker-compose logs -f accounts-service
```

## 📋 Implemented Functionalities

| Functionality | Description | Status |
|---------------|-------------|--------|
| **F1** | Complete CRUD (Customers, Accounts, Movements) | ✅ |
| **F2** | Business rules for movements | ✅ |
| **F3** | "Insufficient balance" validation | ✅ |
| **F4** | Account statement reports | ✅ |
| **F5** | Unit tests | ✅ |
| **F6** | Integration tests | ✅ |
| **F7** | Docker deployment | ✅ |

### Main Endpoints
```
Customers Service (8080):
├── GET    /api/v1/customers
├── POST   /api/v1/customers
├── PUT    /api/v1/customers/{id}
└── DELETE /api/v1/customers/{id}

Accounts Service (8081):
├── GET    /api/v1/accounts
├── POST   /api/v1/accounts
├── POST   /api/v1/movements
└── GET    /api/v1/reports/{client-id}
```

## 🔧 Use Cases

### 1. Create Customer
```json
POST /api/v1/customers
{
  "name": "Jose Lema",
  "gender": "MALE",
  "identification": "1234567890",
  "address": "Otavalo sn y principal",
  "phone": "098254785",
  "password": "1234",
  "status": true
}
```

### 2. Create Account
```json
POST /api/v1/accounts
{
  "type": "Ahorros",
  "initialBalance": 2000.00,
  "identification": "1234567890"
}
```

### 3. Register Movement
```json
POST /api/v1/movements
{
  "type": "Debito",
  "value": 575.00,
  "numberAccount": "478758",
  "description": "Retiro de 575"
}
```

### 4. Generate Report
```
GET /api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json
```

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn test -Dtest=*IntegrationTest
```

### Postman Collection
- Import: `Core-Microservices-Challenge.postman_collection.json`
- Includes all technical test use cases

## 🔄 Asynchronous Communication

### Kafka Events
```
customer.events topic:
├── CUSTOMER_CREATED
├── CUSTOMER_UPDATED
└── CUSTOMER_DELETED
```

**Flow**: Customers Service → Kafka → Accounts Service

## 📊 Database

### Schema
- **BaseDatos.sql**: Complete schema with test data
- **Entities**: Person, Customer, Account, Movement
- **Relationships**: FK constraints and indexes

### Configuration
```yaml
MySQL: localhost:3306/microservices_db
User: admin / Password: admin123
```

## 🏛️ Architecture

### Implemented Patterns
- **Hexagonal Architecture**: Domain, Application, Infrastructure
- **Repository Pattern**: Data access
- **Adapter Pattern**: External integrations
- **Event-Driven**: Asynchronous communication

### Code Quality
- **Clean Code**: SOLID principles
- **Contract-First**: OpenAPI specifications
- **Reactive Programming**: Spring WebFlux
- **Error Handling**: Global exception management

## 📈 Monitoring

### Health Checks
```
/actuator/health - Service status
/actuator/metrics - Application metrics
```

### Logging
- Structured logging with SLF4J
- Correlation IDs for traceability
- Business events tracking

## 🚀 Production Considerations

### Security
- Input validation (Bean Validation)
- SQL injection prevention (JPA)
- Secure password handling

### Scalability
- Stateless microservices
- Connection pooling
- Horizontal scaling ready
- Load balancer compatible

## 📁 Project Structure

```
core-microservices-challenge-accounts/
├── core-customers-microservice/     # Customer service
├── core-accounts-microservice/      # Account service
├── docker-compose.yml              # Orchestration
├── .env                           # Environment variables
├── BaseDatos.sql                  # DB Schema
└── Core-Microservices-Challenge.postman_collection.json
```

## 🎯 Deliverables

- ✅ **Source code**: Complete Git repository
- ✅ **OpenAPI**: YAML specifications
- ✅ **Postman**: Test collection
- ✅ **Docker**: Containerized deployment
- ✅ **Database**: Complete SQL script
- ✅ **Documentation**: Detailed README