# Core Microservices Challenge - Senior Level Implementation

> 📖 **[Versión en Español](README-ES.md)** | **[English Version](README.md)**

This project implements a complete microservices architecture solution for account and customer management, designed for **Senior level** evaluation with all functionalities F1-F7.

## 🏗️ Architecture Overview

### Microservices Architecture
- **Customers Microservice** (Port 8080): Manages Customer and Person entities
- **Accounts Microservice** (Port 8081): Manages Account and Movement entities
- **Asynchronous Communication**: Kafka-based messaging between services
- **Clean Architecture**: Hexagonal architecture with clear separation of concerns

### Technology Stack
- **Java 21** with Spring Boot 3.5.8
- **Spring WebFlux** for reactive programming
- **MySQL** as primary database
- **Apache Kafka** for asynchronous communication
- **Docker & Docker Compose** for containerization
- **OpenAPI 3.0** for Contract-First development
- **JUnit 5 & Mockito** for testing
- **H2 Database** for testing (in-memory)
- **Lombok & MapStruct** for code generation

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Maven 3.8+

### 1. Deploy with Docker (F7 - Containerized Deployment)
```bash
# Clone and navigate to project
cd core-microservices-challenge-accounts

# Start all services (MySQL, Kafka, Zookeeper, and both microservices)
docker-compose up -d

# Check services status
docker-compose ps
```

### 2. Local Development
```bash
# Start infrastructure only
docker-compose up -d mysql kafka zookeeper

# Run customers microservice
cd core-customers-microservice
mvn spring-boot:run

# Run accounts microservice (in another terminal)
cd core-accounts-microservice
mvn spring-boot:run
```

## 📋 Implemented Functionalities

### ✅ F1: CRUD Operations
- **Customers**: `/api/v1/customers` (GET, POST, PUT, DELETE)
- **Accounts**: `/api/v1/accounts` (GET, POST, PUT, DELETE)
- **Movements**: `/api/v1/movements` (GET, POST, PUT, DELETE)

### ✅ F2: Movement Registration Business Rules
- ✅ Movement value must be greater than zero
- ✅ Debito movements subtract from available balance
- ✅ Credito movements add to available balance
- ✅ All transactions are properly registered

### ✅ F3: Insufficient Balance Validation
- ✅ Returns "Saldo no disponible" message when balance is insufficient
- ✅ Proper error handling with HTTP 409 Conflict status

### ✅ F4: Account Statement Reports
- ✅ Endpoint: `/api/v1/reports/{client-id}?startDate=fecha&endDate=fecha`
- ✅ Returns account balances and movement details
- ✅ Supports both JSON and Excel formats

### ✅ F5: Unit Tests
- ✅ Comprehensive MovementService unit tests
- ✅ Tests cover all business rules (F2, F3)
- ✅ Mock-based testing with Mockito

### ✅ F6: Integration Tests
- ✅ End-to-end movement processing tests
- ✅ WebTestClient for reactive testing
- ✅ H2 in-memory database for testing

### ✅ F7: Containerized Deployment
- ✅ Docker containers for both microservices
- ✅ Docker Compose orchestration
- ✅ MySQL, Kafka, and Zookeeper containers

## 🔧 API Documentation

### Service URLs
- **Customers Service**: http://localhost:8080
- **Accounts Service**: http://localhost:8081
- **Swagger UI**: 
  - Customers: http://localhost:8080/swagger-ui.html
  - Accounts: http://localhost:8081/swagger-ui.html

### Sample API Calls

#### Create Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jose Lema",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "password": "1234",
    "status": true
  }'
```

#### Create Account
```bash
curl -X POST http://localhost:8081/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Corriente",
    "initialBalance": 2000.00,
    "identification": "1234567890"
  }'
```

#### Create Movement
```bash
curl -X POST http://localhost:8081/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Debito",
    "value": 575.00,
    "numberAccount": "478758",
    "description": "Retiro de 575"
  }'
```

#### Generate Report
```bash
curl "http://localhost:8081/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json"
```

## 🧪 Testing

### Run Unit Tests
```bash
# Accounts microservice tests
cd core-accounts-microservice
mvn test

# Customers microservice tests
cd core-customers-microservice
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Postman Collection
Import `Core-Microservices-Challenge.postman_collection.json` for comprehensive API testing.

## 📊 Database Schema

The `BaseDatos.sql` file contains:
- Complete MySQL schema with constraints and indexes
- Sample data for testing all use cases
- Proper foreign key relationships

## 🔄 Asynchronous Communication

### Kafka Topics
- `customer.events`: Customer lifecycle events
- Producers in Customers service
- Consumers in Accounts service

### Event Types
- `customer.created`: New customer registration
- `customer.updated`: Customer information changes
- `customer.deleted`: Customer deactivation

## 🏛️ Architecture Patterns

### Hexagonal Architecture
- **Domain**: Core business logic and entities
- **Application**: Use cases and ports
- **Infrastructure**: Adapters for external systems

### Design Patterns
- Repository Pattern for data access
- Adapter Pattern for external integrations
- Factory Pattern for entity creation
- Strategy Pattern for different report formats

## 🔒 Quality Assurance

### Code Quality
- Lombok for boilerplate reduction
- MapStruct for object mapping
- SonarQube-ready code structure
- Comprehensive logging with SLF4J

### Error Handling
- Global exception handling with `@RestControllerAdvice`
- Proper HTTP status codes
- Detailed error messages
- Business rule validation

### Performance & Scalability
- Reactive programming with WebFlux
- Database connection pooling
- Async processing with Kafka
- Stateless microservices design

## 📈 Monitoring & Observability

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- Kafka connectivity monitoring

### Logging
- Structured logging with correlation IDs
- Business event logging
- Error tracking and alerting

## 🚀 Production Considerations

### Security
- Input validation with Bean Validation
- SQL injection prevention with JPA
- Password encryption (BCrypt)
- CORS configuration

### Resilience
- Circuit breaker patterns (ready for implementation)
- Retry mechanisms
- Timeout configurations
- Graceful degradation

### Scalability
- Horizontal scaling support
- Load balancer ready
- Database connection pooling
- Kafka partitioning strategy

## 📝 Development Notes

### Code Standards
- English naming conventions
- Constructor-based dependency injection
- Immutable DTOs where possible
- Comprehensive JavaDoc documentation

### Testing Strategy
- **Unit Tests**: JUnit 5 + Mockito for business logic
- **Integration Tests**: WebTestClient for API endpoints
- **Reactive Testing**: StepVerifier for reactive streams
- **Database Testing**: H2 in-memory database
- **Contract Testing**: OpenAPI specification validation
- **Performance Testing**: Ready for implementation

## 🤝 Contributing

This project follows clean code principles and SOLID design patterns. All contributions should maintain the established architecture and coding standards.

## 📞 Support

For technical questions or deployment issues, refer to the comprehensive logging and error messages provided by the application.