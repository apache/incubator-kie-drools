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
package org.kie.kogito.job.http.recipient;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.job.http.recipient.converters.HttpConverters;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.model.job.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private WebClient webClient;

    @Test
    void testInitialize(@Mock io.vertx.core.Vertx vertxCore) {
        Mockito.when(vertx.getDelegate()).thenReturn(vertxCore);
        tested.initialize();
        assertNotNull(tested.getClient());
    }

    @Test
    void testExecutePeriodic(@Mock HttpRequest<Buffer> request, @Mock MultiMap params) {
        JobDetails scheduledJob =
                JobDetails.builder()
                        .id(JOB_ID)
                        .recipient(new HTTPRecipient(ENDPOINT))
                        .trigger(ScheduledJobAdapter.intervalTrigger(DateUtil.now(), 10, 1))
                        .build();
        Map queryParams = assertExecuteAndReturnQueryParams(request, params, scheduledJob, false);
        assertThat(queryParams).hasSize(1).containsEntry("limit", "8");//repeatCount is init in 1
    }

    private Map assertExecuteAndReturnQueryParams(@Mock HttpRequest<Buffer> request, @Mock MultiMap params,
            JobDetails scheduledJob, boolean mockError) {
        Mockito.when(webClient.request(HttpMethod.POST, 8080, "localhost", "/endpoint")).thenReturn(request);
        Mockito.when(request.queryParams()).thenReturn(params);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponse.statusCode()).thenReturn(mockError ? 500 : 200);
        Mockito.when(request.send()).thenReturn(Uni.createFrom().item(httpResponse));

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        JobExecutionResponse response = tested.execute(scheduledJob).onFailure().recoverWithNull().await().indefinitely();
        Mockito.verify(webClient).request(HttpMethod.POST, 8080, "localhost", "/endpoint");
        Mockito.verify(request).queryParams();
        Mockito.verify(params).addAll(mapCaptor.capture());
        Mockito.verify(request).send();
        if (!mockError) {
            assertThat(response.getJobId()).isEqualTo(JOB_ID);
            assertThat(response.getCode()).isEqualTo("200");
        } else {
            assertThat(response).isNull();//since recover with null
        }
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
        return JobDetails.builder().recipient(new HTTPRecipient(ENDPOINT)).id(JOB_ID).build();
    }
}
