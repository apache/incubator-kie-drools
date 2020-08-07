package com.myspace.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.events.knative.ce.decorators.MessageDecorator;
import org.kie.kogito.events.knative.ce.decorators.MessageDecoratorFactory;
import org.kie.kogito.services.event.DataEventAttrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.TimeZone;

public class MessageProducer {

    Object emitter;

    Optional<Boolean> useCloudEvents = Optional.of(true);

    Optional<MessageDecorator> decorator = MessageDecoratorFactory.newInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger("MessageProducer");

    private ObjectMapper json = new ObjectMapper();

    {
        json.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }

    public void configure() {

    }

    public void produce(ProcessInstance pi, $Type$ eventData) {
        if (decorator.isPresent()) {
        } else {
        }
    }

    private String marshall(ProcessInstance pi, $Type$ eventData) {
        try {

            if (useCloudEvents.orElse(true)) {
                $DataEventType$ event = new $DataEventType$(DataEventAttrBuilder.toType("$channel$", pi),
                        DataEventAttrBuilder.toSource(pi),
                        eventData,
                        pi.getId(),
                        pi.getParentProcessInstanceId(),
                        pi.getRootProcessInstanceId(),
                        pi.getProcessId(),
                        pi.getRootProcessId(),
                        String.valueOf(pi.getState()));
                if (pi.getReferenceId() != null && !pi.getReferenceId().isEmpty()) {
                    event.setKogitoReferenceId(pi.getReferenceId());
                }
                final String eventString = json.writeValueAsString(event);
                LOGGER.debug("CloudEvent marshalled, sending: {}", eventString);
                return eventString;
            } else {
                final String eventString = json.writeValueAsString(eventData);
                LOGGER.debug("Event marshalled, sending: {}", eventString);
                return eventString;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}