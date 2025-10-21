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
package org.kie.kogito.serverless.workflow.rest;

import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kogito.workitem.rest.resulthandlers.RestWorkItemHandlerResult;

import com.fasterxml.jackson.databind.JsonNode;

import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;

import static org.kogito.workitem.rest.RestWorkItemHandlerUtils.checkStatusCode;

public class JsonNodeResultHandler implements RestWorkItemHandlerResult {

    public static final String FAIL_ON_STATUS_ERROR = "failOnStatusCode";
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS_MESSAGE = "statusMessage";
    public static final String RESPONSE_HEADERS = "responseHeaders";

    @Override
    public Object apply(HttpResponse<Buffer> t, Class<?> u, KogitoProcessContext context) {
        Map<String, Object> metadata = context.getNodeInstance().getNode().getMetaData();
        if (metadata == null || toBoolean(metadata.getOrDefault(FAIL_ON_STATUS_ERROR, Boolean.TRUE))) {
            checkStatusCode(t);
        } else {
            context.setVariable(STATUS_CODE, t.statusCode());
            context.setVariable(STATUS_MESSAGE, t.statusMessage());
        }

        context.setVariable(RESPONSE_HEADERS, t.headers().entries().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return apply(t, u);
    }

    private boolean toBoolean(Object potentialBoolean) {
        if (potentialBoolean instanceof Boolean bool) {
            return bool.booleanValue();
        } else if (potentialBoolean instanceof CharSequence literal) {
            return Boolean.parseBoolean(literal.toString());
        } else {
            return true;
        }
    }

    @Override
    public Object apply(HttpResponse<Buffer> t, Class<?> u) {
        if (u == null) {
            return t.bodyAsJson(JsonNode.class);
        } else if (byte[].class.isAssignableFrom(u)) {
            return JsonObjectUtils.fromValue(t.body().getBytes());
        } else if (String.class.isAssignableFrom(u)) {
            return JsonObjectUtils.fromValue(t.bodyAsString());
        } else {
            return t.bodyAsJson(u);
        }
    }
}
