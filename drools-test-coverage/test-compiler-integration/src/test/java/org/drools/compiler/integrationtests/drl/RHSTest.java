/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RHSTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RHSTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testGenericsInRHS() {

        final String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import java.util.Map;\n" +
            "import java.util.HashMap;\n" +
            "rule \"Test Rule\"\n" +
            "  when\n" +
            "  then\n" +
            "    Map<String,String> map = new HashMap<String,String>();\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        ksession.dispose();
    }

    @Test
    public void testRHSClone() {
        // JBRULES-3539
        final String drl = "import java.util.Map;\n" +
                "dialect \"mvel\"\n" +
                "rule \"RHSClone\"\n" +
                "when\n" +
                "   Map($valOne : this['keyOne'] !=null)\n" +
                "then\n" +
                "   System.out.println( $valOne.clone() );\n" +
                "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testIncrementOperator() {
        final String drl =
            "package org.drools.compiler.integrationtest.drl \n" +
            "global java.util.List list \n" +
            "rule rule1 \n" +
            "    dialect \"java\" \n" +
            "when \n" +
            "    $I : Integer() \n" +
            "then \n" +
            "    int i = $I.intValue(); \n" +
            "    i += 5; \n" +
            "    list.add( i ); \n" +
            "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert(5);
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(10, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testKnowledgeHelperFixerInStrings() {
        final String drl =
            "package org.drools.compiler.integrationtests.drl; \n" +
            "global java.util.List list \n" +
            "rule xxx \n" +
            "  no-loop true " +
            "when \n" +
            "  $fact : String() \n" +
            "then \n" +
            "  list.add(\"This is an update()\"); \n" +
            "  list.add(\"This is an update($fact)\"); \n" +
            "  update($fact); \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert("hello");
            ksession.fireAllRules();
            assertEquals(2, list.size());
            assertEquals("This is an update()", list.get(0));
            assertEquals("This is an update($fact)", list.get(1));
        } finally {
            ksession.dispose();
        }
    }
}
