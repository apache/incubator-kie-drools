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
package org.kie.kogito.testcontainers;

import org.kie.kogito.test.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Kafka Container for Kogito examples.
 */
public class KogitoKafkaContainer extends KafkaContainer implements TestResource {

    public static final String NAME = "kafka";
    public static final String KAFKA_PROPERTY = "container.image." + NAME;

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoKafkaContainer.class);

    public KogitoKafkaContainer() {
        super(DockerImageName.parse(kafkaImage()));
        withLogConsumer(f -> System.out.print(f.getUtf8String()));
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        waitingFor(Wait.forListeningPort());
        withStartupTimeout(Constants.CONTAINER_START_TIMEOUT);
        withEnv("KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS", "180000");
        withEnv("KAFKA_TRANSACTION_MAX_TIMEOUT_MS", "180000");
        withEnv("KAFKA_CONNECTIONS_MAX_IDLE_MS", "180000");
        withEnv("KAFKA_OFFSETS_RETENTION_MINUTES", "1");
        withEnv("KAFKA_AUTO_LEADER_REBALANCE_ENABLE", "false");
        withEnv("KAFKA_CONFLUENT_SUPPORT_METRICS_ENABLE", "false");
        withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");
        withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLED", "true");
    }

    @Override
    public void start() {
        super.start();
        LOGGER.info("Kafka servers: {}", getBootstrapServers());
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(KAFKA_PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

    private static String kafkaImage() {
        String kafkaImage = System.getProperty(KAFKA_PROPERTY);
        if (kafkaImage == null) {
            throw new IllegalStateException("Please provide '" + KAFKA_PROPERTY + "' system property");
        }
        return kafkaImage;
    }
}
