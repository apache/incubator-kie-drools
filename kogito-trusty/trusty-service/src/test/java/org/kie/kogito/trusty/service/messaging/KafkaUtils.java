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

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.kie.kogito.trusty.service.TrustyKafkaTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtils {

    public static final String KOGITO_TRACING_TOPIC = "kogito-tracing-test";

    public static final String KOGITO_TRACING_MODEL_TOPIC = "kogito-tracing-model-test";

    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtils.class);

    public static CompletableFuture<Void> sendToKafka(String topic, String payload, KafkaProducer<String, String> producer) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        producer.write(KafkaProducerRecord.create(topic, payload), event -> {
            if (event.succeeded()) {
                future.complete(null);
            } else {
                future.completeExceptionally(event.cause());
            }
        });
        return future;
    }

    public static void sendToKafkaAndWaitForCompletion(String topic, String payload, KafkaProducer<String, String> producer) throws Exception {
        sendToKafka(topic, payload, producer)
                .thenRunAsync(() -> LOG.info("Sent payload to Kafka (length: {})", payload.length()), CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS))
                .get(15L, TimeUnit.SECONDS);
    }

    public static KafkaProducer<String, String> generateProducer() {
        return KafkaProducer.create(Vertx.vertx(), Map.of(
                "bootstrap.servers", System.getProperty(TrustyKafkaTestResource.KAFKA_BOOTSTRAP_SERVERS, "localhost:9092"),
                "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                "value.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                "acks", "all"
        ));
    }
}
