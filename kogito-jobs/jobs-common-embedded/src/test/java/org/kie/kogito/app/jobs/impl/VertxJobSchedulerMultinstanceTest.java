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
import java.util.ArrayList;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.api.JobScheduler;
import org.kie.kogito.app.jobs.api.JobSchedulerBuilder;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class VertxJobSchedulerMultinstanceTest {

    @Test
    public void testSyncJobNew() throws Exception {
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
                .build();
        jobScheduler.init();
        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        memoryJobStore.persist(jobContextFactory.newContext(), jobDetails);
        latchExecutionJobSchedulerListener.waitForExecution();
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testSyncJobChangedWithinWindow() throws Exception {
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
                .build();
        jobScheduler.init();
        JobDescription jobDescription = new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(10)));
        jobScheduler.schedule(jobDescription);

        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1))));
        memoryJobStore.update(jobContextFactory.newContext(), jobDetails);
        latchExecutionJobSchedulerListener.waitForExecution(2000L);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isTrue();
        jobScheduler.close();
    }

    @Test
    public void testSyncJobChangedOutWindow() throws Exception {
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
                .build();
        jobScheduler.init();
        JobDescription jobDescription = new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(5)));
        jobScheduler.schedule(jobDescription);

        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofMinutes(100))));
        memoryJobStore.update(jobContextFactory.newContext(), jobDetails);
        latchExecutionJobSchedulerListener.waitForExecution(10000L);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNotNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isFalse();
        jobScheduler.close();
    }

    @Test
    public void testSyncJobRemoved() throws Exception {
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
                .build();
        jobScheduler.init();
        JobDescription jobDescription = new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(2)));
        jobScheduler.schedule(jobDescription);

        memoryJobStore.remove(jobContextFactory.newContext(), jobId);
        latchExecutionJobSchedulerListener.waitForExecution(5000L);
        assertThat(memoryJobStore.find(jobContextFactory.newContext(), jobId)).isNull();
        assertThat(latchExecutionJobSchedulerListener.isExecuted()).isFalse();
        jobScheduler.close();
    }

    // this test logic in multiinstance is working properly and it is only executed once.
    @Test
    public void testMultipleInstanceConcurrentAccess() throws Exception {
        final Integer TOTAL_COUNT = 5;
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();

        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener(TOTAL_COUNT);
        List<JobScheduler> instances = new ArrayList<>();
        for (int i = 0; i < TOTAL_COUNT; i++) {
            JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder().withJobExecutors(latchJobExecutor)
                    .withJobEventAdapters(new TestJobDetailsEventAdapter())
                    .withEventPublishers(new TestEventPublisher())
                    .withJobContextFactory(jobContextFactory)
                    .withJobStore(memoryJobStore)
                    .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                    .withRefreshJobsInterval(1000L) // every second
                    .build();
            jobScheduler.init();
            instances.add(jobScheduler);

        }
        JobDescription jobDescription = new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1)));

        memoryJobStore.persist(jobContextFactory.newContext(), JobDetailsHelper.newScheduledJobDetails(jobDescription));

        Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> memoryJobStore.find(jobContextFactory.newContext(), jobId) == null);
        assertThat(latchExecutionJobSchedulerListener.getCount()).isEqualTo(1);
        instances.forEach(JobScheduler::close);
    }

    // this test logic in multiinstance is working properly and it is only executed once if the registering node goes down
    @Test
    public void testMultipleInstanceRegisteredDown() throws Exception {
        final Integer TOTAL_COUNT = 5;
        final String jobId = "1";
        JobStore memoryJobStore = new MemoryJobStore();
        JobContextFactory jobContextFactory = new MemoryJobContextFactory();
        TestJobExecutor latchJobExecutor = new TestJobExecutor();

        LatchExecutionJobSchedulerListener latchExecutionJobSchedulerListener = new LatchExecutionJobSchedulerListener(TOTAL_COUNT);
        List<JobScheduler> instances = new ArrayList<>();
        for (int i = 0; i < TOTAL_COUNT; i++) {
            JobScheduler jobScheduler = JobSchedulerBuilder.newJobSchedulerBuilder().withJobExecutors(latchJobExecutor)
                    .withJobEventAdapters(new TestJobDetailsEventAdapter())
                    .withEventPublishers(new TestEventPublisher())
                    .withJobContextFactory(jobContextFactory)
                    .withJobStore(memoryJobStore)
                    .withJobSchedulerListeners(latchExecutionJobSchedulerListener)
                    .withRefreshJobsInterval(1000L) // every second
                    .build();
            jobScheduler.init();
            instances.add(jobScheduler);

        }
        JobScheduler scheduler = instances.get(0);
        JobDescription jobDescription = new TestJobDescription(jobId, ZonedDateTime.now().plus(Duration.ofSeconds(1)));
        scheduler.schedule(jobDescription);
        scheduler.close();
        instances.remove(scheduler);

        // now we wait till is executed only once
        Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> memoryJobStore.find(jobContextFactory.newContext(), jobId) == null);
        assertThat(latchExecutionJobSchedulerListener.getCount()).isEqualTo(1);
        instances.forEach(JobScheduler::close);
    }

}
