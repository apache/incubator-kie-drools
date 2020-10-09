/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kogito.workitem.rest;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestWorkItemHandler implements WorkItemHandler {

    public static final String REST_TASK_TYPE = "Rest Task";
    public static final String ENDPOINT = "endpoint";
    public static final String METHOD = "method";
    public static final String PARAMETER = "Parameter";
    public static final String RESULT = "Result";
    public static final String RESULT_HANDLER = "ResultHandler";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String HOST = "host";
    public static final String PORT = "port";

    private static final Logger logger = LoggerFactory.getLogger(RestWorkItemHandler.class);

    // package scoped to allow unit test
    static class RestUnaryOperator implements UnaryOperator<Object> {

        private Object inputModel;

        public RestUnaryOperator(Object inputModel) {
            this.inputModel = inputModel;
        }

        @Override
        public Object apply(Object value) {
            return value instanceof RestWorkItemHandlerParamResolver
                    ? ((RestWorkItemHandlerParamResolver) value).apply(inputModel) : value;
        }
    }

    private WebClient client;

    public RestWorkItemHandler(WebClient client) {
        this.client = client;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // retrieving parameters
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        String endPoint = getParam(parameters, ENDPOINT, String.class);
        HttpMethod method = HttpMethod.valueOf(getParam(parameters, METHOD, String.class).toUpperCase());
        Object inputModel = getParam(parameters, PARAMETER, Object.class);
        String user = (String) parameters.remove(USER);
        String password = (String) parameters.remove(PASSWORD);
        String hostProp = (String) parameters.remove(HOST);
        String portProp = (String) parameters.remove(PORT);
        RestWorkItemHandlerResult resultHandler = getParam(parameters, RESULT_HANDLER, RestWorkItemHandlerResult.class);
        // create request
        UnaryOperator<Object> resolver = new RestUnaryOperator(inputModel);
        endPoint = resolvePathParams(endPoint, parameters, resolver);
        URI uri = URI.create(endPoint);
        String host = uri.getHost() != null ? uri.getHost() : hostProp;
        int port = uri.getPort() > 0 ? uri.getPort() : Integer.parseInt(portProp);
        HttpRequest<Buffer> request = client.request(method, port, host, uri.getPath());
        if (user != null && !user.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            request.basicAuthentication(user, password);
        }
        // execute request
        Handler<AsyncResult<HttpResponse<Buffer>>> handler = event -> {
            if (event.failed()) {
                logger.error("Rest invocation failed", event.cause());
                manager.abortWorkItem(workItem.getId());
            } else if (event.result().statusCode() >= 300) {
                logger.error("Rest invocation returns invalid code {}", event.result().statusCode());
                manager.abortWorkItem(workItem.getId());
            } else {
                manager.completeWorkItem(workItem.getId(), Collections.singletonMap(RESULT, resultHandler.apply(
                        inputModel, event.result().bodyAsJsonObject())));
            }
        };
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            // if parameters is empty at this stage, assume post content is the whole input model
            // if not, build a map from parameters remaining
            Object body = parameters.isEmpty() ? inputModel : parameters.entrySet().stream().collect(Collectors.toMap(
                    Entry::getKey, e -> resolver.apply(e.getValue())));
            request.sendJson(body, handler);
        } else {
            request.send(handler);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // rest item handler does not support abort
    }

    //  package scoped to allow unit test
    static String resolvePathParams(String endPoint, Map<String, Object> parameters, UnaryOperator<Object> resolver) {
        Set<String> toRemove = new HashSet<>();
        int start = endPoint.indexOf('{');
        if (start == -1) {
            return endPoint;
        }
        StringBuilder sb = new StringBuilder(endPoint);
        while (start != -1) {
            int end = sb.indexOf("}", start);
            if (end == -1) {
                throw new IllegalArgumentException("malformed endpoint should contain enclosing '}' " + endPoint);
            }
            String key = sb.substring(start + 1, end);
            Object value = resolver.apply(parameters.get(key));
            if (value == null) {
                throw new IllegalArgumentException("missing parameter " + key);
            }
            toRemove.add(key);
            sb.replace(start, end + 1, resolver.apply(parameters.get(key)).toString());
            start = sb.indexOf("{", end);
        }
        parameters.keySet().removeAll(toRemove);
        return sb.toString();
    }

    private <T> T getParam(Map<String, Object> parameters, String paramName, Class<T> type) {
        Object value = parameters.remove(paramName);
        if (value == null) {
            throw new IllegalArgumentException("Missing required parameter " + paramName);
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Parameter paramName should be of type " + type + " but it is of type " +
                    value.getClass());
        }
        return type.cast(value);
    }

}
