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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import static java.lang.String.format;

@ApplicationScoped
public class KogitoRuntimeClientImpl implements KogitoRuntimeClient {

    public static final String ABORT_PROCESS_INSTANCE_PATH = "/management/processes/%s/instances/%s";
    public static final String RETRY_PROCESS_INSTANCE_PATH = "/management/processes/%s/instances/%s/retrigger";
    public static final String SKIP_PROCESS_INSTANCE_PATH = "/management/processes/%s/instances/%s/skip";
    public static final String GET_PROCESS_INSTANCE_DIAGRAM_PATH = "/svg/processes/%s/instances/%s";
    public static final String GET_PROCESS_INSTANCE_SOURCE_PATH = "/management/processes/%s/source";
    public static final String GET_PROCESS_INSTANCE_NODE_DEFINITIONS_PATH = "/management/processes/%s/nodes";
    public static final String UPDATE_VARIABLES_PROCESS_INSTANCE_PATH = "/%s/%s";
    public static final String TRIGGER_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodes/%s"; //node def
    public static final String RETRIGGER_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodeInstances/%s"; // nodeInstance Id
    public static final String CANCEL_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodeInstances/%s"; // nodeInstance Id

    public static final String CANCEL_JOB_PATH = "/%s";
    public static final String RESCHEDULE_JOB_PATH = "/%s";

    public static final String GET_TASK_SCHEMA_PATH = "/%s/%s/%s/%s/schema";
    public static final String UPDATE_USER_TASK_INSTANCE_PATH = "/management/processes/%s/instances/%s/tasks/%s";

    public static final String CREATE_USER_TASK_INSTANCE_COMMENT_PATH = "/%s/%s/%s/%s/comments";
    public static final String UPDATE_USER_TASK_INSTANCE_COMMENT_PATH = "/%s/%s/%s/%s/comments/%s";
    public static final String DELETE_USER_TASK_INSTANCE_COMMENT_PATH = "/%s/%s/%s/%s/comments/%s";

    public static final String CREATE_USER_TASK_INSTANCE_ATTACHMENT_PATH = "/%s/%s/%s/%s/attachments";
    public static final String UPDATE_USER_TASK_INSTANCE_ATTACHMENT_PATH = "/%s/%s/%s/%s/attachments/%s";
    public static final String DELETE_USER_TASK_INSTANCE_ATTACHMENT_PATH = "/%s/%s/%s/%s/attachments/%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoRuntimeClientImpl.class);
    private Vertx vertx;
    private SecurityIdentity identity;
    protected Map<String, WebClient> serviceWebClientMap = new HashMap<>();

    @ConfigProperty(name = "kogito.dataindex.gateway.url")
    protected Optional<String> gatewayTargetUrl;

    @Inject
    public KogitoRuntimeClientImpl(Vertx vertx, SecurityIdentity identity) {
        this.vertx = vertx;
        this.identity = identity;
    }

    protected WebClient getWebClient(String runtimeServiceUrl) {
        if (runtimeServiceUrl == null) {
            throw new DataIndexServiceException("Runtime service URL not defined, please review the kogito.service.url system property to point the public URL for this runtime.");
        } else {
            return serviceWebClientMap.computeIfAbsent(runtimeServiceUrl, url -> WebClient.create(vertx, getWebClientToURLOptions(runtimeServiceUrl)));
        }
    }

    protected WebClientOptions getWebClientToURLOptions(String targetHttpURL) {
        try {
            URL dataIndexURL = new URL(targetHttpURL);
            return new WebClientOptions()
                    .setDefaultHost(gatewayTargetUrl.orElse(dataIndexURL.getHost()))
                    .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                    .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
        } catch (MalformedURLException ex) {
            LOGGER.error("Invalid runtime service URL: " + targetHttpURL, ex);
            return null;
        }
    }

    @Override
    public CompletableFuture<String> abortProcessInstance(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(ABORT_PROCESS_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI, "ABORT ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> retryProcessInstance(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(RETRY_PROCESS_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendPostClientRequest(getWebClient(serviceURL), requestURI, "RETRY ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> skipProcessInstance(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(SKIP_PROCESS_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendPostClientRequest(getWebClient(serviceURL), requestURI, "SKIP ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> updateProcessInstanceVariables(String serviceURL, ProcessInstance processInstance, String variables) {
        String requestURI = format(UPDATE_VARIABLES_PROCESS_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendJSONPutClientRequest(getWebClient(serviceURL), requestURI, "UPDATE VARIABLES of ProcessInstance with id: " + processInstance.getId(), variables);
    }

    @Override
    public CompletableFuture<String> getProcessInstanceDiagram(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(GET_PROCESS_INSTANCE_DIAGRAM_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendGetClientRequest(getWebClient(serviceURL), requestURI, "Get Process Instance diagram with id: " + processInstance.getId(), null);
    }

    @Override
    public CompletableFuture<String> getProcessInstanceSourceFileContent(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(GET_PROCESS_INSTANCE_SOURCE_PATH, processInstance.getProcessId());
        return sendGetClientRequest(getWebClient(serviceURL), requestURI, "Get Process Instance source file with processId: " +
                processInstance.getProcessId(), null);
    }

    @Override
    public CompletableFuture<List<Node>> getProcessInstanceNodeDefinitions(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(GET_PROCESS_INSTANCE_NODE_DEFINITIONS_PATH, processInstance.getProcessId());
        return sendGetClientRequest(getWebClient(serviceURL), requestURI, "Get Process Instance available nodes with id: " + processInstance.getId(), List.class);
    }

    @Override
    public CompletableFuture<String> triggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeDefinitionId) {
        String requestURI = format(TRIGGER_NODE_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId(), nodeDefinitionId);
        return sendPostClientRequest(getWebClient(serviceURL), requestURI,
                "Trigger Node " + nodeDefinitionId +
                        "from ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> retriggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        String requestURI = format(RETRIGGER_NODE_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId(), nodeInstanceId);
        return sendPostClientRequest(getWebClient(serviceURL), requestURI,
                "Retrigger NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> cancelNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        String requestURI = format(CANCEL_NODE_INSTANCE_PATH, processInstance.getProcessId(), processInstance.getId(), nodeInstanceId);
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI,
                "Cancel NodeInstance " + nodeInstanceId +
                        "from ProcessInstance with id: " + processInstance.getId());
    }

    @Override
    public CompletableFuture<String> cancelJob(String serviceURL, Job job) {
        String requestURI = format(CANCEL_JOB_PATH, job.getId());
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI, "CANCEL Job with id: " + job.getId());
    }

    @Override
    public CompletableFuture<String> rescheduleJob(String serviceURL, Job job, String newJobData) {
        String requestURI = format(RESCHEDULE_JOB_PATH, job.getId());
        return sendJSONPutClientRequest(getWebClient(serviceURL), requestURI,
                "RESCHEDULED JOB with id: " + job.getId(), newJobData);
    }

    @Override
    public CompletableFuture<String> getUserTaskSchema(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups) {
        String requestURI = format(GET_TASK_SCHEMA_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getName(), userTaskInstance.getId()) + "?" + getUserGroupsURIParameter(user, groups);
        return sendGetClientRequest(getWebClient(serviceURL), requestURI,
                "Get User Task schema for task:" + userTaskInstance.getName() + " with id: " + userTaskInstance.getId(),
                null);
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstance(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, Map taskInfo) {
        String requestURI = format(UPDATE_USER_TASK_INSTANCE_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getId()) + "?" + getUserGroupsURIParameter(user, groups);
        return sendPatchClientRequest(getWebClient(serviceURL), requestURI,
                "Update user task instance:" + userTaskInstance.getName() + " with id: " + userTaskInstance.getId(),
                new JsonObject(taskInfo));
    }

    @Override
    public CompletableFuture<String> createUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentInfo) {
        String requestURI = format(CREATE_USER_TASK_INSTANCE_COMMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(), userTaskInstance.getName(),
                userTaskInstance.getId()) + "?" + getUserGroupsURIParameter(user, groups);
        return sendPostWithBodyClientRequest(getWebClient(serviceURL), requestURI,
                "Adding comment to  UserTask:" + userTaskInstance.getName() + " with id: " + userTaskInstance.getId(),
                commentInfo, MediaType.TEXT_PLAIN);
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentId, String commentInfo) {
        String requestURI = format(UPDATE_USER_TASK_INSTANCE_COMMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(), userTaskInstance.getName(),
                userTaskInstance.getId(), commentId) + "?" + getUserGroupsURIParameter(user, groups);
        return sendPutClientRequest(getWebClient(serviceURL),
                requestURI,
                "Update UserTask: " + userTaskInstance.getName() + " comment:" + commentId + "  with taskid: " + userTaskInstance.getId(),
                commentInfo, MediaType.TEXT_PLAIN);
    }

    @Override
    public CompletableFuture<String> deleteUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentId) {
        String requestURI = format(DELETE_USER_TASK_INSTANCE_COMMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getName(), userTaskInstance.getId(), commentId) + "?" + getUserGroupsURIParameter(user, groups);
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI,
                "Delete comment : " + commentId + "of Task: " + userTaskInstance.getName() + "  with taskid: " + userTaskInstance.getId());
    }

    @Override
    public CompletableFuture<String> createUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String name, String uri) {
        String requestURI = format(CREATE_USER_TASK_INSTANCE_ATTACHMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(), userTaskInstance.getName(),
                userTaskInstance.getId()) + "?" + getUserGroupsURIParameter(user, groups);
        return sendPostWithBodyClientRequest(getWebClient(serviceURL), requestURI,
                "Adding attachment to  UserTask:" + userTaskInstance.getName() + " with id: " + userTaskInstance.getId(),
                "{ \"name\": \"" + name + "\", \"uri\": \"" + uri + "\" }", MediaType.APPLICATION_JSON);
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, String attachmentId, String name, String uri) {
        String requestURI = format(UPDATE_USER_TASK_INSTANCE_ATTACHMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(), userTaskInstance.getName(),
                userTaskInstance.getId(), attachmentId) + "?" + getUserGroupsURIParameter(user, groups);
        return sendJSONPutClientRequest(getWebClient(serviceURL),
                requestURI,
                "Update UserTask: " + userTaskInstance.getName() + " attachment:" + attachmentId +
                        " with taskid: " + userTaskInstance.getId() + "with: " + name + " and info:" + uri,
                "{ \"name\": \"" + name + "\", \"uri\": \"" + uri + "\" }");
    }

    @Override
    public CompletableFuture<String> deleteUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String attachmentId) {
        String requestURI = format(DELETE_USER_TASK_INSTANCE_ATTACHMENT_PATH, userTaskInstance.getProcessId(), userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getName(), userTaskInstance.getId(), attachmentId) + "?" + getUserGroupsURIParameter(user, groups);
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI,
                "Delete attachment : " + attachmentId + "of Task: " + userTaskInstance.getName() + "  with taskid: " + userTaskInstance.getId());
    }

    private String getUserGroupsURIParameter(String user, List<String> groups) {
        final StringBuilder builder = new StringBuilder();
        if (user != null && groups != null) {
            builder.append("user=" + user);
            groups.stream().forEach(group -> builder.append("&group=" + group));
        }
        return builder.toString();
    }

    protected CompletableFuture sendDeleteClientRequest(WebClient webClient, String requestURI, String logMessage) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.delete(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> asyncHttpResponseTreatment(res, future, logMessage));
        return future;
    }

    protected CompletableFuture sendPostWithBodyClientRequest(WebClient webClient, String requestURI, String logMessage, String body, String contentType) {
        CompletableFuture future = new CompletableFuture<>();
        HttpRequest<Buffer> request = webClient.post(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .putHeader("Content-Type", contentType);
        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            request.sendJson(new JsonObject(body), res -> asyncHttpResponseTreatment(res, future, logMessage));
        } else {
            request.sendBuffer(Buffer.buffer(body), res -> asyncHttpResponseTreatment(res, future, logMessage));
        }
        return future;
    }

    private void asyncHttpResponseTreatment(AsyncResult<HttpResponse<Buffer>> res, CompletableFuture future, String logMessage) {
        if (res.succeeded() && ((res.result().statusCode() == 200 || res.result().statusCode() == 201))) {
            future.complete(res.result().bodyAsString() != null ? res.result().bodyAsString() : "Successfully performed: " + logMessage);
        } else {
            future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
        }
    }

    protected CompletableFuture sendPostClientRequest(WebClient webClient, String requestURI, String logMessage) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.post(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> asyncHttpResponseTreatment(res, future, logMessage));
        return future;
    }

    protected CompletableFuture sendJSONPutClientRequest(WebClient webClient, String requestURI, String logMessage, String jsonString) {
        return sendPutClientRequest(webClient, requestURI, logMessage, jsonString, MediaType.APPLICATION_JSON);
    }

    protected CompletableFuture sendPutClientRequest(WebClient webClient, String requestURI, String logMessage, String body, String contentType) {
        CompletableFuture future = new CompletableFuture<>();
        HttpRequest<Buffer> request = webClient.put(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .putHeader("Content-Type", contentType);
        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            request.sendJson(new JsonObject(body), res -> asyncHttpResponseTreatment(res, future, logMessage));
        } else {
            request.sendBuffer(Buffer.buffer(body), res -> asyncHttpResponseTreatment(res, future, logMessage));
        }
        return future;
    }

    protected CompletableFuture sendPatchClientRequest(WebClient webClient, String requestURI, String logMessage, JsonObject jsonBody) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.patch(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .sendJson(jsonBody, res -> asyncHttpResponseTreatment(res, future, logMessage));
        return future;
    }

    protected CompletableFuture sendGetClientRequest(WebClient webClient, String requestURI, String logMessage, Class type) {
        CompletableFuture future = new CompletableFuture<>();

        webClient.get(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> send(logMessage, type, future, res));
        return future;
    }

    protected void send(String logMessage, Class type, CompletableFuture future, AsyncResult<HttpResponse<Buffer>> res) {
        if (res.succeeded() && res.result().statusCode() == 200) {
            if (type != null) {
                future.complete(res.result().bodyAsJson(type));
            } else {
                future.complete(res.result().bodyAsString());
            }
        } else if (res.succeeded() && res.result().statusCode() == 404) {
            future.complete(null);
        } else {
            future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result()), res.cause()));
        }
    }

    private String getErrorMessage(String logMessage, HttpResponse<Buffer> result) {
        String errorMessage = "FAILED: " + logMessage;
        if (result != null) {
            errorMessage += " errorCode:" + result.statusCode() +
                    " errorStatus:" + result.statusMessage() +
                    " errorMessage:" + (result.body() != null ? result.body().toString() : "-");
        }
        return errorMessage;
    }

    protected String getAuthHeader() {
        if (identity != null && identity.getCredential(TokenCredential.class) != null) {
            return "Bearer " + identity.getCredential(TokenCredential.class).getToken();
        }
        return "";
    }
}
