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

package org.kie.kogito.jobs.service.api.event;

import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.Retry;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.api.event.TestConstants.CORRELATION_ID;
import static org.kie.kogito.jobs.service.api.event.TestConstants.DATA_SCHEMA;
import static org.kie.kogito.jobs.service.api.event.TestConstants.ID;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_HEADER_1;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_HEADER_1_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_METHOD;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_PAYLOAD;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_1;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_1_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_2;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_2_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_URL;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RETRY_DELAY;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RETRY_DELAY_UNIT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RETRY_DURATION_UNIT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RETRY_MAX_DURATION;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RETRY_MAX_RETRIES;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SOURCE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SUBJECT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.TIME;
import static org.kie.kogito.jobs.service.api.event.TestConstants.buildJob;

class CreateJobEventTest extends AbstractJobCloudEventTest<CreateJobEvent> {

    @Override
    CreateJobEvent buildEvent() {
        return CreateJobEvent.builder()
                .id(ID)
                .source(SOURCE)
                .dataSchema(DATA_SCHEMA)
                .time(TIME)
                .subject(SUBJECT)
                .job(buildJob())
                .build();
    }

    @Override
    String eventType() {
        return CreateJobEvent.TYPE;
    }

    @Override
    void assertFields(CreateJobEvent event) {
        super.assertFields(event);
        Job job = event.getData();
        assertThat(job).isNotNull();
        assertThat(job.getCorrelationId()).isEqualTo(CORRELATION_ID);

        assertThat(job.getRetry()).isNotNull();
        Retry retry = job.getRetry();
        assertThat(retry.getMaxRetries()).isEqualTo(RETRY_MAX_RETRIES);
        assertThat(retry.getDelay()).isEqualTo(RETRY_DELAY);
        assertThat(retry.getDelayUnit()).isEqualTo(RETRY_DELAY_UNIT);
        assertThat(retry.getMaxDuration()).isEqualTo(RETRY_MAX_DURATION);
        assertThat(retry.getDurationUnit()).isEqualTo(RETRY_DURATION_UNIT);

        assertThat(job.getRecipient()).isInstanceOf(HttpRecipient.class);
        HttpRecipient<?> recipient = (HttpRecipient<?>) job.getRecipient();
        assertThat(recipient.getUrl()).isEqualTo(RECIPIENT_URL);
        assertThat(recipient.getMethod()).isEqualTo(RECIPIENT_METHOD);
        assertThat(recipient.getPayload()).isNotNull();
        assertThat(recipient.getPayload().getData()).isEqualTo(RECIPIENT_PAYLOAD);
        assertThat(recipient.getHeaders())
                .hasSize(1)
                .containsEntry(RECIPIENT_HEADER_1, RECIPIENT_HEADER_1_VALUE);
        assertThat(recipient.getQueryParams())
                .hasSize(2)
                .containsEntry(RECIPIENT_QUERY_PARAM_1, RECIPIENT_QUERY_PARAM_1_VALUE)
                .containsEntry(RECIPIENT_QUERY_PARAM_2, RECIPIENT_QUERY_PARAM_2_VALUE);
    }
}
