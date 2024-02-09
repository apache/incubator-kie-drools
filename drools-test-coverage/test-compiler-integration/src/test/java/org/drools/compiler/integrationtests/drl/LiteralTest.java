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
package org.drools.compiler.integrationtests.drl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Primitives;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class LiteralTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public LiteralTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testLiteral() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "global Cheesery cheesery;\n" +
                "\n" +
                "rule \"literal test rule\"\n" +
                "    when\n" +
                "        Cheese( $x: type, type == \"stilton\" )\n" +
                "    then\n" +
                "        list.add( $x );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("literal-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Cheese stilton = new Cheese("stilton", 5);
            session.insert(stilton);
            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo("stilton");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testLiteralWithEscapes() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"literal test rule\"\n" +
                "    when\n" +
                "        Cheese( $x: type, type == \"s\\tti\\\"lto\\nn\" )\n" +
                "    then\n" +
                "        list.add( $x );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("literal-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final String expected = "s\tti\"lto\nn";
            final Cheese stilton = new Cheese(expected, 5);
            session.insert(stilton);
            final int fired = session.fireAllRules();
            assertThat(fired).isEqualTo(1);

            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo(expected);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testLiteralWithBoolean() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Literal with boolean\"\n" +
                "\n" +
                "    when\n" +
                "        // conditions\n" +
                "        alivePerson : Person(alive ==  true)\n" +
                "    then\n" +
                "        list.add( alivePerson );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("literal-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Person bill = new Person("bill", null, 12);
            bill.setAlive(true);
            session.insert(bill);
            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo(bill);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testBigLiterals() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Primitives.class.getCanonicalName() + ";\n" +
                "rule X\n" +
                "when\n" +
                "    Primitives( bigInteger == 10I, bigInteger < (50I), bigDecimal == 10B, bigDecimal < (50B) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("literal-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final Primitives p = new Primitives();
            p.setBigDecimal(BigDecimal.valueOf(10));
            p.setBigInteger(BigInteger.valueOf(10));
            session.insert(p);

            final int rulesFired = session.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testBigDecimalIntegerLiteral() {

        final String drl = "package org.drools.compiler.integrationtests.drl\n" +
                "\n" +
                "import " + Primitives.class.getCanonicalName() + ";\n" +
                "import java.math.BigDecimal;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"BigDec\"\n" +
                "\n" +
                "    when\n" +
                "        p: Primitives(bigDecimal < 100.01)\n" +
                "    then\n" +
                "        System.err.println(\"rule1\");\n" +
                "        list.add( p );\n" +
                "end\n" +
                "\n" +
                "rule \"BigInt\"\n" +
                "    when\n" +
                "        p: Primitives(bigInteger < 100.1)\n" +
                "    then\n" +
                "    System.err.println(\"rule2\");\n" +
                "        list.add( p );\n" +
                "end\n" +
                "\n" +
                "rule \"BigDec2\"\n" +
                "\n" +
                "    when\n" +
                "        p: Primitives(bigDecimal == 42)\n" +
                "    then\n" +
                "    System.err.println(\"rule3\");\n" +
                "        list.add( p );\n" +
                "end\n" +
                "\n" +
                "rule \"BigInt2\"\n" +
                "    when\n" +
                "        p: Primitives(bigInteger == 42)\n" +
                "    then\n" +
                "    System.err.println(\"rule4\");\n" +
                "        list.add( p );\n" +
                "end\n" +
                "\n" +
                "rule \"BigDec3\"\n" +
                "\n" +
                "    when\n" +
                "        p: Primitives(bigDecimal != 100)\n" +
                "    then\n" +
                "    System.err.println(\"rule5\");\n" +
                "        list.add( p );\n" +
                "end\n" +
                "\n" +
                "rule \"BigInt3\"\n" +
                "    when\n" +
                "        p: Primitives(bigInteger != 100)\n" +
                "    then\n" +
                "    System.err.println(\"rule6\");\n" +
                "        list.add( p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("literal-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Primitives bill = new Primitives();
            bill.setBigDecimal(new BigDecimal("42"));
            bill.setBigInteger(new BigInteger("42"));

            session.insert(bill);
            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).size()).isEqualTo(6);
        } finally {
            session.dispose();
        }
    }
}
