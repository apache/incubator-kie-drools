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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.process.EventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.SignalFactory;
import org.kie.kogito.signal.ProcessInstanceResolver;
import org.kie.kogito.signal.SignalManagerHub;

import static java.util.Collections.emptyList;
import static java.util.Collections.synchronizedSet;

public class DefaultSignalManagerHub implements SignalManagerHub {

    private Set<ProcessInstanceResolver<?>> workflowInstanceResolver = synchronizedSet(new HashSet<>());
    private ConcurrentMap<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

    @Override
    public boolean accept(String eventType, Object event) {
        if (listeners.containsKey(eventType)) {
            return true;
        }
        return workflowInstanceResolver.stream().map(e -> e.waitingForEvents(eventType)).flatMap(List::stream).count() > 0;
    }

    @Override
    public void signalEvent(String eventType, Object payload) {
        // we signal memory first
        List<String> idList = new ArrayList<>();
        listeners.getOrDefault(eventType, emptyList()).forEach(eventListener -> {
            if (eventListener instanceof KogitoProcessInstance kogitoProcessInstance) {
                idList.add(kogitoProcessInstance.getId());
                ProcessInstance<?> processInstance = kogitoProcessInstance.unwrap();
                // this will enforce access to the lock mechanism logic.
                processInstance.send(SignalFactory.of(eventType, processInstance));
            } else {
                eventListener.signalEvent(eventType, payload);
            }
        });

        var processInstancesWaiting = workflowInstanceResolver.stream()
                .map(e -> e.waitingForEvents(eventType))
                .flatMap(List::stream)
                .filter(p -> !idList.contains(p.id()))
                .toList();

        processInstancesWaiting.forEach(eventListener -> eventListener.send(SignalFactory.of(eventType, payload)));
    }

    @Override
    public void signalEvent(String processInstanceId, String eventType, Object payload) {
        workflowInstanceResolver.stream()
                .map(e -> e.findById(processInstanceId))
                .filter(Objects::nonNull)
                .forEach(eventListener -> eventListener.send(SignalFactory.of(eventType, payload)));
    }

    @Override
    public void addEventListener(String eventType, EventListener eventListener) {
        listeners.compute(eventType, (k, v) -> {
            if (v == null) {
                v = new CopyOnWriteArrayList<>();
            }
            v.add(eventListener);
            return v;
        });
    }

    @Override
    public void removeEventListener(String eventType, EventListener eventListener) {
        listeners.compute(eventType, (k, v) -> {
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

    @Override
    public void addProcessInstanceResolver(ProcessInstanceResolver<?> processInstanceResolver) {
        this.workflowInstanceResolver.add(processInstanceResolver);
    }

    @Override
    public void removeProcessInstanceResolver(ProcessInstanceResolver<?> processInstanceResolver) {
        this.workflowInstanceResolver.remove(processInstanceResolver);
    }

}
