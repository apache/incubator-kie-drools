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
package org.kie.kogito.serverless.workflow.executor;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.junit.jupiter.api.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.EventDefBuilder.eventDef;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.callback;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.event;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class WorkflowEventSubscriberTest {

    @Test
    void testCallbackSubscriber() throws InterruptedException, TimeoutException {
        final String eventType = "testSubscribe";
        final String additionalData = "This has been injected by the event";
        Workflow workflow = workflow("testCallback").start(callback(call(expr("concat", "{slogan:.slogan+\"er Beti\"}")), eventDef(eventType))).end().build();
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            String id = application.execute(workflow, jsonObject().put("slogan", "Viva ")).getId();
            publish(eventType, buildCloudEvent(eventType, id)
                    .withData(JsonCloudEventData.wrap(jsonObject().put("additionalData", additionalData)))
                    .build());
            assertThat(application.waitForFinish(id, Duration.ofSeconds(3)).orElseThrow().getWorkflowdata())
                    .isEqualTo(jsonObject().put("additionalData", additionalData).put("slogan", "Viva er Beti"));
        }
    }

    @Test
    void testEventSubscriber() throws InterruptedException, TimeoutException {
        final String eventType = "testSubscribe";
        Workflow workflow =
                workflow("testEvent").start(operation()).next(event().events().event(eventDef(eventType)).action(call(expr("concat", "{slogan:.slogan+\"er Beti\"}"))).endBranch()).end().build();
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            String id = application.execute(workflow, Collections.emptyMap()).getId();
            publish(eventType, buildCloudEvent(eventType, id)
                    .withData(JsonCloudEventData.wrap(jsonObject().put("slogan", "Viva ")))
                    .build());
            assertThat(application.waitForFinish(id, Duration.ofSeconds(3)).orElseThrow().getWorkflowdata()).isEqualTo(jsonObject().put("slogan", "Viva er Beti"));
        }
    }

    @Test
    void testCallbackSubscriberWithoutEvent() throws InterruptedException {
        final String eventType = "testSubscribeEvent";
        Workflow workflow = workflow("testFailure").start(callback(call(expr("concat", ".slogan+\" er Beti\"")), eventDef(eventType))).end().build();
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            String id = application.execute(workflow, jsonObject().put("slogan", "Viva")).getId();
            assertThatExceptionOfType(TimeoutException.class).isThrownBy(() -> application.waitForFinish(id, Duration.ofSeconds(2)));
        }
    }

    private CloudEventBuilder buildCloudEvent(String eventType, String id) {
        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(eventType)
                .withTime(OffsetDateTime.now())
                .withExtension("kogitoprocrefid", id);
    }

    private void publish(String topic, CloudEvent event) {
        MockConsumer<byte[], CloudEvent> mockConsumer = MockKafkaEventReceiverFactory.consumer;
        Set<String> topics = mockConsumer.subscription();
        assertThat(topics).contains(topic);
        mockConsumer.addRecord(new ConsumerRecord<>(topic, 0, 0, new byte[0], event));
    }
}
