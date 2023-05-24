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

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.common.annotation.Identifier;
import io.vertx.mutiny.core.Vertx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
@QuarkusTestResource(value = KafkaQuarkusTestResource.class, restrictToAnnotatedClass = true)
class KafkaConfigurationTest {

    private KafkaConfiguration tested;

    private static final String TOPIC = UUID.randomUUID().toString();

    @Inject
    Vertx vertx;

    @Inject
    @Identifier("default-kafka-broker")
    Instance<Map<String, Object>> defaultKafkaConfiguration;

    @Test
    void topicConfiguration() {
        tested = new KafkaConfiguration(defaultKafkaConfiguration, vertx, Optional.of(Boolean.TRUE), TOPIC);
        assertThat(tested.getAdminClient()).isNull();
        tested.topicConfiguration(new StartupEvent());
        assertThat(tested.getAdminClient()).isNotNull();
        await().atMost(Duration.ofSeconds(4)).untilAsserted(
                () -> assertThat(tested.getAdminClient().listTopicsAndAwait()).contains(TOPIC));
        tested.getAdminClient().deleteTopicsAndAwait(Arrays.asList(TOPIC));
    }

    @Test
    void topicConfigurationDisabledEvents() {
        tested = new KafkaConfiguration(defaultKafkaConfiguration, vertx, Optional.of(Boolean.FALSE), TOPIC);
        assertThat(tested.getAdminClient()).isNull();
        tested.topicConfiguration(new StartupEvent());
        assertThat(tested.getAdminClient()).isNull();
    }
}
