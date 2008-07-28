package org.drools.workflow.core.node;

public interface EventNodeInterface {
	
	boolean acceptsEvent(String type, Object event);

}
