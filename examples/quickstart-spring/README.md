
# Luwian Quickstart (Spring) — Example

A Spring Boot application demonstrating the Luwian features:

## Features Demonstrated

- **Error Handling**: Automatic Problem+JSON error responses using core ErrorConstants
- **Metrics Collection**: Automatic timing and tagging with Luwian metrics
- **JSON Logging**: Structured logging with correlation context
- **Configuration**: Luwian-specific configuration properties
- **Core Integration**: Uses luwian-core implementations via luwian-starter-spring

## Running the Example

```bash
# Compile and run
mvn clean spring-boot:run

# Or build and run
mvn clean package
java -jar target/quickstart-spring-0.1.0-SNAPSHOT.jar
```

## Testing Endpoints

- `GET /api/hello` - Basic hello endpoint
- `GET /api/health` - Health check
- `GET /api/error/not-found` - Triggers 404 error (handled by Luwian)
- `GET /api/error/bad-request` - Triggers 400 error (handled by Luwian)
- `GET /api/error/internal` - Triggers 500 error (handled by Luwian)
- `GET /api/metrics-demo` - Demonstrates metrics collection
- `POST /api/echo` - Echo endpoint for request/response logging

## Observability

- **Metrics**: Visit `/actuator/prometheus` for Prometheus metrics
- **Health**: Visit `/actuator/health` for health status
- **Logs**: Check console for structured JSON logs with correlation IDs

## Configuration

The example uses Luwian configuration in `application.yml`:

```yaml
luwian:
  logging:
    json: true
    http:
      body: false
  error:
    include-stacktrace: on-trace
  metrics:
    enabled: true
    service: quickstart-spring
    environment: development
    version: 1.0.0
```
