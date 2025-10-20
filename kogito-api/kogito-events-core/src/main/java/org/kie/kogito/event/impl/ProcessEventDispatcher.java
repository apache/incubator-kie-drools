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
package org.kie.kogito.event.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.kogito.Model;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventDispatcher;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.process.SignalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessEventDispatcher<M extends Model, D> implements EventDispatcher<M, D> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEventDispatcher.class);

    private final Set<String> correlationKeys;

    private final ProcessService processService;
    private final Optional<Function<D, M>> modelConverter;
    private final Process<M> process;
    private final Function<DataEvent<D>, D> dataResolver;

    public ProcessEventDispatcher(Process<M> process, Optional<Function<D, M>> modelConverter, ProcessService processService, Set<String> correlationKeys,
            Function<DataEvent<D>, D> dataResolver) {
        this.process = process;
        this.modelConverter = modelConverter;
        this.processService = processService;
        this.correlationKeys = correlationKeys;
        this.dataResolver = dataResolver;
    }

    @Override
    public ProcessInstance<M> dispatch(String trigger, DataEvent<D> event) {
        if (shouldSkipMessage(trigger, event)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ignoring message for trigger {} in process {}. Skipping consumed message {}", trigger, process.id(), event);
            }
            return null;
        }

        // now see if we have a particular one to check
        Optional<ProcessInstance<M>> processInstance = null;
        // check correlation key
        String processInstanceId = resolveCorrelationId(event).orElse(null);
        processInstance = signalTargetProcessInstance(processInstanceId, trigger, event, this::findById);
        if (processInstance.isPresent()) {
            LOGGER.debug("sending event to process {} with correlation key {} with trigger {} and payload {}", process.id(), processInstanceId, trigger, event);
            return processInstance.get();
        }

        // check processInstanceId
        processInstanceId = event.getKogitoReferenceId();
        processInstance = signalTargetProcessInstance(processInstanceId, trigger, event, this::findById);
        if (processInstance.isPresent()) {
            LOGGER.debug("sending event to process {} with reference key {} with trigger {} and payload {}", process.id(), processInstanceId, trigger, event);
            return processInstance.get();
        }

        // check businessKey
        processInstanceId = event.getKogitoBusinessKey();
        processInstance = signalTargetProcessInstance(processInstanceId, trigger, event, this::findByBusinessKey);
        if (processInstance.isPresent()) {
            LOGGER.debug("sending event to process {} with business key {} with trigger {} and payload {}", process.id(), processInstanceId, trigger, event);
            return processInstance.get();
        }

        // we signal all the processes waiting for trigger (this covers intermediate catch events)
        LOGGER.debug("sending event to process {} with trigger {} and payload {}", process.id(), trigger, event);
        process.send(SignalFactory.of("Message-" + trigger, event));

        // try to start a new instance if possible (this covers start events)
        return startNewInstance(trigger, event);

    }

    private Optional<ProcessInstance<M>> signalTargetProcessInstance(String processInstanceId, String trigger, DataEvent<D> event, Function<String, Optional<ProcessInstance<M>>> findProcessInstance) {
        if (processInstanceId == null) {
            return Optional.empty();
        }

        Optional<ProcessInstance<M>> processInstance = findProcessInstance.apply(processInstanceId);
        if (processInstance.isEmpty()) {
            return Optional.empty();
        }

        signalProcess(processInstance.get(), trigger, event);
        return processInstance;
    }

    private void signalProcess(ProcessInstance<M> pi, String trigger, DataEvent<D> event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending signal {} to process instance id '{}'", trigger, pi.id());
        }
        signalProcessInstance(trigger, pi.id(), event);
    }

    private Optional<ProcessInstance<M>> findById(String id) {
        LOGGER.debug("Received message with process instance id '{}'", id);
        Optional<ProcessInstance<M>> result = process.instances().findById(id);
        if (LOGGER.isDebugEnabled() && result.isEmpty()) {
            LOGGER.debug("No instance found for process instance id '{}'", id);
        }
        return result;

    }

    private Optional<ProcessInstance<M>> findByBusinessKey(String key) {
        LOGGER.debug("Received message with business key '{}'", key);
        Optional<ProcessInstance<M>> result = process.instances().findByBusinessKey(key);
        if (LOGGER.isDebugEnabled() && result.isEmpty()) {
            LOGGER.debug("No instance found for business key '{}'", key);
        }
        return result;
    }

    private Optional<CompositeCorrelation> compositeCorrelation(DataEvent<?> event) {
        return correlationKeys != null && !correlationKeys.isEmpty() ? Optional.of(new CompositeCorrelation(
                correlationKeys.stream().map(k -> new SimpleCorrelation<>(k, resolve(event, k))).collect(Collectors.toSet()))) : Optional.empty();
    }

    private Optional<String> resolveCorrelationId(DataEvent<?> event) {
        return compositeCorrelation(event).flatMap(process.correlations()::find)
                .map(CorrelationInstance::getCorrelatedId);

    }

    private Object resolve(DataEvent<?> event, String key) {
        if (event.getAttributeNames().contains(key)) {
            return event.getAttribute(key);
        }
        if (event.getExtensionNames().contains(key)) {
            return event.getExtension(key);
        } else {
            LOGGER.warn("Correlation key {} not found for event {}", key, event);
            return null;
        }
    }

    private Optional<M> signalProcessInstance(String trigger, String id, DataEvent<D> event) {
        return processService.signalProcessInstance((Process) process, id, dataResolver.apply(event), "Message-" + trigger);
    }

    private ProcessInstance<M> startNewInstance(String trigger, DataEvent<D> event) {
        if (modelConverter.isEmpty()) {
            return null;
        }
        LOGGER.info("Starting new process instance with signal '{}'", trigger);
        return processService.createProcessInstance(
                process,
                event.getKogitoBusinessKey(),
                modelConverter.get().apply(dataResolver.apply(event)),
                headersFromEvent(event),
                event.getKogitoStartFromNode(),
                trigger,
                event.getKogitoProcessInstanceId(),
                compositeCorrelation(event).orElse(null));

    }

    protected Map<String, List<String>> headersFromEvent(DataEvent<D> event) {
        Map<String, List<String>> headers = new HashMap<>();
        for (String name : event.getAttributeNames()) {
            headers.put(name, toList(event.getAttribute(name)));
        }
        for (String name : event.getExtensionNames()) {
            headers.put(name, toList(event.getExtension(name)));
        }
        return headers;
    }

    private List<String> toList(Object object) {
        if (object instanceof Collection) {
            return ((Collection<Object>) object).stream().map(Object::toString).collect(Collectors.toList());
        } else {
            return Arrays.asList(object.toString());
        }
    }

    private boolean isEventTypeNotMatched(String trigger, DataEvent<?> event) {
        final String eventType = event.getType();
        return eventType != null && !Objects.equals(trigger, eventType);
    }

    private boolean isSourceNotMatched(String trigger, DataEvent<?> event) {
        String source = event.getSource() == null ? null : event.getSource().toString();
        return source != null && !Objects.equals(event.getClass().getSimpleName(), source) && !Objects.equals(trigger, source);
    }

    private boolean shouldSkipMessage(String trigger, DataEvent<?> event) {
        return isEventTypeNotMatched(trigger, event) && isSourceNotMatched(trigger, event);
    }
}
