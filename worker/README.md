# transaction-worker

## Description

This is a **Spring Boot** worker responsible for **processing bank account transactions**. It consumes transaction messages from a Kafka topic, applies balance calculation logic via a strategy pattern (`IBalanceCalculator`), and updates account balances through the backend API.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Apache Kafka (consumer)
- Spring Web (REST client)
- Jackson (JSON deserialization)
- Maven

## Project Structure

```
src/main/java/com/bank/account/transactions/
├── TransactionsExecutor.java              # Application entry point
├── application/
│   ├── engine/                            # Balance calculation strategy
│   │   ├── IBalanceCalculator.java        # Calculator interface
│   │   ├── BalanceCalculatorFactory.java  # Factory to resolve strategy
│   │   └── impl/                          # Strategy implementations
│   └── service/
│       └── TransactionService.java        # Kafka listener / orchestration
├── domain/
│   └── model/                             # Domain models + deserializers
│       └── deserializer/                  # Custom Kafka deserializers
└── infrastructure/
    ├── client/
    │   └── BalanceClient.java             # HTTP client to backend API
    ├── component/                         # Kafka error handler
    ├── config/                            # Jackson & Kafka configuration
    └── util/                              # Utility classes
```

## Prerequisites

- JDK 21+
- Maven 3.8+ (or use the included `mvnw` wrapper)
- Apache Kafka broker running and accessible
- `transactions-backend` service running (balance persistence)
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
docker build -t transaction-worker .

# Run the container
docker run transaction-worker
```

## Configuration

| Profile | Config file                                    |
|---------|------------------------------------------------|
| local   | `src/main/resources/application-local.yml`     |
| docker  | `src/main/resources/application-docker.yml`    |

> _TODO: Document required environment variables (Kafka broker URLs, topic names, consumer group ID, backend API base URL, etc.)._

## Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TransactionApplicationTests

# Run tests with verbose output
./mvnw test -Dsurefire.useFile=false
```

Test reports are generated at:
```
target/surefire-reports/
```