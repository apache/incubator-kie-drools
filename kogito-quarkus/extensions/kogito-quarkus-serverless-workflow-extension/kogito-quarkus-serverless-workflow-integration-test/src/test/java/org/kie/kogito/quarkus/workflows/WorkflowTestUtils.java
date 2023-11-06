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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkflowTestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowTestUtils.class);
    public static final int TIME_OUT_SECONDS = 50;
    public static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";

    private WorkflowTestUtils() {
    }

    public static JsonPath waitForKogitoProcessInstanceEvent(KafkaTestClient kafkaClient, boolean shutdownAfterConsume) throws Exception {
        return waitForKogitoProcessInstanceEvent(kafkaClient, ProcessInstanceStateDataEvent.class, (e) -> true, shutdownAfterConsume);
    }

    public static <T extends DataEvent<?>> JsonPath waitForKogitoProcessInstanceEvent(KafkaTestClient kafkaClient, Class<T> eventType, Predicate<JsonPath> predicate, boolean shutdownAfterConsume)
            throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<JsonPath> cloudEvent = new AtomicReference<>();

        kafkaClient.consume(KOGITO_PROCESSINSTANCES_EVENTS, rawCloudEvent -> {
            JsonPath path = new JsonPath(rawCloudEvent);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CLOUD EVENT: {}", path.prettyPrint());
            }
            String type = path.get("type");
            if (eventType.getSimpleName().equals(type) && predicate.test(path)) {
                cloudEvent.set(path);
                countDownLatch.countDown();
            }
        });
        // give some time to consume the event
        assertThat(countDownLatch.await(TIME_OUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        if (shutdownAfterConsume) {
            kafkaClient.shutdown();
        }
        return cloudEvent.get();
    }

}
