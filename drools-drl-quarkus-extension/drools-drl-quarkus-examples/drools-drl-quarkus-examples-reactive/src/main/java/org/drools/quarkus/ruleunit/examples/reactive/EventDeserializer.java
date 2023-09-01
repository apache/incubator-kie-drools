package org.drools.quarkus.ruleunit.examples.reactive;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class EventDeserializer extends ObjectMapperDeserializer<Event> {

    public EventDeserializer() {
        super(Event.class);
    }
}
