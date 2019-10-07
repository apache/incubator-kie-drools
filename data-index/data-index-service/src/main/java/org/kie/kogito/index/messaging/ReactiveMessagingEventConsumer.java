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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.index.event.DomainModelRegisteredEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.json.ProcessInstanceMetaMapper;
import org.kie.kogito.index.json.UserTaskInstanceMetaMapper;
import org.kie.kogito.index.service.IndexingService;
import org.kie.kogito.index.vertx.ObjectNodeMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

@ApplicationScoped
public class ReactiveMessagingEventConsumer {

    protected static final String KOGITO_DOMAIN_EVENTS = "kogito-domain-events-%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);
    private static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";
    private static final String KOGITO_PROCESSDOMAIN_EVENTS = "kogito-processdomain-events";
    private static final String KOGITO_USERTASKDOMAIN_EVENTS = "kogito-usertaskdomain-events";
    private static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";

    @Inject
    IndexingService indexingService;

    @Inject
    EventBus eventBus;
    
    @Inject
    ObjectNodeMessageCodec codec;
    
    @PostConstruct
    public void setup(){
        eventBus.registerDefaultCodec(ObjectNode.class, codec);
    }

    private Map<String, MessageConsumer<ObjectNode>> consumers = new HashMap<>();

    public void onDomainModelRegisteredEvent(@Observes DomainModelRegisteredEvent event) {
        LOGGER.info("New domain model registered for Process Id: {}", event.getProcessId());
        consumers.computeIfAbsent(event.getProcessId(), f -> {
            MessageConsumer<ObjectNode> consumer = eventBus.consumer(format(KOGITO_DOMAIN_EVENTS, event.getProcessId()), e -> onDomainEvent(e));
            LOGGER.info("Consumer registered for address: {}", consumer.address());
            return consumer;
        });
    }

    @Incoming(KOGITO_PROCESSINSTANCES_EVENTS)
    public CompletionStage<Void> onProcessInstanceEvent(KogitoProcessCloudEvent event) {
        LOGGER.debug("Process instance consumer received KogitoCloudEvent: \n{}", event);
        return CompletableFuture.runAsync(() -> indexingService.indexProcessInstance(event.getData()));
    }

    @Incoming(KOGITO_PROCESSDOMAIN_EVENTS)
    public CompletionStage<Void> onProcessInstanceDomainEvent(KogitoProcessCloudEvent event) {
        LOGGER.debug("Process domain consumer received KogitoCloudEvent: \n{}", event);
        ObjectNode json = new ProcessInstanceMetaMapper().apply(event);
        return sendMessage(json);
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    public CompletionStage<Void> onUserTaskInstanceEvent(KogitoUserTaskCloudEvent event) {
        LOGGER.debug("Task instance received KogitoUserTaskCloudEvent \n{}", event);
        return CompletableFuture.runAsync(() -> indexingService.indexUserTaskInstance(event.getData()));
    }

    @Incoming(KOGITO_USERTASKDOMAIN_EVENTS)
    public CompletionStage<Void> onUserTaskInstanceDomainEvent(KogitoUserTaskCloudEvent event) {
        LOGGER.debug("Task domain received KogitoUserTaskCloudEvent \n{}", event);
        ObjectNode json = new UserTaskInstanceMetaMapper().apply(event);
        return sendMessage(json);
    }

    private CompletableFuture<Void> sendMessage(ObjectNode json) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        String processId = json.get("processId").asText();
        eventBus.request(format(KOGITO_DOMAIN_EVENTS, processId), json, async -> cf.complete(null));
        return cf;
    }

    private void onDomainEvent(Message<ObjectNode> message) {
        try {
            LOGGER.debug("Processing domain message: {}", message);
            indexingService.indexModel(message.body());
            message.reply(null);
        } catch (Exception ex) {
            LOGGER.error("Error processing domain event: {}", ex.getMessage(), ex);
            message.fail(0, ex.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        consumers.values().forEach(c -> c.unregister());
        consumers.clear();
    }
}
