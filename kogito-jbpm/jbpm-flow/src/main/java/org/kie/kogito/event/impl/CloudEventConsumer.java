/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event.impl;

import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudEventConsumer<D, M extends Model, T extends AbstractProcessDataEvent<D>> implements EventConsumer<M> {

    private static final Logger logger = LoggerFactory.getLogger(CloudEventConsumer.class);

    private Function<D, M> function;
    private Class<T> cloudEventClass;

    public CloudEventConsumer(Function<D, M> function, Class<T> cloudEventClass) {
        this.function = function;
        this.cloudEventClass = cloudEventClass;
    }

    @Override
    public void consume(Application application, Process<M> process, String payload, String trigger) {
        try {
            T cloudEvent = EventUtils.readEvent(payload, cloudEventClass);
            M model = function.apply(cloudEvent.getData());
            UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                if (cloudEvent.getKogitoReferenceId() != null) {
                    logger.debug("Received message with reference id '{}' going to use it to send signal '{}'",
                                 cloudEvent.getKogitoReferenceId(),
                                 trigger);
                    Optional<ProcessInstance<M>> instance = process.instances().findById(cloudEvent.getKogitoReferenceId());
                    if(instance.isPresent()){
                        instance.get().send(Sig.of("Message-" + trigger,
                                                   cloudEvent.getData(),
                                                   cloudEvent.getKogitoProcessinstanceId()));
                    } else {
                        logger.warn("Process instance with id '{}' not found for triggering signal '{}'",
                                    cloudEvent.getKogitoReferenceId(),
                                    trigger);
                    }
                } else {
                    logger.debug("Received message without reference id, staring new process instance with trigger '{}'",
                                 trigger);
                    ProcessInstance<M> pi = process.createInstance(model);
                    if (cloudEvent.getKogitoStartFromNode() != null) {
                        pi.startFrom(cloudEvent.getKogitoStartFromNode(), cloudEvent.getKogitoProcessinstanceId());
                    } else {
                        pi.start(trigger, cloudEvent.getKogitoProcessinstanceId());
                    }
                }
                return null;
            });
        } catch (JsonProcessingException e) {
            logger.error("Error when consuming message for process {}", process.id(), e);
        }
    }

}
