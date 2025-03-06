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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NamedConsequencesTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequences(KieBaseTestConfiguration kieBaseTestConfiguration) {
        List<String> results = executeTestWithCondition(kieBaseTestConfiguration, "do[t1]");

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    private List<String> executeTestWithCondition(KieBaseTestConfiguration kieBaseTestConfiguration, String conditionElement) {
        String drl = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    " + conditionElement + "\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        return executeTestWithDRL(kieBaseTestConfiguration, drl);
    }

    private List<String> executeTestWithDRL(KieBaseTestConfiguration kieBaseTestConfiguration, String drl) {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        Cheese stilton = new Cheese( "stilton", 5 );
        Cheese cheddar = new Cheese( "cheddar", 7 );
        Cheese brie = new Cheese( "brie", 5 );

        ksession.insert( stilton );
        ksession.insert( cheddar );
        ksession.insert( brie );

        ksession.fireAllRules();
        return results;
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNonCompilingBreakingConsequences(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    break[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNonCompilingDuplicatedNamedConsequence(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOutOfScopeNamedConsequences(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $b.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAllowedIfDo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        List<String> results = executeTestWithCondition(kieBaseTestConfiguration, "if ( price < 10 ) do[t1]");

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotAllowedIfDo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        List<String> results = executeTestWithCondition(kieBaseTestConfiguration, "if ( price > 10 ) do[t1]");

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAllowedIfBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        List<String> results = executeTestWithCondition(kieBaseTestConfiguration, "if ( price < 10 ) break[t1]");

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotAllowedIfBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        List<String> results = executeTestWithCondition(kieBaseTestConfiguration, "if ( price > 10 ) break[t1]");

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequencesOutsideOR(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" )\n" +
                "    or\n" +
                "    $a: Cheese ( type == \"gorgonzola\" ) )\n" +
                "    do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    @Disabled("Moved to EdgeCaseNonExecModelTest")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequencesInsideOR1(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" ) do[t1]\n" +
                "    or\n" +
                "    $b: Cheese ( type == \"gorgonzola\" ) )\n" +
                "    $c: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $c.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    @Disabled("Moved to EdgeCaseNonExecModelTest")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequencesInsideOR2(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" )\n" +
                "    or\n" +
                "    $b: Cheese ( type == \"gorgonzola\" ) do[t1] )\n" +
                "    $c: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $c.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $b.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOutOfScopeNamedConsequencesWithOr1(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" )\n" +
                "    or\n" +
                "    $b: Cheese ( type == \"gorgonzola\" ) do[t1] )\n" +
                "    $c: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $c.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOutOfScopeNamedConsequencesWithOr2(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" )\n" +
                "    or\n" +
                "    $b: Cheese ( type == \"gorgonzola\" ) do[t1] )\n" +
                "    $c: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $c.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $c.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNonCompilingIFAfterOR(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    ( $a: Cheese ( type == \"stilton\" )\n" +
                "    or\n" +
                "    $a: Cheese ( type == \"gorgonzola\" ) )\n" +
                "    if ( price > 10 ) do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElse1(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > 10 ) do[t1] else do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElseWithConstant(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-325
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > Cheese.BASE_PRICE ) do[t1] else do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElseWithMvelAccessor(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-324
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( $a.price > Cheese.BASE_PRICE ) do[t1] else do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElse2(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price < 10 ) do[t1] else do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElseBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > 10 ) do[t1] else break[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNestedIfElseBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( type.startsWith(\"a\") ) do[t0] else if ( price > 10 ) do[t1] else break[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t0]\n" +
                "    results.add( \"WRONG!\" );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfWithModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > 10 ) break[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEndlessIfWithModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > 10 ) do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "    results.add( \"modify\" );\n" +
                "    if (results.size() > 10) throw new RuntimeException();\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "    if (results.size() > 10) throw new RuntimeException();\n" +
                "end\n";

        assertThatThrownBy(() -> executeTestWithDRL(kieBaseTestConfiguration, str))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfWithModify2(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price < 10 ) break[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $a.getType() );\n" +
                "then[t1]\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfWithModify3(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\", price < 10 )\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price > 10 ) break[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testIfElseWithModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price < 10 ) do[t1] else break[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEndlessIfElseWithModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    if ( price < 10 ) do[t1] else do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    modify( $a ) { setPrice(15) };\n" +
                "    results.add( \"modify\" );\n" +
                "    if (results.size() > 10) throw new RuntimeException();\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "    if (results.size() > 10) throw new RuntimeException();\n" +
                "end\n";

        assertThatThrownBy(() -> executeTestWithDRL(kieBaseTestConfiguration, str))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequenceAfterNotPattern(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-5
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    not Cheese ( type == \"brie\" )\n" +
                "    do[t1]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Cheese( "stilton", 5 ) );
        ksession.insert( new Cheese("cheddar", 7 ) );

        ksession.fireAllRules();

        assertThat(results.contains("stilton")).isTrue();
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMultipleIfAfterEval(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $a: Cheese ( )\n" +
                "    eval( $a.getType().equals(\"stilton\") )\n" +
                "    if ( $a.getPrice() > 10 ) do[t1]\n" +
                "    if ( $a.getPrice() < 10 ) do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "then[t2]\n" +
                "    results.add( $a.getType() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMultipleIfElseInARow(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-26
        String str =
                "global java.util.List results;" +
                        "declare UnBlocker end \n" +
                        "\n" +
                        "declare Car\n" +
                        "  colour\t: String \n" +
                        "  price \t: int\n" +
                        "  horsepower \t: int\n" +
                        "  abs \t\t: boolean\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Init\" \n" +
                        "when \n" +
                        "then \n" +
                        "  insert( \n" +
                        "\tnew Car( \"red\", 1200, 170, true ) \n" +
                        "  ); \n" +
                        "end\n" +
                        "\n" +
                        "rule \"Car\" \n" +
                        "when \n" +
                        "  $car: Car( abs == true ) \n" +
                        "  if ( colour == \"red\" ) do[red] " +
                        "  else if ( colour != \"red\" ) do[notRed]\n" +
                        "  if ( price < 1000 ) do[cheap] " +
                        "  else do[notCheap]\n" +
                        " UnBlocker() \n" +
                        "then\n" +
                        "  results.add( \"Found a Car\" ); \n" +
                        "then[red]\n" +
                        "  results.add( \"Car is red\" ); " +
                        "  insert( new UnBlocker() ); \n" +
                        "then[notRed]\n" +
                        "  results.add( \"Car is NOT red\" ); \n" +
                        "then[cheap]\n" +
                        "  results.add( \"Car is cheap\" ); \n" +
                        "then[notCheap]\n" +
                        "  results.add( \"Car is NOT cheap\" ); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.contains("Found a Car")).isTrue();
        assertThat(results.contains("Car is red")).isTrue();
        assertThat(results.contains("Car is NOT cheap")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDynamicSalience(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-335
        String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule R1 salience( -$id ) when\n" +
                "    fact : Fact( status == Fact.Status.UNKNOWN, $id : id)\n" +
                "    count : Long() from accumulate ( $s:Fact(this != fact, status==Fact.Status.NO, id < fact.id), count( $s ) )" +
                "    if (count.intValue() > 1) break[yes]\n" +
                "then\n" +
                "    results.add(\"n\" + $id);" +
                "    fact.setStatus(Fact.Status.NO);\n" +
                "    update(fact);\n" +
                "then[yes]\n" +
                "    results.add(\"y\" + $id);" +
                "    fact.setStatus(Fact.Status.YES);\n" +
                "    update(fact);\n" +
                "end\n" +
                "    \n" +
                "rule R2 salience 1 when\n" +
                "    fact : Fact( status == Fact.Status.NO, $id : id )\n" +
                "    Fact( status == Fact.Status.YES, id > $id )\n" +
                "then\n" +
                "    delete(fact);\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);
        for (int i = 1; i < 7; i++) {
            ksession.insert(new Fact(i));
        }

        ksession.fireAllRules();
        assertThat(results).isEqualTo(asList("n1", "n2", "y3", "n4", "n5", "y6"));
    }

    public static class Fact {
        public enum Status { UNKNOWN, NO, YES };
        private final int id;
        private Status status = Status.UNKNOWN;

        public Fact(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Fact(" + id + ")";
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNamedConsequenceOnEvents(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-641
        String drl =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "declare StockTick \n" +
                "    @role( event )" +
                "    @timestamp( time )\n" +
                "end\n" +
                "rule R when\n" +
                "    $s1 : StockTick( company == \"XXX\" )\n" +
                "    $s2 : StockTick( price > $s1.price ) do[t1]\n" +
                "    $s3 : StockTick( price < $s1.price )\n" +
                "then\n" +
                "    list.add( \"t0:\" + $s3.getCompany() );\n" +
                "then[t1]\n" +
                "    list.add( \"t1:\" + $s2.getCompany() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(new StockTick(1L, "XXX", 10, 0L));
        ksession.insert(new StockTick(2L, "YYY", 15, 1L));
        ksession.insert(new StockTick(3L, "ZZZ", 5, 2L));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.containsAll(asList("t1:YYY", "t0:ZZZ"))).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNoLoop(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-644
        String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R no-loop when\n" +
                "    $p1 : Person( name == \"Mario\" ) do[t1]\n" +
                "    $p2 : Person( age > $p1.age )\n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "    modify($p2) { setAge(30); }\n" +
                "then[t1]\n" +
                "    list.add(\"t1\");\n" +
                "    modify($p1) { setAge(35); }\n" +
                "end\n";

        // This test has uncovered a BLOCKER which is reported in the PropertyReactiveBlockersTest
        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put("drools.propertySpecific", "ALLOWED");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration,
                                                                 kieModuleConfigurationProperties,
                                                                 drl);
        final KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Person mario = new Person("Mario", 40);
        Person mark = new Person("Mark", 37);
        ksession.insert(mario);
        ksession.insert(mark);
        ksession.fireAllRules();

        assertThat(mario.getAge()).isEqualTo(35);
        assertThat(mark.getAge()).isEqualTo(30);

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo("t1");
        assertThat(list.get(1)).isEqualTo("t0");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMVELBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "    if ( 200 < 400 ) break[t1]\n" +
                "then\n" +
                "    results.add( $b.type );\n" +
                "then[t1]\n" +
                "    results.add( $a.type.toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        System.out.println( results );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMVELNoBreak(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "    if ( 200 > 400 ) break[t1]\n" +
                "then\n" +
                "    results.add( $b.type );\n" +
                "then[t1]\n" +
                "    results.add( $a.type.toUpperCase() );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        System.out.println( results );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMvelInsertWithNamedConsequence(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-726
        String drl2 =
                "package org.drools.compiler\n" +
                "global java.util.concurrent.atomic.AtomicInteger counter\n" +
                "declare Output\n" +
                "    feedback: String\n" +
                "end\n" +
                "rule \"Move to next\" dialect \"mvel\"\n" +
                "   when\n" +
                "          $i: Integer()\n" +
                "          if ($i == 1) break[nextStep1]\n" +
                "   then\n" +
                "           insert(new Output(\"defualt\"));\n" +
                "   then[nextStep1]\n" +
                "           insert(new Output(\"step 1\"));\n" +
                "end\n" +
                "\n" +
                "rule \"Produce output\"\n" +
                "    when\n" +
                "        $output: Output()\n" +
                "    then\n" +
                "        System.out.println($output);\n" +
                "        retract($output);" +
                "        counter.incrementAndGet();\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession kSession = kbase.newKieSession();

        AtomicInteger counter = new AtomicInteger(0);
        kSession.setGlobal("counter", counter);

        FactHandle messageHandle = kSession.insert(1);
        kSession.fireAllRules();

        kSession.delete(messageHandle);
        kSession.insert(2);
        kSession.fireAllRules();

        assertThat(counter.get()).isEqualTo(2);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDeleteWithBreakingBranch(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // DROOLS-1068
        String drl =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  Integer()\n" +
                "  if (true) break[branch]\n" +
                "  not Integer()\n" +
                "then\n" +
                "  list.add(\"main\");\n" +
                "then[branch]\n" +
                "  list.add(\"branch\");\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        FactHandle fh = ksession.insert(1);
        ksession.fireAllRules();
        ksession.delete(fh);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("branch");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithBreakingBranch(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // DROOLS-1115
        String drl =
                "import " + ListHolder.class.getCanonicalName() + ";\n" +
                "query q(ListHolder $listHolder)\n" +
                "    $listHolder := ListHolder()\n" +
                "    $i : Integer()\n" +
                "end\n" +
                "\n" +
                "rule R when\n" +
                "    q($listHolder;)\n" +
                "    $s : String() from $listHolder.list\n" +
                "    if (1==2) break[branch]\n" +
                "then\n" +
                "    $listHolder.getList().add(\"ok\");\n" +
                "then[branch]\n" +
                "    $listHolder.getList().add(\"ko\");\n" +
                "end\n" +
                "\n" +
                "rule Init when then insert(1); end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        list.add("test");

        ksession.insert( new ListHolder(list) );

        ksession.fireAllRules();

        assertThat(list.get(1)).isEqualTo("ok");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testInheritance(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "dialect \"mvel\"\n" +
                "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R0 when\n" +
                "    $a: Cheese ( )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R1 extends R0 when\n" +
                "    eval( $a.getType().equals(\"stilton\") )\n" +
                "    if ( $a.getPrice() > 10 ) do[t1]\n" +
                "    if ( $a.getPrice() < 10 ) do[t2]\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "then\n" +
                "    results.add( $b.type );\n" +
                "then[t1]\n" +
                "    results.add( $a.type.toUpperCase() );\n" +
                "then[t2]\n" +
                "    results.add( $a.type );\n" +
                "end\n";

        List<String> results = executeTestWithDRL(kieBaseTestConfiguration, str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testWrongConsequenceName(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.Cheese;\n " +
                "global java.util.List results;\n" +
                "\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $a: Cheese ( type == \"stilton\" )\n" +
                "    $b: Cheese ( type == \"cheddar\" )\n" +
                "    if ( 200 < 400 ) break[t2]\n" +
                "then\n" +
                "    results.add( $b.getType() );\n" +
                "then[t1]\n" +
                "    results.add( $a.getType().toUpperCase() );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    public static class ListHolder {
        private final List<String> list;

        public ListHolder( List<String> list ) {
            this.list = list;
        }

        public List<String> getList() {
            return list;
        }
    }
}
