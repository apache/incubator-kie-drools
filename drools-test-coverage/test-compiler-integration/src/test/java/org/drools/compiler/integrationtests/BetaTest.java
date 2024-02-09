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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.FirstClass;
import org.drools.testcoverage.common.model.SecondClass;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class BetaTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BetaTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDefaultBetaConstrains() {

        final String drl = "package org.drools.compiler\n" +
                "import " + FirstClass.class.getCanonicalName() + "\n" +
                "import " + SecondClass.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"5+ constraints with not\"\n" +
                "    when\n" +
                "        FirstClass($i1 : item1, $i2 : item2, $i3 : item3, $i4 : item4, $i5 : item5)\n" +
                "        not SecondClass( item1 == $i1, item2 == $i2, item3 == $i3, item4 == $i4, item5 == $i5 )\n" +
                "    then\n" +
                "        results.add( \"NOT\" );\n" +
                "end\n" +
                "\n" +
                "rule \"5+ constraints with pattern\"\n" +
                "    when\n" +
                "        FirstClass($i1 : item1, $i2 : item2, $i3 : item3, $i4 : item4, $i5 : item5)\n" +
                "        SecondClass( item1 == $i1, item2 == $i2, item3 == $i3, item4 == $i4, item5 == $i5 )\n" +
                "    then\n" +
                "        results.add( \"EQUALS\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("beta-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);
            final FirstClass first = new FirstClass("1", "2", "3", "4", "5");
            final FactHandle handle = ksession.insert(first);
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo("NOT");

            ksession.insert(new SecondClass());
            ksession.update(handle, first);
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(1)).isEqualTo("NOT");

            ksession.update(handle, first);
            ksession.insert(new SecondClass(null, "2", "3", "4", "5"));
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(3);
            assertThat(results.get(2)).isEqualTo("NOT");

            ksession.update(handle, first);
            ksession.insert(new SecondClass("1", null, "3", "4", "5"));
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(4);
            assertThat(results.get(3)).isEqualTo("NOT");

            ksession.update(handle, first);
            ksession.insert(new SecondClass("1", "2", null, "4", "5"));
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(5);
            assertThat(results.get(4)).isEqualTo("NOT");

            ksession.update(handle, first);
            ksession.insert(new SecondClass("1", "2", "3", null, "5"));
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(6);
            assertThat(results.get(5)).isEqualTo("NOT");

            ksession.update(handle, first);
            ksession.insert(new SecondClass("1", "2", "3", "4", null));
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(7);
            assertThat(results.get(6)).isEqualTo("NOT");

            ksession.insert(new SecondClass("1", "2", "3", "4", "5"));
            ksession.update(handle, first);
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(8);
            assertThat(results.get(7)).isEqualTo("EQUALS");
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 5000)
    public void testEfficientBetaNodeNetworkUpdate() {
        // [JBRULES-3372]
        final String drl =
                "declare SimpleMembership\n" +
                        "    listId : String\n" +
                        "    patientId : String\n" +
                        "end\n" +
                        "declare SimplePatientFact\n" +
                        "    value : int\n" +
                        "    patientId : String\n" +
                        "end\n" +
                        "rule \"A\"\n" +
                        "when\n" +
                        "$slm : SimpleMembership($pid : patientId, listId == \"5072\" )\n" +
                        "and not (\n" +
                        "    (\n" +
                        "        (\n" +
                        "            SimplePatientFact(value == 1, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 2, patientId == $pid)\n" +
                        "        )\n" +
                        "    ) and (\n" +
                        "        (\n" +
                        "            SimplePatientFact(value == 6, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 7, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 8, patientId == $pid)\n" +
                        "        )\n" +
                        "    ) and (\n" +
                        "       (\n" +
                        "           SimplePatientFact(value == 9, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 10, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 11, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 12, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 13, patientId == $pid)\n" +
                        "       )\n" +
                        "   )\n" +
                        ")\n" +
                        "then\n" +
                        "   System.out.println(\"activated\");\n" +
                        "end";

        KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, drl);
    }
}
