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

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.common.InternalAgenda;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class RuleFlowGroupTest extends CommonTestMethodBase {

    @Test
    public void testRuleFlowGroupWithLockOnActivate() {
        // JBRULES-3590
        final String str = "import org.drools.compiler.Person;\n" +
                "import org.drools.compiler.Cheese;\n" +
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

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person());
        ksession.insert(new Cheese("gorgonzola"));
        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

}
