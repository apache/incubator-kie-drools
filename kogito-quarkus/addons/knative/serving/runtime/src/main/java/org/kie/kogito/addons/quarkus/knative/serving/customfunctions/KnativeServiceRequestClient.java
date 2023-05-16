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
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;

abstract class KnativeServiceRequestClient {

    private static final Logger logger = LoggerFactory.getLogger(KnativeServiceRequestClient.class);

    protected static final long DEFAULT_REQUEST_TIMEOUT_VALUE = 10_000L;

    static final String APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8 = "application/cloudevents+json; charset=UTF-8";

    static final String REQUEST_TIMEOUT_PROPERTY_NAME = "kogito.addon.knative-serving.request-timeout";

    /**
     * Invokes a Knative service using the specified payload.
     *
     * @param processInstanceId process instance ID.
     * @param serviceAddress address of the Knative service
     * @param path resource path
     * @param payload the payload
     * @return a {@link JsonNode} that represents the response payload
     */
    JsonNode execute(String processInstanceId, URI serviceAddress, String path, Map<String, Object> payload) {
        Objects.requireNonNull(serviceAddress, "serviceAddress is a mandatory parameter");
        Objects.requireNonNull(path, "path is a mandatory parameter");

        return sendRequest(processInstanceId, serviceAddress, path, payload);
    }

    protected final JsonNode responseAsJsonObject(HttpResponse<Buffer> response) {
        if (logger.isDebugEnabled()) {
            logger.debug("Response - status code: {}, body: {}", response.statusCode(), response.bodyAsString());
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new WorkItemExecutionException(Integer.toString(response.statusCode()), response.statusMessage());
        } else {
            return getJsonNode(Json.decodeValue(response.body().getDelegate()));
        }
    }

    private JsonNode getJsonNode(Object json) {
        if (json instanceof JsonObject) {
            return JsonObjectUtils.fromValue(((JsonObject) json).getMap());
        } else if (json instanceof JsonArray) {
            ArrayNode jsonArray = ObjectMapperFactory.listenerAware().createArrayNode();
            for (Object item : ((JsonArray) json)) {
                jsonArray.add(getJsonNode(item));
            }
            return jsonArray;
        } else {
            return JsonObjectUtils.fromValue(json);
        }
    }

    protected abstract JsonNode sendRequest(String processInstanceId, URI serviceAddress, String path,
            Map<String, Object> payload);
}
