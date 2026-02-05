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
package org.kie.kogito.app.jobs.impl;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.api.JobScheduler;
import org.kie.kogito.app.jobs.api.JobSchedulerBuilder;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.jobs.service.model.JobDetails;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for race condition between job retry scheduling and cancellation.
 * <p>
 * This test class verifies the fix for the issue where concurrent execution of:
 * 1. Exception handler scheduling a RETRY job
 * 2. Boundary timer canceling the job
 * <p>
 * Would result in the RETRY timer being removed immediately after creation,
 * causing retries to only execute after DB sync operations (~60 seconds apart).
 */
public class VertxJobSchedulerRaceConditionTest {

    /**
     * Test that legitimate cancellations still work correctly.
     * <p>
     * This verifies that the fix doesn't break normal cancel operations
     * where the cancel has the same or higher retry count.
     */
    @Test
    public void testLegitimateCancelStillWorks() throws Exception {
        final String jobId = "legitimate-cancel-test";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        TestJobExecutor jobExecutor = new TestJobExecutor();
        LatchExecutionJobSchedulerListener executionListener = new LatchExecutionJobSchedulerListener();

        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(jobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(executionListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();

        jobScheduler.init();

        // Schedule job with long delay
        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(10))));

        // Verify job is scheduled
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();

        // Cancel it
        jobScheduler.cancel(jobId);

        // Verify job is removed
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();

        // Wait a bit to ensure it doesn't execute
        Thread.sleep(500);
        assertThat(executionListener.isExecuted()).isFalse();
        assertThat(jobExecutor.getJobsExecuted()).isEmpty();

        jobScheduler.close();
    }

    /**
     * Test that reproduces the race condition between retry scheduling and cancellation.
     * <p>
     * Race condition scenario:
     * 1. Job fails and schedules a retry (retries=1)
     * 2. Concurrently, cancel is called and the JobStore returns stale job data (retries=0)
     * 3. WITHOUT FIX: Both use the same map key (jobId), so cancel removes the retry timer
     * 4. WITH FIX: Different map keys (jobId-0 vs jobId-1), so cancel doesn't affect retry timer
     * <p>
     * This test uses a custom JobStore that returns stale job data during cancel to simulate
     * the race condition where cancel gets old job details while retry has been scheduled.
     */
    @Test
    public void testRaceConditionBetweenRetryAndCancel() throws Exception {
        final String jobId = "race-condition-test";
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        CountDownLatch firstFailureLatch = new CountDownLatch(1);
        AtomicBoolean returnStaleData = new AtomicBoolean(false);

        // Custom JobStore that can return stale job data
        JobStore customJobStore = new MemoryJobStore() {
            private JobDetails staleJobDetails = null;

            @Override
            public JobDetails find(JobContext context, String id) {
                JobDetails current = super.find(context, id);

                // Capture the initial job details (retries=0)
                if (current != null && staleJobDetails == null && current.getRetries() == 0) {
                    staleJobDetails = current;
                }

                // Return stale data when flag is set (simulates race condition)
                if (returnStaleData.get() && staleJobDetails != null) {
                    return staleJobDetails;
                }

                return current;
            }
        };

        // Executor that fails 3 times, then succeeds
        TestFailureJobExecutor jobExecutor = new TestFailureJobExecutor(3) {
            @Override
            public void execute(JobDetails jobDescription) {
                if (jobDescription.getRetries() == 0) {
                    firstFailureLatch.countDown();
                }
                super.execute(jobDescription);
            }
        };

        LatchFailureJobSchedulerListener failureListener = new LatchFailureJobSchedulerListener(3);
        LatchExecutionJobSchedulerListener executionListener = new LatchExecutionJobSchedulerListener();

        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(jobExecutor)
                .withRetryInterval(200L)
                .withMaxNumberOfRetries(5)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(customJobStore)
                .withJobSchedulerListeners(failureListener, executionListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();

        jobScheduler.init();

        // Schedule job that will fail
        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofMillis(50))));

        // Wait for first failure
        firstFailureLatch.await(2000L, TimeUnit.MILLISECONDS);

        // Give time for retry to be scheduled (retries=1)
        Thread.sleep(100);

        // Now enable stale data return and cancel
        // This simulates the race: cancel gets stale job (retries=0) while retry (retries=1) is scheduled
        returnStaleData.set(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                // WITHOUT FIX: This removes the retry timer because both use key "jobId"
                // WITH FIX: This only removes timer with key "jobId-0", not "jobId-1"
                jobScheduler.cancel(jobId);
            } catch (Exception e) {
                // Ignore
            }
        });

        // Wait for all retries and final execution
        // WITHOUT FIX: This will timeout - retry timer was removed
        // WITH FIX: All 3 retries execute and job succeeds on 4th attempt
        failureListener.waitForExecution(3000L);
        executionListener.waitForExecution(2000L);

        executor.shutdown();
        executor.awaitTermination(1000L, TimeUnit.MILLISECONDS);

        // WITHOUT FIX: This assertion FAILS - retry timer was removed by cancel
        // WITH FIX: This assertion PASSES - retry timer survived because of different map key
        assertThat(executionListener.isExecuted())
                .as("Job should execute successfully after retry despite concurrent cancel with stale data")
                .isTrue();

        jobScheduler.close();
    }

    /**
     * Test that syncWithJobStores correctly picks up jobs that initially fall outside maxWindowLoad.
     * <p>
     * Scenario:
     * 1. Job is scheduled far in the future (beyond maxWindowLoad window)
     * 2. Initial sync doesn't load it into memory
     * 3. Time passes, next sync occurs when job is within the window
     * 4. Job should be picked up and executed at the correct time
     * <p>
     * This verifies that the map key changes (jobId-retries) don't break the sync mechanism.
     */
    @Test
    public void testSyncPicksUpJobOutsideInitialWindow() throws Exception {
        final String jobId = "sync-outside-window-test";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        TestJobExecutor jobExecutor = new TestJobExecutor();
        LatchExecutionJobSchedulerListener executionListener = new LatchExecutionJobSchedulerListener();

        // Set a short maxWindowLoad (1000ms) - jobs beyond this won't be loaded initially
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(jobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(executionListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .withMaxRefreshJobsIntervalWindow(1000L) // 1-second window
                .build();

        jobScheduler.init();

        // Schedule job 2000ms in the future (outside initial 1000ms window)
        ZonedDateTime futureTime = ZonedDateTime.now().plus(Duration.ofMillis(2000));
        jobScheduler.schedule(new TestJobDescription(jobId, futureTime));

        // Verify job is in store
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();

        // Wait for job to be picked up by sync and executed
        // After ~1000ms, the job should be within the window and picked up by sync
        executionListener.waitForExecution(4000L);

        assertThat(executionListener.isExecuted())
                .as("Job scheduled outside initial window should be picked up by sync and executed")
                .isTrue();
        assertThat(jobExecutor.getJobsExecuted()).contains(jobId);

        jobScheduler.close();
    }

    /**
     * Test that retry attempts scheduled beyond the next sync window are handled correctly.
     * <p>
     * Scenario:
     * 1. Job fails and schedules a retry far in the future (beyond maxWindowLoad)
     * 2. The retry timer should be removed from current window
     * 3. Next sync should pick it up when it falls within the window
     * 4. Retry should execute at the correct time
     * <p>
     * This verifies that retries with different retry counts (different map keys) are
     * properly managed by the sync mechanism.
     */
    @Test
    public void testRetryBeyondSyncWindowIsPickedUpLater() throws Exception {
        final String jobId = "retry-beyond-sync-test";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        CountDownLatch firstFailureLatch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        // Executor that fails once, then succeeds
        TestFailureJobExecutor jobExecutor = new TestFailureJobExecutor(1) {
            @Override
            public void execute(JobDetails jobDescription) {
                executionCount.incrementAndGet();
                if (jobDescription.getRetries() == 0) {
                    firstFailureLatch.countDown();
                }
                super.execute(jobDescription);
            }
        };

        LatchFailureJobSchedulerListener failureListener = new LatchFailureJobSchedulerListener(1);
        LatchExecutionJobSchedulerListener executionListener = new LatchExecutionJobSchedulerListener();

        // Set short window (1000ms) and long retry interval (2000ms - beyond window)
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(jobExecutor)
                .withRetryInterval(2000L) // retry after 2000ms
                .withMaxNumberOfRetries(2)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(failureListener, executionListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .withMaxRefreshJobsIntervalWindow(1000L) // 1 second window
                .build();

        jobScheduler.init();

        // Schedule job that will fail immediately
        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofMillis(100))));

        // Wait for first failure
        firstFailureLatch.await(3000L, TimeUnit.MILLISECONDS);
        assertThat(executionCount.get()).isEqualTo(1);

        // Wait for failure to be processed
        failureListener.waitForExecution(2000L);

        // At this point, retry is scheduled for ~2000ms in future (beyond 1000ms window)
        // The retry timer should be removed from memory by sync
        // Wait for sync to occur and then for retry to be picked up and executed
        Thread.sleep(500); // Let sync remove it from current window

        // Now wait for retry to be picked up by sync and executed
        executionListener.waitForExecution(4000L);

        assertThat(executionListener.isExecuted())
                .as("Retry scheduled beyond sync window should be picked up by later sync and executed")
                .isTrue();
        assertThat(executionCount.get())
                .as("Job should execute twice: initial failure + successful retry")
                .isEqualTo(2);

        jobScheduler.close();
    }

    /**
     * Test that multiple retries with different retry counts are managed correctly by sync.
     * <p>
     * Scenario:
     * 1. Job fails multiple times, creating retries with different retry counts
     * 2. Each retry has a different map key (jobId-0, jobId-1, jobId-2)
     * 3. Sync should correctly manage these different retry attempts
     * 4. All retries should execute in order
     * <p>
     * This is a comprehensive test of the map key changes with the sync mechanism.
     */
    @Test
    public void testMultipleRetriesWithSyncMechanism() throws Exception {
        final String jobId = "multiple-retries-sync-test";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        AtomicInteger executionCount = new AtomicInteger(0);

        // Executor that fails 3 times, then succeeds
        TestFailureJobExecutor jobExecutor = new TestFailureJobExecutor(3) {
            @Override
            public void execute(JobDetails jobDescription) {
                executionCount.incrementAndGet();
                super.execute(jobDescription);
            }
        };

        LatchFailureJobSchedulerListener failureListener = new LatchFailureJobSchedulerListener(3);
        LatchExecutionJobSchedulerListener executionListener = new LatchExecutionJobSchedulerListener();

        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(jobExecutor)
                .withRetryInterval(500L) // retry every 500ms
                .withMaxNumberOfRetries(5)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(failureListener, executionListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .withMaxRefreshJobsIntervalWindow(3000L) // 3 second window
                .build();

        jobScheduler.init();

        // Schedule job that will fail 3 times
        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofMillis(100))));

        // Wait for all failures and final success
        failureListener.waitForExecution(5000L);
        executionListener.waitForExecution(4000L);

        assertThat(executionListener.isExecuted())
                .as("Job should eventually succeed after retries")
                .isTrue();
        assertThat(executionCount.get())
                .as("Job should execute 4 times: 3 failures + 1 success")
                .isEqualTo(4);

        // Verify job is removed from store after successful execution
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId))
                .as("Job should be removed from store after successful execution")
                .isNull();

        jobScheduler.close();
    }
}
