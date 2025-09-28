package io.luwian.core.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Outbox pattern storage for reliable messaging. */
public interface OutboxStore {

    record OutboxRecord(
            String id,
            String aggregateId,
            String type,
            byte[] payload,
            Instant dueAt,
            int attempt) {}

    void save(OutboxRecord record, UnitOfWork uow);

    List<OutboxRecord> fetchDue(int limit, Instant now);

    void markProcessed(String id);

    void markFailed(String id, boolean retryable);

    Optional<OutboxRecord> findById(String id);
}
