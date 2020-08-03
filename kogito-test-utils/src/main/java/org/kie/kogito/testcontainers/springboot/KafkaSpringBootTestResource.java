/**
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
package org.kie.kogito.testcontainers.springboot;

import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Kafka spring boot resource that works within the test lifecycle.
 *
 */
public class KafkaSpringBootTestResource extends ConditionalSpringBootTestResource {

    public static final String KOGITO_KAFKA_PROPERTY = "spring.kafka.bootstrap-servers";

    public KafkaSpringBootTestResource() {
        super(new KogitoKafkaContainer());
    }

    @Override
    protected String getKogitoProperty() {
        return KOGITO_KAFKA_PROPERTY;
    }

    @Override
    protected void updateBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.updateBeanFactory(beanFactory);

        beanFactory.registerSingleton(KafkaClient.class.getName(), new KafkaClient("localhost:" + getTestResource().getMappedPort()));
    }

    public static class Conditional extends KafkaSpringBootTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }

}
