/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.expr.jq;

import org.kie.kogito.jackson.utils.MergeUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.workitems.impl.expr.ParsedExpression;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class JqParsedExpression implements ParsedExpression {

    private Scope scope;
    private JsonQuery query;

    public JqParsedExpression(Scope scope, String expr) {
        this.scope = scope;
        try {
            this.query = JsonQuery.compile(expr, Versions.JQ_1_6);
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to compile expression " + expr, e);
        }
    }

    private interface TypedOutput<T> extends Output {
        T getResult();
    }

    private <T> TypedOutput<T> output(Object context, Class<T> returnClass) {
        TypedOutput<?> out;
        if (Boolean.class.isAssignableFrom(returnClass)) {
            out = new BooleanOutput();
        } else if (String.class.isAssignableFrom(returnClass)) {
            out = new StringOutput();
        } else {
            out = new JsonNodeOutput((JsonNode) context);
        }
        return (TypedOutput<T>) out;
    }

    private static class BooleanOutput implements TypedOutput<Boolean> {

        boolean result;

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            result = out.asBoolean();
        }

        @Override
        public Boolean getResult() {
            return result;
        }

    }

    private static class StringOutput implements TypedOutput<String> {
        StringBuilder sb = new StringBuilder();

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(out.asText());
        }

        @Override
        public String getResult() {
            return sb.toString();
        }

    }

    private static class JsonNodeOutput implements TypedOutput<JsonNode> {

        private JsonNode context;
        private JsonNode result;
        private boolean arrayCreated;

        public JsonNodeOutput(JsonNode context) {
            this.context = context;
        }

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            if (out.isArray() || out.isObject()) {
                MergeUtils.merge(out, context);
            }
            if (this.result == null) {
                this.result = out;
            } else if (!arrayCreated) {
                ArrayNode newNode = ObjectMapperFactory.get().createArrayNode();
                newNode.add(this.result).add(out);
                this.result = newNode;
                arrayCreated = true;
            } else {
                ((ArrayNode) this.result).add(out);
            }
        }

        @Override
        public JsonNode getResult() {
            return result;
        }
    }

    @Override
    public <T> T eval(Object context, Class<T> returnClass) {
        try {
            TypedOutput<T> output = output(context, returnClass);
            query.apply(this.scope, (JsonNode) context, output);
            return output.getResult();
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to evaluate content " + context + " using query " + query, e);
        }
    }
}
