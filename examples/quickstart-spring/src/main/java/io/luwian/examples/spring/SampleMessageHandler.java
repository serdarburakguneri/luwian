package io.luwian.examples.spring;

import io.luwian.core.messaging.Envelope;
import io.luwian.core.messaging.Message;
import io.luwian.core.messaging.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleMessageHandler implements MessageHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(SampleMessageHandler.class);

    @Override
    public void handle(Envelope<Message> envelope) {
        log.info(
                "Received message id={} key={}", envelope.message().id(), envelope.message().key());
        envelope.ack();
    }
}
