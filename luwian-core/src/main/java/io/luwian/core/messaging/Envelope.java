package io.luwian.core.messaging;

/** Wrapper for a message with delivery semantics. */
public interface Envelope<M extends Message> {
    M message();

    void ack();

    void nack(boolean retryable);
}
