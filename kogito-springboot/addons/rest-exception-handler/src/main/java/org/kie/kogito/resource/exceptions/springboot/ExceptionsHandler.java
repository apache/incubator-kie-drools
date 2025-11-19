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
package org.kie.kogito.resource.exceptions.springboot;

import java.util.List;
import java.util.Map;

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
import org.kie.kogito.resource.exceptions.AbstractExceptionsHandler;
import org.kie.kogito.resource.exceptions.ExceptionBodyMessage;
import org.kie.kogito.usertask.UserTaskInstanceNotAuthorizedException;
import org.kie.kogito.usertask.UserTaskInstanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler extends AbstractExceptionsHandler<ResponseEntity<Map<String, String>>> {

    @Autowired
    public ExceptionsHandler(List<org.kie.kogito.handler.ExceptionHandler> handlers) {
        super(handlers);
    }

    @Override
    protected ResponseEntity<Map<String, String>> badRequest(ExceptionBodyMessage body) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.getBody());
    }

    @Override
    protected ResponseEntity<Map<String, String>> conflict(ExceptionBodyMessage body) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.getBody());
    }

    @Override
    protected ResponseEntity<Map<String, String>> internalError(ExceptionBodyMessage body) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.getBody());
    }

    @Override
    protected ResponseEntity<Map<String, String>> notFound(ExceptionBodyMessage body) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.getBody());
    }

    @Override
    protected ResponseEntity<Map<String, String>> forbidden(ExceptionBodyMessage body) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.getBody());
    }

    @ExceptionHandler(InvalidLifeCyclePhaseException.class)
    public ResponseEntity<Map<String, String>> toResponse(InvalidLifeCyclePhaseException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<Map<String, String>> toResponse(InvalidTransitionException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NodeInstanceNotFoundException.class)
    public ResponseEntity<Map<String, String>> toResponse(NodeInstanceNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> toResponse(NodeNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Map<String, String>> toResponse(NotAuthorizedException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(UserTaskInstanceNotAuthorizedException.class)
    public ResponseEntity<Map<String, String>> toResponse(UserTaskInstanceNotAuthorizedException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceDuplicatedException.class)
    public ResponseEntity<Map<String, String>> toResponse(ProcessInstanceDuplicatedException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceExecutionException.class)
    public ResponseEntity<Map<String, String>> toResponse(ProcessInstanceExecutionException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceNotFoundException.class)
    public ResponseEntity<Map<String, String>> toResponse(ProcessInstanceNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(UserTaskInstanceNotFoundException.class)
    public ResponseEntity<Map<String, String>> toResponse(UserTaskInstanceNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(WorkItemNotFoundException.class)
    public ResponseEntity<Map<String, String>> toResponse(WorkItemNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(WorkItemExecutionException.class)
    public ResponseEntity<Map<String, String>> toResponse(WorkItemExecutionException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(VariableViolationException.class)
    public ResponseEntity<Map<String, String>> toResponse(VariableViolationException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> toResponse(IllegalArgumentException exception) {
        return mapException(exception);
    }

}
