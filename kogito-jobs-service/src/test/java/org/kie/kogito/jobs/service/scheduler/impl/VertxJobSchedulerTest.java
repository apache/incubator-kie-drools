/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.scheduler.impl;

import java.time.Duration;
import java.util.function.Consumer;

import io.reactivex.Flowable;
import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobSchedulerTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VertxJobSchedulerTest extends BaseTimerJobSchedulerTest {

    @Spy
    @InjectMocks
    private VertxJobScheduler tested;

    @Mock
    private Vertx vertx;

    @Captor
    private ArgumentCaptor<Consumer<Long>> handlerCaptor;

    @Captor
    private ArgumentCaptor<Long> timeCaptor;

    @BeforeEach
    public void setUp() {
        super.setUp();
        lenient().when(vertx.setTimer(anyLong(), any())).thenReturn(Long.valueOf(SCHEDULED_ID));
        lenient().when(vertx.setPeriodic(anyLong(), any())).thenReturn(Long.valueOf(SCHEDULED_ID));
    }

    @Override
    public BaseTimerJobScheduler tested() {
        return tested;
    }

    @Test
    void testDoSchedule() {
        PublisherBuilder<String> schedule = tested.doSchedule(Duration.ofMillis(1), job);
        verify(vertx, never()).setTimer(timeCaptor.capture(), handlerCaptor.capture());
        Flowable.fromPublisher(schedule.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(vertx).setTimer(timeCaptor.capture(), handlerCaptor.capture());
        assertJobSchedule();
    }

    private void assertJobSchedule() {
        assertThat(timeCaptor.getValue()).isEqualTo(1);
        handlerCaptor.getValue().accept(10l);
        verify(jobRepository).get(JOB_ID);
        verify(jobExecutor).execute(scheduled);
    }

    @Test
    void testDoPeriodicSchedule() {
        PublisherBuilder<String> periodicSchedule = tested.doPeriodicSchedule(Duration.ofMillis(1), job);
        verify(vertx, never()).setPeriodic(timeCaptor.capture(), handlerCaptor.capture());
        Flowable.fromPublisher(periodicSchedule.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(vertx).setPeriodic(timeCaptor.capture(), handlerCaptor.capture());
        assertJobSchedule();
    }

    @Test
    void testDoCancel() {
        Publisher<Boolean> cancel = tested.doCancel(ScheduledJob.builder().job(job).scheduledId(SCHEDULED_ID).build());
        verify(vertx, never()).cancelTimer(Long.valueOf(SCHEDULED_ID));
        Flowable.fromPublisher(cancel).subscribe(dummyCallback(), dummyCallback());
        verify(vertx).cancelTimer(Long.valueOf(SCHEDULED_ID));
    }

    @Test
    void testDoCancelNullId() {
        Publisher<Boolean> cancel = tested.doCancel(ScheduledJob.builder().job(job).scheduledId(null).build());
        Flowable.fromPublisher(cancel).subscribe(dummyCallback(), dummyCallback());
        verify(vertx, never()).cancelTimer(anyLong());
    }
}