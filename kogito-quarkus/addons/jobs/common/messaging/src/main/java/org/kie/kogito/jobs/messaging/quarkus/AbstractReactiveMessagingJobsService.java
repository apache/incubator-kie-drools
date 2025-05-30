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
package org.kie.kogito.jobs.messaging.quarkus;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.JobsServiceException;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.api.event.serialization.JobCloudEventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.providers.locals.ContextAwareMessage;
import io.vertx.core.Context;
import io.vertx.core.Vertx;

import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackPatternJob;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackURI;

public abstract class AbstractReactiveMessagingJobsService implements JobsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReactiveMessagingJobsService.class);

    public static final String KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL = "kogito-job-service-job-request-events";

    private JobCloudEventSerializer serializer;

    private URI serviceUrl;

    private Emitter<String> eventsEmitter;

    private ObjectMapper objectMapper;

    protected AbstractReactiveMessagingJobsService() {
    }

    protected AbstractReactiveMessagingJobsService(URI serviceUrl,
            ObjectMapper objectMapper,
            Emitter<String> eventsEmitter) {
        this.serviceUrl = serviceUrl;
        this.eventsEmitter = eventsEmitter;
        this.objectMapper = objectMapper;
        this.serializer = new JobCloudEventSerializer(objectMapper);
    }

    @Override
    public String scheduleJob(JobDescription description) {
        Job job = buildCallbackPatternJob(description, buildCallbackURI(description, serviceUrl.toString()), objectMapper);
        LOGGER.debug("scheduleProcessInstanceJob job: {}", job);
        CreateJobEvent event = CreateJobEvent.builder()
                .source(serviceUrl)
                .job(job)
                .build();
        emitEvent(event);
        return job.getId();
    }

    @Override
    public boolean cancelJob(String id) {
        LOGGER.debug("cancelJob, id: {}", id);
        DeleteJobEvent event = DeleteJobEvent.builder()
                .source(serviceUrl)
                .lookupId(JobLookupId.fromId(id))
                .build();
        emitEvent(event);
        return true;
    }

    @Override
    public String rescheduleJob(JobDescription jobDescription) {
        cancelJob(jobDescription.id());
        return scheduleJob(jobDescription);
    }

    protected Message<String> decorate(Message<String> message) {
        return message;
    }

    protected abstract String getAddonName();

    void emitEvent(JobCloudEvent<?> event) {
        LOGGER.debug("About to emit JobCloudEvent {} to channel {}", event, KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL);
        try {
            String json = serializer.serialize(event);
            LOGGER.trace("JobCloudEvent json value: {}", json);
            Context context = Vertx.currentContext();
            Uni<Void> uni = Uni.createFrom().emitter(e -> eventsEmitter.send(decorate(ContextAwareMessage.of(json)
                    .withAck(() -> {
                        e.complete(null);
                        return CompletableFuture.completedFuture(null);
                    }).withNack(reason -> {
                        e.fail(reason);
                        return CompletableFuture.completedFuture(null);
                    }))));
            if (context != null) {
                uni = uni.emitOn(runnable -> context.runOnContext(x -> runnable.run()));
            }
            uni.await().indefinitely();
            LOGGER.trace("Successfully emitted JobCloudEvent {} to channel {}", event, KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL);
        } catch (Exception e) {
            throw new JobsServiceException("Error while emitting JobCloudEvent event to channel: " +
                    KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL + ", event: " + event, e);
        }
    }
}
