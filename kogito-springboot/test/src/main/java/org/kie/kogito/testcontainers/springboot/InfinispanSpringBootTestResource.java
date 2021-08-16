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
package org.kie.kogito.testcontainers.springboot;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.test.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoInfinispanContainer;

/**
 * Infinispan spring boot resource that works within the test lifecycle.
 *
 */
public class InfinispanSpringBootTestResource extends ConditionalSpringBootTestResource<KogitoInfinispanContainer> {

    public static final String KOGITO_INFINISPAN_PROPERTY = "infinispan.remote.server-list";

    public InfinispanSpringBootTestResource() {
        super(new KogitoInfinispanContainer());
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(KOGITO_INFINISPAN_PROPERTY, "localhost:" + getTestResource().getMappedPort());
        properties.put("infinispan.remote.use-auth", "false");
        return properties;
    }

    public static class Conditional extends InfinispanSpringBootTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }

}
