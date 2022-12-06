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

package org.kie.kogito.jobs.service.api.event.serialization;

import java.net.URI;
import java.time.OffsetDateTime;

import org.kie.kogito.jobs.service.api.TemporalUnit;

import io.cloudevents.SpecVersion;

public class SerializationTestConstants {

    static final String ID = "ID";
    static final SpecVersion SPEC_VERSION = SpecVersion.V1;
    static final URI SOURCE = URI.create("http://localhost:8080/kogito-process");
    static final OffsetDateTime TIME = OffsetDateTime.parse("2022-12-24T18:01:15.001+01:00");
    static final String SUBJECT = "SUBJECT";
    static final String JOB_ID = "JOB_ID";
    static final String CORRELATION_ID = "CORRELATION_ID";
    static final String SCHEDULE_START_TIME = "2023-01-30T22:01:15.001+01:00";
    static final int SCHEDULE_REPEAT_COUNT = 5;
    static final long SCHEDULE_DELAY = 2;
    static final TemporalUnit SCHEDULE_DELAY_UNIT = TemporalUnit.HOURS;

    static final int RETRY_MAX_RETRIES = 3;
    static final long RETRY_DELAY = 10;
    static final TemporalUnit RETRY_DELAY_UNIT = TemporalUnit.SECONDS;
    static final long RETRY_MAX_DURATION = 1;
    static final TemporalUnit RETRY_DURATION_UNIT = TemporalUnit.MINUTES;

    static final byte[] RECIPIENT_PAYLOAD = "<user><name>Michael</name><surname>Jackson</surname></user>".getBytes();
    static final String RECIPIENT_URL = "http://bank.gateway.internal/adduser";
    static final String RECIPIENT_METHOD = "POST";
    static final String RECIPIENT_HEADER_1 = "Content-Type";
    static final String RECIPIENT_HEADER_1_VALUE = "application/xml";
    static final String RECIPIENT_QUERY_PARAM_1 = "param1";
    static final String RECIPIENT_QUERY_PARAM_1_VALUE = "value1";
    static final String RECIPIENT_QUERY_PARAM_2 = "param2";
    static final String RECIPIENT_QUERY_PARAM_2_VALUE = "value2";
}
