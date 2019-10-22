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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class VertxJobScheduler extends BaseTimerJobScheduler {

    private Logger logger = LoggerFactory.getLogger(VertxJobScheduler.class);

    @Inject
    private Vertx vertx;

    @Override
    public Publisher<ScheduledJob> doSchedule(Duration delay, Job job) {
        logger.debug("Job Scheduling {}", job);
        return ReactiveStreams
                .of(setTimer(delay, job))
                .map(id -> new ScheduledJob(job, String.valueOf(id)))
                .buildRs();
    }

    private long setTimer(Duration delay, Job job) {
        return vertx.setTimer(delay.toMillis(), scheduledId -> execute(job));
    }

    @Override
    public Publisher<Boolean> doCancel(ScheduledJob scheduledJob) {
        return ReactiveStreams
                .of(scheduledJob)
                .map(ScheduledJob::getScheduledId)
                .map(Long::valueOf)
                .map(vertx::cancelTimer)
                .buildRs();
    }
}