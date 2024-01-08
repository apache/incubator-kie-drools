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
package org.kie.kogito.jobs.embedded;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

@QuarkusTest
public class EmbeddedJobsServiceTests {

    @Inject
    JobsService jobService;

    @Inject
    TestEventPublisher publisher;

    @Test
    public void testJobService() throws Exception {
        // testing only we have the full lifecycle
        publisher.expectedEvents(2);

        ProcessInstanceJobDescription description = ProcessInstanceJobDescription.builder()
                .generateId()
                .timerId("-1")
                .expirationTime(DurationExpirationTime.now())
                .processInstanceId("1")
                .rootProcessInstanceId(null)
                .processId("processId")
                .rootProcessId(null)
                .nodeInstanceId("node_1")
                .build();
        jobService.scheduleProcessInstanceJob(description);

        List<DataEvent<?>> events = publisher.getEvents();
        Assertions.assertEquals(2, events.size());

    }

}
