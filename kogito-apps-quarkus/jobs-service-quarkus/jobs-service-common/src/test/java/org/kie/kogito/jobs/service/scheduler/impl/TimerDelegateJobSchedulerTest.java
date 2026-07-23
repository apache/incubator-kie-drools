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
package org.kie.kogito.jobs.service.scheduler.impl;

import java.util.UUID;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.job.DelegateJob;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsContext;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobSchedulerTest;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Job;
import org.kie.kogito.timer.JobContext;
import org.kie.kogito.timer.Trigger;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import io.smallrye.mutiny.Multi;

import static mutiny.zero.flow.adapters.AdaptersToFlow.publisher;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimerDelegateJobSchedulerTest extends BaseTimerJobSchedulerTest {

    @Spy
    @InjectMocks
    private TimerDelegateJobScheduler tested;

    @Mock
    private VertxTimerServiceScheduler timer;

    @BeforeEach
    public void setUp() {
        super.setUp();
        ManageableJobHandle handle = new ManageableJobHandle(SCHEDULED_ID);
        handle.setScheduledTime(DateUtil.now());
        lenient().when(timer.scheduleJob(any(Job.class), any(JobContext.class), any(Trigger.class))).thenReturn(handle);
    }

    @Override
    public BaseTimerJobScheduler tested() {
        return tested;
    }

    @Test
    void testDoSchedule() {
        PublisherBuilder<ManageableJobHandle> schedule = tested.doSchedule(scheduledJob, scheduledJob.getTrigger());
        Multi.createFrom().publisher(publisher(schedule.buildRs())).subscribe().with(dummyCallback(), dummyCallback());
        verify(timer).scheduleJob(any(DelegateJob.class), any(JobDetailsContext.class), eq(scheduledJob.getTrigger()));
    }

    @Test
    void testDoCancel() {
        Publisher<ManageableJobHandle> cancel = tested.doCancel(JobDetails.builder().of(scheduledJob).scheduledId(SCHEDULED_ID).build());
        Multi.createFrom().publisher(publisher(cancel)).subscribe().with(dummyCallback(), dummyCallback());
        verify(timer).removeJob(any(ManageableJobHandle.class));
    }

    @Test
    void testDoCancelNullId() {
        Publisher<ManageableJobHandle> cancel =
                tested.doCancel(JobDetails.builder().of(scheduledJob).scheduledId(null).build());
        Multi.createFrom().publisher(publisher(cancel)).subscribe().with(dummyCallback(), dummyCallback());
        verify(timer, never()).removeJob(any(ManageableJobHandle.class));
    }

    private JobExecutionResponse getJobResponse() {
        return JobExecutionResponse.builder()
                .jobId(UUID.randomUUID().toString())
                .message("Processing job")
                .code("123")
                .now()
                .build();
    }
}
