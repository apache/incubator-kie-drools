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

import org.drools.core.util.MVELSafeHelper;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

public class MVELExpressionEvaluator implements ExpressionEvaluator {

    private final ParserConfiguration config;
    private MVELEvaluator evaluator = MVELSafeHelper.getEvaluator();

    public MVELExpressionEvaluator(ClassLoader classLoader) {
        this.config = new ParserConfiguration();
        config.setClassLoader(classLoader);
    }

    @Override
    public boolean evaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
            // FIXME to test with null
        if (!(rawExpression instanceof String)) {
            throw new IllegalArgumentException("Raw value should be a String and not a '" + rawExpression.getClass().getCanonicalName() + "'");
        }

        Map<String, Object> params = new HashMap<>();
        // FIXME move $value to string constant
        params.put("$value", resultValue);

        Object expressionResult = compileAndExecute((String) rawExpression, params);
        if(!(expressionResult instanceof Boolean)) {
            throw new IllegalArgumentException("Wrong expression return type");
        }
        return (boolean) expressionResult;
    }

    @Override
    public Object evaluateLiteralExpression(String className, List<String> genericClasses, Object rawExpression) {
        // FIXME to test with null
        if (!(rawExpression instanceof String)) {
            throw new IllegalArgumentException("Raw value should be a String and not a '" + rawExpression.getClass().getCanonicalName() + "'");
        }
        return compileAndExecute((String) rawExpression, Collections.emptyMap());
    }

    @Override
    public String fromObjectToExpression(Object value) {
        throw new UnsupportedOperationException("Impossible to revert an MVEL expression");
    }

    @SuppressWarnings("unchecked")
    protected <T> T compileAndExecute(String expression, Map<String, Object> params) {
        ParserContext ctx = new ParserContext(this.config);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            ctx.addVariable(entry.getKey(), entry.getValue().getClass());
        }

        Object compiledExpression = MVEL.compileExpression(expression, ctx);
        return (T) evaluator.executeExpression(compiledExpression, params);
    }
}
