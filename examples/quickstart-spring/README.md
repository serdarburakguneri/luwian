
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

```bash
# Basic hello endpoint
curl http://localhost:8080/api/hello
```

```bash
# Health check
curl http://localhost:8080/api/health
```

```bash
# Error handling examples (handled by Luwian)
curl http://localhost:8080/api/error/not-found
```

```bash
curl http://localhost:8080/api/error/bad-request
```

```bash
curl http://localhost:8080/api/error/internal
```

```bash
# Metrics collection demo
curl http://localhost:8080/api/metrics-demo
```

```bash
# Echo endpoint for request/response logging
curl -X POST http://localhost:8080/api/echo -H "Content-Type: application/json" -d '{"message": "Hello Luwian!", "test": true}'
```

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
