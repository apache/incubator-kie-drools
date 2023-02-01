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

import java.net.URI;
import java.time.OffsetDateTime;

import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.Retry;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import io.cloudevents.SpecVersion;

public class TestConstants {

    public static final String ID = "ID";
    public static final SpecVersion SPEC_VERSION = SpecVersion.V1;
    public static final URI SOURCE = URI.create("http://localhost:8080/kogito-process");
    public static final URI DATA_SCHEMA = URI.create("http://localhost:8080/data-schema");
    public static final OffsetDateTime TIME = OffsetDateTime.parse("2022-12-24T18:01:15.001+01:00");
    public static final String SUBJECT = "SUBJECT";
    public static final String JOB_ID = "JOB_ID";
    public static final String CORRELATION_ID = "CORRELATION_ID";
    public static final OffsetDateTime SCHEDULE_START_TIME = OffsetDateTime.parse("2023-01-30T22:01:15.001+01:00");
    public static final int SCHEDULE_REPEAT_COUNT = 5;
    public static final long SCHEDULE_DELAY = 2;
    public static final TemporalUnit SCHEDULE_DELAY_UNIT = TemporalUnit.HOURS;

    public static final int RETRY_MAX_RETRIES = 3;
    public static final long RETRY_DELAY = 10;
    public static final TemporalUnit RETRY_DELAY_UNIT = TemporalUnit.SECONDS;
    public static final long RETRY_MAX_DURATION = 1;
    public static final TemporalUnit RETRY_DURATION_UNIT = TemporalUnit.MINUTES;

    public static final byte[] RECIPIENT_PAYLOAD = "<user><name>Michael</name><surname>Jackson</surname></user>".getBytes();
    public static final String RECIPIENT_URL = "http://bank.gateway.internal/adduser";
    public static final String RECIPIENT_METHOD = "POST";
    public static final String RECIPIENT_HEADER_1 = "Content-Type";
    public static final String RECIPIENT_HEADER_1_VALUE = "application/xml";
    public static final String RECIPIENT_QUERY_PARAM_1 = "param1";
    public static final String RECIPIENT_QUERY_PARAM_1_VALUE = "value1";
    public static final String RECIPIENT_QUERY_PARAM_2 = "param2";
    public static final String RECIPIENT_QUERY_PARAM_2_VALUE = "value2";

    public static Job buildJob() {
        return Job.builder()
                .correlationId(CORRELATION_ID)
                .retry(Retry.builder()
                        .maxRetries(RETRY_MAX_RETRIES)
                        .delay(RETRY_DELAY)
                        .delayUnit(RETRY_DELAY_UNIT)
                        .maxDuration(RETRY_MAX_DURATION)
                        .durationUnit(RETRY_DURATION_UNIT)
                        .build())
                .schedule(TimerSchedule.builder()
                        .startTime(SCHEDULE_START_TIME)
                        .repeatCount(SCHEDULE_REPEAT_COUNT)
                        .delay(SCHEDULE_DELAY)
                        .delayUnit(SCHEDULE_DELAY_UNIT)
                        .build())
                .recipient(HttpRecipient.builder()
                        .forBinaryPayload()
                        .payload(HttpRecipientBinaryPayloadData.from(RECIPIENT_PAYLOAD))
                        .url(RECIPIENT_URL)
                        .method(RECIPIENT_METHOD)
                        .header(RECIPIENT_HEADER_1, RECIPIENT_HEADER_1_VALUE)
                        .queryParam(RECIPIENT_QUERY_PARAM_1, RECIPIENT_QUERY_PARAM_1_VALUE)
                        .queryParam(RECIPIENT_QUERY_PARAM_2, RECIPIENT_QUERY_PARAM_2_VALUE)
                        .build())
                .build();
    }
}
