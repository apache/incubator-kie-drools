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
package org.kie.dmn.feel.runtime.functions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.feel.util.CoerceUtil.coerceParams;

public class BaseFEELFunctionHelper {

    private final static Logger logger = LoggerFactory.getLogger(BaseFEELFunctionHelper.class);

    static Object[] getAdjustedParametersForMethod(EvaluationContext ctx, Object[] params, boolean isNamedParams,
                                                   Method m) {
        logger.trace("getAdjustedParametersForMethod {} {} {} {}", ctx, params, isNamedParams, m);
        Object[] toReturn = addCtxParamIfRequired(ctx, params, isNamedParams, m);
        Class<?>[] parameterTypes = m.getParameterTypes();
        if (isNamedParams) {
            // This is inherently frail because it expects that, if, the first parameter is NamedParameter and the
            // function is a CustomFunction, then all parameters are NamedParameter
            NamedParameter[] namedParams =
                    Arrays.stream(toReturn).map(NamedParameter.class::cast).toArray(NamedParameter[]::new);
            toReturn = BaseFEELFunctionHelper.calculateActualParams(m, namedParams);
            if (toReturn == null) {
                // incompatible method
                return null;
            }
        } else if (toReturn.length > 0) {
            // if named parameters, then it has been adjusted already in the calculateActualParams method,
            // otherwise adjust here
            toReturn = adjustForVariableParameters(toReturn, parameterTypes);
        }
        toReturn = adjustByCoercion(parameterTypes, toReturn);
        return toReturn;
    }

    /**
     * This method check if the input parameters, set inside the given <code>CandidateMethod</code>,
     * could match the given <code>parameterTypes</code>, eventually <b>coerced</b>.
     * In case of match with coercion, the given <code>CandidateMethod</code> is updated.
     * @param parameterTypes
     * @param actualParams
     * @return <code>true</code> if successfully matched, <code>false</code> otherwise
     */
    static Object[] adjustByCoercion(Class<?>[] parameterTypes, Object[] actualParams) {
        logger.trace("adjustByCoercion {} {}", parameterTypes, actualParams);
        Object[] toReturn = actualParams;
        int counter = Math.min(parameterTypes.length, actualParams.length);
        for (int i = 0; i < counter; i++) {
            if (actualParams[i] != null) {
                Class<?> currentIdxActualParameterType = actualParams[i].getClass();
                Class<?> expectedParameterType = parameterTypes[i];
                if (!expectedParameterType.isAssignableFrom(currentIdxActualParameterType)) {
                    Optional<Object[]> coercedParams = coerceParams(currentIdxActualParameterType,
                                                                    expectedParameterType, toReturn, i);
                    if (coercedParams.isPresent()) {
                        toReturn = coercedParams.get();
                        continue;
                    }
                    return null;
                }
            }
        }
        return toReturn;
    }

    /**
     * This method check if the input parameters, set inside the given <code>CandidateMethod</code>,
     * could match the given <code>parameterTypes</code>, eventually <b>coerced</b>.
     * In case of match with coercion, the given <code>CandidateMethod</code> is updated.
     * @param parameterTypes
     * @param cm
     * @return <code>true</code> if successfully matched, <code>false</code> otherwise
     */
    static boolean areParametersMatching(Class<?>[] parameterTypes, BaseFEELFunction.CandidateMethod cm) {
        logger.trace("areParametersMatching {} {}", parameterTypes, cm);
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> currentIdxActualParameterType = cm.getActualClasses()[i];
            Class<?> expectedParameterType = parameterTypes[i];
            if (currentIdxActualParameterType != null && !expectedParameterType.isAssignableFrom(currentIdxActualParameterType)) {
                Optional<Object[]> coercedParams = coerceParams(currentIdxActualParameterType, expectedParameterType,
                                                                cm.getActualParams(), i);
                if (coercedParams.isPresent()) {
                    cm.setActualParams(coercedParams.get());
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * This method insert <code>context</code> reference inside the given parameters, if the given
     * <code>Method</code> signature include it.
     * Depending on the <code>isNamedParams</code>, the reference could be the given <code>EvaluationContext</code>
     * itself, or a <code>NamedParameter</code>
     * @param ctx
     * @param params
     * @param isNamedParams
     * @param m
     * @return
     */
    static Object[] addCtxParamIfRequired(EvaluationContext ctx, Object[] params, boolean isNamedParams, Method m) {
        logger.trace("addCtxParamIfRequired {} {} {} {}", ctx, params, isNamedParams, m);
        Object[] actualParams;
        // Here, we check if any of the parameters is an EvaluationContext
        boolean injectCtx = Arrays.stream(m.getParameterTypes()).anyMatch(EvaluationContext.class::isAssignableFrom);
        if (injectCtx) {
            actualParams = new Object[params.length + 1];
            int j = 0;
            for (int i = 0; i < m.getParameterCount(); i++) {
                if (EvaluationContext.class.isAssignableFrom(m.getParameterTypes()[i])) {
                    if (isNamedParams) {
                        actualParams[i] = new NamedParameter("ctx", ctx);
                    } else {
                        actualParams[i] = ctx;
                    }
                } else if (j < params.length) {
                    actualParams[i] = params[j];
                    j++;
                }
            }
        } else {
            actualParams = params;
        }
        return actualParams;
    }

    /**
     * Method to retrieve the actual parameters from the given <code>NamedParameter[]</code>
     * It returns <code>null</code> if the actual parameters does not match with the <code>Method</code> ones
     * @param m
     * @param params
     * @return an <code>Object[]</code> with mapped values, or <code>null</code> if the mapping has not been possible
     * for all <code>params</code>
     */
    static Object[] calculateActualParams(Method m, NamedParameter[] params) {
        logger.trace("calculateActualParams {} {}", m, params);
        List<String> names = getParametersNames(m);
        Object[] actualParams = new Object[names.size()];
        boolean isVariableParameters =
                m.getParameterCount() > 0 && m.getParameterTypes()[m.getParameterCount() - 1].isArray();
        String variableParamPrefix = isVariableParameters ? names.get(names.size() - 1) : null;
        List<Object> variableParams = isVariableParameters ? new ArrayList<>() : null;
        for (NamedParameter np : params) {
            if (!calculateActualParam(np, names, actualParams, isVariableParameters, variableParamPrefix,
                                      variableParams)) {
                return null;
            }
        }
        if (isVariableParameters) {
            actualParams[actualParams.length - 1] = variableParams.toArray();
        }
        return actualParams;
    }

    /**
     * Method to populate the given <code>actualParams</code> or <code>variableParams</code> with values extracted
     * from <code>NamedParameter</code>
     * @param np
     * @param names
     * @param actualParams
     * @param isVariableParameters
     * @param variableParamPrefix
     * @param variableParams
     * @return <code>true</code> if a mapping has been found, <code>false</code> otherwise
     */
    static boolean calculateActualParam(NamedParameter np, List<String> names, Object[] actualParams,
                                        boolean isVariableParameters, String variableParamPrefix,
                                        List<Object> variableParams) {
        logger.trace("calculateActualParam {} {} {} {} {} {}", np, names, actualParams, isVariableParameters, variableParamPrefix, variableParams);
        if (names.contains(np.getName())) {
            actualParams[names.indexOf(np.getName())] = np.getValue();
            return true;
        } else if (isVariableParameters) {
            return calculateActualParamVariableParameters(np, variableParamPrefix, variableParams);
        } else {
            // invalid parameter, method is incompatible
            return false;
        }
    }

    /**
     * Method to populate the given <code>variableParams</code> with values extracted from <code>NamedParameter</code>
     * @param np
     * @param variableParamPrefix
     * @param variableParams
     * @return <code>true</code> if a mapping has been found, <code>false</code> otherwise
     */
    static boolean calculateActualParamVariableParameters(NamedParameter np, String variableParamPrefix,
                                                          List<Object> variableParams) {
        logger.trace("calculateActualParamVariableParameters {} {} {}", np, variableParamPrefix, variableParams);
        // check if it is a variable parameters method
        if (np.getName().matches(variableParamPrefix + "\\d+")) {
            int index = Integer.parseInt(np.getName().substring(variableParamPrefix.length())) - 1;
            if (variableParams.size() <= index) {
                for (int i = variableParams.size(); i < index; i++) {
                    // need to add nulls in case the user skipped indexes
                    variableParams.add(null);
                }
                variableParams.add(np.getValue());
            } else {
                variableParams.set(index, np.getValue());
            }
        } else {
            // invalid parameter, method is incompatible
            return false;
        }
        return true;
    }

    /**
     * Retrieves the names of the parameters from the given <code>Method</code>,
     * from the ones annotated with <code>ParameterName</code>
     * @param m
     * @return
     */
    static List<String> getParametersNames(Method m) {
        logger.trace("getParametersNames {}", m);
        Annotation[][] pas = m.getParameterAnnotations();
        List<String> toReturn = new ArrayList<>(m.getParameterCount());
        for (int i = 0; i < m.getParameterCount(); i++) {
            for (int p = 0; p < pas[i].length; i++) {
                if (pas[i][p] instanceof ParameterName) {
                    toReturn.add(((ParameterName) pas[i][p]).value());
                    break;
                }
            }
            if (toReturn.get(i) == null) {
                // no name found
                return null;
            }
        }
        return toReturn;
    }

    /**
     * Method invoked by <code>CustomFunction</code>.
     * It refactors the input parameters to match the order defined in the <code>CustomFunction</code>,
     * returning the actual value of the given <code>params</code>
     * @param params
     * @param pnames the parameters defined in the <code>CustomFunction</code>
     * @return
     */
    static Object[] rearrangeParameters(NamedParameter[] params, List<String> pnames) {
        logger.trace("rearrangeParameters {} {}", params, pnames);
        if (pnames.isEmpty()) {
            return params;
        } else {
            Object[] actualParams = new Object[pnames.size()];
            for (int i = 0; i < actualParams.length; i++) {
                for (int j = 0; j < params.length; j++) {
                    if (params[j].getName().equals(pnames.get(i))) {
                        actualParams[i] = params[j].getValue();
                        break;
                    }
                }
            }
            return actualParams;
        }
    }

    /**
     * Adjust CandidateMethod considering var args signature.
     * It converts a series of object to an array, if the last parameter type is an array.
     * It is needed to differentiate function(list) from function(n0...nx), e.g.
     * sum([1,2,3]) = 6
     * sum(1,2,3) = 6
     */
    static Object[] adjustForVariableParameters(Object[] actualParams, Class<?>[] parameterTypes) {
        logger.trace("adjustForVariableParameters {} {}", actualParams, parameterTypes);
        if (parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].isArray()) {
            // then it is a variable parameters function call
            Object[] toReturn = new Object[parameterTypes.length];
            if (toReturn.length > 1) {
                System.arraycopy(actualParams, 0, toReturn, 0, toReturn.length - 1);
            }
            Object[] remaining = new Object[actualParams.length - parameterTypes.length + 1];
            toReturn[toReturn.length - 1] = remaining;
            System.arraycopy(actualParams, parameterTypes.length - 1, remaining, 0, remaining.length);
            return toReturn;
        } else {
            return actualParams;
        }
    }

    /**
     * Adjust CandidateMethod considering var args signature.
     * It converts a series of object to an array, if the last parameter type is an array.
     * It is needed to differentiate function(list) from function(n0...nx), e.g.
     * sum([1,2,3]) = 6
     * sum(1,2,3) = 6
     */
    static void adjustForVariableParameters(BaseFEELFunction.CandidateMethod cm, Class<?>[] parameterTypes) {
        logger.trace("adjustForVariableParameters {} {}", cm, parameterTypes);
        if (parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].isArray()) {
            // then it is a variable parameters function call
            Object[] newParams = new Object[parameterTypes.length];
            if (newParams.length > 1) {
                System.arraycopy(cm.getActualParams(), 0, newParams, 0, newParams.length - 1);
            }
            Object[] remaining = new Object[cm.getActualParams().length - parameterTypes.length + 1];
            newParams[newParams.length - 1] = remaining;
            System.arraycopy(cm.getActualParams(), parameterTypes.length - 1, remaining, 0, remaining.length);
            cm.setActualParams(newParams);
        }
    }

    /**
     *  This method apply the <code>NumberEvalHelper.coerceNumber</code> to the given result or,
     *  if it is an array, recursively to all its elements
     * @param result
     * @return
     */
    static Object normalizeResult(Object result) {
        logger.trace("normalizeResult {}", result);
        // this is to normalize types returned by external functions
        if (result != null && result.getClass().isArray()) {
            List<Object> objs = new ArrayList<>();
            for (int i = 0; i < Array.getLength(result); i++) {
                objs.add(NumberEvalHelper.coerceNumber(Array.get(result, i)));
            }
            return objs;
        } else {
            return NumberEvalHelper.coerceNumber(result);
        }
    }
}
