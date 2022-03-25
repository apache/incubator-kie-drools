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

package org.kie.kogito.jobs.api;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.TimerJobId;

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
    private static final TimerJobId JOB_ID = new TimerJobId(1L);
    private static final String CALLBACK = "CALLBACK";

    private static final String EXPECTED_CALLBACK_URI = SERVICE_URI + "/management/jobs/" + PROCESS_ID
            + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + JOB_ID.encode();

    @Test
    void buildCallbackURI() {
        String callbackURI = JobCallbackResourceDef.buildCallbackURI(mockProcessInstanceJobDescription(), SERVICE_URI.toString());
        assertThat(callbackURI).isEqualTo(EXPECTED_CALLBACK_URI);
    }

    @Test
    void buildCallbackPatternJob() {
        Job job = JobCallbackResourceDef.buildCallbackPatternJob(mockProcessInstanceJobDescription(), CALLBACK);
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(JOB_ID.encode());
        assertThat(job.getExpirationTime()).isEqualTo(EXPIRATION_TIME.get());
        assertThat(job.getRepeatLimit()).isZero();
        assertThat(job.getRepeatInterval()).isNull();
        assertThat(job.getPriority()).isEqualTo(PRIORITY);
        assertThat(job.getCallbackEndpoint()).isEqualTo(CALLBACK);
        assertThat(job.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(job.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(job.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(job.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
    }

    private ProcessInstanceJobDescription mockProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.of(JOB_ID,
                EXPIRATION_TIME,
                PRIORITY,
                PROCESS_INSTANCE_ID,
                ROOT_PROCESS_INSTANCE_ID, PROCESS_ID,
                ROOT_PROCESS_ID,
                NODE_INSTANCE_ID);
    }
}
