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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_JOBS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESSINSTANCES_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_PROCESS_DEFINITIONS_EVENTS;
import static org.kie.kogito.index.service.messaging.ReactiveMessagingEventConsumer.KOGITO_USERTASKINSTANCES_EVENTS;
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

    private void send(String file, String topic) throws Exception {
        String json = readFileContent(file);
        kafkaClient.produce(json, topic);
    }

}
