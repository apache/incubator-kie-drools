package org.drools.event.process;

import org.drools.runtime.process.NodeInstance;

public interface ProcessNodeEvent
    extends
    ProcessEvent {

    NodeInstance getNodeInstance();

}
