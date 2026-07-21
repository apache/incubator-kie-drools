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

import java.util.*;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.kie.kogito.Model;
import org.kie.kogito.persistence.jdbc.AbstractProcessInstancesDataIsolationIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.bpmn2.BpmnProcess;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * Base class for Quarkus JDBC persistence data isolation tests.
 * Provides common configuration and test method overrides.
 */
public abstract class BaseQuarkusDataIsolationIT extends AbstractProcessInstancesDataIsolationIT {

    @Inject
    public BaseQuarkusDataIsolationIT(DataSource dataSource, Processes processes) {
        super(dataSource, processes);
    }

    /**
     * Mock Processes bean that returns only local process IDs for data isolation testing.
     */
    @Alternative
    @ApplicationScoped
    public static class MockProcesses implements Processes {

        private final Map<String, BpmnProcess> processMap = new HashMap<>();
        private final DataSource dataSource;

        @Inject
        public MockProcesses(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @PostConstruct
        public void init() {
            // Create all processes in a single application context to ensure CallActivity can find subprocesses
            Map<String, BpmnProcess> processes = AbstractProcessInstancesDataIsolationIT.createProcesses(
                    dataSource,
                    this,
                    "BPMN2-UserTask.bpmn2",
                    "BPMN2-CallActivity.bpmn2",
                    "Remote-BPMN2-CallActivity.bpmn2");

            processMap.putAll(processes);
        }

        @Override
        public Collection<String> processIds() {
            // Return only LOCAL process IDs for data isolation filtering
            // Remote_BPMN2_CallActivity is intentionally excluded
            return localProcessIds();
        }

        @Override
        public Collection<Process<? extends Model>> processes() {
            return processMap.values().stream().filter(it -> localProcessIds().contains(it.id())).collect(Collectors.toSet());
        }

        @Override
        public Process<? extends Model> processById(String processId) {
            return processMap.get(processId);
        }
    }
}
