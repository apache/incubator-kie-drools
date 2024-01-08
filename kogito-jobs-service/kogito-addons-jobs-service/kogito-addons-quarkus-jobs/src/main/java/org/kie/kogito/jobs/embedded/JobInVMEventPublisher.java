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

package org.kie.kogito.jobs.embedded;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.resource.RestApiConstants;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.jobs.service.events.JobDataEvent.JOB_EVENT_TYPE;

@ApplicationScoped
@Alternative
public class JobInVMEventPublisher implements JobEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobInVMEventPublisher.class);

    private final String url;

    private final List<EventPublisher> eventPublishers;

    private final ObjectMapper objectMapper;

    @Inject
    ReactiveJobScheduler scheduler;

    @Inject
    Event<EmbeddedJobServiceEvent> bus;

    public JobInVMEventPublisher(
            @ConfigProperty(name = "kogito.service.url", defaultValue = "http://localhost:8080") String url,
            Instance<EventPublisher> eventPublishers,
            ObjectMapper objectMapper) {
        this.url = url;
        this.eventPublishers = eventPublishers.stream().collect(toList());
        this.objectMapper = objectMapper;
        LOGGER.info("JobInVMEventPublisher Started with url {}", url);
    }

    @Override
    public JobExecutionResponse publishJobError(JobExecutionResponse response) {
        try {
            LOGGER.debug("publishJobError {}", response);

            ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionError, response)
                    .findFirst()
                    .run()
                    .thenApply(Optional::isPresent)
                    .exceptionally(e -> {
                        LOGGER.error("Error handling error {}", response, e);
                        return false;
                    }).toCompletableFuture().get();

            return response;
        } catch (Exception e) {
            LOGGER.error("error in publishJobError", e);
            return response;
        }
    }

    @Override
    public JobExecutionResponse publishJobSuccess(JobExecutionResponse response) {
        try {
            LOGGER.debug("publishJobSuccess {}", response);
            ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionSuccess, response)
                    .findFirst()
                    .run()
                    .thenApply(Optional::isPresent)
                    .exceptionally(e -> {
                        LOGGER.error("Error handling error {}", response, e);
                        return false;
                    }).toCompletableFuture().get();

            return response;
        } catch (Exception e) {
            LOGGER.error("error in publishJobSuccess", e);
            return response;
        }
    }

    @Override
    public JobDetails publishJobStatusChange(JobDetails jobDetails) {
        try {
            LOGGER.debug("publishJobStatusChange {}", jobDetails);
            if (eventPublishers.isEmpty()) {
                return jobDetails;
            }

            bus.fireAsync(new EmbeddedJobServiceEvent(jobDetails)).toCompletableFuture().get();
            return jobDetails;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void observe(@ObservesAsync EmbeddedJobServiceEvent serviceEvent) {
        JobDetails jobDetails = serviceEvent.getJobDetails();
        LOGGER.debug("Emmit in-vm publishJobStatusChange {}", jobDetails);
        try {
            ScheduledJob scheduledJob = ScheduledJobAdapter.of(jobDetails);
            byte[] jsonContent = objectMapper.writeValueAsBytes(scheduledJob);
            JobInstanceDataEvent event = new JobInstanceDataEvent(JOB_EVENT_TYPE,
                    url + RestApiConstants.JOBS_PATH,
                    jsonContent,
                    scheduledJob.getProcessInstanceId(),
                    scheduledJob.getRootProcessInstanceId(),
                    scheduledJob.getProcessId(),
                    scheduledJob.getRootProcessId(),
                    null);

            eventPublishers.forEach(e -> e.publish(event));
        } catch (Exception e) {
            LOGGER.error("Job status change propagation has failed at eventPublisher: " + eventPublishers.getClass() + " execution.", e);
        }
    }
}
