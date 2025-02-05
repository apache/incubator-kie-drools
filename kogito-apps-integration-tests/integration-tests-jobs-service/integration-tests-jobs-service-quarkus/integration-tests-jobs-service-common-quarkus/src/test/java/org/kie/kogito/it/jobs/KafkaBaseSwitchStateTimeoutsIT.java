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
package org.kie.kogito.it.jobs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaBaseSwitchStateTimeoutsIT extends BaseSwitchStateTimeoutsIT {

    private KafkaTestClient kafkaClient;
    private static final String KOGITO_OUTGOING_STREAM_TOPIC = "kogito-sw-out-events";

    @BeforeEach
    void setup() {
        String kafkaBootstrapServers = ConfigProvider.getConfig().getValue(KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY, String.class);
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    void cleanUp() {
        kafkaClient.shutdown();
    }

    private static JsonPath waitForEvent(KafkaTestClient kafkaClient, String topic, long seconds) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> cloudEvent = new AtomicReference<>();
        kafkaClient.consume(topic, rawCloudEvent -> {
            cloudEvent.set(rawCloudEvent);
            countDownLatch.countDown();
        });
        // give some time to consume the event.
        assertThat(countDownLatch.await(seconds, TimeUnit.SECONDS)).isTrue();
        return new JsonPath(cloudEvent.get());
    }

    @Override
    protected void verifyNoDecisionEventWasProduced(String processInstanceId) throws Exception {
        JsonPath result = waitForEvent(kafkaClient, KOGITO_OUTGOING_STREAM_TOPIC, 50);
        assertDecisionEvent(result, processInstanceId, PROCESS_RESULT_EVENT_TYPE, DECISION_NO_DECISION);
    }
}
