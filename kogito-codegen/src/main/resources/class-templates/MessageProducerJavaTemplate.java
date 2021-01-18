package com.myspace.demo;

import java.util.Optional;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.impl.DefaultEventMarshaller;
import org.kie.kogito.services.event.EventMarshaller;

public class MessageProducer {

    Object emitter;

    Optional<Boolean> useCloudEvents = Optional.of(true);

    EventMarshaller marshaller = new DefaultEventMarshaller();

    public void configure() {

    }

    public void produce(ProcessInstance pi, $Type$ eventData) {

    }

    private String marshall(ProcessInstance pi, $Type$ eventData) {
        return marshaller.marshall(eventData,
                                   e -> new $DataEventType$("",
                                                            e,
                                                            pi.getId(),
                                                            pi.getParentProcessInstanceId(),
                                                            pi.getRootProcessInstanceId(),
                                                            pi.getProcessId(),
                                                            pi.getRootProcessId(),
                                                            String.valueOf(pi.getState()),
                                                            pi.getReferenceId() == null || pi.getReferenceId().trim().isEmpty() ? null : pi.getReferenceId()),
                                   useCloudEvents);
    }
}