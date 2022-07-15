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

import java.nio.Buffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.TestUtils;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
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
import static org.kie.kogito.index.api.KogitoRuntimeClientImpl.GET_PROCESS_INSTANCE_SOURCE_PATH;
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
    private static String TASK_ID = "taskId";
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
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);

        client.abortProcessInstance(SERVICE_URL, pI);
        verify(client).sendDeleteClientRequest(webClientMock,
                format(ABORT_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "ABORT ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testRetryProcessInstance() {
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.retryProcessInstance(SERVICE_URL, pI);
        verify(client).sendPostClientRequest(webClientMock,
                format(RETRY_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "RETRY ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testSkipProcessInstance() {
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.skipProcessInstance(SERVICE_URL, pI);
        verify(client).sendPostClientRequest(webClientMock,
                format(SKIP_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "SKIP ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testUpdateProcessInstanceVariables() {
        setupIdentityMock();
        when(webClientMock.put(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.updateProcessInstanceVariables(SERVICE_URL, pI, pI.getVariables().toString());
        verify(client).sendJSONPutClientRequest(webClientMock,
                format(UPDATE_VARIABLES_PROCESS_INSTANCE_PATH, pI.getProcessId(), pI.getId()),
                "UPDATE VARIABLES of ProcessInstance with id: " + pI.getId(), pI.getVariables().toString());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(pI.getVariables().toString());
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testTriggerNodeInstance() {
        String nodeDefId = "nodeDefId";
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.triggerNodeInstance(SERVICE_URL, pI, nodeDefId);
        verify(client).sendPostClientRequest(webClientMock,
                format(TRIGGER_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeDefId),
                "Trigger Node " + nodeDefId + "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testRetriggerNodeInstance() {
        String nodeInstanceId = "nodeInstanceId";
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.retriggerNodeInstance(SERVICE_URL, pI, nodeInstanceId);
        verify(client).sendPostClientRequest(webClientMock,
                format(RETRIGGER_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeInstanceId),
                "Retrigger NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testCancelNodeInstance() {
        String nodeInstanceId = "nodeInstanceId";
        setupIdentityMock();
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.cancelNodeInstance(SERVICE_URL, pI, nodeInstanceId);
        verify(client).sendDeleteClientRequest(webClientMock,
                format(CANCEL_NODE_INSTANCE_PATH, pI.getProcessId(), pI.getId(), nodeInstanceId),
                "Cancel NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + pI.getId());
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testCancelJob() {
        setupIdentityMock();
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);

        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");
        client.cancelJob(SERVICE_URL, job);

        verify(client).sendDeleteClientRequest(webClientMock,
                format(CANCEL_JOB_PATH, job.getId()),
                "CANCEL Job with id: " + JOB_ID);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testRescheduleJob() {
        String newJobData = "{ }";
        setupIdentityMock();
        when(webClientMock.put(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        Job job = createJob(JOB_ID, PROCESS_INSTANCE_ID, "SCHEDULED");

        client.rescheduleJob(SERVICE_URL, job, newJobData);
        verify(client).sendJSONPutClientRequest(webClientMock,
                format(RESCHEDULE_JOB_PATH, JOB_ID),
                "RESCHEDULED JOB with id: " + job.getId(), newJobData);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(newJobData);
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testGetProcessInstanceDiagram() {
        setupIdentityMock();
        when(webClientMock.get(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.getProcessInstanceDiagram(SERVICE_URL, pI);
        verify(client).sendGetClientRequest(webClientMock,
                format(GET_PROCESS_INSTANCE_DIAGRAM_PATH, pI.getProcessId(), pI.getId()),
                "Get Process Instance diagram with id: " + pI.getId(),
                null);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testGetProcessInstanceNodeDefinitions() {
        setupIdentityMock();
        when(webClientMock.get(anyString())).thenReturn(httpRequestMock);

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
        verify(response).statusMessage();
        verify(response).body();
        verify(response, never()).bodyAsJson(List.class);

        HttpResponse responseWithoutError = mock(HttpResponse.class);
        handlerCaptor.getValue().handle(createResponseMocks(responseWithoutError, true, 200));
        verify(responseWithoutError, never()).statusMessage();
        verify(responseWithoutError, never()).body();
        verify(responseWithoutError).bodyAsJson(List.class);
    }

    @Test
    public void testGetProcessInstanceSource() {
        setupIdentityMock();
        when(webClientMock.get(anyString())).thenReturn(httpRequestMock);

        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);

        client.getProcessInstanceSourceFileContent(SERVICE_URL, pI);
        verify(client).sendGetClientRequest(webClientMock,
                format(GET_PROCESS_INSTANCE_SOURCE_PATH, pI.getProcessId()),
                "Get Process Instance source file with processId: " + pI.getProcessId(),
                null);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testGetUserTaskSchema() {
        setupIdentityMock();
        when(webClientMock.get(anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.getUserTaskSchema(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"));
        verify(client).sendGetClientRequest(webClientMock, "/travels/" + PROCESS_INSTANCE_ID + "/TaskName/" + TASK_ID + "/schema?user=jdoe&group=managers",
                "Get User Task schema for task:TaskName with id: " + taskInstance.getId(), null);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testUpdateUserTaskInstance() {
        setupIdentityMock();
        when(webClientMock.patch(anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");
        Map taskInfo = new HashMap();
        taskInfo.put("description", "NewDescription");

        client.updateUserTaskInstance(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), taskInfo);
        ArgumentCaptor<JsonObject> jsonCaptor = ArgumentCaptor.forClass(JsonObject.class);
        verify(client).sendPatchClientRequest(eq(webClientMock),
                eq("/management/processes/travels/instances/" + PROCESS_INSTANCE_ID + "/tasks/" + TASK_ID + "?user=jdoe&group=managers"),
                eq("Update user task instance:" + taskInstance.getName() + " with id: " + taskInstance.getId()),
                jsonCaptor.capture());
        assertThat(jsonCaptor.getValue().getString("description")).isEqualTo("NewDescription");
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonOject = new JsonObject(taskInfo);
        verify(httpRequestMock).sendJson(eq(jsonOject), handlerCaptor.capture());
        verify(httpRequestMock).putHeader(eq("Authorization"), eq("Bearer " + AUTHORIZED_TOKEN));
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testCreateUserTaskInstanceComment() {
        String commentInfo = "newComment";
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.createUserTaskInstanceComment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), commentInfo);
        verify(client).sendPostWithBodyClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/comments?user=jdoe&group=managers"),
                eq("Adding comment to  UserTask:" + taskInstance.getName() + " with id: " + taskInstance.getId()), eq(commentInfo), eq("text/plain"));
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).sendBuffer(any(), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testCreateUserTaskInstanceAttachment() {
        String attachmentUri = "nhttps://drive.google.com/file/d/AttachmentUri";
        String attachmentName = "newAttachmentName";
        setupIdentityMock();
        when(webClientMock.post(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.createUserTaskInstanceAttachment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), attachmentName, attachmentUri);
        verify(client).sendPostWithBodyClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/attachments?user=jdoe&group=managers"),
                eq("Adding attachment to  UserTask:" + taskInstance.getName() + " with id: " + taskInstance.getId()),
                eq("{ \"name\": \"" + attachmentName + "\", \"uri\": \"" + attachmentUri + "\" }"), eq("application/json"));
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonObject = new JsonObject("{ \"name\": \"" + attachmentName + "\", \"uri\": \"" + attachmentUri + "\" }");

        verify(httpRequestMock).sendJson(eq(jsonObject), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testUpdateUserTaskInstanceComment() {
        String commentInfo = "NewCommentContent";
        String commentId = "commentId";
        setupIdentityMock();
        when(webClientMock.put(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.updateUserTaskInstanceComment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), commentId, commentInfo);
        verify(client).sendPutClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/comments/" + commentId + "?user=jdoe&group=managers"),
                eq("Update UserTask: " + taskInstance.getName() + " comment:" + commentId + "  with taskid: " + taskInstance.getId()),
                eq(commentInfo), eq("text/plain"));

        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).sendBuffer(any(), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testDeleteTaskInstanceComment() {
        String commentId = "commentId";
        setupIdentityMock();
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.deleteUserTaskInstanceComment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), commentId);
        verify(client).sendDeleteClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/comments/" + commentId + "?user=jdoe&group=managers"),
                eq("Delete comment : " + commentId + "of Task: " + taskInstance.getName() + "  with taskid: " + taskInstance.getId()));
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testUpdateUserTaskInstanceAttachment() {
        String attachmentName = "NewAttachmentName";
        String attachmentContent = "NewAttachmentContent";
        String attachmentId = "attachmentId";
        setupIdentityMock();
        when(webClientMock.put(anyString())).thenReturn(httpRequestMock);
        when(httpRequestMock.putHeader(eq("Content-Type"), anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.updateUserTaskInstanceAttachment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"),
                attachmentId, attachmentName, attachmentContent);
        verify(client).sendJSONPutClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/attachments/" + attachmentId + "?user=jdoe&group=managers"),
                eq("Update UserTask: " + taskInstance.getName() + " attachment:" + attachmentId +
                        " with taskid: " + taskInstance.getId() + "with: " + attachmentName +
                        " and info:" + attachmentContent),
                eq("{ \"name\": \"" + attachmentName + "\", \"uri\": \"" + attachmentContent + "\" }"));

        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        JsonObject jsonObject = new JsonObject("{ \"name\": \"" + attachmentName + "\", \"uri\": \"" + attachmentContent + "\" }");
        verify(httpRequestMock).sendJson(eq(jsonObject), handlerCaptor.capture());
        checkResponseHandling(handlerCaptor.getValue());
    }

    @Test
    public void testDeleteTaskInstanceAttachment() {
        String attachmentId = "attachmentId";
        setupIdentityMock();
        when(webClientMock.delete(anyString())).thenReturn(httpRequestMock);

        UserTaskInstance taskInstance = createUserTaskInstance(PROCESS_INSTANCE_ID, TASK_ID, "InProgress");

        client.deleteUserTaskInstanceAttachment(SERVICE_URL, taskInstance, "jdoe", Collections.singletonList("managers"), attachmentId);
        verify(client).sendDeleteClientRequest(eq(webClientMock),
                eq("/travels/" + PROCESS_INSTANCE_ID + "/" + taskInstance.getName() + "/" + TASK_ID + "/attachments/" + attachmentId + "?user=jdoe&group=managers"),
                eq("Delete attachment : " + attachmentId + "of Task: " + taskInstance.getName() + "  with taskid: " + taskInstance.getId()));
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(httpRequestMock).send(handlerCaptor.capture());
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

    private UserTaskInstance createUserTaskInstance(String processInstanceId, String userTaskId, String state) {
        return TestUtils.getUserTaskInstance(userTaskId, "travels", processInstanceId, null, null, state, "jdoe");
    }

    private Job createJob(String jobId, String processInstanceId, String status) {
        return TestUtils.getJob(jobId, "travels", processInstanceId, null, null, status);
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
