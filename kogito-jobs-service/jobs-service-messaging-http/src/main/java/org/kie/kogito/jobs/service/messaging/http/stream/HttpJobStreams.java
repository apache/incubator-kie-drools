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
package org.kie.kogito.jobs.service.messaging.http.stream;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.stream.AbstractJobStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

@ApplicationScoped
public class HttpJobStreams extends AbstractJobStreams {

    public static final String PUBLISH_EVENTS_CONFIG_KEY = "kogito.jobs-service.http.job-status-change-events";
    public static final String JOB_STATUS_CHANGE_EVENTS_HTTP = "kogito-job-service-job-status-events-http";
    public static final String PARTITION_KEY_EXTENSION = "partitionkey";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJobStreams.class);

    /**
     * Metadata to include the content-type for structured CloudEvents messages
     */
    public static final Supplier<OutgoingHttpMetadata> OUTGOING_HTTP_METADATA = () -> new OutgoingHttpMetadata.Builder()
            .addHeader(HttpHeaders.CONTENT_TYPE, JsonFormat.CONTENT_TYPE)
            .build();

    @Inject
    public HttpJobStreams(ObjectMapper objectMapper,
            @ConfigProperty(name = PUBLISH_EVENTS_CONFIG_KEY) Optional<Boolean> config,
            @Channel(JOB_STATUS_CHANGE_EVENTS_HTTP) @OnOverflow(value = OnOverflow.Strategy.UNBOUNDED_BUFFER) Emitter<String> emitter,
            @ConfigProperty(name = "kogito.service.url", defaultValue = "http://localhost:8080") String url) {
        super(objectMapper, config.orElse(false), emitter, url);
    }

    @Override
    public void jobStatusChange(JobDetails job) {
        LOGGER.debug("jobStatusChange call received, enabled: {}, job: {}", enabled, job);
        super.jobStatusChange(job);
    }

    @Override
    protected JobDataEvent buildEvent(JobDetails job) {
        JobDataEvent event = super.buildEvent(job);
        // use the well-known extension https://github.com/cloudevents/spec/blob/main/cloudevents/extensions/partitioning.md
        // to instruct potential http driven Brokers like, Knative Eventing Kafka Broker, to process accordingly.
        event.addExtensionAttribute(PARTITION_KEY_EXTENSION, event.getData().getId());
        return event;
    }

    @Override
    protected Message<String> decorate(Message<String> message, JobDataEvent event) {
        return message.addMetadata(OUTGOING_HTTP_METADATA.get());
    }
}
