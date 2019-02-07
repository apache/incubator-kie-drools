/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.services.signal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.process.EventListener;

public class LightSignalManager implements SignalManager {

	private final SignalableResolver instanceResolver;
	private Map<String, List<EventListener>> processEventListeners = new ConcurrentHashMap<String, List<EventListener>>();

	public LightSignalManager(SignalableResolver instanceResolver) {
		this.instanceResolver = instanceResolver;
	}
	
	public void addEventListener(String type, EventListener eventListener) {
		List<EventListener> eventListeners = processEventListeners.get(type);
		//this first "if" is not pretty, but allows to synchronize only when needed
		if (eventListeners == null) {
			synchronized(processEventListeners){
				eventListeners = processEventListeners.get(type);
				if(eventListeners==null){
					eventListeners = new CopyOnWriteArrayList<EventListener>();
					processEventListeners.put(type, eventListeners);
				}
			}
		}		
		eventListeners.add(eventListener);
	}
	
	public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
				if (eventListeners.isEmpty()) {
					processEventListeners.remove(type);
					eventListeners = null;
				}
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
	public void signalEvent(long processInstanceId, String type, Object event) {
		instanceResolver.find(processInstanceId)
				.ifPresent(signalable -> signalable.signalEvent(type, event));
	}	
}
