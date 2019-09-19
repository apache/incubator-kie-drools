/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);
    private static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";
    private static final String KOGITO_PROCESSDOMAIN_EVENTS = "kogito-processdomain-events";
    private static final String KOGITO_USERTASKDOMAIN_EVENTS = "kogito-usertaskdomain-events";
    private static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";

    @Inject
    IndexingService indexingService;

    @Incoming(KOGITO_PROCESSINSTANCES_EVENTS)
    public void onProcessInstanceEvent(KogitoProcessCloudEvent event) {
        try {
            LOGGER.debug("Process instance consumer received KogitoCloudEvent: \n{}", event);
            indexingService.indexProcessInstance(event);
        } catch (Exception ex) {
            LOGGER.error("Error processing KogitoCloudEvent: {}", ex.getMessage(), ex);
        }
    }

    @Incoming(KOGITO_PROCESSDOMAIN_EVENTS)
    public void onProcessInstanceDomainEvent(KogitoProcessCloudEvent event) {
        try {
            LOGGER.debug("Process domain consumer received KogitoCloudEvent: \n{}", event);
            indexingService.indexProcessInstanceModel(event);
        } catch (Exception ex) {
            LOGGER.error("Error processing KogitoCloudEvent: {}", ex.getMessage(), ex);
        }
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    public void onUserTaskInstanceEvent(KogitoUserTaskCloudEvent event) {
        try {
            LOGGER.debug("Task instance received KogitoUserTaskCloudEvent \n{}", event);
            indexingService.indexUserTaskInstance(event);
        } catch (Exception ex) {
            LOGGER.error("Error processing KogitoUserTaskCloudEvent: {}", ex.getMessage(), ex);
        }
    }

    @Incoming(KOGITO_USERTASKDOMAIN_EVENTS)
    public void onUserTaskInstanceDomainEvent(KogitoUserTaskCloudEvent event) {
        try {
            LOGGER.debug("Task domain received KogitoUserTaskCloudEvent \n{}", event);
            indexingService.indexUserTaskInstanceDomain(event);
        } catch (Exception ex) {
            LOGGER.error("Error processing KogitoUserTaskCloudEvent: {}", ex.getMessage(), ex);
        }
    }
}
