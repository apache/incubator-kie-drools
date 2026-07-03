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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.kie.kogito.Model;
import org.kie.kogito.persistence.jdbc.AbstractProcessInstancesDataIsolationIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for Spring Boot JDBC persistence data isolation tests.
 * Provides common configuration and test method overrides with Spring transaction management.
 */
@Transactional
public abstract class BaseSpringBootDataIsolationIT extends AbstractProcessInstancesDataIsolationIT {

    @Autowired
    public BaseSpringBootDataIsolationIT(DataSource dataSource, Processes processes) {
        super(dataSource, processes);
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public Processes mockProcesses(@Autowired DataSource dataSource) {
            return new Processes() {
                private final Map<String, BpmnProcess> processMap = new HashMap<>();

                {
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
                    return AbstractProcessInstancesDataIsolationIT.localProcessIds();
                }

                @Override
                public Process<? extends Model> processById(String processId) {
                    return processMap.get(processId);
                }

                @Override
                public Collection<Process<? extends Model>> processes() {
                    return localProcessIds().stream().map(processMap::get).collect(Collectors.toSet());
                }
            };
        }
    }
}
