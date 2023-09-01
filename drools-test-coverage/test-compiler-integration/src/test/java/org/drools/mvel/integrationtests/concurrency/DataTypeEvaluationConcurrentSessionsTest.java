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
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    @Parameterized.Parameters(name = "Enforced jitting={0}, Share KieBase={1}, Share KieSession={2}, KieBase type={3}")
    public static List<Object[]> getTestParameters() {
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
        Collection<Object[]> kbParams = TestParametersUtil.getKieBaseCloudConfigurations(false);
        // combine
        List<Object[]> params = new ArrayList<>();
        for (Boolean[] baseParam : baseParams) {
            for (Object[] kbParam : kbParams) {
                if (baseParam[0] == true && ((KieBaseTestConfiguration) kbParam[0]).isExecutableModel()) {
                    // jitting & exec-model test is not required
                } else {
                    params.add(new Object[]{baseParam[0], baseParam[1], baseParam[2], kbParam[0]});
                }
            }
        }
        return params;
    }

    public DataTypeEvaluationConcurrentSessionsTest(final boolean enforcedJitting,
                                                       final boolean sharedKieBase, final boolean sharedKieSession, final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(enforcedJitting, false, sharedKieBase, sharedKieSession, kieBaseTestConfiguration);
    }

    @Test(timeout = 40000)
    public void testBooleanPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 40000)
    public void testBoolean() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 40000)
    public void testBytePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test(timeout = 40000)
    public void testByte() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteObjectValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test(timeout = 40000)
    public void testShortPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test(timeout = 40000)
    public void testShort() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortObjectValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test(timeout = 40000)
    public void testIntPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 40000)
    public void testInteger() throws InterruptedException {
        testFactAttributeType("    $factWithInteger: FactWithInteger(integerValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 40000)
    public void testLongPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 40000)
    public void testLong() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longObjectValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 40000)
    public void testFloatPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test(timeout = 40000)
    public void testFloat() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test(timeout = 40000)
    public void testDoublePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test(timeout = 40000)
    public void testDouble() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test(timeout = 40000)
    public void testBigDecimal() throws InterruptedException {
        testFactAttributeType("    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n", new FactWithBigDecimal(BigDecimal.TEN));
    }

    @Test(timeout = 40000)
    public void testCharPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(charValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 40000)
    public void testCharacter() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(characterValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 40000)
    public void testString() throws InterruptedException {
        testFactAttributeType("    $factWithString: FactWithString(stringValue == \"test\") \n", new FactWithString("test"));
    }

    @Test(timeout = 40000)
    public void testEnum() throws InterruptedException {
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
}
