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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.SequentialOption;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that {@code @Propagation(IMMEDIATE)} is safely ignored in sequential mode,
 * and that {@code @Propagation(EAGER)} continues to work correctly.
 *
 * <p>{@code @Propagation(IMMEDIATE)} sets {@code RuleImpl.dataDriven = true}, which
 * causes the path to be evaluated as soon as a segment links, bypassing the agenda.
 * In sequential mode tuple memory is disabled, so this early evaluation hits null
 * tuple memory and either throws NPE or produces incorrect results.</p>
 *
 * <p>R2 uses {@code Person( age >= 0 )} so that a modify on age is delivered as an
 * <em>update</em> (not an insert) to R2's beta node, which is the condition that
 * triggers the NPE. A constraint like {@code Person()} would not react to age changes,
 * and {@code Person( age == 1 )} would not match before the modify.</p>
 *
 * @see <a href="https://github.com/apache/incubator-kie/issues/7209">incubator-kie#7209</a>
 */
public class SequentialPropagationTest {

    public static Stream<Arguments> parameters() {
        List<Arguments> args = new ArrayList<>();
        for (KieBaseTestConfiguration config : TestParametersUtil2.getKieBaseCloudConfigurations(true)) {
            args.add(Arguments.of(config, "IMMEDIATE"));
            args.add(Arguments.of(config, "EAGER"));
        }
        return args.stream();
    }

    /**
     * Single rule with the propagation annotation and no modify.
     * Without the fix, IMMEDIATE causes the rule not to fire at all.
     */
    @ParameterizedTest(name = "KieBase type={0}, propagation={1}")
    @MethodSource("parameters")
    public void testNoModify(KieBaseTestConfiguration kieBaseTestConfiguration, String propagation) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R @Propagation(" + propagation + ") when\n" +
                "    $p : Person()\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    list.add( \"R\" );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R");
    }

    /**
     * JoinNode left update: R1 modifies Person.age, R2 has {@code Person( age >= 0 )} joined with Cheese.
     * Without the fix, IMMEDIATE causes NPE on null left tuple memory.
     */
    @ParameterizedTest(name = "KieBase type={0}, propagation={1}")
    @MethodSource("parameters")
    public void testJoinNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration, String propagation) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 when\n" +
                "    $p : Person()\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    modify( $p ) { setAge( 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 @Propagation(" + propagation + ") when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $c : Cheese( type == $p.likes )\n" +
                "then\n" +
                "    list.add( \"R2:\" + $p.getAge() );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        Cheese c = new Cheese("stilton", 10);
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p, c);
        assertThat(list).containsExactly("R1", "R2:1");
    }

    /**
     * NotNode right update: R1 modifies Cheese.type to "cheddar", which should block
     * R2's {@code not Cheese( type == "cheddar" )}.
     * Without the fix, IMMEDIATE causes R2 to fire incorrectly.
     */
    @ParameterizedTest(name = "KieBase type={0}, propagation={1}")
    @MethodSource("parameters")
    public void testNotNodeRightUpdate(KieBaseTestConfiguration kieBaseTestConfiguration, String propagation) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 when\n" +
                "    $p : Person()\n" +
                "    $c : Cheese( type == \"stilton\" )\n" +
                "then\n" +
                "    modify( $c ) { setType( \"cheddar\" ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 @Propagation(" + propagation + ") when\n" +
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

    /**
     * FromNode left update: R1 changes address to Paris and modifies Person.age.
     * R2 has {@code Person( age >= 0 )} with {@code Address() from $p.addresses}.
     * Without the fix, IMMEDIATE causes NPE on null previousMatches.
     */
    @ParameterizedTest(name = "KieBase type={0}, propagation={1}")
    @MethodSource("parameters")
    public void testFromNodeLeftUpdate(KieBaseTestConfiguration kieBaseTestConfiguration, String propagation) {
        String str =
                "package org.drools.mvel.compiler.test\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "global " + List.class.getCanonicalName() + " list\n" +
                "rule R1 salience 100 when\n" +
                "    $p : Person()\n" +
                "    $a : Address( street == \"London\" ) from $p.addresses\n" +
                "then\n" +
                "    $a.setStreet( \"Paris\" );\n" +
                "    modify( $p ) { setAge( 1 ) };\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 @Propagation(" + propagation + ") when\n" +
                "    $p : Person( age >= 0 )\n" +
                "    $a : Address() from $p.addresses\n" +
                "then\n" +
                "    list.add( \"R2:\" + $a.getStreet() );\n" +
                "end\n";

        Person p = new Person("p1", "stilton", 0);
        p.addAddress(new Address("London"));
        List<String> list = executeSequential(str, kieBaseTestConfiguration, p);
        assertThat(list).containsExactly("R1", "R2:Paris");
    }

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
}
