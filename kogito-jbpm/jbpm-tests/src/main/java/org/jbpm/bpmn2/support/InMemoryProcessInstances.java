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
package org.jbpm.bpmn2.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.kogito.Model;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryProcessInstances.class);

    private final ConcurrentMap<String, byte[]> instances = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<String>> eventTypes = new ConcurrentHashMap<>();

    private ProcessInstanceMarshallerService marshaller;
    private Process<T> process;

    public InMemoryProcessInstances(Process<T> process) {
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners().build();
        this.process = process;
    }

    protected Process<T> getProcess() {
        return process;
    }

    protected ProcessInstanceMarshallerService getMarshaller() {
        return marshaller;
    }

    protected ConcurrentMap<String, byte[]> getInstances() {
        return instances;
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        byte[] data = instances.get(id);
        if (data == null) {
            return Optional.empty();
        }

        if (LOGGER.isInfoEnabled()) {
            // The replace calls are sanitization of the user input. The id has a way to reach here from the user.
            LOGGER.info("find by id {}", id.replace('\n', '_').replace('\r', '_'));
        }
        AbstractProcessInstance<T> pi = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process, mode);
        connectProcessInstance(pi);
        return Optional.of(pi);
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        LOGGER.info("create instance {} for {}", id, process.id());
        byte[] data = marshaller.marshallProcessInstance(instance);
        byte[] oldValue = instances.putIfAbsent(id, data);
        if (oldValue != null) {
            throw new ProcessInstanceDuplicatedException(id);
        }
        connectProcessInstance(instance);
        mergeEventTypes(instance);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance)) {
            LOGGER.info("update instance {} for {}", id, process.id());
            byte[] data = marshaller.marshallProcessInstance(instance);
            instances.put(id, data);
            connectProcessInstance(instance);
            mergeEventTypes(instance);
        }
    }

    @Override
    public void remove(String id) {
        LOGGER.info("remove instance {} for {}", id, process.id());
        instances.remove(id);
        cleanEventTypes(id);
    }

    @Override
    public boolean exists(String id) {
        return instances.containsKey(id);
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        return instances.values().stream()
                .map(data -> {
                    AbstractProcessInstance<T> pi = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process);
                    connectProcessInstance(pi);
                    return (ProcessInstance<T>) pi;
                });
    }

    protected void connectProcessInstance(ProcessInstance<T> instance) {
        Supplier<byte[]> supplier = () -> instances.get(instance.id());
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        List<String> processInstanceIds = eventTypes.getOrDefault(eventType, Collections.emptyList());
        List<ProcessInstance<T>> waitingInstances = new ArrayList<>();

        for (String processInstanceId : processInstanceIds) {
            AbstractProcessInstance<T> pi = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(instances.get(processInstanceId), process, mode);
            connectProcessInstance(pi);
            waitingInstances.add(pi);
        }
        return waitingInstances.stream();
    }

    private void mergeEventTypes(ProcessInstance<T> instance) {
        cleanEventTypes(instance.id());
        WorkflowProcessInstance workflowInstance = ((AbstractProcessInstance<T>) instance).internalGetProcessInstance();
        Set<String> uniqueEventTypes = Stream.of(workflowInstance.getEventTypes()).collect(Collectors.toSet());
        for (String eventType : uniqueEventTypes) {
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
}
