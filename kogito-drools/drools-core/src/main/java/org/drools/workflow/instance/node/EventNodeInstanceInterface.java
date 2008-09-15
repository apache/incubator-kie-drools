package org.drools.workflow.instance.node;

public interface EventNodeInstanceInterface {

	void signalEvent(String type, Object event);
	
}
