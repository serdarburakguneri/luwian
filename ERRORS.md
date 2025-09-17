# Luwian — Problem+JSON Error Catalog (v1)

Framework-wide contract used by both Spring and Quarkus starters.

## Media Type
`application/problem+json`

## JSON Schema (minimum shape)
```json
{
  "type": "https://errors.luwian.io/<code>",
  "title": "Human summary",
  "status": 400,
  "detail": "Optional developer-facing detail",
  "instance": "/path/that/errored",
  "correlationId": "…",
  "errorCode": "LUW-XXXX",
  "violations": [ { "field": "name", "message": "must not be blank" } ]
}
```

- Always include `correlationId` (read from MDC / request context).
- Include stacktrace details **only** when tracing is enabled *and* `luwian.error.include-stacktrace=on-trace`.

## Default Exception Mappings

| Exception (base class)                                               | HTTP | errorCode     | type                   | title                      | Notes |
|---|---:|---|---|---|---|
| `jakarta.validation.ConstraintViolationException`                    | 400 | `LUW-VAL-001` | `validation-error`     | Validation failed          | Includes `violations[]`. |
| `org.springframework.web.bind.MethodArgumentNotValidException` (Spring) | 400 | `LUW-VAL-002` | `validation-error`     | Validation failed          | Binding errors. |
| `java.util.NoSuchElementException`                                   | 404 | `LUW-NOT-001` | `not-found`            | Resource not found         | Generic 404. |
| `java.lang.IllegalArgumentException`                                 | 400 | `LUW-REQ-001` | `bad-request`          | Bad request                | Input semantic error. |
| `java.lang.IllegalStateException`                                    | 409 | `LUW-STA-001` | `conflict`             | Invalid state              | Business invariant violation. |
| `java.lang.SecurityException`                                        | 403 | `LUW-SEC-001` | `forbidden`            | Forbidden                  | AuthZ failure. |
| `java.nio.file.AccessDeniedException`                                | 403 | `LUW-SEC-002` | `forbidden`            | Forbidden                  | Spring Security maps too. |
| `java.lang.UnsupportedOperationException`                            | 405 | `LUW-MTH-001` | `method-not-allowed`   | Method not allowed         |  |
| `java.lang.Exception` (fallback)                                     | 500 | `LUW-INT-000` | `internal-error`       | Internal server error      | Don't leak details by default. |

> `type` will later resolve to `https://errors.luwian.io/<type>`. For unknown exceptions use `about:blank`.

## HTTP Headers
- If `traceparent` is present, echo `correlationId` and set `x-correlation-id` on the response.
- Error responses must set `Content-Type: application/problem+json`.

## Examples

### 400 — Validation Error
```json
{
  "type": "https://errors.luwian.io/validation-error",
  "title": "Validation failed",
  "status": 400,
  "detail": "Request body contains invalid fields.",
  "instance": "/orders",
  "correlationId": "b23f…",
  "errorCode": "LUW-VAL-001",
  "violations": [
    {"field": "name", "message": "must not be blank"}
  ]
}
```

### 500 — Internal Server Error
```json
{
  "type": "https://errors.luwian.io/internal-error",
  "title": "Internal server error",
  "status": 500,
  "detail": "An unexpected error occurred.",
  "instance": "/orders",
  "correlationId": "b23f…",
  "errorCode": "LUW-INT-000"
}
```
