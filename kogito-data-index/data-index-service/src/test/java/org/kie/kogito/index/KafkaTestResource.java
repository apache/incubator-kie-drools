/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import static java.util.Collections.singletonMap;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestResource.class);
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static KafkaContainer KAFKA = new KafkaContainer().withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Override
    public Map<String, String> start() {
        if (KAFKA.isRunning() == false) {
            KAFKA.start();
            LOGGER.info("Kafka servers: {}", KAFKA.getBootstrapServers());
        }
        return singletonMap(KAFKA_BOOTSTRAP_SERVERS, KAFKA.getBootstrapServers());
    }

    @Override
    public void stop() {
        KAFKA.stop();
    }
}
