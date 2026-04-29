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
package org.drools.swagger.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.drools.swagger.model.ExecutionRequest;
import org.drools.swagger.model.ExecutionResponse;
import org.drools.swagger.model.FactTypeInfo;
import org.drools.swagger.model.PackageInfo;
import org.drools.swagger.model.RuleInfo;
import org.drools.swagger.service.RuleExecutionService;
import org.drools.swagger.service.RuleIntrospectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiHandler.class);

    private final RuleIntrospectionService introspectionService;
    private final RuleExecutionService executionService;
    private final ObjectMapper objectMapper;

    public ApiHandler(RuleIntrospectionService introspectionService, RuleExecutionService executionService) {
        this.introspectionService = introspectionService;
        this.executionService = executionService;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            Object result = routeRequest(path, method, exchange);
            sendJson(exchange, 200, result);
        } catch (NotFoundException e) {
            sendJson(exchange, 404, Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            sendJson(exchange, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            LOG.error("API error: {}", path, e);
            sendJson(exchange, 500, Map.of("error", e.getMessage()));
        }
    }

    private Object routeRequest(String path, String method, HttpExchange exchange) throws Exception {
        String apiPath = path.startsWith("/api") ? path.substring(4) : path;

        if (apiPath.equals("/summary") && "GET".equalsIgnoreCase(method)) {
            return introspectionService.getSummary();
        }

        if (apiPath.equals("/packages") && "GET".equalsIgnoreCase(method)) {
            return introspectionService.getAllPackages();
        }

        if (apiPath.startsWith("/packages/") && "GET".equalsIgnoreCase(method)) {
            return handlePackageRoutes(apiPath.substring("/packages/".length()));
        }

        if (apiPath.equals("/execute") && "POST".equalsIgnoreCase(method)) {
            return handleExecution(exchange);
        }

        throw new NotFoundException("Unknown API endpoint: " + path);
    }

    private Object handlePackageRoutes(String subPath) {
        String[] parts = subPath.split("/");
        String packageName = decodePackageName(parts[0]);

        if (parts.length == 1) {
            PackageInfo pkg = introspectionService.getPackage(packageName);
            if (pkg == null) {
                throw new NotFoundException("Package not found: " + packageName);
            }
            return pkg;
        }

        if (parts.length == 2 && "rules".equals(parts[1])) {
            return introspectionService.getRulesInPackage(packageName);
        }

        if (parts.length == 2 && "facts".equals(parts[1])) {
            return introspectionService.getFactTypesInPackage(packageName);
        }

        if (parts.length == 3 && "rules".equals(parts[1])) {
            String ruleName = decode(parts[2]);
            RuleInfo rule = introspectionService.getRule(packageName, ruleName);
            if (rule == null) {
                throw new NotFoundException("Rule not found: " + packageName + "." + ruleName);
            }
            return rule;
        }

        if (parts.length == 3 && "facts".equals(parts[1])) {
            String typeName = decode(parts[2]);
            FactTypeInfo factType = introspectionService.getFactType(packageName, typeName);
            if (factType == null) {
                throw new NotFoundException("Fact type not found: " + packageName + "." + typeName);
            }
            return factType;
        }

        throw new NotFoundException("Unknown route: " + subPath);
    }

    private ExecutionResponse handleExecution(HttpExchange exchange) throws Exception {
        byte[] body = exchange.getRequestBody().readAllBytes();
        if (body.length == 0) {
            throw new BadRequestException("Request body is required");
        }
        ExecutionRequest request = objectMapper.readValue(body, ExecutionRequest.class);
        return executionService.execute(request);
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] responseBytes = objectMapper.writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String decodePackageName(String encoded) {
        return decode(encoded).replace('~', '.');
    }

    private String decode(String value) {
        return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    static class NotFoundException extends RuntimeException {
        NotFoundException(String message) {
            super(message);
        }
    }

    static class BadRequestException extends RuntimeException {
        BadRequestException(String message) {
            super(message);
        }
    }
}
