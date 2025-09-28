package io.luwian.core.messaging;

import java.util.Map;

/** Minimal message abstraction used for transport-agnostic handlers. */
public interface Message {
    String id();

    String key();

    byte[] payload();

    Map<String, String> headers();
}
