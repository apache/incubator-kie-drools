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

import org.kie.kogito.test.resources.TestResource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * This container wraps Keycloak container
 */
public class KogitoKeycloakContainer extends KogitoGenericContainer<KogitoKeycloakContainer> implements TestResource {

    public static final String NAME = "keycloak";
    public static final String USER = "admin";
    public static final String PASSWORD = "admin";
    public static final String REALM = "kogito";
    public static final String CLIENT_ID = "kogito-app";
    public static final String CLIENT_SECRET = "secret";
    public static final int PORT = 8080;

    private static final String REALM_FILE = "/opt/keycloak/data/import/realm.json";

    public KogitoKeycloakContainer() {
        super(NAME);
        addExposedPort(PORT);
        withEnv("KEYCLOAK_ADMIN", USER);
        withEnv("KEYCLOAK_ADMIN_PASSWORD", PASSWORD);
        withClasspathResourceMapping("testcontainers/keycloak/kogito-realm.json", REALM_FILE, BindMode.READ_ONLY);
        waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1));
        withCommand("start-dev --import-realm");
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
