package org.kie.kogito.test;

import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.CloudEventEmitter;
import org.kie.kogito.event.impl.DefaultEventMarshaller;
import org.kie.kogito.services.event.impl.AbstractMessageProducer;

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

    protected $DataEventType$ dataEventTypeConstructor($DataType$ e, KogitoProcessInstance pi, String trigger) {
        return new $DataEventType$(
                trigger,
                "",
                e,
                pi.getStringId(),
                pi.getParentProcessInstanceStringId(),
                pi.getRootProcessInstanceId(),
                pi.getProcessId(),
                pi.getRootProcessId(),
                String.valueOf(pi.getState()),
                pi.getReferenceId());
    }
}
