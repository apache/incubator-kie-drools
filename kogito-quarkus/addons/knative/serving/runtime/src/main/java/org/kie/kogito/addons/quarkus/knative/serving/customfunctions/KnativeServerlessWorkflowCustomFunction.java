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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

/**
 * Implementation of a Serverless Workflow custom function to invoke Knative services.
 * 
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#defining-custom-function-types">Serverless Workflow specification - Defining custom function types</a>
 */
@ApplicationScoped
final class KnativeServerlessWorkflowCustomFunction {

    private static final Logger logger = LoggerFactory.getLogger(KnativeServerlessWorkflowCustomFunction.class);

    static final String REQUEST_TIMEOUT_PROPERTY_NAME = "kogito.addon.knative-serving.request-timeout";

    private static final long DEFAULT_REQUEST_TIMEOUT_VALUE = 10_000L;

    private final WebClient webClient;

    private final KnativeServiceRegistry knativeServiceRegistry;

    private final Duration requestTimeout;

    @Inject
    KnativeServerlessWorkflowCustomFunction(Vertx vertx, KnativeServiceRegistry knativeServiceRegistry,
            @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME) Optional<Long> requestTimeout) {
        this.webClient = WebClient.create(vertx);
        this.knativeServiceRegistry = knativeServiceRegistry;
        this.requestTimeout = Duration.ofMillis(requestTimeout.orElse(DEFAULT_REQUEST_TIMEOUT_VALUE));
    }

    @PreDestroy
    void preDestroy() {
        webClient.close();
    }

    /**
     * Invokes a Knative service using the specified payload.
     * 
     * @param knativeServiceName name of the Knative service
     * @param path resource path
     * @param payload the payload
     * @return a {@link JsonNode} that represents the response payload
     */
    JsonNode execute(String knativeServiceName, String path, Map<String, Object> payload) {
        Objects.requireNonNull(knativeServiceName, "knativeServiceName is a mandatory parameter");
        Objects.requireNonNull(path, "path is a mandatory parameter");

        KnativeServiceAddress serviceAddress = getServiceAddress(knativeServiceName);

        return sendRequest(serviceAddress, path, payload);
    }

    private JsonNode sendRequest(KnativeServiceAddress serviceAddress, String path, Map<String, Object> payload) {
        HttpRequest<Buffer> request = webClient.post(serviceAddress.getPort(), serviceAddress.getHost(), path)
                .ssl(serviceAddress.isSsl());

        HttpResponse<Buffer> response;

        if (payload.isEmpty()) {
            logger.debug("Sending request with empty body - host: {}, port: {}, path: {}", serviceAddress.getHost(), serviceAddress.getPort(), path);
            response = request.send().await().atMost(requestTimeout);
        } else {
            JsonObject body = new JsonObject(payload);
            logger.debug("Sending request with body - host: {}, port: {}, path: {}, body: {}", serviceAddress.getHost(), serviceAddress.getPort(), path, body);
            response = request.sendJsonObject(body).await().atMost(requestTimeout);
        }

        JsonObject responseBody = response.bodyAsJsonObject();

        logger.debug("Response - status code: {}, body: {}", response.statusCode(), responseBody);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new WorkItemExecutionException(Integer.toString(response.statusCode()), response.statusMessage());
        } else {
            ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
            responseBody.fieldNames().forEach(fieldName -> jsonNode.put(fieldName, responseBody.getString(fieldName)));
            return jsonNode;
        }
    }

    private KnativeServiceAddress getServiceAddress(String knativeServiceName) {
        return knativeServiceRegistry.getServiceAddress(knativeServiceName)
                .orElseThrow(() -> new WorkItemExecutionException("The Knative service '" + knativeServiceName
                        + "' could not be found."));
    }
}
