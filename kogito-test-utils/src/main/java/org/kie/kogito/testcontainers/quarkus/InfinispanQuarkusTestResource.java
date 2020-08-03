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
package org.kie.kogito.testcontainers.quarkus;

import org.kie.kogito.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.testcontainers.InfinispanContainer;

/**
 * Infinispan quarkus resource that works within the test lifecycle.
 *
 */
public class InfinispanQuarkusTestResource extends ConditionalQuarkusTestResource {

    public static final String KOGITO_INFINISPAN_PROPERTY = "quarkus.infinispan-client.server-list";

    public InfinispanQuarkusTestResource() {
        super(new InfinispanContainer());
    }

    @Override
    protected String getKogitoProperty() {
        return KOGITO_INFINISPAN_PROPERTY;
    }

    public static class Conditional extends InfinispanQuarkusTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }
}
