
# Luwian

A Java framework that standardizes logging, metrics, and error handling across Spring Boot and Quarkus apps.

## Quick Start

### Generate a New Service
```bash
# Build the CLI
cd luwian-cli
mvn -q -ntp -B -DskipTests clean package

# Generate a Spring Boot service
java -jar target/luwian-cli-*-runner.jar new service orders \
  --pkg com.acme.orders \
  --http-port 8080

# Or run directly with Maven
mvn -q -ntp -B -DskipTests exec:java -Dexec.mainClass=io.luwian.cli.LuwianCli -- new service orders --pkg com.acme.orders
```

### Run the Generated Service
```bash
cd orders
mvn spring-boot:run
# -> http://localhost:8080/hello
```

## Observability

### Viewing Logs
The generated service includes structured JSON logging with correlation IDs:

```bash
# View logs in JSON format
curl http://localhost:8080/hello
# Check console output for structured JSON logs

# Example log entry:
# {"timestamp":"2025-09-18T10:40:50.123+00:00","level":"INFO","logger":"com.acme.orders.web.HelloController","message":"Processing request","correlationId":"abc123","service":"orders-service"}
```

### Viewing Metrics
Access Prometheus metrics at the actuator endpoint:

```bash
# View all metrics
curl http://localhost:8080/actuator/prometheus

# View health status
curl http://localhost:8080/actuator/health

# View available endpoints
curl http://localhost:8080/actuator
```

**Key Metrics Available:**
- `http_server_requests_seconds` - HTTP request timing with custom tags (service, env, version)
- `jvm_memory_used_bytes` - JVM memory usage
- `system_cpu_usage` - System CPU usage
- `application_ready_time_seconds` - Application startup time

**Custom Tags Applied:**
- `service` - Service name (from `luwian.metrics.service` or `spring.application.name`)
- `env` - Environment (from `luwian.metrics.environment`, default: "dev")
- `version` - Version (from `luwian.metrics.version`, default: "unknown")

### Configuration
All observability settings are under the `luwian.*` namespace in `application.yml`:

```yaml
luwian:
  logging:
    json: true          # Enable JSON structured logging
    http:
      body: false       # Log HTTP request/response bodies (off in prod)
  metrics:
    enabled: true       # Enable Micrometer metrics
    service: ${spring.application.name:my-service}
    environment: dev
    version: 1.0.0
    aop-timed: true     # Enable @Timed annotation support (requires AOP)
  error:
    include-stacktrace: on-trace  # Include stack traces in error responses
```

## What's Inside

- `luwian-core` — Common interfaces and error models
- `luwian-starter-spring` — Spring Boot auto-configuration 
- `luwian-starter-quarkus` — Quarkus integration (coming)
- `luwian-cli` — Project generator: `luwian new service`
- `examples/` — Working examples

## What It Does

**Config**: One `luwian.*` namespace for both Spring and Quarkus. No more hunting through different config files.

**Logging**: JSON logs with correlation IDs. Works the same whether you're on Spring or Quarkus.

**Metrics**: Global tags, HTTP metrics, custom counters. Micrometer under the hood.

**Errors**: Problem+JSON responses with proper HTTP status codes. No more random error formats.

**CLI**: `luwian new service` generates a working Spring Boot app in seconds.

## Build
```bash
# Java 21 + Maven
mvn -q -ntp -B clean verify
```

## Docs
- [Config](CONFIG.md) - All the `luwian.*` keys
- [Errors](ERRORS.md) - Problem+JSON spec
- [Spring Wiring](STARTER-WIRING.md) - How the auto-config works

## Status
Early stage. Spring Boot starter works, CLI generates projects, Quarkus support coming next.
