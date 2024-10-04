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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.OnOverflow.Strategy;
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

public abstract class AbstractMessagingEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessagingEventPublisher.class);

    @Inject
    ObjectMapper json;

    @Inject
    @Channel(PROCESS_INSTANCES_TOPIC_NAME)
    @OnOverflow(Strategy.UNBOUNDED_BUFFER)
    MutinyEmitter<String> processInstancesEventsEmitter;
    private AbstractMessageEmitter processInstanceConsumer;

    @Inject
    @Channel(PROCESS_DEFINITIONS_TOPIC_NAME)
    MutinyEmitter<String> processDefinitionEventsEmitter;
    private AbstractMessageEmitter processDefinitionConsumer;

    @Inject
    @Channel(USER_TASK_INSTANCES_TOPIC_NAME)
    MutinyEmitter<String> userTasksEventsEmitter;
    private AbstractMessageEmitter userTaskConsumer;
    @Inject
    EventsRuntimeConfig eventsRuntimeConfig;

    @Inject
    Instance<MessageDecoratorProvider> decoratorProviderInstance;

    private MessageDecoratorProvider decoratorProvider;

    @PostConstruct
    public void init() {
        decoratorProvider = decoratorProviderInstance.isResolvable() ? decoratorProviderInstance.get() : null;
        processDefinitionConsumer = eventsRuntimeConfig.isProcessInstancesPropagateError() ? new BlockingMessageEmitter(processDefinitionEventsEmitter, PROCESS_DEFINITIONS_TOPIC_NAME)
                : new ReactiveMessageEmitter(processDefinitionEventsEmitter, PROCESS_DEFINITIONS_TOPIC_NAME);
        processInstanceConsumer = eventsRuntimeConfig.isProcessDefinitionPropagateError() ? new BlockingMessageEmitter(processInstancesEventsEmitter, PROCESS_INSTANCES_TOPIC_NAME)
                : new ReactiveMessageEmitter(processInstancesEventsEmitter, PROCESS_INSTANCES_TOPIC_NAME);
        userTaskConsumer = eventsRuntimeConfig.isUserTasksPropagateError() ? new BlockingMessageEmitter(userTasksEventsEmitter, USER_TASK_INSTANCES_TOPIC_NAME)
                : new ReactiveMessageEmitter(userTasksEventsEmitter, USER_TASK_INSTANCES_TOPIC_NAME);
    }

    protected Optional<AbstractMessageEmitter> getConsumer(DataEvent<?> event) {
        if (event == null) {
            return Optional.empty();
        }
        switch (event.getType()) {
            case "ProcessDefinitionEvent":
                return eventsRuntimeConfig.isProcessDefinitionEventsEnabled() ? Optional.of(processDefinitionConsumer) : Optional.empty();

            case "ProcessInstanceErrorDataEvent":
            case "ProcessInstanceNodeDataEvent":
            case "ProcessInstanceSLADataEvent":
            case "ProcessInstanceStateDataEvent":
            case "ProcessInstanceVariableDataEvent":
                return eventsRuntimeConfig.isProcessInstancesEventsEnabled() ? Optional.of(processInstanceConsumer) : Optional.empty();

            case "UserTaskInstanceAssignmentDataEvent":
            case "UserTaskInstanceAttachmentDataEvent":
            case "UserTaskInstanceCommentDataEvent":
            case "UserTaskInstanceDeadlineDataEvent":
            case "UserTaskInstanceStateDataEvent":
            case "UserTaskInstanceVariableDataEvent":
                return eventsRuntimeConfig.isUserTasksEventsEnabled() ? Optional.of(userTaskConsumer) : Optional.empty();

            default:
                return Optional.empty();
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }

    protected void publishToTopic(AbstractMessageEmitter emitter, Object event) {
        logger.debug("About to publish event {} to topic {}", event, emitter.topic);
        Message<String> message = null;
        try {
            String eventString = json.writeValueAsString(event);
            logger.debug("Event payload '{}'", eventString);
            message = decorateMessage(ContextAwareMessage.of(eventString));
        } catch (Exception e) {
            logger.error("Error while creating event to topic {} for event {}", emitter.topic, event);
        }
        if (message != null) {
            emitter.accept(message);
        }
    }

    protected Message<String> decorateMessage(Message<String> message) {
        return decoratorProvider != null ? decoratorProvider.decorate(message) : message;
    }

    protected static abstract class AbstractMessageEmitter implements Consumer<Message<String>> {

        protected final String topic;
        protected final MutinyEmitter<String> emitter;

        protected AbstractMessageEmitter(MutinyEmitter<String> emitter, String topic) {
            this.emitter = emitter;
            this.topic = topic;
        }
    }

    private static class BlockingMessageEmitter extends AbstractMessageEmitter {
        protected BlockingMessageEmitter(MutinyEmitter<String> emitter, String topic) {
            super(emitter, topic);
        }

        @Override
        public void accept(Message<String> message) {
            emitter.sendMessageAndAwait(message);
            logger.debug("Successfully published message {}", message.getPayload());
        }
    }

    private static class ReactiveMessageEmitter extends AbstractMessageEmitter {
        protected ReactiveMessageEmitter(MutinyEmitter<String> emitter, String topic) {
            super(emitter, topic);
        }

        @Override
        public void accept(Message<String> message) {
            emitter.sendMessageAndForget(message
                    .withAck(() -> onAck(message))
                    .withNack(reason -> onNack(reason, message)));
        }

        private CompletionStage<Void> onAck(Message<String> message) {
            logger.debug("Successfully published message {}", message.getPayload());
            return CompletableFuture.completedFuture(null);
        }

        private CompletionStage<Void> onNack(Throwable reason, Message<String> message) {
            logger.error("Error while publishing message {}", message, reason);
            return CompletableFuture.completedFuture(null);
        }

    }
}
