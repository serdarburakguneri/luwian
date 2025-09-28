package io.luwian.core.persistence;

import java.time.Duration;
import java.util.Optional;

/** Idempotency key store for HTTP/messaging handlers. */
public interface IdempotencyStore {

    boolean putIfAbsent(String key, Duration ttl);

    Optional<StoredValue> get(String key);

    void storeResult(String key, byte[] serializedResult);

    record StoredValue(byte[] serializedResult) {}
}
