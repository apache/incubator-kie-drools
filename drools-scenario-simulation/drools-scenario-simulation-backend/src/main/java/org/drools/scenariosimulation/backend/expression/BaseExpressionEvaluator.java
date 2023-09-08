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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;

import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.getField;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.revertValue;

public class BaseExpressionEvaluator extends AbstractExpressionEvaluator {

    private final ClassLoader classLoader;

    public BaseExpressionEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String fromObjectToExpression(Object value) {
        return revertValue(value);
    }

    @Override
    protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString) {
        if (rawExpression != null && skipEmptyString && rawExpression.isEmpty()) {
            return true;
        }
        if (resultClass == null) {
            return rawExpression == null || rawExpression.isEmpty();
        }
        return BaseExpressionOperator.findOperator(rawExpression).eval(rawExpression, resultValue, resultClass, classLoader);
    }

    @Override
    protected Object internalLiteralEvaluation(String rawValue, String className) {
        return BaseExpressionOperator.findOperator(rawValue).evaluateLiteralExpression(className, rawValue, classLoader);
    }

    @Override
    protected Object extractFieldValue(Object result, String fieldName) {
        try {
            if (result instanceof Map) {
                return ((Map) result).get(fieldName);
            } else {
                Field declaredField = getField(result.getClass(), fieldName);
                declaredField.setAccessible(true);
                return declaredField.get(result);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossible to find field: " + fieldName, e);
        }
    }

    @Override
    protected Object createObject(String className, List<String> genericClasses) {
        if (ScenarioSimulationSharedUtils.isMap(className)) {
            return new HashMap();
        } else {
            try {
                return classLoader.loadClass(className).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Impossible to instantiate " + className, e);
            }
        }
    }

    @Override
    protected void setField(Object toReturn, String fieldName, Object fieldValue) {
        if (toReturn instanceof Map) {
            Map returnMap = (Map) toReturn;
            returnMap.put(fieldName, fieldValue);
        } else {
            try {
                Field declaredField = getField(toReturn.getClass(), fieldName);
                declaredField.setAccessible(true);
                declaredField.set(toReturn, fieldValue);
            } catch (Exception e) {
                throw new IllegalArgumentException("Impossible to set the field " + fieldName, e);
            }
        }
    }

    @Override
    protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
        try {
            if (ScenarioSimulationSharedUtils.isMap(className)) {
                return new AbstractMap.SimpleEntry<>(genericClasses.get(genericClasses.size() - 1), Collections.emptyList());
            }
            Field declaredField = getField(element.getClass(), fieldName);
            Class<?> fieldType = declaredField.getType();
            List<String> genericsString = new ArrayList<>();
            if (declaredField.getGenericType() instanceof ParameterizedType) {
                ParameterizedType generics = (ParameterizedType) declaredField.getGenericType();
                for (Type typeArgument : generics.getActualTypeArguments()) {
                    if (typeArgument instanceof ParameterizedType) {
                        genericsString.add(((ParameterizedType) typeArgument).getRawType().getTypeName());
                    } else {
                        genericsString.add(typeArgument.getTypeName());
                    }
                }
            }
            return new AbstractMap.SimpleEntry<>(fieldType.getCanonicalName(), genericsString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossible to get the field " + fieldName, e);
        }
    }
}
