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

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(Parameterized.class)
public class AddRuleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AddRuleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMemoriesCCEWhenAddRemoveAddRule() {
        // JBRULES-3656
        final String rule1 = "import " + AddRuleTest.class.getCanonicalName() + ".*\n" +
                "import java.util.Date\n" +
                "rule \"RTR - 28717 retract\"\n" +
                "when\n" +
                "        $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId,\n" +
                "        ( listId != null && listId == \"28717\" ) ) and not ($patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null &&\n" +
                "        spaceId == $listMembershipPatientSpaceIdRoot ) and\n" +
                "        (($ruleTime0 : RuleTime( $ruleTimeStartOfDay4_1 : startOfDay, $ruleTimeTime4_1 : time ) and $patient1 :\n" +
                "        SimplePatient( spaceId != null && spaceId == $patientSpaceIdRoot, birthDate != null && (birthDate after[0s,1d] $ruleTimeStartOfDay4_1) ) ) ) )\n" +
                "then\n" +
                "end";

        final String rule2 = "import " + AddRuleTest.class.getCanonicalName() + ".*\n" +
                "import java.util.Date\n" +
                "rule \"RTR - 28717 retract\"\n" +
                "when  $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId, ( listId != null && listId == \"28717\" ) )\n" +
                "    and not ($patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null && spaceId == $listMembershipPatientSpaceIdRoot )\n" +
                "    and ( ($ruleTime0 : RuleTime( $ruleTimeStartOfDay4_1 : startOfDay, $ruleTimeTime4_1 : time )\n" +
                "    and $patient1 : SimplePatient( spaceId != null && spaceId == $patientSpaceIdRoot, birthDate != null && (birthDate not after[0s,1d] $ruleTimeStartOfDay4_1) ) ) ) )\n" +
                "then\n" +
                "end";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-memories-cce-when-add-remove-rule", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieContainer.getKieBase();

        kbase.newKieSession();
        kbase.addPackages(TestUtil.createKnowledgeBuilder(null, rule1).getKnowledgePackages());
        kbase.addPackages(TestUtil.createKnowledgeBuilder(null, rule2).getKnowledgePackages());
    }

    @Test
    public void testAddRuleWithFrom() {
        // JBRULES-3499
        final String str1 = "global java.util.List names;\n" +
                "global java.util.List list;\n";

        final String str2 = "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List names;\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "   $p : Person( )\n" +
                "   String( this == $p.name ) from names\n" +
                "then\n" +
                " list.add( $p );\n" +
                "end";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-add-rule-with-from", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, str1);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieContainer.getKieBase();
        final KieSession ksession = kbase.newKieSession();

        final List<String> names = new ArrayList<>();
        names.add("Mark");
        ksession.setGlobal("names", names);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Person p = new Person("Mark");
        ksession.insert(p);

        ksession.fireAllRules();

        kbase.addPackages(TestUtil.createKnowledgeBuilder(null, str2).getKnowledgePackages());

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertSame(p, list.get(0));
        ksession.dispose();
    }

    @Test
    public void testDynamicallyAddInitialFactRule() {
        String rule = "package org.drools.compiler.test\n" +
                "global java.util.List list\n" +
                "rule xxx when\n" +
                "   i:Integer()\n" +
                "then\n" +
                "   list.add(i);\n" +
                "end";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-add-dynamically-init-fact-rule", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, rule);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieContainer.getKieBase();
        final KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);

        session.insert(5);
        session.fireAllRules();

        assertEquals(5, list.get(0));

        rule = "package org.drools.compiler.test\n" +
                "global java.util.List list\n" +
                "rule xxx when\n" +
                "then\n" +
                "   list.add(\"x\");\n" +
                "end";
        final Collection<KiePackage> kpkgs = TestUtil.createKnowledgeBuilder(null, rule).getKnowledgePackages();
        kbase.addPackages(kpkgs);

        session.fireAllRules();

        assertEquals("x", list.get(1));
    }

    public static class RuleTime {
        public Date getTime() {
            return new Date();
        }

        public Date getStartOfDay() {
            return new Date();
        }
    }

    public static class SimpleMembership {
        public String getListId() {
            return "";
        }

        public String getPatientSpaceId() {
            return "";
        }
    }

    public class SimplePatient {
        public String getSpaceId() {
            return "";
        }

        public String getFactHandleString() {
            return "";
        }

        public Date getBirthDate() {
            return new Date();
        }
    }
}
