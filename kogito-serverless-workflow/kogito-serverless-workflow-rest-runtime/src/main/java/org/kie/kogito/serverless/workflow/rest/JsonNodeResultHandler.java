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

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kogito.workitem.rest.resulthandlers.RestWorkItemHandlerResult;

import com.fasterxml.jackson.databind.JsonNode;

import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;

import static org.kogito.workitem.rest.RestWorkItemHandlerUtils.checkStatusCode;

public class JsonNodeResultHandler implements RestWorkItemHandlerResult {

    static final String STATUS_CODE = "statusCode";
    static final String STATUS_MESSAGE = "statusMessage";

    @Override
    public Object apply(HttpResponse<Buffer> t, Class<?> u, KogitoProcessContext context) {
        Map<String, Object> metadata = context.getNodeInstance().getNode().getMetaData();
        if (metadata == null || toBoolean(metadata.getOrDefault("failOnStatusCode", Boolean.TRUE))) {
            checkStatusCode(t);
        } else {
            context.setVariable(STATUS_CODE, t.statusCode());
            context.setVariable(STATUS_MESSAGE, t.statusMessage());
        }
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
        return u == null ? t.bodyAsJson(JsonNode.class) : t.bodyAsJson(u);
    }
}
