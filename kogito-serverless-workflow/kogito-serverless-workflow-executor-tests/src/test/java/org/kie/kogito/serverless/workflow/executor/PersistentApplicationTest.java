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
package org.kie.kogito.serverless.workflow.executor;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.kogito.persistence.rocksdb.RocksDBProcessInstancesFactory;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;

import com.fasterxml.jackson.databind.node.TextNode;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.EventDefBuilder.eventDef;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.callback;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class PersistentApplicationTest {
    @Test
    void testCallbackSubscriberWithPersistence(@TempDir Path tempDir) throws InterruptedException, TimeoutException, RocksDBException {
        final String eventType = "testSubscribe";
        final String additionalData = "This has been injected by the event";
        Workflow workflow = workflow("testCallback").start(callback(call(expr("concat", "{slogan:.slogan+\"er Beti\"}")), eventDef(eventType))).end().build();
        try (StaticWorkflowApplication application =
                StaticWorkflowApplication.create().processInstancesFactory(new RocksDBProcessInstancesFactory(new Options().setCreateIfMissing(true), tempDir.toString()))) {
            String id = application.execute(workflow, jsonObject().put("slogan", "Viva ")).getId();
            assertThat(application.variables(id).orElseThrow().getWorkflowdata()).doesNotContain(new TextNode(additionalData));
            publish(eventType, buildCloudEvent(eventType, id)
                    .withData(JsonCloudEventData.wrap(jsonObject().put("additionalData", additionalData)))
                    .build());
            assertThat(application.waitForFinish(id, Duration.ofSeconds(2000)).orElseThrow().getWorkflowdata())
                    .isEqualTo(jsonObject().put("additionalData", additionalData).put("slogan", "Viva er Beti"));
            await().atMost(Duration.ofSeconds(1)).pollInterval(Duration.ofMillis(50)).until(() -> application.variables(id).isEmpty());
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
