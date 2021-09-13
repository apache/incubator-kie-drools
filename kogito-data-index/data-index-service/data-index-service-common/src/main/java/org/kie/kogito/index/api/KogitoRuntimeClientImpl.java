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
import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
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
    public static final String GET_PROCESS_INSTANCE_NODE_DEFINITIONS_PATH = "/management/processes/%s/nodes";
    public static final String UPDATE_VARIABLES_PROCESS_INSTANCE_PATH = "/%s/%s";
    public static final String TRIGGER_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodes/%s"; //node def
    public static final String RETRIGGER_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodeInstances/%s"; // nodeInstance Id
    public static final String CANCEL_NODE_INSTANCE_PATH = "/management/processes/%s/instances/%s/nodeInstances/%s"; // nodeInstance Id

    public static final String CANCEL_JOB_PATH = "/%s";
    public static final String RESCHEDULE_JOB_PATH = "/%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoRuntimeClientImpl.class);
    private Vertx vertx;
    private SecurityIdentity identity;
    protected Map<String, WebClient> serviceWebClientMap = new HashMap<>();

    @Inject
    public KogitoRuntimeClientImpl(Vertx vertx, SecurityIdentity identity) {
        this.vertx = vertx;
        this.identity = identity;
    }

    protected WebClient getWebClient(String runtimeServiceUrl) {
        return serviceWebClientMap.computeIfAbsent(runtimeServiceUrl, url -> WebClient.create(vertx, getWebClientToURLOptions(runtimeServiceUrl)));
    }

    protected WebClientOptions getWebClientToURLOptions(String targetHttpURL) {
        try {
            URL dataIndexURL = new URL(targetHttpURL);
            return new WebClientOptions()
                    .setDefaultHost(dataIndexURL.getHost())
                    .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                    .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
        } catch (MalformedURLException malformedURLException) {
            LOGGER.error("getWebClientToURLOptions has thrown malformedURLException with " + targetHttpURL);
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
        return sendPutClientRequest(getWebClient(serviceURL), requestURI, "UPDATE VARIABLES of ProcessInstance with id: " + processInstance.getId(), variables);
    }

    @Override
    public CompletableFuture<String> getProcessInstanceDiagram(String serviceURL, ProcessInstance processInstance) {
        String requestURI = format(GET_PROCESS_INSTANCE_DIAGRAM_PATH, processInstance.getProcessId(), processInstance.getId());
        return sendGetClientRequest(getWebClient(serviceURL), requestURI, "Get Process Instance diagram with id: " + processInstance.getId(), null);
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
        return sendPutClientRequest(getWebClient(serviceURL), requestURI,
                "RESCHEDULED JOB with id: " + job.getId(), newJobData);

    }

    protected CompletableFuture sendDeleteClientRequest(WebClient webClient, String requestURI, String logMessage) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.delete(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> {
                    if (res.succeeded() && (res.result().statusCode() == 200)) {
                        future.complete(res.result().bodyAsString());
                    } else {
                        future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
                    }
                });
        return future;
    }

    protected CompletableFuture sendPostClientRequest(WebClient webClient, String requestURI, String logMessage) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.post(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> {
                    if (res.succeeded() && (res.result().statusCode() == 200)) {
                        future.complete(res.result().bodyAsString());
                    } else {
                        future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
                    }
                });
        return future;
    }

    protected CompletableFuture sendPutClientRequest(WebClient webClient, String requestURI, String logMessage, String jsonBody) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.put(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .sendJson(new JsonObject(jsonBody), res -> {
                    if (res.succeeded() && (res.result().statusCode() == 200)) {
                        future.complete(res.result().bodyAsString());
                    } else {
                        future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
                    }
                });
        return future;
    }

    protected CompletableFuture sendGetClientRequest(WebClient webClient, String requestURI, String logMessage, Class type) {
        CompletableFuture future = new CompletableFuture<>();

        webClient.get(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> {
                    if (res.succeeded() && (res.result().statusCode() == 200)) {
                        if (type != null) {
                            future.complete(res.result().bodyAsJson(type));
                        } else {
                            future.complete(res.result().bodyAsString());
                        }
                    } else {
                        future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
                    }
                });
        return future;
    }

    private String getErrorMessage(String logMessage, HttpResponse<Buffer> result) {
        String errorMessage = "FAILED: " + logMessage;
        if (result != null) {
            errorMessage += " errorCode:" + result.statusCode() +
                    " errorStatus:" + result.statusMessage();
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
