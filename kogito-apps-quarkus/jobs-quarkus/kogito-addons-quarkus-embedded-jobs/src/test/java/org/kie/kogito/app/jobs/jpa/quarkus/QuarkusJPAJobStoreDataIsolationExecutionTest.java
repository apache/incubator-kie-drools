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
package org.kie.kogito.app.jobs.jpa.quarkus;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.app.jobs.impl.JobDetailsHelper;
import org.kie.kogito.app.jobs.jpa.DataIsolationKeyDescriptor;
import org.kie.kogito.app.jobs.jpa.JPAJobStore;
import org.kie.kogito.app.jobs.quarkus.QuarkusJobsService;
import org.kie.kogito.app.jobs.quarkus.jpa.QuarkusJPAJobContext;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for verifying data isolation in job execution when Processes bean is available.
 * Tests that jobs are executed when their process ID matches a locally deployed process.
 */
@QuarkusTest
@TestProfile(QuarkusJPAJobStoreDataIsolationExecutionTest.DataIsolationProfile.class)
public class QuarkusJPAJobStoreDataIsolationExecutionTest {
    private static final Logger LOG = LoggerFactory.getLogger(QuarkusJPAJobStoreDataIsolationExecutionTest.class);

    @Inject
    JobsService jobsService;

    @Inject
    TestJobSchedulerListener listener;

    @Inject
    TestJobExecutor testJobExecutor;

    @Inject
    TestExceptionHandler exceptionHandler;

    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction userTransaction;

    /**
     * Mock Processes bean that simulates locally deployed processes.
     */
    @Alternative
    @ApplicationScoped
    public static class MockProcesses implements Processes {

        private static final Set<DataIsolationKeyDescriptor> LOCAL_PROCESS_IDS = Set.of(new DataIsolationKeyDescriptor("localProcess1", "1.0"), new DataIsolationKeyDescriptor("localProcess2", "2.0"));

        // Pre-initialize the map of mocks on creation (main test thread)
        private final java.util.Map<DataIsolationKeyDescriptor, Process<? extends Model>> mockProcessesMap =
                LOCAL_PROCESS_IDS.stream().collect(Collectors.toMap(
                        id -> id,
                        it -> {
                            Process<? extends Model> p = mock(Process.class);
                            when(p.id()).thenReturn(it.processId());
                            when(p.version()).thenReturn(it.processVersion());
                            return p;
                        }));

        @Override
        public Collection<String> processIds() {
            return LOCAL_PROCESS_IDS.stream().map(DataIsolationKeyDescriptor::processId).collect(Collectors.toSet());
        }

        @Override
        public Process<? extends Model> processById(String processId) {
            // No Mockito code executed here at runtime
            return mockProcessesMap.values().stream().filter(it -> processId.equals(it.id())).findFirst().orElse(null);
        }

        @Override
        public Collection<Process<? extends Model>> processes() {
            // No Mockito code executed here at runtime
            return mockProcessesMap.values();
        }
    }

    /**
     * Test profile that enables the mock alternatives
     */
    public static class DataIsolationProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MockProcesses.class);
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("kogito.persistence.data-isolation.enabled", "true");
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Destroy auto-started scheduler FIRST to prevent early execution
        try {
            ((QuarkusJobsService) jobsService).destroy();
        } catch (Exception e) {
            // Log but continue - we need to proceed with test setup
            LOG.error("Warning: Error destroying scheduler in setup: {}", e.getMessage());
        }

        testJobExecutor.reset();
        exceptionHandler.reset();

        // Directly persist jobs to database (bypassing scheduleJob to avoid transaction synchronizations)
        JPAJobStore jobStore = new JPAJobStore();
        JobContext context = new QuarkusJPAJobContext(null, entityManager);
        userTransaction.begin();
        try {
            // Local jobs that should be executed
            ProcessInstanceJobDescription localJob1 = new ProcessInstanceJobDescription(
                    "local-job-1",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId1",
                    null,
                    "localProcess1",
                    "1.0",
                    null,
                    null,
                    "nodeInstanceId1");

            ProcessInstanceJobDescription localJob2 = new ProcessInstanceJobDescription(
                    "local-job-2",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId3",
                    null,
                    "localProcess1",
                    "1.0",
                    null,
                    null,
                    "nodeInstanceId3");

            ProcessInstanceJobDescription localJob3 = new ProcessInstanceJobDescription(
                    "local-job-3",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId4",
                    null,
                    "localProcess2",
                    "2.0",
                    null,
                    null,
                    "nodeInstanceId4");

            ProcessInstanceJobDescription remoteJob3 = new ProcessInstanceJobDescription(
                    "remote-job-3",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId4",
                    null,
                    "localProcess2",
                    "2.1",
                    null,
                    null,
                    "nodeInstanceId6");

            // Remote jobs that should NOT be executed
            ProcessInstanceJobDescription remoteJob1 = new ProcessInstanceJobDescription(
                    "remote-job-1",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId2",
                    null,
                    "remoteProcess1",
                    "x",
                    null,
                    null,
                    "nodeInstanceId2");

            ProcessInstanceJobDescription remoteJob2 = new ProcessInstanceJobDescription(
                    "remote-job-2",
                    "-1",
                    ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))),
                    5,
                    "processInstanceId5",
                    null,
                    "remoteProcess2",
                    "y",
                    null,
                    null,
                    "nodeInstanceId5");

            // Directly persist jobs without using scheduleJob (avoids transaction synchronizations)
            JobDetails jobDetails1 = JobDetailsHelper.newScheduledJobDetails(localJob1);
            JobDetails jobDetails2 = JobDetailsHelper.newScheduledJobDetails(localJob2);
            JobDetails jobDetails3 = JobDetailsHelper.newScheduledJobDetails(localJob3);
            JobDetails jobDetails4 = JobDetailsHelper.newScheduledJobDetails(remoteJob1);
            JobDetails jobDetails5 = JobDetailsHelper.newScheduledJobDetails(remoteJob2);
            JobDetails jobDetails6 = JobDetailsHelper.newScheduledJobDetails(remoteJob3);

            jobStore.persist(context, jobDetails1);
            jobStore.persist(context, jobDetails2);
            jobStore.persist(context, jobDetails3);
            jobStore.persist(context, jobDetails4);
            jobStore.persist(context, jobDetails5);
            jobStore.persist(context, jobDetails6);

            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }

        // Now init the scheduler - it will load all jobs via loadActiveJobs()
        ((QuarkusJobsService) jobsService).init();
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        try {
            ((QuarkusJobsService) jobsService).destroy();
        } catch (Exception e) {
            LOG.error("Warning: Error destroying scheduler in cleanup: {}", e.getMessage());
        }
        entityManager.createQuery("DELETE FROM JobDetailsEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testDataIsolationJobExecution() throws Exception {
        // Expect only the 3 local jobs to be executed
        listener.setCount(3);

        // Wait for jobs to execute
        assertThat(listener.await(10, TimeUnit.SECONDS)).isTrue();

        // Verify exactly 3 local jobs were executed and remote jobs were not
        assertThat(listener.getExecutedJobIds())
                .hasSize(3)
                .containsExactlyInAnyOrder("local-job-1", "local-job-2", "local-job-3")
                .doesNotContain("remote-job-1", "remote-job-2", "remote-job-3");
    }
}
