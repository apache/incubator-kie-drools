package org.drools.process.instance.event;

import org.drools.process.instance.EventListener;
import org.drools.process.instance.ProcessInstance;

public interface SignalManager {
	
	void signalEvent(String type, Object event);
	
	void signalEvent(ProcessInstance processInstance, String type, Object event);
	
	void addEventListener(String type, EventListener eventListener);
	
	void removeEventListener(String type, EventListener eventListener);

}
