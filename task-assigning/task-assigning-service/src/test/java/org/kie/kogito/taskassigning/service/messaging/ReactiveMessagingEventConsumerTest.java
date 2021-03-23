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

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ReactiveMessagingEventConsumerTest {

    @Test
    @Timeout(10)
    void onUserTaskEvent() throws Exception {
        UserTaskEventConsumer userTaskEventConsumer = mock(UserTaskEventConsumer.class);
        ReactiveMessagingEventConsumer consumer = spy(new ReactiveMessagingEventConsumer(userTaskEventConsumer));
        UserTaskEvent event = new UserTaskEvent();
        Message<UserTaskEvent> message = Message.of(event);
        CompletionStage<Void> stage = consumer.onUserTaskEvent(message);
        stage.toCompletableFuture().get();
        verify(userTaskEventConsumer).accept(event);
    }
}
