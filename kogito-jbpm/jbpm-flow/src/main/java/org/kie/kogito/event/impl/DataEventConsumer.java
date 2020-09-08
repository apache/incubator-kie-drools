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

import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataEventConsumer<M extends Model, D> implements EventConsumer<M> {

    private static final Logger logger = LoggerFactory.getLogger(DataEventConsumer.class);

    private Function<D, M> function;
    private Class<D> dataEventClass;

    public DataEventConsumer(Function<D, M> function, Class<D> dataEventClass) {
        this.function = function;
        this.dataEventClass = dataEventClass;
    }

    @Override
    public void consume(Application application, Process<M> process, String payload, String trigger) {
        try {
            D eventData = EventUtils.readEvent(payload, dataEventClass);
            M model = function.apply(eventData);
            UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                logger.debug(
                    "Received message without reference id, staring new process instance with trigger '{}'",
                    trigger);
                ProcessInstance<M> pi = process.createInstance(model);
                pi.start(trigger, null);
                return null;
            });
        } catch (JsonProcessingException e) {
            logger.error("Error when consuming message for process {}", process.id(), e);
        }
    }

}
