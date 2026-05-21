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
package org.kie.kogito.index.service;

import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.service.graphql.AbstractWebSocketSubscriptionIT;

import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_JOBS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESSINSTANCES_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESS_DEFINITIONS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_USERTASKINSTANCES_EVENTS;

public abstract class QuarkusAbstractWebSocketIT extends AbstractWebSocketSubscriptionIT {

    @Inject
    @Any
    public InMemoryConnector connector;

    protected void indexProcessCloudEvent(ProcessDefinitionDataEvent event) {
        connector.source(KOGITO_PROCESS_DEFINITIONS_EVENTS).send(event);
    }

    protected void indexProcessCloudEvent(ProcessInstanceDataEvent<?> event) {
        connector.source(KOGITO_PROCESSINSTANCES_EVENTS).send(event);
    }

    protected void indexUserTaskCloudEvent(UserTaskInstanceDataEvent<?> event) {
        connector.source(KOGITO_USERTASKINSTANCES_EVENTS).send(event);
    }

    protected void indexJobCloudEvent(KogitoJobCloudEvent event) {
        connector.source(KOGITO_JOBS_EVENTS).send(event);
    }
}
