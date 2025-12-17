# Core Microservices Challenge - Caso Práctico

> 📖 **[English Version](README-EN.md)** | **[Versión en Español](README.md)**

Solución completa de arquitectura de microservicios para gestión de cuentas y clientes, implementando todas las funcionalidades F1-F7.

## 🏗️ Arquitectura

### Microservicios
- **Customers Service** (Puerto 8080): Gestión de clientes y personas
- **Accounts Service** (Puerto 8081): Gestión de cuentas y movimientos
- **Comunicación asíncrona**: Kafka para eventos entre servicios

### Tecnologías
- **Java 21** + Spring Boot 3.5.8 + WebFlux
- **MySQL** (Base de datos)
- **Apache Kafka** (Comunicación asíncrona)
- **Docker Compose** (Orquestación)
- **OpenAPI 3.0** (Contract-First)
- **JUnit 5** + Mockito (Testing)

## 🚀 Despliegue

### Requisitos
- Docker & Docker Compose
- Java 21 (desarrollo local)

### Pasos de Instalación

#### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd core-microservices-challenge-accounts
```

#### 2. Configurar Variables de Entorno
```bash
# Copiar archivo de configuración
cp .env.example .env

# Opcional: Editar variables si es necesario
# nano .env
```

#### 3. Inicializar Base de Datos
```bash
# El script BaseDatos.sql se ejecuta automáticamente
# al iniciar MySQL por primera vez
```

#### 4. Desplegar Servicios
```bash
# Construir e iniciar todos los servicios
docker-compose up --build -d

# Verificar que todos los servicios estén corriendo
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f
```

#### 5. Verificar Despliegue
```bash
# Health check de servicios
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### URLs de Servicios
- **Customers**: http://localhost:8080
- **Accounts**: http://localhost:8081
- **Swagger UI Customers**: http://localhost:8080/swagger-ui.html
- **Swagger UI Accounts**: http://localhost:8081/swagger-ui.html
- **MySQL**: localhost:3306 (admin/admin123)
- **Kafka**: localhost:9092

### Comandos Útiles
```bash
# Parar servicios
docker-compose down

# Parar y limpiar volúmenes
docker-compose down -v

# Reconstruir servicios
docker-compose build --no-cache

# Ver logs de un servicio específico
docker-compose logs -f customers-service
docker-compose logs -f accounts-service
```

## 📋 Funcionalidades Implementadas

| Funcionalidad | Descripción | Estado |
|---------------|-------------|--------|
| **F1** | CRUD Completo (Customers, Accounts, Movements) | ✅ |
| **F2** | Reglas de negocio para movimientos | ✅ |
| **F3** | Validación "Saldo no disponible" | ✅ |
| **F4** | Reportes de estado de cuenta | ✅ |
| **F5** | Pruebas unitarias | ✅ |
| **F6** | Pruebas de integración | ✅ |
| **F7** | Despliegue con Docker | ✅ |

### Endpoints Principales
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

## 🔧 Casos de Uso

### 1. Crear Cliente
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

### 2. Crear Cuenta
```json
POST /api/v1/accounts
{
  "type": "Ahorros",
  "initialBalance": 2000.00,
  "identification": "1234567890"
}
```

### 3. Registrar Movimiento
```json
POST /api/v1/movements
{
  "type": "Debito",
  "value": 575.00,
  "numberAccount": "478758",
  "description": "Retiro de 575"
}
```

### 4. Generar Reporte
```
GET /api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json
```

## 🧪 Testing

```bash
# Ejecutar pruebas unitarias
mvn test

# Ejecutar pruebas de integración
mvn test -Dtest=*IntegrationTest
```

### Colección Postman
- Importar: `Core-Microservices-Challenge.postman_collection.json`
- Incluye todos los casos de uso de la prueba técnica

## 🔄 Comunicación Asíncrona

### Kafka Events
```
customer.events topic:
├── CUSTOMER_CREATED
├── CUSTOMER_UPDATED
└── CUSTOMER_DELETED
```

**Flujo**: Customers Service → Kafka → Accounts Service

## 📊 Base de Datos

### Esquema
- **BaseDatos.sql**: Schema completo con datos de prueba
- **Entidades**: Person, Customer, Account, Movement
- **Relaciones**: FK constraints y índices

### Configuración
```yaml
MySQL: localhost:3306/microservices_db
User: admin / Password: admin123
```

## 🏛️ Arquitectura

### Patrones Implementados
- **Hexagonal Architecture**: Domain, Application, Infrastructure
- **Repository Pattern**: Acceso a datos
- **Adapter Pattern**: Integraciones externas
- **Event-Driven**: Comunicación asíncrona

### Calidad de Código
- **Clean Code**: Principios SOLID
- **Contract-First**: OpenAPI specifications
- **Reactive Programming**: Spring WebFlux
- **Error Handling**: Global exception management

## 📈 Monitoreo

### Health Checks
```
/actuator/health - Estado de servicios
/actuator/metrics - Métricas de aplicación
```

### Logging
- Structured logging con SLF4J
- Correlation IDs para trazabilidad
- Business events tracking

## 🚀 Consideraciones de Producción

### Seguridad
- Validación de entrada (Bean Validation)
- Prevención SQL injection (JPA)
- Manejo seguro de contraseñas

### Escalabilidad
- Stateless microservices
- Connection pooling
- Horizontal scaling ready
- Load balancer compatible

## 📁 Estructura del Proyecto

```
core-microservices-challenge-accounts/
├── core-customers-microservice/     # Servicio de clientes
├── core-accounts-microservice/      # Servicio de cuentas
├── docker-compose.yml              # Orquestación
├── .env                           # Variables de entorno
├── BaseDatos.sql                  # Schema de BD
└── Core-Microservices-Challenge.postman_collection.json
```

## 🎯 Entregables

- ✅ **Código fuente**: Repositorio Git completo
- ✅ **OpenAPI**: Especificaciones YAML
- ✅ **Postman**: Colección de pruebas
- ✅ **Docker**: Despliegue containerizado
- ✅ **Base de datos**: Script SQL completo
- ✅ **Documentación**: README detallado