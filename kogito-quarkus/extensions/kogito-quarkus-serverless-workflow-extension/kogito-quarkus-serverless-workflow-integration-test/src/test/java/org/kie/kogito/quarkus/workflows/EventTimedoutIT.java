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
package org.kie.kogito.quarkus.workflows;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.impl.ByteArrayCloudEventUnmarshallerFactory;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTypedTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.workflows.AssuredTestUtils.startProcess;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class EventTimedoutIT {

    private final static Logger logger = LoggerFactory.getLogger(EventTimedoutIT.class);

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;
    private ObjectMapper objectMapper;
    private KafkaTypedTestClient<byte[], ByteArraySerializer, ByteArrayDeserializer> kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTypedTestClient<>(kafkaBootstrapServers, ByteArraySerializer.class, ByteArrayDeserializer.class);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void cleanUp() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    void testTimedout() throws InterruptedException {
        String id = startProcess("eventTimedout");
        Converter<byte[], CloudEvent> converter = new ByteArrayCloudEventUnmarshallerFactory(objectMapper).unmarshaller(Map.class).cloudEvent();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        kafkaClient.consume("timeoutError", v -> {
            try {
                CloudEvent event = converter.convert(v);
                if (id.equals(event.getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_ID))) {
                    countDownLatch.countDown();
                }
            } catch (IOException e) {
                logger.info("Unmarshall exception", e);
            }
        });
        countDownLatch.await(10, TimeUnit.SECONDS);
        assertThat(countDownLatch.getCount()).isZero();
    }
}
