/**
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
package org.drools.scenariosimulation.backend.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import org.drools.mvel.MVELSafeHelper;
import org.drools.mvel.util.MVELEvaluator;
import org.drools.scenariosimulation.backend.util.JsonUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.ACTUAL_VALUE_IDENTIFIER;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MALFORMED_MVEL_EXPRESSION;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.compareValues;
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
    public ExpressionEvaluatorResult evaluateUnaryExpression(String rawExpression, Object resultValue, Class<?> resultClass) {
        Map<String, Object> params = new HashMap<>();
        params.put(ACTUAL_VALUE_IDENTIFIER, resultValue);

        Object expressionResult = compileAndExecute(rawExpression, params);
        if (!(expressionResult instanceof Boolean)) {
            // try to compare via compare/equals operators
            return ExpressionEvaluatorResult.of(compareValues(expressionResult, resultValue));
        }
        return ExpressionEvaluatorResult.of((boolean) expressionResult);
    }

    @Override
    public Object evaluateLiteralExpression(String rawExpression, String className, List<String> genericClasses) {
        Object expressionResult = compileAndExecute(rawExpression, new HashMap<>());
        Class<Object> requiredClass = loadClass(className, classLoader);
        if (expressionResult != null && !requiredClass.isAssignableFrom(expressionResult.getClass())) {
            throw new IllegalArgumentException("Cannot assign a '" + expressionResult.getClass().getCanonicalName() +
                                                       "' to '" + requiredClass.getCanonicalName() + "'");
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
            Class<?> type = entry.getValue() != null ? entry.getValue().getClass() : null;
            ctx.addVariable(entry.getKey(), type);
        }

        String expression = cleanExpression(rawExpression);
        Object compiledExpression = MVEL.compileExpression(expression, ctx);
        return evaluator.executeExpression(compiledExpression, params);
    }

    /**
     * The clean works in the following ways:
     * - NOT COLLECTIONS CASE: The given rawExpression without MVEL_ESCAPE_SYMBOL ('#');
     * - COLLECTION CASE: Retrieving the value from rawExpression, which is a JSON String node in this case, removing
     *                    the MVEL_ESCAPE_SYMBOL ('#');
     * In both cases, the given String must start with MVEL_ESCAPE_SYMBOL.
     * All other cases are wrong: a <code>IllegalArgumentException</code> is thrown.
     * @param rawExpression
     * @return
     */
    protected String cleanExpression(String rawExpression) {
        if (rawExpression != null && rawExpression.trim().startsWith(MVEL_ESCAPE_SYMBOL)) {
            return rawExpression.replaceFirst(MVEL_ESCAPE_SYMBOL, "");
        }
        Optional<JsonNode> optionalJSONNode = JsonUtils.convertFromStringToJSONNode(rawExpression);
        if (optionalJSONNode.isPresent()) {
            JsonNode jsonNode = optionalJSONNode.get();
            if (jsonNode.isTextual() && jsonNode.asText() != null && jsonNode.asText().trim().startsWith(MVEL_ESCAPE_SYMBOL)) {
                String expression = jsonNode.asText();
                return expression.replaceFirst(MVEL_ESCAPE_SYMBOL, "");
            }
        }
        throw new IllegalArgumentException(MALFORMED_MVEL_EXPRESSION + "'" + rawExpression + "'");
    }
}
