package com.myspace.demo;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;

public class $Type$MessageConsumer {

    Process<$Type$> process;

    Application application;

    ObjectMapper objectMapper;

    Optional<Boolean> useCloudEvents = Optional.of(true);

    EventConsumerFactory eventConsumerFactory;

    public void configure() {
        eventConsumerFactory = new DefaultEventConsumerFactory(objectMapper);
    }

    public void consume(String payload) {
        eventConsumerFactory
            .get(event -> {
                $Type$ model = new $Type$();
                model.set$ModelRef$(event);
                return model;
            }, $DataType$.class, $DataEventType$.class, useCloudEvents)
            .consume(application, process, payload, "$Trigger$");
    }
}
