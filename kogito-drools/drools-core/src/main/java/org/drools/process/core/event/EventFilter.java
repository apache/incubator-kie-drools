package org.drools.process.core.event;

public interface EventFilter {
	
	boolean acceptsEvent(String type, Object event);

}
