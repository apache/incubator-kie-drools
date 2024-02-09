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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ForAllTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ForAllTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void test1P1CFiring1() {
        check("age >= 18", 1, new Person("Mario", 45));
    }

    @Test
    public void test1P1CFiring2() {
        check("age == 8 || == 45", 1, new Person("Mario", 45), new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiring() {
        check("age >= 18", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiringWithAnd() {
        check("name == \"Sofia\" && age >= 18", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiringWithParenthesis() {
        check("(name == \"Sofia\" && age >= 18)", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiringWithOr() {
        check("age >= 18 || name == \"Mario\"", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiringWithParenthesisAndOr() {
        check("(name == \"Sofia\" && age >= 18) || name == \"Mario\"", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P2CFiring() {
        check("age >= 18, name.startsWith(\"M\")", 1, new Person("Mario", 45), new Person("Mark", 43));
    }

    @Test
    public void test1P2CFiringWithIn() {
        check("age >= 18, name in(\"Mario\", \"Mark\")", 1, new Person("Mario", 45), new Person("Mark", 43));
    }

    @Test
    public void test1P2CNotFiring1() {
        check("age >= 18, name.startsWith(\"M\")", 0, new Person("Mario", 45), new Person("Mark", 43), new Person("Edson", 40));
    }

    @Test
    public void test1P2CNotFiring2() {
        check("age < 18, name.startsWith(\"M\")", 0, new Person("Sofia", 8));
    }

    @Test
    public void test2P1CFiring() {
        check("age >= 18", "name.startsWith(\"M\")", 1, new Person("Mario", 45), new Person("Sofia", 8));
    }

    @Test
    public void test2P1CNotFiring() {
        check("age >= 1", "name.startsWith(\"M\")", 0, new Person("Mario", 45), new Person("Sofia", 8));
    }

    @Test
    public void test2P2CFiring() {
        check("", "age >= 18, name.startsWith(\"M\")", 1, new Person("Mario", 45), new Person("Mark", 43));
    }

    @Test
    public void test2P2CNotFiring1() {
        check("", "age >= 18, name.startsWith(\"M\")", 0, new Person("Mario", 45), new Person("Mark", 43), new Person("Edson", 40));
    }

    @Test
    public void test2P3CFiring() {
        check("name.length() < 6", "age >= 18, name.startsWith(\"M\")", 1, new Person("Mario", 45), new Person("Mark", 43), new Person("Daniele", 43));
    }

    @Test
    public void testNoFiring() {
        // DROOLS-5915
        check("", "age >= 18", 0);
    }

    private void check(String constraints1, int expectedFires, Object... objs) {
        check( constraints1, null, expectedFires, objs );
    }

    private void check(String constraints1, String constraints2, int expectedFires, Object... objs) {
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1 when\n" +
                "    forall(\n" +
                "       $p: Person( " + constraints1 + " )\n" +
                ( constraints2 != null ?
                "       Person( this == $p," + constraints2 + " )\n" :
                "" ) +
                "    )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);

        final KieSession ksession = kbase.newKieSession();
        try {

            for (Object obj : objs) {
                ksession.insert( obj );
            }
            assertThat(ksession.fireAllRules()).isEqualTo(expectedFires);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWithDate() throws Exception {
        // DROOLS-4925

        String pkg = "org.drools.compiler.integrationtests.operators";

        String drl =
                "package " + pkg + ";\n" +
                "declare Fact\n" +
                "    d : java.util.Date\n" +
                "end\n" +
                "\n" +
                "rule \"forall with date\" when\n" +
                "  forall(Fact(d == \"01-Jan-2020\"))\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        FactType factType = kbase.getFactType(pkg, "Fact");

        for (int i = 0; i < 3; i++) {
            Object fact = factType.newInstance();
            factType.set(fact, "d", df.parse("01-Jan-2020"));
            ksession.insert(fact);
        }

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testWithIndexedAlpha() throws Exception {
        // DROOLS-5019

        String pkg = "org.drools.compiler.integrationtests.operators";

        String drl =
                "rule R1 when\n" +
                "  forall( $s: String() String( this == $s, toString == \"A\" ) )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  String( toString == \"B\" )\n" +
                "then\n" +
                "end\n" +
                "rule R3 when\n" +
                "  String( toString == \"C\" )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);

        KieSession ksession1 = kbase.newKieSession();
        ksession1.insert( "A" );
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

        KieSession ksession2 = kbase.newKieSession();
        ksession2.insert( "D" );
        assertThat(ksession2.fireAllRules()).isEqualTo(0);
    }

    @Test
    public void testForallWithNotEqualConstraint() throws Exception {
        // DROOLS-5100

        String drl =
                "rule \"forall with not equal\"\n" +
                "when forall(String(this != \"foo\"))\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new String("bar"));
        ksession.insert(new String("baz"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testForallWithNotEqualConstraintOnDate() throws Exception {
        // DROOLS-5224

        String drl =
                "rule \"forall with not equal\"\n" +
                "when forall(java.util.Date(this != \"29-Dec-2019\"))\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Date(0));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public class Pojo {

        private List<Integer> x = new ArrayList<>();
        private int y;
        private int z;

        public Pojo(List<Integer> x, int y, int z) {
            this.x.addAll(x);
            this.y = y;
            this.z = z;
        }

        public List<Integer> getX() {
            return x;
        }

        public void setX(List<Integer> x) {
            this.x.addAll(x);
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }

        @Override
        public String toString() {
            return "Pojo{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
        }
    }

    @Test
    public void testForallWithEmptyListConstraintCombinedWithOrFiring() throws Exception {
        checkForallWithEmptyListConstraintCombinedWithOrFiring(true);
    }

    @Test
    public void testForallWithEmptyListConstraintCombinedWithOrNotFiring() throws Exception {
        checkForallWithEmptyListConstraintCombinedWithOrFiring(false);
    }

    private void checkForallWithEmptyListConstraintCombinedWithOrFiring(boolean firing) {
        // DROOLS-5682

        String drl =
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "rule \"forall with not equal\"\n" +
                "when forall($p : Pojo(y == 1)\n" +
                "            Pojo(x.empty || x contains 2, z == 3, this == $p))\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Pojo(Collections.emptyList(), 1, 3));
        ksession.insert(new Pojo(List.of(2), 1, 3));
        ksession.insert(new Pojo(List.of(3), firing ? 0 : 1, 3));
        ksession.insert(new Pojo(List.of(2), firing ? 0 : 1, 0));

        assertThat(ksession.fireAllRules()).isEqualTo(firing ? 1 : 0);
    }

    @Test
    public void testForallWithMultipleConstraints() throws Exception {
        checkForallWithComplexExpression("value > 1, value < 10");
    }

    @Test
    public void testForallWithAnd() throws Exception {
        checkForallWithComplexExpression("value > 1 && value < 10");
    }

    @Test
    public void testForallWithAndInMultipleConstraints() throws Exception {
        checkForallWithComplexExpression("value > 1, value > 2 && value < 10");
    }

    @Test
    public void testForallWithOr() throws Exception {
        checkForallWithComplexExpression("value < 1 || value > 50");
    }

    @Test
    public void testForallWithIn() throws Exception {
        // DROOLS-6560
        checkForallWithComplexExpression("value in (1, 43)");
    }

    @Test
    public void testForallWithNotIn() throws Exception {
        // DROOLS-6560
        checkForallWithComplexExpression("value not in (1, 42)");
    }

    private void checkForallWithComplexExpression(String expression) throws Exception {
        // DROOLS-6469
        String drl =
                "package test\n" +
                "declare Fact\n" +
                "    tag : String\n" +
                "    value : int\n" +
                "end\n" +
                "rule \"Rule\"\n" +
                "when\n" +
                "forall (f:Fact(tag == \"X\") Fact(this==f, " + expression + "))\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        FactType ft = kbase.getFactType("test", "Fact");
        Object f1 = ft.newInstance();
        ft.set(f1, "tag", "X");
        ft.set(f1, "value", 42);
        ksession.insert(f1);
        Object f2 = ft.newInstance();
        ft.set(f2, "tag", "Y");
        ft.set(f2, "value", 42);
        ksession.insert(f2);

        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }
}
