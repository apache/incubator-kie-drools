/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.resource.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.WorkItemNotFoundException;
import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.process.workitem.InvalidTransitionException;
import org.kie.kogito.process.workitem.NotAuthorizedException;

public abstract class BaseExceptionsHandler<T> {

    public static final String MESSAGE = "message";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    private static final String TASK_ID = "taskId";
    public static final String VARIABLE = "variable";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String NODE_ID = "nodeId";
    public static final String FAILED_NODE_ID = "failedNodeId";
    public static final String ID = "id";
    private final Map<Class<? extends Exception>, FunctionHolder<T, ?>> mapper;

    private static class FunctionHolder<T, R> {
        private final Function<Exception, R> contentGenerator;
        private final Function<R, T> responseGenerator;

        public FunctionHolder(Function<Exception, R> contentGenerator, Function<R, T> responseGenerator) {
            this.contentGenerator = contentGenerator;
            this.responseGenerator = responseGenerator;
        }

        public Function<Exception, R> getContentGenerator() {
            return contentGenerator;
        }

        public Function<R, T> getResponseGenerator() {
            return responseGenerator;
        }
    }

    private final FunctionHolder<T, Exception> defaultHolder = new FunctionHolder<>(ex -> ex, BaseExceptionsHandler.this::internalError);

    protected BaseExceptionsHandler() {
        mapper = new HashMap<>();
        mapper.put(InvalidLifeCyclePhaseException.class, new FunctionHolder<>(
                ex -> Collections.singletonMap(MESSAGE, ex.getMessage()), BaseExceptionsHandler.this::badRequest));

        mapper.put(InvalidTransitionException.class, new FunctionHolder<>(
                ex -> Collections.singletonMap(MESSAGE, ex.getMessage()), BaseExceptionsHandler.this::badRequest));

        mapper.put(NodeInstanceNotFoundException.class, new FunctionHolder<>(
                ex -> {
                    NodeInstanceNotFoundException exception = (NodeInstanceNotFoundException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(MESSAGE, exception.getMessage());
                    response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                    response.put(NODE_INSTANCE_ID, exception.getNodeInstanceId());
                    return response;
                }, BaseExceptionsHandler.this::notFound));

        mapper.put(NodeNotFoundException.class, new FunctionHolder<>(
                ex -> {
                    NodeNotFoundException exception = (NodeNotFoundException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(MESSAGE, exception.getMessage());
                    response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                    response.put(NODE_ID, exception.getNodeId());
                    return response;
                }, BaseExceptionsHandler.this::notFound));

        mapper.put(NotAuthorizedException.class, new FunctionHolder<>(
                ex -> Collections.singletonMap(MESSAGE, ex.getMessage()), BaseExceptionsHandler.this::forbidden));

        mapper.put(ProcessInstanceDuplicatedException.class, new FunctionHolder<>(
                ex -> {
                    ProcessInstanceDuplicatedException exception = (ProcessInstanceDuplicatedException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(MESSAGE, exception.getMessage());
                    response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                    return response;
                }, BaseExceptionsHandler.this::conflict));

        mapper.put(ProcessInstanceExecutionException.class, new FunctionHolder<>(
                ex -> {
                    ProcessInstanceExecutionException exception = (ProcessInstanceExecutionException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(ID, exception.getProcessInstanceId());
                    response.put(FAILED_NODE_ID, exception.getFailedNodeId());
                    response.put(MESSAGE, exception.getErrorMessage());
                    return response;
                }, BaseExceptionsHandler.this::internalError));

        mapper.put(ProcessInstanceNotFoundException.class, new FunctionHolder<>(
                ex -> {
                    ProcessInstanceNotFoundException exception = (ProcessInstanceNotFoundException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(MESSAGE, exception.getMessage());
                    response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                    return response;
                }, BaseExceptionsHandler.this::notFound));

        mapper.put(WorkItemNotFoundException.class, new FunctionHolder<>(ex -> {
            WorkItemNotFoundException exception = (WorkItemNotFoundException) ex;
            return Map.of(MESSAGE, exception.getMessage(), TASK_ID, exception.getWorkItemId());
        }, BaseExceptionsHandler.this::notFound));

        mapper.put(VariableViolationException.class, new FunctionHolder<>(
                ex -> {
                    VariableViolationException exception = (VariableViolationException) ex;
                    Map<String, String> response = new HashMap<>();
                    response.put(MESSAGE, exception.getMessage() + " : " + exception.getErrorMessage());
                    response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                    response.put(VARIABLE, exception.getVariableName());
                    return response;
                }, BaseExceptionsHandler.this::badRequest));

        mapper.put(IllegalArgumentException.class, new FunctionHolder<>(ex -> Collections.singletonMap(MESSAGE, ex.getMessage()), BaseExceptionsHandler.this::badRequest));
    }

    protected abstract <R> T badRequest(R body);

    protected abstract <R> T conflict(R body);

    protected abstract <R> T internalError(R body);

    protected abstract <R> T notFound(R body);

    protected abstract <R> T forbidden(R body);

    public <R extends Exception, U> T mapException(R exception) {
        FunctionHolder<T, U> holder = (FunctionHolder<T, U>) mapper.getOrDefault(exception.getClass(), defaultHolder);
        U body = holder.getContentGenerator().apply(exception);
        if (exception instanceof ProcessInstanceExecutionException) {
            Throwable rootCause = ((ProcessInstanceExecutionException) exception).getCause();

            while (rootCause != null) {
                if (mapper.containsKey(rootCause.getClass())) {
                    holder = (FunctionHolder<T, U>) mapper.get(rootCause.getClass());
                    break;
                }
                rootCause = rootCause.getCause();
            }
        }
        return holder.getResponseGenerator().apply(body);
    }
}
