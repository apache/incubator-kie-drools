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

package org.jbpm.usertask.jpa.springboot;

import java.util.Collection;
import java.util.stream.Collectors;

import org.jbpm.usertask.jpa.AbstractUserTaskInstancesDataIsolationIT;
import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.jbpm.usertask.jpa.springboot.repository.SpringBootUserTaskJPAContext;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseSpringBootDataIsolationIT extends AbstractUserTaskInstancesDataIsolationIT {

    public BaseSpringBootDataIsolationIT(JPAUserTaskInstances userTaskInstances, UserTaskInstanceRepository userTaskInstanceRepository, SpringBootUserTaskJPAContext context, Processes processes) {
        super(userTaskInstances, userTaskInstanceRepository, context, processes);
    }

    /**
     * Override test methods that access lazy collections and wrap them in TransactionTemplate
     * to ensure the Hibernate session remains open for lazy loading.
     */

    @Test
    @Transactional
    @Override
    public void testDataIsolationWithProcessesBean() {
        super.testDataIsolationWithProcessesBean();
    }

    @Test
    @Transactional
    @Override
    public void testFindByIdFiltersRemoteProcessTasks() {
        super.testFindByIdFiltersRemoteProcessTasks();
    }

    @Test
    @Transactional
    @Override
    public void testExistsFiltersRemoteProcessTasks() {
        super.testExistsFiltersRemoteProcessTasks();
    }

    @Test
    @Transactional
    @Override
    public void testFindByIdentityWithProcessFiltering() {
        super.testFindByIdentityWithProcessFiltering();
    }

    @Test
    @Transactional
    @Override
    public void testFindAllFiltersRemoteProcessTasks() {
        super.testFindAllFiltersRemoteProcessTasks();
    }

    @Test
    @Transactional
    @Override
    public void testFilteringWithMultipleProcessIds() {
        super.testFilteringWithMultipleProcessIds();
    }

    @Test
    @Transactional
    @Override
    public void testNoResultsWhenProcessIdsDoNotMatch() {
        super.testNoResultsWhenProcessIdsDoNotMatch();
    }

    @Test
    @Transactional
    @Override
    public void testFindAllMatchesProcessIdWhenRootProcessIdIsNull() {
        super.testFindAllMatchesProcessIdWhenRootProcessIdIsNull();
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public Processes mockProcesses() {
            return new Processes() {
                @Override
                public Collection<String> processIds() {
                    return localProcessIds();
                }

                @Override
                public Process<? extends Model> processById(String processId) {
                    Process<? extends Model> process = mock(Process.class);
                    when(process.id()).thenReturn(processId);
                    when(process.version()).thenReturn("1.0");
                    return process;
                }

                @Override
                public Collection<Process<? extends Model>> processes() {
                    return localProcessIds().stream().map(this::processById).collect(Collectors.toSet());
                }
            };
        }
    }
}
