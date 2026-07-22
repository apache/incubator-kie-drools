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
package org.kie.kogito.persistence.springboot;

import javax.sql.DataSource;

import org.kie.kogito.process.Processes;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * PostgreSQL variant of JDBC persistence data isolation test for Spring Boot.
 * Tests that process instances are properly filtered by local process IDs when Processes bean is available.
 */
@SpringBootTest(classes = { TestApplication.class, BaseSpringBootDataIsolationIT.TestConfig.class })
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
@ActiveProfiles("postgresql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostgreSQLSpringBootDataIsolationIT extends BaseSpringBootDataIsolationIT {

    @Autowired
    public PostgreSQLSpringBootDataIsolationIT(DataSource dataSource, Processes processes) {
        super(dataSource, processes);
    }
}
