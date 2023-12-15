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

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.api.serlialization.SerializationUtils;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class JobCallbackResourceDefTest {

    private static final URI SERVICE_URI = URI.create("http://myService.com:8080");
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final Integer PRIORITY = 0;
    private static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    private static final ExpirationTime EXPIRATION_TIME = ExactExpirationTime.of("2020-03-21T10:15:30+01:00");
    private static final String JOB_ID = "JOB_ID";

    private static final String TIMER_ID = "TIMER_ID";
    private static final String CALLBACK = "CALLBACK";

    private static final String EXPECTED_CALLBACK_URI = SERVICE_URI + "/management/jobs/" + PROCESS_ID
            + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + TIMER_ID;

    @Test
    void buildCallbackURI() {
        String callbackURI = JobCallbackResourceDef.buildCallbackURI(mockProcessInstanceJobDescription(), SERVICE_URI.toString());
        assertThat(callbackURI).isEqualTo(EXPECTED_CALLBACK_URI);
    }

    @Test
    void buildCallbackPatternJob() {
        org.kie.kogito.jobs.service.api.Job job = JobCallbackResourceDef.buildCallbackPatternJob(mockProcessInstanceJobDescription(), CALLBACK, SerializationUtils.DEFAULT_OBJECT_MAPPER);
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(JOB_ID);
        assertThat(job.getRecipient())
                .isNotNull()
                .isInstanceOf(HttpRecipient.class);
        HttpRecipient<?> httpRecipient = (HttpRecipient<?>) job.getRecipient();
        assertThat(httpRecipient.getMethod()).isEqualTo("POST");
        assertThat(httpRecipient.getUrl()).isEqualTo(CALLBACK);
        assertThat(httpRecipient.getHeaders())
                .hasSize(6)
                .containsEntry(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .containsEntry("processId", PROCESS_ID)
                .containsEntry("processInstanceId", PROCESS_INSTANCE_ID)
                .containsEntry("rootProcessId", ROOT_PROCESS_ID)
                .containsEntry("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
                .containsEntry("nodeInstanceId", NODE_INSTANCE_ID);
        assertThat(httpRecipient.getPayload()).isNotNull();
        assertThat(httpRecipient.getPayload().getData()).isNotNull();
        assertThat(httpRecipient.getPayload().getData()).isInstanceOf(JsonNode.class);
        JsonNode json = (JsonNode) httpRecipient.getPayload().getData();
        assertThat(json.get("correlationId").asText()).isEqualTo(JOB_ID);
        assertThat(job.getSchedule())
                .isNotNull()
                .isInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) job.getSchedule();
        assertThat(timerSchedule.getStartTime()).isEqualTo(EXPIRATION_TIME.get().toOffsetDateTime());
    }

    private ProcessInstanceJobDescription mockProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.builder()
                .id(JOB_ID)
                .timerId(TIMER_ID)
                .expirationTime(EXPIRATION_TIME)
                .priority(PRIORITY)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .nodeInstanceId(NODE_INSTANCE_ID)
                .build();
    }
}
