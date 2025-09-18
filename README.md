
# Luwian

A lightweight Java library that provides observability patterns for Spring Boot and Quarkus applications. Handles structured logging, metrics collection, and error responses without the configuration overhead.

## Quick Start

### Setup
```bash
# Make luwian command globally available
./setup.sh

# If you get "command not found", reload your shell:
source ~/.bashrc  # or ~/.zshrc
```

### Generate Services by Architecture

**Layered Architecture**
```bash
# 1. Create service
luwian "new service orders --pkg com.acme.orders --http-port 8080 --dir .."
```

```bash
# 2. Add handlers
luwian "add handler Order --pkg com.acme.orders --path /api/orders --arch layered --root ../orders"
```

**Hexagonal Architecture**
```bash
# 1. Create service
luwian "new service orders --pkg com.acme.orders --http-port 8080 --dir .."
```

```bash
# 2. Add handlers
luwian "add handler Order --pkg com.acme.orders --path /api/orders --arch hex --root ../orders"
```

**Domain-Driven Design (DDD)**
```bash
# DDD support will be added soon
```


### Run the Generated Service
```bash
cd orders
mvn spring-boot:run
# -> http://localhost:8080/hello
```

## Observability

**Structured Logging**: JSON logs with correlation IDs
**Metrics**: Prometheus metrics with custom tags (service, env, version)
**Error Handling**: Problem+JSON responses with proper HTTP status codes

```bash
# View metrics
curl http://localhost:8080/actuator/prometheus

# View health
curl http://localhost:8080/actuator/health
```

Configuration under `luwian.*` namespace in `application.yml`:
```yaml
luwian:
  logging:
    json: true
  metrics:
    enabled: true
    service: ${spring.application.name:my-service}
    environment: dev
    version: 1.0.0
```

## Modules

- `luwian-core` — Core interfaces, error models, and utilities
- `luwian-starter-spring` — Spring Boot auto-configuration and beans
- `luwian-cli` — Maven-based code generator for service scaffolding
- `examples/` — Sample projects demonstrating usage patterns

## What It Does

- **Structured Logging**: JSON logs with correlation IDs and consistent formatting
- **Metrics Collection**: Micrometer integration with custom tags (service, env, version)
- **Error Responses**: Problem+JSON format with proper HTTP status codes
- **Unified Configuration**: Single `luwian.*` namespace across Spring Boot and Quarkus
- **Code Generation**: CLI tool for layered and hexagonal architectures

## Build
```bash
mvn -q -ntp -B clean verify
```

## Docs
- [Config](CONFIG.md) - All the `luwian.*` keys
- [Errors](ERRORS.md) - Problem+JSON spec
- [Spring Wiring](STARTER-WIRING.md) - Auto-config details
