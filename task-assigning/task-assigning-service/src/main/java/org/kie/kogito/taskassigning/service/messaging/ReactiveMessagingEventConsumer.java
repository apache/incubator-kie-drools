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

package org.kie.kogito.taskassigning.service.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);

    private static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";

    private UserTaskEventConsumer userTaskEventConsumer;

    @Inject
    public ReactiveMessagingEventConsumer(UserTaskEventConsumer userTaskEventConsumer) {
        this.userTaskEventConsumer = userTaskEventConsumer;
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onUserTaskEvent(Message<UserTaskEvent> message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("UserTaskEvent received: {}", message.getPayload());
        }
        return CompletableFuture.runAsync(() -> handleEvent(message.getPayload()))
                .thenRun(message::ack);
    }

    private void handleEvent(UserTaskEvent event) {
        userTaskEventConsumer.accept(event);
    }
}
