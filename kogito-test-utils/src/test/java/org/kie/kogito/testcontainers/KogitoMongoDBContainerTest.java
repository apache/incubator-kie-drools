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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class KogitoMongoDBContainerTest {

    private static final int MAPPED_PORT = 27020;

    private KogitoMongoDBContainer container;

    @BeforeEach
    public void setup() {
        container = spy(new KogitoMongoDBContainer());
        doNothing().when(container).start();
    }

    @Test
    void shouldGetResourceName() {
        assertThat(container.getResourceName()).isEqualTo(KogitoMongoDBContainer.NAME);
    }

    @Test
    void shouldGetMapperPort() {
        doReturn(MAPPED_PORT).when(container).getMappedPort(KogitoMongoDBContainer.MONGODB_INTERNAL_PORT);
        assertThat(container.getMappedPort()).isEqualTo(MAPPED_PORT);
    }

    @Test
    void shouldGetDockerImageName() {
        assertThat(container.getDockerImageName()).isEqualTo(System.getProperty(Constants.CONTAINER_NAME_PREFIX + KogitoMongoDBContainer.NAME));
    }
}
