/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.api.event;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.api.event.CancelJobRequestEvent.CANCEL_JOB_REQUEST;
import static org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST;

class CreateProcessInstanceJobRequestEventTest extends AbstractProcessInstanceContextJobCloudEventTest<CreateProcessInstanceJobRequestEvent> {

    static final String JOB_ID = "JOB_ID";
    static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    static final ZonedDateTime EXPIRATION_TIME = ZonedDateTime.parse("2021-11-25T19:00:00.000+01:00");
    static final Integer PRIORITY = 1;
    static final String CALLBACK_ENDPOINT = "http://localhost:8080/management/jobs/" + PROCESS_ID + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + NODE_INSTANCE_ID;
    static final long REPEAT_INTERVAL = 6000;
    static final int REPEAT_LIMIT = 3;

    @Override
    CreateProcessInstanceJobRequestEvent buildEvent() {
        return CreateProcessInstanceJobRequestEvent.builder()
                .id(ID)
                .type(CREATE_PROCESS_INSTANCE_JOB_REQUEST)
                .specVersion(SPEC_VERSION)
                .source(SOURCE)
                .time(TIME)
                .subject(SUBJECT)
                .dataContentType(DATA_CONTENT_TYPE)
                .dataSchema(DATA_SCHEMA)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .kogitoAddons(ADDONS)
                .job(new Job(JOB_ID,
                        EXPIRATION_TIME,
                        PRIORITY,
                        CALLBACK_ENDPOINT,
                        PROCESS_INSTANCE_ID,
                        ROOT_PROCESS_INSTANCE_ID,
                        PROCESS_ID,
                        ROOT_PROCESS_ID,
                        REPEAT_INTERVAL,
                        REPEAT_LIMIT,
                        NODE_INSTANCE_ID))
                .build();
    }

    @Override
    String eventType() {
        return CREATE_PROCESS_INSTANCE_JOB_REQUEST;
    }

    @Override
    void assertFields(CreateProcessInstanceJobRequestEvent event) {
        super.assertFields(event);
        Job job = event.getData();
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(JOB_ID);
        assertThat(job.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(job.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(job.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(job.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(job.getNodeInstanceId()).isEqualTo(NODE_INSTANCE_ID);
        assertThat(job.getExpirationTime()).isEqualTo(EXPIRATION_TIME);
        assertThat(job.getCallbackEndpoint()).isEqualTo(CALLBACK_ENDPOINT);
        assertThat(job.getRepeatInterval()).isEqualTo(REPEAT_INTERVAL);
        assertThat(job.getRepeatLimit()).isEqualTo(REPEAT_LIMIT);
        assertThat(job.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    void testDefaultValues() {
        CancelJobRequestEvent event = CancelJobRequestEvent.builder().build();
        assertThat(event.getSpecVersion()).isNotNull();
        assertThat(event.getType()).isEqualTo(CANCEL_JOB_REQUEST);
        assertThat(event.getTime()).isNotNull();
    }
}
