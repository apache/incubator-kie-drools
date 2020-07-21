/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyInfinispanServerTestResource;
import org.kie.kogito.trusty.service.TrustyKafkaTestResource;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CLOUDEVENT_WITH_ERRORS_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectDecision;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithErrors;

@QuarkusTest
@QuarkusTestResource(TrustyInfinispanServerTestResource.class)
@QuarkusTestResource(TrustyKafkaTestResource.class)
class TraceEventConsumerIT {

    private static final Logger LOG = LoggerFactory.getLogger(TraceEventConsumerIT.class);

    @Inject
    TraceEventConsumer traceEventConsumer;

    @Inject
    TrustyService trustyService;

    @Inject
    TrustyStorageService trustyStorageService;

    KafkaProducer<String, String> producer;

    @BeforeEach
    public void setup() {
        trustyStorageService.getDecisionsStorage().clear();

        System.err.println("kafka.bootstrap.servers: " + System.getProperty(TrustyKafkaTestResource.KAFKA_BOOTSTRAP_SERVERS, "localhost:9092"));

        producer = KafkaProducer.create(Vertx.vertx(), Map.of(
                "bootstrap.servers", System.getProperty(TrustyKafkaTestResource.KAFKA_BOOTSTRAP_SERVERS, "localhost:9092"),
                "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                "value.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                "acks", "all"
        ));
    }

    @Test
    void testCorrectCloudEvent() throws Exception {
        sendToKafkaAndWaitForCompletion(buildCloudEventJsonString(buildCorrectTraceEvent()));
        Decision storedDecision = trustyService.getDecisionById(CORRECT_CLOUDEVENT_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildCorrectDecision(), storedDecision);
    }

    @Test
    void testCloudEventWithErrors() throws Exception {
        sendToKafkaAndWaitForCompletion(buildCloudEventJsonString(buildTraceEventWithErrors()));
        Decision storedDecision = trustyService.getDecisionById(CLOUDEVENT_WITH_ERRORS_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildDecisionWithErrors(), storedDecision);
    }

    private CompletableFuture<Void> sendToKafka(String payload) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        producer.write(KafkaProducerRecord.create("trusty-service-test", payload), event -> {
            if (event.succeeded()) {
                future.complete(null);
            } else {
                future.completeExceptionally(event.cause());
            }
        });
        return future;
    }

    private void sendToKafkaAndWaitForCompletion(String payload) throws Exception {
        sendToKafka(payload)
                .thenRunAsync(() -> LOG.info("Sent payload to Kafka (length: {})", payload.length()), CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS))
                .get(15L, TimeUnit.SECONDS);
    }

}
