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

import java.time.Duration;
import java.util.HashMap;
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
class CloudEventKnativeServiceRequestClient extends KnativeServiceRequestClient {

    private static final Logger logger = LoggerFactory.getLogger(CloudEventKnativeServiceRequestClient.class);

    private final WebClient webClient;

    private final Duration requestTimeout;

    @Inject
    CloudEventKnativeServiceRequestClient(Vertx vertx,
            @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME) Optional<Long> requestTimeout) {
        this.webClient = WebClient.create(vertx);
        this.requestTimeout = Duration.ofMillis(requestTimeout.orElse(DEFAULT_REQUEST_TIMEOUT_VALUE));
    }

    @Override
    protected JsonNode sendRequest(String processInstanceId, KnativeServiceAddress serviceAddress, String path, Map<String, Object> cloudEvent) {
        validateCloudEvent(cloudEvent);

        HttpRequest<Buffer> request = webClient.post(serviceAddress.getPort(), serviceAddress.getHost(), path)
                .putHeader("Content-Type", APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)
                .ssl(serviceAddress.isSsl());

        JsonObject body = new JsonObject(createCloudEventWithGeneratedId(cloudEvent, processInstanceId));

        logger.debug("Sending request with CloudEvent - host: {}, port: {}, path: {}, CloudEvent: {}",
                serviceAddress.getHost(), serviceAddress.getPort(), path, body);

        HttpResponse<Buffer> response = request.sendBuffer(Buffer.buffer(body.encode())).await().atMost(requestTimeout);

        return responseAsJsonObject(response);
    }

    private static HashMap<String, Object> createCloudEventWithGeneratedId(Map<String, Object> cloudEvent, String processInstanceId) {
        HashMap<String, Object> modifiableCloudEvent = new HashMap<>(cloudEvent);
        Object oldIdValue = modifiableCloudEvent.put("id", generateCloudEventId(processInstanceId, cloudEvent.get("source").toString()));

        if (oldIdValue != null) {
            logger.warn("CloudEvent id should not be set. Ignoring id {}. A generated value will be used instead.", oldIdValue);
        }
        return modifiableCloudEvent;
    }

    private static String generateCloudEventId(String processInstanceId, String source) {
        return source + '_' + processInstanceId;
    }

    private void validateCloudEvent(Map<String, Object> cloudEvent) {
        List<String> missingAttributes = CloudEventUtils.getMissingAttributes(cloudEvent);
        missingAttributes.remove("id");

        if (!missingAttributes.isEmpty()) {
            throw new IllegalArgumentException("Invalid CloudEvent. The following mandatory attributes are missing: "
                    + String.join(", ", missingAttributes));
        }
    }

    @PreDestroy
    void preDestroy() {
        webClient.close();
    }
}
