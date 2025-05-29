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
package org.kie.kogito.process.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.kogito.Model;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.ProcessInstanceReadMode;

class MapProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private final ConcurrentHashMap<String, WorkflowProcessInstance> instances = new ConcurrentHashMap<>();
    private AbstractProcess<T> process;

    private final ConcurrentMap<String, List<String>> eventTypes = new ConcurrentHashMap<>();

    public MapProcessInstances(AbstractProcess<T> process) {
        this.process = process;
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        WorkflowProcessInstance instance = instances.get(id);
        if (instance == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(toProcessInstance(instance, mode));
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        WorkflowProcessInstance existing = instances.putIfAbsent(id, ((AbstractProcessInstance<T>) instance).internalGetProcessInstance());
        if (existing != null) {
            throw new ProcessInstanceDuplicatedException(id);
        }
        connectProcessInstance(instance);
        mergeEventTypes(instance);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance)) {
            instances.put(id, ((AbstractProcessInstance<T>) instance).internalGetProcessInstance());
            connectProcessInstance(instance);
            mergeEventTypes(instance);
        }
    }

    @Override
    public void remove(String id) {
        instances.remove(id);
        cleanEventTypes(id);
    }

    @Override
    public boolean exists(String id) {
        return instances.containsKey(id);
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        return instances.values().stream().map(e -> toProcessInstance(e, mode));
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        List<String> processInstanceIds = eventTypes.getOrDefault(eventType, Collections.emptyList());
        List<ProcessInstance<T>> waitingInstances = new ArrayList<>();

        for (String processInstanceId : processInstanceIds) {
            AbstractProcessInstance pi = (AbstractProcessInstance) toProcessInstance(instances.get(processInstanceId), mode);
            connectProcessInstance(pi);
            waitingInstances.add(pi);
        }
        return waitingInstances.stream();
    }

    private ProcessInstance<T> toProcessInstance(WorkflowProcessInstance instance, ProcessInstanceReadMode mode) {
        ProcessInstance<T> processInstance = null;
        if (mode.equals(ProcessInstanceReadMode.READ_ONLY)) {
            processInstance = process.createReadOnlyInstance(instance);
        } else {
            processInstance = process.createInstance(instance);
        }

        connectProcessInstance(processInstance);
        return processInstance;
    }

    protected void connectProcessInstance(ProcessInstance<T> instance) {
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(pi -> {
            WorkflowProcessInstance workflowProcessInstance = instances.get(instance.id());
            if (workflowProcessInstance == null) {
                throw new ProcessInstanceNotFoundException(instance.id());
            }
            ((AbstractProcessInstance<?>) instance).internalSetProcessInstance(workflowProcessInstance);
        });
    }

    private void mergeEventTypes(ProcessInstance<T> instance) {
        cleanEventTypes(instance.id());
        String[] events = getUniqueEvents(instance);
        for (String eventType : events) {
            eventTypes.compute(eventType, (k, v) -> {
                List<String> instancesId = v;
                if (instancesId == null) {
                    instancesId = new CopyOnWriteArrayList<>();
                }
                instancesId.add(instance.id());
                return !instancesId.isEmpty() ? instancesId : null;
            });
        }
    }

    private void cleanEventTypes(String processInstanceId) {
        for (String eventType : eventTypes.keySet()) {
            eventTypes.compute(eventType, (k, v) -> {
                List<String> instancesId = v;
                if (instancesId == null) {
                    instancesId = new CopyOnWriteArrayList<>();
                }
                instancesId.remove(processInstanceId);
                return !instancesId.isEmpty() ? instancesId : null;
            });
        }
    }

    private String[] getUniqueEvents(ProcessInstance<T> instance) {
        return Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new))
                .toArray(String[]::new);
    }

}
