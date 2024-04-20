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
package org.kie.kogito.addons.quarkus.jobs.service.embedded.stream;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.jobs.JobsServiceException;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.resource.RestApiConstants;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static org.kie.kogito.jobs.service.events.JobDataEvent.JOB_EVENT_TYPE;

/**
 * This class is intended to propagate the job status change events to the embedded data index by using the
 * EventPublisher API. Events propagation is enabled only when the embedded data index is present in current application.
 */
@ApplicationScoped
@Alternative
public class EventPublisherJobStreams implements JobEventPublisher {

    public static final String DATA_INDEX_EVENT_PUBLISHER = "org.kie.kogito.index.addon.DataIndexEventPublisher";

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisherJobStreams.class);

    private final String url;

    private final List<EventPublisher> eventPublisher;

    private final ObjectMapper objectMapper;

    private final ManagedExecutor managedExecutor;

    @Inject
    public EventPublisherJobStreams(@ConfigProperty(name = "kogito.service.url", defaultValue = "http://localhost:8080") String url,
            Instance<EventPublisher> eventPublishers,
            ObjectMapper objectMapper,
            ManagedExecutor managedExecutor) {
        this.url = url;
        eventPublisher = eventPublishers.stream().collect(Collectors.toList());
        this.objectMapper = objectMapper;
        this.managedExecutor = managedExecutor;
    }

    @Override
    public JobDetails publishJobStatusChange(JobDetails jobDetails) {
        try {
            managedExecutor.runAsync(() -> {
                if (eventPublisher != null) {
                    ScheduledJob scheduledJob = ScheduledJobAdapter.of(jobDetails);
                    byte[] jsonContent;
                    try {
                        jsonContent = objectMapper.writeValueAsBytes(scheduledJob);
                    } catch (Exception e) {
                        throw new JobsServiceException("It was not possible to serialize scheduledJob to json: " + scheduledJob, e);
                    }
                    JobInstanceDataEvent event = new JobInstanceDataEvent(JOB_EVENT_TYPE,
                            url + RestApiConstants.JOBS_PATH,
                            jsonContent,
                            scheduledJob.getProcessInstanceId(),
                            scheduledJob.getRootProcessInstanceId(),
                            scheduledJob.getProcessId(),
                            scheduledJob.getRootProcessId(),
                            null);
                    try {
                        eventPublisher.forEach(e -> e.publish(event));
                    } catch (Exception e) {
                        LOGGER.error("Job status change propagation has failed at eventPublisher: " + eventPublisher.getClass() + " execution.", e);
                    }
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Job status change propagation has failed.", e);
        }
        return jobDetails;
    }
}
