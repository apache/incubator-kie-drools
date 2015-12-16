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

import org.drools.compiler.Cat;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.List;

public class AlphaNetworkModifyTest extends CommonTestMethodBase {
    
    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, String nodeName) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType().getSimpleName().equals( nodeName ) ) {
                return n;
            }
        }
        return null;
    }
    
    @Test
    public void testModifyWithLiaToEval() {
        String str = "";
        str += "package org.simple \n";
        str += "import " + Person.class.getCanonicalName() + "\n";
        str += "import " + Cheese.class.getCanonicalName() + "\n";
        str += "import " + Cat.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    $pe : Person() \n";
        str += "    $ch : Cheese() \n";
        str += "    $ca : Cat() \n";    
        str += "then \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $ch : Cheese() \n";
        str += "    $ca : Cat() \n";           
        str += "    $pe : Person() \n"; 
        str += "then \n";
        str += "end  \n";        
        str += "rule x3 \n";
        str += "when \n";
        str += "    $ch : Cheese() \n"; 
        str += "then \n";
        str += "end  \n";        
        str += "rule x4 \n";
        str += "when \n";
        str += "    $ch : Cheese() \n";
        str += "    eval( $ch != null ) \n";
        str += "then \n";
        str += "end  \n";         
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        
        ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
        ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );
        ObjectTypeNode otnCat = getObjectTypeNode(kbase, "Cat" );

        assertEquals( 0, otnPerson.getOtnIdCounter() );
        assertEquals( 0, otnCheese.getOtnIdCounter() );
        assertEquals( 0, otnCat.getOtnIdCounter() );
        wm.insert( new Person() );
        wm.insert( new Cat("yyy") );
        wm.insert( new Cheese() );
        wm.fireAllRules();

        assertEquals( 2, otnPerson.getOtnIdCounter() );
        assertEquals( 4, otnCheese.getOtnIdCounter() );
        assertEquals( 2, otnCat.getOtnIdCounter() );
    }    
    
    @Test
    public void testModifyWithLiaToFrom() {
        // technically you can't have a modify with InitialFactImpl
        // But added test for completeness
        
        String str = "";
        str += "package org.simple \n";
        str += "import " + Person.class.getCanonicalName() + "\n";
        str += "import " + Cheese.class.getCanonicalName() + "\n";
        str += "import " + Cat.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    $pe : Person() from list\n";  
        str += "then \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $ch : Cheese() from list\n";
        str += "then \n";
        str += "end  \n";        
        str += "rule x3 \n";
        str += "when \n";
        str += "    $ch : Cheese() from list\n"; 
        str += "then \n";
        str += "end  \n";        
        str += "rule x4 \n";
        str += "when \n";
        str += "    $ch : Cheese() from list\n";
        str += "    eval( $ch != null ) \n";
        str += "then \n";
        str += "end  \n";         
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        wm.fireAllRules();

        ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getSinkPropagator().getSinks()[0];
        
        LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();
        
        assertEquals(0, sinks[0].getLeftInputOtnId().getId() );
        assertEquals(1, sinks[1].getLeftInputOtnId().getId() );
        assertEquals(2, sinks[2].getLeftInputOtnId().getId() );
        assertEquals(3, sinks[3].getLeftInputOtnId().getId() );
    }        
    
    @Test
    public void testModifyWithLiaToAcc() {
        // technically you can't have a modify with InitialFactImpl
        // But added test for completeness
        
        String str = "";
        str += "package org.simple \n";
        str += "import " + Person.class.getCanonicalName() + "\n";
        str += "import " + Cheese.class.getCanonicalName() + "\n";
        str += "import " + Cat.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    Object() from accumulate( $p : Person() and Cheese(), collectList( $p ) )\n";  
        str += "    Person() \n";        
        str += "then \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n";
        str += "    Person() \n";        
        str += "then \n";
        str += "end  \n";        
        str += "rule x3 \n";
        str += "when \n";
        str += "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n";
        str += "    Person() \n";        
        str += "then \n";
        str += "end  \n";        
        str += "rule x4 \n";
        str += "when \n";
        str += "    Object() from accumulate( $ch : Cheese(), collectList( $ch ) )\n";
        str += "    Person() \n";
        str += "then \n";
        str += "end  \n";         
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        wm.fireAllRules();
        
        
        ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getSinkPropagator().getSinks()[0];
        
        LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();
        
        assertEquals(0, sinks[0].getLeftInputOtnId().getId() );
        assertEquals(1, sinks[1].getLeftInputOtnId().getId() );
        assertEquals(2, sinks[2].getLeftInputOtnId().getId() );
        assertEquals(3, sinks[3].getLeftInputOtnId().getId() );
        
        ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
        ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );

        assertEquals( 0, otnPerson.getOtnIdCounter() );
        assertEquals( 0, otnCheese.getOtnIdCounter() );
        wm.insert( new Person() );
        wm.insert( new Cheese() );
        wm.fireAllRules();

        assertEquals( 5, otnPerson.getOtnIdCounter() );
        assertEquals( 4, otnCheese.getOtnIdCounter() );
    }       

}
