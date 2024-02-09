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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.PersonWithSpecificEquals;
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
public class EqualsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EqualsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEqualitySupport() {

        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + PersonWithSpecificEquals.class.getCanonicalName() + " ;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test equality support\"\n" +
                "    salience 10\n" +
                "when\n" +
                "    $p : PersonWithSpecificEquals( name == \"bob\" )\n" +
                "then\n" +
                "    $p.setName( \"mark\" );\n" +
                "    results.add( $p.getName() );\n" +
                "    update( $p );\n" +
                "end\n" +
                "\n" +
                "rule \"test 2\"\n" +
                "when\n" +
                "    $p : PersonWithSpecificEquals( name == \"bob\" )\n" +
                "then\n" +
                "    results.add( \"This rule should NEVER fire\" );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("equals-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final PersonWithSpecificEquals person = new PersonWithSpecificEquals("bob", 30);
            ksession.insert(person);
            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo("mark");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNotEqualsOperator() {
        // JBRULES-3003: restriction evaluation returns 'false' for "trueField != falseField"

        final String str = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + Primitives.class.getCanonicalName() + " ;\n" +
                "rule NotEquals\n" +
                "when\n" +
                "    Primitives( booleanPrimitive != booleanWrapper )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("equals-test",
                                                                         kieBaseTestConfiguration,
                                                                         str);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Primitives p = new Primitives();
            p.setBooleanPrimitive(true);
            p.setBooleanWrapper(Boolean.FALSE);

            ksession.insert(p);

            final int rules = ksession.fireAllRules();
            ksession.dispose();

            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testCharComparisons() {

        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + Primitives.class.getCanonicalName() + " ;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test chars 1\"\n" +
                "    salience 100\n" +
                "when\n" +
                "    Primitives( charPrimitive == 'a' ) \n" +
                "then\n" +
                "    results.add( \"1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"test chars 2\"\n" +
                "    salience 90\n" +
                "when\n" +
                "    Primitives( $c1: charPrimitive == 'a' ) \n" +
                "    Primitives( charPrimitive != $c1 )\n" +
                "then\n" +
                "    results.add( \"2\" );\n" +
                "end\n" +
                "\n" +
                "rule \"test chars 3\"\n" +
                "    salience 80\n" +
                "when\n" +
                "    Primitives( $c1: stringAttribute == 'a' ) \n" +
                "    Primitives( charPrimitive == $c1 )\n" +
                "then\n" +
                "    results.add( \"3\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("equals-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Primitives p1 = new Primitives();
            p1.setCharPrimitive('a');
            p1.setStringAttribute("b");
            final Primitives p2 = new Primitives();
            p2.setCharPrimitive('b');
            p2.setStringAttribute("a");

            ksession.insert(p1);
            ksession.insert(p2);

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(3);
            assertThat(results.get(0)).isEqualTo("1");
            assertThat(results.get(1)).isEqualTo("2");
            assertThat(results.get(2)).isEqualTo("3");
        } finally {
            ksession.dispose();
        }
    }
}
