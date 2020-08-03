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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.InfinispanContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

public class InfinispanSpringBootTestResourceTest {

    private static final String IMAGE = "my-infinispan-image";

    private InfinispanSpringBootTestResource resource;

    @BeforeEach
    public void setup() {
        System.setProperty(InfinispanContainer.INFINISPAN_PROPERTY, IMAGE);
        givenResource();
    }

    @Test
    public void shouldGetProperty() {
        assertEquals(InfinispanSpringBootTestResource.KOGITO_INFINISPAN_PROPERTY, resource.getKogitoProperty());
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

    private void givenResource() {
        resource = new InfinispanSpringBootTestResource();
    }

    private void givenConditionalResource() {
        resource = spy(new InfinispanSpringBootTestResource.Conditional());
    }

    private void thenConditionalIsEnabled() {
        assertTrue(resource.isConditionalEnabled());
    }

    private void thenConditionalIsDisabled() {
        assertFalse(resource.isConditionalEnabled());
    }
}
