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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class JqExpression implements Expression {

    private static final Logger logger = LoggerFactory.getLogger(JqExpression.class);

    private final Supplier<Scope> scope;
    private final String expr;
    private JsonQuery query;
    private String compiledExpr;

    public JqExpression(Supplier<Scope> scope, String expr) {
        this.expr = expr;
        this.compiledExpr = expr;
        this.scope = scope;
    }

    private interface TypedOutput<T> extends Output {
        T getResult();
    }

    private <T> TypedOutput<T> output(Class<T> returnClass) {
        TypedOutput<?> out;
        if (Boolean.class.isAssignableFrom(returnClass)) {
            out = new BooleanOutput();
        } else if (String.class.isAssignableFrom(returnClass)) {
            out = new StringOutput();
        } else if (Collection.class.isAssignableFrom(returnClass)) {
            out = new CollectionOutput();
        } else {
            out = new JsonNodeOutput();
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

    private static class CollectionOutput implements TypedOutput<Collection> {
        Collection result = new ArrayList<>();

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            Object obj = JsonObjectUtils.toJavaValue(out);
            if (obj instanceof Collection)
                result.addAll((Collection) obj);
            else {
                result.add(obj);
            }
        }

        @Override
        public Collection<?> getResult() {
            return result;
        }

    }

    private static class JsonNodeOutput implements TypedOutput<JsonNode> {

        private JsonNode result;
        private boolean arrayCreated;

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
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
    public <T> T eval(Object target, Class<T> returnClass, KogitoProcessContext context) {
        return eval(JsonObjectUtils.fromValue(target), returnClass, context);
    }

    @Override
    public void assign(Object target, Object value, KogitoProcessContext context) {
        JsonNode targetNode = JsonObjectUtils.fromValue(target);
        ExpressionHandlerUtils.assign(targetNode, eval(targetNode, JsonNode.class, context), JsonObjectUtils.fromValue(value), expr);
    }

    private <T> T eval(JsonNode context, Class<T> returnClass, KogitoProcessContext processInfo) {
        try {
            TypedOutput<T> output = output(returnClass);
            compile(Optional.ofNullable(processInfo));
            query.apply(scope.get(), context, output);
            return output.getResult();
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to evaluate content " + context + " using expr " + expr, e);
        }
    }

    private void compile(Optional<KogitoProcessContext> context) throws JsonQueryException {
        String resolvedExpr = ExpressionHandlerUtils.prepareExpr(expr, context, JqExpressionHandler::inject);
        logger.debug("Resolved expr {}", resolvedExpr);
        if (this.query == null || !resolvedExpr.equals(compiledExpr)) {
            compiledExpr = resolvedExpr;
            logger.debug("Compiled expr {}", compiledExpr);
            this.query = JsonQuery.compile(compiledExpr, Versions.JQ_1_6);
        }
    }

    @Override
    public boolean isValid(Optional<KogitoProcessContext> context) {
        try {
            compile(context);
            // jq considers a string not containing any special char a valid one, for those ones, validate with a null context
            if (isConstantString()) {
                query.apply(scope.get(), NullNode.instance, output(JsonNode.class));
            }
            return true;
        } catch (JsonQueryException e) {
            return false;
        }
    }

    private boolean isConstantString() {
        return compiledExpr.chars().allMatch(Character::isJavaIdentifierPart);
    }

    @Override
    public String asString() {
        return expr;
    }
}
