/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
class PlainJsonKnativeServiceRequestClient extends KnativeServiceRequestClient {

    private static final Logger logger = LoggerFactory.getLogger(PlainJsonKnativeServiceRequestClient.class);

    static final String CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE = "A Knative custom function argument cannot be a CloudEvent when the 'asCloudEvent' property are not set to 'true'";

    private final WebClient webClient;

    private final Duration requestTimeout;

    @Inject
    PlainJsonKnativeServiceRequestClient(Vertx vertx,
            @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME) Optional<Long> requestTimeout) {
        this.webClient = WebClient.create(vertx);
        this.requestTimeout = Duration.ofMillis(requestTimeout.orElse(DEFAULT_REQUEST_TIMEOUT_VALUE));
    }

    @Override
    protected JsonNode sendRequest(String processInstanceId, URI serviceAddress, String path,
            Map<String, Object> payload) {
        HttpRequest<Buffer> request;
        if (serviceAddress.getPort() >= 0) {
            request = webClient.post(serviceAddress.getPort(), serviceAddress.getHost(), path);
        } else {
            request = webClient.post(serviceAddress.getHost(), path);
        }
        request.ssl("https".equals(serviceAddress.getScheme()));

        HttpResponse<Buffer> response;

        if (payload.isEmpty()) {
            logger.debug("Sending request with empty body - host: {}, port: {}, path: {}", serviceAddress.getHost(),
                    serviceAddress.getPort(), path);

            response = request.send().await().atMost(requestTimeout);
        } else {
            validatePayload(payload);

            JsonObject body = new JsonObject(payload);

            logger.debug("Sending request with body - host: {}, port: {}, path: {}, body: {}", serviceAddress.getHost(),
                    serviceAddress.getPort(), path, body);

            response = request.sendJsonObject(body).await().atMost(requestTimeout);
        }

        return responseAsJsonObject(response);
    }

    private void validatePayload(Map<String, Object> payload) {
        List<String> missingAttributes = CloudEventUtils.getMissingAttributes(payload);
        if (missingAttributes.isEmpty() || (missingAttributes.size() == 1 && missingAttributes.contains("id"))) {
            throw new IllegalArgumentException(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
        }
    }

    @PreDestroy
    void preDestroy() {
        webClient.close();
    }
}
