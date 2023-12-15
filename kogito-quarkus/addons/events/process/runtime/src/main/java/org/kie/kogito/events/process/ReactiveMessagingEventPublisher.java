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
package org.kie.kogito.events.process;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.quarkus.common.reactive.messaging.MessageDecoratorProvider;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.events.config.EventsRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.providers.locals.ContextAwareMessage;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ReactiveMessagingEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveMessagingEventPublisher.class);

    @Inject
    ObjectMapper json;

    @Inject
    @Channel(PROCESS_INSTANCES_TOPIC_NAME)
    MutinyEmitter<String> processInstancesEventsEmitter;

    @Inject
    @Channel(PROCESS_DEFINITIONS_TOPIC_NAME)
    MutinyEmitter<String> processDefinitionEventsEmitter;

    @Inject
    @Channel(USER_TASK_INSTANCES_TOPIC_NAME)
    MutinyEmitter<String> userTasksEventsEmitter;
    @Inject
    EventsRuntimeConfig eventsRuntimeConfig;

    @Inject
    Instance<MessageDecoratorProvider> decoratorProviderInstance;

    private MessageDecoratorProvider decoratorProvider;

    @PostConstruct
    public void init() {
        decoratorProvider = decoratorProviderInstance.isResolvable() ? decoratorProviderInstance.get() : null;
    }

    @Override
    public void publish(DataEvent<?> event) {

        switch (event.getType()) {
            case "ProcessDefinitionEvent":
                if (eventsRuntimeConfig.isProcessDefinitionEventsEnabled()) {
                    publishToTopic(event, processDefinitionEventsEmitter, PROCESS_DEFINITIONS_TOPIC_NAME);
                }
                break;
            case "ProcessInstanceErrorDataEvent":
            case "ProcessInstanceNodeDataEvent":
            case "ProcessInstanceSLADataEvent":
            case "ProcessInstanceStateDataEvent":
            case "ProcessInstanceVariableDataEvent":
                if (eventsRuntimeConfig.isProcessInstancesEventsEnabled()) {
                    publishToTopic(event, processInstancesEventsEmitter, PROCESS_INSTANCES_TOPIC_NAME);
                }
                break;

            case "UserTaskInstanceAssignmentDataEvent":
            case "UserTaskInstanceAttachmentDataEvent":
            case "UserTaskInstanceCommentDataEvent":
            case "UserTaskInstanceDeadlineDataEvent":
            case "UserTaskInstanceStateDataEvent":
            case "UserTaskInstanceVariableDataEvent":
                if (eventsRuntimeConfig.isUserTasksEventsEnabled()) {
                    publishToTopic(event, userTasksEventsEmitter, USER_TASK_INSTANCES_TOPIC_NAME);
                }
                break;
            default:
                logger.debug("Unknown type of event '{}', ignoring for this publisher", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }

    protected void publishToTopic(DataEvent<?> event, MutinyEmitter<String> emitter, String topic) {
        logger.debug("About to publish event {} to topic {}", event, topic);
        try {
            String eventString = json.writeValueAsString(event);
            Message<String> message = decorateMessage(ContextAwareMessage.of(eventString));

            logger.debug("Event payload '{}'", eventString);
            emitter.sendMessageAndAwait(message);

        } catch (Exception e) {
            logger.error("Error while creating event to topic {} for event {}", topic, event, e);
        }
    }

    protected CompletionStage<Void> onAck(DataEvent<?> event, String topic) {
        logger.debug("Successfully published event {} to topic {}", event, topic);
        return CompletableFuture.completedFuture(null);
    }

    protected CompletionStage<Void> onNack(Throwable reason, DataEvent<?> event, String topic) {
        logger.error("Error while publishing event to topic {} for event {}", topic, event, reason);
        return CompletableFuture.completedFuture(null);
    }

    protected Message<String> decorateMessage(Message<String> message) {
        return decoratorProvider != null ? decoratorProvider.decorate(message) : message;
    }
}
