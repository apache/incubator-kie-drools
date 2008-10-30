package org.drools.event.process;

import org.drools.process.instance.NodeInstance;

public interface ProcessNodeEvent extends ProcessEvent {
	
	NodeInstance getNodeInstance();

}
