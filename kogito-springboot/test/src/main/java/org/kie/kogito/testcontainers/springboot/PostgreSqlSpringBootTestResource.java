/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.testcontainers.springboot;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.test.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;

import static org.kie.kogito.testcontainers.KogitoPostgreSqlContainer.POSTGRESQL_CONNECTION_URI;

/**
 * PostgreSQL spring boot resource that works within the test lifecycle.
 */
public class PostgreSqlSpringBootTestResource extends ConditionalSpringBootTestResource<KogitoPostgreSqlContainer> {

    public static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    public static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";

    public PostgreSqlSpringBootTestResource() {
        super(new KogitoPostgreSqlContainer());
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(POSTGRESQL_CONNECTION_URI, getTestResource().getReactiveUrl());
        properties.put(SPRING_DATASOURCE_URL, getTestResource().getJdbcUrl());
        properties.put(SPRING_DATASOURCE_USERNAME, getTestResource().getUsername());
        properties.put(SPRING_DATASOURCE_PASSWORD, getTestResource().getPassword());
        return properties;
    }

    public static class Conditional extends PostgreSqlSpringBootTestResource {

        public Conditional() {
            enableConditional();
        }
    }
}
