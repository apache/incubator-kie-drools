package org.drools.process.instance;

public interface EventListener {
	
	void signalEvent(String type, Object event);

}
