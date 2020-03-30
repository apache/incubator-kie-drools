/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.management.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.process.workitem.InvalidTransitionException;
import org.kie.kogito.process.workitem.NotAuthorizedException;

public abstract class BaseExceptionHandler<T> {

    public static final String MESSAGE = "message";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String VARIABLE = "variable";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String NODE_ID = "nodeId";
    public static final String FAILED_NODE_ID = "failedNodeId";
    public static final String ID = "id";
    private final Map<Class<? extends Exception>, Function<Exception, T>> mapper;

    public BaseExceptionHandler() {
        mapper = new HashMap<>();
        mapper.put(InvalidLifeCyclePhaseException.class,
                   ex -> badRequest(Collections.singletonMap(MESSAGE, ex.getMessage())));

        mapper.put(InvalidTransitionException.class,
                   ex -> badRequest(Collections.singletonMap(MESSAGE, ex.getMessage())));

        mapper.put(NodeInstanceNotFoundException.class,
                   ex -> {
                       NodeInstanceNotFoundException exception = (NodeInstanceNotFoundException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(MESSAGE, exception.getMessage());
                       response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                       response.put(NODE_INSTANCE_ID, exception.getNodeInstanceId());
                       return notFound(response);
                   });

        mapper.put(NodeNotFoundException.class,
                   ex -> {
                       NodeNotFoundException exception = (NodeNotFoundException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(MESSAGE, exception.getMessage());
                       response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                       response.put(NODE_ID, exception.getNodeId());
                       return notFound(response);
                   });

        mapper.put(NotAuthorizedException.class,
                   ex -> forbidden(Collections.singletonMap(MESSAGE, ex.getMessage())));

        mapper.put(ProcessInstanceDuplicatedException.class,
                   ex -> {
                       ProcessInstanceDuplicatedException exception = (ProcessInstanceDuplicatedException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(MESSAGE, exception.getMessage());
                       response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                       return conflict(response);
                   });

        mapper.put(ProcessInstanceExecutionException.class,
                   ex -> {
                       ProcessInstanceExecutionException exception = (ProcessInstanceExecutionException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(ID, exception.getProcessInstanceId());
                       response.put(FAILED_NODE_ID, exception.getFailedNodeId());
                       response.put(MESSAGE, exception.getErrorMessage());
                       return internalError(response);
                   });

        mapper.put(ProcessInstanceNotFoundException.class,
                   ex -> {
                       ProcessInstanceNotFoundException exception = (ProcessInstanceNotFoundException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(MESSAGE, exception.getMessage());
                       response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                       return notFound(response);
                   });

        mapper.put(VariableViolationException.class,
                   ex -> {
                       VariableViolationException exception = (VariableViolationException) ex;
                       Map<String, String> response = new HashMap<>();
                       response.put(MESSAGE, exception.getMessage() + " : " + exception.getErrorMessage());
                       response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
                       response.put(VARIABLE, exception.getVariableName());
                       return badRequest(response);
                   });
    }

    protected abstract <R> T badRequest(R body);

    protected abstract <R> T conflict(R body);

    protected abstract <R> T internalError(R body);

    protected abstract <R> T notFound(R body);

    protected abstract <R> T forbidden(R body);

    public <R extends Exception> T mapException(R exception) {
        return mapper
                .getOrDefault(exception.getClass(), this::internalError)
                .apply(exception);
    }
}
