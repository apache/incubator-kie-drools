package org.drools.process.instance.event;

import org.drools.process.instance.EventListener;

public interface SignalManager {
	
	void signalEvent(String type, Object event);
	
	void addEventListener(String type, EventListener eventListener);
	
	void removeEventListener(String type, EventListener eventListener);

}
