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
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.FunctionJsonNode;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.utils.JsonNodeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class JqExpression implements Expression {

    static final String LANG = "jq";

    private static final Logger logger = LoggerFactory.getLogger(JqExpression.class);
    private final Supplier<Scope> scope;
    private final String expr;
    private final Version version;
    private JsonQuery query;
    private JsonQueryException validationError;

    public JqExpression(Supplier<Scope> scope, String expr, Version version) {
        this.expr = expr;
        this.scope = scope;
        this.version = version;
    }

    private interface TypedOutput extends Output {
        Object getResult();
    }

    private TypedOutput output(Class<?> returnClass) {
        TypedOutput out;
        if (String.class.isAssignableFrom(returnClass)) {
            out = new StringOutput();
        } else if (Collection.class.isAssignableFrom(returnClass)) {
            out = new CollectionOutput();
        } else {
            out = new JsonNodeOutput();
        }
        return out;
    }

    private static class StringOutput implements TypedOutput {
        StringBuilder sb = new StringBuilder();

        @Override
        public void emit(JsonNode out) throws JsonQueryException {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(out.asText());
        }

        @Override
        public Object getResult() {
            return sb.toString();
        }

    }

    private static class CollectionOutput implements TypedOutput {
        Collection<Object> result = new ArrayList<>();

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
        public Object getResult() {
            return result;
        }

    }

    private static class JsonNodeOutput implements TypedOutput {

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

    private Scope getScope(KogitoProcessContext processInfo) {
        Scope childScope = Scope.newChildScope(scope.get());
        childScope.setValue(ExpressionHandlerUtils.SECRET_MAGIC, new FunctionJsonNode(ExpressionHandlerUtils::getSecret));
        childScope.setValue(ExpressionHandlerUtils.CONTEXT_MAGIC, new FunctionJsonNode(ExpressionHandlerUtils.getContextFunction(processInfo)));
        childScope.setValue(ExpressionHandlerUtils.CONST_MAGIC, ExpressionHandlerUtils.getConstants(processInfo));
        return childScope;
    }

    private <T> T eval(JsonNode context, Class<T> returnClass, KogitoProcessContext processInfo) {
        try (JsonNodeContext jsonNode = JsonNodeContext.from(context, processInfo)) {
            TypedOutput output = output(returnClass);
            compile();
            query.apply(getScope(processInfo), jsonNode.getNode(), output);
            return JsonObjectUtils.convertValue(output.getResult(), returnClass);
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to evaluate content " + context + " using expr " + expr, e);
        }
    }

    private void compile() throws JsonQueryException {
        if (this.query == null) {
            try {
                this.query = JsonQuery.compile(expr, version);
            } catch (JsonQueryException ex) {
                validationError = ex;
                throw ex;
            }
        }
    }

    private static final Pattern JQ_FUNCTION_NAME = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

    @Override
    public boolean isValid() {
        try {
            compile();
            if (JQ_FUNCTION_NAME.matcher(expr).matches()) {
                query.apply(scope.get(), ObjectMapperFactory.get().createObjectNode(), out -> {
                });
            }
        } catch (JsonQueryException ex) {
            logger.debug("Invalid expression {}", ex.getMessage());
            return false;
        }
        return validationError == null;
    }

    @Override
    public String asString() {
        return expr;
    }

    @Override
    public Exception validationError() {
        return validationError;
    }

    @Override
    public String lang() {
        return LANG;
    }
}
