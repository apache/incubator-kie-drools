/*
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
package org.drools.mvel.integrationtests.sequential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.SequentialOption;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that sequential mode disables beta node sharing by default,
 * producing correct results for all beta node types (FromNode, JoinNode,
 * AccumulateNode, NotNode, ExistsNode) when rules use modify().
 *
 * @see <a href="https://github.com/apache/incubator-kie/issues/7201">incubator-kie#7201</a>
 */
public class SequentialNodeSharingTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    // =====================================================================
    // FromNode tests
    // =====================================================================

    /**
     * FromNode - left update: R1 modifies $p (the from source), R2 shares the same from node.
     * Previously caused NPE in PhreakFromNode.doLeftUpdates.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testFromNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $a : Address() from $p.addresses\n" +
                "then\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $a : Address() from $p.addresses\n" +
                "then\n" +
                "    list.add( \"R2:\" + $a.getStreet() );\n" +
                "end\n";

        List<String> list = executeSequential(str, kieBaseTestConfiguration, createPersonWithAddress());
        assertThat(list).containsExactly("R1", "R2:London");
    }

    /**
     * FromNode - right update: R1 changes address from London to Paris. R2 should see the updated address.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testFromNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $a : Address( street == \"London\" ) from $p.addresses\n" +
                "then\n" +
                "    $a.setStreet( \"Paris\" );\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $a : Address() from $p.addresses\n" +
                "then\n" +
                "    list.add( \"R2:\" + $a.getStreet() );\n" +
                "end\n";

        List<String> list = executeSequential(str, kieBaseTestConfiguration, createPersonWithAddress());
        assertThat(list).containsExactly("R1", "R2:Paris");
    }

    // =====================================================================
    // JoinNode tests
    // =====================================================================

    /**
     * JoinNode - left update: R1 modifies $p (left input), R2 shares the same join node.
     * Previously caused NPE in doUpdatesReorderLeftMemory.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testJoinNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    list.add( \"R2:\" + $c.getType() );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1", "R2:stilton");
    }

    /**
     * JoinNode - right update: R1 modifies $c (right input, Cheese).
     * Previously produced incorrect result with stale match.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testJoinNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    modify( $c ) { setType( \"cheddar\" ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    list.add( \"R2:\" + $c.getType() );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1");
    }

    // =====================================================================
    // AccumulateNode tests
    // =====================================================================

    /**
     * AccumulateNode - left update: R1 modifies $p (left input), R2 shares the same accumulate node.
     * Previously caused NPE via doUpdatesReorderLeftMemory.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAccumulateNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $total : Number() from accumulate(\n" +
                "        Cheese( type == $p.likes, $price : price ),\n" +
                "        sum( $price )\n" +
                "    )\n" +
                "then\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1:\" + $total );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $total : Number() from accumulate(\n" +
                "        Cheese( type == $p.likes, $price : price ),\n" +
                "        sum( $price )\n" +
                "    )\n" +
                "then\n" +
                "    list.add( \"R2:\" + $total );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c1 = new Cheese("stilton", 10);
        Cheese c2 = new Cheese("stilton", 20);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c1, c2);
        assertThat(list).containsExactly("R1:30", "R2:30");
    }

    /**
     * AccumulateNode - right update: R1b modifies cheese price.
     * Previously produced incorrect result with pre-update value.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAccumulateNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $total : Number() from accumulate(\n" +
                "        $c : Cheese( type == $p.likes, $price : price ),\n" +
                "        sum( $price )\n" +
                "    )\n" +
                "then\n" +
                "    list.add( \"R1:\" + $total );\n" +
                "end\n" +
                "rule R1b salience 50 no-loop when\n" +
                "    $c : Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    modify( $c ) { setPrice( 99 ) };\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $total : Number() from accumulate(\n" +
                "        $c : Cheese( type == $p.likes, $price : price ),\n" +
                "        sum( $price )\n" +
                "    )\n" +
                "then\n" +
                "    list.add( \"R2:\" + $total );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c1 = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c1);
        assertThat(list).containsExactly("R1:10", "R1b", "R2:99");
    }

    // =====================================================================
    // NotNode tests
    // =====================================================================

    /**
     * NotNode - left update: R1 modifies $p (left input), R2 shares the same not node.
     * Previously caused NPE in doUpdatesExistentialReorderLeftMemory.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    not Cheese( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    not Cheese( type == \"cheddar\" )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1", "R2");
    }

    /**
     * NotNode - right update: R1 changes cheese to "cheddar", which should block R2's "not Cheese(type == 'cheddar')".
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    modify( $c ) { setType( \"cheddar\" ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    not Cheese( type == \"cheddar\" )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1");
    }

    // =====================================================================
    // ExistsNode tests
    // =====================================================================

    /**
     * ExistsNode - left update: R1 modifies $p (left input), R2 shares the same exists node.
     * Previously caused R2 to fire twice.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExistsNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    exists Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    modify( $p ) { setAge( $p.getAge() + 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    exists Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1", "R2");
    }

    /**
     * ExistsNode - right update: R1b changes cheese from stilton to cheddar,
     * so R2's "exists Cheese(type == 'stilton')" should fail.
     * Previously R2 incorrectly fired after stilton was removed.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExistsNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 no-loop when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    exists Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R1b salience 50 no-loop when\n" +
                "    $c : Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    modify( $c ) { setType( \"cheddar\" ) };\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    exists Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1", "R1b");
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    private List<String> executeSequential(String drl, KieBaseTestConfiguration kieBaseTestConfiguration, Object... facts) {
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, drl);
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(
                kieModule, kieBaseTestConfiguration, SequentialOption.YES);
        StatelessKieSession ksession = kbase.newStatelessKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.execute(CommandFactory.newInsertElements(Arrays.asList(facts)));
        return list;
    }

    private Person createPersonWithAddress() {
        Person p = new Person("p1", "stilton", 0);
        p.addAddress(new Address("London"));
        return p;
    }
}
