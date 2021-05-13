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
import java.util.function.Function;

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

    public CloudEventConsumer(Function<D, M> function) {
        this.function = function;
    }

    @Override
    public void consume(Application application, Process<M> process, Object object, String trigger) {
        T cloudEvent = (T) object;
        M model = function.apply(cloudEvent.getData());
        String simpleName = cloudEvent.getClass().getSimpleName();
        // currently we filter out messages on the receiving end; for strategy see https://issues.redhat.com/browse/KOGITO-3591
        if (ignoredMessageType(cloudEvent, simpleName) && ignoredMessageType(cloudEvent, trigger)) {
            logger.warn("Consumer for CloudEvent type '{}', trigger '{}': ignoring message with type '{}',  source '{}'",
                    simpleName,
                    trigger,
                    cloudEvent.getType(),
                    cloudEvent.getSource());
            return;
        }
        UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            if (cloudEvent.getKogitoReferenceId() != null && !cloudEvent.getKogitoReferenceId().isEmpty()) {
                logger.debug("Received message with reference id '{}' going to use it to send signal '{}'",
                        cloudEvent.getKogitoReferenceId(),
                        trigger);
                Optional<ProcessInstance<M>> instance = process.instances().findById(cloudEvent.getKogitoReferenceId());
                if (instance.isPresent()) {
                    instance.get().send(Sig.of("Message-" + trigger,
                            cloudEvent.getData(),
                            cloudEvent.getKogitoProcessinstanceId()));
                } else {
                    logger.warn("Process instance with id '{}' not found for triggering signal '{}'",
                            cloudEvent.getKogitoReferenceId(),
                            trigger);
                }
            } else {
                logger.debug("Received message without reference id, starting new process instance with trigger '{}'",
                        trigger);
                ProcessInstance<M> pi = process.createInstance(model);
                if (cloudEvent.getKogitoStartFromNode() != null && !cloudEvent.getKogitoStartFromNode().isEmpty()) {
                    pi.startFrom(cloudEvent.getKogitoStartFromNode(), cloudEvent.getKogitoProcessinstanceId());
                } else {
                    pi.start(trigger, cloudEvent.getKogitoProcessinstanceId());
                }
            }
            return null;
        });
    }

    private boolean ignoredMessageType(T cloudEvent, String type) {
        return !type.equals(cloudEvent.getType()) && !type.equals(cloudEvent.getSource());
    }

}
