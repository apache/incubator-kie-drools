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

import org.kie.kogito.handler.ExceptionHandler;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ExceptionsHandler extends AbstractExceptionsHandler<Response> {

    public ExceptionsHandler(Iterable<ExceptionHandler> handlers) {
        super(handlers);
    }

    @Override
    protected Response badRequest(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }

    @Override
    protected Response conflict(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.CONFLICT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }

    @Override
    protected Response internalError(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }

    @Override
    protected Response notFound(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }

    @Override
    protected Response forbidden(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.FORBIDDEN)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }

    @Override
    protected Response preconditionFailed(ExceptionBodyMessage body) {
        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(body.getBody())
                .build();
    }
}
