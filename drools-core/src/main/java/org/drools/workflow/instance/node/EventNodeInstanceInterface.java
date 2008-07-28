package org.drools.workflow.instance.node;

public interface EventNodeInstanceInterface {

	void triggerEvent(String type, Object event);
}
