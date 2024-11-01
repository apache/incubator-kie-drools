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
package org.kie.kogito.resource.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.internal.process.workitem.WorkItemNotFoundException;
import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;

import static org.kie.kogito.resource.exceptions.ExceptionBodyMessage.ERROR_CODE;

public class ExceptionBodyMessageFunctions {

    public static final String MESSAGE = "message";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String TASK_ID = "taskId";
    public static final String VARIABLE = "variable";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String NODE_ID = "nodeId";
    public static final String FAILED_NODE_ID = "failedNodeId";
    public static final String ID = "id";

    public static <T extends Exception> Function<T, ExceptionBodyMessage> defaultMessageException() {
        return ex -> new ExceptionBodyMessage(Collections.singletonMap(MESSAGE, ex.getMessage()));
    }

    public static Function<NodeInstanceNotFoundException, ExceptionBodyMessage> nodeInstanceNotFoundMessageException() {
        return ex -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, ex.getMessage());
            response.put(PROCESS_INSTANCE_ID, ex.getProcessInstanceId());
            response.put(NODE_INSTANCE_ID, ex.getNodeInstanceId());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<NodeNotFoundException, ExceptionBodyMessage> nodeNotFoundMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, exception.getMessage());
            response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
            response.put(NODE_ID, exception.getNodeId());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<ProcessInstanceDuplicatedException, ExceptionBodyMessage> processInstanceDuplicatedMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, exception.getMessage());
            response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<ProcessInstanceExecutionException, ExceptionBodyMessage> processInstanceExecutionMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(ID, exception.getProcessInstanceId());
            response.put(FAILED_NODE_ID, exception.getFailedNodeId());
            response.put(MESSAGE, exception.getErrorMessage());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<ProcessInstanceNotFoundException, ExceptionBodyMessage> processInstanceNotFoundExceptionMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, exception.getMessage());
            response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<WorkItemNotFoundException, ExceptionBodyMessage> workItemNotFoundMessageException() {
        return exception -> {
            return new ExceptionBodyMessage(Map.of(MESSAGE, exception.getMessage(), TASK_ID, exception.getWorkItemId()));
        };
    }

    public static Function<WorkItemExecutionException, ExceptionBodyMessage> workItemExecutionMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, exception.getMessage());
            response.put(ERROR_CODE, exception.getErrorCode());
            return new ExceptionBodyMessage(response);
        };
    }

    public static Function<VariableViolationException, ExceptionBodyMessage> variableViolationMessageException() {
        return exception -> {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE, exception.getMessage() + " : " + exception.getErrorMessage());
            response.put(PROCESS_INSTANCE_ID, exception.getProcessInstanceId());
            response.put(VARIABLE, exception.getVariableName());
            return new ExceptionBodyMessage(response);
        };
    }
}
