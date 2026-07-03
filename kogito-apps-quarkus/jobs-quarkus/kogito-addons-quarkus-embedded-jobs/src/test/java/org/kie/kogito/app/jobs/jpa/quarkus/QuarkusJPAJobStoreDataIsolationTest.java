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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.app.jobs.impl.JobDetailsHelper;
import org.kie.kogito.app.jobs.jpa.DataIsolationKeyDescriptor;
import org.kie.kogito.app.jobs.jpa.JPAJobContext;
import org.kie.kogito.app.jobs.jpa.JPAJobStore;
import org.kie.kogito.app.jobs.jpa.JobDetailsEntityHelper;
import org.kie.kogito.app.jobs.jpa.model.JobDetailsEntity;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.ProcessJobDescription;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for verifying data isolation in JPAJobStore when Processes bean is available.
 * Tests that JPAJobStore only loads jobs for locally deployed process IDs.
 */
@QuarkusTest
@TestProfile(QuarkusJPAJobStoreDataIsolationTest.DataIsolationProfile.class)
public class QuarkusJPAJobStoreDataIsolationTest {

    @Inject
    EntityManager entityManager;

    @Inject
    Processes processes;

    private JPAJobStore jobStore;

    /**
     * Mock Processes bean that simulates locally deployed processes.
     * Only returns "localProcess1" and "localProcess2" as local process IDs.
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
    @Transactional
    public void init() {
        entityManager.createQuery("DELETE FROM JobDetailsEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
        jobStore = new JPAJobStore();
    }

    /**
     * Helper method to directly insert a job into the database, simulating
     * a job created by another business service sharing the same database.
     */
    @Transactional
    void insertProcessJob(String jobId, String processId, String version) {
        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(
                ProcessJobDescription.of(
                        ExactExpirationTime.of(OffsetDateTime.now().plusSeconds(10).atZoneSameInstant(ZoneOffset.UTC)),
                        ProcessJobDescription.DEFAULT_PRIORITY,
                        processId,
                        version,
                        jobId));
        persist(jobDetails);
    }

    @Transactional
    void insertProcessInstanceJob(String jobId, String processInstanceId, String processId, String version) {
        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(
                new ProcessInstanceJobDescription(
                        jobId,
                        "timer-" + jobId,
                        ExactExpirationTime.of(OffsetDateTime.now().plusSeconds(10).atZoneSameInstant(ZoneOffset.UTC)),
                        ProcessInstanceJobDescription.DEFAULT_PRIORITY,
                        processInstanceId,
                        null,
                        processId,
                        version,
                        null,
                        null,
                        "node-" + jobId));
        persist(jobDetails);
    }

    @Transactional
    void insertUserTaskJob(String jobId, String userTaskId, String processId, String processVersion) {
        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(
                new UserTaskInstanceJobDescription(
                        jobId,
                        ExactExpirationTime.of(OffsetDateTime.now().plusSeconds(10).atZoneSameInstant(ZoneOffset.UTC)),
                        ProcessInstanceJobDescription.DEFAULT_PRIORITY,
                        userTaskId,
                        processId,
                        processVersion,
                        "process-instance-" + jobId,
                        "node-" + jobId,
                        null,
                        null,
                        null));
        persist(jobDetails);
    }

    private void persist(JobDetails jobDetails) {
        entityManager.persist(JobDetailsEntityHelper.merge(jobDetails, new JobDetailsEntity()));
        entityManager.flush();
    }

    @Test
    @Transactional
    public void testLoadActiveJobsProcess() {
        // Given: Process jobs created through JobDetailsHelper.newScheduledJobDetails
        // where correlationId == correlationRootId == processId (stored in process_id column)
        insertProcessJob("local-job-1", "localProcess1", "1.0");
        insertProcessJob("local-job-2", "localProcess2", "2.0");
        insertProcessJob("local-job-2-1", "localProcess2", "2.1");
        insertProcessJob("remote-job-1", "remoteProcess1", "x");
        insertProcessJob("remote-job-2", "remoteProcess2", "y");
        entityManager.clear();

        // When: Loading active jobs through JPAJobStore
        JobContext jobContext = getJobContext();
        OffsetDateTime maxWindow = OffsetDateTime.now().plus(Duration.ofHours(1));
        List<JobDetails> jobs = jobStore.loadActiveJobs(jobContext, maxWindow);

        // Then: Only jobs whose processId (correlationRootId) is in LOCAL_PROCESS_IDS are returned
        assertThat(jobs).hasSize(2);
        assertThat(jobs).extracting(JobDetails::getId)
                .containsExactlyInAnyOrder("local-job-1", "local-job-2");
    }

    @Test
    @Transactional
    public void testFindReturnsLocalJob() {
        // Given: A local job exists
        insertProcessJob("local-job-3", "localProcess1", "1.0");
        entityManager.clear();

        // When: Finding the job by ID
        JobContext jobContext = getJobContext();
        JobDetails found = jobStore.find(jobContext, "local-job-3");

        // Then: Job is found (find doesn't filter by process ID)
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo("local-job-3");
        assertThat(found.getCorrelationId()).isEqualTo("localProcess1");
    }

    @Test
    @Transactional
    public void testFindReturnsRemoteJob() {
        // Given: A remote job exists
        insertProcessJob("remote-job-3", "remoteProcess3", "x");
        entityManager.clear();

        // When: Finding the remote job by ID
        JobContext jobContext = getJobContext();
        JobDetails found = jobStore.find(jobContext, "remote-job-3");

        // Then: Job is found (find doesn't filter by process ID)
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo("remote-job-3");
    }

    @Test
    @Transactional
    public void testLoadActiveJobsWithMixedJobs() {
        // Given: Multiple process jobs, where only local processId (correlationRootId) values belong to LOCAL_PROCESS_IDS
        insertProcessJob("local-job-4", "localProcess1", "1.0");
        insertProcessJob("local-job-5", "localProcess2", "2.0");
        insertProcessJob("local-job-6", "localProcess1", "1.0");
        insertProcessJob("local-job-7", "localProcess2", "2.1");
        insertProcessJob("remote-job-4", "remoteProcess4", "x");
        insertProcessJob("remote-job-5", "remoteProcess5", "y");
        insertProcessJob("remote-job-6", "remoteProcess6", "z");
        entityManager.clear();

        // When: Loading active jobs
        JobContext jobContext = getJobContext();
        OffsetDateTime maxWindow = OffsetDateTime.now().plus(Duration.ofHours(1));
        List<JobDetails> jobs = jobStore.loadActiveJobs(jobContext, maxWindow);

        // Then: Only the 3 jobs whose processId (correlationRootId) is in LOCAL_PROCESS_IDS are returned
        assertThat(jobs).hasSize(3);
        assertThat(jobs).extracting(JobDetails::getId)
                .containsExactlyInAnyOrder("local-job-4", "local-job-5", "local-job-6");
        assertThat(jobs).extracting(JobDetails::getCorrelationId)
                .allMatch(id -> id.equals("localProcess1") || id.equals("localProcess2"));
    }

    @Test
    @Transactional
    public void testLoadActiveJobsProcessInstance() {
        // Given: Process instance jobs created through JobDetailsHelper.newScheduledJobDetails
        // where correlationId is processInstanceId and correlationRootId is processId (stored in process_id column)
        insertProcessInstanceJob("local-instance-job-1", "processInstance-1", "localProcess1", "1.0");
        insertProcessInstanceJob("remote-instance-job-1", "processInstance-remote-1", "remoteProcess1", "x");
        entityManager.clear();

        // When: Loading active jobs through JPAJobStore
        JobContext jobContext = getJobContext();
        OffsetDateTime maxWindow = OffsetDateTime.now().plus(Duration.ofHours(1));
        List<JobDetails> jobs = jobStore.loadActiveJobs(jobContext, maxWindow);

        // Then: Process instance jobs are selected only when processId (correlationRootId) is in LOCAL_PROCESS_IDS
        assertThat(jobs).extracting(JobDetails::getId)
                .containsExactly("local-instance-job-1");
        assertThat(jobs).singleElement()
                .satisfies(job -> {
                    assertThat(job.getCorrelationId()).isEqualTo("processInstance-1");
                    assertThat(job.getProcessId()).isEqualTo("localProcess1");
                });
    }

    @Test
    @Transactional
    public void testLoadActiveJobsUserTask() {
        // Given: User task jobs created through JobDetailsHelper.newScheduledJobDetails
        // where correlationId is the job description id and correlationRootId is processId (stored in process_id column)
        insertUserTaskJob("local-usertask-job-1", "userTask-1", "localProcess2", "2.0");
        insertUserTaskJob("remote-usertask-job-1", "userTask-remote-1", "remoteProcess2", "1.1");
        entityManager.clear();

        // When: Loading active jobs through JPAJobStore
        JobContext jobContext = getJobContext();
        OffsetDateTime maxWindow = OffsetDateTime.now().plus(Duration.ofHours(1));
        List<JobDetails> jobs = jobStore.loadActiveJobs(jobContext, maxWindow);

        // Then: User task jobs are selected only when processId (correlationRootId) is in LOCAL_PROCESS_IDS
        assertThat(jobs).extracting(JobDetails::getId)
                .containsExactly("local-usertask-job-1");
        assertThat(jobs).singleElement()
                .satisfies(job -> {
                    assertThat(job.getCorrelationId()).isEqualTo("local-usertask-job-1");
                    assertThat(job.getProcessId()).isEqualTo("localProcess2");
                });
    }

    @Test
    @Transactional
    public void testShouldRunWithLocalJob() {
        // Given: A local job exists in SCHEDULED status
        insertProcessJob("local-shouldrun-1", "localProcess1", "1.0");
        entityManager.clear();

        // When: Calling shouldRun for the local job
        JobContext jobContext = getJobContext();
        boolean shouldRun = jobStore.shouldRun(jobContext, "local-shouldrun-1");

        // Then: shouldRun returns true (job was updated to RUNNING)
        assertThat(shouldRun).isTrue();

        // Verify the job status was updated to RUNNING
        JobDetailsEntity entity = entityManager.find(JobDetailsEntity.class, "local-shouldrun-1");
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo("RUNNING");
    }

    @Test
    @Transactional
    public void testShouldRunWithRemoteJob() {
        // Given: A remote job exists in SCHEDULED status
        insertProcessJob("remote-shouldrun-1", "remoteProcess1", "x");
        entityManager.clear();

        // When: Calling shouldRun for the remote job
        JobContext jobContext = getJobContext();
        boolean shouldRun = jobStore.shouldRun(jobContext, "remote-shouldrun-1");

        // Then: shouldRun returns false (job was filtered out by CTE, not updated)
        assertThat(shouldRun).isFalse();

        // Verify the job status remains SCHEDULED (was not updated)
        JobDetailsEntity entity = entityManager.find(JobDetailsEntity.class, "remote-shouldrun-1");
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo("SCHEDULED");
    }

    private JobContext getJobContext() {
        return new JPAJobContext() {

            @Override
            public EntityManager getEntityManager() {
                return entityManager;
            }

            @Override
            public Processes getProcesses() {
                return processes;
            }
        };
    }
}
