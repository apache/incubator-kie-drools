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
package org.kie.kogito.jobs.management.quarkus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.api.JobCallbackPayload;
import org.kie.kogito.jobs.management.RestJobsServiceTest;
import org.kie.kogito.jobs.service.api.Job;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;

import jakarta.enterprise.inject.Instance;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VertxJobsServiceTest extends RestJobsServiceTest<VertxJobsService> {

    @Mock
    private Vertx vertx;

    @Mock
    private WebClient webClient;

    @Mock
    private Instance instance;

    @Mock
    private HttpRequest<Buffer> request;

    @Mock
    private ObjectMapper objectMapper;

    @Override
    public VertxJobsService createJobService(String jobServiceUrl, String callbackUrl) {
        when(instance.isResolvable()).thenReturn(true);
        when(instance.get()).thenReturn(webClient);
        VertxJobsService jobsService = new VertxJobsService(jobServiceUrl, callbackUrl, vertx, instance, objectMapper);
        jobsService.initialize();
        return jobsService;
    }

    @Test
    void initialize() {
        reset(instance);
        when(instance.isResolvable()).thenReturn(false);
        tested = new VertxJobsService(JOB_SERVICE_URL, CALLBACK_URL, vertx, instance, objectMapper);
        tested.initialize();
        verify(instance, never()).get();
    }

    @Test
    void scheduleProcessInstanceJob() {
        when(webClient.post(anyString())).thenReturn(request);
        when(objectMapper.valueToTree(any(JobCallbackPayload.class))).thenReturn(JSON_PAYLOAD);
        ProcessInstanceJobDescription processInstanceJobDescription = buildProcessInstanceJobDescription();
        tested.scheduleProcessInstanceJob(processInstanceJobDescription);
        verify(webClient).post("/v2/jobs");
        ArgumentCaptor<Job> jobArgumentCaptor = forClass(Job.class);
        verify(request).sendJson(jobArgumentCaptor.capture(), any(Handler.class));
        Job job = jobArgumentCaptor.getValue();
        assertExpectedJob(job, processInstanceJobDescription.id());
    }

    @Test
    void cancelJob() {
        when(webClient.delete(anyString())).thenReturn(request);
        tested.cancelJob(JOB_ID);
        verify(webClient).delete("/v2/jobs/" + JOB_ID);
    }
}
