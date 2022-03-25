/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.messaging.quarkus;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.JobsServiceException;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.api.event.serialization.JobCloudEventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.MutinyEmitter;

import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackPatternJob;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackURI;

@ApplicationScoped
public class ReactiveMessagingJobsService implements JobsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingJobsService.class);

    private static final String KOGITO_ADDON = "jobs-messaging";

    private static final String KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL = "kogito-job-service-job-request-events";

    private final JobCloudEventSerializer serializer;

    private final URI serviceUrl;

    private final MutinyEmitter<String> eventsEmitter;

    @Inject
    public ReactiveMessagingJobsService(
            @ConfigProperty(name = "kogito.service.url") URI serviceUrl,
            ObjectMapper objectMapper,
            @Channel(KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL) MutinyEmitter<String> eventsEmitter) {
        this.serviceUrl = serviceUrl;
        this.eventsEmitter = eventsEmitter;
        this.serializer = new JobCloudEventSerializer(objectMapper);
    }

    @Override
    public String scheduleProcessJob(ProcessJobDescription description) {
        throw new UnsupportedOperationException("Scheduling for process jobs is not yet implemented");
    }

    @Override
    public String scheduleProcessInstanceJob(ProcessInstanceJobDescription description) {
        Job job = buildCallbackPatternJob(description, buildCallbackURI(description, serviceUrl.toString()));
        LOGGER.debug("scheduleProcessInstanceJob job: {}", job);
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .source(serviceUrl)
                .job(job)
                .processInstanceId(description.processInstanceId())
                .processId(description.processId())
                .rootProcessInstanceId(description.rootProcessInstanceId())
                .rootProcessId(description.rootProcessId())
                .kogitoAddons(KOGITO_ADDON)
                .build();
        emitEvent(event);
        return job.getId();
    }

    @Override
    public boolean cancelJob(String id) {
        LOGGER.debug("cancelJob, id: {}", id);
        CancelJobRequestEvent event = CancelJobRequestEvent.builder()
                .source(serviceUrl)
                .jobId(id)
                .kogitoAddons(KOGITO_ADDON)
                .build();
        emitEvent(event);
        return true;
    }

    private void emitEvent(JobCloudEvent<?> event) {
        LOGGER.debug("About to emit JobCloudEvent {} to channel {}", event, KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL);
        try {
            String json = serializer.serialize(event);
            LOGGER.trace("JobCloudEvent json value: {}", json);
            eventsEmitter.sendAndAwait(json);
            LOGGER.trace("Successfully emitted JobCloudEvent {} to channel {}", event, KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL);
        } catch (Exception e) {
            throw new JobsServiceException("Error while emitting JobCloudEvent event to channel: " +
                    KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL + ", event: " + event, e);
        }
    }
}
