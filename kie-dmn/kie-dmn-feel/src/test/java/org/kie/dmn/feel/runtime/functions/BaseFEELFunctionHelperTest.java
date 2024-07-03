/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseFEELFunctionHelperTest {

    private EvaluationContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = CodegenTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void areParametersMatching() throws NoSuchMethodException {
        // AllFunction.invoke(@ParameterName( "list" ) List list)
        // no coercion needed
        Class<?>[] parameterTypes =  AllFunction.class.getMethod("invoke", List.class).getParameterTypes();
        assertNotNull(parameterTypes);
        Object[] actualParams = {List.of(true, false)};
        BaseFEELFunction.CandidateMethod cm = new BaseFEELFunction.CandidateMethod(actualParams );
        assertTrue(BaseFEELFunctionHelper.areParametersMatching(parameterTypes, cm));
        assertEquals(actualParams, cm.getActualParams());

        // coercion needed
        actualParams = new Object[]{ true };
        cm = new BaseFEELFunction.CandidateMethod(actualParams );
        assertTrue(BaseFEELFunctionHelper.areParametersMatching(parameterTypes, cm));

        Object[] retrieved = cm.getActualParams();
        assertNotEquals(actualParams, retrieved);

        // SumFunction.invoke(@ParameterName("list") Number single)
        // coercion fails
        parameterTypes =  SumFunction.class.getMethod("invoke", Number.class).getParameterTypes();
        actualParams = new Object[]{ true };
        cm = new BaseFEELFunction.CandidateMethod(actualParams );
        assertFalse(BaseFEELFunctionHelper.areParametersMatching(parameterTypes, cm));
    }

    @Test
    void addCtxParamIfRequired() throws NoSuchMethodException {
        // AllFunction.invoke(@ParameterName( "list" ) List list)
        Method method = AllFunction.class.getMethod("invoke", List.class);
        assertNotNull(method);
        Object[] parameters = {List.of(true, false)};

        Object[] retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, true, method);
        assertNotNull(retrieved);
        assertEquals(parameters.length, retrieved.length);
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i]);
        }

        // SortFunction.invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
        //                                             @ParameterName("list") List list,
        //                                             @ParameterName("precedes") FEELFunction function)
        method = SortFunction.class.getMethod("invoke", EvaluationContext.class, List.class, FEELFunction.class);
        assertNotNull(method);
        parameters = new Object[]{List.of(1, 2), AllFunction.INSTANCE};
        // direct reference to ctx
        retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, false, method);
        assertNotNull(retrieved);
        assertEquals(parameters.length + 1, retrieved.length);
        assertEquals(ctx, retrieved[0]);
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i + 1]);
        }

        // NamedParameter reference to ctx
        retrieved = BaseFEELFunctionHelper.addCtxParamIfRequired(ctx, parameters, true, method);
        assertNotNull(retrieved);
        assertEquals(parameters.length + 1, retrieved.length);
        assertEquals(NamedParameter.class, retrieved[0].getClass());
        NamedParameter retrievedNamedParameter = (NamedParameter) retrieved[0];
        assertEquals("ctx", retrievedNamedParameter.getName());
        assertEquals(ctx, retrievedNamedParameter.getValue());
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i + 1]);
        }
    }

    @Test
    void calculateActualParams() throws NoSuchMethodException {
        // CeilingFunction.invoke(@ParameterName( "n" ) BigDecimal n)
        Method m = CeilingFunction.class.getMethod("invoke", BigDecimal.class);
        assertNotNull(m);
        NamedParameter[] parameters = {new NamedParameter("n", BigDecimal.valueOf(1.5))};
        Object[] retrieved = BaseFEELFunctionHelper.calculateActualParams(m, parameters);
        assertNotNull(retrieved);
        assertEquals(parameters.length, retrieved.length);
        assertEquals(parameters[0].getValue(), retrieved[0]);

        parameters = new NamedParameter[]{new NamedParameter("undefined", BigDecimal.class)};
        retrieved = BaseFEELFunctionHelper.calculateActualParams(m, parameters);
        assertNull(retrieved);
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
        assertTrue(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters, variableParamPrefix, variableParams));
        assertEquals(np.getValue(), actualParams[0]);

        np = new NamedParameter("undefined", BigDecimal.valueOf(1.5));
        actualParams = new Object[1];
        assertFalse(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters, variableParamPrefix, variableParams));


        // populate by variableparameters
        variableParamPrefix = "varPref";
        int varIndex = 12;
        np = new NamedParameter(variableParamPrefix + varIndex, BigDecimal.valueOf(1.5));
        names = Collections.singletonList("n");
        actualParams = new Object[1];
        isVariableParameters = true;
        variableParams = new ArrayList();
        assertTrue(BaseFEELFunctionHelper.calculateActualParam(np, names, actualParams, isVariableParameters, variableParamPrefix, variableParams));
        assertEquals(varIndex, variableParams.size());
        for (int i = 0; i < varIndex - 1; i++) {
            assertNull(variableParams.get(i));
        }
        assertEquals(np.getValue(), variableParams.get(varIndex -1));
    }

    @Test
    void calculateActualParamVariableParameters() {
        // populate by variableparameters
        String variableParamPrefix = "varPref";
        int varIndex = 12;
        NamedParameter np = new NamedParameter(variableParamPrefix + varIndex, BigDecimal.valueOf(1.5));
        List<Object> variableParams = new ArrayList<>();
        assertTrue(BaseFEELFunctionHelper.calculateActualParamVariableParameters(np, variableParamPrefix, variableParams));
        assertEquals(varIndex, variableParams.size());
        for (int i = 0; i < varIndex - 1; i++) {
            assertNull(variableParams.get(i));
        }
        assertEquals(np.getValue(), variableParams.get(varIndex -1));

        np = new NamedParameter("variableParamPrefix", BigDecimal.valueOf(1.5));
        variableParams = new ArrayList<>();
        assertFalse(BaseFEELFunctionHelper.calculateActualParamVariableParameters(np, variableParamPrefix, variableParams));
    }

    @Test
    void getParametersNames() throws NoSuchMethodException {
        // SumFunction.invoke(@ParameterName("n") Object[] list)
        Method m = SumFunction.class.getMethod("invoke", Object.class.arrayType());
        assertNotNull(m);
        List<String> retrieved = BaseFEELFunctionHelper.getParametersNames(m);
        assertNotNull(retrieved);
        int counter = 0;
        Annotation[][] pas = m.getParameterAnnotations();
        for (Annotation[] annotations : pas) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ParameterName parameterName) {
                    assertEquals(parameterName.value(), retrieved.get(counter));
                    counter++;
                }
            }
        }

        // DateAndTimeFunction.invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
        //                                                 @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second,
        //                                                 @ParameterName( "hour offset" ) Number hourOffset )
        m = DateAndTimeFunction.class.getMethod("invoke", Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class,
                                                Number.class);
        assertNotNull(m);
        retrieved = BaseFEELFunctionHelper.getParametersNames(m);
        assertNotNull(retrieved);
        counter = 0;
        pas = m.getParameterAnnotations();
        for (Annotation[] annotations : pas) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ParameterName parameterName) {
                    assertEquals(parameterName.value(), retrieved.get(counter));
                    counter++;
                }
            }
        }

    }

    @Test
    void rearrangeParameters() {
        NamedParameter[] params = {new NamedParameter("fake", new Object())};
        Object[] retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, Collections.emptyList());
        assertNotNull(retrieved);
        assertEquals(params, retrieved);

        List<String> pnames = IntStream.range(0, 3)
                .mapToObj(i -> "Parameter_" +i)
                .toList();

        // single param in correct position
        params = new NamedParameter[]{new NamedParameter(pnames.get(0), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertNotNull(retrieved);
        assertEquals(pnames.size(), retrieved.length);
        for (int i = 0; i < retrieved.length; i++) {
            if (i == 0) {
                assertEquals(params[0].getValue(), retrieved[i]);
            } else {
                assertNull(retrieved[i]);
            }
        }

        // single param in wrong position
        params = new NamedParameter[]{new NamedParameter(pnames.get(2), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertNotNull(retrieved);
        assertEquals(pnames.size(), retrieved.length);
        for (int i = 0; i < retrieved.length; i++) {
            if (i == 2) {
                assertEquals(params[0].getValue(), retrieved[i]);
            } else {
                assertNull(retrieved[i]);
            }
        }

        // reverting the whole order
        params = new NamedParameter[]{new NamedParameter(pnames.get(2), new Object()),
                new NamedParameter(pnames.get(1), new Object()),
                new NamedParameter(pnames.get(0), new Object())};
        retrieved = BaseFEELFunctionHelper.rearrangeParameters(params, pnames);
        assertNotNull(retrieved);
        assertEquals(pnames.size(), retrieved.length);
        for (int i = 0; i < retrieved.length; i++) {
            switch (i) {
                case 0:
                    assertEquals(params[2].getValue(), retrieved[i]);
                    break;
                case 1:
                    assertEquals(params[1].getValue(), retrieved[i]);
                    break;
                case 2:
                    assertEquals(params[0].getValue(), retrieved[i]);
                    break;
            }
        }

    }

    @Test
    void adjustForVariableParameters() throws NoSuchMethodException {
        Object[] parameters = new Object[]{ 1, 3, 5};
        BaseFEELFunction.CandidateMethod cm = new BaseFEELFunction.CandidateMethod(parameters);
        // SumFunction.invoke(@ParameterName("n") Object[] list)
        // parameters are converted to array for this above method
        Method method = SumFunction.class.getMethod("invoke", Object.class.arrayType());
        assertNotNull(method);
        BaseFEELFunctionHelper.adjustForVariableParameters(cm, method.getParameterTypes());
        Object[] retrieved = cm.getActualParams();
        assertNotNull(retrieved);
        assertEquals(1, retrieved.length);
        assertEquals(Object.class.arrayType(), retrieved[0].getClass());
        Object[] retrievedParams = (Object[]) retrieved[0];
        assertEquals(parameters.length, retrievedParams.length);
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrievedParams[i]);
        }

        cm = new BaseFEELFunction.CandidateMethod(parameters);
        // SumFunction.invoke(@ParameterName("list") List list)
        // parameters are not converted to array for this above method
        method = SumFunction.class.getMethod("invoke", List.class);
        assertNotNull(method);
        BaseFEELFunctionHelper.adjustForVariableParameters(cm, method.getParameterTypes());
        retrieved = cm.getActualParams();
        assertNotNull(retrieved);
        assertEquals(parameters.length, retrieved.length);
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i]);
        }
    }

    @Test
    void normalizeResult() {
        List<Object> originalResult = List.of(3, "4", 56);
        Object result = originalResult.toArray();
        Object retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertNotNull(retrieved);
        assertInstanceOf(List.class, retrieved);
        List<Object> retrievedList = (List<Object>) retrieved;
        assertEquals(originalResult.size(), retrievedList.size());
        for (int i = 0; i < originalResult.size(); i++) {
            assertEquals(NumberEvalHelper.coerceNumber(originalResult.get(i)), retrievedList.get(i));
        }

        result = 23;
        retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertNotNull(retrieved);
        assertEquals(NumberEvalHelper.coerceNumber(result), retrieved);

        result = "23";
        retrieved = BaseFEELFunctionHelper.normalizeResult(result);
        assertNotNull(retrieved);
        assertEquals(NumberEvalHelper.coerceNumber(result), retrieved);
    }

    @Test
    void nullCount() {
        Random random = new Random();
        int elements = random.nextInt(10);
        Object[] params = new Object[elements];
        int expectedCount = 0;
        for (int i = 0; i < elements; i++) {
            if (random.nextBoolean()) {
                params[i] = null;
                expectedCount++;
            } else {
                params[i] = new Object();
            }
        }
        assertEquals(expectedCount, BaseFEELFunctionHelper.nullCount(params));
    }
}