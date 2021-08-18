/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.workitem.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import org.jbpm.process.core.Process;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.impl.WorkItemHandlerParamResolver;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kogito.workitem.rest.bodybuilders.DefaultWorkItemHandlerBodyBuilder;
import org.kogito.workitem.rest.bodybuilders.RestWorkItemHandlerBodyBuilder;
import org.kogito.workitem.rest.resulthandlers.DefaultRestWorkItemHandlerResult;
import org.kogito.workitem.rest.resulthandlers.RestWorkItemHandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

public class RestWorkItemHandler implements KogitoWorkItemHandler {

    public static final String REST_TASK_TYPE = "Rest";
    public static final String URL = "Url";
    public static final String METHOD = "Method";
    public static final String CONTENT_DATA = "ContentData";
    public static final String RESULT = "Result";
    public static final String USER = "Username";
    public static final String PASSWORD = "Password";
    public static final String HOST = "Host";
    public static final String PORT = "Port";
    public static final String RESULT_HANDLER = "ResultHandler";
    public static final String BODY_BUILDER = "BodyBuilder";

    private static final Logger logger = LoggerFactory.getLogger(RestWorkItemHandler.class);
    private static final RestWorkItemHandlerResult DEFAULT_RESULT_HANDLER = new DefaultRestWorkItemHandlerResult();
    private static final RestWorkItemHandlerBodyBuilder DEFAULT_BODY_BUILDER = new DefaultWorkItemHandlerBodyBuilder();
    private static final Map<String, RestWorkItemHandlerBodyBuilder> BODY_BUILDERS = new ConcurrentHashMap<>();

    // package scoped to allow unit test
    static class RestUnaryOperator implements UnaryOperator<Object> {

        private Object inputModel;

        public RestUnaryOperator(Object inputModel) {
            this.inputModel = inputModel;
        }

        @Override
        public Object apply(Object value) {
            return value instanceof WorkItemHandlerParamResolver
                    ? ((WorkItemHandlerParamResolver) value).apply(inputModel)
                    : value;
        }
    }

    private WebClient client;

    public RestWorkItemHandler(WebClient client) {
        this.client = client;
    }

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        RestWorkItemTargetInfo targetInfo = getTargetInfo(workItem);
        logger.debug("Using target {}", targetInfo);
        //retrieving parameters
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        //removing unnecessary parameter
        parameters.remove("TaskName");

        String endPoint = getParam(parameters, URL, String.class, null);
        if (endPoint == null) {
            throw new IllegalArgumentException("Missing required parameter " + URL);
        }
        HttpMethod method = HttpMethod.valueOf(getParam(parameters, METHOD, String.class, "GET").toUpperCase());
        Object inputModel = getParam(parameters, CONTENT_DATA, Object.class, null);
        String user = getParam(parameters, USER, String.class, null);
        String password = getParam(parameters, PASSWORD, String.class, null);
        String hostProp = getParam(parameters, HOST, String.class, "localhost");
        int portProp = getParam(parameters, PORT, Integer.class, 8080);

        RestWorkItemHandlerResult resultHandler = getParam(parameters, RESULT_HANDLER, RestWorkItemHandlerResult.class,
                DEFAULT_RESULT_HANDLER);
        RestWorkItemHandlerBodyBuilder bodyBuilder = getBodyBuilder(parameters);

        logger.debug("Filtered parameters are {}", parameters);
        // create request
        UnaryOperator<Object> resolver = new RestUnaryOperator(inputModel);
        endPoint = resolvePathParams(endPoint, parameters, resolver);
        Optional<URL> url = getUrl(endPoint);
        String host = url.map(java.net.URL::getHost).orElse(hostProp);
        int port = url.map(java.net.URL::getPort).orElse(portProp);
        String path = url.map(java.net.URL::getPath).orElse(endPoint).replace(" ", "%20");//fix issue with spaces in the path

        HttpRequest<Buffer> request = client.request(method, port, host, path);
        if (user != null && !user.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            request.basicAuthentication(user, password);
        }
        HttpResponse<Buffer> response = method == HttpMethod.POST || method == HttpMethod.PUT ? request.sendJsonAndAwait(bodyBuilder.apply(inputModel, parameters, resolver)) : request.sendAndAwait();
        manager.completeWorkItem(workItem.getStringId(), targetInfo != null ? Collections.singletonMap(RESULT,
                resultHandler.apply(targetInfo, response)) : Collections.emptyMap());

    }

    public RestWorkItemHandlerBodyBuilder getBodyBuilder(Map<String, Object> parameters) {
        Object param = parameters.get(BODY_BUILDER);
        //in case the body builder is not set as an input, just use the default
        if (Objects.isNull(param)) {
            return DEFAULT_BODY_BUILDER;
        }
        //check if an instance of RestWorkItemHandlerBodyBuilder was set and just return it
        if (param instanceof RestWorkItemHandlerBodyBuilder) {
            return (RestWorkItemHandlerBodyBuilder) param;
        }
        //in case of String, try to load an instance by the FQN of a RestWorkItemHandlerBodyBuilder
        if (param instanceof String) {
            return BODY_BUILDERS.computeIfAbsent(param.toString(), this::loadBodyBuilder);
        }
        throw new IllegalArgumentException("Invalid body builder instance " + param);
    }

    private RestWorkItemHandlerBodyBuilder loadBodyBuilder(String className) {
        try {
            return getClassLoader().loadClass(className).asSubclass(RestWorkItemHandlerBodyBuilder.class).getConstructor().newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid RestWorkItemHandlerBodyBuilder Class " + className, e);
        }
    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private Optional<URL> getUrl(String endPoint) {
        return Optional.ofNullable(endPoint)
                .map(spec -> {
                    try {
                        return new URL(spec);
                    } catch (MalformedURLException e) {
                        return null;
                    }
                });
    }

    private RestWorkItemTargetInfo getTargetInfo(KogitoWorkItem workItem) {
        String varName = ((WorkItemNode) ((WorkItemNodeInstance) workItem.getNodeInstance()).getNode()).getOutMapping(
                RESULT);
        if (varName != null) {
            KogitoProcessInstance pi = workItem.getProcessInstance();
            Object instance = pi.getVariables().get(varName);
            Class<?> type = getType(pi, varName);
            if (instance != null || type != null) {
                return new RestWorkItemTargetInfo(instance, type);
            }
        }
        logger.warn("no out mapping for {}", RESULT);
        return null;
    }

    private Class<?> getType(KogitoProcessInstance pi, String varName) {
        VariableScope variableScope = (VariableScope) ((Process) pi.getProcess()).getDefaultContext(
                VariableScope.VARIABLE_SCOPE);
        Variable variable = variableScope.findVariable(varName);
        if (variable != null) {
            try {
                return getClassLoader().loadClass(variable.getType().getStringType());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Problem loading type " + variable.getType().getStringType(), e);
            }
        } else {
            logger.warn("Cannot find definition for variable {}", varName);
        }
        return null;
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
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

    private <T> T getParam(Map<String, Object> parameters, String paramName, Class<T> type, T defaultValue) {
        Object value = parameters.remove(paramName);
        if (value == null) {
            value = defaultValue;
        } else if (!type.isAssignableFrom(value.getClass())) {
            if (type.isAssignableFrom(Integer.class) && CharSequence.class.isAssignableFrom(value.getClass())) {
                try {
                    value = Integer.parseInt(value.toString());
                } catch (NumberFormatException ex) {
                    value = defaultValue;
                }
            } else {
                throw new IllegalArgumentException("Parameter paramName should be of type " + type +
                        " but it is of type " +
                        value.getClass());
            }
        }
        return type.cast(value);
    }
}
