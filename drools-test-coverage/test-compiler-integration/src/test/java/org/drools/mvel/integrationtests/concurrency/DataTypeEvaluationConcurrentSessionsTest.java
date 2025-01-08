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
package org.drools.mvel.integrationtests.concurrency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.mvel.integrationtests.facts.AnEnum;
import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;
import org.drools.mvel.integrationtests.facts.FactWithBoolean;
import org.drools.mvel.integrationtests.facts.FactWithByte;
import org.drools.mvel.integrationtests.facts.FactWithCharacter;
import org.drools.mvel.integrationtests.facts.FactWithDouble;
import org.drools.mvel.integrationtests.facts.FactWithEnum;
import org.drools.mvel.integrationtests.facts.FactWithFloat;
import org.drools.mvel.integrationtests.facts.FactWithInteger;
import org.drools.mvel.integrationtests.facts.FactWithLong;
import org.drools.mvel.integrationtests.facts.FactWithShort;
import org.drools.mvel.integrationtests.facts.FactWithString;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    public static Stream<Arguments> parameters() {
        List<Boolean[]> baseParams = Arrays.asList(
                                                   new Boolean[]{false, false, false},
                                                   new Boolean[]{true, false, false},
                                                   new Boolean[]{false, true, false},
                                                   new Boolean[]{true, true, false},

                                                   new Boolean[]{false, false, true},
                                                   new Boolean[]{true, false, true},
                                                   new Boolean[]{false, true, true},
                                                   new Boolean[]{true, true, true});
        // TODO: EM failed with some tests. File JIRAs
        Collection<KieBaseTestConfiguration> kbParams = TestParametersUtil2.getKieBaseCloudConfigurations(false);
        // combine
        List<Arguments> params = new ArrayList<>();
        for (Boolean[] baseParam : baseParams) {
            for (KieBaseTestConfiguration kbParam : kbParams) {
                if (baseParam[0] && kbParam.isExecutableModel()) {
                    // jitting & exec-model test is not required
                } else {
                    params.add(arguments(baseParam[0], baseParam[1], baseParam[2], kbParam));
                }
            }
        }
        return params.stream();
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testBooleanPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
    	testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanValue == false) \n", new FactWithBoolean(false));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testBoolean(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n", new FactWithBoolean(false));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testBytePrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithByte: FactWithByte(byteValue == 15) \n", new FactWithByte((byte) 15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testByte(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithByte: FactWithByte(byteObjectValue == 15) \n", new FactWithByte((byte) 15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testShortPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithShort: FactWithShort(shortValue == 15) \n", new FactWithShort((short) 15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testShort(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithShort: FactWithShort(shortObjectValue == 15) \n", new FactWithShort((short) 15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testIntPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testInteger(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithInteger: FactWithInteger(integerValue == 15) \n", new FactWithInteger(15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testLongPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testLong(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithLong: FactWithLong(longObjectValue == 15) \n", new FactWithLong(15));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFloatPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFloat(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testDoublePrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testDouble(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testBigDecimal(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n", new FactWithBigDecimal(BigDecimal.TEN));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testCharPrimitive(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithChar: FactWithCharacter(charValue == 'a') \n", new FactWithCharacter('a'));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testCharacter(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithChar: FactWithCharacter(characterValue == 'a') \n", new FactWithCharacter('a'));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testString(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithString: FactWithString(stringValue == \"test\") \n", new FactWithString("test"));
    }

    @ParameterizedTest(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testEnum(boolean enforcedJitting, boolean isKieBaseShared, boolean isKieSessionShared, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, isKieBaseShared, isKieSessionShared, kieBaseTestConfiguration);
        testFactAttributeType("    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n", new FactWithEnum(AnEnum.FIRST));
    }

    private void testFactAttributeType(final String ruleConstraint, final Object factInserted) throws InterruptedException {
        final String drl =
                " import org.drools.mvel.integrationtests.facts.*;\n" +
                        " global " + AtomicInteger.class.getCanonicalName() + " numberOfFirings;\n" +
                        " rule R1 \n" +
                        " when \n" +
                        ruleConstraint +
                        " then \n" +
                        " numberOfFirings.incrementAndGet(); \n" +
                        " end ";

        final AtomicInteger numberOfFirings = new AtomicInteger();

        parallelTest(NUMBER_OF_REPETITIONS, NUMBER_OF_THREADS, (kieSession, counter) -> {
            kieSession.insert(factInserted);
            final int rulesFired = kieSession.fireAllRules();
            return isKieSessionShared || rulesFired == 1;
        }, "numberOfFirings", numberOfFirings, drl);

        if (isKieSessionShared) {
            // This is 1 because engine doesn't insert an already existing object twice, so when sharing a session
            // the object should be present just once in the session. When not sharing a session, there is N separate
            // sessions, so each one should fire.
            assertThat(numberOfFirings.get()).isEqualTo(1);
        } else {
            assertThat(numberOfFirings.get()).isEqualTo(10);
        }
    }
}
