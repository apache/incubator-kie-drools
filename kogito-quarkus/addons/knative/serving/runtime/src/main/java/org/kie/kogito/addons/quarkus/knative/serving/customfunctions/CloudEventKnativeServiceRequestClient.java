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
import java.util.HashMap;
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

    private static final String ID = "id";

    private final WebClient webClient;

    private final Duration requestTimeout;

    @Inject
    CloudEventKnativeServiceRequestClient(Vertx vertx,
            @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME) Optional<Long> requestTimeout) {
        this.webClient = WebClient.create(vertx);
        this.requestTimeout = Duration.ofMillis(requestTimeout.orElse(DEFAULT_REQUEST_TIMEOUT_VALUE));
    }

    @Override
    protected JsonNode sendRequest(String processInstanceId, URI serviceAddress, String path, Map<String, Object> cloudEvent) {
        HttpRequest<Buffer> request;
        if (serviceAddress.getPort() >= 0) {
            request = webClient.post(serviceAddress.getPort(), serviceAddress.getHost(), path);
        } else {
            request = webClient.post(serviceAddress.getHost(), path);
        }
        request.putHeader("Content-Type", APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)
                .ssl("https".equals(serviceAddress.getScheme()));

        if (cloudEvent.get(ID) == null) {
            cloudEvent = createCloudEventWithGeneratedId(cloudEvent, processInstanceId);
        }

        CloudEventUtils.validateCloudEvent(cloudEvent);

        JsonObject body = new JsonObject(cloudEvent);

        logger.debug("Sending request with CloudEvent - host: {}, port: {}, path: {}, CloudEvent: {}",
                serviceAddress.getHost(), serviceAddress.getPort(), path, body);

        HttpResponse<Buffer> response = request.sendBuffer(Buffer.buffer(body.encode())).await().atMost(requestTimeout);

        return responseAsJsonObject(response);
    }

    private static Map<String, Object> createCloudEventWithGeneratedId(Map<String, Object> cloudEvent, String processInstanceId) {
        Map<String, Object> modifiableCloudEvent = ensureModifiable(cloudEvent);
        modifiableCloudEvent.put(ID, generateCloudEventId(processInstanceId, cloudEvent));
        return modifiableCloudEvent;
    }

    private static Map<String, Object> ensureModifiable(Map<String, Object> map) {
        return map instanceof HashMap ? map : new HashMap<>(map);
    }

    static String generateCloudEventId(String processInstanceId, Map<String, Object> cloudEvent) {
        return processInstanceId + "_" + cloudEvent.hashCode();
    }

    @PreDestroy
    void preDestroy() {
        webClient.close();
    }
}
