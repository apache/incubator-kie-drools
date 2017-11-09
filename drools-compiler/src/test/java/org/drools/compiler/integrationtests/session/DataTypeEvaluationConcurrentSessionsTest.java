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

package org.drools.compiler.integrationtests.session;

import java.math.BigDecimal;
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
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentSessionsTest {

    public DataTypeEvaluationConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase) {
        super(enforcedJitting, serializeKieBase);
    }

    @Test
    public void testBooleanPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanValue == false) \n", new FactWithBoolean(false));
    }

    @Test
    public void testBoolean() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n", new FactWithBoolean(false));
    }

    @Test
    public void testBytePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test
    public void testByte() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteObjectValue == 15) \n", new FactWithByte((byte) 15));
    }

    @Test
    public void testShortPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test
    public void testShort() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortObjectValue == 15) \n", new FactWithShort((short) 15));
    }

    @Test
    public void testIntPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @Test
    public void testInteger() throws InterruptedException {
        testFactAttributeType("    $factWithInteger: FactWithInteger(integerValue == 15) \n", new FactWithInteger(15));
    }

    @Test
    public void testLongPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @Test
    public void testLong() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longObjectValue == 15) \n", new FactWithLong(15));
    }

    @Test
    public void testFloatPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test
    public void testFloat() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n", new FactWithFloat(15.1f));
    }

    @Test
    public void testDoublePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test
    public void testDouble() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n", new FactWithDouble(15.1d));
    }

    @Test
    public void testBigDecimal() throws InterruptedException {
        testFactAttributeType("    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n", new FactWithBigDecimal(BigDecimal.TEN));
    }

    @Test
    public void testCharPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(charValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test
    public void testCharacter() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(characterValue == 'a') \n", new FactWithCharacter('a'));
    }

    @Test
    public void testString() throws InterruptedException {
        testFactAttributeType("    $factWithString: FactWithString(stringValue == \"test\") \n", new FactWithString("test"));
    }

    @Test
    public void testEnum() throws InterruptedException {
        testFactAttributeType("    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n", new FactWithEnum(AnEnum.FIRST));
    }

    private void testFactAttributeType(final String ruleConstraint, final Object factInserted) throws InterruptedException {
        final String drl1 =
                " import org.drools.compiler.integrationtests.facts.*;\n" +
                        " rule R1 \n" +
                        " when \n" +
                        ruleConstraint +
                        " then \n" +
                        " end ";

        parallelTest( 1, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                kieSession.insert(factInserted);
                return kieSession.fireAllRules() == 1;
            }
        }, drl1);
    }

    @Test
    public void testEnum2() throws InterruptedException {
        String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                        "import " + CategoryTypeEnum.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "  $s : String( this == \"odd\" )\n" +
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.ODD, firings not contains \"R1\" )\n" +
                        "then\n" +
                        "  $p.getFirings().add(\"R1\");\n" +
                        "  $p.appendDescription($s);\n" +
                        "  update($p);\n" +
                        "end\n";

        String drl2 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                        "import " + CategoryTypeEnum.class.getCanonicalName() + ";\n" +
                        "rule R2 when\n" +
                        "  $s : String( this == \"pair\" )\n" +
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.PAIR, firings not contains \"R2\" )\n" +
                        "then\n" +
                        "  $p.getFirings().add(\"R2\");\n" +
                        "  $p.appendDescription($s);\n" +
                        "  update($p);" +
                        "end\n";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                Product[] products = new Product[10];
                final boolean pair = counter % 2 == 0;
                final String pairString = pair ? "pair" : "odd";
                for (int i = 0; i < 10; i++) {
                    products[i] = new Product( "" + i, pairString );
                }

                kieSession.insert( pairString );
                for (int i = 0; i < 10; i++) {
                    kieSession.insert( products[i] );
                }

                kieSession.fireAllRules();

                for (int i = 0; i < 10; i++) {
                    if ( products[i].getCategory().equals(pairString) && !products[i].getCategory().equals( products[i].getDescription() ) ) {
                        return false;
                    } else if (!products[i].getCategory().equals(pairString) && !products[i].getDescription().isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
        }, drl1, drl2 );
    }
}
