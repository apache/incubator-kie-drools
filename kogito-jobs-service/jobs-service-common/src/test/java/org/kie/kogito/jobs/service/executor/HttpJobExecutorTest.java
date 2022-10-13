/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.executor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.converters.HttpConverters;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.model.job.Recipient;
import org.kie.kogito.jobs.service.model.job.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpJobExecutorTest {

    public static final String ENDPOINT = "http://localhost:8080/endpoint";
    public static final String JOB_ID = UUID.randomUUID().toString();

    @InjectMocks
    private HttpJobExecutor tested;

    @Mock
    private Vertx vertx;

    @Spy
    private HttpConverters httpConverters = new HttpConverters();

    @Mock
    private JobStreams jobStreams;

    @Mock
    private WebClient webClient;

    @Test
    void testInitialize(@Mock io.vertx.core.Vertx vertxCore) {
        when(vertx.getDelegate()).thenReturn(vertxCore);
        tested.initialize();
        assertNotNull(tested.getClient());
    }

    @Test
    void testExecutePeriodic(@Mock HttpRequest<Buffer> request, @Mock MultiMap params) {
        JobDetails scheduledJob =
                JobDetails.builder()
                        .id(JOB_ID)
                        .recipient(new Recipient.HTTPRecipient(ENDPOINT))
                        .trigger(ScheduledJobAdapter.intervalTrigger(DateUtil.now(), 10, 1))
                        .executionCounter(1).build();

        Map queryParams = assertExecuteAndReturnQueryParams(request, params, scheduledJob, false);
        assertThat(queryParams).hasSize(1).containsEntry("limit", "9");
    }

    private Map assertExecuteAndReturnQueryParams(@Mock HttpRequest<Buffer> request, @Mock MultiMap params,
            JobDetails scheduledJob, boolean mockError) {
        when(webClient.request(HttpMethod.POST, 8080, "localhost", "/endpoint")).thenReturn(request);
        when(request.queryParams()).thenReturn(params);
        HttpResponse response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(mockError ? 500 : 200);
        when(request.send()).thenReturn(Uni.createFrom().item(response));

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<JobExecutionResponse> responseCaptor = ArgumentCaptor.forClass(JobExecutionResponse.class);

        tested.execute(CompletableFuture.completedFuture(scheduledJob));
        verify(webClient).request(HttpMethod.POST, 8080, "localhost", "/endpoint");
        verify(request).queryParams();
        verify(params).addAll(mapCaptor.capture());
        verify(request).send();
        JobExecutionResponse jobExecutionResponse = mockError
                ? verify(jobStreams).publishJobError(responseCaptor.capture())
                : verify(jobStreams).publishJobSuccess(responseCaptor.capture());
        JobExecutionResponse value = responseCaptor.getValue();
        assertThat(value.getJobId()).isEqualTo(JOB_ID);
        assertThat(value.getCode()).isEqualTo(mockError ? "500" : "200");
        return mapCaptor.getValue();
    }

    @Test
    void testExecute(@Mock HttpRequest<Buffer> request, @Mock MultiMap params) {
        JobDetails job = createSimpleJob();

        Map queryParams = assertExecuteAndReturnQueryParams(request, params, job, false);
        assertThat(queryParams).isEmpty();
    }

    @Test
    void testExecuteWithError(@Mock HttpRequest<Buffer> request, @Mock MultiMap params) {
        JobDetails job = createSimpleJob();

        Map queryParams = assertExecuteAndReturnQueryParams(request, params, job, true);
        assertThat(queryParams).isEmpty();
    }

    private JobDetails createSimpleJob() {
        return JobDetails.builder().recipient(new Recipient.HTTPRecipient(ENDPOINT)).id(JOB_ID).build();
    }
}
