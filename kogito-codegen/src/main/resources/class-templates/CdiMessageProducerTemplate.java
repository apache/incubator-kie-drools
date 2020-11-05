package org.kie.kogito.test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.impl.DefaultEventMarshaller;
import org.kie.kogito.services.event.CloudEventEmitter;
import org.kie.kogito.services.event.impl.AbstractMessageProducer;

@javax.enterprise.context.ApplicationScoped()
public class MessageProducer extends AbstractMessageProducer<$DataType$, $DataEventType$> {

    @javax.inject.Inject()
    CloudEventEmitter emitter;
    @javax.inject.Inject()
    ConfigBean configBean;

    @javax.annotation.PostConstruct
    public void init() {
        setParams(emitter,
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
