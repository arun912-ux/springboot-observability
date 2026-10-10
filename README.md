# Spring Boot Observability Demo

A comprehensive demonstration of Spring Boot observability features including distributed tracing, metrics collection, and contextual logging using **OpenTelemetry**, **Micrometer**, and **Prometheus**, coupled with cloud-native persistence, caching, fault tolerance, and secret management.

---

## Tech Stack

- **Java**: 21 (Virtual Threads enabled via `spring.threads.virtual.enabled: true`)
- **Framework**: Spring Boot 4.0.8, Spring Cloud 2025.1.x
- **Observability**:
  - **OpenTelemetry**: Distributed tracing and correlated log export via OTLP (W3C Trace Context and B3 propagation)
  - **Micrometer & Prometheus**: Metric collection, timer histograms, and Actuator metrics export
  - **Logback OpenTelemetry Appender**: Structured logging with embedded trace and span IDs
- **Persistence & Caching**:
  - **PostgreSQL**: Primary data store with Spring Data JPA and HikariCP connection pooling
  - **Redis**: Distributed cache with Lettuce client (configured for Upstash / standalone Redis)
  - **Caffeine**: In-memory high performance local cache
- **Resilience & Client Communication**:
  - **Resilience4j**: Circuit breaking and fallback mechanisms
  - **Spring Resilience**: Method-level retry policies (`@Retryable`)
  - **Spring WebFlux WebClient**: Reactive HTTP client for external weather APIs
  - **Spring Boot RestClient**: Modern synchronous HTTP client
- **Messaging & Security**:
  - **RabbitMQ**: Message-driven architecture via Spring Boot AMQP
  - **OAuth2 Client**: M2M client credentials token management for outbound API calls
- **Secret Management**:
  - **HashiCorp Vault**: External configuration and secret retrieval via `spring-cloud-starter-vault-config`
  - **Infisical**: Dynamic secret synchronization using the Infisical Java SDK
- **API Documentation & Testing**:
  - **SpringDoc OpenAPI / Swagger UI**: Interactive API documentation
  - **Testcontainers**: Isolated integration testing with PostgreSQL and Redis containers

---

## Architecture Overview

```
                      +---------------------------+
                      |       Client / HTTP       |
                      +-------------+-------------+
                                    |
                                    v
                     +-----------------------------+
                     |        WebController        |
                     |  (@Observed, Span Context)  |
                     +--------------+--------------+
                                    |
                                    v
                     +-----------------------------+
                     |       WeatherService        |
                     |  (@Timed, @Cacheable, @Tx)  |
                     +------+---------------+------+
                            |               |
               (Cache Miss) |               | (Cache Hit)
                            v               v
               +--------------------+   +-------------------+
               |   PGDBRepository   |   |    Redis Cache    |
               |    (PostgreSQL)    |   |    / Caffeine     |
               +---------+----------+   +-------------------+
                         |
           (If Not in DB)|
                         v
               +--------------------+
               |   WeatherClient    |
               | (CircuitBreaker,   |
               |  Retry, WebClient) |
               +---------+----------+
                         |
                         v
               +--------------------+
               |   OpenWeatherMap   |
               |    External API    |
               +--------------------+
```

### Key Request Flow
1. **HTTP Ingestion**: Requests arrive at [`WebController`](file:///home/arun/Drives/ARUN_D/WORK/0.Java/spring-observability/src/main/java/org/example/observability/controller/WebController.java), where tracing spans are tagged with request metadata.
2. **Caching**: [`WeatherService`](file:///home/arun/Drives/ARUN_D/WORK/0.Java/spring-observability/src/main/java/org/example/observability/service/WeatherService.java) checks the Redis cache (`city-weather` cache partition).
3. **Database Fallback**: On cache miss, the service checks PostgreSQL for previously persisted weather snapshots.
4. **Resilient Outbound Call**: If not cached or stored, [`WeatherClient`](file:///home/arun/Drives/ARUN_D/WORK/0.Java/spring-observability/src/main/java/org/example/observability/client/WeatherClient.java) calls OpenWeatherMap with circuit breaker protection and retry policies.
5. **Telemetry Export**: Distributed traces, execution metrics, and logs are piped to the OTLP exporter (Fluent Bit / Grafana Cloud).

---

## API Endpoints

### Application Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Basic service health greeting |
| `GET` | `/home` | Sample endpoint demonstrating custom latency instrumentation (`@Observed`) |
| `GET` | `/weather?city={city}` | Fetches weather data for a specified city (cached & persisted) |

### Documentation Endpoints

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Specification**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Actuator Endpoints

- `/actuator/health` - Application and component health status
- `/actuator/metrics` - Detailed application metric listings
- `/actuator/prometheus` - Prometheus scrape endpoint
- `/actuator/threaddump` - Virtual and platform thread diagnostic dump
- `/actuator/trace` - Request trace context
- `/actuator/logfile` - Application log stream

---

## Observability Features

### 1. Distributed Tracing
- Implemented with **OpenTelemetry Java SDK** and **Micrometer Tracing**.
- Context propagation configured for **W3C Trace Context** (`traceparent`, `tracestate`) and **B3** headers.
- Traces automatically instrument HTTP requests, WebClient calls, and database operations.
- Custom span tags added via `Span.current().setAttribute(...)`.

### 2. Metrics & Percentiles
- Micrometer integration with explicit bucket histograms.
- Percentiles published: `0.75`, `0.90`, `0.95`, `0.99`.
- Method timing configured via `@Timed(value = "weatherservice.getweather", histogram = true)`.
- OTLP metric pusher exporting to Grafana Cloud / Fluent Bit collectors.

### 3. Centralized Structured Logging
- Logback configuration (`logback-spring.xml`) integrates [`OpenTelemetryAppender`](file:///home/arun/Drives/ARUN_D/WORK/0.Java/spring-observability/src/main/resources/logback-spring.xml).
- Every log message automatically inherits active `trace_id` and `span_id` for correlated log searching in Grafana Loki.

---

## Configuration & Environment Variables

The application expects the following environment variables to connect to external systems and secret providers:

| Variable | Description | Example / Default |
| :--- | :--- | :--- |
| `OPEN_WEATHER_API_KEY` | API key for OpenWeatherMap API | `your_openweathermap_api_key` |
| `POSTGRES_USERNAME` | PostgreSQL database user | `postgres` |
| `POSTGRES_PASSWORD` | PostgreSQL database password | `secret` |
| `REDIS_PASSWORD` | Redis authentication password | `your_redis_password` |
| `RABBITMQ_USERNAME` | RabbitMQ user | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `guest` |
| `GRAFANA_OTLP_BASIC_AUTH_KEY` | Base64 authorization key for OTLP Grafana Cloud / Fluent Bit exporter | `Basic <base64_credentials>` |
| `M2M_CLIENT_ID` | OAuth2 client credentials ID | `your_client_id` |
| `M2M_CLIENT_SECRET` | OAuth2 client credentials secret | `your_client_secret` |

### Infrastructure Dependencies
- **HashiCorp Vault**: By default, configured to pull secrets from `http://localhost:8200` with root token (`vault://`).
- **Infisical**: Requires valid Infisical project setup (`.infisical.json`) and Universal Auth credentials configured.

---

## Getting Started

### Prerequisites

- **Java 21** or higher (required by Gradle toolchain)
- **Docker** (required for Testcontainers integration tests and local infrastructure)

### Running Locally

1. **Clone the repository**:
   ```bash
   git clone <repo-url>
   cd spring-observability
   ```

2. **Set required environment variables**:
   ```bash
   export OPEN_WEATHER_API_KEY="your_api_key"
   export POSTGRES_USERNAME="postgres"
   export POSTGRES_PASSWORD="password"
   export REDIS_PASSWORD="redis_password"
   export RABBITMQ_USERNAME="guest"
   export RABBITMQ_PASSWORD="guest"
   export GRAFANA_OTLP_BASIC_AUTH_KEY="dummy_key"
   export M2M_CLIENT_ID="dummy_m2m_id"
   export M2M_CLIENT_SECRET="dummy_m2m_secret"
   ```

3. **Start local HashiCorp Vault** (if running with default Vault config):
   ```bash
   docker run -d --name vault-dev -p 8200:8200 -e VAULT_DEV_ROOT_TOKEN_ID="root" hashicorp/vault:latest
   ```

4. **Launch the application**:
   ```bash
   ./gradlew bootRun
   ```

---

## Testing & Verification

### Unit Tests
Executes unit tests and Spring context verification:
```bash
./gradlew test
```

### Integration Tests (Testcontainers)
Spins up isolated PostgreSQL and Redis Docker containers to verify end-to-end repository and caching workflows:
```bash
./gradlew integrationTest
```
*(Requires a running Docker daemon).*

### Full Build
Runs compilation, verification, unit tests, and integration tests:
```bash
./gradlew build
```

---

## Containerization

Build the application Docker container using the multi-stage [`Dockerfile`](file:///home/arun/Drives/ARUN_D/WORK/0.Java/spring-observability/Dockerfile):

```bash
docker build -t spring-observability:latest .
docker run -p 8080:8080 --env-file .env spring-observability:latest
```