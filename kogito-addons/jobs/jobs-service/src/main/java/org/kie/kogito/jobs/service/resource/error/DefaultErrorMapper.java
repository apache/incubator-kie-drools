/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.resource.error;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.kie.kogito.jobs.service.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DefaultErrorMapper implements ExceptionMapper<Exception> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultErrorMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("Sending error response", exception);
        return Response.status(Optional.ofNullable(exception)
                                       .filter(WebApplicationException.class::isInstance)
                                       .map(WebApplicationException.class::cast)
                                       .map(WebApplicationException::getResponse)
                                       .map(Response::getStatus)
                                       .orElse(500))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}