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

    @org.springframework.beans.factory.annotation.Autowired()
    MessageProducer(
            CloudEventEmitter emitter,
            ConfigBean configBean) {
        super(emitter,
              new DefaultEventMarshaller(),
              "$Trigger$",
              configBean.useCloudEvents());
    }

    protected $DataEventType$ dataEventTypeConstructor($DataType$ e, ProcessInstance pi, String trigger) {
        return new $DataEventType$(
                trigger,
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
