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

package org.kie.kogito.jobs.management.quarkus;

import javax.enterprise.inject.Instance;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.api.Job;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VertxJobsServiceTest {

    public static final String CALLBACK_URL = "http://localhost";
    public static final String JOB_SERVICE_URL = "http://localhost:8085";

    private VertxJobsService tested;

    @Mock
    private Vertx vertx;

    @Mock
    private WebClient webClient;

    @Mock
    private Instance instance;

    @BeforeEach
    public void setUp() {
        when(instance.isResolvable()).thenReturn(true);
        when(instance.get()).thenReturn(webClient);

        tested = new VertxJobsService(JOB_SERVICE_URL, CALLBACK_URL, vertx, instance);
        tested.initialize();
    }

    @Test
    void testInitialize() {
        reset(instance);
        when(instance.isResolvable()).thenReturn(false);
        tested = new VertxJobsService(JOB_SERVICE_URL, CALLBACK_URL, vertx, instance);
        tested.initialize();
        verify(instance, never()).get();
    }

    @Test
    void testScheduleProcessJob() {
        ProcessJobDescription processJobDescription = ProcessJobDescription.of(ExactExpirationTime.now(),
                                                                               1,
                                                                               "processId");
        assertThatThrownBy(() -> tested.scheduleProcessJob(processJobDescription))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testScheduleProcessInstanceJob(@Mock HttpRequest request) {
        when(webClient.post(anyString())).thenReturn(request);

        ProcessInstanceJobDescription processInstanceJobDescription = ProcessInstanceJobDescription.of(123,
                                                                                                       ExactExpirationTime.now(),
                                                                                                       "processInstanceId",
                                                                                                       "processId");
        tested.scheduleProcessInstanceJob(processInstanceJobDescription);
        verify(webClient).post("/jobs");
        ArgumentCaptor<Job> jobArgumentCaptor = forClass(Job.class);
        verify(request).sendJson(jobArgumentCaptor.capture(), any(Handler.class));
        Job job = jobArgumentCaptor.getValue();
        assertThat(job.getId()).isEqualTo(processInstanceJobDescription.id());
        assertThat(job.getExpirationTime()).isEqualTo(processInstanceJobDescription.expirationTime().get());
        assertThat(job.getProcessInstanceId()).isEqualTo(processInstanceJobDescription.processInstanceId());
        assertThat(job.getProcessId()).isEqualTo(processInstanceJobDescription.processId());
    }

    @Test
    void testCancelJob(@Mock HttpRequest request) {
        when(webClient.delete(anyString())).thenReturn(request);
        tested.cancelJob("123");
        verify(webClient).delete("/jobs/123");
    }
}