/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.services.signal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.signal.SignalManager;

public class LightSignalManager implements SignalManager {

    private EventListenerResolver instanceResolver;
    private ConcurrentHashMap<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public LightSignalManager(EventListenerResolver instanceResolver) {
        this.instanceResolver = instanceResolver;
    }

    public void addEventListener(String type, EventListener eventListener) {
        listeners.compute(type, (k, v) -> {
            if (v == null) {
                v = new CopyOnWriteArrayList<>();
            }
            v.add(eventListener);
            return v;
        });
    }

    public void removeEventListener(String type, EventListener eventListener) {
        listeners.compute(type, (k, v) -> {
            if (v == null) {
                return null;
            }
            v.remove(eventListener);
            if (v.isEmpty()) {
                return null;
            }
            return v;
        });
    }

    public void signalEvent(String type, Object event) {
        if (!listeners.containsKey(type)) {
            if (event instanceof ProcessInstance && listeners.containsKey(((ProcessInstance) event).getProcessId())) {
                listeners.getOrDefault(((ProcessInstance) event).getProcessId(), Collections.emptyList())
                        .forEach(e -> e.signalEvent(type, event));
                return;
            }
        }
        listeners.getOrDefault(type, Collections.emptyList())
                .forEach(e -> e.signalEvent(type, event));
    }

    public void signalEvent(String processInstanceId, String type, Object event) {
        instanceResolver.find(processInstanceId)
                .ifPresent(signalable -> signalable.signalEvent(type, event));
    }

    @Override
    public boolean accept(String type, Object event) {
        if (listeners.containsKey(type)) {
            return true;
        }
        // handle processInstance events that are registered as child processes
        return event instanceof ProcessInstance &&
                listeners.containsKey(((ProcessInstance) event).getProcessId());
    }
}
