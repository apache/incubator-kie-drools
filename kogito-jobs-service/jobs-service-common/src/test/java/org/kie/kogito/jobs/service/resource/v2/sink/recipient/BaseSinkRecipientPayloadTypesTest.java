/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.resource.v2.sink.recipient;

import java.net.URI;
import java.time.OffsetDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.resource.v2.ExternalResourcesMock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.cloudevents.SpecVersion;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.TestUtils.assertJobHasFinishedV2;
import static org.kie.kogito.jobs.service.TestUtils.createJobV2;
import static org.kie.kogito.jobs.service.health.HealthCheckUtils.awaitReadyHealthCheck;

public abstract class BaseSinkRecipientPayloadTypesTest implements ExternalResourcesMock.ExternalResourcesMockAware {

    public static final String EXTERNAL_RESOURCE_FOR_BINARY_MODE_JSON_PAYLOAD = "/external-resource/sink-recipient-binary-mode-json-payload-data";
    public static final String EXTERNAL_RESOURCE_FOR_BINARY_MODE_BINARY_PAYLOAD = "/external-resource/sink-recipient-binary-mode-binary-payload-data";
    public static final String EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_JSON_PAYLOAD = "/external-resource/sink-recipient-structured-mode-json-payload-data";
    public static final String EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_BINARY_PAYLOAD = "/external-resource/sink-recipient-structured-mode-binary-payload-data";

    public static final String SINK_BINARY_MODE_JSON_JOB_ID = "SINK_BINARY_MODE_JSON_JOB_ID";
    public static final String SINK_BINARY_MODE_BINARY_JOB_ID = "SINK_BINARY_MODE_BINARY_JOB_ID";
    public static final String SINK_STRUCTURED_MODE_JSON_JOB_ID = "SINK_STRUCTURED_MODE_JSON_JOB_ID";
    public static final String SINK_STRUCTURED_MODE_BINARY_JOB_ID = "SINK_STRUCTURED_MODE_BINARY_JOB_ID";

    public static final String SINK_CE_TYPE = "SINK_CE_TYPE";
    public static final SpecVersion SINK_CE_SPECVERSION = SpecVersion.V1;
    public static final URI SINK_CE_SOURCE = URI.create("http://SINK_CE_SOURCE");
    public static final String SINK_CE_SUBJECT = "SINK_CE_SUBJECT";
    public static final String SINK_CE_DATACONTENTTYPE = "SINK_CE_DATACONTENTTYPE";
    public static final URI SINK_CE_DATASCHEMA = URI.create("http://SINK_CE_DATASCHEMA");
    public static final String SINK_EXTENSION_1_NAME = "sinkextension1name";
    public static final String SINK_EXTENSION_1_VALUE = "SINK_EXTENSION_1_VALUE";
    public static final String SINK_EXTENSION_2_NAME = "sinkextension2name";
    public static final String SINK_EXTENSION_2_VALUE = "SINK_EXTENSION_2_VALUE";
    public static final String SINK_PROPERTY_1 = "PROPERTY_1";
    public static final String SINK_PROPERTY_1_VALUE = "PROPERTY_1_VALUE";
    public static final byte[] SINK_BINARY_VALUE = "Arbitrary bytes sent to the sink recipient".getBytes();

    protected WireMockServer externalResourcesServer;

    protected String externalResourcesServerURL;

    @Inject
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        awaitReadyHealthCheck(1, MINUTES);
    }

    @Override
    public void setExternalResourcesServer(WireMockServer externalResourcesServer) {
        this.externalResourcesServer = externalResourcesServer;
        this.externalResourcesServerURL = externalResourcesServer.baseUrl();
    }

    @Test
    void sinkRecipientBinaryModeWithJsonPayloadData() throws Exception {
        ObjectNode payload = objectMapper.createObjectNode()
                .put(SINK_PROPERTY_1, SINK_PROPERTY_1_VALUE);
        SinkRecipient<SinkRecipientJsonPayloadData> sinkRecipient = SinkRecipient.builder()
                .forJsonPayload()
                .payload(SinkRecipientJsonPayloadData.from(payload))
                .sinkUrl(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_BINARY_MODE_JSON_PAYLOAD)
                .contentMode(SinkRecipient.ContentMode.BINARY)
                .build();
        applyCommonValues(sinkRecipient);
        Job job = Job.builder()
                .id(SINK_BINARY_MODE_JSON_JOB_ID)
                .correlationId(SINK_BINARY_MODE_JSON_JOB_ID)
                .recipient(sinkRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeSinkRecipientJob(job);
    }

    @Test
    void sinkRecipientBinaryModeWithBinaryPayloadData() throws Exception {
        SinkRecipient<SinkRecipientBinaryPayloadData> sinkRecipient = SinkRecipient.builder()
                .forBinaryPayload()
                .payload(SinkRecipientBinaryPayloadData.from(SINK_BINARY_VALUE))
                .sinkUrl(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_BINARY_MODE_BINARY_PAYLOAD)
                .contentMode(SinkRecipient.ContentMode.BINARY)
                .build();
        applyCommonValues(sinkRecipient);
        Job job = Job.builder()
                .id(SINK_BINARY_MODE_BINARY_JOB_ID)
                .correlationId(SINK_BINARY_MODE_BINARY_JOB_ID)
                .recipient(sinkRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeSinkRecipientJob(job);
    }

    @Test
    void sinkRecipientStructuredModeWithJsonPayloadData() throws Exception {
        ObjectNode payload = objectMapper.createObjectNode()
                .put(SINK_PROPERTY_1, SINK_PROPERTY_1_VALUE);
        SinkRecipient<SinkRecipientJsonPayloadData> sinkRecipient = SinkRecipient.builder()
                .forJsonPayload()
                .payload(SinkRecipientJsonPayloadData.from(payload))
                .sinkUrl(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_JSON_PAYLOAD)
                .contentMode(SinkRecipient.ContentMode.STRUCTURED)
                .build();
        applyCommonValues(sinkRecipient);
        Job job = Job.builder()
                .id(SINK_STRUCTURED_MODE_JSON_JOB_ID)
                .correlationId(SINK_STRUCTURED_MODE_JSON_JOB_ID)
                .recipient(sinkRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeSinkRecipientJob(job);
    }

    @Test
    void sinkRecipientStructuredModeWithBinaryPayloadData() throws Exception {
        SinkRecipient<SinkRecipientBinaryPayloadData> sinkRecipient = SinkRecipient.builder()
                .forBinaryPayload()
                .payload(SinkRecipientBinaryPayloadData.from(SINK_BINARY_VALUE))
                .sinkUrl(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_BINARY_PAYLOAD)
                .contentMode(SinkRecipient.ContentMode.STRUCTURED)
                .build();
        applyCommonValues(sinkRecipient);
        Job job = Job.builder()
                .id(SINK_STRUCTURED_MODE_BINARY_JOB_ID)
                .correlationId(SINK_STRUCTURED_MODE_BINARY_JOB_ID)
                .recipient(sinkRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeSinkRecipientJob(job);
    }

    private static void executeSinkRecipientJob(Job job) throws Exception {
        Job createdJob = createJobV2(job);
        assertThat(createdJob).isNotNull();
        assertJobHasFinishedV2(job.getId(), 60);
    }

    private static void applyCommonValues(SinkRecipient<?> recipient) {
        recipient.setCeSpecVersion(SINK_CE_SPECVERSION);
        recipient.setCeType(SINK_CE_TYPE);
        recipient.setCeDataSchema(SINK_CE_DATASCHEMA);
        recipient.setCeDataContentType(SINK_CE_DATACONTENTTYPE);
        recipient.setCeSource(SINK_CE_SOURCE);
        recipient.setCeSubject(SINK_CE_SUBJECT);
        recipient.addCeExtension(SINK_EXTENSION_1_NAME, SINK_EXTENSION_1_VALUE);
        recipient.addCeExtension(SINK_EXTENSION_2_NAME, SINK_EXTENSION_2_VALUE);
    }
}
