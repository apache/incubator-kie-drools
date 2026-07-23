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
package org.kie.kogito.jobs.service.resource.v2.http.recipient;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.resource.v2.ExternalResourcesMock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import jakarta.inject.Inject;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.TestUtils.assertJobHasFinishedV2;
import static org.kie.kogito.jobs.service.TestUtils.createJobV2;
import static org.kie.kogito.jobs.service.health.HealthCheckUtils.awaitReadyHealthCheck;

public abstract class BaseHttpRecipientPayloadTypesTest implements ExternalResourcesMock.ExternalResourcesMockAware {

    public static final String EXTERNAL_RESOURCE_FOR_JSON_PAYLOAD = "/external-resource/http-recipient-json-payload-data";

    public static final String EXTERNAL_RESOURCE_FOR_STRING_PAYLOAD = "/external-resource/http-recipient-string-payload-data";

    public static final String EXTERNAL_RESOURCE_FOR_BINARY_PAYLOAD = "/external-resource/http-recipient-binary-payload-data";

    public static final String EXTERNAL_RESOURCE_FOR_CUSTOM_TIMEOUT = "/external-resource/http-recipient-custom-timeout";

    public static final String HTTP_JSON_JOB_ID = "HTTP_JSON_JOB_ID";

    public static final String HTTP_STRING_JOB_ID = "HTTP_STRING_JOB_ID";

    public static final String HTTP_BINARY_JOB_ID = "HTTP_BINARY_JOB_ID";

    public static final String HTTP_JOB_WITH_CUSTOM_TIMEOUT_ID = "HTTP_JOB_WITH_CUSTOM_TIMEOUT_ID";

    public static final String HTTP_HEADER_1 = "HTTP_HEADER_1";

    public static final String HTTP_HEADER_1_VALUE = "HTTP_HEADER_1_VALUE";

    public static final String HTTP_HEADER_2 = "HTTP_HEADER_2";

    public static final String HTTP_HEADER_2_VALUE = "HTTP_HEADER_2_VALUE";

    public static final String HTTP_QUERY_PARAM_1 = "HTTP_QUERY_PARAM_1";

    public static final String HTTP_QUERY_PARAM_1_VALUE = "HTTP_QUERY_PARAM_1_VALUE";

    public static final String HTTP_PROPERTY_1 = "HTTP_PROPERTY_1";

    public static final String HTTP_PROPERTY_1_VALUE = "HTTP_PROPERTY_1_VALUE";

    public static final String HTTP_TEXT_PLAIN_VALUE = "Plain text sent to the recipient";

    public static final byte[] HTTP_BINARY_VALUE = "Arbitrary bytes sent to the http recipient".getBytes();

    public static final Long EXECUTION_TIMEOUT = 2L;

    public static final TemporalUnit EXECUTION_TIMEOUT_UNIT = TemporalUnit.SECONDS;

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
    void httpRecipientWithJsonPayloadData() throws Exception {
        ObjectNode payload = objectMapper.createObjectNode()
                .put(HTTP_PROPERTY_1, HTTP_PROPERTY_1_VALUE);
        HttpRecipient<HttpRecipientJsonPayloadData> httpRecipient = HttpRecipient.builder()
                .forJsonPayload()
                .payload(HttpRecipientJsonPayloadData.from(payload))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .url(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_JSON_PAYLOAD)
                .build();
        applyCommonValues(httpRecipient);
        Job job = Job.builder()
                .id(HTTP_JSON_JOB_ID)
                .correlationId(HTTP_JSON_JOB_ID)
                .recipient(httpRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeHttpRecipientJob(job);
    }

    @Test
    void httpRecipientWithStringPayloadData() throws Exception {
        HttpRecipient<HttpRecipientStringPayloadData> httpRecipient = HttpRecipient.builder()
                .forStringPayload()
                .payload(HttpRecipientStringPayloadData.from(HTTP_TEXT_PLAIN_VALUE))
                .header(CONTENT_TYPE, TEXT_PLAIN)
                .url(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_STRING_PAYLOAD)
                .build();
        applyCommonValues(httpRecipient);
        Job job = Job.builder()
                .id(HTTP_STRING_JOB_ID)
                .correlationId(HTTP_STRING_JOB_ID)
                .recipient(httpRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeHttpRecipientJob(job);
    }

    @Test
    void httpRecipientWithBinaryPayloadData() throws Exception {
        HttpRecipient<HttpRecipientBinaryPayloadData> httpRecipient = HttpRecipient.builder()
                .forBinaryPayload()
                .payload(HttpRecipientBinaryPayloadData.from(HTTP_BINARY_VALUE))
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM)
                .url(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_BINARY_PAYLOAD)
                .build();
        applyCommonValues(httpRecipient);
        Job job = Job.builder()
                .id(HTTP_BINARY_JOB_ID)
                .correlationId(HTTP_BINARY_JOB_ID)
                .recipient(httpRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .build();
        executeHttpRecipientJob(job);
    }

    @Test
    void httpRecipientWithCustomTimeout() throws Exception {
        HttpRecipient<HttpRecipientStringPayloadData> httpRecipient = HttpRecipient.builder()
                .forStringPayload()
                .payload(HttpRecipientStringPayloadData.from(HTTP_TEXT_PLAIN_VALUE))
                .header(CONTENT_TYPE, TEXT_PLAIN)
                .url(externalResourcesServerURL + EXTERNAL_RESOURCE_FOR_CUSTOM_TIMEOUT)
                .build();
        applyCommonValues(httpRecipient);
        Job job = Job.builder()
                .id(HTTP_JOB_WITH_CUSTOM_TIMEOUT_ID)
                .correlationId(HTTP_JOB_WITH_CUSTOM_TIMEOUT_ID)
                .recipient(httpRecipient)
                .schedule(TimerSchedule.builder()
                        .startTime(OffsetDateTime.now().plusSeconds(3))
                        .build())
                .executionTimeout(EXECUTION_TIMEOUT)
                .executionTimeoutUnit(EXECUTION_TIMEOUT_UNIT)
                .build();
        Job createdJob = executeHttpRecipientJob(job);
        assertThat(createdJob.getExecutionTimeout()).isEqualTo(EXECUTION_TIMEOUT);
        assertThat(createdJob.getExecutionTimeoutUnit()).isEqualTo(EXECUTION_TIMEOUT_UNIT);
    }

    private Job executeHttpRecipientJob(Job job) throws Exception {
        Job createdJob = createJobV2(job);
        assertThat(createdJob).isNotNull();
        assertJobHasFinishedV2(job.getId(), 60);
        return job;
    }

    private static void applyCommonValues(HttpRecipient<?> recipient) {
        recipient.addHeader(HTTP_HEADER_1, HTTP_HEADER_1_VALUE);
        recipient.addHeader(HTTP_HEADER_2, HTTP_HEADER_2_VALUE);
        recipient.addQueryParam(HTTP_QUERY_PARAM_1, HTTP_QUERY_PARAM_1_VALUE);
        recipient.setMethod("POST");
    }
}
