package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.Cat;
import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.Person;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class AlphaNetworkModifyTest extends CommonTestMethodBase {
    
    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, String nodeName) {
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
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
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        
        ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
        ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );
        ObjectTypeNode otnCat = getObjectTypeNode(kbase, "Cat" );

        assertEquals( 0, otnPerson.getOtnIdCounter() );
        assertEquals( 0, otnCheese.getOtnIdCounter() );
        assertEquals( 0, otnCat.getOtnIdCounter() );
        wm.insert( new Person() );
        wm.insert( new Cat("yyy") );
        wm.insert( new Cheese() );
        
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
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        wm.fireAllRules();
        
        
        ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getSinkPropagator().getSinks()[0];
        
        LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();
        
        assertEquals(0, sinks[0].getLeftInputOtnId() );
        assertEquals(1, sinks[1].getLeftInputOtnId() );
        assertEquals(2, sinks[2].getLeftInputOtnId() );
        assertEquals(3, sinks[3].getLeftInputOtnId() );
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
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        wm.fireAllRules();
        
        
        ObjectTypeNode otnInit = getObjectTypeNode(kbase, "InitialFactImpl" );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otnInit.getSinkPropagator().getSinks()[0];
        
        LeftTupleSink[] sinks = liaNode.getSinkPropagator().getSinks();
        
        assertEquals(0, sinks[0].getLeftInputOtnId() );
        assertEquals(1, sinks[1].getLeftInputOtnId() );
        assertEquals(2, sinks[2].getLeftInputOtnId() );
        assertEquals(3, sinks[3].getLeftInputOtnId() );
        
        ObjectTypeNode otnPerson = getObjectTypeNode(kbase, "Person" );
        ObjectTypeNode otnCheese = getObjectTypeNode(kbase, "Cheese" );

        assertEquals( 0, otnPerson.getOtnIdCounter() );
        assertEquals( 0, otnCheese.getOtnIdCounter() );
        wm.insert( new Person() );
        wm.insert( new Cheese() );
        
        assertEquals( 5, otnPerson.getOtnIdCounter() );
        assertEquals( 4, otnCheese.getOtnIdCounter() );        
    }       

}
