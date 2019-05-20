/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.concurrency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.compiler.integrationtests.facts.AnEnum;
import org.drools.compiler.integrationtests.facts.FactWithBigDecimal;
import org.drools.compiler.integrationtests.facts.FactWithBoolean;
import org.drools.compiler.integrationtests.facts.FactWithByte;
import org.drools.compiler.integrationtests.facts.FactWithCharacter;
import org.drools.compiler.integrationtests.facts.FactWithDouble;
import org.drools.compiler.integrationtests.facts.FactWithEnum;
import org.drools.compiler.integrationtests.facts.FactWithFloat;
import org.drools.compiler.integrationtests.facts.FactWithInteger;
import org.drools.compiler.integrationtests.facts.FactWithLong;
import org.drools.compiler.integrationtests.facts.FactWithShort;
import org.drools.compiler.integrationtests.facts.FactWithString;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.*;

public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    static Stream<Arguments> parameters() {
        return Stream.of(
                new Parameters(true, true, true, true),
                new Parameters(true, true, true, false),
                new Parameters(true, true, false, false),
                new Parameters(true, true, false, true),
                new Parameters(true, false, true, true),
                new Parameters(true, false, true, false),
                new Parameters(true, false, false, false),
                new Parameters(true, false, false, true),
                new Parameters(false, true, true, true),
                new Parameters(false, true, true, false),
                new Parameters(false, true, false, false),
                new Parameters(false, true, false, true),
                new Parameters(false, false, true, true),
                new Parameters(false, false, true, false),
                new Parameters(false, false, false, false),
                new Parameters(false, false, false, true)
        ).map(Arguments::arguments);
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testBooleanPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithBoolean: FactWithBoolean(booleanValue == false) \n",
                              new FactWithBoolean(false));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testBoolean(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n",
                              new FactWithBoolean(false));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testBytePrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithByte: FactWithByte(byteValue == 15) \n",
                              new FactWithByte((byte) 15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testByte(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithByte: FactWithByte(byteObjectValue == 15) \n",
                              new FactWithByte((byte) 15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testShortPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithShort: FactWithShort(shortValue == 15) \n",
                              new FactWithShort((short) 15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testShort(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithShort: FactWithShort(shortObjectValue == 15) \n",
                              new FactWithShort((short) 15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testIntPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testInteger(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithInteger: FactWithInteger(integerValue == 15) \n",
                              new FactWithInteger(15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testLongPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testLong(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithLong: FactWithLong(longObjectValue == 15) \n",
                              new FactWithLong(15));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testFloatPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithFloat: FactWithFloat(floatValue == 15.1) \n",
                              new FactWithFloat(15.1f));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testFloat(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n",
                              new FactWithFloat(15.1f));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testDoublePrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n",
                              new FactWithDouble(15.1d));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testDouble(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n",
                              new FactWithDouble(15.1d));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testBigDecimal(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n",
                              new FactWithBigDecimal(BigDecimal.TEN));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testCharPrimitive(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithChar: FactWithCharacter(charValue == 'a') \n",
                              new FactWithCharacter('a'));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testCharacter(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithChar: FactWithCharacter(characterValue == 'a') \n",
                              new FactWithCharacter('a'));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testString(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithString: FactWithString(stringValue == \"test\") \n",
                              new FactWithString("test"));
    }

    @ParameterizedDataTypeEvaluationConcurrentSessionsTest
    public void testEnum(Parameters params) throws InterruptedException {
        testFactAttributeType(params, "    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n",
                              new FactWithEnum(AnEnum.FIRST));
    }

    private void testFactAttributeType(final Parameters params, final String ruleConstraint,
                                       final Object factInserted) throws InterruptedException {
        final String drl =
                " import org.drools.compiler.integrationtests.facts.*;\n" +
                        " global " + AtomicInteger.class.getCanonicalName() + " numberOfFirings;\n" +
                        " rule R1 \n" +
                        " when \n" +
                        ruleConstraint +
                        " then \n" +
                        " numberOfFirings.incrementAndGet(); \n" +
                        " end ";

        final AtomicInteger numberOfFirings = new AtomicInteger();

        boolean sharedKieSession = params.isSharedKieSession();
        parallelTest(params, NUMBER_OF_REPETITIONS, NUMBER_OF_THREADS, (kieSession, counter) -> {
            kieSession.insert(factInserted);
            final int rulesFired = kieSession.fireAllRules();
            return sharedKieSession || rulesFired == 1;
        }, "numberOfFirings", numberOfFirings, drl);

        if (sharedKieSession) {
            // This is 1 because engine doesn't insert an already existing object twice, so when sharing a session
            // the object should be present just once in the session. When not sharing a session, there is N separate
            // sessions, so each one should fire.
            assertThat(numberOfFirings.get()).isEqualTo(1);
        } else {
            assertThat(numberOfFirings.get()).isEqualTo(10);
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("parameters")
    public @interface ParameterizedDataTypeEvaluationConcurrentSessionsTest {

    }
}
