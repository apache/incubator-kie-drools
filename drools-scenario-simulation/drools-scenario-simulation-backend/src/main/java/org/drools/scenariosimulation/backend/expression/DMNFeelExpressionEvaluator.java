/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DMNFeelExpressionEvaluator extends AbstractExpressionEvaluator {

    private final FEEL feel = FEEL.newInstance();
    private final ClassLoader classLoader;

    public DMNFeelExpressionEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean evaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
        if (rawExpression != null && !(rawExpression instanceof String)) {
            throw new IllegalArgumentException("Raw expression should be a string");
        }

        return commonEvaluateUnaryExpression(rawExpression, resultValue, resultClass);
    }

    @Override
    public Object evaluateLiteralExpression(String className, List<String> genericClasses, Object raw) {
        if (!(raw instanceof String)) {
            return raw;
        }

        return commonEvaluationLiteralExpression(className, genericClasses, (String) raw);
    }

    private EvaluationContext newEvaluationContext() {
        return new EvaluationContextImpl(classLoader, new FEELEventListenersManager());
    }

    @Override
    protected Object internalLiteralEvaluation(String raw, String className) {
        EvaluationContext evaluationContext = newEvaluationContext();
        return feel.evaluate(raw, evaluationContext);
    }

    @Override
    protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString) {
        if (rawExpression != null && skipEmptyString && rawExpression.isEmpty()) {
            return true;
        }
        EvaluationContext evaluationContext = newEvaluationContext();
        List<UnaryTest> unaryTests = feel.evaluateUnaryTests(rawExpression);
        if (unaryTests.size() < 1) {
            throw new IllegalArgumentException("Impossible to parse the expression '" + rawExpression + "'");
        }
        return unaryTests.stream().allMatch(unaryTest -> unaryTest.apply(evaluationContext, resultValue));
    }

    @Override
    protected Object extractFieldValue(Object result, String fieldName) {
        return ((Map<String, Object>) result).get(fieldName);
    }

    @Override
    protected Object createObject(String className, List<String> genericClasses) {
        return new HashMap<String, Object>();
    }

    @Override
    protected void setField(Object toReturn, String fieldName, Object fieldValue) {
        Map<String, Object> returnMap = (Map<String, Object>) toReturn;
        returnMap.put(fieldName, fieldValue);
    }

    /**
     * This is not used for DMN
     * @param element
     * @param fieldName
     * @param className
     * @param genericClasses
     * @return
     */
    @Override
    protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
        return new AbstractMap.SimpleEntry<>("", Collections.singletonList(""));
    }
}
