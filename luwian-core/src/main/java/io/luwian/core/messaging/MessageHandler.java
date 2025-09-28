package io.luwian.core.messaging;

/** Functional interface for handling messages with ack/nack. */
@FunctionalInterface
public interface MessageHandler<M extends Message> {
    void handle(Envelope<M> envelope) throws Exception;
}
