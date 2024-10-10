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
package org.kie.kogito.expr.jq;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.FunctionJsonNode;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.jackson.utils.PrefixJsonNode;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.utils.VariablesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.internal.javacc.ExpressionParser;
import net.thisptr.jackson.jq.internal.tree.FunctionCall;
import net.thisptr.jackson.jq.internal.tree.StringInterpolation;
import net.thisptr.jackson.jq.internal.tree.binaryop.BinaryOperatorExpression;

public class JqExpression implements Expression {

    static final String LANG = "jq";

    private static final Logger logger = LoggerFactory.getLogger(JqExpression.class);
    private final Map<Class<? extends net.thisptr.jackson.jq.Expression>, Collection<Field>> declaredFieldsMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends net.thisptr.jackson.jq.Expression>, Collection<Field>> allFieldsMap = new ConcurrentHashMap<>();

    private final Supplier<Scope> scope;
    private final String expr;

    private net.thisptr.jackson.jq.Expression internalExpr;
    private JsonQueryException validationError;
    private static Field rhsField;

    static {
        try {
            rhsField = BinaryOperatorExpression.class.getDeclaredField("rhs");
            rhsField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            logger.warn("Unexpected exception while resolving rhs field", e);
        }
    }

    public JqExpression(Supplier<Scope> scope, String expr, Version version) {
        this.expr = expr;
        this.scope = scope;
        try {
            this.internalExpr = compile(version);
            checkFunctionCall(internalExpr);
        } catch (JsonQueryException ex) {
            validationError = ex;
        }
    }

    private net.thisptr.jackson.jq.Expression compile(Version version) throws JsonQueryException {
        net.thisptr.jackson.jq.Expression expression;
        try {
            expression = ExpressionParser.compile(expr, version);
        } catch (JsonQueryException ex) {
            expression = handleStringInterpolation(version).orElseThrow(() -> ex);
        }
        checkFunctionCall(expression);
        return expression;
    }

    private Optional<net.thisptr.jackson.jq.Expression> handleStringInterpolation(Version version) {
        if (!expr.startsWith("\"")) {
            try {
                net.thisptr.jackson.jq.Expression expression = ExpressionParser.compile("\"" + expr + "\"", version);
                if (expression instanceof StringInterpolation) {
                    return Optional.of(expression);
                }
            } catch (JsonQueryException ex) {
                // ignoring it
            }
        }
        return Optional.empty();
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
            if (!out.isNull() && out.asText() != null) {
                sb.append(out.asText());
            }
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
        childScope.setValue(ExpressionHandlerUtils.SECRET_MAGIC, new PrefixJsonNode<>(ExpressionHandlerUtils::getOptionalSecret));
        childScope.setValue(ExpressionHandlerUtils.CONTEXT_MAGIC, new FunctionJsonNode(ExpressionHandlerUtils.getContextFunction(processInfo)));
        childScope.setValue(ExpressionHandlerUtils.CONST_MAGIC, ExpressionHandlerUtils.getConstants(processInfo));
        VariablesHelper.getAdditionalVariables(processInfo).forEach(childScope::setValue);
        return childScope;
    }

    private <T> T eval(JsonNode context, Class<T> returnClass, KogitoProcessContext processInfo) {
        if (validationError != null) {
            throw new IllegalArgumentException("Unable to evaluate content " + context + " using expr " + expr, validationError);
        }
        TypedOutput output = output(returnClass);
        try {
            internalExpr.apply(getScope(processInfo), context, output);
            return JsonObjectUtils.convertValue(output.getResult(), returnClass);
        } catch (JsonQueryException e) {
            throw new IllegalArgumentException("Unable to evaluate content " + context + " using expr " + expr, e);
        }
    }

    @Override
    public boolean isValid() {
        return validationError == null;
    }

    private void checkFunctionCall(net.thisptr.jackson.jq.Expression toCheck) throws JsonQueryException {
        if (toCheck instanceof FunctionCall) {
            toCheck.apply(scope.get(), ObjectMapperFactory.get().createObjectNode(), out -> {
            });
        } else if (toCheck instanceof BinaryOperatorExpression) {
            if (rhsField != null) {
                try {
                    checkFunctionCall((net.thisptr.jackson.jq.Expression) rhsField.get(toCheck));
                } catch (ReflectiveOperationException e) {
                    logger.warn("Ignoring unexpected error {} while accesing field {} for class{} and expression {}", e.getMessage(), rhsField.getName(), toCheck.getClass(), expr);
                }
            }
        } else if (toCheck != null) {
            for (Field f : getAllExprFields(toCheck))
                try {
                    checkFunctionCall((net.thisptr.jackson.jq.Expression) f.get(toCheck));
                } catch (ReflectiveOperationException e) {
                    logger.warn("Ignoring unexpected error {} while accesing field {} for class{} and expression {}", e.getMessage(), f.getName(), toCheck.getClass(), expr);
                }
        }
    }

    private Collection<Field> getAllExprFields(net.thisptr.jackson.jq.Expression toCheck) {
        return allFieldsMap.computeIfAbsent(toCheck.getClass(), this::getAllExprFields);
    }

    private Collection<Field> getAllExprFields(Class<? extends net.thisptr.jackson.jq.Expression> clazz) {
        Collection<Field> fields = new HashSet<>();
        Class<?> currentClass = clazz;
        do {
            fields.addAll(declaredFieldsMap.computeIfAbsent(currentClass.asSubclass(net.thisptr.jackson.jq.Expression.class), this::getDeclaredExprFields));
            currentClass = currentClass.getSuperclass();
        } while (net.thisptr.jackson.jq.Expression.class.isAssignableFrom(currentClass));
        return fields;
    }

    private Collection<Field> getDeclaredExprFields(Class<? extends net.thisptr.jackson.jq.Expression> clazz) {
        Collection<Field> fields = new HashSet<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (net.thisptr.jackson.jq.Expression.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                fields.add(f);
            }
        }
        return fields;
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
