/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudEventConsumer<D, M extends Model, T extends AbstractProcessDataEvent<D>> implements EventConsumer<M> {

    private static final Logger logger = LoggerFactory.getLogger(CloudEventConsumer.class);

    private Optional<Function<D, M>> function;
    private ProcessService processService;
    private ExecutorService executor;

    public CloudEventConsumer(ProcessService processService, ExecutorService executor, Optional<Function<D, M>> function) {
        this.processService = processService;
        this.executor = executor;
        this.function = function;
    }

    @Override
    public CompletionStage<?> consume(Application application, Process<M> process, Object object, String trigger) {
        T cloudEvent = (T) object;
        String simpleName = cloudEvent.getClass().getSimpleName();
        // currently we filter out messages on the receiving end; for strategy see https://issues.redhat.com/browse/KOGITO-3591
        if (ignoredMessageType(cloudEvent, simpleName) && ignoredMessageType(cloudEvent, trigger)) {
            logger.warn("Consumer for CloudEvent type '{}', trigger '{}': ignoring message with type '{}',  source '{}'",
                    simpleName,
                    trigger,
                    cloudEvent.getType(),
                    cloudEvent.getSource());
            return CompletableFuture.completedFuture(null);
        }
        if (cloudEvent.getKogitoReferenceId() != null && !cloudEvent.getKogitoReferenceId().isEmpty()) {
            logger.debug("Received message with reference id '{}' going to use it to send signal '{}'",
                    cloudEvent.getKogitoReferenceId(),
                    trigger);
            Optional<ProcessInstance<M>> instance = process.instances().findById(cloudEvent.getKogitoReferenceId());
            if (instance.isPresent()) {
                return CompletableFuture.completedFuture(processService.signalProcessInstance((Process) process, cloudEvent.getKogitoReferenceId(), cloudEvent.getData(), "Message-" + trigger));
            } else if (function.isPresent()) {
                logger.info("Process instance with id '{}' not found for triggering signal '{}', starting a new one",
                        cloudEvent.getKogitoReferenceId(),
                        trigger);
                return startNewInstance(process, function.get().apply(cloudEvent.getData()), cloudEvent, trigger);
            } else {
                return CompletableFuture.failedStage(new IllegalArgumentException("Process instance with id " + cloudEvent.getKogitoReferenceId() + " not found for triggering signal " + trigger));
            }

        } else if (function.isPresent()) {
            logger.debug("Received message without reference id, starting new process instance with trigger '{}'", trigger);
            return startNewInstance(process, function.get().apply(cloudEvent.getData()), cloudEvent, trigger);
        } else {
            return CompletableFuture.failedStage(new IllegalArgumentException("Received not start event without kogito referecence id for trigger " + trigger));
        }
    }

    private CompletionStage<Void> startNewInstance(Process<M> process, M model, T cloudEvent, String trigger) {
        return CompletableFuture.runAsync(() -> processService.createProcessInstance(process, model, cloudEvent.getKogitoStartFromNode(), trigger, cloudEvent.getKogitoProcessinstanceId()), executor);
    }

    private boolean ignoredMessageType(T cloudEvent, String type) {
        return !type.equals(cloudEvent.getType()) && !type.equals(cloudEvent.getSource());
    }

}
