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
package org.kie.kogito.index.addon.api;

import java.nio.Buffer;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.addon.source.files.SourceFilesProvider;
import org.kie.kogito.index.api.KogitoRuntimeCommonClient;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.svg.ProcessSvgService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import jakarta.enterprise.inject.Instance;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KogitoAddonRuntimeClientImplTest {

    private static int ACTIVE = 1;
    private static int ERROR = 5;
    private static String SERVICE_URL = "http://runtimeURL.com";
    private static String PROCESS_INSTANCE_ID = "pId";
    private static final String NODE_ID = "nodeId";
    private static String TASK_ID = "taskId";
    private static String JOB_ID = "jobId";
    private static String AUTHORIZED_TOKEN = "authToken";

    @Mock
    public Vertx vertx;

    @Mock
    private SecurityIdentity identityMock;

    private TokenCredential tokenCredential;

    @Mock
    private WebClient webClientMock;

    @Mock
    private HttpRequest httpRequestMock;

    public static final String NODE_ID_ERROR = "processInstanceIdError";

    private KogitoAddonRuntimeClientImpl client;

    @Mock
    Instance<ProcessSvgService> processSvgServiceInstance;

    @Mock
    private ProcessSvgService processSvgService;

    @Mock
    private SourceFilesProvider sourceFilesProvider;

    @Mock
    Instance<Processes> processesInstance;

    @Mock
    private Processes processes;

    @Mock
    private AbstractProcess process;

    @Mock
    private ProcessInstances instances;

    @Mock
    private org.kie.kogito.process.ProcessInstance processInstance;

    @Mock
    private ProcessError error;

    @Mock
    private Object variables;

    @Mock
    Instance<Application> applicationInstance;

    @Mock
    private Application application;

    @BeforeEach
    public void setup() {
        lenient().when(processSvgServiceInstance.isResolvable()).thenReturn(true);
        lenient().when(processSvgServiceInstance.get()).thenReturn(processSvgService);
        lenient().when(processesInstance.isResolvable()).thenReturn(true);
        lenient().when(processesInstance.get()).thenReturn(processes);
        lenient().when(processes.processById(anyString())).thenReturn(process);
        lenient().when(process.instances()).thenReturn(instances);
        lenient().when(instances.findById(anyString())).thenReturn(Optional.of(processInstance));
        lenient().when(processInstance.error()).thenReturn(Optional.of(error));
        lenient().when(processInstance.variables()).thenReturn(variables);
        lenient().when(processInstance.id()).thenReturn(PROCESS_INSTANCE_ID);
        lenient().when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ERROR);
        lenient().when(error.failedNodeId()).thenReturn(NODE_ID_ERROR);
        lenient().when(error.errorMessage()).thenReturn("Test error message");
        lenient().when(application.unitOfWorkManager()).thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
        lenient().when(applicationInstance.isResolvable()).thenReturn(true);
        lenient().when(applicationInstance.get()).thenReturn(application);

        client = spy(new KogitoAddonRuntimeClientImpl(processSvgServiceInstance, sourceFilesProvider, processesInstance, applicationInstance));
        client.setGatewayTargetUrl(Optional.empty());
        client.addServiceWebClient(SERVICE_URL, webClientMock);
        client.setVertx(vertx);
        client.setIdentity(identityMock);
    }

    private org.kie.kogito.process.ProcessInstance mockProcessInstanceStatusActive() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(processInstance);
    }

    private org.kie.kogito.process.ProcessInstance mockProcessInstanceStatusError() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ERROR);
            return null;
        }).when(processInstance);
    }

    private ProcessError mockProcessInstanceStatusActiveOnError() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(error);
    }

    @Test
    void testAbortProcessInstanceSuccess() {
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        mockProcessInstanceStatusActive().abort();
        client.abortProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).abort();
    }

    @Test
    void testAbortProcessInstanceError() {
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        mockProcessInstanceStatusError().abort();
        assertThrows(ProcessInstanceExecutionException.class,
                () -> client.abortProcessInstance(SERVICE_URL, pI));
        verify(processInstance, times(2)).error();
        verify(processInstance, times(1)).abort();
    }

    @Test
    void testRetryProcessInstance() {
        mockProcessInstanceStatusActiveOnError().retrigger();
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.retryProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(1)).error();
        verify(error, times(1)).retrigger();
        verify(error, times(0)).skip();
    }

    @Test
    void testSkipProcessInstance() {
        mockProcessInstanceStatusActiveOnError().skip();
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.skipProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(1)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(1)).skip();
    }

    @Test
    void testTriggerNodeInstance() {
        mockProcessInstanceStatusActive().triggerNode(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.triggerNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).triggerNode(NODE_ID);
    }

    @Test
    void testRetriggerNodeInstance() {
        mockProcessInstanceStatusActive().retriggerNodeInstance(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.retriggerNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).retriggerNodeInstance(NODE_ID);
    }

    @Test
    void testCancelNodeInstance() {
        mockProcessInstanceStatusActive().cancelNodeInstance(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.cancelNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).cancelNodeInstance(NODE_ID);
    }

    @Test
    protected void testCancelJobRest() {
        setupIdentityMock();
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);
        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");
        client.cancelJob(SERVICE_URL, job);

        verify(client).sendDeleteClientRequest(webClientMock,
                format(KogitoRuntimeCommonClient.CANCEL_JOB_PATH, job.getId()),
                "CANCEL Job with id: " + JOB_ID);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    protected void testRescheduleWithoutJobServiceInstance() {
        String newJobData = "{\"expirationTime\": \"2023-08-27T04:35:54.631Z\",\"retries\": 2}";
        setupIdentityMock();
        when(webClientMock.patch(anyString())).thenReturn(httpRequestMock);

        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");

        client.rescheduleJob(SERVICE_URL, job, newJobData);
        ArgumentCaptor<JsonObject> jsonCaptor = ArgumentCaptor.forClass(JsonObject.class);

        verify(client).sendPatchClientRequest(eq(webClientMock),
                eq(format(KogitoRuntimeCommonClient.RESCHEDULE_JOB_PATH, JOB_ID)),
                eq("RESCHEDULED JOB with id: " + job.getId()),
                jsonCaptor.capture());

        assertThat(jsonCaptor.getValue().getString("expirationTime")).isEqualTo("2023-08-27T04:35:54.631Z");
        assertThat(jsonCaptor.getValue().getString("retries")).isEqualTo("2");

        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(newJobData);
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        verify(httpRequestMock).putHeader("Authorization", "Bearer " + AUTHORIZED_TOKEN);
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testWebClientToURLOptions() {
        String defaultHost = "localhost";
        int defaultPort = 8180;
        WebClientOptions webClientOptions = client.getWebClientToURLOptions("http://" + defaultHost + ":" + defaultPort);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo(defaultHost);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(defaultPort);
    }

    @Test
    public void testWebClientToURLOptionsWithoutPort() {
        String dataIndexUrl = "http://service.com";
        WebClientOptions webClientOptions = client.getWebClientToURLOptions(dataIndexUrl);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(80);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo("service.com");
        assertFalse(webClientOptions.isSsl());
    }

    @Test
    public void testWebClientToURLOptionsWithoutPortSSL() {
        String dataIndexurl = "https://service.com";
        WebClientOptions webClientOptions = client.getWebClientToURLOptions(dataIndexurl);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(443);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo("service.com");
        assertTrue(webClientOptions.isSsl());
    }

    @Test
    public void testMalformedURL() {
        assertThat(client.getWebClientToURLOptions("malformedURL")).isNull();
    }

    @Test
    void testOverrideURL() {
        String host = "host.testcontainers.internal";
        client.setGatewayTargetUrl(Optional.of(host));
        WebClientOptions webClientOptions = client.getWebClientToURLOptions("http://service.com");
        assertThat(webClientOptions.getDefaultHost()).isEqualTo(host);
    }

    @Test
    public void testGetAuthHeader() {
        tokenCredential = mock(TokenCredential.class);
        when(identityMock.getCredential(TokenCredential.class)).thenReturn(tokenCredential);
        when(tokenCredential.getToken()).thenReturn(AUTHORIZED_TOKEN);

        String token = client.getAuthHeader();
        verify(identityMock, times(2)).getCredential(TokenCredential.class);
        assertThat(token).isEqualTo("Bearer " + AUTHORIZED_TOKEN);

        when(identityMock.getCredential(TokenCredential.class)).thenReturn(null);
        token = client.getAuthHeader();
        assertThat(token).isEqualTo("");
    }

    private ProcessInstance createProcessInstance(String processInstanceId, int status) {
        return TestUtils.getProcessInstance("travels", processInstanceId, status, null, null);
    }

    private Job createJob(String jobId, String processInstanceId, String status) {
        return TestUtils.getJob(jobId, "travels", processInstanceId, null, null, status);
    }

    private AsyncResult createResponseMocks(HttpResponse response, boolean succeed, int statusCode) {
        AsyncResult asyncResultMock = mock(AsyncResult.class);
        when(asyncResultMock.succeeded()).thenReturn(succeed);
        when(asyncResultMock.result()).thenReturn(response);
        when(response.statusCode()).thenReturn(statusCode);
        return asyncResultMock;
    }

    protected void checkResponseHandling(Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        HttpResponse response = mock(HttpResponse.class);
        HttpResponse responseWithoutError = mock(HttpResponse.class);

        handler.handle(createResponseMocks(response, false, 404));
        verify(response).statusMessage();
        verify(response).body();
        verify(response, never()).bodyAsString();

        handler.handle(createResponseMocks(responseWithoutError, true, 200));
        verify(responseWithoutError, never()).statusMessage();
        verify(responseWithoutError, never()).body();
        verify(responseWithoutError).bodyAsString();
    }

    protected void setupIdentityMock() {
        tokenCredential = mock(TokenCredential.class);
        when(identityMock.getCredential(TokenCredential.class)).thenReturn(tokenCredential);
        when(tokenCredential.getToken()).thenReturn(AUTHORIZED_TOKEN);
        when(httpRequestMock.putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN))).thenReturn(httpRequestMock);
    }
}
