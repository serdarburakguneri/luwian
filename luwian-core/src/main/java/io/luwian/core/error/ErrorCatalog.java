package io.luwian.core.error;

import java.util.Optional;

/** SPI to resolve exceptions to catalog entries and payload enrichers. */
public interface ErrorCatalog {
    Optional<ErrorCode> resolve(Throwable error);
}
