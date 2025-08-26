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
package org.kie.kogito.serverless.workflow.parser.handlers.openapi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.vertx.core.http.HttpMethod;

public class OpenAPIDescriptor {

    private final HttpMethod method;
    private final String path;
    private final Collection<SecurityScheme> schemes;
    private final Set<String> pathParams = new HashSet<>();
    private final Set<String> queryParams = new HashSet<>();
    private final Set<String> headerParams = new HashSet<>();
    private final Optional<String> resultType;

    OpenAPIDescriptor(HttpMethod method, String path, Operation operation, Collection<SecurityScheme> schemes) {
        this.method = method;
        this.path = path;
        this.schemes = schemes;

        this.resultType = operation.getResponses().entrySet().stream().filter(e -> isValidStatusCode(Integer.parseInt(e.getKey())))
                .filter(e -> e.getValue().getContent() != null)
                .flatMap(e -> e.getValue().getContent().keySet().stream()).findFirst();

        if (operation.getParameters() != null) {
            operation.getParameters().forEach(this::addParameter);
        }
    }

    private boolean isValidStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private void addParameter(Parameter parameter) {
        switch (parameter.getIn()) {
            case "query":
                queryParams.add(parameter.getName());
                break;
            case "path":
                pathParams.add(parameter.getName());
                break;
            case "header":
                headerParams.add(parameter.getName());
                break;
            default:
                // all other argument types can be safely ignored
        }
    }

    public Optional<String> getResultType() {
        return resultType;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Set<String> getPathParams() {
        return pathParams;
    }

    public Set<String> getQueryParams() {
        return queryParams;
    }

    public Set<String> getHeaderParams() {
        return headerParams;
    }

    public Collection<SecurityScheme> getSchemes() {
        return schemes;
    }
}
