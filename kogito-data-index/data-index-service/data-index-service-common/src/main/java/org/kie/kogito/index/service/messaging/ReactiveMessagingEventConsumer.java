/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.service.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.ProcessInstanceEventMapper;
import org.kie.kogito.index.event.UserTaskInstanceEventMapper;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.UnlessBuildProperty;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@UnlessBuildProperty(name = "kogito.data-index.blocking", stringValue = "true", enableIfMissing = true)
public class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);

    public static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";
    public static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";
    public static final String KOGITO_JOBS_EVENTS = "kogito-jobs-events";

    @Inject
    IndexingService indexingService;

    @Inject
    Event<DataEvent> eventPublisher;

    @Incoming(KOGITO_PROCESSINSTANCES_EVENTS)
    public Uni<Void> onProcessInstanceEvent(ProcessInstanceDataEvent event) {
        LOGGER.debug("Process instance consumer received ProcessInstanceDataEvent: \n{}", event);
        return Uni.createFrom().item(event)
                .invoke(e -> indexingService.indexProcessInstance(new ProcessInstanceEventMapper().apply(e)))
                .invoke(e -> eventPublisher.fire(e))
                .onFailure()
                .invoke(t -> LOGGER.error("Error processing process instance ProcessInstanceDataEvent: {}", t.getMessage(), t))
                .onItem().ignore().andContinueWithNull();
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    public Uni<Void> onUserTaskInstanceEvent(UserTaskInstanceDataEvent event) {
        LOGGER.debug("Task instance received UserTaskInstanceDataEvent \n{}", event);
        return Uni.createFrom().item(event)
                .invoke(e -> indexingService.indexUserTaskInstance(new UserTaskInstanceEventMapper().apply(e)))
                .invoke(e -> eventPublisher.fire(e))
                .onFailure()
                .invoke(t -> LOGGER.error("Error processing task instance UserTaskInstanceDataEvent: {}", t.getMessage(), t))
                .onItem().ignore().andContinueWithNull();
    }

    @Incoming(KOGITO_JOBS_EVENTS)
    public Uni<Void> onJobEvent(KogitoJobCloudEvent event) {
        LOGGER.debug("Job received KogitoJobCloudEvent \n{}", event);
        return Uni.createFrom().item(event)
                .onItem().invoke(e -> indexingService.indexJob(e.getData()))
                .onFailure().invoke(t -> LOGGER.error("Error processing job KogitoJobCloudEvent: {}", t.getMessage(), t))
                .onItem().ignore().andContinueWithNull();
    }

}
