/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Pet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class AlphaNetworkModifyTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaNetworkModifyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    private ObjectTypeNode getObjectTypeNode(final KieBase kbase, final String nodeName) {
        final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( final ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType().getSimpleName().equals( nodeName ) ) {
                return n;
            }
        }
        return null;
    }
    
    @Test
    public void testModifyWithLiaToEval() {
        final String drl =
            "package org.simple \n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import " + Cheese.class.getCanonicalName() + "\n" +
            "import " + Pet.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule x1 \n" +
            "when \n" +
            "    $pe : Person() \n" +
            "    $ch : Cheese() \n" +
            "    $ca : Pet() \n" +
            "then \n" +
            "end  \n" +
            "rule x2 \n" +
            "when \n" +
            "    $ch : Cheese() \n" +
            "    $ca : Pet() \n" +
            "    $pe : Person() \n" +
            "then \n" +
            "end  \n" +
            "rule x3 \n" +
            "when \n" +
            "    $ch : Cheese() \n" +
            "then \n" +
            "end  \n" +
            "rule x4 \n" +
            "when \n" +
            "    $ch : Cheese() \n" +
            "    eval( $ch != null ) \n" +
            "then \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-network-modify-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
            final ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );
            final ObjectTypeNode otnPet = getObjectTypeNode(kbase, "Pet" );

            assertNotNull(otnPerson);
            assertNotNull(otnCheese);
            assertNotNull(otnPet);

            assertEquals( 0, otnPerson.getOtnIdCounter() );
            assertEquals( 0, otnCheese.getOtnIdCounter() );
            assertEquals( 0, otnPet.getOtnIdCounter() );
            wm.insert( new Person() );
            wm.insert( new Pet("yyy") );
            wm.insert( new Cheese() );
            wm.fireAllRules();

            assertEquals( 2, otnPerson.getOtnIdCounter() );
            assertEquals( 4, otnCheese.getOtnIdCounter() );
            assertEquals( 2, otnPet.getOtnIdCounter() );
        } finally {
            wm.dispose();
        }
    }    
    
    @Test
    public void testModifyWithLiaToFrom() {
        // technically you can't have a modify with InitialFactImpl
        // But added test for completeness
        
        final String drl =
            "package org.simple \n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import " + Cheese.class.getCanonicalName() + "\n" +
            "import " + Pet.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule x1 \n" +
            "when \n" +
            "    $pe : Person() from list\n" +
            "then \n" +
            "end  \n" +
            "rule x2 \n" +
            "when \n" +
            "    $ch : Cheese() from list\n" +
            "then \n" +
            "end  \n" +
            "rule x3 \n" +
            "when \n" +
            "    $ch : Cheese() from list\n" +
            "then \n" +
            "end  \n" +
            "rule x4 \n" +
            "when \n" +
            "    $ch : Cheese() from list\n" +
            "    eval( $ch != null ) \n" +
            "then \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-network-modify-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            wm.fireAllRules();

            final ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
            assertNotNull(otnInit);
            final LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getObjectSinkPropagator().getSinks()[0];

            final LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();

            assertEquals(2, sinks.length );
            assertEquals(0, sinks[0].getLeftInputOtnId().getId() );
            assertEquals(1, sinks[1].getLeftInputOtnId().getId() );
        } finally {
            wm.dispose();
        }
    }
    
    @Test
    public void testModifyWithLiaToAcc() {
        // technically you can't have a modify with InitialFactImpl
        // But added test for completeness
        
        final String drl =
            "package org.simple \n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import " + Cheese.class.getCanonicalName() + "\n" +
            "import " + Pet.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule x1 \n" +
            "when \n" +
            "    Object() from accumulate( $p : Person() and Cheese(), collectList( $p ) )\n" +
            "    Person() \n" +
            "then \n" +
            "end  \n" +
            "rule x2 \n" +
            "when \n" +
            "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n" +
            "    Person() \n" +
            "then \n" +
            "end  \n" +
            "rule x3 \n" +
            "when \n" +
            "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n" +
            "    Person() \n" +
            "then \n" +
            "end  \n" +
            "rule x4 \n" +
            "when \n" +
            "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n" +
            "    Person() \n" +
            "then \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-network-modify-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            wm.fireAllRules();


            final ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
            assertNotNull(otnInit);
            final LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getObjectSinkPropagator().getSinks()[0];

            final LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();

            assertEquals(0, sinks[0].getLeftInputOtnId().getId() );
            assertEquals(1, sinks[1].getLeftInputOtnId().getId() );
            assertEquals(2, sinks[2].getLeftInputOtnId().getId() );

            final ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
            final ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );
            assertNotNull(otnPerson);
            assertNotNull(otnCheese);
            assertEquals( 0, otnPerson.getOtnIdCounter() );
            assertEquals( 0, otnCheese.getOtnIdCounter() );
            wm.insert( new Person() );
            wm.insert( new Cheese() );
            wm.fireAllRules();

            assertEquals( 3, otnPerson.getOtnIdCounter() );
            assertEquals( 2, otnCheese.getOtnIdCounter() );
        } finally {
            wm.dispose();
        }
    }       

}
