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

package org.jbpm.usertask.jpa.quarkus;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.usertask.jpa.AbstractUserTaskInstancesDataIsolationIT;
import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.quarkus.repository.QuarkusUserTaskJPAContext;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * H2 variant of UserTask Storage data isolation test for Quarkus.
 * Tests that user tasks are properly filtered by local process IDs when Processes bean is available.
 */
@QuarkusTest
@QuarkusTestResource(value = H2DatabaseTestResource.class)
@TestProfile(H2QuarkusDataIsolationIT.DataIsolationProfile.class)
@TestTransaction
public class H2QuarkusDataIsolationIT extends AbstractUserTaskInstancesDataIsolationIT {

    @Alternative
    @ApplicationScoped
    public static class MockProcesses implements Processes {

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
    }

    @Inject
    public H2QuarkusDataIsolationIT(JPAUserTaskInstances userTaskInstances,
            UserTaskInstanceRepository userTaskInstanceRepository,
            QuarkusUserTaskJPAContext context, Processes processes) {
        super(userTaskInstances, userTaskInstanceRepository, context, processes);
    }

    /**
     * Test profile that enables the mock Processes bean for data isolation testing.
     */
    public static class DataIsolationProfile extends H2QuarkusTestProfile {
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
