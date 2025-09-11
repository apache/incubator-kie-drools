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
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.impl.StringCloudEventUnmarshallerFactory;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;

@QuarkusIntegrationTest
@QuarkusTestResource(SimpleServerServicesMock.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class EventWithHeaderIT {

    private static final String SERVICE_URL = "/event-with-headers";
    private static final String SERVICE_GET_BY_ID_URL = SERVICE_URL + "/{id}";
    private static final String EVENT_TYPE = "lock-event";
    private static final String EVENT_TOPIC = "lock-event";

    private static final String EXPECTED_CALLBACK_EVENT_TYPE = "id-event";

    private static final String DEFAULT_OUT_EVENT_TOPIC = "kogito-sw-out-events";

    public static final String SIMPLE_TOKEN = "TEST_TOKEN";

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;
    ObjectMapper objectMapper;
    KafkaTestClient kafkaClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventWithHeaderIT.class);

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
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
    @SuppressWarnings("squid:S2699")
    void triggerWorkflowWithCloudEvent() throws Exception {

        String lockEvent = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(EventWithHeaderIT.EVENT_TYPE)
                .withTime(OffsetDateTime.now())
                .withExtension("xauthorizationsimple", SIMPLE_TOKEN)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("name", "The Kraken")))
                .build());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Converter<String, CloudEvent> converter = new StringCloudEventUnmarshallerFactory(objectMapper).unmarshaller(Map.class).cloudEvent();
        final AtomicReference<String> idEvent = new AtomicReference<>();
        kafkaClient.consume(DEFAULT_OUT_EVENT_TOPIC, rawIdEventCloudEvent -> {
            idEvent.set(rawIdEventCloudEvent);
            try {
                CloudEvent event = converter.convert(rawIdEventCloudEvent);
                if (EXPECTED_CALLBACK_EVENT_TYPE.equals(event.getType()) &&
                        "event-with-headers".equals(event.getExtension("kogitoprocid"))) {
                    countDownLatch.countDown();
                }
            } catch (IOException e) {
                LOGGER.error("Error while converting raw CloudEvent to CloudEvent map: {}", rawIdEventCloudEvent, e);
            }
        });
        kafkaClient.produce(lockEvent, EventWithHeaderIT.EVENT_TOPIC);
        // give some time to consume the event and produce the output idEvent CloudEvent
        assertThat(countDownLatch.await(15, TimeUnit.SECONDS)).isTrue();
        String processInstanceId = new JsonPath(idEvent.get()).get("data");
        LOGGER.info("processInstanceId: {}", processInstanceId);

        // give some time for the event to be processed and the process to finish.
        assertProcessInstanceHasFinished(EventWithHeaderIT.SERVICE_GET_BY_ID_URL, processInstanceId, 1, 180);
    }

}
