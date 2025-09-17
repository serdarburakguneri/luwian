
# Luwian — Enterprise Java Framework

## Modules
- `luwian-core` — cross-cutting abstractions & conventions (logging/metrics/tracing config surfaces, error model skeletons).
- `luwian-starter-spring` — Spring Boot starter wiring for Luwian conventions.
- `luwian-starter-quarkus` — Quarkus extension wiring for Luwian conventions.
- `luwian-cli` — CLI (future) for project generation: `luwian new`, `luwian add`, `luwian deploy`.
- `examples/quickstart-*` — minimal apps demonstrating usage.

## Build
```bash
# Java 21 + Maven
mvn -q -ntp -B clean verify
```

## Status
I am thinking about the architecture and the core interfaces
