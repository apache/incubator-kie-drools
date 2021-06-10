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
package org.kie.kogito.testcontainers.springboot;

import java.util.Map;

import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.event.ContextClosedEvent;

import static java.util.Collections.singletonMap;

/**
 * Kafka spring boot resource that works within the test lifecycle.
 *
 */
public class KafkaSpringBootTestResource extends ConditionalSpringBootTestResource<KogitoKafkaContainer> {

    public static final String KOGITO_KAFKA_PROPERTY = "spring.kafka.bootstrap-servers";

    private KafkaClient kafkaClient = null;

    public KafkaSpringBootTestResource() {
        super(new KogitoKafkaContainer());
    }

    @Override
    protected Map<String, String> getProperties() {
        return singletonMap(KOGITO_KAFKA_PROPERTY, "localhost:" + getTestResource().getMappedPort());
    }

    @Override
    protected void updateBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.updateBeanFactory(beanFactory);

        if (!beanFactory.containsBean(KafkaClient.class.getName())) {
            kafkaClient = new KafkaClient("localhost:" + getTestResource().getMappedPort());
            beanFactory.registerSingleton(KafkaClient.class.getName(), kafkaClient);
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
        super.onApplicationEvent(event);
    }

    public static class Conditional extends KafkaSpringBootTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }

}
