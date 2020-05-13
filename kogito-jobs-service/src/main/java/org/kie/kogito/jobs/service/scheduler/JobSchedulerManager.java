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

package org.kie.kogito.jobs.service.scheduler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.quarkus.runtime.StartupEvent;
import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.VertxJobScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JobSchedulerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerManager.class);

    /**
     * The current chunk size  in minutes the scheduler handles, it is used to keep a limit number of jobs scheduled
     * in the in-memory scheduler.
     */
    @ConfigProperty(name = "kogito.jobs-service.schedulerChunkInMinutes")
    long schedulerChunkInMinutes;

    /**
     * The interval the job loading method runs to fetch the persisted jobs from the repository.
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobIntervalInMinutes")
    long loadJobIntervalInMinutes;

    /**
     * The interval based on the current time the job loading method uses to fetch jobs "FROM (now -
     * {@link #loadJobFromCurrentTimeIntervalInMinutes}) TO {@link #schedulerChunkInMinutes}"
     */
    @ConfigProperty(name = "kogito.jobs-service.loadJobFromCurrentTimeIntervalInMinutes")
    long loadJobFromCurrentTimeIntervalInMinutes;

    @Inject
    VertxJobScheduler scheduler;

    @Inject
    ReactiveJobRepository repository;

    @Inject
    Vertx vertx;

    void onStartup(@Observes StartupEvent startupEvent) {
        if (loadJobIntervalInMinutes > schedulerChunkInMinutes) {
            LOGGER.warn("The loadJobIntervalInMinutes ({}) cannot be greater than schedulerChunkInMinutes ({}), " +
                                "setting value {} for both",
                        loadJobIntervalInMinutes,
                        schedulerChunkInMinutes,
                        schedulerChunkInMinutes);
            loadJobIntervalInMinutes = schedulerChunkInMinutes;
        }

        //first execution
        vertx.runOnContext(v -> loadScheduledJobs());
        //periodic execution
        vertx.setPeriodic(TimeUnit.MINUTES.toMillis(loadJobIntervalInMinutes), id -> loadScheduledJobs());
    }

    //Runs periodically loading the jobs from the repository in chunks
    void loadScheduledJobs() {
        loadJobsInCurrentChunk()
                .filter(j -> !scheduler.scheduled(j.getId()).isPresent())//not consider already scheduled jobs
                .flatMapRsPublisher(t -> ErrorHandling.skipErrorPublisher(scheduler::schedule, t))
                .forEach(a -> LOGGER.debug("Loaded and scheduled job {}", a))
                .run()
                .whenComplete((v, t) -> Optional.ofNullable(t)
                        .map(ex -> {
                            LOGGER.error("Error Loading scheduled jobs!", ex);
                            return null;
                        })
                        .orElseGet(() -> {
                            LOGGER.info("Loading scheduled jobs completed !");
                            return null;
                        })
                );
    }

    private PublisherBuilder<ScheduledJob> loadJobsInCurrentChunk() {
        return repository.findByStatusBetweenDatesOrderByPriority(DateUtil.now().minusMinutes(loadJobFromCurrentTimeIntervalInMinutes),
                                                                  DateUtil.now().plusMinutes(schedulerChunkInMinutes),
                                                                  JobStatus.SCHEDULED, JobStatus.RETRY);
    }
}