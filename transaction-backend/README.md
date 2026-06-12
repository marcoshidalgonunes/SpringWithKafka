# transactions-backend

## Description

This is a **Spring Boot** Web API responsible for **transaction operations**. It exposes REST endpoints to manage transactions, persisting data in **PostgreSQL** database. Besides that, it consumes transaction messages from a Kafka topic, saving transactions processed to update balances in database.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Web / Spring JDBC
- Apache Kafka
- Maven

## Project Structure

```
src/main/java/com/bank/account/transactions/
├── TransactionBackend.java          # Application entry point
├── application/
│   ├── consumer/                     # Kafka consumers
│   ├── controller/                   # REST controllers
│   └── service/                      # Business logic
├── domain/
│   └── model/                        # Domain models
└── infrastructure/
    |── config/                       # Configuration wrappers    
    |── repository/                   # Data access layer
    └── util/                         # Utility classes    
```

## Prerequisites

- JDK 21+
- Maven 3.8+ (or use the included `mvnw` wrapper)
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
docker build -t transactions-backend .

# Run the container
docker run -p 8083:8083 transactions-backend
```

## Configuration

| Profile  | Config file                          |
|----------|--------------------------------------|
| local    | `src/main/resources/application-local.yml`  |
| docker   | `src/main/resources/application-docker.yml` |

> _TODO: Document required environment variables (DB URL, Kafka brokers, etc.)._

## API Endpoints

| Method | Path                                             | Description                        |
|--------|--------------------------------------------------|------------------------------------|
| GET    | `/api/transactions/{accountId}/{transactionId}`  | Get transaction by account and id  |
| POST   | `/api/transactions                               | Create transaction                 |

## Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TransactionsApplicationTests

# Run tests with verbose output
./mvnw test -Dsurefire.useFile=false
```

Test reports are generated at:
```
target/surefire-reports/
```