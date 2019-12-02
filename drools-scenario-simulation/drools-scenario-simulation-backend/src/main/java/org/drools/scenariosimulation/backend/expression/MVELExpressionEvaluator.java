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

package org.drools.scenariosimulation.backend.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.addon.MVELEvaluator;
import org.drools.core.util.MVELSafeHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.ACTUAL_VALUE_IDENTIFIER;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.loadClass;

public class MVELExpressionEvaluator implements ExpressionEvaluator {

    private final ParserConfiguration config;
    private final MVELEvaluator evaluator = MVELSafeHelper.getEvaluator();
    private final ClassLoader classLoader;

    public MVELExpressionEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.config = new ParserConfiguration();
        config.setClassLoader(classLoader);
    }

    @Override
    public boolean evaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
        if (!(rawExpression instanceof String)) {
            String rawClass = rawExpression == null ? null : rawExpression.getClass().getCanonicalName();
            throw new IllegalArgumentException("Raw expression should be a String and not a '" + rawClass + "'");
        }

        Map<String, Object> params = new HashMap<>();
        params.put(ACTUAL_VALUE_IDENTIFIER, resultValue);

        Object expressionResult = compileAndExecute((String) rawExpression, params);
        if (!(expressionResult instanceof Boolean)) {
            // try to compare via compare/equals operators
            return BaseExpressionOperator.EQUALS.eval(expressionResult, resultValue, resultClass, classLoader);
        }
        return (boolean) expressionResult;
    }

    @Override
    public Object evaluateLiteralExpression(String className, List<String> genericClasses, Object rawExpression) {
        if (!(rawExpression instanceof String)) {
            throw new IllegalArgumentException("Raw expression should be a String and not a '" + rawExpression.getClass().getCanonicalName() + "'");
        }
        Object expressionResult = compileAndExecute((String) rawExpression, Collections.emptyMap());
        Class<Object> requiredClass = loadClass(className, classLoader);
        if (expressionResult != null && !requiredClass.isAssignableFrom(expressionResult.getClass())) {
            throw new IllegalArgumentException("Cannot assign a '" + expressionResult.getClass().getCanonicalName() +
                                                       "' to '" + requiredClass.getCanonicalName());
        }
        return expressionResult;
    }

    @Override
    public String fromObjectToExpression(Object value) {
        throw new UnsupportedOperationException("The condition has not been satisfied");
    }

    protected Object compileAndExecute(String rawExpression, Map<String, Object> params) {
        ParserContext ctx = new ParserContext(this.config);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            ctx.addVariable(entry.getKey(), entry.getValue().getClass());
        }

        String expression = cleanExpression(rawExpression);
        Object compiledExpression = MVEL.compileExpression(expression, ctx);
        return evaluator.executeExpression(compiledExpression, params);
    }

    protected String cleanExpression(String rawExpression) {
        if (!rawExpression.trim().startsWith(MVEL_ESCAPE_SYMBOL)) {
            throw new IllegalArgumentException("Malformed MVEL expression '" + rawExpression + "'");
        }
        return rawExpression.replaceFirst(MVEL_ESCAPE_SYMBOL, "");
    }
}
