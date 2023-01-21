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
import static org.kie.kogito.testcontainers.Constants.CONTAINER_NAME_PREFIX;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class KeycloakContainerTest {

    private static final int MAPPED_PORT = 11112;
    private static final String IMAGE = "my-keycloak-image";

    private KogitoKeycloakContainer container;

    @BeforeEach
    public void setup() {
        System.setProperty(CONTAINER_NAME_PREFIX + KogitoKeycloakContainer.NAME, IMAGE);
        container = spy(new KogitoKeycloakContainer());
        doNothing().when(container).start();
        doNothing().when(container).setDockerImageName(IMAGE);
    }

    @Test
    public void shouldAddDefaultSettings() {
        assertThat(container.getExposedPorts()).contains(KogitoKeycloakContainer.PORT);
        assertThat(container.getEnvMap()).containsEntry("KEYCLOAK_ADMIN", "admin")
                .containsEntry("KEYCLOAK_ADMIN_PASSWORD", "admin");
    }

    @Test
    public void shouldGetResourceName() {
        assertThat(container.getResourceName()).isEqualTo(KogitoKeycloakContainer.NAME);
    }

    @Test
    public void shouldGetMapperPort() {
        doReturn(MAPPED_PORT).when(container).getMappedPort(KogitoKeycloakContainer.PORT);
        assertThat(container.getMappedPort()).isEqualTo(MAPPED_PORT);
    }

}
