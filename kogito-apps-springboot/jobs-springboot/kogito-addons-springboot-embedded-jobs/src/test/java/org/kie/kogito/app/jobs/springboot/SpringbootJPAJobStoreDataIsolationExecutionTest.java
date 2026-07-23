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
package org.kie.kogito.app.jobs.springboot;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.impl.JobDetailsHelper;
import org.kie.kogito.app.jobs.jpa.DataIsolationKeyDescriptor;
import org.kie.kogito.app.jobs.jpa.JPAJobContext;
import org.kie.kogito.app.jobs.jpa.JPAJobStore;
import org.kie.kogito.app.jobs.springboot.jpa.SpringbootJPAJobContext;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.process.Processes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for verifying data isolation in job execution when Processes bean is available.
 * Tests end-to-end that only jobs for locally deployed process IDs are actually executed.
 *
 * Uses @MockitoBean to provide a mock Processes bean that only affects this test class.
 */
@SpringBootTest(properties = { "kogito.persistence.data-isolation.enabled=true" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringbootJPAJobStoreDataIsolationExecutionTest {
    private static final Logger LOG = LoggerFactory.getLogger(SpringbootJPAJobStoreDataIsolationExecutionTest.class);

    @MockitoBean
    Processes processes;

    @Autowired
    JobsService jobsService;

    @Autowired
    TestJobSchedulerListener listener;

    @Autowired
    TestJobExecutor testJobExecutor;

    @Autowired
    TestExceptionHandler exceptionHandler;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PlatformTransactionManager transactionManager;

    private static final Set<DataIsolationKeyDescriptor> LOCAL_PROCESS_IDS = Set.of(
            new DataIsolationKeyDescriptor("localProcess1", "1.0"),
            new DataIsolationKeyDescriptor("localProcess2", "2.0"));

    @BeforeEach
    public void setup() throws Exception {
        // CRITICAL: Destroy the auto-started scheduler FIRST to prevent it from
        // loading jobs before we insert them all. The scheduler auto-starts with the app
        // and immediately calls loadActiveJobs() with long window (minimum is 1 minute).
        try {
            ((SpringbootJobsService) jobsService).destroy();

        } catch (Exception e) {
            // Log but continue - we need to proceed with test setup
            LOG.error("Warning: Error destroying scheduler in setup: {}", e.getMessage());
        }

        testJobExecutor.reset();
        exceptionHandler.reset();

        // Configure the mock Processes bean to return local process IDs
        when(processes.processIds()).thenReturn(LOCAL_PROCESS_IDS.stream()
                .map(DataIsolationKeyDescriptor::processId)
                .collect(Collectors.toSet()));

        // Mock processes() to return Process objects with id and version matching LOCAL_PROCESS_IDS
        java.util.Collection<org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> mockProcesses =
                LOCAL_PROCESS_IDS.stream()
                        .map(descriptor -> {
                            @SuppressWarnings("unchecked")
                            org.kie.kogito.process.Process<? extends org.kie.kogito.Model> mockProcess =
                                    (org.kie.kogito.process.Process<? extends org.kie.kogito.Model>) org.mockito.Mockito.mock(org.kie.kogito.process.Process.class);
                            when(mockProcess.id()).thenReturn(descriptor.processId());
                            when(mockProcess.version()).thenReturn(descriptor.processVersion());
                            return mockProcess;
                        })
                        .collect(Collectors.toList());
        when(processes.processes()).thenReturn(mockProcesses);

        when(processes.processById(org.mockito.ArgumentMatchers.anyString())).thenReturn(null);

        // Schedule all jobs BEFORE init() so they're picked up during loadActiveJobs()
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        JPAJobStore jobStore = new JPAJobStore();
        JPAJobContext context = new SpringbootJPAJobContext(null, entityManager);

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
                "remoteProcessInstanceId2",
                null,
                "remoteProcess2",
                "y",
                null,
                null,
                "remoteNodeInstanceId");

        // Directly persist jobs without using scheduleJob (avoids transaction synchronizations)
        JobDetails jobDetails1 = JobDetailsHelper.newScheduledJobDetails(localJob1);
        JobDetails jobDetails2 = JobDetailsHelper.newScheduledJobDetails(localJob2);
        JobDetails jobDetails3 = JobDetailsHelper.newScheduledJobDetails(localJob3);
        JobDetails jobDetails4 = JobDetailsHelper.newScheduledJobDetails(remoteJob1);
        JobDetails jobDetails5 = JobDetailsHelper.newScheduledJobDetails(remoteJob2);

        jobStore.persist(context, jobDetails1);
        jobStore.persist(context, jobDetails2);
        jobStore.persist(context, jobDetails3);
        jobStore.persist(context, jobDetails4);
        jobStore.persist(context, jobDetails5);

        transactionManager.commit(transaction);

        // Now init the scheduler - it will load all jobs via loadActiveJobs()
        ((SpringbootJobsService) jobsService).init();
    }

    @AfterEach
    public void cleanup() {
        try {
            ((SpringbootJobsService) jobsService).destroy();
        } catch (Exception e) {
            LOG.error("Warning: Error destroying scheduler in cleanup: {}", e.getMessage());
        }
    }

    @Test
    public void testDataIsolationJobExecution() throws Exception {
        // Expect only the 3 local jobs to be executed
        listener.setCount(3);

        // Wait for jobs to execute
        assertThat(listener.await(25, TimeUnit.SECONDS)).isTrue();

        // Verify exactly 3 local jobs were executed and remote jobs were not
        assertThat(listener.getExecutedJobIds())
                .hasSize(3)
                .containsExactlyInAnyOrder("local-job-1", "local-job-2", "local-job-3")
                .doesNotContain("remote-job-1", "remote-job-2");
    }
}
