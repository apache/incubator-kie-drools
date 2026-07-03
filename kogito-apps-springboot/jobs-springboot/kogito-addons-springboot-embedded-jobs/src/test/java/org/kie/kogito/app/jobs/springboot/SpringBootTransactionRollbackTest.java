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

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpringBootTransactionRollbackTest {

    @Autowired
    JobsService jobsService;

    @Autowired
    TestJobSchedulerListener listener;

    @Autowired
    TestJobExecutor testJobExecutor;

    @Autowired
    TestExceptionHandler exceptionHandler;

    @Autowired
    SpringBootTransactionRollbackMarker transactionRollbackMarker;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MockDataRepository mockDataRepository;

    @BeforeEach
    public void init() {
        testJobExecutor.reset();
        exceptionHandler.reset();
        mockDataRepository.reset();
    }

    @Test
    public void testTransactionRollbackMarkerInjected() {
        assertThat(transactionRollbackMarker).isNotNull();
        assertThat(transactionRollbackMarker.isTransactionActive()).isFalse();
    }

    @Test
    public void testTransactionRollbackOnJobFailure() throws Exception {
        // Configure the test executor to fail 4 times (initial + 3 retries)
        testJobExecutor.setNumberOfFailures(4);

        ProcessInstanceJobDescription jobDescription = ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder()
                .id("rollback-test-job")
                .timerId("-1")
                .expirationTime(ExactExpirationTime.of(Instant.now().plus(Duration.ofSeconds(2)).atZone(ZoneId.of("UTC"))))
                .priority(5)
                .processInstanceId("test-process-instance")
                .rootProcessInstanceId(null)
                .processId("test-process")
                .processVersion(null)
                .rootProcessId("test-node-instance").build();

        listener.setCount(4);
        jobsService.scheduleJob(jobDescription);

        // Wait for all retries to complete and error handler to be invoked
        Awaitility.await()
                .atMost(Duration.ofSeconds(10L))
                .untilAsserted(() -> {
                    assertThat(exceptionHandler.isError())
                            .as("Error handler should be invoked after max retries")
                            .isTrue();
                });

        assertThat(listener.await(1, java.util.concurrent.TimeUnit.SECONDS))
                .as("All job executions should have been attempted")
                .isTrue();

        assertThat(mockDataRepository.getTotalPersistenceAttempts())
                .as("Should have 4 persistence attempts (initial + 3 retries)")
                .isEqualTo(4);

        assertThat(mockDataRepository.getRolledBackAttempts())
                .as("All failed attempts should have been rolled back by Spring")
                .isEqualTo(4);

        assertThat(mockDataRepository.getSuccessfulCommits())
                .as("No data should be committed when all attempts fail")
                .isEqualTo(0);

        assertThat(mockDataRepository.getCommittedCount("rollback-test-job"))
                .as("No duplicate data should be persisted due to automatic rollback")
                .isEqualTo(0);
    }
}
