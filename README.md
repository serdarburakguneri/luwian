
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
