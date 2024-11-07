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

import java.util.stream.Stream;

import org.drools.core.common.InternalAgenda;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleFlowGroupTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testRuleFlowGroupWithLockOnActivate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-3590
        final String drl = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "ruleflow-group \"group1\"\n" +
                "lock-on-active true\n" +
                "when\n" +
                "   $p : Person()\n" +
                "then\n" +
                "   $p.setName(\"John\");\n" +
                "   update ($p);\n" +
                "end\n" +
                "rule R2\n" +
                "ruleflow-group \"group1\"\n" +
                "lock-on-active true\n" +
                "when\n" +
                "   $p : Person( name == null )\n" +
                "   forall ( Cheese ( type == \"cheddar\" ))\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("ruleflowgroup-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person());
            ksession.insert(new Cheese("gorgonzola"));
            ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");
            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

}
