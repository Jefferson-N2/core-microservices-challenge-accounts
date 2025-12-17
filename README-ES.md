# Desafío Microservicios Core - Implementación Nivel Senior

Este proyecto implementa una solución completa de arquitectura de microservicios para gestión de cuentas y clientes, diseñada para evaluación de **nivel Senior** con todas las funcionalidades F1-F7.

## 🏗️ Resumen de Arquitectura

### Arquitectura de Microservicios
- **Microservicio Clientes** (Puerto 8080): Gestiona entidades Cliente y Persona
- **Microservicio Cuentas** (Puerto 8081): Gestiona entidades Cuenta y Movimiento
- **Comunicación Asíncrona**: Mensajería basada en Kafka entre servicios
- **Arquitectura Limpia**: Arquitectura hexagonal con clara separación de responsabilidades

### Stack Tecnológico
- **Java 21** con Spring Boot 3.5.8
- **Spring WebFlux** para programación reactiva
- **MySQL** como base de datos principal
- **Apache Kafka** para comunicación asíncrona
- **Docker & Docker Compose** para contenerización
- **OpenAPI 3.0** para desarrollo Contract-First
- **JUnit 5 & Mockito** para testing
- **Lombok & MapStruct** para generación de código

## 🚀 Inicio Rápido

### Prerrequisitos
- Docker y Docker Compose
- Java 21 (para desarrollo local)
- Maven 3.8+

### 1. Despliegue con Docker (F7 - Despliegue Contenerizado)
```bash
# Clonar y navegar al proyecto
cd core-microservices-challenge-accounts

# Iniciar todos los servicios (MySQL, Kafka, Zookeeper, y ambos microservicios)
docker-compose up -d

# Verificar estado de servicios
docker-compose ps
```

### 2. Desarrollo Local
```bash
# Iniciar solo infraestructura
docker-compose up -d mysql kafka zookeeper

# Ejecutar microservicio clientes
cd core-customers-microservice
mvn spring-boot:run

# Ejecutar microservicio cuentas (en otra terminal)
cd core-accounts-microservice
mvn spring-boot:run
```

## 📋 Funcionalidades Implementadas

### ✅ F1: Operaciones CRUD
- **Clientes**: `/api/v1/customers` (GET, POST, PUT, DELETE)
- **Cuentas**: `/api/v1/accounts` (GET, POST, PUT, DELETE)
- **Movimientos**: `/api/v1/movements` (GET, POST, PUT, DELETE)

### ✅ F2: Reglas de Negocio para Registro de Movimientos
- ✅ El valor del movimiento debe ser mayor que cero
- ✅ Movimientos DeBITO se restan del saldo disponible
- ✅ Movimientos CréDITO se suman al saldo disponible
- ✅ Todas las transacciones se registran correctamente

### ✅ F3: Validación de Saldo Insuficiente
- ✅ Retorna mensaje "Saldo no disponible" cuando el saldo es insuficiente
- ✅ Manejo adecuado de errores con estado HTTP 409 Conflict

### ✅ F4: Reportes de Estado de Cuenta
- ✅ Endpoint: `/api/v1/reports/{client-id}?startDate=fecha&endDate=fecha`
- ✅ Retorna saldos de cuentas y detalles de movimientos
- ✅ Soporta formatos JSON y Excel

### ✅ F5: Pruebas Unitarias
- ✅ Pruebas completas del MovementService
- ✅ Las pruebas cubren todas las reglas de negocio (F2, F3)
- ✅ Testing basado en mocks con Mockito

### ✅ F6: Pruebas de Integración
- ✅ Pruebas end-to-end de procesamiento de movimientos
- ✅ WebTestClient para testing reactivo
- ✅ Base de datos H2 en memoria para testing

### ✅ F7: Despliegue Contenerizado
- ✅ Contenedores Docker para ambos microservicios
- ✅ Orquestación con Docker Compose
- ✅ Contenedores MySQL, Kafka, y Zookeeper

## 🔧 Documentación de API

### URLs de Servicios
- **Servicio Clientes**: http://localhost:8080
- **Servicio Cuentas**: http://localhost:8081
- **Swagger UI**: 
  - Clientes: http://localhost:8080/swagger-ui.html
  - Cuentas: http://localhost:8081/swagger-ui.html

### Ejemplos de Llamadas API

#### Crear Cliente
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jefferson Noroña",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "password": "<password>",
    "status": true
  }'
```

#### Crear Cuenta
```bash
curl -X POST http://localhost:8081/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Corriente",
    "initialBalance": 2000.00,
    "identification": "1234567890"
  }'
```

#### Crear Movimiento
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

#### Generar Reporte
```bash
curl "http://localhost:8081/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31&format=json"
```

## 🧪 Testing

### Ejecutar Pruebas Unitarias
```bash
# Pruebas del microservicio cuentas
cd core-accounts-microservice
mvn test

# Pruebas del microservicio clientes
cd core-customers-microservice
mvn test
```

### Ejecutar Pruebas de Integración
```bash
mvn test -Dtest=*IntegrationTest
```

### Colección Postman
Importar `Core-Microservices-Challenge.postman_collection.json` para testing completo de APIs.

## 📊 Esquema de Base de Datos

El archivo `BaseDatos.sql` contiene:
- Esquema completo de MySQL con restricciones e índices
- Datos de ejemplo para probar todos los casos de uso
- Relaciones de claves foráneas apropiadas

## 🔄 Comunicación Asíncrona

### Tópicos Kafka
- `customer.events`: Eventos del ciclo de vida del cliente
- Productores en el servicio Clientes
- Consumidores en el servicio Cuentas

### Tipos de Eventos
- `customer.created`: Registro de nuevo cliente
- `customer.updated`: Cambios en información del cliente
- `customer.deleted`: Desactivación del cliente

## 🏛️ Patrones de Arquitectura

### Arquitectura Hexagonal
- **Dominio**: Lógica de negocio central y entidades
- **Aplicación**: Casos de uso y puertos
- **Infraestructura**: Adaptadores para sistemas externos

### Patrones de Diseño
- Patrón Repository para acceso a datos
- Patrón Adapter para integraciones externas
- Patrón Factory para creación de entidades
- Patrón Strategy para diferentes formatos de reporte

## 🔒 Aseguramiento de Calidad

### Calidad de Código
- Lombok para reducción de código repetitivo
- MapStruct para mapeo de objetos
- Estructura de código lista para SonarQube
- Logging completo con SLF4J

### Manejo de Errores
- Manejo global de excepciones con `@RestControllerAdvice`
- Códigos de estado HTTP apropiados
- Mensajes de error detallados
- Validación de reglas de negocio

### Rendimiento y Escalabilidad
- Programación reactiva con WebFlux
- Pool de conexiones de base de datos
- Procesamiento asíncrono con Kafka
- Diseño de microservicios sin estado

## 📈 Monitoreo y Observabilidad

### Health Checks
- Endpoints de Spring Boot Actuator
- Verificaciones de conectividad de base de datos
- Monitoreo de conectividad Kafka

### Logging
- Logging estructurado con IDs de correlación
- Logging de eventos de negocio
- Seguimiento y alertas de errores

## 🚀 Consideraciones de Producción

### Seguridad
- Validación de entrada con Bean Validation
- Prevención de inyección SQL con JPA
- Encriptación de contraseñas (BCrypt)
- Configuración CORS

### Resiliencia
- Patrones circuit breaker (listo para implementación)
- Mecanismos de reintento
- Configuraciones de timeout
- Degradación elegante

### Escalabilidad
- Soporte para escalado horizontal
- Listo para balanceador de carga
- Pool de conexiones de base de datos
- Estrategia de particionado Kafka

## 📝 Notas de Desarrollo

### Estándares de Código
- Convenciones de nomenclatura en inglés
- Inyección de dependencias basada en constructor
- DTOs inmutables donde sea posible
- Documentación completa con JavaDoc

### Estrategia de Testing
- Pruebas unitarias para lógica de negocio
- Pruebas de integración para endpoints de API
- Testing de contratos con OpenAPI
- Listo para pruebas de rendimiento

## 🤝 Contribución

Este proyecto sigue principios de código limpio y patrones de diseño SOLID. Todas las contribuciones deben mantener la arquitectura establecida y estándares de codificación.

## 📞 Soporte

Para preguntas técnicas o problemas de despliegue, consultar el logging completo y mensajes de error proporcionados por la aplicación.