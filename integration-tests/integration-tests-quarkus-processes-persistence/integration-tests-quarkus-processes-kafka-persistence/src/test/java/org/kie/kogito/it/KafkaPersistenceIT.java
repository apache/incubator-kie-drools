/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.it;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.common.annotation.Identifier;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class KafkaPersistenceIT extends PersistenceTest {

    public static final String PROCESS_TOPIC = "kogito.process." + PersistenceTest.PROCESS_ID;
    public static final String PROCESS_EMBEDDED_TOPIC = "kogito.process." + PersistenceTest.PROCESS_EMBEDDED_ID;
    private Logger LOGGER = LoggerFactory.getLogger(KafkaPersistenceIT.class);

    @Inject
    @Identifier("default-kafka-broker")
    Map<String, Object> kafkaConfig;

    @BeforeEach
    public void init() {
        List<String> topics = Arrays.asList(PROCESS_TOPIC, PROCESS_EMBEDDED_TOPIC);
        try {
            AdminClient client = AdminClient.create(kafkaConfig);
            List<NewTopic> newTopics = topics.stream().map(e -> new NewTopic(e, 1, (short) 1)).collect(Collectors.toList());
            CreateTopicsResult result = client.createTopics(newTopics);
            LOGGER.info("trying to create {}", result);
            result.all().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Error creating {}", topics, e);
        }
    }
}
