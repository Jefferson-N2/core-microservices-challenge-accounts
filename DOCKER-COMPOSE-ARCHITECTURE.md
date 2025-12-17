# 🐳 Docker Compose Architecture Guide

## 📋 Overview

Este documento explica cada componente del `docker-compose.yml` y cómo interactúan entre sí para formar la arquitectura completa de microservicios.

## 🏗️ Arquitectura General

```
┌─────────────────────────────────────────────────────────────┐
│                    microservices-network                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   MySQL     │  │  Zookeeper  │  │       Kafka         │  │
│  │   :3306     │  │   :2181     │  │   :9092/:29092      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│           │                                    │            │
│           └────────────────┬───────────────────┘            │
│                           │                                │
│  ┌─────────────────────────┼─────────────────────────────┐  │
│  │                        │                             │  │
│  │  ┌─────────────────┐   │   ┌─────────────────────┐   │  │
│  │  │ Customers       │   │   │    Accounts         │   │  │
│  │  │ Service         │───┼───│    Service          │   │  │
│  │  │ :8080          │   │   │    :8081            │   │  │
│  │  └─────────────────┘   │   └─────────────────────┘   │  │
│  │                        │                             │  │
│  └────────────────────────┼─────────────────────────────┘  │
│                           │                                │
└───────────────────────────┼────────────────────────────────┘
                           │
                    ┌─────────────┐
                    │   Host      │
                    │  Machine    │
                    └─────────────┘
```

## 🔧 Componentes Detallados

### 1. 🗄️ MySQL Database

```yaml
mysql:
  image: mysql:8.0
  container_name: ${COMPOSE_PROJECT_NAME:-core-microservices}-mysql
```

**Propósito**: Base de datos relacional principal para ambos microservicios.

**Configuración**:
- **Puerto**: `3306` (expuesto al host)
- **Base de datos**: `microservices_db`
- **Usuario**: `admin` / `admin123`
- **Root password**: `root`

**Variables de entorno**:
```yaml
MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-root}
MYSQL_DATABASE: ${DB_NAME:-microservices_db}
MYSQL_USER: ${DB_USERNAME:-admin}
MYSQL_PASSWORD: ${DB_PASSWORD:-admin123}
```

**Volúmenes**:
- `mysql_data:/var/lib/mysql` - Persistencia de datos
- `./BaseDatos.sql:/docker-entrypoint-initdb.d/init.sql` - Script de inicialización

**Health Check**:
```yaml
test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
timeout: 20s
retries: 10
```
Verifica que MySQL esté respondiendo antes de que otros servicios dependan de él.

---

### 2. 🐘 Zookeeper

```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.4.0
  container_name: ${COMPOSE_PROJECT_NAME:-core-microservices}-zookeeper
```

**Propósito**: Coordinador de servicios distribuidos requerido por Kafka.

**Configuración**:
- **Puerto**: `2181` (interno)
- **Tick Time**: `2000ms` - Intervalo básico de tiempo

**Variables de entorno**:
```yaml
ZOOKEEPER_CLIENT_PORT: ${ZOOKEEPER_PORT:-2181}
ZOOKEEPER_TICK_TIME: 2000
```

**Health Check**:
```yaml
test: ["CMD", "bash", "-c", "echo 'ruok' | nc localhost 2181"]
```
Envía comando "ruok" (are you ok) a Zookeeper para verificar estado.

**Función en la arquitectura**:
- Mantiene metadatos de Kafka
- Coordina brokers de Kafka
- Gestiona configuración distribuida

---

### 3. 📨 Kafka

```yaml
kafka:
  image: confluentinc/cp-kafka:7.4.0
  container_name: ${COMPOSE_PROJECT_NAME:-core-microservices}-kafka
  depends_on:
    zookeeper:
      condition: service_healthy
```

**Propósito**: Sistema de mensajería para comunicación asíncrona entre microservicios.

**Configuración**:
- **Puerto externo**: `9092` (para conexiones desde host)
- **Puerto interno**: `29092` (para comunicación entre contenedores)
- **Broker ID**: `1`

**Variables de entorno críticas**:
```yaml
KAFKA_ZOOKEEPER_CONNECT: ${ZOOKEEPER_HOST:-zookeeper}:${ZOOKEEPER_PORT:-2181}
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
```

**Listeners explicados**:
- `PLAINTEXT://localhost:9092` - Para conexiones desde el host
- `PLAINTEXT_INTERNAL://kafka:29092` - Para conexiones entre contenedores

**Health Check**:
```yaml
test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
```

**Topics automáticos**:
- `customer.events` - Eventos de ciclo de vida de clientes

---

### 4. 👥 Customers Service

```yaml
customers-service:
  build:
    context: ./core-customers-microservice
    dockerfile: Dockerfile
  depends_on:
    mysql:
      condition: service_healthy
    kafka:
      condition: service_healthy
```

**Propósito**: Microservicio para gestión de clientes y personas.

**Puerto**: `8080` (expuesto al host)

**Dependencias**:
- **MySQL**: Para persistencia de datos
- **Kafka**: Para publicar eventos de clientes

**Variables de entorno**:

*Base de datos*:
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/microservices_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME: ${DB_USERNAME:-admin}
SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-admin123}
```

*Kafka*:
```yaml
SPRING_KAFKA_BOOTSTRAP_SERVERS: ${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}
KAFKA_CUSTOMER_TOPIC: ${KAFKA_CUSTOMER_TOPIC:-customer.events}
```

*Spring Boot*:
```yaml
SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-docker}
SERVER_PORT: 8080
```

**Health Check**:
```yaml
test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
interval: 30s
timeout: 10s
retries: 5
start_period: 60s
```

**Funcionalidades**:
- CRUD de clientes
- Publicación de eventos Kafka
- API REST en puerto 8080
- Swagger UI disponible

---

### 5. 💰 Accounts Service

```yaml
accounts-service:
  build:
    context: ./core-accounts-microservice
    dockerfile: Dockerfile
  depends_on:
    mysql:
      condition: service_healthy
    kafka:
      condition: service_healthy
    customers-service:
      condition: service_healthy
```

**Propósito**: Microservicio para gestión de cuentas y movimientos.

**Puerto**: `8081` (expuesto al host)

**Dependencias**:
- **MySQL**: Para persistencia de datos
- **Kafka**: Para consumir eventos de clientes
- **Customers Service**: Para sincronización de datos

**Variables de entorno adicionales**:
```yaml
KAFKA_CONSUMER_GROUP_ID: ${KAFKA_CONSUMER_GROUP_ID:-accounts-service}
```

**Funcionalidades**:
- CRUD de cuentas y movimientos
- Consumo de eventos Kafka
- Generación de reportes (JSON/Excel)
- Validación de reglas de negocio
- API REST en puerto 8081

---

## 🌐 Red y Comunicación

### Red Unificada
```yaml
networks:
  microservices-network:
    name: ${NETWORK_NAME:-microservices-network}
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

**Características**:
- **Subnet**: `172.20.0.0/16` (65,534 IPs disponibles)
- **Driver**: `bridge` (red local)
- **Aislamiento**: Todos los servicios en la misma red privada

### Comunicación Interna
- **MySQL**: `mysql:3306`
- **Kafka**: `kafka:29092` (interno) / `localhost:9092` (externo)
- **Zookeeper**: `zookeeper:2181`
- **Customers**: `customers-service:8080`
- **Accounts**: `accounts-service:8081`

---

## 💾 Persistencia

### Volúmenes
```yaml
volumes:
  mysql_data:
    driver: local
```

**mysql_data**: Almacena datos de MySQL de forma persistente, sobrevive a reinicios de contenedores.

---

## 🔄 Orden de Inicio

1. **Zookeeper** - Se inicia primero
2. **MySQL** - Se inicia en paralelo con Zookeeper
3. **Kafka** - Espera a que Zookeeper esté healthy
4. **Customers Service** - Espera MySQL y Kafka healthy
5. **Accounts Service** - Espera MySQL, Kafka y Customers Service healthy

---

## 🏥 Health Checks

Cada servicio tiene health checks configurados:

| Servicio | Comando | Intervalo | Timeout | Reintentos |
|----------|---------|-----------|---------|------------|
| MySQL | `mysqladmin ping` | - | 20s | 10 |
| Zookeeper | `echo 'ruok' \| nc localhost 2181` | 10s | 5s | 5 |
| Kafka | `kafka-broker-api-versions` | 10s | 5s | 5 |
| Customers | `curl /actuator/health` | 30s | 10s | 5 |
| Accounts | `curl /actuator/health` | 30s | 10s | 5 |

---

## 🔧 Variables de Configuración

### Principales variables del .env:

| Variable | Propósito | Valor por defecto |
|----------|-----------|-------------------|
| `COMPOSE_PROJECT_NAME` | Prefijo de contenedores | `core-microservices` |
| `DB_URL` | URL de conexión MySQL | `jdbc:mysql://mysql:3306/microservices_db` |
| `KAFKA_BOOTSTRAP_SERVERS` | Servidores Kafka | `kafka:9092` |
| `NETWORK_NAME` | Nombre de la red | `microservices-network` |
| `CUSTOMERS_SERVICE_PORT` | Puerto customers | `8080` |
| `ACCOUNTS_SERVICE_PORT` | Puerto accounts | `8081` |

---

## 🚀 Comandos Útiles

### Gestión de servicios
```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs de un servicio específico
docker-compose logs -f customers-service

# Verificar estado de servicios
docker-compose ps

# Parar todos los servicios
docker-compose down
```

### Inspección de red
```bash
# Ver redes Docker
docker network ls

# Inspeccionar la red de microservicios
docker network inspect microservices-network

# Ver IPs asignadas
docker inspect $(docker-compose ps -q) | grep IPAddress
```

### Verificación de salud
```bash
# Health checks manuales
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health

# Conectar a MySQL
docker exec -it core-microservices-mysql mysql -u admin -p

# Ver topics de Kafka
docker exec -it core-microservices-kafka kafka-topics.sh --bootstrap-server localhost:9092 --list
```

---

## 🎯 Flujo de Datos

1. **Cliente HTTP** → **Customers Service** (puerto 8080)
2. **Customers Service** → **MySQL** (persistencia)
3. **Customers Service** → **Kafka** (evento `customer.events`)
4. **Kafka** → **Accounts Service** (consumo de eventos)
5. **Cliente HTTP** → **Accounts Service** (puerto 8081)
6. **Accounts Service** → **MySQL** (persistencia)

Este flujo garantiza la consistencia eventual entre microservicios mediante eventos asíncronos.