package org.jbpm.kie.services.impl.event;

import org.kie.api.event.process.ProcessNodeLeftEvent;

public class NodeInstanceLeftEvent extends NodeInstanceEvent {

    private static final long serialVersionUID = -512023758682705814L;

    public NodeInstanceLeftEvent(ProcessNodeLeftEvent event) {
        super(event);
    }

}
