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
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.api.JobScheduler;
import org.kie.kogito.app.jobs.api.JobSchedulerBuilder;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import static org.assertj.core.api.Assertions.assertThat;

public class VertxJobSchedulerTest {

    @Test
    public void testBasicExactTimeFlow() throws Exception {
        final String jobId = "1";
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();
        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(latchJobExecutor.getJobsExecuted()).hasSize(1);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testBasicPeriodicLimitedTimeFlow() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener(3);
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();
        ExpirationTime expirationTime = DurationExpirationTime.repeat(0, 1000L, 3);
        jobScheduler.schedule(new TestJobDescription("1", expirationTime));

        latchExecutionJobSchedulerListener.waitForExecution();
        // there should not be any job left in database
        assertThat(latchJobExecutor.getJobsExecuted()).hasSize(3);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testBasicPeriodicUnlimitedTimeFlow() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener(6);
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();
        ExpirationTime expirationTime = DurationExpirationTime.repeat(0, 1000L, SimpleTimerTrigger.INDEFINITELY);
        jobScheduler.schedule(new TestJobDescription(jobId, expirationTime));

        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(latchJobExecutor.getJobsExecuted()).hasSize(6);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testCancelJob() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder().withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(10))));
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();
        jobScheduler.cancel(jobId);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isFalse();
        jobScheduler.close();
    }

    @Test
    public void testRescheduleJob() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder().withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(10))));
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();
        jobScheduler.reschedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution(2000L);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testRetryTillErrorJob() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestFailureJobExecutor latchJobExecutor = new TestFailureJobExecutor(3);
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withRetryInterval(1000L)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();
        latchExecutionJobSchedulerListener.waitForExecution(5000L);
        JobDetails jobDetails = memoryJobStore.find(jobContextFactory.newContext(), jobId);
        assertThat(jobDetails).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testRetryBeforeErrorJob() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestFailureJobExecutor latchJobExecutor = new TestFailureJobExecutor(2);
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        TestEventPublisher eventPublisher = new TestEventPublisher();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withRetryInterval(1000L)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(eventPublisher)
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testExactTime() throws Exception {
        final String jobId = "1";
        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .withRefreshJobsInterval(100000L)
                .build();
        jobScheduler.init();
        ExpirationTime expirationTime = ExactExpirationTime.of(ZonedDateTime.now().plus(1, ChronoUnit.MILLIS));
        jobScheduler.schedule(new TestJobDescription(jobId, expirationTime));
        latchExecutionJobSchedulerListener.waitForExecution(1000L);
        assertThat(latchJobExecutor.getJobsExecuted()).hasSize(1);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();

    }

    @Test
    public void testNumberOfRetries() throws Exception {
        final int NUMBER_OF_FAILURES = 4; // first execution + number of retries
        final int NUMBER_OF_RETRIES = NUMBER_OF_FAILURES - 1;

        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestFailureJobExecutor latchJobExecutor = new TestFailureJobExecutor(NUMBER_OF_FAILURES);
        LatchFailureJobSchedulerListener latchExecutionJobSchedulerListener = new LatchFailureJobSchedulerListener(NUMBER_OF_FAILURES);
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withMaxNumberOfRetries(NUMBER_OF_RETRIES)
                .withJobExecutors(latchJobExecutor)
                .withRetryInterval(1000L)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();

        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull().extracting(JobDetails::getStatus).isEqualTo(JobStatus.ERROR);

        jobScheduler.close();
    }

    @Test
    public void testEventPublishedOnErrorWithNoRetries() throws Exception {

        int EXPECTED_EVENTS = 3; // SCHEDULED, RUNNING, ERROR

        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestFailureJobExecutor latchJobExecutor = new TestFailureJobExecutor(1);
        TestEventPublisher eventPublisher = new TestEventPublisher();
        LatchFailureJobSchedulerListener latchExecutionJobSchedulerListener = new LatchFailureJobSchedulerListener(1);
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withMaxNumberOfRetries(0)
                .withJobExecutors(latchJobExecutor)
                .withRetryInterval(1000L)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(eventPublisher)
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();

        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(eventPublisher.getPublishedEventsCount()).isEqualTo(EXPECTED_EVENTS);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull().extracting(JobDetails::getStatus).isEqualTo(JobStatus.ERROR);

        jobScheduler.close();
    }

    @Test
    public void testEventPublishedOnErrorWithRetry() throws Exception {
        final int NUMBER_OF_FAILURES = 2; // first execution + number of retries
        final int NUMBER_OF_RETRIES = NUMBER_OF_FAILURES - 1;
        int EXPECTED_EVENTS = 6; // SCHEDULED, RUNNING, 2xRETRY, RUNNING, ERROR

        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestFailureJobExecutor latchJobExecutor = new TestFailureJobExecutor(NUMBER_OF_FAILURES);
        TestEventPublisher eventPublisher = new TestEventPublisher();
        LatchFailureJobSchedulerListener latchExecutionJobSchedulerListener = new LatchFailureJobSchedulerListener(NUMBER_OF_FAILURES);
        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withMaxNumberOfRetries(NUMBER_OF_RETRIES)
                .withJobExecutors(latchJobExecutor)
                .withRetryInterval(1000L)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(eventPublisher)
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();

        jobScheduler.init();

        jobScheduler.schedule(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(eventPublisher.getPublishedEventsCount()).isEqualTo(EXPECTED_EVENTS);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull().extracting(JobDetails::getStatus).isEqualTo(JobStatus.ERROR);

        jobScheduler.close();
    }

    @Test
    public void testBasicOverdueTime() throws Exception {
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();

        TestJobDescription testDescription = new TestJobDescription(jobId, ZonedDateTime.now().minus(Duration.ofSeconds(1)));
        memoryJobStore.persist(jobContextFactory.newContext(), JobDetailsHelper.newScheduledJobDetails(testDescription));

        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();

        JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder()
                .withJobExecutors(latchJobExecutor)
                .withJobEventAdapters(new TestJobDetailsEventAdapter())
                .withEventPublishers(new TestEventPublisher())
                .withJobContextFactory(jobContextFactory)
                .withJobStore(memoryJobStore)
                .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                .withJobDescriptorMergers(new TestJobDescriptionMerger())
                .build();
        jobScheduler.init();
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(latchJobExecutor.getJobsExecuted()).hasSize(1);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

}
