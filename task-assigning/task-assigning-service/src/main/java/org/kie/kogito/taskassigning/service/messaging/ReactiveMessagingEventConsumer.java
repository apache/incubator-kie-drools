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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.taskassigning.service.TaskAssigningException;
import org.kie.kogito.taskassigning.service.TaskAssigningService;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.kie.kogito.taskassigning.service.util.TaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);

    private static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";

    private final TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    private final AtomicBoolean failFast = new AtomicBoolean();

    private final Event<TaskAssigningService.FailFastRequestEvent> failFastRequestEvent;

    @Inject
    public ReactiveMessagingEventConsumer(TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer,
            Event<TaskAssigningService.FailFastRequestEvent> failFastRequestEvent) {
        this.taskAssigningServiceEventConsumer = taskAssigningServiceEventConsumer;
        this.failFastRequestEvent = failFastRequestEvent;
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onUserTaskEvent(Message<UserTaskEvent> message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("UserTaskEvent received: {}", message.getPayload());
        }
        if (failFast.get()) {
            return message.nack(new TaskAssigningException("Task assigning service is in fail fast mode" +
                    " and is not able to accept messages"));
        } else {
            try {
                handleEvent(message.getPayload());
                return message.ack();
            } catch (Exception e) {
                final String msg = String.format("An error was produced during a UserTaskEvent event processing: %s", e.getMessage());
                TaskAssigningException error = new TaskAssigningException(msg, e);
                failFastRequestEvent.fire(new TaskAssigningService.FailFastRequestEvent(error));
                return message.nack(error);
            }
        }
    }

    public void failFast() {
        failFast.set(true);
    }

    private void handleEvent(UserTaskEvent event) {
        taskAssigningServiceEventConsumer.accept(new TaskDataEvent(TaskUtil.fromUserTaskEvent(event)));
    }
}
