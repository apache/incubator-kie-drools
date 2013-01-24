package org.droolsjbpm.services.impl.event;

import org.kie.event.process.ProcessNodeLeftEvent;

public class NodeInstanceLeftEvent extends NodeInstanceEvent {

    private static final long serialVersionUID = -512023758682705814L;

    public NodeInstanceLeftEvent(ProcessNodeLeftEvent event) {
        super(event);
    }

}
