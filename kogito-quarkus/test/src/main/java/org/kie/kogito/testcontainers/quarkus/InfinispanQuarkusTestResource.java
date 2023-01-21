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

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.test.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.testcontainers.KogitoInfinispanContainer;

/**
 * Infinispan quarkus resource that works within the test lifecycle.
 */
public class InfinispanQuarkusTestResource extends ConditionalQuarkusTestResource<KogitoInfinispanContainer> {

    public static final String KOGITO_INFINISPAN_PROPERTY = "quarkus.infinispan-client.hosts";

    public InfinispanQuarkusTestResource() {
        super(new KogitoInfinispanContainer());
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(KOGITO_INFINISPAN_PROPERTY, getServerUrl());
        properties.put("quarkus.infinispan-client.use-auth", "false");
        return properties;
    }

    public static class Conditional extends InfinispanQuarkusTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }
}
