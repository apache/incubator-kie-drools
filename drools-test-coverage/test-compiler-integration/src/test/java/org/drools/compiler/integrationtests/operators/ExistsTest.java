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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.AFact;
import org.drools.testcoverage.common.model.Cheese;
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
public class ExistsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ExistsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testExistsIterativeModifyBug() {
        // JBRULES-2809
        // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
        // However before this was done it would attempt to find the next tuple, starting from itself
        // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
        // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "import " + AFact.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule xxx \n" +
            "when \n" +
            "  $f1 : AFact() \n" +
            "    exists AFact(this != $f1, eval(field2 == $f1.getField2())) \n" +
            "    eval( !$f1.getField1().equals(\"1\") ) \n" +
            "then \n" +
            "  list.add($f1); \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final AFact a1 = new AFact("2", "2");
            final AFact a2 = new AFact("1", "2");
            final AFact a3 = new AFact("1", "2");

            final FactHandle fa1 = ksession.insert(a1);
            final FactHandle fa2 = ksession.insert(a2);
            final FactHandle fa3 = ksession.insert(a3);

            // a2, a3 are blocked by a1
            // modify a1, so that a1,a3 are now blocked by a2
            a1.setField2("1"); // Do
            ksession.update(fa1, a1);
            a1.setField2("2"); // Undo
            ksession.update(fa1, a1);

            // modify a2, so that a1,a2 are now blocked by a3
            a2.setField2("1"); // Do
            ksession.update(fa2, a2);
            a2.setField2("2"); // Undo
            ksession.update(fa2, a2);

            // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
            ksession.update(fa3, a3);

            a3.setField2("1"); // Do
            ksession.update(fa3, a3);
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1); // a2 should still be blocked by a1, but bug from previous update hanging onto blocked
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNodeSharingNotExists() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule rule1\n" +
                "when\n" +
                "       not Cheese( type == Cheese.STILTON )\n" +
                "then\n" +
                "       results.add( \"rule1\" );\n" +
                "end\n" +
                "\n" +
                "rule rule2\n" +
                "when\n" +
                "       exists Cheese( type == Cheese.STILTON )\n" +
                "then\n" +
                "       results.add( \"rule2\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo("rule1");

            ksession.insert(new Cheese("stilton", 10));
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.get(1)).isEqualTo("rule2");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testLastMemoryEntryExistsBug() {
        // JBRULES-2809
        // This occurs when a blocker is the last in the node's memory, or if there is only one fact in the node
        // And it gets no opportunity to rematch with itself

        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "import " + AFact.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule x1 \n" +
            "when \n" +
            "    $s : String( this == 'x1' ) \n" +
            "    exists AFact( this != null ) \n" +
            "then \n" +
            "  list.add(\"fired x1\"); \n" +
            "end  \n" +
            "rule x2 \n" +
            "when \n" +
            "    $s : String( this == 'x2' ) \n" +
            "    exists AFact( field1 == $s, this != null ) \n" + // this ensures an index bucket
            "then \n" +
            "  list.add(\"fired x2\"); \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert("x1");
            ksession.insert("x2");
            final AFact a1 = new AFact("x1", null);
            final AFact a2 = new AFact("x2", null);

            final FactHandle fa1 = ksession.insert(a1);
            final FactHandle fa2 = ksession.insert(a2);

            // make sure the 'exists' is obeyed when fact is cycled causing add/remove node memory
            ksession.update(fa1, a1);
            ksession.update(fa2, a2);
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testExistsWithOrAndSubnetwork() {
        // DROOLS-6550
        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "global java.util.List list \n" +
            "rule \"Rule Result\" salience 100 when\n" +
            "        exists (\n" +
            "            String()\n" +
            "            or ( Integer() and Long() )\n" +
            "        )\n" +
            "    then\n" +
            "        list.add(\"ok\");\n" +
            "end\n" +
            "\n" +
            "rule Init when\n" +
            "then\n" +
            "    insert(\"test\");\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo("ok");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSharedExistsWithNot() {
        // DROOLS-6710
        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "global java.util.List list \n" +
            "rule R1 when\n" +
            "        exists\n" +
            "        (\n" +
            "           String(this == \"A\")\n" +
            "           and String(this == \"B\")\n" +
            "        )\n" +
            "    then\n" +
            "        list.add(\"NOT OK\");\n" +
            "end\n" +
            "\n" +
            "rule R2 when\n" +
            "        exists\n" +
            "        (\n" +
            "           String(this == \"A\")\n" +
            "           and not String(this == \"B\")\n" +
            "        )\n" +
            "    then\n" +
            "        list.add(\"OK\");\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert("A");
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo("OK");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void existsAndNotWithSingleCoercion_shouldNotMatchExists() {
        // KIE-766
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                           "import " + StringHolder.class.getCanonicalName() + "\n" +
                           "import " + BDHolder.class.getCanonicalName() + "\n" +
                           "global java.util.List list \n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $stringHolder : StringHolder( $value1 : value1 )\n" +
                           "    exists BDHolder( $value1 == value1 )\n" +
                           "  then\n" +
                           "    list.add(\"R1\");\n" +
                           "end\n" +
                           "rule R2\n" +
                           "  when\n" +
                           "    $stringHolder : StringHolder( $value1 : value1 )\n" +
                           "    not BDHolder( $value1 == value1 )\n" +
                           "  then\n" +
                           "    list.add(\"R2\");\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(new StringHolder("9999", "567"));
            ksession.insert(new BDHolder(new BigDecimal("0"), new BigDecimal("567")));
            ksession.fireAllRules();

            assertThat(list).hasSize(1);
            assertThat(list).as("BDHolder shouldn't match the exists constraints").containsExactly("R2");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void existsAndNotWithMultipleCoercion_shouldNotMatchExists() {
        // KIE-766
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                           "import " + StringHolder.class.getCanonicalName() + "\n" +
                           "import " + BDHolder.class.getCanonicalName() + "\n" +
                           "global java.util.List list \n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $stringHolder : StringHolder( $value1 : value1, $value2 : value2 )\n" +
                           "    exists BDHolder( $value1 == value1, $value2 == value2 )\n" +
                           "  then\n" +
                           "    list.add(\"R1\");\n" +
                           "end\n" +
                           "rule R2\n" +
                           "  when\n" +
                           "    $stringHolder : StringHolder( $value1 : value1, $value2 : value2 )\n" +
                           "    not BDHolder( $value1 == value1, $value2 == value2 )\n" +
                           "  then\n" +
                           "    list.add(\"R2\");\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(new StringHolder("9999", "567"));
            ksession.insert(new BDHolder(new BigDecimal("0"), new BigDecimal("567")));
            ksession.fireAllRules();

            assertThat(list).hasSize(1);
            assertThat(list).as("BDHolder shouldn't match the exists constraints").containsExactly("R2");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void existsAndNotWithBigDecimals_shouldNotMatchExists() {
        // KIE-766
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                           "import " + BDHolder.class.getCanonicalName() + "\n" +
                           "global java.util.List list \n" +
                           "rule R1\n" +
                           "  when\n" +
                           "    $bdHolder1 : BDHolder( $value1 : value1, $value2 : value2 )\n" +
                           "    exists BDHolder( this != $bdHolder1, $value1 == value1, $value2 == value2 )\n" +
                           "  then\n" +
                           "    list.add(\"R1\");\n" +
                           "end\n" +
                           "rule R2\n" +
                           "  when\n" +
                           "    $bdHolder1 : BDHolder( $value1 : value1, $value2 : value2 )\n" +
                           "    not BDHolder( this != $bdHolder1, $value1 == value1, $value2 == value2 )\n" +
                           "  then\n" +
                           "    list.add(\"R2\");\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exists-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(new BDHolder(new BigDecimal("9999"), new BigDecimal("567")));
            ksession.insert(new BDHolder(new BigDecimal("0"), new BigDecimal("567")));
            ksession.fireAllRules();

            assertThat(list).hasSize(2);
            assertThat(list).as("BDHolder shouldn't match the exists constraints. R2 fires twice because 2 objects can match as $bdHolder1")
                            .containsExactly("R2", "R2");
        } finally {
            ksession.dispose();
        }
    }

    public static class StringHolder {

        private String value1;
        private String value2;

        public StringHolder(String value1, String value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

    }

    public static class BDHolder {

        private BigDecimal value1;
        private BigDecimal value2;

        public BDHolder(BigDecimal value1, BigDecimal value2) {
            super();
            this.value1 = value1;
            this.value2 = value2;
        }

        public BigDecimal getValue1() {
            return value1;
        }

        public void setValue1(BigDecimal value1) {
            this.value1 = value1;
        }

        public BigDecimal getValue2() {
            return value2;
        }

        public void setValue2(BigDecimal value2) {
            this.value2 = value2;
        }

    }
}
