package com.myspace.demo;



import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.event.impl.DefaultEventMarshaller;
import org.kie.kogito.events.knative.ce.decorators.MessageDecorator;
import org.kie.kogito.events.knative.ce.decorators.MessageDecoratorFactory;
import org.kie.kogito.services.event.DataEventAttrBuilder;
import org.kie.kogito.services.event.EventMarshaller;


import java.util.Optional;

public class MessageProducer {

    Object emitter;

    Optional<Boolean> useCloudEvents = Optional.of(true);

    Optional<MessageDecorator> decorator = MessageDecoratorFactory.newInstance();

    EventMarshaller marshaller = new DefaultEventMarshaller();

    public void configure() {

    }

    public void produce(KogitoProcessInstance pi, $Type$ eventData) {
        if (decorator.isPresent()) {
        } else {
        }
    }
    

    private String marshall(KogitoProcessInstance pi, $Type$ eventData) {
        return marshaller.marshall(eventData, e -> new $DataEventType$(
            DataEventAttrBuilder.toType("$channel$", pi),
            DataEventAttrBuilder.toSource(pi),
            e,
            pi.getStringId(),
            pi.getParentProcessInstanceStringId(),
            pi.getRootProcessInstanceId(),
            pi.getProcessId(),
            pi.getRootProcessId(),
            String.valueOf(pi.getState()),pi.getReferenceId()), useCloudEvents);
    }
}
