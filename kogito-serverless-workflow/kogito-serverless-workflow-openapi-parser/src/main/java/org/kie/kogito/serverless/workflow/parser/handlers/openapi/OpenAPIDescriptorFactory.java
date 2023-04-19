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
package org.kie.kogito.serverless.workflow.parser.handlers.openapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.vertx.core.http.HttpMethod;

import static org.kie.kogito.internal.utils.ConversionUtils.toCamelCase;

public class OpenAPIDescriptorFactory {

    public static OpenAPIDescriptor of(OpenAPI openAPI, String operationId) {
        // path to operation map
        Map<String, List<OperationInfo>> operations = collectOperations(openAPI.getPaths(), operationId);
        if (operations.isEmpty()) {
            throw new IllegalArgumentException("Cannot find operation for " + operationId);
        }
        if (operations.size() > 1) {
            // TODO improvement try to infer the right method from arguments
            throw new IllegalArgumentException("There is more than one operation " + operations + " in different paths with name " + operationId);
        }
        Map.Entry<String, List<OperationInfo>> operEntry = operations.entrySet().iterator().next();

        if (operEntry.getValue().size() > 1) {
            // TODO improvement try to infer the right method from arguments
            throw new IllegalArgumentException("There is more than one operation " + operations + " in different methods with name " + operationId + " for path " + operEntry.getKey());
        }
        OperationInfo operation = operEntry.getValue().get(0);
        return new OpenAPIDescriptor(operation.getMethod(), operEntry.getKey(), operation.getOperation(), getSchemes(openAPI, operation.getOperation()));
    }

    private static Collection<SecurityScheme> getSchemes(OpenAPI openAPI, Operation operation) {
        Set<String> schemeNames = new HashSet<>();
        List<SecurityRequirement> security = operation.getSecurity();
        if (security != null) {
            if (security.isEmpty()) {
                return Collections.emptyList();
            }
            // do not care if and or or, we try to fill all 
            security.forEach(s -> schemeNames.addAll(s.keySet()));
        }

        if (openAPI.getComponents() != null) {
            Map<String, SecurityScheme> schemes = openAPI.getComponents().getSecuritySchemes();
            if (schemes != null) {
                if (!schemeNames.isEmpty()) {
                    schemes = new HashMap<>(schemes);
                    schemes.keySet().retainAll(schemeNames);
                }
                return schemes.values();
            }
        }
        return Collections.emptyList();
    }

    private static void checkOperation(String path, String operationId, HttpMethod method, Operation operation, Map<String, List<OperationInfo>> map) {
        if (operation != null && (operationId.equals(operation.getOperationId()) || operationId.equals(toCamelCase(operation.getOperationId())))) {
            map.computeIfAbsent(path, k -> new ArrayList<>()).add(new OperationInfo(method, operation));
        }
    }

    private static Map<String, List<OperationInfo>> collectOperations(Paths paths, String operationId) {
        Map<String, List<OperationInfo>> result = new HashMap<>();
        for (Entry<String, PathItem> path : paths.entrySet()) {
            checkOperation(path.getKey(), operationId, HttpMethod.GET, path.getValue().getGet(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.HEAD, path.getValue().getHead(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.DELETE, path.getValue().getDelete(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.PATCH, path.getValue().getPatch(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.POST, path.getValue().getPost(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.PUT, path.getValue().getPut(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.OPTIONS, path.getValue().getOptions(), result);
            checkOperation(path.getKey(), operationId, HttpMethod.TRACE, path.getValue().getTrace(), result);
        }
        return result;
    }

    private static class OperationInfo {
        private final HttpMethod method;
        private final Operation operation;

        public OperationInfo(HttpMethod method, Operation operation) {
            this.method = method;
            this.operation = operation;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public Operation getOperation() {
            return operation;
        }

        @Override
        public String toString() {
            return "OperationInfo [method=" + method + ", operation=" + operation + "]";
        }
    }

    public static String getDefaultURL(OpenAPI openAPI, String defaultBase) {
        List<Server> servers = openAPI.getServers();
        return servers != null && !servers.isEmpty() ? servers.get(0).getUrl() : defaultBase;
    }

    private OpenAPIDescriptorFactory() {
    }
}
