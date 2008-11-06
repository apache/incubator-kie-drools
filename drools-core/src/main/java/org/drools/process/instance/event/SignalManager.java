package org.drools.process.instance.event;

import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.ProcessInstance;

public interface SignalManager {
	
	void signalEvent(String type, Object event);
	
	void signalEvent(ProcessInstance processInstance, String type, Object event);
	
	void addEventListener(String type, EventListener eventListener);
	
	void removeEventListener(String type, EventListener eventListener);

}
