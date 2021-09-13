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
package org.kie.kogito.index.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.TestUtils;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
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

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.ABORT_PROCESS_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.CANCEL_JOB_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.CANCEL_NODE_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.GET_PROCESS_INSTANCE_DIAGRAM_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.GET_PROCESS_INSTANCE_NODE_DEFINITIONS_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.RESCHEDULE_JOB_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.RETRIGGER_NODE_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.RETRY_PROCESS_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.SKIP_PROCESS_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.TRIGGER_NODE_INSTANCE_PATH;
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.UPDATE_VARIABLES_PROCESS_INSTANCE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KogitoRuntimeClientTest {

    private static int ACTIVE = 1;
    private static int ERROR = 5;
    private static String SERVICE_URL = "http://runtimeURL.com";
    private static String PROCESS_INSTANCE_ID = "pId";
    private static String JOB_ID = "jobId";

    private static String AUTHORIZED_TOKEN = "authToken";

    @Mock
    public Vertx vertx;

    @Mock
    private SecurityIdentity identityMock;

    private TokenCredential tokenCredential;

    private KogitoRuntimeClientImpl client;

    private WebClient webClientMock;
    private HttpRequest httpRequestMock;

    @BeforeEach
    public void setup() {
        webClientMock = mock(WebClient.class);
        httpRequestMock = mock(HttpRequest.class);

        client = spy(new KogitoRuntimeClientImpl(vertx, identityMock));
        client.serviceWebClientMap.put(SERVICE_URL, webClientMock);
    }

    @Test
    public void testAbortProcessInstance() {
        setupIdentityMock();
        when(webClientMock.delete(any())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);

        client.abortProcessInstance(SERVICE_URL, pI);
        verify(client).sendDeleteClientRequest(webClientMock,
                format(ABORT_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "ABORT ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testRetryProcessInstance() {
        setupIdentityMock();
        when(webClientMock.post(any())).thenReturn(httpRequestMock);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.retryProcessInstance(SERVICE_URL, pI);
        verify(client).sendPostClientRequest(webClientMock,
                format(RETRY_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "RETRY ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testSkipProcessInstance() {
        setupIdentityMock();
        when(webClientMock.post(any())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.skipProcessInstance(SERVICE_URL, pI);
        verify(client).sendPostClientRequest(webClientMock,
                format(SKIP_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "SKIP ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testUpdateProcessInstanceVariables() throws Exception {
        when(webClientMock.put(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.updateProcessInstanceVariables(SERVICE_URL, pI, pI.getVariables().toString());
        verify(client).sendPutClientRequest(webClientMock,
                format(UPDATE_VARIABLES_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "UPDATE VARIABLES of ProcessInstance with id: " + pI.getId(), pI.getVariables().toString());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(pI.getVariables().toString());
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testTriggerNodeInstance() {
        String nodeDefId = "nodeDefId";
        when(webClientMock.post(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.triggerNodeInstance(SERVICE_URL, pI, nodeDefId);
        verify(client).sendPostClientRequest(webClientMock,
                format(TRIGGER_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeDefId),
                "Trigger Node " + nodeDefId + "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testRetriggerNodeInstance() {
        String nodeInstanceId = "nodeInstanceId";
        when(webClientMock.post(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.retriggerNodeInstance(SERVICE_URL, pI, nodeInstanceId);
        verify(client).sendPostClientRequest(webClientMock,
                format(RETRIGGER_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeInstanceId),
                "Retrigger NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testCancelNodeInstance() {
        String nodeInstanceId = "nodeInstanceId";
        when(webClientMock.delete(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.cancelNodeInstance(SERVICE_URL, pI, nodeInstanceId);
        verify(client).sendDeleteClientRequest(webClientMock,
                format(CANCEL_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeInstanceId),
                "Cancel NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testCancelJob() {
        when(webClientMock.delete(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");
        client.cancelJob(SERVICE_URL, job);

        verify(client).sendDeleteClientRequest(webClientMock,
                format(CANCEL_JOB_PATH, job.getId()),
                "CANCEL Job with id: " + JOB_ID);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testRescheduleJob() throws Exception {
        String newJobData = "{ }";
        when(webClientMock.put(any())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(anyString(), anyString())).thenReturn(httpRequestMock);

        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");

        client.rescheduleJob(SERVICE_URL, job, newJobData);
        verify(client).sendPutClientRequest(webClientMock,
                format(RESCHEDULE_JOB_PATH, JOB_ID),
                "RESCHEDULED JOB with id: " + job.getId(), newJobData);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(newJobData);
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testGetProcessInstanceDiagram() {
        setupIdentityMock();
        when(webClientMock.get(any())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.getProcessInstanceDiagram(SERVICE_URL, pI);
        verify(client).sendGetClientRequest(webClientMock,
                format(GET_PROCESS_INSTANCE_DIAGRAM_PATH, pI.getProcessId(), pI.getId()),
                "Get Process Instance diagram with id: " + pI.getId(),
                null);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsString();

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsString();
    }

    @Test
    public void testGetProcessInstanceNodeDefinitions() {
        setupIdentityMock();
        when(webClientMock.get(any())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.getProcessInstanceNodeDefinitions(SERVICE_URL, pI);
        verify(client).sendGetClientRequest(webClientMock,
                format(GET_PROCESS_INSTANCE_NODE_DEFINITIONS_PATH, pI.getProcessId(), pI.getId()),
                "Get Process Instance available nodes with id: " + pI.getId(),
                List.class);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        HttpResponse response = mock(HttpResponse.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, false, 404));
        verify(response, never()).bodyAsJson(List.class);

        handlerCaptor.getValue().handle(createResponseMocks(response, true, 200));
        verify(response).bodyAsJson(List.class);
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

    private AsyncResult createResponseMocks(HttpResponse response, boolean succeed, int statusCode) {
        AsyncResult asyncResultMock = mock(AsyncResult.class);
        when(asyncResultMock.succeeded()).thenReturn(succeed);
        when(asyncResultMock.result()).thenReturn(response);
        when(response.statusCode()).thenReturn(statusCode);
        return asyncResultMock;
    }

    private ProcessInstance createProcessInstance(String processInstanceId, int status) {
        return TestUtils.getProcessInstance("travels", processInstanceId, status, null, null);
    }

    private Job createJob(String jobId, String processInstanceId, String status) {
        return TestUtils.getJob(jobId, "travels", processInstanceId, null, null, status);
    }

    protected void setupIdentityMock() {
        tokenCredential = mock(TokenCredential.class);
        when(identityMock.getCredential(TokenCredential.class)).thenReturn(tokenCredential);
        when(tokenCredential.getToken()).thenReturn(AUTHORIZED_TOKEN);
        when(httpRequestMock.putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN))).thenReturn(httpRequestMock);
    }

}
