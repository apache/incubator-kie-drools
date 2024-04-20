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
import java.util.Collection;
import java.util.Optional;

import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.InternalSchedulerService;
import org.kie.kogito.timer.Job;
import org.kie.kogito.timer.JobContext;
import org.kie.kogito.timer.TimerService;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.DefaultTimerJobFactoryManager;
import org.kie.kogito.timer.impl.TimerJobFactoryManager;
import org.kie.kogito.timer.impl.TimerJobInstance;

import io.vertx.mutiny.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VertxTimerServiceScheduler implements TimerService<ManageableJobHandle>,
        InternalSchedulerService {

    private static final long MIN_TIMER_DELAY = 1000;

    protected TimerJobFactoryManager jobFactoryManager = DefaultTimerJobFactoryManager.instance;

    protected final Vertx vertx;

    public VertxTimerServiceScheduler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void reset() {
        //no action is required
    }

    @Override
    public void shutdown() {
        vertx.close();
    }

    @Override
    public long getTimeToNextJob() {
        return 0;
    }

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances(long id) {
        return jobFactoryManager.getTimerJobInstances();
    }

    @Override
    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager) {
        this.jobFactoryManager = timerJobFactoryManager;
    }

    @Override
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return jobFactoryManager;
    }

    @Override
    public ManageableJobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        return Optional.ofNullable(trigger.hasNextFireTime())
                .map(id -> new ManageableJobHandle(false))
                .map(jobHandle -> jobFactoryManager.createTimerJobInstance(job, ctx, trigger, jobHandle, this))
                .map(jobInstance -> {
                    internalSchedule(jobInstance);
                    return (ManageableJobHandle) jobInstance.getJobHandle();
                })
                .orElse(null);
    }

    @Override
    public boolean removeJob(ManageableJobHandle jobHandle) {
        return vertx.cancelTimer(jobHandle.getId());
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        final Trigger trigger = timerJobInstance.getTrigger();
        if (trigger.hasNextFireTime() == null) {
            return;
        }
        final long then = trigger.hasNextFireTime().getTime();
        final ZonedDateTime now = DateUtil.now();
        final long delay = calculateDelay(then, now);
        final ManageableJobHandle handle = (ManageableJobHandle) timerJobInstance.getJobHandle();
        long scheduledId = vertx.setTimer(delay, i -> timerJobInstance.getJob().execute(timerJobInstance.getJobContext()));
        handle.setId(scheduledId);
        handle.setScheduledTime(now);
    }

    private long calculateDelay(long then, ZonedDateTime now) {
        long delay = then - now.toInstant().toEpochMilli();
        return Math.max(MIN_TIMER_DELAY, delay);
    }

    public Vertx getVertx() {
        return vertx;
    }
}
