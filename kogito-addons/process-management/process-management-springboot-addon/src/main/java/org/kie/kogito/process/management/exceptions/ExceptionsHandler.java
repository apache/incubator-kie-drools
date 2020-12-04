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

import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.NodeNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.process.workitem.InvalidTransitionException;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler extends BaseExceptionHandler<ResponseEntity> {

    @Override
    protected <R> ResponseEntity badRequest(R body) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected <R> ResponseEntity conflict(R body) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected <R> ResponseEntity internalError(R body) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected <R> ResponseEntity notFound(R body) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected <R> ResponseEntity forbidden(R body) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(InvalidLifeCyclePhaseException.class)
    public ResponseEntity toResponse(InvalidLifeCyclePhaseException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity toResponse(InvalidTransitionException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NodeInstanceNotFoundException.class)
    public ResponseEntity toResponse(NodeInstanceNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity toResponse(NodeNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity toResponse(NotAuthorizedException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceDuplicatedException.class)
    public ResponseEntity<Object> toResponse(ProcessInstanceDuplicatedException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceExecutionException.class)
    public ResponseEntity toResponse(ProcessInstanceExecutionException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(ProcessInstanceNotFoundException.class)
    public ResponseEntity toResponse(ProcessInstanceNotFoundException exception) {
        return mapException(exception);
    }

    @ExceptionHandler(VariableViolationException.class)
    public ResponseEntity toResponse(VariableViolationException exception) {
        return mapException(exception);
    }
}
