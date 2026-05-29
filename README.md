# SpringWithKafka

Example of Spring Boot projects comprising an API (BFF), a backend processor, and a worker that orchestrates transaction processing through Kafka and PostgreSQL.

## Services

- `bff`: Spring Boot API exposing `POST /api/transaction` — receives transactions and publishes them to Kafka
- `worker`: Spring Boot Kafka consumer that orchestrates transaction processing — consumes transactions from Kafka and delegates balance processing to `backend`
- `backend`: Spring Boot service responsible for balance persistence; reads balances via `GET /api/balance/{accountId}` and applies updates via `PUT /api/balance/{accountId}` directly against PostgreSQL; required by `worker`

## Solution layout

```
SpringWithKafka/
├── bff/        # transactions-api (Spring Boot)
├── worker/     # transaction-worker (Spring Boot)
└── backend/    # transactions-backend (Spring Boot)
```

## Prerequisites

- JDK 21+
- Maven 3.8+
- Docker & Docker Compose

## Local development

Build all services individually:

```bash
cd bff     && ./mvnw clean package -DskipTests
cd worker  && ./mvnw clean package -DskipTests
cd backend && ./mvnw clean package -DskipTests
```

Run tests for each service:

```bash
cd bff     && ./mvnw test
cd worker  && ./mvnw test
cd backend && ./mvnw test
```

Run services individually (local profile):

```bash
cd bff     && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
cd worker  && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Docker Compose

All three services (`bff`, `backend`, and `worker`) are required and run together:

```bash
docker compose up --build
```

`worker` depends on both `kafka` and `backend` being healthy before it starts.

The BFF health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

## PostgreSQL initialization

The `postgresdata` volume is declared as external and must be created before starting the stack:

```bash
docker volume create postgresdata
```

The SQL scripts are **not** mounted automatically. After the `postgres` container is running, apply them in order:

```bash
docker exec -i postgres psql -U postgres < postgres/create_balance_table.sql
docker exec -i postgres psql -U postgres < postgres/create_transaction_table.sql
docker exec -i postgres psql -U postgres < postgres/process_transaction_stored_procedure.sql
```

To reset the database, remove and recreate the volume, then re-run the scripts above:

```bash
docker compose down
docker volume rm postgresdata
docker volume create postgresdata
```

## Notes

- Kafka bootstrap is `localhost:9092` locally and `kafka:29092` in Docker.
- Each service has a dedicated `application-local.yml` and `application-docker.yml` profile.
- `worker` is the default transaction processing path in Compose.