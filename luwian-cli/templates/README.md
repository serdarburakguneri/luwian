# Luwian CLI Templates — Hello-world (Spring)

These templates are used by `luwian new service <name>` to scaffold a minimal Spring Boot app with **only** a `/hello` endpoint.  
Business code should be generated later via CLI (e.g., `luwian add handler`), not bundled in quickstarts.

## Variables
| Var        | Example               | Description |
|------------|-----------------------|-------------|
| `service`  | `orders`              | Artifact/folder name |
| `pkg`      | `com.acme.orders`     | Base package |
| `pkgPath`  | `com/acme/orders`     | `pkg` with dots replaced by slashes |
| `httpPort` | `8080`                | HTTP port |

