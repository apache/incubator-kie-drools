package com.myspace.demo;

import java.util.Optional;

import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;

public class $Type$MessageConsumer {

    Process<$Type$> process;

    Application application;

    Optional<Boolean> useCloudEvents = Optional.of(true);

    EventConsumerFactory eventConsumerFactory = new DefaultEventConsumerFactory();

    public void configure() {}

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
