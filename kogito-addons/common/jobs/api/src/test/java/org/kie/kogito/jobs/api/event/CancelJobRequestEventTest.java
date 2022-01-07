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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.api.event.CancelJobRequestEvent.CANCEL_JOB_REQUEST;

class CancelJobRequestEventTest extends AbstractProcessInstanceContextJobCloudEventTest<CancelJobRequestEvent> {

    private static final String JOB_ID = "JOB_ID";

    @Override
    CancelJobRequestEvent buildEvent() {
        return CancelJobRequestEvent.builder()
                .id(ID)
                .type(CANCEL_JOB_REQUEST)
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
                .jobId(JOB_ID)
                .build();
    }

    @Override
    String eventType() {
        return CANCEL_JOB_REQUEST;
    }

    @Override
    void assertFields(CancelJobRequestEvent event) {
        super.assertFields(event);
        assertThat(event.getData().getId()).isEqualTo(JOB_ID);
    }

    @Test
    void testDefaultValues() {
        CancelJobRequestEvent event = CancelJobRequestEvent.builder().build();
        assertThat(event.getSpecVersion()).isNotNull();
        assertThat(event.getType()).isEqualTo(CANCEL_JOB_REQUEST);
        assertThat(event.getTime()).isNotNull();
    }

    @Test
    void jobId() {
        CancelJobRequestEvent.JobId jobId = new CancelJobRequestEvent.JobId(JOB_ID);
        assertThat(jobId.getId()).isEqualTo(JOB_ID);
    }
}
