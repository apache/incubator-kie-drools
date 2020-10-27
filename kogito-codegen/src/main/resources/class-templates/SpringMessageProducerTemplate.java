package org.kie.kogito.test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.services.event.CloudEventEmitter;
import org.kie.kogito.services.event.EventMarshaller;
import org.kie.kogito.event.impl.DefaultEventMarshaller;
import org.kie.kogito.services.event.impl.AbstractMessageProducer;

import java.util.Optional;

@org.springframework.stereotype.Component()
public class MessageProducer extends AbstractMessageProducer<$DataType$, $DataEventType$> {

    // this field will go away when we transition Spring to generic CE handling
    org.kie.kogito.addon.cloudevents.spring.SpringKafkaCloudEventEmitter em;

    @org.springframework.beans.factory.annotation.Autowired()
    MessageProducer(
            // we will use the interface when we transition Spring to generic CE handling
            org.kie.kogito.addon.cloudevents.spring.SpringKafkaCloudEventEmitter emitter,
            ConfigBean configBean) {
        super(emitter,
              new DefaultEventMarshaller(),
              configBean.useCloudEvents());

        // this is only while we wait to transition Spring to the generic interface
        this.em = emitter;
    }

    // this override will go away when we transition Spring to generic CE handling
    public void produce(ProcessInstance pi, $DataType$ eventData) {
        em.emit("$channel$", this.marshall(pi, eventData));
    }

    protected $DataEventType$ dataEventTypeConstructor($DataType$ e, ProcessInstance pi) {
        return new $DataEventType$(
                "",
                e,
                pi.getId(),
                pi.getParentProcessInstanceId(),
                pi.getRootProcessInstanceId(),
                pi.getProcessId(),
                pi.getRootProcessId(),
                String.valueOf(pi.getState()),
                pi.getReferenceId());
    }
}
