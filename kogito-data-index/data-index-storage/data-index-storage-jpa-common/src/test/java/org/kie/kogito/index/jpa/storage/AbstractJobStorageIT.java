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
package org.kie.kogito.index.jpa.storage;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.model.JobEntity;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;

import jakarta.inject.Inject;

public abstract class AbstractJobStorageIT extends AbstractStorageIT<String, JobEntity, Job> {

    @Inject
    JobEntityStorage storage;

    public AbstractJobStorageIT() {
        super(Job.class);
    }

    @Override
    public Storage<String, Job> getStorage() {
        return storage;
    }

    @Test
    public void testJobEntity() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        Job job1 = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "EXPECTED", 0L);
        Job job2 = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "SCHEDULED", 1000L);
        testStorage(jobId, job1, job2);
    }

    @Test
    public void testJobEntityWithExceptionDetails() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        // Create job with ERROR status and exception details
        Job jobWithError = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "ERROR", 0L);
        jobWithError.setExceptionMessage("java.lang.RuntimeException");
        jobWithError.setExceptionDetails("Connection timeout after 30 seconds");

        // Store the job
        storage.put(jobId, jobWithError);

        // Retrieve and verify exception details are persisted
        Job retrievedJob = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedJob)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "ERROR")
                .hasFieldOrPropertyWithValue("exceptionMessage", "java.lang.RuntimeException")
                .hasFieldOrPropertyWithValue("exceptionDetails", "Connection timeout after 30 seconds");
    }

    @Test
    public void testJobEntityWithRetryAndExceptionDetails() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        // Create job with RETRY status and exception details
        Job jobWithRetry = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "RETRY", 1000L);
        jobWithRetry.setRetries(2);
        jobWithRetry.setExceptionMessage("java.net.ConnectException");
        jobWithRetry.setExceptionDetails("Connection refused: connect");

        // Store the job
        storage.put(jobId, jobWithRetry);

        // Retrieve and verify exception details are persisted
        Job retrievedJob = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedJob)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "RETRY")
                .hasFieldOrPropertyWithValue("retries", 2)
                .hasFieldOrPropertyWithValue("exceptionMessage", "java.net.ConnectException")
                .hasFieldOrPropertyWithValue("exceptionDetails", "Connection refused: connect");
    }

    @Test
    public void testJobEntityExceptionDetailsClearedOnSuccess() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        // First create job with ERROR status and exception details
        Job jobWithError = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "ERROR", 0L);
        jobWithError.setExceptionMessage("java.lang.RuntimeException");
        jobWithError.setExceptionDetails("Initial error");

        storage.put(jobId, jobWithError);

        // Update job to EXECUTED status with null exception details
        Job jobExecuted = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "EXECUTED", 0L);
        jobExecuted.setExceptionMessage(null);
        jobExecuted.setExceptionDetails(null);

        storage.put(jobId, jobExecuted);

        // Retrieve and verify exception details are cleared
        Job retrievedJob = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedJob)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "EXECUTED")
                .hasFieldOrPropertyWithValue("exceptionMessage", null)
                .hasFieldOrPropertyWithValue("exceptionDetails", null);
    }

    @Test
    public void testJobEntityRetryToExecutedTransition() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        // First create job with RETRY status and exception details
        Job jobWithRetry = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "RETRY", 0L);
        jobWithRetry.setRetries(2);
        jobWithRetry.setExceptionMessage("Temporary connection error");
        jobWithRetry.setExceptionDetails("java.net.ConnectException: Connection refused\n\tat java.net.Socket.connect(Socket.java:123)");

        storage.put(jobId, jobWithRetry);

        // Verify RETRY state with exception details
        Job retrievedRetry = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedRetry)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "RETRY")
                .hasFieldOrPropertyWithValue("retries", 2)
                .hasFieldOrPropertyWithValue("exceptionMessage", "Temporary connection error")
                .hasFieldOrPropertyWithValue("exceptionDetails", "java.net.ConnectException: Connection refused\n\tat java.net.Socket.connect(Socket.java:123)");

        // Update job to EXECUTED status after successful retry (exception details cleared)
        Job jobExecuted = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "EXECUTED", 0L);
        jobExecuted.setRetries(2); // Keep retry count for audit trail
        jobExecuted.setExceptionMessage(null); // Clear exception on success
        jobExecuted.setExceptionDetails(null); // Clear exception on success

        storage.put(jobId, jobExecuted);

        // Retrieve and verify exception details are cleared after successful retry
        Job retrievedExecuted = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedExecuted)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "EXECUTED")
                .hasFieldOrPropertyWithValue("retries", 2) // Retry count preserved
                .hasFieldOrPropertyWithValue("exceptionMessage", null) // Exception cleared
                .hasFieldOrPropertyWithValue("exceptionDetails", null); // Exception cleared
    }

    @Test
    public void testJobEntityWithLongExceptionDetails() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        // Create job with long exception details (simulating stack trace)
        String longStackTrace = "java.lang.RuntimeException: Error processing request\n" +
                "\tat com.example.Service.process(Service.java:123)\n" +
                "\tat com.example.Controller.handle(Controller.java:45)\n" +
                "\tat org.springframework.web.method.support.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:219)\n" +
                "Caused by: java.sql.SQLException: Connection timeout\n" +
                "\tat com.example.Database.query(Database.java:89)\n" +
                "\t... 15 more";

        Job jobWithLongError = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "ERROR", 0L);
        jobWithLongError.setExceptionMessage("java.lang.RuntimeException");
        jobWithLongError.setExceptionDetails(longStackTrace);

        // Store the job
        storage.put(jobId, jobWithLongError);

        // Retrieve and verify long exception details are persisted
        Job retrievedJob = storage.get(jobId);
        org.assertj.core.api.Assertions.assertThat(retrievedJob)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", jobId)
                .hasFieldOrPropertyWithValue("status", "ERROR")
                .hasFieldOrPropertyWithValue("exceptionMessage", "java.lang.RuntimeException");

        org.assertj.core.api.Assertions.assertThat(retrievedJob.getExceptionDetails())
                .isNotNull()
                .contains("java.lang.RuntimeException: Error processing request")
                .contains("Caused by: java.sql.SQLException: Connection timeout");
    }
}
