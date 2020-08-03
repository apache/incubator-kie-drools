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
package org.kie.kogito.testcontainers;

import java.time.Duration;

import org.kie.kogito.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * This container wraps Keycloak container
 *
 */
public class KeycloakContainer extends GenericContainer<KeycloakContainer> implements TestResource {

    public static final String NAME = "keycloak";
    public static final String KEYCLOAK_PROPERTY = "container.image." + NAME;
    public static final int PORT = 8080;

    private static final String REALM_FILE = "/tmp/realm.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakContainer.class);

    public KeycloakContainer() {
        addExposedPort(PORT);
        withEnv("KEYCLOAK_USER", "admin");
        withEnv("KEYCLOAK_PASSWORD", "admin");
        withEnv("KEYCLOAK_IMPORT", REALM_FILE);
        withClasspathResourceMapping("testcontainers/keycloak/kogito-realm.json", REALM_FILE, BindMode.READ_ONLY);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        waitingFor(Wait.forHttp("/auth").withStartupTimeout(Duration.ofMinutes(5)));
        setDockerImageName(System.getProperty(KEYCLOAK_PROPERTY));
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

}
