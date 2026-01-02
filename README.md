# Spring Boot Observability Demo

A comprehensive demonstration of Spring Boot 3.x observability features including metrics, distributed tracing, and logging using Micrometer, OpenTelemetry, and Grafana Cloud.

## Features

- **Distributed Tracing** with OpenTelemetry and W3C Trace Context propagation
- **Metrics Collection** with Micrometer and Prometheus
- **Centralized Logging** (configured for Grafana Cloud)
- **Actuator Endpoints** for monitoring and management
- **Database Operations** with Spring Data JPA and PostgreSQL
- **Caching** with Redis
- **WebClient** for HTTP requests
- **Custom** Metrics and Spans

## Tech Stack

- **Spring Boot 3.5.8**
- **Java 17**
- **PostgreSQL** - Primary database
- **Redis** - For caching
- **Micrometer** - Application metrics
- **OpenTelemetry** - Distributed tracing
- **Grafana Cloud** - For metrics and traces visualization
- **Actuator** - Application monitoring

## Architecture

The application demonstrates a weather service that:
1. Accepts city names as input
2. Fetches weather data (simulated)
3. Caches responses in Redis
4. Stores data in PostgreSQL
5. Emits metrics and traces

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Grafana Cloud account (for metrics and traces)

### Running Locally

1. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```

2. **Configure Environment**:
   Update `application.yml` with your Grafana Cloud credentials.

3. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

## API Endpoints

- `GET /` - Basic health check
- `GET /home` - Sample endpoint with custom observation
- `GET /weather?city={city}` - Get weather for a city

### Actuator Endpoints

- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/threaddump` - Thread dump
- `/actuator/trace` - Request traces

## Observability Features

### Metrics

The application exposes various metrics including:
- HTTP request metrics
- JVM metrics
- Database metrics
- Custom business metrics

### Tracing

Distributed tracing is implemented using OpenTelemetry with:
- W3C Trace Context propagation
- Custom spans and attributes
- Database call tracing
- HTTP client tracing

### Logging

Structured logging with:
- Log levels
- Correlation IDs
- Contextual logging

## Configuration

Key configuration in `application.yml`:
- OpenTelemetry export to Grafana Cloud
- Metrics collection settings
- Tracing sampling
- Actuator endpoints

## Monitoring

Access the Grafana Cloud dashboard to view:
- Application metrics
- Distributed traces
- System health

## Development

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

### Code Style

The project follows standard Java and Spring Boot best practices.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot Team
- OpenTelemetry Community
- Grafana Labs