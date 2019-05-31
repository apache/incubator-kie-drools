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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.process.EventListener;
import org.kie.kogito.signal.SignalManager;
import org.kie.kogito.signal.SignalManagerHub;

public class LightSignalManager implements SignalManager {

    private SignalManagerHub signalManagerHub;
	private final EventListenerResolver instanceResolver;
	private ConcurrentHashMap<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

	public LightSignalManager(EventListenerResolver instanceResolver, SignalManagerHub signalManagerHub) {
		this.instanceResolver = instanceResolver;
		this.signalManagerHub = signalManagerHub;
	}
	
	public void addEventListener(String type, EventListener eventListener) {
		listeners.compute(type, (k, v) -> {
			if (v == null) {
				v = new CopyOnWriteArrayList<>();
			}
			v.add(eventListener);
			return v;
		});
		signalManagerHub.subscribe(type, this);
	}
	
	public void removeEventListener(String type, EventListener eventListener) {
		listeners.computeIfPresent(type, (k, v) -> {
			v.remove(eventListener);
			if (v.isEmpty()) {
			    listeners.remove(type);
			}
			return v;
		});
		signalManagerHub.unsubscribe(type, this);
	}
	
	public void signalEvent(String type, Object event) {
	    if (!listeners.containsKey(type)) {
	        signalManagerHub.publish(type, event);
	    }
	    
	    listeners.getOrDefault(type, Collections.emptyList())
				.forEach(e -> e.signalEvent(type, event));
	    

	}

	public void signalEvent(long processInstanceId, String type, Object event) {
		instanceResolver.find(processInstanceId)
				.ifPresent(signalable -> signalable.signalEvent(type, event));
	}	
}
