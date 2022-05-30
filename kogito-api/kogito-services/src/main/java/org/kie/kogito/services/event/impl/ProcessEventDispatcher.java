/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.services.event.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringUtils;
import org.kie.kogito.Model;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.CorrelationResolver;
import org.kie.kogito.event.EventDispatcher;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.correlation.CompositeAttributeCorrelationResolver;
import org.kie.kogito.services.event.correlation.SimpleAttributeCorrelationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.BUSINESS_KEY;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_REFERENCE_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_START_FROM_NODE;

public class ProcessEventDispatcher<M extends Model> implements EventDispatcher<M> {

    private final CorrelationResolver kogitoReferenceCorrelationResolver = new SimpleAttributeCorrelationResolver(PROCESS_REFERENCE_ID);
    private final CorrelationResolver eventTypeResolver = new SimpleAttributeCorrelationResolver("type");
    private final CorrelationResolver eventSourceResolver = new SimpleAttributeCorrelationResolver("source");
    private final CorrelationResolver businessKeyResolver = new SimpleAttributeCorrelationResolver(BUSINESS_KEY);
    private final CorrelationResolver nodeIdResolver = new SimpleAttributeCorrelationResolver(PROCESS_START_FROM_NODE);
    private final CorrelationResolver referenceIdResolver = new SimpleAttributeCorrelationResolver(PROCESS_INSTANCE_ID);
    private UnaryOperator<Object> dataResolver;
    private final Optional<CompositeAttributeCorrelationResolver> instanceCorrelationResolver;

    private ProcessService processService;
    private Function<Object, M> modelConverter;
    private Process<M> process;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEventDispatcher.class);
    private ExecutorService executor;

    public ProcessEventDispatcher(Process<M> process, Function<Object, M> modelConverter, ProcessService processService, ExecutorService executor) {
        this(process, modelConverter, processService, executor, null, null);
    }

    public ProcessEventDispatcher(Process<M> process, Function<Object, M> modelConverter, ProcessService processService, ExecutorService executor, Set<String> correlations,
            UnaryOperator<Object> dataResolver) {
        this.process = process;
        this.modelConverter = modelConverter;
        this.processService = processService;
        this.executor = executor;

        this.dataResolver = dataResolver;

        this.instanceCorrelationResolver = Optional.ofNullable(correlations)
                .filter(c -> !c.isEmpty())
                .map(CompositeAttributeCorrelationResolver::new);
    }

    @Override
    public CompletableFuture<ProcessInstance<M>> dispatch(String trigger, Object event) {
        if (shouldSkipMessage(trigger, event)) {
            LOGGER.info("Ignoring message for trigger {} in process {}. Skipping consumed message {}", trigger, process.id(), event);
            return CompletableFuture.completedFuture(null);
        }

        final String kogitoReferenceId = resolveCorrelationId(event);
        if (StringUtils.isNotEmpty(kogitoReferenceId)) {
            return CompletableFuture.supplyAsync(() -> handleMessageWithReference(trigger, event, kogitoReferenceId), executor);
        }

        //if the trigger is for a start event (model converter is set only for start node)
        if (modelConverter != null) {
            return CompletableFuture.supplyAsync(() -> startNewInstance(trigger, event), executor);
        }

        LOGGER.info("No matches found for trigger {} in process {}. Skipping consumed message {}", trigger, process.id(), event);
        return CompletableFuture.completedFuture(null);
    }

    private String resolveCorrelationId(Object event) {
        //extract if any, instance correlation
        final Optional<CompositeCorrelation> correlation = instanceCorrelationResolver.map(r -> r.resolve(event));

        //if exists call service to find the workflow instance id aka referenceId
        final Optional<CorrelationInstance> correlationInstance = correlation.flatMap(process.correlations()::find);

        //if not found the default is kogitoReferenceId
        return correlationInstance
                .map(CorrelationInstance::getCorrelatedId)
                .orElseGet(() -> kogitoReferenceCorrelationResolver.resolve(event).asString());
    }

    private ProcessInstance<M> handleMessageWithReference(String trigger, Object event, String instanceId) {
        LOGGER.debug("Received message with reference id '{}' going to use it to send signal '{}'",
                instanceId,
                trigger);
        return process.instances()
                .findById(instanceId)
                .map(instance -> {
                    signalProcessInstance(trigger, instance.id(), event);
                    return instance;
                })
                .orElseGet(() -> {
                    LOGGER.info("Process instance with id '{}' not found for triggering signal '{}'", instanceId, trigger);
                    return startNewInstance(trigger, event);
                });
    }

    private Optional<M> signalProcessInstance(String trigger, String id, Object event) {
        return processService.signalProcessInstance((Process) process, id, dataResolver.apply(event), "Message-" + trigger);
    }

    private ProcessInstance<M> startNewInstance(String trigger, Object event) {
        if (modelConverter == null) {
            return null;
        }
        final String businessKey = businessKeyResolver.resolve(event).asString();
        final String fromNode = nodeIdResolver.resolve(event).asString();
        final String referenceId = referenceIdResolver.resolve(event).asString();//keep reference with the caller starting the instance (usually the caller process instance)

        final Object data = dataResolver.apply(event);

        //event correlation, extract if any, the workflow instance correlation
        final CompositeCorrelation correlation = instanceCorrelationResolver.map(r -> r.resolve(event)).orElse(null);
        LOGGER.info("Starting new process instance with signal '{}'", trigger);
        return processService.createProcessInstance(process, businessKey, modelConverter.apply(data), fromNode, trigger, referenceId, correlation);
    }

    private boolean shouldSkipMessage(String trigger, Object event) {
        final String eventType = eventTypeResolver.resolve(event).asString();
        final String source = eventSourceResolver.resolve(event).asString();
        final boolean isEventTypeNotMatched = nonNull(eventType) && !Objects.equals(trigger, eventType);
        final boolean isSourceNotMatched = nonNull(source) && !Objects.equals(event.getClass().getSimpleName(), source) && !Objects.equals(trigger, source);
        return isEventTypeNotMatched && isSourceNotMatched;
    }
}
