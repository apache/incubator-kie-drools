/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.junit.QuarkusTestProfile;

import static java.util.Collections.singletonMap;
import static org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource.KOGITO_OIDC_TENANTS;

public class KeycloakTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();
        config.put("quarkus.http.auth.policy.role-policy1.roles-allowed", "confidential");
        config.put("quarkus.http.auth.permission.roles1.paths", "/*");
        config.put("quarkus.http.auth.permission.roles1.policy", "role-policy1");
        return config;
    }

    @Override
    public String getConfigProfile() {
        return "keycloak-test";
    }

    @Override
    public List<TestResourceEntry> testResources() {
        Map<String, String> args = singletonMap(KOGITO_OIDC_TENANTS, "web-app-tenant");
        return Arrays.asList(
                new TestResourceEntry(PostgreSqlQuarkusTestResource.class, Collections.emptyMap(), true),
                new TestResourceEntry(InMemoryMessagingTestResource.class, Collections.emptyMap(), true),
                new TestResourceEntry(KeycloakQuarkusTestResource.class, args, true));
    }

}
