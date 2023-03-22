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

package org.kie.kogito.job.sink.recipient;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.kie.kogito.job.recipient.common.http.HTTPRequestExecutorTest;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cloudevents.SpecVersion;
import io.vertx.mutiny.core.Vertx;

import static org.assertj.core.api.Assertions.assertThat;

class SinkJobExecutorTest extends HTTPRequestExecutorTest<SinkRecipient<?>, SinkJobExecutor> {

    public static final String JOB_CE_TYPE = "JOB_CE_TYPE";
    public static final SpecVersion JOB_CE_SPECVERSION = SpecVersion.V1;
    public static final URI JOB_CE_SOURCE = URI.create("http://JOB_CE_SOURCE");
    public static final String JOB_CE_SUBJECT = "JOB_CE_SUBJECT";
    public static final String JOB_CE_DATACONTENTTYPE = "JOB_CE_DATACONTENTTYPE";
    public static final URI JOB_CE_DATASCHEMA = URI.create("http://JOB_CE_DATASCHEMA");
    public static final String PROPERTY_NAME = "PROPERTY_NAME";
    public static final String PROPERTY_VALUE = "PROPERTY_VALUE";

    @Override
    protected SinkJobExecutor createExecutor(long timeout, Vertx vertx, ObjectMapper objectMapper) {
        return new SinkJobExecutor(timeout, vertx, objectMapper);
    }

    @Override
    protected void assertExecuteConditions() {
        assertThat(queryParamsCaptor.getValue()).isEmpty();
        assertThat(headersCaptor.getValue()).hasSize(9);
        assertCommonHeaders(headersCaptor.getValue());
        assertThat(headersCaptor.getValue()).containsEntry("ce-limit", "0");
        assertCommonBuffer();
    }

    @Override
    protected void assertExecuteWithErrorConditions() {
        assertExecuteConditions();
    }

    @Override
    protected void assertExecutePeriodicConditions() {
        assertThat(queryParamsCaptor.getValue()).isEmpty();
        assertThat(headersCaptor.getValue()).hasSize(9);
        assertCommonHeaders(headersCaptor.getValue());
        assertThat(headersCaptor.getValue()).containsEntry("ce-limit", "10");
        assertCommonBuffer();
    }

    private void assertCommonHeaders(Map<String, String> headers) {
        assertThat(headers.get("ce-id")).isNotNull();
        assertThat(headers.get("ce-time")).isNotNull();
        assertThat(headers).containsEntry("ce-source", JOB_CE_SOURCE.toString());
        assertThat(headers).containsEntry("ce-subject", JOB_CE_SUBJECT);
        assertThat(headers).containsEntry("ce-specversion", JOB_CE_SPECVERSION.toString());
        assertThat(headers).containsEntry("ce-type", JOB_CE_TYPE);
        assertThat(headers).containsEntry("content-type", JOB_CE_DATACONTENTTYPE);
        assertThat(headers).containsEntry("ce-dataschema", JOB_CE_DATASCHEMA.toString());
    }

    private void assertCommonBuffer() {
        assertThat(bufferCaptor.getValue()).isNotNull()
                .hasToString("{\"PROPERTY_NAME\":\"PROPERTY_VALUE\"}");
    }

    @Override
    protected JobDetails createSimpleJob() {
        SinkRecipient<?> recipient = createRecipient();
        return JobDetails.builder()
                .recipient(new RecipientInstance(recipient))
                .id(JOB_ID)
                .build();
    }

    @Override
    protected JobDetails createPeriodicJob() {
        SinkRecipient<?> recipient = createRecipient();
        return JobDetails.builder()
                .id(JOB_ID)
                .recipient(new RecipientInstance(recipient))
                .trigger(new SimpleTimerTrigger(DateUtil.toDate(OffsetDateTime.now()), 1, ChronoUnit.MILLIS, 10, null))
                .build();
    }

    private SinkRecipient<?> createRecipient() {
        ObjectNode json = objectMapper.createObjectNode().put(PROPERTY_NAME, PROPERTY_VALUE);
        return SinkRecipient.builder().forJsonPayload()
                .payload(SinkRecipientJsonPayloadData.from(json))
                .sinkUrl(ENDPOINT)
                .contentMode(SinkRecipient.ContentMode.BINARY)
                .ceSpecVersion(JOB_CE_SPECVERSION)
                .ceEventType(JOB_CE_TYPE)
                .ceSource(JOB_CE_SOURCE)
                .ceDataContentType(JOB_CE_DATACONTENTTYPE)
                .ceDataSchema(JOB_CE_DATASCHEMA)
                .ceSubject(JOB_CE_SUBJECT)
                .build();
    }
}
