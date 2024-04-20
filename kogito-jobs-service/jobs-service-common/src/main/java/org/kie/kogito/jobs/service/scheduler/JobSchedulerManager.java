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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.management.MessagingChangeEvent;
import org.kie.kogito.jobs.service.management.ReleaseLeaderEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.mutiny.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static org.kie.kogito.jobs.service.repository.ReactiveJobRepository.SortTerm.byCreated;
import static org.kie.kogito.jobs.service.repository.ReactiveJobRepository.SortTerm.byFireTime;
import static org.kie.kogito.jobs.service.repository.ReactiveJobRepository.SortTerm.byId;

@ApplicationScoped
public class JobSchedulerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerManager.class);

    /**
     * The current chunk size in minutes the scheduler handles, it is used to keep a limit number of jobs scheduled
     * in the in-memory scheduler.
     */
    @ConfigProperty(name = "kogito.jobs-service.schedulerChunkInMinutes", defaultValue = "10")
    long schedulerChunkInMinutes;

    /**
     * The interval the job loading method runs to fetch the persisted jobs from the repository.
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobIntervalInMinutes", defaultValue = "10")
    long loadJobIntervalInMinutes;

    /**
     * The interval based on the current time the job loading method uses to fetch jobs "FROM (now -
     * {@link #loadJobFromCurrentTimeIntervalInMinutes}) TO {@link #schedulerChunkInMinutes}"
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobFromCurrentTimeIntervalInMinutes", defaultValue = "0")
    long loadJobFromCurrentTimeIntervalInMinutes;

    /**
     * Number of retries configured for the periodic jobs loading procedure. Every time the procedure is started this
     * value is considered.
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobRetries", defaultValue = "3")
    int loadJobRetries;

    /**
     * Error strategy to apply when the periodic jobs loading procedure has exceeded the jobLoadReties.
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobErrorStrategy", defaultValue = "NONE")
    String loadJobErrorStrategy;

    @Inject
    TimerDelegateJobScheduler scheduler;

    @Inject
    ReactiveJobRepository repository;

    @Inject
    Event<ReleaseLeaderEvent> releaseLeaderEvent;

    @Inject
    Event<JobSchedulerManagerErrorEvent> jobSchedulerManagerErrorEvent;

    @Inject
    Vertx vertx;
    final AtomicBoolean enabled = new AtomicBoolean(false);

    final AtomicLong periodicTimerIdForLoadJobs = new AtomicLong(-1L);

    final AtomicBoolean initialLoading = new AtomicBoolean(true);

    static final ZonedDateTime INITIAL_DATE = ZonedDateTime.of(LocalDateTime.parse("2000-01-01T00:00:00"), DateUtil.DEFAULT_ZONE);

    enum LoadJobErrorStrategy {
        NONE,
        /**
         * The service liveness check goes to DOWN, indicating that the service must be restarted.
         */
        FAIL_SERVICE
    }

    private void startJobsLoadingFromRepositoryTask() {
        //guarantee it starts the task just in case it is not already active
        initialLoading.set(true);
        if (periodicTimerIdForLoadJobs.get() < 0) {
            if (loadJobIntervalInMinutes > schedulerChunkInMinutes) {
                LOGGER.warn("The loadJobIntervalInMinutes ({}) cannot be greater than schedulerChunkInMinutes ({}), " +
                        "setting value {} for both",
                        loadJobIntervalInMinutes,
                        schedulerChunkInMinutes,
                        schedulerChunkInMinutes);
                loadJobIntervalInMinutes = schedulerChunkInMinutes;
            }
            //first execution
            vertx.runOnContext(this::loadJobDetails);
            //next executions to run periodically
            periodicTimerIdForLoadJobs.set(vertx.setPeriodic(TimeUnit.MINUTES.toMillis(loadJobIntervalInMinutes), id -> loadJobDetails()));
        }
    }

    private void cancelJobsLoadingFromRepositoryTask() {
        if (periodicTimerIdForLoadJobs.get() > 0) {
            vertx.cancelTimer(periodicTimerIdForLoadJobs.get());
            //negative id indicates this is not active anymore
            periodicTimerIdForLoadJobs.set(-1);
        }
    }

    protected synchronized void onMessagingStatusChange(@Observes MessagingChangeEvent event) {
        boolean wasEnabled = enabled.getAndSet(event.isEnabled());
        if (enabled.get() && !wasEnabled) {
            // good, avoid starting twice if we receive two consecutive enabled = true
            startJobsLoadingFromRepositoryTask();
        } else if (!enabled.get()) {
            // but only cancel if we receive enabled = false, otherwise with two consecutive enable we are also cancelling.
            cancelJobsLoadingFromRepositoryTask();
        }
    }

    /**
     * Runs periodically loading the jobs from the repository in chunks.
     */
    void loadJobDetails() {
        if (!enabled.get()) {
            LOGGER.info("Skip loading scheduled jobs");
            return;
        }
        ZonedDateTime fromFireTime = DateUtil.now().minusMinutes(loadJobFromCurrentTimeIntervalInMinutes);
        ZonedDateTime toFireTime = DateUtil.now().plusMinutes(schedulerChunkInMinutes);
        if (initialLoading.get()) {
            fromFireTime = INITIAL_DATE;
        }
        doLoadJobDetails(fromFireTime, toFireTime, loadJobRetries);
    }

    public void doLoadJobDetails(ZonedDateTime fromFireTime, ZonedDateTime toFireTime, final int retries) {
        LOGGER.info("Loading jobs to schedule from the repository, fromFireTime: {} toFireTime: {}.", fromFireTime, toFireTime);
        loadJobsBetweenDates(fromFireTime, toFireTime)
                .filter(this::isNotScheduled)
                .flatMapRsPublisher(jobDetails -> ErrorHandling.skipErrorPublisher((jd) -> scheduler.internalSchedule(jd, initialLoading.get()), jobDetails))
                .forEach(jobDetails -> LOGGER.debug("Loaded and scheduled job {}.", jobDetails))
                .run()
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error(String.format("Error during jobs loading, retries left: %d.", retries), throwable);
                        if (retries > 0) {
                            LOGGER.info("Jobs loading retry: #{} will be executed.", retries - 1);
                            doLoadJobDetails(fromFireTime, toFireTime, retries - 1);
                        } else {
                            LOGGER.error("Jobs loading has failed and no more retires are left, loadJobErrorStrategy: {} will be applied.", loadJobErrorStrategy);
                            applyLoadJobsErrorStrategy(throwable);
                        }
                    }
                    initialLoading.set(false);
                    LOGGER.info("Loading scheduled jobs completed !");
                });
    }

    private boolean isNotScheduled(JobDetails jobDetails) {
        Date triggerFireTime = jobDetails.getTrigger().hasNextFireTime();
        ZonedDateTime nextFireTime = triggerFireTime != null ? DateUtil.instantToZonedDateTime(triggerFireTime.toInstant()) : null;
        boolean scheduled = scheduler.scheduled(jobDetails.getId()).isPresent();
        LOGGER.debug("Job found, id: {}, nextFireTime: {}, created: {}, status: {}, already scheduled: {}", jobDetails.getId(),
                nextFireTime,
                jobDetails.getCreated(),
                jobDetails.getStatus(),
                scheduled);
        return !scheduled;
    }

    private PublisherBuilder<JobDetails> loadJobsBetweenDates(ZonedDateTime fromFireTime, ZonedDateTime toFireTime) {
        return repository.findByStatusBetweenDates(fromFireTime, toFireTime,
                new JobStatus[] { JobStatus.SCHEDULED, JobStatus.RETRY },
                new ReactiveJobRepository.SortTerm[] { byCreated(true), byFireTime(true), byId(true) });
    }

    private void applyLoadJobsErrorStrategy(Throwable throwable) {
        if (LoadJobErrorStrategy.FAIL_SERVICE.name().equalsIgnoreCase(loadJobErrorStrategy)) {
            scheduler.unscheduleTimers();
            cancelJobsLoadingFromRepositoryTask();
            releaseLeaderEvent.fire(new ReleaseLeaderEvent());
            String message = "An unrecoverable error occurred during the jobs loading procedure from database." +
                    " Please check the database status and configuration, or contact the administrator for a detailed review of the error: " + throwable.getMessage();
            LOGGER.error(message, throwable);
            jobSchedulerManagerErrorEvent.fire(new JobSchedulerManagerErrorEvent(message, throwable));
        }
    }
}
