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
package org.kie.kogito.index.service.messaging;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_JOBS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESSINSTANCES_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESS_DEFINITIONS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_USERTASKINSTANCES_EVENTS;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getUserTaskCloudEvent;
import static org.kie.kogito.index.test.TestUtils.readFileContent;

public abstract class AbstractMessagingKafkaConsumerIT extends AbstractMessagingConsumerIT {

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY, defaultValue = "localhost:9092")
    public String kafkaBootstrapServers;

    KafkaTestClient kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        super.setup();
    }

    @AfterEach
    void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
        super.close();
    }

    protected void sendUserTaskInstanceEvent() throws Exception {
        send("user_task_instance_event.json", KOGITO_USERTASKINSTANCES_EVENTS);
    }

    protected void sendProcessInstanceEvent() throws Exception {
        send("process_instance_event.json", KOGITO_PROCESSINSTANCES_EVENTS);
    }

    protected void sendProcessDefinitionEvent() throws Exception {
        send("process_definition_event.json", KOGITO_PROCESS_DEFINITIONS_EVENTS);
    }

    protected void sendJobEvent() throws Exception {
        send("job_event.json", KOGITO_JOBS_EVENTS);
    }

    @Override
    protected void sendProcessInstanceEventCollection() throws Exception {
        Collection<ProcessInstanceDataEvent<?>> events = List.of(
                getProcessCloudEvent("travels", "processId-UUID1", ProcessInstanceState.ACTIVE, null, null, null, "user1"),
                getProcessCloudEvent("travels", "processId-UUID2", ProcessInstanceState.ACTIVE, null, null, null, "user2"));
        kafkaClient.produce(ObjectMapperFactory.get().writeValueAsString(new MultipleProcessInstanceDataEvent(URI.create("test"), events)), KOGITO_PROCESSINSTANCES_EVENTS);
    }

    @Override
    protected void sendUserTaskInstanceEventCollection() throws Exception {
        Collection<UserTaskInstanceDataEvent<?>> events = List.of(
                getUserTaskCloudEvent("taskId-UUID1", "travels", "processId-UUID1", null, null, "IN_PROGRESS"),
                getUserTaskCloudEvent("taskId-UUID2", "travels", "processId-UUID1", null, null, "COMPLETED"));
        kafkaClient.produce(ObjectMapperFactory.get().writeValueAsString(new MultipleUserTaskInstanceDataEvent(URI.create("test"), events)), KOGITO_USERTASKINSTANCES_EVENTS);
    }

    @Override
    protected void sendProcessDefinitionEventCollection() throws Exception {
        kafkaClient.produce(readFileContent("process_definition_event.json"), KOGITO_PROCESS_DEFINITIONS_EVENTS);
        kafkaClient.produce(readFileContent("process_definition_11_event.json"), KOGITO_PROCESS_DEFINITIONS_EVENTS);
    }

    private void send(String file, String topic) throws Exception {
        String json = readFileContent(file);
        kafkaClient.produce(json, topic);
    }

}
