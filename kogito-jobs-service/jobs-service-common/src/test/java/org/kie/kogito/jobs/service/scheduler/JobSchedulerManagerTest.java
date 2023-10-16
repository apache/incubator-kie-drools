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
package org.kie.kogito.jobs.service.scheduler;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.management.MessagingChangeEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.Vertx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSchedulerManagerTest {

    public static final String JOB_ID = UUID.randomUUID().toString();

    @Mock
    TimerDelegateJobScheduler scheduler;

    @Mock
    ReactiveJobRepository repository;

    @Mock
    Vertx vertx;

    @Spy
    @InjectMocks
    private JobSchedulerManager tested;

    @Captor
    private ArgumentCaptor<Runnable> captorFirstExecution;

    @Captor
    private ArgumentCaptor<Consumer<Long>> captorPeriodic;

    private JobDetails scheduledJob;

    @BeforeEach
    void setUp() {
        this.scheduledJob = JobDetails
                .builder()
                .id(JOB_ID)
                .trigger(new PointInTimeTrigger(System.currentTimeMillis(), null, null))
                .build();

        lenient().when(repository.findByStatusBetweenDatesOrderByPriority(any(ZonedDateTime.class),
                any(ZonedDateTime.class),
                any(JobStatus.class),
                any(JobStatus.class)))
                .thenReturn(ReactiveStreams.of(scheduledJob));
        lenient().when(scheduler.scheduled(JOB_ID))
                .thenReturn(Optional.empty());
        lenient().when(scheduler.schedule(scheduledJob))
                .thenReturn(ReactiveStreams.of(scheduledJob).buildRs());
        tested.onMessagingStatusChange(new MessagingChangeEvent(true));
    }

    @Test
    void testOnStartup(@Mock StartupEvent event) {
        tested.onStartup(event);
        verify(vertx).runOnContext(captorFirstExecution.capture());
        verify(vertx).setPeriodic(eq(tested.loadJobIntervalInMinutes), captorPeriodic.capture());
    }

    @Test
    void testOnStartupInvalidInterval(@Mock StartupEvent event) {
        tested.schedulerChunkInMinutes = 10;
        tested.loadJobIntervalInMinutes = 20;

        tested.onStartup(event);

        assertThat(tested.loadJobIntervalInMinutes).isEqualTo(tested.schedulerChunkInMinutes);
    }

    @Test
    void testLoadJobDetailss() {
        tested.loadJobDetails();
        verify(scheduler).schedule(scheduledJob);
    }

    @Test
    void testLoadAlreadyJobDetailss() {
        when(scheduler.scheduled(JOB_ID)).thenReturn(Optional.of(DateUtil.now()));

        tested.loadJobDetails();
        verify(scheduler, never()).schedule(scheduledJob);
    }
}
