# transactions-api (BFF)

## Description

This is a **Spring Boot** BFF (Backend for Frontend) API responsible for **receiving bank account transactions for processing **. It exposes REST endpoints to submit transactions, publishes them to **Apache Kafka** for downstream processing and responds with processing result.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Apache Kafka
- Jackson (JSON serialization)
- Maven

## Project Structure

```
src/main/java/com/bank/account/transactions/
├── TransactionApi.java                      # Application entry point
├── application/
│   ├── controller/                           # REST controllers
│   └── settings/                             # Kafka configuration settings
├── domain/
│   └── model/                                # Domain models (Transaction, Account, etc.)
└── infrastructure/
    ├── component/                            # Kafka error handler
    ├── config/                               # Jackson configuration
    ├── messages/                             # Kafka messaging / producers
    └── util/                                 # Utility classes
```

## Prerequisites

- JDK 21+
- Maven 3.8+ (or use the included `mvnw` wrapper)
- Apache Kafka broker running and accessible
- Docker (optional, for containerized run)

## Build

```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

## Run

### Local profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Docker

```bash
# Build the image
docker build -t transactions-api .

# Run the container
docker run -p 8081:8081 transactions-api
```

## Configuration

| Profile | Config file                                    |
|---------|------------------------------------------------|
| local   | `src/main/resources/application-local.yml`     |
| docker  | `src/main/resources/application-docker.yml`    |

> _TODO: Document required environment variables (Kafka broker URLs, topic names, etc.)._

## API Endpoints

| Method | Path            | Description              |
|--------|-----------------|--------------------------|
| POST   | `/api/transaction` | Submit a new transaction |

> _TODO: Complete endpoint list with request/response body examples._

## Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=PoctransactionsApplicationTests

# Run tests with verbose output
./mvnw test -Dsurefire.useFile=false
```

Test reports are generated at:
```
target/surefire-reports/
```