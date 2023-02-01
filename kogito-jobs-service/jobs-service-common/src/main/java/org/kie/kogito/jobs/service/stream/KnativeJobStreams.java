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

package org.kie.kogito.jobs.service.stream;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.kie.kogito.jobs.service.model.JobDetails;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

@ApplicationScoped
public class KnativeJobStreams extends AbstractJobStreams {

    public static final String PUBLISH_EVENTS_CONFIG_KEY = "kogito.jobs-service.knative-events";
    public static final String JOB_STATUS_CHANGE_EVENTS_KNATIVE = "kogito-job-service-job-status-events-knative";

    /**
     * Metadata to include the content-type for structured CloudEvents messages
     */
    public static final Supplier<OutgoingHttpMetadata> OUTGOING_HTTP_METADATA = () -> new OutgoingHttpMetadata.Builder()
            .addHeader(HttpHeaders.CONTENT_TYPE, JsonFormat.CONTENT_TYPE)
            .build();

    @Inject
    public KnativeJobStreams(ObjectMapper objectMapper,
            @ConfigProperty(name = PUBLISH_EVENTS_CONFIG_KEY) Optional<String> config,
            @Channel(JOB_STATUS_CHANGE_EVENTS_KNATIVE) @OnOverflow(value = OnOverflow.Strategy.LATEST) Emitter<String> emitter,
            @ConfigProperty(name = "kogito.service.url", defaultValue = "http://localhost:8080") String url) {
        super(objectMapper, config.map(Boolean::valueOf).filter(Boolean.TRUE::equals).orElse(false), emitter, url);
    }

    @Incoming(AvailableStreams.JOB_STATUS_CHANGE_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Override
    public void jobStatusChange(JobDetails job) {
        super.jobStatusChange(job);
    }

    @Override
    protected Message<String> decorate(Message<String> message) {
        return message.addMetadata(OUTGOING_HTTP_METADATA.get());
    }
}
