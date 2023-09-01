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
package org.drools.compiler.integrationtests.operators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MathTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MathTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testAddition() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "global java.lang.Integer two;\n" +
                "\n" +
                "rule \"returnvalue rule test\"\n" +
                "    when\n" +
                "        $person1: Person( $age1 : age )\n" +
                "        // We have no autoboxing of primtives, so have to do by hand\n" +
                "        person2: Person( age == ( $age1 + two.intValue() ) )\n" +
                "    then\n" +
                "        list.add( $person1 );\n" +
                "        list.add( person2 );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("math-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("two", 2);

            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person peter = new Person("peter", null, 12);
            ksession.insert(peter);
            final Person jane = new Person("jane", null, 10);
            ksession.insert(jane);
            ksession.fireAllRules();

            assertThat(((List) ksession.getGlobal("list")).get(0)).isEqualTo(jane);
            assertThat(((List) ksession.getGlobal("list")).get(1)).isEqualTo(peter);
        } finally {
            ksession.dispose();
        }
    }

    public static class RandomNumber {
        private int randomNumber;

        public void begin() {
            this.randomNumber = new Random().nextInt(100 );
        }
        public void setValue(final int value) {
            this.randomNumber = value;
        }
        public int getValue() {
            return this.randomNumber;
        }
    }

    public class Guess implements Serializable {
        private Integer value;

        public void setValue(final Integer guess) {
            this.value = guess;
        }
        public Integer getValue() {
            return this.value;
        }

    }

    @Test
    public void testNumberComparisons() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + RandomNumber.class.getCanonicalName() + ";\n" +
                "import " + Guess.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule High\n" +
                "   when \n" +
                "      RandomNumber(randomValue:value)\n" +
                "      // literal constraint\n" +
                "      Guess(guess: value > 10)\n" +
                "   then \n" +
                "      results.add(\"LOWER\");\n" +
                "end\n" +
                "\n" +
                "rule Low\n" +
                "   when \n" +
                "      RandomNumber(randomValue:value)\n" +
                "      // return value constraint\n" +
                "      Guess(guess: value < ( 10 ) )\n" +
                "   then \n" +
                "      results.add(\"HIGHER\");\n" +
                "end\n" +
                "\n" +
                "rule Win\n" +
                "   when \n" +
                "      RandomNumber(randomValue:value)\n" +
                "      // variable constraint\n" +
                "      Guess(value==randomValue)\n" +
                "   then \n" +
                "      results.add(\"CORRECT\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("math-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            // asserting the sensor object
            final RandomNumber rn = new RandomNumber();
            rn.setValue(10);
            ksession.insert(rn);

            final Guess guess = new Guess();
            guess.setValue(5);

            final FactHandle handle = ksession.insert(guess);

            ksession.fireAllRules();

            // HIGHER
            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo("HIGHER");

            guess.setValue(15);
            ksession.update(handle, guess);

            ksession.fireAllRules();

            // LOWER
            assertThat(list.size()).isEqualTo(2);
            assertThat(list.get(1)).isEqualTo("LOWER");

            guess.setValue(10);
            ksession.update(handle, guess);

            ksession.fireAllRules();

            // CORRECT
            assertThat(list.size()).isEqualTo(3);
            assertThat(list.get(2)).isEqualTo("CORRECT");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testShiftOperator()  {
        final String drl = "dialect \"mvel\"\n" +
                "rule kickOff\n" +
                "when\n" +
                "then\n" +
                "   insert( Integer.valueOf( 1 ) );\n" +
                "   insert( Long.valueOf( 1 ) );\n" +
                "   insert( Integer.valueOf( 65552 ) ); // 0x10010\n" +
                "   insert( Long.valueOf( 65552 ) );\n" +
                "   insert( Integer.valueOf( 65568 ) ); // 0x10020\n" +
                "   insert( Long.valueOf( 65568 ) );\n" +
                "   insert( Integer.valueOf( 65536 ) ); // 0x10000\n" +
                "   insert( Long.valueOf( 65536L ) );\n" +
                "   insert( Long.valueOf( 4294967296L ) ); // 0x100000000L\n" +
                "end\n" +
                "rule test1\n" +
                "   salience -1\n" +
                "when\n" +
                "   $a: Integer( $one: intValue == 1 )\n" +
                "   $b: Integer( $shift: intValue )\n" +
                "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                "then\n" +
                "   System.out.println( \"test1 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                "end\n" +
                "rule test2\n" +
                "   salience -2\n" +
                "when\n" +
                "   $a: Integer( $one: intValue == 1 )\n" +
                "   $b: Long ( $shift: longValue )\n" +
                "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                "then\n" +
                "   System.out.println( \"test2 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                "end\n" +
                "rule test3\n" +
                "   salience -3\n" +
                "when\n" +
                "   $a: Long ( $one: longValue == 1 )\n" +
                "   $b: Long ( $shift: longValue )\n" +
                "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                "then\n" +
                "   System.out.println( \"test3 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                "end\n" +
                "rule test4\n" +
                "   salience -4\n" +
                "when\n" +
                "   $a: Long ( $one: longValue == 1 )\n" +
                "   $b: Integer( $shift: intValue )\n" +
                "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                "then\n" +
                "   System.out.println( \"test4 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("math-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(13);
        } finally {
            ksession.dispose();
        }
    }
}
