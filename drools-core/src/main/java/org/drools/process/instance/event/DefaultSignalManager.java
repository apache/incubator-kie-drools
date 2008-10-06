package org.drools.process.instance.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.process.instance.EventListener;

public class DefaultSignalManager implements SignalManager {
	
	private Map<String, List<EventListener>> processEventListeners;

	public void addEventListener(String type, EventListener eventListener) {
		if (processEventListeners == null) {
			processEventListeners = new HashMap<String, List<EventListener>>();
		}
		List<EventListener> eventListeners = processEventListeners.get(type);
		if (eventListeners == null) {
			eventListeners = new CopyOnWriteArrayList<EventListener>();
			processEventListeners.put(type, eventListeners);
		}
		eventListeners.add(eventListener);
	}
	
	public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
			}
		}
	}
	
	public void signalEvent(String type, Object event) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				for (EventListener eventListener: eventListeners) {
					eventListener.signalEvent(type, event);
				}
			}
		}
	}
	
}
