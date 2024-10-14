/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;

import static org.assertj.core.api.Assertions.assertThat;

class BaseFEELFunctionHelperTest {

    private EvaluationContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = EvaluationContextTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void getAdjustedParametersForMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // StddevFunction.invoke(@ParameterName( "list" ) List<?> list)
        Method method = StddevFunction.class.getMethod("invoke", List.class);
        assertThat(method).isNotNull();
        Object actualValue = Arrays.asList(2, 4, 7, 5);
        Object[] parameters = {new NamedParameter("list", actualValue)};

        Object[] retrieved = BaseFEELFunctionHelper.getAdjustedParametersForMethod(ctx, parameters, true, method);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(parameters.length);
        assertThat(retrieved[0]).isEqualTo(actualValue);
    }

    @Test
    void adjustByCoercion() {
        // no coercion needed
        Object actualParam = List.of(true, false);
        Class<?>[] parameterTypes = new Class[]{List.class};
        Object[] actualParams = {actualParam};
        Object[] retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isEqualTo(actualParams);

        actualParam = "StringA";
        parameterTypes = new Class[]{String.class};
        actualParams = new Object[]{actualParam};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isEqualTo(actualParams);

        // coercing more objects to different types: fails
        parameterTypes = new Class[]{String.class, Integer.class};
        actualParams = new Object[]{"String", 34 };
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isEqualTo(actualParams);

        // not coercing null value to not-list type
        actualParam = null;
        actualParams = new Object[]{actualParam};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isEqualTo(actualParams);

        // not coercing null value to singleton list
        parameterTypes = new Class[]{List.class};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isEqualTo(actualParams);

        // coercing not-null value to singleton list
        actualParam = "StringA";
        actualParams = new Object[]{actualParam};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotEqualTo(actualParams);
        assertThat(retrieved.length).isEqualTo(1);
        assertThat(retrieved[0]).isNotNull();
        assertThat(retrieved[0]).isInstanceOf(List.class);
        List retrievedList = (List) retrieved[0];
        assertThat(retrievedList.size()).isEqualTo(1);
        assertThat(retrievedList.get(0)).isEqualTo(actualParam);

        // coercing null value to array: fails
        parameterTypes = new Class[]{Object.class.arrayType()};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isNull();

        // coercing one object to different type: fails
        actualParam = 45;
        parameterTypes = new Class[]{String.class};
        actualParams = new Object[]{actualParam};
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isNull();

        // coercing more objects to different types: fails
        parameterTypes = new Class[]{String.class, Integer.class};
        actualParams = new Object[]{"String", "34" };
        retrieved = BaseFEELFunctionHelper.adjustByCoercion(parameterTypes, actualParams);
        assertThat(retrieved).isNull();
    }

    @Test
    void addCtxParamIfRequired() throws NoSuchMethodException {
        // AllFunction.invoke(@ParameterName( "list" ) List list)
        Method method = AllFunction.class.getMethod("invoke", List.class);
        assertThat(method).isNotNull();
        Object[] parameters = {List.of(true, false)};

        Object[] retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, true, method);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            assertThat(retrieved[i]).isEqualTo(parameters[i]);
        }

        // SortFunction.invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
        //                                             @ParameterName("list") List list,
        //                                             @ParameterName("precedes") FEELFunction function)
        method = SortFunction.class.getMethod("invoke", EvaluationContext.class, List.class, FEELFunction.class);
        assertThat(method).isNotNull();
        parameters = new Object[]{List.of(1, 2), AllFunction.INSTANCE};
        // direct reference to ctx
        retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, false, method);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(parameters.length + 1);
        assertThat(retrieved[0]).isEqualTo(ctx);
        for (int i = 0; i < parameters.length; i++) {
            assertThat(retrieved[i + 1]).isEqualTo(parameters[i]);
        }

        // NamedParameter reference to ctx
        retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, true, method);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(parameters.length + 1);
        assertThat(retrieved[0].getClass()).isEqualTo(NamedParameter.class);
        NamedParameter retrievedNamedParameter = (NamedParameter) retrieved[0];
        assertThat(retrievedNamedParameter.getName()).isEqualTo("ctx");
        assertThat(retrievedNamedParameter.getValue()).isEqualTo(ctx);
        for (int i = 0; i < parameters.length; i++) {
            assertThat(retrieved[i + 1]).isEqualTo(parameters[i]);
        }
    }

    @Test
    void calculateActualParams() throws NoSuchMethodException {
        // CeilingFunction.invoke(@ParameterName( "n" ) BigDecimal n)
        Method m = CeilingFunction.class.getMethod("invoke", BigDecimal.class);
        assertThat(m).isNotNull();
        NamedParameter[] parameters = {new NamedParameter("n", BigDecimal.valueOf(1.5))};
        Object[] retrieved = BaseFEELFunctionHelper.calculateActualParams(m, parameters);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(parameters.length);
        assertThat(retrieved[0]).isEqualTo(parameters[0].getValue());

        parameters = new NamedParameter[]{new NamedParameter("undefined", BigDecimal.class)};
        retrieved = BaseFEELFunctionHelper.calculateActualParams(m, parameters);
        assertThat(retrieved).isNull();
    }

    @Test
    void calculateActualParam() {
        // populate by NamedParameter value
        NamedParameter np = new NamedParameter("n", BigDecimal.valueOf(1.5));
        List<String> names = Collections.singletonList("n");
        Object[] actualParams = new Object[1];
        boolean isVariableParameters = false;
        String variableParamPrefix = null;
        List<Object> variableParams = null;
        assertThat(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters,
                variableParamPrefix, variableParams)).isTrue();
        assertThat(actualParams[0]).isEqualTo(np.getValue());

        np = new NamedParameter("undefined", BigDecimal.valueOf(1.5));
        actualParams = new Object[1];
        assertThat(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters,
                variableParamPrefix, variableParams)).isFalse();

        // populate by variableparameters
        variableParamPrefix = "varPref";
        int varIndex = 12;
        np = new NamedParameter(variableParamPrefix + varIndex, BigDecimal.valueOf(1.5));
        names = Collections.singletonList("n");
        actualParams = new Object[1];
        isVariableParameters = true;
        variableParams = new ArrayList();
        assertThat(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters,
                variableParamPrefix, variableParams)).isTrue();
        assertThat(variableParams.size()).isEqualTo(varIndex);
        for (int i = 0; i < varIndex - 1; i++) {
            assertThat(variableParams.get(i)).isNull();
        }
        assertThat(variableParams.get(varIndex - 1)).isEqualTo(np.getValue());
    }

    @Test
    void calculateActualParamVariableParameters() {
        // populate by variableparameters
        String variableParamPrefix = "varPref";
        int varIndex = 12;
        NamedParameter np = new NamedParameter(variableParamPrefix + varIndex, BigDecimal.valueOf(1.5));
        List<Object> variableParams = new ArrayList<>();
        assertThat(BaseFEELFunctionHelper.calculateActualParamVariableParameters(np, variableParamPrefix,
                variableParams)).isTrue();
        assertThat(variableParams.size()).isEqualTo(varIndex);
        for (int i = 0; i < varIndex - 1; i++) {
            assertThat(variableParams.get(i)).isNull();
        }
        assertThat(variableParams.get(varIndex - 1)).isEqualTo(np.getValue());

        np = new NamedParameter("variableParamPrefix", BigDecimal.valueOf(1.5));
        variableParams = new ArrayList<>();
        assertThat(BaseFEELFunctionHelper.calculateActualParamVariableParameters(np, variableParamPrefix,
                variableParams)).isFalse();
    }

    @Test
    void getParametersNames() throws NoSuchMethodException {
        // SumFunction.invoke(@ParameterName("n") Object[] list)
        Method m = SumFunction.class.getMethod("invoke", Object.class.arrayType());
        assertThat(m).isNotNull();
        List<String> retrieved = BaseFEELFunctionHelper.getParametersNames(m);
        assertThat(retrieved).isNotNull();
        int counter = 0;
        Annotation[][] pas = m.getParameterAnnotations();
        for (Annotation[] annotations : pas) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ParameterName parameterName) {
                    assertThat(retrieved.get(counter)).isEqualTo(parameterName.value());
                    counter++;
                }
            }
        }

        // DateAndTimeFunction.invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month,
        // @ParameterName( "day" ) Number day,
        //                                                 @ParameterName( "hour" ) Number hour, @ParameterName(
        //                                                 "minute" ) Number minute, @ParameterName( "second" )
        //                                                 Number second,
        //                                                 @ParameterName( "hour offset" ) Number hourOffset )
        m = DateAndTimeFunction.class.getMethod("invoke", Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class);
        assertThat(m).isNotNull();
        retrieved = BaseFEELFunctionHelper.getParametersNames(m);
        assertThat(retrieved).isNotNull();
        counter = 0;
        pas = m.getParameterAnnotations();
        for (Annotation[] annotations : pas) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ParameterName parameterName) {
                    assertThat(retrieved.get(counter)).isEqualTo(parameterName.value());
                    counter++;
                }
            }
        }
    }

    @Test
    void rearrangeParameters() {
        NamedParameter[] params = {new NamedParameter("fake", new Object())};
        Object[] retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, Collections.emptyList());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(params);

        List<String> pnames = IntStream.range(0, 3)
                .mapToObj(i -> "Parameter_" + i)
                .toList();

        // single param in correct position
        params = new NamedParameter[]{new NamedParameter(pnames.get(0), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(pnames.size());
        for (int i = 0; i < retrieved.length; i++) {
            if (i == 0) {
                assertThat(retrieved[i]).isEqualTo(params[0].getValue());
            } else {
                assertThat(retrieved[i]).isNull();
            }
        }

        // single param in wrong position
        params = new NamedParameter[]{new NamedParameter(pnames.get(2), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(pnames.size());
        for (int i = 0; i < retrieved.length; i++) {
            if (i == 2) {
                assertThat(retrieved[i]).isEqualTo(params[0].getValue());
            } else {
                assertThat(retrieved[i]).isNull();
            }
        }

        // reverting the whole order
        params = new NamedParameter[]{new NamedParameter(pnames.get(2), new Object()),
                new NamedParameter(pnames.get(1), new Object()),
                new NamedParameter(pnames.get(0), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(pnames.size());
        for (int i = 0; i < retrieved.length; i++) {
            switch (i) {
                case 0:
                    assertThat(retrieved[i]).isEqualTo(params[2].getValue());
                    break;
                case 1:
                    assertThat(retrieved[i]).isEqualTo(params[1].getValue());
                    break;
                case 2:
                    assertThat(retrieved[i]).isEqualTo(params[0].getValue());
                    break;
            }
        }
    }

    @Test
    void normalizeResult() {
        List<Object> originalResult = List.of(3, "4", 56);
        Object result = originalResult.toArray();
        Object retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(List.class);
        List<Object> retrievedList = (List<Object>) retrieved;
        assertThat(retrievedList.size()).isEqualTo(originalResult.size());
        for (int i = 0; i < originalResult.size(); i++) {
            assertThat(retrievedList.get(i)).isEqualTo(NumberEvalHelper.coerceNumber(originalResult.get(i)));
        }

        result = 23;
        retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(NumberEvalHelper.coerceNumber(result));

        result = "23";
        retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(NumberEvalHelper.coerceNumber(result));
    }
}