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

import org.kie.kogito.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;

import static org.kie.kogito.testcontainers.KogitoPostgreSqlContainer.POSTGRESQL_CONNECTION_URI;

/**
 * PostgreSQL spring boot resource that works within the test lifecycle.
 *
 */
public class PostgreSqlSpringBootTestResource extends ConditionalSpringBootTestResource<KogitoPostgreSqlContainer> {

    public PostgreSqlSpringBootTestResource() {
        super(new KogitoPostgreSqlContainer());
    }

    @Override
    protected String getKogitoProperty() {
        return POSTGRESQL_CONNECTION_URI;
    }

    @Override
    protected String getKogitoPropertyValue() {
        return getTestResource().getConnectionUri();
    }

    public static class Conditional extends PostgreSqlSpringBootTestResource {

        public Conditional() {
            enableConditional();
        }
    }
}
