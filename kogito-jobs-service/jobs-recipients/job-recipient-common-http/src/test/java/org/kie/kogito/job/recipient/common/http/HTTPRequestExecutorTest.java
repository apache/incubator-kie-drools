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
package org.kie.kogito.job.recipient.common.http;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.serialization.SerializationUtils;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public abstract class HTTPRequestExecutorTest<R extends Recipient<?>, E extends HTTPRequestExecutor<R>> {

    public static final long DEFAULT_TIMEOUT = 5000;
    public static final int PORT = 8080;
    public static final String HOST = "localhost";
    public static final String PATH = "/my-service";
    public static final String ENDPOINT = "http://" + HOST + ":" + PORT + PATH;
    public static final String JOB_ID = "JOB_ID";
    public static final String JOB_DATA = "JOB_DATA";

    @Mock
    protected Vertx vertx;

    @Mock
    protected WebClient webClient;

    @Mock
    protected HttpRequest<Buffer> request;

    @Mock
    protected MultiMap params;

    @Mock
    protected MultiMap headers;

    @Captor
    protected ArgumentCaptor<Map<String, String>> queryParamsCaptor;

    @Captor
    protected ArgumentCaptor<Map<String, String>> headersCaptor;

    @Captor
    protected ArgumentCaptor<Buffer> bufferCaptor;

    protected ObjectMapper objectMapper;

    protected E tested;

    @BeforeEach
    void setUp() {
        objectMapper = SerializationUtils.DEFAULT_OBJECT_MAPPER;
        tested = spy(createExecutor(DEFAULT_TIMEOUT, vertx, objectMapper));
        doReturn(webClient).when(tested).createClient();
        tested.initialize();
    }

    protected abstract E createExecutor(long timeout, Vertx vertx, ObjectMapper objectMapper);

    @Test
    void testExecute() {
        JobDetails job = createSimpleJob();
        executeAndCollectRequestInfo(request, params, headers, job, false);
        assertExecuteConditions();
        assertTimeout(DEFAULT_TIMEOUT);
    }

    @Test
    void testExecuteWithCustomTimeout() {
        JobDetails job = spy(createSimpleJob());
        doReturn(2L).when(job).getExecutionTimeout();
        doReturn(ChronoUnit.SECONDS).when(job).getExecutionTimeoutUnit();
        executeAndCollectRequestInfo(request, params, headers, job, false);
        assertExecuteConditions();
        assertTimeout(2000L);
    }

    protected abstract void assertExecuteConditions();

    @Test
    void testExecuteWithError() {
        JobDetails job = createSimpleJob();
        executeAndCollectRequestInfo(request, params, headers, job, true);
        assertExecuteWithErrorConditions();
    }

    protected abstract void assertExecuteWithErrorConditions();

    @Test
    void testExecutePeriodic() {
        JobDetails job = createPeriodicJob();
        executeAndCollectRequestInfo(request, params, headers, job, false);
        assertExecutePeriodicConditions();
    }

    protected abstract void assertExecutePeriodicConditions();

    protected abstract JobDetails createSimpleJob();

    protected abstract JobDetails createPeriodicJob();

    @SuppressWarnings("unchecked")
    private Map<String, String>[] executeAndCollectRequestInfo(HttpRequest<Buffer> request, MultiMap params, MultiMap headers,
            JobDetails scheduledJob, boolean mockError) {
        doReturn(request).when(webClient).requestAbs(HttpMethod.POST, ENDPOINT);
        doReturn(request).when(request).timeout(anyLong());
        doReturn(params).when(request).queryParams();
        doReturn(headers).when(request).headers();

        HttpResponse<Buffer> httpResponse = mock(HttpResponse.class);
        int statusCode = mockError ? 500 : 200;
        doReturn(statusCode).when(httpResponse).statusCode();
        doReturn(Uni.createFrom().item(httpResponse)).when(request).sendBuffer(any());

        JobExecutionResponse response = tested.execute(scheduledJob).onFailure().recoverWithNull().await().indefinitely();
        verify(webClient).requestAbs(HttpMethod.POST, ENDPOINT);
        verify(request).sendBuffer(bufferCaptor.capture());
        verify(request).queryParams();
        verify(request).headers();
        verify(params).addAll(queryParamsCaptor.capture());
        verify(headers).addAll(headersCaptor.capture());

        verify(request).sendBuffer(any());
        if (!mockError) {
            assertThat(response.getJobId()).isEqualTo(JOB_ID);
            assertThat(response.getCode()).isEqualTo("200");
        } else {
            assertThat(response).isNull();//since recover with null
        }
        return new Map[] { headersCaptor.getValue(), queryParamsCaptor.getValue() };
    }

    private void assertTimeout(long expectedTimeout) {
        verify(request).timeout(expectedTimeout);
    }
}
