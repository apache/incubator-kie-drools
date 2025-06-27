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
package org.kie.kogito.index.quarkus.service.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import jakarta.inject.Inject;

import static java.lang.String.format;

public class KogitoRuntimeCommonClient {

    public static final String CANCEL_JOB_PATH = "/jobs/%s";
    public static final String RESCHEDULE_JOB_PATH = "/jobs/%s";

    public static final String FROM_PROCESS_INSTANCE_WITH_ID = "from ProcessInstance with id: ";

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoRuntimeCommonClient.class);

    protected Vertx vertx;

    protected SecurityIdentity identity;

    protected Map<String, WebClient> serviceWebClientMap = new HashMap<>();

    @ConfigProperty(name = "kogito.dataindex.gateway.url")
    protected Optional<String> gatewayTargetUrl;

    public void setGatewayTargetUrl(Optional<String> gatewayTargetUrl) {
        this.gatewayTargetUrl = gatewayTargetUrl;
    }

    public void addServiceWebClient(String serviceUrl, WebClient webClient) {
        serviceWebClientMap.put(serviceUrl, webClient);
    }

    protected WebClient getWebClient(String runtimeServiceUrl) {
        if (runtimeServiceUrl == null) {
            throw new DataIndexServiceException("Runtime service URL not defined, please review the kogito.service.url system property to point the public URL for this runtime.");
        } else {
            return serviceWebClientMap.computeIfAbsent(runtimeServiceUrl, url -> WebClient.create(vertx, getWebClientToURLOptions(runtimeServiceUrl)));
        }
    }

    public WebClientOptions getWebClientToURLOptions(String targetHttpURL) {
        try {
            URL dataIndexURL = new URL(targetHttpURL);
            return new WebClientOptions()
                    .setDefaultHost(gatewayTargetUrl.orElse(dataIndexURL.getHost()))
                    .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                    .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
        } catch (MalformedURLException ex) {
            LOGGER.error(String.format("Invalid runtime service URL: %s", targetHttpURL), ex);
            return null;
        }
    }

    public CompletableFuture<String> cancelJob(String serviceURL, Job job) {
        String requestURI = format(CANCEL_JOB_PATH, job.getId());
        LOGGER.debug("Sending DELETE to URI {}", requestURI);
        return sendDeleteClientRequest(getWebClient(serviceURL), requestURI, "CANCEL Job with id: " + job.getId());
    }

    public CompletableFuture<String> rescheduleJob(String serviceURL, Job job, String newJobData) {
        String requestURI = format(RESCHEDULE_JOB_PATH, job.getId());
        LOGGER.debug("Sending body: {} PATCH to URI {}", newJobData, requestURI);
        return sendPatchClientRequest(getWebClient(serviceURL), requestURI,
                "RESCHEDULED JOB with id: " + job.getId(), new JsonObject(newJobData));
    }

    public CompletableFuture sendDeleteClientRequest(WebClient webClient, String requestURI, String logMessage) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.delete(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .send(res -> asyncHttpResponseTreatment(res, future, logMessage));
        LOGGER.debug("Sending DELETE to URI {}", requestURI);
        return future;
    }

    protected void asyncHttpResponseTreatment(AsyncResult<HttpResponse<Buffer>> res, CompletableFuture<String> future, String logMessage) {
        asyncHttpResponseTreatment(res, future, result -> {
            String jsonMessage = result.bodyAsString();
            LOGGER.trace("Result {}", jsonMessage);
            return jsonMessage != null ? jsonMessage : "Successfully performed: " + logMessage;
        }, logMessage);

    }

    protected <T> void asyncHttpResponseTreatment(AsyncResult<HttpResponse<Buffer>> res, CompletableFuture<T> future, Function<HttpResponse<Buffer>, T> function, String logMessage) {
        if (res.succeeded() && (res.result().statusCode() == 200 || res.result().statusCode() == 201)) {
            future.complete(function.apply(res.result()));
        } else {
            LOGGER.error("Error {}", logMessage);
            future.completeExceptionally(new DataIndexServiceException(getErrorMessage(logMessage, res.result())));
        }
    }

    public CompletableFuture sendPatchClientRequest(WebClient webClient, String requestURI, String logMessage, JsonObject jsonBody) {
        CompletableFuture future = new CompletableFuture<>();
        webClient.patch(requestURI)
                .putHeader("Authorization", getAuthHeader())
                .sendJson(jsonBody, res -> asyncHttpResponseTreatment(res, future, logMessage));
        return future;
    }

    protected String getErrorMessage(String logMessage, HttpResponse<Buffer> result) {
        String errorMessage = "FAILED: " + logMessage;
        if (result != null) {
            errorMessage += " errorCode:" + result.statusCode() +
                    " errorStatus:" + result.statusMessage() +
                    " errorMessage:" + (result.body() != null ? result.body().toString() : "-");
        }
        return errorMessage;
    }

    public String getAuthHeader() {
        if (identity != null && identity.getCredential(TokenCredential.class) != null) {
            return "Bearer " + identity.getCredential(TokenCredential.class).getToken();
        }
        return "";
    }

    @Inject
    public void setIdentity(SecurityIdentity identity) {
        this.identity = identity;
    }

    @Inject
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

}
