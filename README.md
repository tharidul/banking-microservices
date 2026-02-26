<div align="center">

# 🏦 Banking Microservices Platform

A production-ready, cloud-native banking system built with **Spring Boot 3.5** and **Spring Cloud 2025.0.1**, featuring JWT authentication, event-driven architecture, distributed tracing, and resilience patterns.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.0.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.4.0-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Microservices](#-microservices)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Reference](#-api-reference)
- [Service Ports](#-service-ports)
- [Configuration](#-configuration)
- [Observability](#-observability)
- [Project Structure](#-project-structure)

---

## 🌟 Overview

This project is a fully functional **banking platform** built on a microservices architecture. It demonstrates real-world enterprise patterns including:

- **Centralized Authentication** via JWT tokens
- **Service Discovery** using Netflix Eureka
- **API Gateway** with JWT validation and route management
- **Event-Driven Communication** via Apache Kafka
- **Fault Tolerance** with Resilience4j Circuit Breaker
- **Distributed Tracing** with Zipkin & Micrometer Brave
- **Metrics & Monitoring** with Prometheus Actuator endpoints
- **Inter-service Communication** via OpenFeign

---

## 🏗️ Architecture

```
                        ┌──────────────────────────────────────────────────┐
                        │              CLIENT (REST API Consumer)           │
                        └────────────────────────┬─────────────────────────┘
                                                 │ HTTP Request
                                                 ▼
                        ┌──────────────────────────────────────────────────┐
                        │          API GATEWAY  (Port: 8080)                │
                        │   • JWT Validation Filter                         │
                        │   • Route Configuration (lb://)                   │
                        │   • Spring Cloud Gateway + WebFlux                │
                        └─────┬────────┬────────┬────────┬────────���────────┘
                              │        │        │        │        │
              ┌───────────────┘        │        │        │        └──────────────────┐
              ▼                        ▼        ▼        ▼                           ▼
  ┌───────────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
  │  auth-service     │  │account-service│  │transaction-  │  │  loan-       │  │  audit-      │
  │  Port: 8081       │  │  Port: 8082  │  │  service     │  │  service     │  │  service     │
  │  JWT Generation   │  │  Account CRUD│  │  Port: 8083  │  │  Port: 8085  │  │  Port: 8086  │
  │  User Auth/Reg    │  │  Deposit/    │  │  Deposit/    │  │  Loan CRUD   │  │  Kafka       │
  │  BCrypt Passwords │  │  Withdraw    │  │  Withdraw/   │  │  OpenFeign   │  │  Consumer    │
  └────────┬──────────┘  └──────┬───────┘  │  Transfer    │  │  Circuit     │  │  Audit Logs  │
           │                    │          └──────┬───────┘  │  Breaker     │  └──────────────┘
           │                    │                 │          └──────────────┘
           │                    │                 │ Kafka Events                        ▲
           │                    │  OpenFeign      │ (transaction-events topic)          │
           │                    └─────────────────┘                                     │
           │                                      │                                     │
           │         ┌────────────────────────────┴─────────────────────────────┐      │
           │         │              KAFKA (Port: 9092) + ZOOKEEPER (2181)        │──────┘
           │         └──────────────────────────────────────────────────────────┘
           │                                      │
           │                                      ▼
           │                        ┌──────────────────────────┐
           │                        │   notification-service    │
           │                        │   Port: 8084              │
           │                        │   Kafka Consumer          │
           │                        │   Transaction Alerts      │
           │                        └──────────────────────────┘
           │
           ▼
  ┌────────────────────────────────────────────────────────────┐
  │              SUPPORTING INFRASTRUCTURE                      │
  │  ┌────────────────────┐   ┌──────────────────────────────┐ │
  │  │  eureka-server     │   │  config-server               │ │
  │  │  Port: 8761        │   │  Port: 8888                  │ │
  │  │  Service Registry  │   │  Centralized Configuration   │ │
  │  └────────────────────┘   └──────────────────────────────┘ │
  └────────────────────────────────────────────────────────────┘
```

---

## 🔧 Microservices

### 1. 🔐 Auth Service (`auth-service`) — Port `8081`

Handles user authentication and JWT token issuance.

**Key Features:**
- User registration with BCrypt password encoding
- JWT token generation and validation (HS256, 24h expiry)
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- Stateless Spring Security with `JwtAuthFilter`
- Duplicate email detection on registration

**Endpoints:**
| Method | Path | Description | Auth Required |
|--------|------|-------------|---------------|
| `POST` | `/api/v1/auth/register` | Register a new user | ❌ |
| `POST` | `/api/v1/auth/login` | Authenticate and receive a JWT | ❌ |

---

### 2. 🏦 Account Service (`account-service`) — Port `8082`

Manages bank accounts with full CRUD operations.

**Key Features:**
- Unique 10-digit account number generation
- Account types: `SAVINGS`, `CURRENT`
- Account status: `ACTIVE`, `CLOSED`
- Deposit and withdraw operations with balance validation
- Integrated with Eureka and Config Server

**Endpoints:**
| Method | Path | Description | Auth Required |
|--------|------|-------------|---------------|
| `POST` | `/api/v1/accounts` | Create a new account | ✅ |
| `GET` | `/api/v1/accounts/user/{userId}` | Get all accounts for a user | ✅ |
| `GET` | `/api/v1/accounts/{id}` | Get account by ID | ✅ |
| `GET` | `/api/v1/accounts/number/{accountNumber}` | Get account by number | ✅ |
| `PUT` | `/api/v1/accounts/{id}/close` | Close an account | ✅ |
| `PUT` | `/api/v1/accounts/{accountNumber}/deposit` | Deposit funds | ✅ |
| `PUT` | `/api/v1/accounts/{accountNumber}/withdraw` | Withdraw funds | ✅ |

---

### 3. 💸 Transaction Service (`transaction-service`) — Port `8083`

Handles all financial transactions with Kafka event publishing and Circuit Breaker resilience.

**Key Features:**
- Supports `DEPOSIT`, `WITHDRAWAL`, and `TRANSFER` transaction types
- Unique UUID-based transaction reference generation
- Calls Account Service via **OpenFeign** client
- **Resilience4j Circuit Breaker** on all account interactions (fallback methods included)
- Publishes `TransactionEvent` to Kafka topic `transaction-events`
- Transaction status: `PENDING`, `COMPLETED`, `FAILED`

**Endpoints:**
| Method | Path | Description | Auth Required |
|--------|------|-------------|---------------|
| `POST` | `/api/v1/transactions/deposit` | Deposit to an account | ✅ |
| `POST` | `/api/v1/transactions/withdraw` | Withdraw from an account | ✅ |
| `POST` | `/api/v1/transactions/transfer` | Transfer between accounts | ✅ |
| `GET` | `/api/v1/transactions/account/{accountNumber}` | Get transaction history | ✅ |
| `GET` | `/api/v1/transactions/{reference}` | Get transaction by reference | ✅ |

---

### 4. 🔔 Notification Service (`notification-service`) — Port `8084`

Listens to Kafka transaction events and sends customer notifications.

**Key Features:**
- Kafka Consumer (group: `notification-group`) on topic `transaction-events`
- Processes `DEPOSIT`, `WITHDRAWAL`, and `TRANSFER` events
- Logs rich customer-facing notification messages
- No database — pure event-driven consumer

---

### 5. 🏛️ Audit Service (`audit-service`) — Port varies

Persists audit logs by consuming Kafka transaction events.

**Key Features:**
- Kafka Consumer on `transaction-events` topic
- Stores transaction audit records to PostgreSQL (`audit_db`)
- Provides traceability for all financial operations
- Distributed tracing via Zipkin

---

### 6. 💰 Loan Service (`loan-service`) — Port varies

Manages loan applications and lifecycle.

**Key Features:**
- Loan entity CRUD with JPA/PostgreSQL
- Inter-service communication via **OpenFeign**
- **Resilience4j Circuit Breaker** integration
- Distributed tracing with Zipkin + Brave
- Prometheus metrics exposure

---

### 7. 🌐 API Gateway (`api-gateway`) — Port `8080`

Single entry point for all client requests with JWT validation.

**Key Features:**
- Spring Cloud Gateway (reactive, WebFlux-based)
- `JwtAuthFilter` validates Bearer tokens on protected routes
- Whitelist: `/api/v1/auth/login` and `/api/v1/auth/register` bypass JWT check
- Routes to all downstream services via Eureka load balancing (`lb://`)
- Request routing:

| Path Pattern | Downstream Service |
|---|---|
| `/api/v1/auth/**` | `auth-service` |
| `/api/v1/accounts/**` | `account-service` |
| `/api/v1/transactions/**` | `transaction-service` |
| `/api/v1/notifications/**` | `notification-service` |
| `/api/v1/audit/**` | `audit-service` |
| `/api/v1/loans/**` | `loan-service` |

---

### 8. 🔍 Eureka Server (`eureka-server`) — Port `8761`

Service registry enabling dynamic service discovery across all microservices.

**Key Features:**
- Netflix Eureka Server (`@EnableEurekaServer`)
- All microservices register themselves on startup
- Used by API Gateway for load-balanced routing (`lb://`)
- Dashboard available at `http://localhost:8761`

---

### 9. ⚙️ Config Server (`config-server`) — Port `8888`

Centralized configuration server for all microservices.

**Key Features:**
- Spring Cloud Config Server
- All services bootstrap with `optional:configserver:http://localhost:8888`

---

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.5.11 |
| **Cloud** | Spring Cloud | 2025.0.1 |
| **Service Discovery** | Netflix Eureka | Spring Cloud 2025.0.1 |
| **API Gateway** | Spring Cloud Gateway | Spring Cloud 2025.0.1 |
| **Config Management** | Spring Cloud Config | Spring Cloud 2025.0.1 |
| **Message Broker** | Apache Kafka | 7.4.0 (Confluent) |
| **Message Coordination** | Apache Zookeeper | 7.4.0 (Confluent) |
| **Database** | PostgreSQL | Latest |
| **ORM** | Spring Data JPA / Hibernate | Spring Boot 3.5.11 |
| **Security** | Spring Security + JWT (JJWT) | 0.11.5 |
| **HTTP Client** | Spring Cloud OpenFeign | Spring Cloud 2025.0.1 |
| **Resilience** | Resilience4j Circuit Breaker | Spring Cloud 2025.0.1 |
| **Tracing** | Micrometer Brave + Zipkin | Spring Boot 3.5.11 |
| **Metrics** | Micrometer Prometheus | Spring Boot 3.5.11 |
| **Build Tool** | Maven Wrapper | 3.9.12 |
| **Boilerplate Reduction** | Lombok | Latest |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Java 21+** — [Download JDK 21](https://adoptium.net/)
- **Maven 3.9+** — (or use the included `./mvnw` wrapper)
- **Docker & Docker Compose** — For Kafka and Zookeeper
- **PostgreSQL** — Running locally or via Docker
- **Zipkin** (optional) — For distributed tracing

---

## 🚀 Getting Started

### Step 1: Start Infrastructure (Kafka + Zookeeper)

A `docker-compose.yml` is provided at the root of the project:

```bash
docker-compose up -d
```

This starts:
- **Zookeeper** on port `2181`
- **Kafka** on port `9092`

### Step 2: Set Up PostgreSQL Databases

Create the following databases in your PostgreSQL instance:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE account_db;
CREATE DATABASE transaction_db;
CREATE DATABASE audit_db;
-- Create a user if needed:
CREATE USER lkml WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO lkml;
GRANT ALL PRIVILEGES ON DATABASE account_db TO lkml;
GRANT ALL PRIVILEGES ON DATABASE transaction_db TO lkml;
GRANT ALL PRIVILEGES ON DATABASE audit_db TO lkml;
```

> ⚠️ **Security Note:** The default credentials (`lkml` / `1234`) are for development only. Update them for any shared or production environment.

### Step 3: Start Services in Order

Start each service in the following order (each in its own terminal or as a background process):

```bash
# 1. Config Server (must start first)
cd config-server && ./mvnw spring-boot:run

# 2. Eureka Server (service discovery)
cd eureka-server && ./mvnw spring-boot:run

# 3. API Gateway
cd api-gateway && ./mvnw spring-boot:run

# 4. Auth Service
cd auth-service && ./mvnw spring-boot:run

# 5. Account Service
cd account-service && ./mvnw spring-boot:run

# 6. Transaction Service
cd transaction-service && ./mvnw spring-boot:run

# 7. Notification Service
cd notification-service && ./mvnw spring-boot:run

# 8. Audit Service
cd audit-service && ./mvnw spring-boot:run

# 9. Loan Service
cd loan-service && ./mvnw spring-boot:run
```

### Step 4: Verify All Services Are Registered

Open the Eureka dashboard: [http://localhost:8761](http://localhost:8761)

---

## 📡 API Reference

All API calls go through the **API Gateway** at `http://localhost:8080`.

### Authentication

#### Register
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "securepassword"
}
```

#### Login
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User logged in successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john.doe@example.com",
    "fullName": "John Doe"
  }
}
```

### Authenticated Requests

Add the JWT token as a Bearer token in all subsequent requests:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### Create Account
```http
POST http://localhost:8080/api/v1/accounts
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "accountType": "SAVINGS"
}
```

#### Deposit Funds
```http
PUT http://localhost:8080/api/v1/accounts/{accountNumber}/deposit?amount=500.00
Authorization: Bearer <token>
```

#### Transfer Funds
```http
POST http://localhost:8080/api/v1/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "fromAccountNumber": "1234567890",
  "toAccountNumber": "0987654321",
  "amount": 250.00,
  "description": "Rent payment"
}
```

---

## 🔌 Service Ports

| Service | Port |
|---------|------|
| API Gateway | `8080` |
| Auth Service | `8081` |
| Account Service | `8082` |
| Transaction Service | `8083` |
| Notification Service | `8084` |
| Loan Service | `8085` |
| Audit Service | `8086` |
| Eureka Server | `8761` |
| Config Server | `8888` |
| Kafka Broker | `9092` |
| Zookeeper | `2181` |
| Zipkin (optional) | `9411` |

---

## ⚙️ Configuration

Each service uses `application.properties` with the following key settings. Sensitive values should be externalized via the Config Server in non-local environments.

| Property | Description |
|----------|-------------|
| `jwt.secret` | Shared HS256 signing secret (must be identical across `auth-service` and `api-gateway`) |
| `jwt.expiration` | Token expiry in milliseconds (default: `86400000` = 24 hours) |
| `spring.datasource.*` | PostgreSQL connection settings |
| `spring.kafka.bootstrap-servers` | Kafka broker address |
| `eureka.client.serviceUrl.defaultZone` | Eureka server URL |

---

## 🔁 Circuit Breaker Configuration

The `transaction-service` and `loan-service` use Resilience4j Circuit Breakers:

```properties
# Sliding window of 5 calls
resilience4j.circuitbreaker.instances.account-service.sliding-window-size=5
# Open circuit if 50% of calls fail
resilience4j.circuitbreaker.instances.account-service.failure-rate-threshold=50
# Wait 10 seconds before trying again
resilience4j.circuitbreaker.instances.account-service.wait-duration-in-open-state=10s
# Allow 3 calls in half-open state
resilience4j.circuitbreaker.instances.account-service.permitted-number-of-calls-in-half-open-state=3
```

---

## 📊 Observability

### Distributed Tracing (Zipkin)

All services are configured with Micrometer Brave and publish traces to Zipkin:

```properties
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
```

Start Zipkin locally:
```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

Then visit: [http://localhost:9411](http://localhost:9411)

### Metrics (Prometheus)

All services expose Prometheus-compatible metrics via Spring Boot Actuator:

```
GET http://localhost:{service-port}/actuator/prometheus
GET http://localhost:{service-port}/actuator/health
GET http://localhost:{service-port}/actuator/metrics
```

---

## 📁 Project Structure

```
banking-microservices/
├── docker-compose.yml              # Kafka + Zookeeper infrastructure
├── .gitignore
│
├── api-gateway/                    # Spring Cloud Gateway + JWT filter
├── auth-service/                   # JWT authentication + user management
├── account-service/                # Bank account management
├── transaction-service/            # Financial transactions + Kafka producer
├── notification-service/           # Kafka consumer for customer notifications
├── audit-service/                  # Kafka consumer for audit logging
├── loan-service/                   # Loan management
├── eureka-server/                  # Netflix Eureka service registry
└── config-server/                  # Spring Cloud Config Server
```

Each service follows the standard Spring Boot layout:
```
{service}/
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/bank/{service}/
    │   │   ├── controller/         # REST controllers
    │   │   ├── service/            # Business logic
    │   │   ├── entity/             # JPA entities & enums
    │   │   ├── repository/         # Spring Data JPA repositories
    │   │   ├── dto/                # Request/Response DTOs
    │   │   ├── kafka/              # Producers, Consumers, Events
    │   │   ├── client/             # OpenFeign clients
    │   │   ├── config/             # Spring configurations
    │   │   └── security/           # Security filters & JWT utilities
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
```

---

## 🔐 Security Architecture

```
Request Flow:
Client ──► API Gateway (JWT Validation) ──► Microservice

Token Flow:
1. POST /api/v1/auth/register  →  User created, JWT returned
2. POST /api/v1/auth/login     →  JWT returned (24h validity)
3. All protected endpoints     →  Bearer <JWT> header required
4. API Gateway validates JWT   →  Uses shared secret key
5. Request forwarded           →  Downstream service trusts gateway
```

- **Algorithm:** HS256 (HMAC-SHA256)
- **Expiry:** 24 hours (86,400,000 ms)
- **Subject:** User email address
- **Password Storage:** BCrypt encoding

---

## 📬 Kafka Event Flow

```
transaction-service  ──publish──►  topic: transaction-events
                                        │
                          ┌────────────┴────────────┐
                          ▼                         ▼
              notification-service           audit-service
              (logs customer alerts)     (persists audit records)
```

**TransactionEvent Payload:**
```json
{
  "transactionReference": "TXN-UUID",
  "transactionType": "DEPOSIT | WITHDRAWAL | TRANSFER",
  "amount": 500.00,
  "fromAccountNumber": "1234567890",
  "toAccountNumber": "0987654321",
  "description": "Payment",
  "createdAt": "2026-02-26T10:30:00"
}
```

---

<div align="center">

Built with ❤️ using **Spring Boot** & **Spring Cloud**

</div>