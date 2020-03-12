/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.management;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;

import static org.assertj.core.api.Assertions.assertThat;

public class RestJobsServiceTest {

    public static final String CALLBACK_URL = "http://localhost";
    public static final String JOB_SERVICE_URL = "http://localhost:8085";
    private final RestJobsService tested;

    public RestJobsServiceTest() {
        this.tested = new RestJobsService(JOB_SERVICE_URL, CALLBACK_URL) {
            @Override
            public String scheduleProcessJob(ProcessJobDescription description) {
                return null;
            }

            @Override
            public String scheduleProcessInstanceJob(ProcessInstanceJobDescription description) {
                return null;
            }

            @Override
            public boolean cancelJob(String id) {
                return false;
            }
        };
    }

    @Test
    void testGetCallbackEndpoint() {
        ProcessInstanceJobDescription description = ProcessInstanceJobDescription.of(123,
                                                                                     ExactExpirationTime.now(),
                                                                                     "processInstanceId",
                                                                                     "processId");
        String callbackEndpoint = tested.getCallbackEndpoint(description);
        assertThat(callbackEndpoint)
                .isEqualTo("http://localhost:80/management/jobs/processId/instances/processInstanceId/timers/" + description.id());
    }

    @Test
    void testGetJobsServiceUri() {
        URI jobsServiceUri = tested.getJobsServiceUri();
        assertThat(jobsServiceUri.toString()).isEqualTo(JOB_SERVICE_URL + "/jobs");
    }
}