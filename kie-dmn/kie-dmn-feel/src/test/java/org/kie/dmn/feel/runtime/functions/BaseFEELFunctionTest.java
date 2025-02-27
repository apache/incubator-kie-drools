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
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;

import static org.assertj.core.api.Assertions.assertThat;

class BaseFEELFunctionTest {

    private EvaluationContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = EvaluationContextTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void invokeReflectiveCustomFunction() {
        List<FEELFunction.Param> parameters = List.of(new FEELFunction.Param("foo", BuiltInType.UNKNOWN),
                                                  new FEELFunction.Param("person's age", BuiltInType.UNKNOWN));

        BaseNode left = new InfixOpNode(InfixOperator.EQ,
                                        new NameRefNode(BuiltInType.UNKNOWN, "foo"),
                                        new NullNode(""),
                                        "foo = null");
        BaseNode right = new InfixOpNode(InfixOperator.LT,
                                        new NameRefNode(BuiltInType.UNKNOWN, "person's age"),
                                        new NumberNode(BigDecimal.valueOf(18), "18"),
                                        "person's age < 18");
        BaseNode body = new InfixOpNode(InfixOperator.AND, left, right, "foo = null and person's age < 18");
        BaseFEELFunction toTest = new CustomFEELFunction("<anonymous>",
                                                         parameters,
                                                         body,
                                                         ctx);
       Object[] params = {new NamedParameter("foo", null),
       new NamedParameter("person's age", 16)};
        Object retrieved = toTest.invokeReflectively(ctx, params);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(Boolean.class);
        assertThat((Boolean) retrieved).isTrue();

        params = new Object[]{new NamedParameter("foo", null),
                new NamedParameter("person's age", 19)};
        retrieved = toTest.invokeReflectively(ctx, params);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(Boolean.class);
        assertThat((Boolean) retrieved).isFalse();
    }

    @Test
    void getAllFunctionCandidateMethod() {
        BaseFEELFunction toTest = AllFunction.INSTANCE;

        // invoke(@ParameterName( "list" ) List list)
        Object[] parameters = {List.of(true, false)};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(List.class);

        // invoke(@ParameterName( "b" ) Object[] list)
        parameters = new Object[]{true, false};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(Object.class.arrayType());
    }

    @Test
    void getDateAndTimeFunctionCandidateMethod() {
        BaseFEELFunction toTest = DateAndTimeFunction.INSTANCE;

        // invoke(@ParameterName( "from" ) String val)
        Object[] parameters = {"2017-09-07T10:20:30"};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(String.class);

        // invoke(@ParameterName( "date" ) TemporalAccessor date, @ParameterName( "time" ) TemporalAccessor time)
        parameters = new Object[]{LocalDate.of(2017, 6, 12), LocalTime.of(10, 6, 20)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(2);
        assertThat(parametersRetrieved).extracting("type").containsExactly(TemporalAccessor.class, TemporalAccessor.class);


//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(6);
        assertThat(parametersRetrieved).extracting("type").containsExactly(Number.class, Number.class, Number.class, 
        		Number.class, Number.class, Number.class);

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second,
//               @ParameterName( "hour offset" ) Number hourOffset )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20, 2};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(7);
        assertThat(parametersRetrieved).extracting("type").containsExactly(Number.class, Number.class, Number.class, 
        		Number.class, Number.class, Number.class, Number.class);

//        invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day"
//        ) Number day,
//               @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName(
//               "second" ) Number second,
//               @ParameterName( "timezone" ) String timezone )
        parameters = new Object[]{2017, 6, 12, 10, 6, 20, "Europe/Paris"};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(7);
        assertThat(parametersRetrieved).extracting("type").containsExactly(Number.class, Number.class, Number.class, 
        		Number.class, Number.class, Number.class, String.class);
    }

    @Test
    void getExtendedTimeFunctionCandidateMethod() {
        BaseFEELFunction toTest = org.kie.dmn.feel.runtime.functions.extended.TimeFunction.INSTANCE;

        // invoke(@ParameterName( "from" ) String val)
        Object[] parameters = {"10:20:30"};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(String.class);

//      invoke(
//            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
//            @ParameterName("second") Number seconds)
        parameters = new Object[]{10, 6, 20};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(3);
        assertThat(parametersRetrieved).extracting("type").containsExactly(Number.class, Number.class, Number.class);

//     invoke(
//            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
//            @ParameterName("second") Number seconds, @ParameterName("offset") Duration offset)
        parameters = new Object[]{10, 6, 20, Duration.ofHours(3)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(4);
        
        assertThat(parametersRetrieved).extracting("type").containsExactly(Number.class, Number.class, Number.class, Duration.class);


//      invoke(@ParameterName("from") TemporalAccessor date
        parameters = new Object[]{LocalTime.of(10, 6, 20)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(TemporalAccessor.class);
    }

    @Test
    void getSortFunctionCandidateMethod() {
        BaseFEELFunction toTest = SortFunction.INSTANCE;

        // invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
        //                                             @ParameterName("list") List list,
        //                                             @ParameterName("precedes") FEELFunction function
        Object[] parameters = {List.of(1, 2), AllFunction.INSTANCE};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(3);
        assertThat(parametersRetrieved).extracting("type").containsExactly(EvaluationContext.class, List.class, FEELFunction.class);

        // invoke(@ParameterName("list") List list)
        parameters = new Object[]{List.of(1, 3, 5)};
        candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(List.class);
    }

    @Test
    void getStddevFunctionCandidateMethod() {
        BaseFEELFunction toTest = StddevFunction.INSTANCE;

        // invoke(@ParameterName("list") List<?> list)
        Object actualValue = Arrays.asList(2, 4, 7, 5);
        Object[] parameters = {new NamedParameter("list", actualValue)};
        BaseFEELFunction.CandidateMethod candidateMethodRetrieved = toTest.getCandidateMethod(ctx, parameters, false);
        assertThat(candidateMethodRetrieved).isNotNull();
        Method retrieved = candidateMethodRetrieved.getActualMethod();
        assertThat(retrieved).isNotNull();
        assertThat(Modifier.isPublic(retrieved.getModifiers())).isTrue();
        assertThat(retrieved.getName()).isEqualTo("invoke");
        Parameter[] parametersRetrieved = retrieved.getParameters();
        assertThat(parametersRetrieved).isNotNull();
        assertThat(parametersRetrieved).hasSize(1);
        assertThat(parametersRetrieved).extracting("type").containsExactly(List.class);
    }

    @Test
    void testIsCompatible() {
        BaseFEELFunction toTest = AllFunction.INSTANCE;

        org.kie.dmn.feel.lang.Type stringType = BuiltInType.STRING;
        org.kie.dmn.feel.lang.Type integerType = BuiltInType.NUMBER;

        org.kie.dmn.feel.lang.Type[] inputTypes;
        org.kie.dmn.feel.lang.Type outputType = stringType;
        boolean result;

        inputTypes = new org.kie.dmn.feel.lang.Type[]{stringType};
        result = toTest.isCompatible(inputTypes, outputType);
        assertThat(result).isTrue();

        inputTypes = new org.kie.dmn.feel.lang.Type[]{integerType};
        outputType = integerType;
        result = toTest.isCompatible(inputTypes, outputType);
        assertThat(result).isTrue();

        inputTypes = new org.kie.dmn.feel.lang.Type[]{stringType};
        outputType = stringType;
        result = toTest.isCompatible(inputTypes, outputType);
        assertThat(result).isTrue();

        inputTypes = new org.kie.dmn.feel.lang.Type[]{integerType};
        result = toTest.isCompatible(inputTypes, outputType);
        assertThat(result).isFalse();

        outputType = integerType;
        result = toTest.isCompatible(inputTypes, outputType);
        assertThat(result).isFalse();
    }

}