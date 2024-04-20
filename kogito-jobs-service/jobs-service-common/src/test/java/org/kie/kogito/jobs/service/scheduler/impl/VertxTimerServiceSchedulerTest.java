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

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.executor.JobExecutorResolver;
import org.kie.kogito.jobs.service.job.DelegateJob;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsContext;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Job;
import org.kie.kogito.timer.JobContext;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VertxTimerServiceSchedulerTest {

    private VertxTimerServiceScheduler tested;
    private Job job;
    private JobContext context;
    private Trigger trigger;
    private JobDetails jobDetails;

    @Mock
    private JobExecutorResolver jobExecutorResolver;

    @Mock
    private JobExecutor jobExecutor;

    @Mock
    private ReactiveJobScheduler reactiveJobScheduler;

    @Captor
    private ArgumentCaptor<JobDetails> jobCaptor;

    @Captor
    private ArgumentCaptor<Long> timeCaptor;

    @Spy
    private Vertx vertx = Vertx.vertx();

    @BeforeEach
    public void setUp() {
        tested = new VertxTimerServiceScheduler(vertx);
    }

    @Test
    void testScheduleJob() {
        ZonedDateTime time = DateUtil.now().plusSeconds(1);
        final ManageableJobHandle handle = schedule(time);
        doReturn(jobExecutor).when(jobExecutorResolver).get(any());
        JobExecutionResponse response = new JobExecutionResponse();
        Uni<JobExecutionResponse> result = Uni.createFrom().item(response);
        PublisherBuilder<JobDetails> executionSuccessPublisherBuilder = ReactiveStreams.of(jobDetails);
        doReturn(executionSuccessPublisherBuilder).when(reactiveJobScheduler).handleJobExecutionSuccess(response);
        doReturn(result).when(jobExecutor).execute(jobDetails);
        verify(vertx).setTimer(timeCaptor.capture(), any());
        assertThat(timeCaptor.getValue()).isGreaterThanOrEqualTo(time.toInstant().minusMillis(System.currentTimeMillis()).toEpochMilli());
        given().await()
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(jobExecutorResolver).get(jobCaptor.capture()));
        assertThat(jobCaptor.getValue()).isEqualTo(jobDetails);
        assertThat(handle.isCancel()).isFalse();
        assertThat(handle.getScheduledTime()).isNotNull();
    }

    @Test
    void testRemoveScheduleJob() {
        final ManageableJobHandle handle = schedule(DateUtil.now().plusHours(1));
        verify(vertx).setTimer(timeCaptor.capture(), any());
        given().await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(handle.isCancel()).isFalse();
                    assertThat(handle.getScheduledTime()).isNotNull();
                });
        assertThat(tested.removeJob(handle)).isTrue();
    }

    private ManageableJobHandle schedule(ZonedDateTime time) {
        final long timestamp = time.toInstant().toEpochMilli();
        trigger = new PointInTimeTrigger(timestamp, null, null);
        jobDetails = JobDetails.builder().build();
        context = new JobDetailsContext(jobDetails);
        job = new DelegateJob(jobExecutorResolver, reactiveJobScheduler);
        return tested.scheduleJob(job, context, trigger);
    }

    @Test
    void testLifeCycle() {
        assertThat(System.currentTimeMillis())
                .isLessThanOrEqualTo(tested.getCurrentTime())
                .isLessThanOrEqualTo(System.currentTimeMillis());

        assertThat(tested.getTimeToNextJob()).isZero();

        assertThat(tested.getTimerJobInstances(0)).isEmpty();

        tested.reset();
        verify(vertx, never()).close();

        tested.shutdown();
        verify(vertx).close();
    }
}
