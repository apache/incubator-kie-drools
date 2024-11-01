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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.handler.ExceptionHandler;
import org.kie.kogito.internal.process.runtime.MessageException;
import org.kie.kogito.internal.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.internal.process.workitem.InvalidTransitionException;
import org.kie.kogito.internal.process.workitem.NotAuthorizedException;
import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.internal.process.workitem.WorkItemNotFoundException;
import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.usertask.UserTaskInstanceNotAuthorizedException;
import org.kie.kogito.usertask.UserTaskInstanceNotFoundException;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.nodeInstanceNotFoundMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.nodeNotFoundMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.processInstanceDuplicatedMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.processInstanceExecutionMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.processInstanceNotFoundExceptionMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.variableViolationMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.workItemExecutionMessageException;
import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.workItemNotFoundMessageException;
import static org.kie.kogito.resource.exceptions.RestExceptionHandler.newExceptionHandler;

public abstract class AbstractExceptionsHandler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExceptionsHandler.class);

    RestExceptionHandler<? extends Exception, T> DEFAULT_HANDLER = newExceptionHandler(Exception.class, this::badRequest);

    private Map<Class<? extends Throwable>, RestExceptionHandler<? extends Throwable, T>> mapper;

    private List<ExceptionHandler> errorHandlers;

    protected AbstractExceptionsHandler() {
        this(Collections.emptyList());
    }

    protected AbstractExceptionsHandler(Iterable<ExceptionHandler> errorHandlers) {
        List<RestExceptionHandler<? extends Throwable, T>> handlers = List.<RestExceptionHandler<? extends Throwable, T>> of(
                newExceptionHandler(InvalidLifeCyclePhaseException.class, this::badRequest),
                newExceptionHandler(UserTaskTransitionException.class, this::badRequest),
                newExceptionHandler(UserTaskInstanceNotFoundException.class, this::notFound),
                newExceptionHandler(UserTaskInstanceNotAuthorizedException.class, this::forbidden),
                newExceptionHandler(InvalidTransitionException.class, this::badRequest),
                newExceptionHandler(NodeInstanceNotFoundException.class, nodeInstanceNotFoundMessageException(), this::notFound),
                newExceptionHandler(NodeNotFoundException.class, nodeNotFoundMessageException(), this::notFound),
                newExceptionHandler(NotAuthorizedException.class, this::forbidden),
                newExceptionHandler(ProcessInstanceDuplicatedException.class, processInstanceDuplicatedMessageException(), this::conflict),
                newExceptionHandler(ProcessInstanceExecutionException.class, processInstanceExecutionMessageException(), this::internalError),
                newExceptionHandler(ProcessInstanceNotFoundException.class, processInstanceNotFoundExceptionMessageException(), this::notFound),
                newExceptionHandler(WorkItemNotFoundException.class, workItemNotFoundMessageException(), this::notFound),
                newExceptionHandler(VariableViolationException.class, variableViolationMessageException(), this::badRequest),
                newExceptionHandler(WorkItemExecutionException.class, workItemExecutionMessageException(), this::fromErrorCode),
                newExceptionHandler(IllegalArgumentException.class, this::badRequest),
                newExceptionHandler(MessageException.class, this::badRequest));

        this.mapper = new HashMap<>();
        for (RestExceptionHandler<? extends Throwable, T> handler : handlers) {
            this.mapper.put(handler.getType(), handler);
        }
        this.errorHandlers = new ArrayList<>();
        errorHandlers.iterator().forEachRemaining(this.errorHandlers::add);

    }

    private T fromErrorCode(ExceptionBodyMessage message) {
        switch (message.getErrorCode()) {
            case "400":
                return badRequest(message);
            case "403":
                return forbidden(message);
            case "404":
                return notFound(message);
            case "409":
                return conflict(message);
            default:
                return internalError(message);
        }
    }

    protected abstract T badRequest(ExceptionBodyMessage body);

    protected abstract T conflict(ExceptionBodyMessage body);

    protected abstract T internalError(ExceptionBodyMessage body);

    protected abstract T notFound(ExceptionBodyMessage body);

    protected abstract T forbidden(ExceptionBodyMessage body);

    public T mapException(Exception exceptionThrown) {

        var handler = mapper.getOrDefault(exceptionThrown.getClass(), DEFAULT_HANDLER);
        ExceptionBodyMessage message = handler.getContent(exceptionThrown);

        Throwable rootCause = exceptionThrown.getCause();
        while (rootCause != null) {
            if (mapper.containsKey(rootCause.getClass())) {
                handler = mapper.get(rootCause.getClass());
                message.merge(handler.getContent(rootCause));
            }
            rootCause = rootCause.getCause();
        }
        // we invoked the error handlers
        errorHandlers.forEach(e -> e.handle(exceptionThrown));
        T response = handler.buildResponse(message);
        LOG.debug("mapping exception {} with response {}", exceptionThrown, message.getBody());
        return response;
    }
}
