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
package org.kie.kogito.persistence.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * PostgreSQL variant of data isolation test for JDBC persistence.
 * Tests that process instances are properly filtered by local process IDs when Processes bean is available.
 */
@Testcontainers
public class PostgreSqlProcessInstancesDataIsolationIT extends AbstractProcessInstancesDataIsolationIT {

    @Container
    private final static KogitoPostgreSqlContainer PG_CONTAINER = new KogitoPostgreSqlContainer();
    private static PGSimpleDataSource PG_DATA_SOURCE;

    public PostgreSqlProcessInstancesDataIsolationIT() {
        super(PG_DATA_SOURCE, createMockProcesses());
    }

    private static Processes createMockProcesses() {
        return new Processes() {
            private final Map<String, BpmnProcess> processMap = new HashMap<>();

            {
                // Create all processes in a single application context to ensure CallActivity can find subprocesses
                if (PG_DATA_SOURCE != null) {
                    Map<String, BpmnProcess> processes = AbstractProcessInstancesDataIsolationIT.createProcesses(
                            PG_DATA_SOURCE,
                            this,
                            "BPMN2-UserTask.bpmn2",
                            "BPMN2-CallActivity.bpmn2",
                            "Remote-BPMN2-CallActivity.bpmn2");

                    processMap.putAll(processes);
                }
            }

            @Override
            public Collection<String> processIds() {
                // Return only LOCAL process IDs for data isolation filtering
                // Remote_BPMN2_CallActivity is intentionally excluded
                return AbstractProcessInstancesDataIsolationIT.localProcessIds();
            }

            @Override
            public Collection<Process<? extends Model>> processes() {
                return processMap.values().stream().filter(it -> localProcessIds().contains(it.process().getId())).collect(Collectors.toSet());
            }

            @Override
            public Process<? extends Model> processById(String processId) {
                return processMap.get(processId);
            }
        };
    }

    @AfterEach
    public void cleanup() {
        abortInstances();
    }

    @BeforeAll
    public static void start() {
        PG_DATA_SOURCE = new PGSimpleDataSource();
        PG_DATA_SOURCE.setUrl(PG_CONTAINER.getJdbcUrl());
        PG_DATA_SOURCE.setUser(PG_CONTAINER.getUsername());
        PG_DATA_SOURCE.setPassword(PG_CONTAINER.getPassword());
        AbstractProcessInstancesIT.initMigration(PG_DATA_SOURCE);
    }
}
