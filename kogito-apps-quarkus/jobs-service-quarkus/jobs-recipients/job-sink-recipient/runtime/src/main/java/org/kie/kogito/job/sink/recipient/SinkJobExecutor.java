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
package org.kie.kogito.job.sink.recipient;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.job.recipient.common.http.HTTPRequest;
import org.kie.kogito.job.recipient.common.http.HTTPRequestExecutor;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.vertx.core.http.HttpHeaders;
import io.vertx.mutiny.core.Vertx;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SinkJobExecutor extends HTTPRequestExecutor<SinkRecipient<?>> implements JobExecutor {

    static final String CE_SPECVERSION_HEADER = "ce-specversion";
    static final String CE_ID_HEADER = "ce-id";
    static final String CE_SOURCE_HEADER = "ce-source";
    static final String CE_TYPE_HEADER = "ce-type";
    static final String CE_TIME_HEADER = "ce-time";
    static final String CE_SUBJECT_HEADER = "ce-subject";
    static final String CE_DATASCHEMA_HEADER = "ce-dataschema";
    static final String CE_DATASCHEMA_HEADER_V03 = "ce-schemaurl";

    @Inject
    public SinkJobExecutor(@ConfigProperty(name = "kogito.job.recipient.sink.timeout-in-millis") long timeout,
            Vertx vertx,
            ObjectMapper objectMapper) {
        super(timeout, vertx, objectMapper);
    }

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public Class<SinkRecipient> type() {
        return SinkRecipient.class;
    }

    @Override
    protected SinkRecipient<?> getRecipient(JobDetails job) {
        if (job.getRecipient().getRecipient() instanceof SinkRecipient) {
            return (SinkRecipient<?>) job.getRecipient().getRecipient();
        }
        throw new IllegalArgumentException("SinkRecipient is expected for job " + job);
    }

    @Override
    protected HTTPRequest buildRequest(SinkRecipient<?> recipient, String limit) {
        String resolvedSinkUrl = recipient.getSinkUrl();
        if (recipient.getContentMode() == SinkRecipient.ContentMode.STRUCTURED) {
            return buildStructuredRequest(recipient, resolvedSinkUrl, HTTPRequest.HTTPMethod.POST, limit);
        } else {
            return buildBinaryRequest(recipient, resolvedSinkUrl, HTTPRequest.HTTPMethod.POST, limit);
        }
    }

    private HTTPRequest buildBinaryRequest(SinkRecipient<?> recipient, String sinkUrl, HTTPRequest.HTTPMethod method, String limit) {
        HTTPRequest.Builder builder = HTTPRequest.builder()
                .url(sinkUrl)
                .method(method)
                .addHeader(HttpHeaders.CONTENT_TYPE.toString(), recipient.getCeDataContentType())
                .addHeader(CE_SPECVERSION_HEADER, recipient.getCeSpecVersion().toString())
                .addHeader(CE_ID_HEADER, buildRandomId())
                .addHeader(CE_SOURCE_HEADER, recipient.getCeSource().toString())
                .addHeader(CE_TYPE_HEADER, recipient.getCeType())
                .addHeader(CE_TIME_HEADER, OffsetDateTime.now().toString());

        if (recipient.getCeDataSchema() != null) {
            builder.addHeader(recipient.getCeSpecVersion() == SpecVersion.V03 ? CE_DATASCHEMA_HEADER_V03 : CE_DATASCHEMA_HEADER, recipient.getCeDataSchema().toString());
        }
        if (recipient.getCeSubject() != null) {
            builder.addHeader(CE_SUBJECT_HEADER, recipient.getCeSubject());
        }
        filterEntries(recipient.getCeExtensions())
                .forEach((key, value) -> builder.addHeader(ceHeader(key), value.toString()));
        builder.addHeader(ceHeader("limit"), limit);
        builder.body(recipient.getPayload().getData());
        return builder.build();
    }

    private HTTPRequest buildStructuredRequest(SinkRecipient<?> recipient, String sinkUrl, HTTPRequest.HTTPMethod method, String limit) {
        HTTPRequest.Builder requestBuilder = HTTPRequest.builder()
                .url(sinkUrl)
                .method(method)
                .addHeader(HttpHeaders.CONTENT_TYPE.toString(), JsonFormat.CONTENT_TYPE);

        CloudEventBuilder eventBuilder = CloudEventBuilder.v1()
                .withType(recipient.getCeType())
                .withId(buildRandomId())
                .withSource(recipient.getCeSource())
                .withTime(OffsetDateTime.now());

        if (recipient.getCeDataContentType() != null) {
            eventBuilder.withDataContentType(recipient.getCeDataContentType());
        }
        if (recipient.getCeDataSchema() != null) {
            eventBuilder.withDataSchema(recipient.getCeDataSchema());
        }
        if (recipient.getCeSubject() != null) {
            eventBuilder.withSubject(recipient.getCeSubject());
        }
        filterEntries(recipient.getCeExtensions())
                .forEach((key, value) -> eventBuilder.withExtension(key, value.toString()));
        if (limit != null) {
            eventBuilder.withExtension("limit", limit);
        }
        if (recipient.getPayload() != null) {
            if (recipient.getPayload().getData() instanceof byte[]) {
                eventBuilder.withData((byte[]) recipient.getPayload().getData());
            } else if (recipient.getPayload().getData() instanceof JsonNode) {
                eventBuilder.withData(JsonCloudEventData.wrap((JsonNode) recipient.getPayload().getData()));
            }
        }
        CloudEvent event = eventBuilder.build();
        if (recipient.getCeSpecVersion() == SpecVersion.V03) {
            event = CloudEventBuilder.v03(event).build();
        }
        byte[] body = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE).serialize(event);
        return requestBuilder.body(body).build();
    }

    private static String buildRandomId() {
        return UUID.randomUUID().toString();
    }

    private static String ceHeader(String name) {
        return "ce-" + name;
    }
}
