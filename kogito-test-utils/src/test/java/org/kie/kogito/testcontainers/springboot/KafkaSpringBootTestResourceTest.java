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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaSpringBootTestResourceTest {

    @Mock
    private KogitoKafkaContainer container;

    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    private KafkaSpringBootTestResource resource;

    @Test
    public void shouldGetProperty() {
        givenResource();
        assertEquals(KafkaSpringBootTestResource.KOGITO_KAFKA_PROPERTY, resource.getKogitoProperty());
    }

    @Test
    public void shouldConditionalBeDisabledByDefault() {
        givenResource();
        thenConditionalIsDisabled();
    }

    @Test
    public void shouldConditionalBeEnabled() {
        givenConditionalResource();
        thenConditionalIsEnabled();
    }

    @Test
    public void shouldAddKafkaClientInContext() {
        givenResource();
        givenContainer();
        whenUpdateBeanFactory();
        thenKafkaClientIsRegistered();
    }

    private void givenConditionalResource() {
        resource = spy(new KafkaSpringBootTestResource.Conditional());
    }

    private void givenResource() {
        resource = spy(new KafkaSpringBootTestResource());
    }

    private void givenContainer() {
        doReturn(container).when(resource).getTestResource();
    }

    private void whenUpdateBeanFactory() {
        resource.updateBeanFactory(beanFactory);
    }

    private void thenKafkaClientIsRegistered() {
        verify(beanFactory).registerSingleton(eq(KafkaClient.class.getName()), isA(KafkaClient.class));
    }

    private void thenConditionalIsEnabled() {
        assertTrue(resource.isConditionalEnabled());
    }

    private void thenConditionalIsDisabled() {
        assertFalse(resource.isConditionalEnabled());
    }
}
