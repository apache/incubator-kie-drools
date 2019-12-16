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

import java.util.Arrays;
import java.util.Collection;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class RemoveRuleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RemoveRuleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testRemoveBigRule() {
        // JBRULES-3496
        final String str =
                "package org.drools.compiler.test\n" +
                        "\n" +
                        "declare SimpleFact\n" +
                        "   patientSpaceId : String\n" +
                        "   block : int\n" +
                        "end\n" +
                        "\n" +
                        "declare SimpleMembership\n" +
                        "   patientSpaceId : String\n" +
                        "   listId : String\n" +
                        "end\n" +
                        "\n" +
                        "declare SimplePatient\n" +
                        "   spaceId : String\n" +
                        "end\n" +
                        "\n" +
                        "rule \"RTR - 47146 retract\"\n" +
                        "agenda-group \"list membership\"\n" +
                        "when\n" +
                        "   $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId, ( listId != null && listId == \"47146\" ) )\n" +
                        "   not ( $patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null && spaceId == $listMembershipPatientSpaceIdRoot ) \n" +
                        "       and ( ( " +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 1 )\n" +
                        "         ) or ( " +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 2 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId  == $patientSpaceIdRoot, block == 3 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 4 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 5 )\n" +
                        "       ) ) and ( ( " +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 6 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 7 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 8 )\n" +
                        "       ) ) and ( ( " +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 9 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 10 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 11 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 12 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 13 )\n" +
                        "         ) or ( (" +
                        "            SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 14 )\n" +
                        "           ) and (" +
                        "              SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 15 )\n" +
                        "         ) ) or ( ( " +
                        "            SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 16 )\n" +
                        "           ) and ( " +
                        "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 17 )\n" +
                        "         ) ) or ( ( " +
                        "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 18 )\n" +
                        "           ) and (" +
                        "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 19 )\n" +
                        "         ) ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 20 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 21 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 22 )\n" +
                        "         ) or ( ( " +
                        "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 23 )\n" +
                        "         ) and (" +
                        "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 24 )\n" +
                        "     ) ) ) and ( ( " +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 25 )\n" +
                        "         ) or (" +
                        "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 26 )\n" +
                        "     ) ) )\n" +
                        "then\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = KieServices.get().newReleaseId("org.kie", "test-remove-big-rule", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, str);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) kc.getKieBase();

        final ReleaseId releaseId2 = KieServices.get().newReleaseId("org.kie", "test-remove-big-rule", "1.1");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration);

        kc.updateToVersion(releaseId2);

        final EntryPointNode epn = kbase.getRete().getEntryPointNodes().values().iterator().next();
        for (final ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            final ObjectSink[] sinks = otn.getObjectSinkPropagator().getSinks();
            if (sinks.length > 0) {
                fail( otn + " has sinks " + Arrays.toString( sinks ) );
            }
        }
    }

    @Test
    public void testRemoveRuleWithFromNode() {
        // JBRULES-3631
        final String str =
                "package org.drools.compiler;\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Address.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "   not( Person( name == \"Mark\" ));\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "   $p: Person( name == \"Mark\" );\n" +
                        "   not( Address() from $p.getAddresses() );\n" +
                        "then\n" +
                        "end\n";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-remove-rule-with-from-node", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, str);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieBase kbase = kieContainer.getKieBase();
        assertEquals(2, kbase.getKiePackage("org.drools.compiler").getRules().size());
        kbase.removeRule( "org.drools.compiler", "R2" );

        assertEquals( 1, kbase.getKiePackage( "org.drools.compiler" ).getRules().size() );
    }

    @Test
    public void testRuleRemovalWithJoinedRootPattern() {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Person.class.getCanonicalName() + ";\n";
        str += "import " + Cheese.class.getCanonicalName() + ";\n";
        str += "rule rule1 \n";
        str += "when \n";
        str += "  String() \n";
        str += "  Person() \n";
        str += "then \n";
        str += "end  \n";
        str += "rule rule2 \n";
        str += "when \n";
        str += "  String() \n";
        str += "  Cheese() \n";
        str += "then \n";
        str += "end  \n";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-remove-rule-with-joined-root-pattern", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, str);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        final KieBase kbase = kieContainer.getKieBase();
        final KieSession ksession = kbase.newKieSession();
        final DefaultFactHandle handle = (DefaultFactHandle) ksession.insert("hello");
        ksession.fireAllRules();
        LeftTuple leftTuple = handle.getFirstLeftTuple();
        assertNotNull(leftTuple);
        assertNotNull(leftTuple.getPeer());
        kbase.removeRule("org.drools.compiler", "rule2");
        leftTuple = handle.getFirstLeftTuple();
        assertNotNull(leftTuple);
        assertNull(leftTuple.getHandleNext());
    }

     @Test
    public void testRemoveAccumulateRule() {
        // DROOLS-4864
        final String str =
                "package org.drools.compiler.test\n" +
                "\n" +
                "rule Acc no-loop\n" +
                "when\n" +
                "    accumulate(\n" +
                "            String( $l : length, this == \"test\" );\n" +
                "            $max : max( $l ))\n" +
                "then end\n";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = KieServices.get().newReleaseId("org.kie", "test-remove-acc-rule", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, str);

        final KieContainer kc = ks.newKieContainer(releaseId1);
        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) kc.getKieBase();

        KieSession ksession = kbase.newKieSession();
        ksession.insert("xxx");
        final ReleaseId releaseId2 = KieServices.get().newReleaseId("org.kie", "test-remove-acc-rule", "1.1");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration);

        kc.updateToVersion(releaseId2);

        final EntryPointNode epn = kbase.getRete().getEntryPointNodes().values().iterator().next();
        for (final ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            final ObjectSink[] sinks = otn.getObjectSinkPropagator().getSinks();
            if (sinks.length > 0) {
                fail( otn + " has sinks " + Arrays.toString( sinks ) );
            }
        }
    }
}
