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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    @Parameterized.Parameters(name = "Enforced jitting={0}, Serialize KieBase={1}, Share KieBase={2}, Share KieSession={3}")
    public static List<Boolean[]> getTestParameters() {
        return Arrays.asList(
                new Boolean[]{false, false, false, false},
                new Boolean[]{false, true, false, false},
                new Boolean[]{true, false, false, false},
                new Boolean[]{true, true, false, false},
                new Boolean[]{false, false, true, false},
                new Boolean[]{false, true, true, false},
                new Boolean[]{true, false, true, false},
                new Boolean[]{true, true, true, false},

                new Boolean[]{false, false, false, true},
                new Boolean[]{false, true, false, true},
                new Boolean[]{true, false, false, true},
                new Boolean[]{true, true, false, true},
                new Boolean[]{false, false, true, true},
                new Boolean[]{false, true, true, true},
                new Boolean[]{true, false, true, true},
                new Boolean[]{true, true, true, true});
    }

    public DataTypeEvaluationConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase,
                                                       final boolean sharedKieBase, final boolean sharedKieSession) {
        super(enforcedJitting, serializeKieBase, sharedKieBase, sharedKieSession);
    }

    @Test(timeout = 20000)
    public void testBooleanPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 20000)
    public void testBoolean() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 20000)
    public void testBytePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test(timeout = 20000)
    public void testByte() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteObjectValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test(timeout = 20000)
    public void testShortPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test(timeout = 20000)
    public void testShort() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortObjectValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test(timeout = 20000)
    public void testIntPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 20000)
    public void testInteger() throws InterruptedException {
        testFactAttributeType("    $factWithInteger: FactWithInteger(integerValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 20000)
    public void testLongPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 20000)
    public void testLong() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longObjectValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 20000)
    public void testFloatPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test(timeout = 20000)
    public void testFloat() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test(timeout = 20000)
    public void testDoublePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test(timeout = 20000)
    public void testDouble() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test(timeout = 20000)
    public void testBigDecimal() throws InterruptedException {
        testFactAttributeType("    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n", new FactWithBigDecimal(BigDecimal.TEN));
    }

    @Test(timeout = 20000)
    public void testCharPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(charValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 20000)
    public void testCharacter() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(characterValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 20000)
    public void testString() throws InterruptedException {
        testFactAttributeType("    $factWithString: FactWithString(stringValue == \"test\") \n", new FactWithString("test"));
    }

    @Test(timeout = 20000)
    public void testEnum() throws InterruptedException {
        testFactAttributeType("    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n", new FactWithEnum(AnEnum.FIRST));
    }

    private void testFactAttributeType(final String ruleConstraint, final Object factInserted) throws InterruptedException {
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

        parallelTest(NUMBER_OF_REPETITIONS, NUMBER_OF_THREADS, (kieSession, counter) -> {
            kieSession.insert(factInserted);
            final int rulesFired = kieSession.fireAllRules();
            return sharedKieSession || rulesFired == 1;
        }, "numberOfFirings", numberOfFirings, drl);

        if (sharedKieSession) {
            // This is 1 because engine doesn't insert an already existing object twice, so when sharing a session
            // the object should be present just once in the session. When not sharing a session, there is N separate
            // sessions, so each one should fire.
            Assertions.assertThat(numberOfFirings.get()).isEqualTo(1);
        } else {
            Assertions.assertThat(numberOfFirings.get()).isEqualTo(10);
        }
    }
}
