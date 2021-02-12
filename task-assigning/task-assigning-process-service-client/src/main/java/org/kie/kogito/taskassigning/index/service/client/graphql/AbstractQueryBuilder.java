/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.taskassigning.util.JsonUtils.OBJECT_MAPPER;

public abstract class AbstractQueryBuilder<T extends AbstractQueryBuilder> {

    protected static class VariableDef {

        private String name;
        private Argument argument;

        public VariableDef(String name, Argument argument) {
            this.name = name;
            this.argument = argument;
        }

        public String getName() {
            return name;
        }

        public Argument getArgument() {
            return argument;
        }
    }

    protected String queryName;
    protected List<VariableDef> variableDefs = new ArrayList<>();

    protected abstract String getRequest();

    protected T queryName(String queryName) {
        this.queryName = queryName;
        return cast();
    }

    protected T withVariable(String name, Argument argument) {
        variableDefs.add(new VariableDef(name, argument));
        return cast();
    }

    @SuppressWarnings("unchecked")
    protected T cast() {
        return (T) this;
    }

    protected ObjectNode buildAsJson() {
        if (variableDefs.isEmpty()) {
            throw new IllegalArgumentException("A parametrizable query must have at least one variable definition");
        }
        String request = getRequest();
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("A query request must be defined");
        }

        StringBuilder queryBuilder = new StringBuilder();

        StringBuilder queryHeaderParams = new StringBuilder();
        variableDefs.forEach(variableDef -> appendHeaderParam(queryHeaderParams, variableDef));
        queryBuilder.append("query ").append(queryName)
                .append("(")
                .append(queryHeaderParams)
                .append(")");

        StringBuilder queryBody = new StringBuilder();
        StringBuilder queryBodyParams = new StringBuilder();
        variableDefs.forEach(variableDef -> appendBodyParam(queryBodyParams, variableDef));
        queryBody.append(queryName)
                .append("(").append(queryBodyParams).append(")")
                .append("{").append(request).append("}");

        queryBuilder.append("{")
                .append(queryBody)
                .append("}");

        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        result.put("query", queryBuilder.toString());
        ObjectNode variables = result.objectNode();
        result.set("variables", variables);
        variableDefs.forEach(variableDef -> variables.set(variableDef.getName(), variableDef.getArgument().asJson()));
        return result;
    }

    protected void appendHeaderParam(StringBuilder headerParams, VariableDef variableDef) {
        if (headerParams.length() > 0) {
            headerParams.append(", ");
        }
        headerParams.append("$")
                .append(variableDef.getName())
                .append(": ")
                .append(variableDef.getArgument().getTypeId());
    }

    protected void appendBodyParam(StringBuilder bodyParams, VariableDef variableDef) {
        if (bodyParams.length() > 0) {
            bodyParams.append(", ");
        }
        bodyParams.append(variableDef.getName())
                .append(": $")
                .append(variableDef.getName());
    }
}
