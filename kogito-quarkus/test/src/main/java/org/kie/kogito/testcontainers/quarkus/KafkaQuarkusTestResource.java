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
package org.kie.kogito.testcontainers.quarkus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.kie.kogito.test.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

/**
 * Kafka quarkus resource that works within the test lifecycle.
 *
 */
public class KafkaQuarkusTestResource extends ConditionalQuarkusTestResource<KogitoKafkaContainer> {

    public static final String KOGITO_KAFKA_PROPERTY = "kafka.bootstrap.servers";
    public static final String KOGITO_KAFKA_TOPICS = "kogito.test.topics";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaQuarkusTestResource.class);
    private List<String> topics = emptyList();

    public KafkaQuarkusTestResource() {
        super(new KogitoKafkaContainer());
    }

    @Override
    protected Map<String, String> getProperties() {
        return singletonMap(KOGITO_KAFKA_PROPERTY, "localhost:" + getTestResource().getMappedPort());
    }

    @Override
    public void init(Map<String, String> initArgs) {
        if (initArgs.containsKey(KOGITO_KAFKA_TOPICS)) {
            topics = Arrays.stream(initArgs.getOrDefault(KOGITO_KAFKA_TOPICS, "").split(",")).collect(toList());
        }
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> props = super.start();
        String bootstrap = props.get(KOGITO_KAFKA_PROPERTY);
        if (bootstrap != null) {
            try {
                AdminClient client = AdminClient.create(singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap));
                List<NewTopic> newTopics = topics.stream().map(e -> new NewTopic(e, 1, (short) 1)).collect(Collectors.toList());
                CreateTopicsResult result = client.createTopics(newTopics);
                LOGGER.info("trying to create {}", result);
                result.all().get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("Error creating {}", topics, e);
            }
        }
        return props;
    }

    public static class Conditional extends KafkaQuarkusTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }
}
