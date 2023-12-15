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
package org.kie.kogito.jobs.api;

import java.time.temporal.ChronoUnit;

import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

/**
 * Common definitions for add-ons implementations based on the Jobs Service to Runtime rest callback pattern.
 */
public class JobCallbackResourceDef {

    public static final String PROCESS_ID = "processId";

    public static final String PROCESS_INSTANCE_ID = "processInstanceId";

    public static final String ROOT_PROCESS_ID = "rootProcessId";

    public static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";

    public static final String NODE_INSTANCE_ID = "nodeInstanceId";

    public static final String TIMER_ID = "timerId";

    public static final String LIMIT = "limit";

    public static final String LIMIT_DEFAULT_VALUE = "0";

    public static final String JOBS_CALLBACK_URI = "/management/jobs";

    public static final String JOBS_CALLBACK_POST_URI = "{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/timers/{" + TIMER_ID + "}";

    private JobCallbackResourceDef() {
    }

    public static String buildCallbackURI(ProcessInstanceJobDescription description, String jobsCallbackEndpoint) {
        return URIBuilder.toURI(jobsCallbackEndpoint
                + JOBS_CALLBACK_URI + "/"
                + description.processId()
                + "/instances/"
                + description.processInstanceId()
                + "/timers/"
                + description.timerId())
                .toString();
    }

    public static org.kie.kogito.jobs.service.api.Job buildCallbackPatternJob(ProcessInstanceJobDescription description,
            String callback, ObjectMapper objectMapper) {
        return org.kie.kogito.jobs.service.api.Job.builder()
                .id(description.id())
                .correlationId(description.id())
                .recipient(buildRecipient(description, callback, objectMapper))
                .schedule(buildSchedule(description))
                .build();
    }

    private static HttpRecipient<HttpRecipientJsonPayloadData> buildRecipient(ProcessInstanceJobDescription description, String callback, ObjectMapper objectMapper) {
        return HttpRecipient.builder()
                .forJsonPayload()
                .payload(HttpRecipientJsonPayloadData.from(buildPayload(description, objectMapper)))
                .url(callback)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(PROCESS_ID, description.processId())
                .header(PROCESS_INSTANCE_ID, description.processInstanceId())
                .header(ROOT_PROCESS_ID, description.rootProcessId())
                .header(ROOT_PROCESS_INSTANCE_ID, description.rootProcessInstanceId())
                .header(NODE_INSTANCE_ID, description.nodeInstanceId())
                .build();
    }

    private static JsonNode buildPayload(ProcessInstanceJobDescription description, ObjectMapper objectMapper) {
        return objectMapper.valueToTree(new JobCallbackPayload(description.id()));
    }

    private static TimerSchedule buildSchedule(ProcessInstanceJobDescription description) {
        return TimerSchedule.builder()
                .startTime(description.expirationTime().get().toOffsetDateTime().truncatedTo(ChronoUnit.MILLIS))
                .repeatCount(translateLimit(description.expirationTime().repeatLimit()))
                .delay(description.expirationTime().repeatInterval())
                .delayUnit(TemporalUnit.MILLIS)
                .build();
    }

    /**
     * Translate the repeatLimit coming from the process format into repetitions in the public API TimerSchedule.
     */
    private static int translateLimit(int repeatLimit) {
        if (repeatLimit < 0) {
            throw new IllegalArgumentException("The repeatLimit must be greater or equal han zero, but is: " + repeatLimit);
        }
        if (repeatLimit >= 1) {
            return repeatLimit - 1;
        }
        return repeatLimit;
    }
}
