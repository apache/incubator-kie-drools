package org.drools.event.process;

import org.drools.process.instance.NodeInstance;

public interface NodeEvent extends ProcessEvent {
	
	NodeInstance getNodeInstance();

}
