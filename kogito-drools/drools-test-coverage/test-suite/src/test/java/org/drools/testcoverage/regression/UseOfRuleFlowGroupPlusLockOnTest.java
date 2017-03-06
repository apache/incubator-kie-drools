/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.common.DefaultAgenda;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import java.io.StringReader;

/**
 * Tests if ruleflow-group + lock-on-activate causes a ClassCastException.
 * bz845000
 */
public class UseOfRuleFlowGroupPlusLockOnTest {

    private static final String DRL =
            "package org.drools.testcoverage.regression\n" +
            "import org.drools.testcoverage.common.model.Person\n" +
            "import org.drools.testcoverage.common.model.Cheese\n" +
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
            "end\n";

    @Test
    public void test() {
        final Resource resource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
                       resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromResources(true, resource);
        final KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person());
        ksession.insert(new Cheese("eidam"));
        ((DefaultAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");

        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }
}
