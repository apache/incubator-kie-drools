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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        parameters = new Object[]{true, false};
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
        parameters = new Object[]{LocalDate.of(2017, 6, 12), LocalTime.of(10, 6, 20)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(2, parametersRetrieved.length);
        Arrays.stream(parametersRetrieved).forEach(parameter -> assertEquals(TemporalAccessor.class,
                                                                             parameter.getType()));

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20};
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

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second,
//               @ParameterName( "hour offset" ) Number hourOffset )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20, 2};
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

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second,
//               @ParameterName( "timezone" ) String timezone )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20, "Europe/Paris"};
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
        parameters = new Object[]{10, 6, 20};
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
        parameters = new Object[]{LocalTime.of(10, 6, 20)};
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
    void getSortFunctionCandidateMethod() {
        BaseFEELFunction toTest = SortFunction.INSTANCE;

        // invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
        //                                             @ParameterName("list") List list,
        //                                             @ParameterName("precedes") FEELFunction function
        Object[] parameters = {List.of(1, 2), AllFunction.INSTANCE};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(3, parametersRetrieved.length);
        assertEquals(EvaluationContext.class, parametersRetrieved[0].getType());
        assertEquals(List.class, parametersRetrieved[1].getType());
        assertEquals(FEELFunction.class, parametersRetrieved[2].getType());

        // invoke(@ParameterName("list") List list)
        parameters = new Object[]{List.of(1, 3, 5)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertNotNull(candidateMethodRetrieved);
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertNotNull(retrieved);
        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
        assertEquals("invoke", retrieved.getName());
        parametersRetrieved = retrieved.getParameters();
        assertNotNull(parametersRetrieved);
        assertEquals(1, parametersRetrieved.length);
        assertEquals(List.class, parametersRetrieved[0].getType());
    }

    @Test
    void getStddevFunctionCandidateMethod() {
        BaseFEELFunction toTest = StddevFunction.INSTANCE;

        // invoke(@ParameterName("list") List<?> list)
        Object actualValue = Arrays.asList(2, 4, 7, 5);
        Object[] parameters = {new NamedParameter("list", actualValue)};
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
    }



//    @Test
//    void getCustomFunctionCandidateMethod() {
    // TODO {gcardosi}: verify behavior with CustomFEELFunction
    //
//        List<FEELFunction.Param> params = List.of(new FEELFunction.Param("foo", BuiltInType.UNKNOWN),
//                                                  new FEELFunction.Param("foo", BuiltInType.UNKNOWN));
//
//        BaseNode body = new InfixOpNode(InfixOperator.EQ,
//                                        new NameRefNode(BuiltInType.UNKNOWN, "foo"),
//                                        new NullNode((String) null),
//                                        "foo = null and person's age < 18");
//        BaseFEELFunction toTest = new CustomFEELFunction("<anonymous>",
//                                                         params,
//                                                         body,
//                                                         ctx);
//
//        // invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
//        //                                             @ParameterName("list") List list,
//        //                                             @ParameterName("precedes") FEELFunction function
//        Object[] parameters = {List.of(1, 2), AllFunction.INSTANCE};
//        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
//        assertNotNull(candidateMethodRetrieved);
//        Method retrieved = candidateMethodRetrieved.getActualMethod();
//        assertNotNull(retrieved);
//        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
//        assertEquals("invoke", retrieved.getName());
//        Parameter[] parametersRetrieved = retrieved.getParameters();
//        assertNotNull(parametersRetrieved);
//        assertEquals(3, parametersRetrieved.length);
//        assertEquals(EvaluationContext.class, parametersRetrieved[0].getType());
//        assertEquals(List.class, parametersRetrieved[1].getType());
//        assertEquals(FEELFunction.class, parametersRetrieved[2].getType());
//
//        // invoke(@ParameterName("list") List list)
//        parameters = new Object[]{List.of(1, 3, 5)};
//        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
//        assertNotNull(candidateMethodRetrieved);
//        retrieved = candidateMethodRetrieved.getActualMethod();
//        assertNotNull(retrieved);
//        assertTrue(Modifier.isPublic(retrieved.getModifiers()));
//        assertEquals("invoke", retrieved.getName());
//        parametersRetrieved = retrieved.getParameters();
//        assertNotNull(parametersRetrieved);
//        assertEquals(1, parametersRetrieved.length);
//        assertEquals(List.class, parametersRetrieved[0].getType());
//    }
}