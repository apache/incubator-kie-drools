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
package org.kie.kogito.jobs.service.stream;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Identifier;
import io.vertx.kafka.admin.NewTopic;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.kafka.admin.KafkaAdminClient;

import static org.kie.kogito.jobs.service.stream.KafkaJobStreams.PUBLISH_EVENTS_CONFIG_KEY;

@Startup
@ApplicationScoped
public class KafkaConfiguration {

    private Instance<Map<String, Object>> defaultKafkaConfiguration;

    private Vertx vertx;

    private Optional<Boolean> enabled;

    private String topic;

    private KafkaAdminClient adminClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Inject
    public KafkaConfiguration(@Identifier("default-kafka-broker") Instance<Map<String, Object>> defaultKafkaConfiguration,
            Vertx vertx,
            @ConfigProperty(name = PUBLISH_EVENTS_CONFIG_KEY) Optional<Boolean> enabled,
            @ConfigProperty(name = "kogito.jobs-events-topic") String topic) {
        this.defaultKafkaConfiguration = defaultKafkaConfiguration;
        this.vertx = vertx;
        this.enabled = enabled;
        this.topic = topic;
    }

    /**
     * Verify if the needed Kafka topics used by the application already exists, in case they are not found create
     * them. This should not be needed in case the infrastructure is already provisioned with all topics into Kafka.
     * This avoids the health check issues for kafka where the topics are all checked.
     * 
     * @param event Startup event
     */
    void topicConfiguration(StartupEvent event) {
        LOGGER.info("Kafka topic configuration check.");
        final Map<String, String> config = defaultKafkaConfiguration.get().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, el -> (String) el.getValue()));
        enabled.filter(Boolean.TRUE::equals)
                .map(e -> getOrCreateClient(config))
                .ifPresent(client -> client.listTopics()
                        .subscribe()
                        .with(t -> Optional.ofNullable(t.contains(topic))
                                .map(match -> new NewTopic(topic, 1, (short) 1))
                                .ifPresent(newTopic -> client.createTopics(Arrays.asList(newTopic))
                                        .subscribe()
                                        .with(r -> LOGGER.info("Created topic {}", topic)))));
    }

    private KafkaAdminClient getOrCreateClient(Map<String, String> config) {
        adminClient = Optional.ofNullable(adminClient)
                .orElseGet(() -> KafkaAdminClient.create(vertx, config));
        return adminClient;
    }

    protected KafkaAdminClient getAdminClient() {
        return adminClient;
    }
}
