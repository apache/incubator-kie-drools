/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.resource.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.kie.kogito.jobs.service.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

    public static final int DEFAULT_ERROR_CODE = 500;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean logStackTrace;
    private final int errorCode;

    public BaseExceptionMapper(boolean logStackTrace) {
        this(DEFAULT_ERROR_CODE, logStackTrace);
    }

    public BaseExceptionMapper(int errorCode, boolean logStackTrace) {
        this.errorCode = errorCode;
        this.logStackTrace = logStackTrace;
    }

    @Override
    public Response toResponse(T exception) {
        log(exception);
        return buildResponse(exception, errorCode);
    }

    protected Response buildResponse(T exception, int errorCode) {
        return Response.status(errorCode)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(errorMessage(exception)))
                .build();
    }

    protected void log(T exception) {
        if (logStackTrace) {
            logger.error("Handling HTTP Error", exception);
        } else {
            logger.error("Handling HTTP Error {}", exception.getMessage());
        }
    }

    protected String errorMessage(T exception) {
        return exception.getMessage();
    }
}
