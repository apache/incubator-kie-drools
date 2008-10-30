package org.drools.process.instance;

public interface EventListener {
	
	void signalEvent(String type, Object event);
	
	/**
	 * Returns the event types this event listener is interested in.
	 * May return null if the event types are unknown.
	 */
	String[] getEventTypes();

}
