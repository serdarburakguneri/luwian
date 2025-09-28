package io.luwian.core.persistence;

import java.time.Instant;

/** Inbox pattern storage for deduplication/idempotent message processing. */
public interface InboxStore {

    record InboxRecord(String id, String source, Instant receivedAt) {}

    boolean markIfNew(InboxRecord record);
}
