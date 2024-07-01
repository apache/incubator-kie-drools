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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.runtime.FEELFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseFEELFunctionTest {

    private EvaluationContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = CodegenTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void getAllFunctionCandidateMethod() {
        BaseFEELFunction toTest = AllFunction.INSTANCE;

        // invoke(@ParameterName( "list" ) List list)
        Object[] parameters = {List.of(true, false)};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(List.class, parametersRetrieved[0].getType());

        // invoke(@ParameterName( "b" ) Object[] list)
        parameters = new Object[]{ true, false };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(Object.class.arrayType(), parametersRetrieved[0].getType());
    }

    @Test
    void getDateAndTimeFunctionCandidateMethod() {
        BaseFEELFunction toTest = DateAndTimeFunction.INSTANCE;

        // invoke(@ParameterName( "from" ) String val)
        Object[] parameters = {"2017-09-07T10:20:30"};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(String.class, parametersRetrieved[0].getType());

        // invoke(@ParameterName( "date" ) TemporalAccessor date, @ParameterName( "time" ) TemporalAccessor time)
        parameters = new Object[]{ LocalDate.of(2017, 6, 12), LocalTime.of(10, 6, 20) };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(2, parametersRetrieved.length);
        Arrays.stream(parametersRetrieved).forEach(parameter -> assertEquals(TemporalAccessor.class, parameter.getType()));

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second )
        parameters = new Object[]{ 2017, 6, 12, 10, 6, 20 };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(6, parametersRetrieved.length);
        Arrays.stream(parametersRetrieved).forEach(parameter -> assertEquals(Number.class, parameter.getType()));

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second,
//               @ParameterName( "hour offset" ) Number hourOffset )
        parameters = new Object[]{ 2017, 6, 12, 10, 6, 20, 2 };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(7, parametersRetrieved.length);
        Arrays.stream(parametersRetrieved).forEach(parameter -> assertEquals(Number.class, parameter.getType()));

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second,
//               @ParameterName( "timezone" ) String timezone )
        parameters = new Object[]{ 2017, 6, 12, 10, 6, 20, "Europe/Paris" };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(7, parametersRetrieved.length);
        for (int i = 0; i < 6; i++) {
            assertEquals(Number.class, parametersRetrieved[i].getType());
        }
        assertEquals(String.class, parametersRetrieved[6].getType());
    }

    @Test
    void getExtendedTimeFunctionCandidateMethod() {
        BaseFEELFunction toTest = org.kie.dmn.feel.runtime.functions.extended.TimeFunction.INSTANCE;

        // invoke(@ParameterName( "from" ) String val)
        Object[] parameters = {"10:20:30"};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(String.class, parametersRetrieved[0].getType());

//      invoke(
//            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
//            @ParameterName("second") Number seconds)
        parameters = new Object[]{ 10, 6, 20 };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(3, parametersRetrieved.length);
        Arrays.stream(parametersRetrieved).forEach(parameter -> assertEquals(Number.class, parameter.getType()));

//     invoke(
//            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
//            @ParameterName("second") Number seconds, @ParameterName("offset") Duration offset)
        parameters = new Object[]{10, 6, 20, Duration.ofHours(3)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(4, parametersRetrieved.length);
        for (int i = 0; i < 3; i++) {
            assertEquals(Number.class, parametersRetrieved[i].getType());
        }
        assertEquals(Duration.class, parametersRetrieved[3].getType());

//      invoke(@ParameterName("from") TemporalAccessor date
        parameters = new Object[]{ LocalTime.of(10, 6, 20) };
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(TemporalAccessor.class, parametersRetrieved[0].getType());
    }

    @Test
    void getActualParameters() throws NoSuchMethodException {
        // AllFunction.invoke(@ParameterName( "list" ) List list)
        Method method = AllFunction.class.getMethod("invoke", List.class);
        assertNotNull(method);
        Object[] parameters = {List.of(true, false)};

        Object[] retrieved = BaseFEELFunction.getActualParameters(ctx, parameters, true, method);
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
        parameters = new Object[]{ List.of(1, 2), AllFunction.INSTANCE};
        // direct reference to ctx
        retrieved = BaseFEELFunction.getActualParameters(ctx, parameters, false, method);
        assertNotNull(retrieved);
        assertEquals(parameters.length + 1, retrieved.length);
        assertEquals(ctx, retrieved[0]);
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i+1]);
        }

        // NamedParameter reference to ctx
        retrieved = BaseFEELFunction.getActualParameters(ctx, parameters, true, method);
        assertNotNull(retrieved);
        assertEquals(parameters.length + 1, retrieved.length);
        assertEquals(NamedParameter.class, retrieved[0].getClass());
        NamedParameter retrievedNamedParameter = (NamedParameter) retrieved[0];
        assertEquals("ctx", retrievedNamedParameter.getName());
        assertEquals(ctx, retrievedNamedParameter.getValue());
        for (int i = 0; i < parameters.length; i++) {
            assertEquals(parameters[i], retrieved[i+1]);
        }
    }
}