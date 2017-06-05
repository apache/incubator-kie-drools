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
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.definition.KnowledgePackage;

public class RemoveRuleTest extends CommonTestMethodBase {

    @Test
    public void testRemoveBigRule() throws Exception {
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

        final Collection<KnowledgePackage> kpgs = loadKnowledgePackagesFromString( str );

        Assert.assertEquals(1, kpgs.size());

        final KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( kpgs );

        kbase.removeKnowledgePackage( kpgs.iterator().next().getName() );

        final EntryPointNode epn = ( (InternalKnowledgeBase) kbase ).getRete().getEntryPointNodes().values().iterator().next();
        for (final ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            final ObjectSink[] sinks = otn.getObjectSinkPropagator().getSinks();
            if (sinks.length > 0) {
                fail( otn + " has sinks " + Arrays.toString( sinks ) );
            }
        }
    }

    @Test
    public void testRemoveRuleWithFromNode() throws Exception {
        // JBRULES-3631
        final String str =
                "package org.drools.compiler;\n" +
                        "import org.drools.compiler.*;\n" +
                        "rule R1 when\n" +
                        "   not( Person( name == \"Mark\" ));\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "   $p: Person( name == \"Mark\" );\n" +
                        "   not( Address() from $p.getAddresses() );\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        assertEquals(2, kbase.getKiePackage("org.drools.compiler").getRules().size());
        kbase.removeRule( "org.drools.compiler", "R2" );

        assertEquals( 1, kbase.getKiePackage( "org.drools.compiler" ).getRules().size() );
    }

    @Test
    public void testRuleRemovalWithJoinedRootPattern() {
        String str = "";
        str += "package org.drools.compiler \n";
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

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
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
}
