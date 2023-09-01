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
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a place where minor edge cases which fail with exec-model can be temporarily moved from test-compiler-integration test classes.
 * When fixed, you should move them back to the original test class (or remove @Ignore from the test method).
 */
@RunWith(Parameterized.class)
public class EdgeCaseNonExecModelTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EdgeCaseNonExecModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    // Moved from NamedConsequencesTest
    // DROOLS-6290
    @Test
    public void testNamedConsequencesInsideOR1() {
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

        List<String> results = executeTestWithDRL(str);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    // Moved from NamedConsequencesTest
    // DROOLS-6290
    @Test
    public void testNamedConsequencesInsideOR2() {
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

        List<String> results = executeTestWithDRL(str);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    private List<String> executeTestWithDRL(String drl) {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();
        return results;
    }
}
