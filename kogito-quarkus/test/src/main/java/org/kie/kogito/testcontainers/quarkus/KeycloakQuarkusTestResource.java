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
package org.kie.kogito.testcontainers.quarkus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.test.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.testcontainers.KogitoKeycloakContainer;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Keycloak quarkus resource that works within the test lifecycle.
 */
public class KeycloakQuarkusTestResource extends ConditionalQuarkusTestResource<KogitoKeycloakContainer> {

    public static final String KOGITO_KEYCLOAK_PROPERTY = "quarkus.oidc.auth-server-url";
    public static final String KOGITO_OIDC_TENANTS = "kogito.test.tenants";

    private List<String> tenants = emptyList();

    public KeycloakQuarkusTestResource() {
        super(new KogitoKeycloakContainer());
    }

    @Override
    public void init(Map<String, String> initArgs) {
        if (initArgs.containsKey(KOGITO_OIDC_TENANTS)) {
            tenants = Arrays.stream(initArgs.getOrDefault(KOGITO_OIDC_TENANTS, "").split(",")).collect(toList());
        }
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        String url = format("http://localhost:%s/realms/kogito", getTestResource().getMappedPort());
        properties.put(KOGITO_KEYCLOAK_PROPERTY, url);
        tenants.forEach(tenant -> properties.put(format("quarkus.oidc.%s.auth-server-url", tenant), url));
        return properties;
    }

    public static class Conditional extends KeycloakQuarkusTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }

}
