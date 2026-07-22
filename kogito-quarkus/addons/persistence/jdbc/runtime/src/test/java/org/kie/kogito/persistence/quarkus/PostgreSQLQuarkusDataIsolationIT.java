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

package org.kie.kogito.persistence.quarkus;

import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.kie.kogito.process.Processes;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import jakarta.inject.Inject;

/**
 * PostgreSQL variant of Process Instances data isolation test for Quarkus.
 * Tests that process instances are properly filtered by local process IDs when Processes bean is available.
 */
@QuarkusTest
@QuarkusTestResource(value = PostgreSqlQuarkusTestResource.class, restrictToAnnotatedClass = true)
@TestProfile(PostgreSQLQuarkusDataIsolationIT.DataIsolationProfile.class)
@TestTransaction
public class PostgreSQLQuarkusDataIsolationIT extends BaseQuarkusDataIsolationIT {

    @Inject
    public PostgreSQLQuarkusDataIsolationIT(DataSource dataSource, Processes processes) {
        super(dataSource, processes);
    }

    /**
     * Test profile that enables the mock Processes bean for data isolation testing.
     */
    public static class DataIsolationProfile extends PostgreSQLTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MockProcesses.class);
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("kogito.persistence.data-isolation.enabled", "true");
        }
    }
}
