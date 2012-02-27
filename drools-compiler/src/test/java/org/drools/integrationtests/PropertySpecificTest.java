package org.drools.integrationtests;

import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.InitialFact;
import org.drools.KnowledgeBase;
import org.drools.Person;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.PropertySpecificOption;
import org.drools.common.InternalRuleBase;
import org.drools.definition.type.FactType;
import org.drools.definition.type.Modifies;
import org.drools.definition.type.PropertySpecific;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.PropertySpecificUtil;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;
import static org.drools.reteoo.PropertySpecificUtil.calculateMaskFromPattern;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertySpecificTest extends CommonTestMethodBase {
    
    @Test
    public void testRTNodeEmptyLHS() {
        String rule = "package org.drools\n" +
                      "rule r1\n" +
                        "when\n" +
                        "then\n" +
                        "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "InitialFactImpl" );
        assertNotNull( otn );

        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, rtNode.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, rtNode.getInferredMask() );
    }   
    
    @Test
    public void testRTNodeNoConstraintsNoPropertySpecific() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person()\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person" );
        assertNotNull( otn );

        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, rtNode.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, rtNode.getInferredMask() );
    }   
    
    @Test
    public void testRTNodeWithConstraintsNoPropertySpecific() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person( name == 'bobba')\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person" );
        assertNotNull( otn );

        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, alphaNode.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, alphaNode.getInferredMask() );        
        
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, rtNode.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, rtNode.getInferredMask() );
    }  
    
    @Test
    public void testBetaNodeNoConstraintsNoPropertySpecific() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "import " + Cheese.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person()\n" +
                      "   Cheese()\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese" );
        assertNotNull( otn );

        BetaNode betaNode = ( BetaNode ) otn.getSinkPropagator().getSinks()[0];
        
        assertEquals( Long.MAX_VALUE, betaNode.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightInferredMask() );
    }    
    
    @Test
    public void testBetaNodeWithConstraintsNoPropertySpecific() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "import " + Cheese.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person()\n" +
                      "   Cheese( type == 'brie' )\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese" );
        assertNotNull( otn );

        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, alphaNode.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, alphaNode.getInferredMask() );
        
        BetaNode betaNode = ( BetaNode ) alphaNode.getSinkPropagator().getSinks()[0]; 
        
        assertEquals( Long.MAX_VALUE, betaNode.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightInferredMask() );
    }  
    
    @Test
    public void testInitialFactBetaNodeWithRightInputAdapter() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "import " + Cheese.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   exists(eval(1==1))\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "InitialFactImpl" );
        assertNotNull( otn );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        BetaNode betaNode = ( BetaNode ) liaNode.getSinkPropagator().getSinks()[1];
        
        assertEquals( Long.MAX_VALUE, betaNode.getLeftDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getLeftInferredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightInferredMask() );                
    }  
    
    @Test
    public void testPersonFactBetaNodeWithRightInputAdapter() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "import " + Cheese.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person()\n" + 
                      "   exists(eval(1==1))\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person" );
        assertNotNull( otn );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        BetaNode betaNode = ( BetaNode ) liaNode.getSinkPropagator().getSinks()[1];
        
        assertEquals( Long.MAX_VALUE, betaNode.getLeftDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getLeftInferredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode.getRightInferredMask() );                
    }    
    
    @Test
    public void testSharedAlphanodeWithBetaNodeConstraintsNoPropertySpecific() {
        String rule = "package org.drools\n" +
                      "import " + Person.class.getCanonicalName() + "\n" +
                      "import " + Cheese.class.getCanonicalName() + "\n" +
                      "rule r1\n" +
                      "when\n" +
                      "   Person()\n" +
                      "   Cheese( type == 'brie', price == 1.5 )\n" +
                      "then\n" +
                      "end\n"+
                      "rule r2\n" +
                      "when\n" +
                      "   Person()\n" +
                      "   Cheese( type == 'brie', price == 2.5 )\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese" );
        assertNotNull( otn );

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, alphaNode1.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, alphaNode1.getInferredMask() );
        
        
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( Long.MAX_VALUE, alphaNode1_1.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode ) alphaNode1_1.getSinkPropagator().getSinks()[0]; 
        
        assertEquals( Long.MAX_VALUE, betaNode1.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode1.getRightInferredMask() );
        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( Long.MAX_VALUE, alphaNode1_2.getDeclaredMask() );
        assertEquals( Long.MAX_VALUE, alphaNode1_2.getInferredMask() );  
        
        BetaNode betaNode2 = ( BetaNode ) alphaNode1_2.getSinkPropagator().getSinks()[0]; 
        
        assertEquals( Long.MAX_VALUE, betaNode2.getRightDeclaredMask() );
        assertEquals( Long.MAX_VALUE, betaNode2.getRightInferredMask() );
    }       
    

    private KnowledgeBase getKnowledgeBase(String... rules) {
        String rule = "package org.drools\n" +
                "global java.util.List list;\n" +
                "declare A\n" +
                "    @propertySpecific\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    c : int\n" +
                "    s : String\n" +
                "    i : int\n" +
                "    j : int\n" +
                "    k : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    c : int\n" +
                "    s : String\n" +
                "    i : int\n" +
                "    j : int\n" +
                "    k : int\n" +
                "end\n";
        int i = 0;
        for ( String str : rules ) {
            rule += "rule r" + (i++) + "\n" +
                    "when\n" +
                    str +
                    "then\n" +
                    "end\n";
        }
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        return kbase;
    }
    
    @Test
    public void testRtnNoConstraintsNoWatches() {
        String rule1 = "A()";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;

        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( 0, rtNode.getDeclaredMask() );
        assertEquals( 0, rtNode.getInferredMask() );
    }   
    
    @Test
    public void testRtnNoConstraintsWithWatches() {
        String rule1 = "A() @watch(a)";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;

        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) otn.getSinkPropagator().getSinks()[0];
        
        List<String> sp = getSettableProperties(wm, otn);
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), rtNode.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), rtNode.getInferredMask() );        
    }       
    
    @Test
    public void testRtnWithConstraintsNoWatches() {
        String rule1 = "A( a == 10 )";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);
        
        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getInferredMask());
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];        
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals(  0, rtNode.getDeclaredMask() ); // rtn declares nothing
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), rtNode.getInferredMask() ); // rtn infers from alpha 
    }  
    
    @Test
    public void testRtnWithConstraintsWithWatches() {
        String rule1 = "A( a == 10 ) @watch(b)";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);
        
        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b"), 0L, sp), alphaNode.getInferredMask() );
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];        
        
        RuleTerminalNode rtNode = ( RuleTerminalNode ) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals(  calculateMaskFromPattern(list("b"), 0L, sp), rtNode.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b"), 0L, sp), rtNode.getInferredMask() );         
    }      
    
    @Test
    public void testRtnSharedAlphaNoWatches() {
        String rule1 = "A( a == 10, b == 15 )";
        String rule2 = "A( a == 10, i == 20 )";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        LeftInputAdapterNode liaNode1 = ( LeftInputAdapterNode ) alphaNode1_1.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtNode1 = ( RuleTerminalNode ) liaNode1.getSinkPropagator().getSinks()[0];
        
        assertEquals( 0, rtNode1.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b"), 0L, sp), rtNode1.getInferredMask() );
        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        LeftInputAdapterNode liaNode2 = ( LeftInputAdapterNode ) alphaNode1_2.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtNode2 = ( RuleTerminalNode ) liaNode2.getSinkPropagator().getSinks()[0];
        
        assertEquals( 0, rtNode2.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i"), 0L, sp), rtNode2.getInferredMask() );
        
        // test rule removal        
        kbase.removeRule( "org.drools", "r0" );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i"), 0L, sp), alphaNode1.getInferredMask() );

        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        assertEquals(  0, rtNode2.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i"), 0L, sp), rtNode2.getInferredMask() );
        
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        
        kbase.removeRule( "org.drools", "r1" );
        otn = getObjectTypeNode(kbase, "A" );
        
        alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b"), 0L, sp), alphaNode1.getInferredMask() );   
        
        alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b"), 0L, sp), alphaNode1_1.getInferredMask() );   
        
        liaNode1 = ( LeftInputAdapterNode ) alphaNode1_1.getSinkPropagator().getSinks()[0];
        rtNode1 = ( RuleTerminalNode ) liaNode1.getSinkPropagator().getSinks()[0];       
        assertEquals(  0, rtNode1.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b"), 0L, sp), rtNode1.getInferredMask() );         
    }      
    
    @Test
    public void testRtnSharedAlphaWithWatches() {
        String rule1 = "A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "A( a == 10, i == 20 ) @watch(s)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c", "s", "i"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        LeftInputAdapterNode liaNode1 = ( LeftInputAdapterNode ) alphaNode1_1.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtNode1 = ( RuleTerminalNode ) liaNode1.getSinkPropagator().getSinks()[0];
        
        assertEquals(  calculateMaskFromPattern(list("c"), 0L, sp), rtNode1.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), rtNode1.getInferredMask() );
        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), alphaNode1_2.getInferredMask() );  
        
        LeftInputAdapterNode liaNode2 = ( LeftInputAdapterNode ) alphaNode1_2.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtNode2 = ( RuleTerminalNode ) liaNode2.getSinkPropagator().getSinks()[0];
        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), rtNode2.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), rtNode2.getInferredMask() );
        
        // test rule removal        
        kbase.removeRule( "org.drools", "r0" );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), alphaNode1.getInferredMask() );

        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), rtNode2.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), rtNode2.getInferredMask() );
        
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        
        kbase.removeRule( "org.drools", "r1" );
        otn = getObjectTypeNode(kbase, "A" );
        
        alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1.getInferredMask() );   
        
        alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );   
        
        liaNode1 = ( LeftInputAdapterNode ) alphaNode1_1.getSinkPropagator().getSinks()[0];
        rtNode1 = ( RuleTerminalNode ) liaNode1.getSinkPropagator().getSinks()[0];       
        assertEquals(  calculateMaskFromPattern(list("c"), 0L, sp), rtNode1.getDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), rtNode1.getInferredMask() );        
    } 
    
    @Test
    public void testBetaNoConstraintsNoWatches() {
        String rule1 = "B() A()";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;

        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        BetaNode betaNode = ( BetaNode )  otn.getSinkPropagator().getSinks()[0]; 
        assertEquals( 0, betaNode.getRightDeclaredMask() );
        assertEquals( 0, betaNode.getRightInferredMask() );
        
        assertEquals( 0, betaNode.getLeftDeclaredMask() );
        assertEquals( 0, betaNode.getLeftInferredMask() );        
    }     
    
    @Test
    public void testBetaNoConstraintsWithWatches() {
        String rule1 = "B() @watch(a) A() @watch(a)";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;

        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );
        List<String> sp = getSettableProperties(wm, otn);
        
        BetaNode betaNode = ( BetaNode )  otn.getSinkPropagator().getSinks()[0];                
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), betaNode.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), betaNode.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), betaNode.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), betaNode.getLeftInferredMask() );        
    }  
    
    @Test
    public void testBetaWithConstraintsNoWatches() {
        String rule1 = "$b : B(a == 15) A( a == 10, b == $b.b )";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);
        
        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b"), 0L, sp), alphaNode.getInferredMask());
        
        BetaNode betaNode = ( BetaNode )  alphaNode.getSinkPropagator().getSinks()[0];
        assertEquals(  calculateMaskFromPattern(list( "b" ), 0L, sp), betaNode.getRightDeclaredMask() ); // beta declares nothing
        assertEquals(  calculateMaskFromPattern(list("a", "b"), 0L, sp), betaNode.getRightInferredMask() ); // beta infers from alpha 
        
        otn = getObjectTypeNode(kbase, "B" );
        alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getInferredMask());        
        
        assertEquals(  0, betaNode.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a"), 0L, sp), betaNode.getLeftInferredMask() );         
    }    
    
    @Test
    public void testBetaWithConstraintsWithWatches() {
        String rule1 = "$b : B( a == 15) @watch(c) A( a == 10, b == $b.b ) @watch(s)";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);
        
        AlphaNode alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "s"), 0L, sp), alphaNode.getInferredMask() );
        
        BetaNode betaNode = ( BetaNode )  alphaNode.getSinkPropagator().getSinks()[0];
        assertEquals(  calculateMaskFromPattern(list("b","s"), 0L, sp), betaNode.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "s"), 0L, sp), betaNode.getRightInferredMask() );   
        
        otn = getObjectTypeNode(kbase, "B" );
        alphaNode = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "c"), 0L, sp), alphaNode.getInferredMask());        
        
        assertEquals(  calculateMaskFromPattern(list( "c"), 0L, sp), betaNode.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "c"), 0L, sp), betaNode.getLeftInferredMask() );         
    }   
    
    @Test
    public void testBetaSharedAlphaNoWatches() {
        String rule1 = "$b : B( a == 15) @watch(c) A( a == 10, s == 15, b == $b.b  )";
        String rule2 = "$b : B( a == 15) @watch(j) A( a == 10, i == 20, b == $b.b  )";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "s", "i"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("s"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "s", "b"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "s", "b"), 0L, sp), betaNode1.getRightInferredMask() );

        assertEquals( calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "c"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        BetaNode betaNode2 = ( BetaNode )  alphaNode1_2.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "j"), 0L, sp), betaNode2.getLeftInferredMask() );        
        
        // test rule removal        
        kbase.removeRule( "org.drools", "r0" );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b"), 0L, sp), alphaNode1.getInferredMask() );

        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("b"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "c"), 0L, sp), betaNode1.getLeftInferredMask() );          
        
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        
        kbase.removeRule( "org.drools", "r1" );
        otn = getObjectTypeNode(kbase, "A" );
        
        alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "s", "b"), 0L, sp), alphaNode1.getInferredMask() );   
        
        alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("s"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "s", "b"), 0L, sp), alphaNode1_1.getInferredMask() );   
        
        betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("b"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "s", "b"), 0L, sp), betaNode1.getRightInferredMask() );   
        
        assertEquals( calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "j"), 0L, sp), betaNode2.getLeftInferredMask() );           
    }     
    
    @Test
    public void testBetaSharedAlphaWithWatches() {
//        String rule1 = "$b : B( a == 15) @watch(c) A( a == 10, s == 15, b == $b.b  )";
//        String rule2 = "$b : B( a == 15) @watch(j) A( a == 10, i == 20, b == $b.b  )";
        
        String rule1 = "$b : B( a == 15) @watch(c) A( a == 10, b == 15, b == $b.b  ) @watch(c)";
        String rule2 = "$b : B( a == 15) @watch(j) A( a == 10, i == 20, b == $b.b ) @watch(s)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c", "s", "i"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("b", "c"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "c"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "s"), 0L, sp), alphaNode1_2.getInferredMask() );  
        

        BetaNode betaNode2 = ( BetaNode )  alphaNode1_2.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("b", "s"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b", "s"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "j"), 0L, sp), betaNode2.getLeftInferredMask() );   
        
        // test rule removal        
        kbase.removeRule( "org.drools", "r0" );
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "s"), 0L, sp), alphaNode1.getInferredMask() );

        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "s"), 0L, sp), alphaNode1_2.getInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("b", "s"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b", "s"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "j"), 0L, sp), betaNode2.getLeftInferredMask() );        
        
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        
        kbase.removeRule( "org.drools", "r1" );
        otn = getObjectTypeNode(kbase, "A" );
        
        alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1.getInferredMask() );   
        
        alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );   
        
        betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("b", "c"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals( calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals( calculateMaskFromPattern(list("a", "c"), 0L, sp), betaNode1.getLeftInferredMask() );        
    }     
    
    @Test
    public void testComplexBetaSharedAlphaWithWatches() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c", "s", "i", "j"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("i"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "i"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // second share
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i", "s", "j"), 0L, sp), alphaNode1_2.getInferredMask() );  
        

        BetaNode betaNode2 = ( BetaNode )  alphaNode1_2.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "j"), 0L, sp), betaNode2.getLeftInferredMask() );         
        
        // third share
        AlphaNode alphaNode1_3 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_3.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i", "s", "j"), 0L, sp), alphaNode1_3.getInferredMask() ); 
        
        AlphaNode alphaNode1_4 = ( AlphaNode ) alphaNode1_3.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_4.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i", "j"), 0L, sp), alphaNode1_4.getInferredMask() );          
        

        BetaNode betaNode3 = ( BetaNode )  alphaNode1_4.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode3.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b", "j"), 0L, sp), betaNode3.getRightInferredMask() ); 
        
        assertEquals(  calculateMaskFromPattern(list("k"), 0L, sp), betaNode3.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("c", "k"), 0L, sp), betaNode3.getLeftInferredMask() );        
    }   
    
    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR1() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        kbase.removeRule( "org.drools", "r0" );
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "s","j"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "s", "j"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "j"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // fist share, second alpha
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i", "s", "j"), 0L, sp), alphaNode1_2.getInferredMask() );  
        

        BetaNode betaNode2 = ( BetaNode )  alphaNode1_2.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "j"), 0L, sp), betaNode2.getLeftInferredMask() );         
        
        // second split, third alpha
        AlphaNode alphaNode1_3 = ( AlphaNode ) alphaNode1_2.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_3.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "i", "j"), 0L, sp), alphaNode1_3.getInferredMask() ); 

        BetaNode betaNode3 = ( BetaNode )  alphaNode1_3.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode3.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b", "j"), 0L, sp), betaNode3.getRightInferredMask() ); 
        
        assertEquals(  calculateMaskFromPattern(list("k"), 0L, sp), betaNode3.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("c", "k"), 0L, sp), betaNode3.getLeftInferredMask() );        
    }       
    
    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR2() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        kbase.removeRule( "org.drools", "r1" );
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c", "i", "j"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first split
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("i"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "i"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // fist share, second alpha
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "j"), 0L, sp), alphaNode1_2.getInferredMask() );  
        
        AlphaNode alphaNode1_3 = ( AlphaNode ) alphaNode1_2.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_3.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "b", "j"), 0L, sp), alphaNode1_3.getInferredMask() );         
        

        BetaNode betaNode2 = ( BetaNode )  alphaNode1_3.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "b", "j"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("k"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("c", "k"), 0L, sp), betaNode2.getLeftInferredMask() );         
           
    }         
    
    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR3() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KnowledgeBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        kbase.removeRule( "org.drools", "r2" );
        
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A" );
        assertNotNull( otn );

        List<String> sp = getSettableProperties(wm, otn);        

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("a"), 0L, sp), alphaNode1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c", "i", "s"), 0L, sp), alphaNode1.getInferredMask() );
                
        // first share
        AlphaNode alphaNode1_1 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertEquals( calculateMaskFromPattern(list("b"), 0L, sp), alphaNode1_1.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), alphaNode1_1.getInferredMask() );  
        
        // first split
        BetaNode betaNode1 = ( BetaNode )  alphaNode1_1.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("c"), 0L, sp), betaNode1.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "b", "c"), 0L, sp), betaNode1.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("i"), 0L, sp), betaNode1.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "i"), 0L, sp), betaNode1.getLeftInferredMask() );        
        
        // second split
        AlphaNode alphaNode1_2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertEquals( calculateMaskFromPattern(list("i"), 0L, sp), alphaNode1_2.getDeclaredMask( ) );
        assertEquals( calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), alphaNode1_2.getInferredMask() );           
        

        BetaNode betaNode2 = ( BetaNode )  alphaNode1_2.getSinkPropagator().getSinks()[0];        
        assertEquals(  calculateMaskFromPattern(list("s"), 0L, sp), betaNode2.getRightDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("a", "i", "s"), 0L, sp), betaNode2.getRightInferredMask() );
        
        assertEquals(  calculateMaskFromPattern(list("j"), 0L, sp), betaNode2.getLeftDeclaredMask() );
        assertEquals(  calculateMaskFromPattern(list("b", "j"), 0L, sp), betaNode2.getLeftInferredMask() );            
    }    

    @Test(timeout = 5000)
    public void testPropertySpecific() throws Exception {
        String rule = "package org.drools\n" +
                "global java.util.List list;\n" +
                "declare A\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $a : A(s == \"start\");\n" +
                "    $b : B(s == $a.s);\n" +
                "then\n" +
                "    modify($a) { setS(\"running\") };\n" +
                "    list.add(\"R1\");\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    A($s : s);\n" +
                "    $b : B(s != $s);\n" +
                "then\n" +
                "    modify($b) { setS($s) };\n" +
                "    list.add(\"R2\");\n" +
                "end\n" +
                "rule R3\n" +
                "when\n" +
                "    $a : A(s != \"end\");\n" +
                "    $b : B(i > $a.i);\n" +
                "then\n" +
                "    modify($a) { setS(\"end\") };\n" +
                "    list.add(\"R3\");\n" +
                "end\n" +
                "rule R4\n" +
                "when\n" +
                "    $a : A(s == \"running\");\n" +
                "    $b : B(s != $a.s);\n" + // Slot specific allows to avoid an infinite loop even without the constraint i!=2
                // "    $b : B(i != 2, != $a.s);\n" + // add this constraint if you disable slot specific
                "then\n" +
                "    modify($b) { setI(2) };\n" +
                "    list.add(\"R4\");\n" +
                "end\n" +
                "rule R5\n" +
                "when\n" +
                "    $b : B(i == 2, s == \"running\");\n" +
                "then\n" +
                "    modify($b) { setI(4) };\n" +
                "    list.add(\"R5\");\n" +
                "end";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "start" );
        factTypeA.set( factA, "i", 3 );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "s", "start" );
        factTypeB.set( factB, "i", 1 );
        ksession.insert( factB );

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        int rules = ksession.fireAllRules();

        list = (List)ksession.getGlobal( "list" );
        System.out.println(list);

        assertEquals(6, rules);
        assertEquals("end", factTypeB.get(factB, "s"));
        assertEquals(4, factTypeB.get(factB, "i"));
        ksession.dispose();
    }

    @Test
    public void testPropertySpecificSimplified() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( ! s, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "on", false );
        factTypeB.set( factB, "s", "x" );
        ksession.insert( factB );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, factTypeB.get(factB, "on"));
        assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWatchNothing() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !* )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert(factA);

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "on", false );
        factTypeB.set( factB, "s", "x" );
        ksession.insert(factB);

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, factTypeB.get(factB, "on"));
        assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWrongPropertyNameInWatchAnnotation() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !s1, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testDuplicatePropertyNamesInWatchAnnotation() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( s, !s )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testWrongUasgeOfWatchAnnotationOnNonPropertySpecificClass() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !s, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testPropertySpecificJavaBean() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.C\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $c : C(s != $s)\n" +
                "then\n" +
                "    modify($c) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $c : C(on == false)\n" +
                "then\n" +
                "    modify($c) { turnOn() }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert( factA );

        C c = new C();
        c.setOn(false);
        c.setS("x");
        ksession.insert( c );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, c.isOn());
        assertEquals("y", c.getS());
        ksession.dispose();
    }

    @Test(timeout = 5000)
    public void testPropertySpecificOnAlphaNode() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.C\n" +
                "rule R1\n" +
                "when\n" +
                "    $c : C(s == \"test\")\n" +
                "then\n" +
                "    modify($c) { turnOn() }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        C c = new C();
        c.setOn(false);
        c.setS("test");
        ksession.insert( c );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        assertEquals(true, c.isOn());
        ksession.dispose();
    }

    @Test(expected=RuntimeException.class)
    public void testInfiniteLoop() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.C\n" +
                "global java.util.concurrent.atomic.AtomicInteger counter\n" +
                "rule R1\n" +
                "when\n" +
                "    $c : C(s == \"test\") @watch( on )\n" +
                "then\n" +
                "    modify($c) { turnOn() }\n" +
                "    if (counter.incrementAndGet() > 10) throw new RuntimeException();\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        AtomicInteger counter = new AtomicInteger(0);
        ksession.setGlobal( "counter", counter );

        C c = new C();
        c.setOn(false);
        c.setS("test");
        ksession.insert( c );

        try {
            ksession.fireAllRules();
        } finally {
            assertTrue(counter.get() >= 10);
            ksession.dispose();
        }
    }

    @Test(timeout = 5000)
    public void testSharedWatchAnnotation() throws Exception {
        String rule = "package org.drools\n" +
                "declare A\n" +
                "    @propertySpecific\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $a : A(a == 0) @watch( i )\n" +
                "    $b : B(i == $a.i) @watch( s )\n" +
                "then\n" +
                "    modify($a) { setS(\"end\") }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $a : A(a == 0) @watch( b )\n" +
                "then\n" +
                "    modify($a) { setI(1) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "a", 0 );
        factTypeA.set( factA, "b", 0 );
        factTypeA.set( factA, "i", 0 );
        factTypeA.set( factA, "s", "start" );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "i", 1 );
        factTypeB.set( factB, "s", "start" );
        ksession.insert( factB );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);
        assertEquals("end", factTypeA.get(factA, "s"));
    }

    @PropertySpecific
    public static class C {
        private boolean on;
        private String s;

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Modifies( { "on" } )
        public void turnOn() {
            setOn(true);
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    @Test
    public void testBetaNodePropagation() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Hero\n" +
                "import org.drools.integrationtests.PropertySpecificTest.MoveCommand\n" +
                "rule \"Move\" when\n" +
                "   $mc : MoveCommand( move == 1 )" +
                "   $h  : Hero( canMove == true )" +
                "then\n" +
                "   modify( $h ) { setPosition($h.getPosition() + 1) };\n" +
                "   retract ( $mc );\n" +
                "   System.out.println( \"Move: \" + $h + \" : \" + $mc );" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Hero hero = new Hero();
        hero.setPosition(0);
        hero.setCanMove(true);
        ksession.insert(hero);
        ksession.fireAllRules();

        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setMove(1);
        ksession.insert(moveCommand);
        ksession.fireAllRules();

        moveCommand = moveCommand = new MoveCommand();
        moveCommand.setMove(1);
        ksession.insert(moveCommand);
        ksession.fireAllRules();

        assertEquals(2, hero.getPosition());
    }

    @Test(timeout = 5000)
    public void testPropSpecOnPatternWithThis() throws Exception {
        String rule = "package org.drools\n" +
                "declare A\n" +
                "    @propertySpecific\n" +
                "    i : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    a : A\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $b : B();\n" +
                "    $a : A(this == $b.a);\n" +
                "then\n" +
                "    modify($b) { setA(null) };\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "i", 1 );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "a", factA );
        ksession.insert( factB );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testPropSpecOnBetaNode() throws Exception {
        String rule = "package org.drools\n" +
                "declare A\n" +
                "    @propertySpecific\n" +
                "    i : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    i : int\n" +
                "    j : int\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $a : A()\n" +
                "    $b : B($i : i < 4, j < 2, j == $a.i)\n" +
                "then\n" +
                "    modify($b) { setI($i+1) };\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType typeA = kbase.getFactType( "org.drools", "A" );
        FactType typeB = kbase.getFactType( "org.drools", "B" );

        Object a1 = typeA.newInstance();
        typeA.set( a1, "i", 1 );
        ksession.insert( a1 );

        Object a2 = typeA.newInstance();
        typeA.set( a2, "i", 2 );
        ksession.insert( a2 );

        Object b1 = typeB.newInstance();
        typeB.set( b1, "i", 1 );
        typeB.set( b1, "j", 1 );
        ksession.insert( b1 );

        int rules = ksession.fireAllRules();
        assertEquals(3, rules);
    }

    @Test(timeout = 5000)
    public void testConfig() throws Exception {
        String rule = "package org.drools\n" +
                "declare A\n" +
                "    i : int\n" +
                "    j : int\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $a : A(i == 1)\n" +
                "then\n" +
                "    modify($a) { setJ(2) };\n" +
                "end\n";

        KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        config.setOption(PropertySpecificOption.ALWAYS);
        KnowledgeBase kbase = loadKnowledgeBaseFromString( config, rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType typeA = kbase.getFactType( "org.drools", "A" );
        Object a = typeA.newInstance();
        typeA.set( a, "i", 1 );
        ksession.insert( a );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testEmptyBetaConstraint() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Hero\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Cell\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Init\n" +
                "import org.drools.integrationtests.PropertySpecificTest.CompositeImageName\n" +
                "declare CompositeImageName\n" +
                "   @propertySpecific\n" +
                "end\n" +
                "rule \"Show First Cell\" when\n" +
                "   Init()\n" +
                "   $c : Cell( row == 0, col == 0 )\n" +
                "then\n" +
                "   modify( $c ) { hidden = false };\n" +
                "end\n" +
                "\n" +
                "rule \"Paint Empty Hero\" when\n" +
                "   $c : Cell()\n" +
                "   $cin : CompositeImageName( cell == $c )\n" +
                "   not Hero( row == $c.row, col == $c.col  )\n" +
                "then\n" +
                "   modify( $cin ) { hero = \"\" };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(rule);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Init());

        Cell cell = new Cell();
        cell.setRow(0);
        cell.setCol(0);
        cell.hidden = true;
        ksession.insert(cell);

        Hero hero = new Hero();
        hero.setRow(1);
        hero.setCol(1);
        ksession.insert(hero);

        CompositeImageName cin = new CompositeImageName();
        cin.setHero("hero");
        cin.setCell(cell);
        ksession.insert(cin);

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);
    }

    @Test(timeout = 5000)
    public void testNoConstraint() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Cell\n" +
                "rule R1 when\n" +
                "   $c : Cell()\n" +
                "then\n" +
                "   modify( $c ) { hidden = true };\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(rule);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Cell());

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test(timeout = 5000)
    public void testNodeSharing() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.Cell\n" +
                "rule R1 when\n" +
                "   $c : Cell()\n" +
                "then\n" +
                "   modify( $c ) { hidden = true };\n" +
                "   System.out.println( \"R1\");\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $c : Cell(hidden == true)\n" +
                "then\n" +
                "   System.out.println( \"R2\");\n" +
                "end\n" +
                "rule R3 when\n" +
                "   $c : Cell(hidden == true, row == 0)\n" +
                "then\n" +
                "   modify( $c ) { setCol(1) };\n" +
                "   System.out.println( \"R3\");\n" +
                "end\n" +
                "rule R4 when\n" +
                "   $c : Cell(hidden == true, col == 1)\n" +
                "then\n" +
                "   modify( $c ) { setRow(1) };\n" +
                "   System.out.println( \"R4\");\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(rule);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Cell());

        int rules = ksession.fireAllRules();
        assertEquals(4, rules);
    }

    @PropertySpecific
    public static class Init { }

    @PropertySpecific
    public static class Hero {
        private boolean canMove;
        private int position;
        private int col;
        private int row;

        public boolean isCanMove() {
            return canMove;
        }

        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        @Override
        public String toString() {
            return "Hero{" + "position=" + position + '}';
        }
    }

    @PropertySpecific
    public static class MoveCommand {
        private int move;

        public int getMove() {
            return move;
        }

        public void setMove(int move) {
            this.move = move;
        }

        @Override
        public String toString() {
            return "MoveCommand{" + "move=" + move + '}';
        }
    }

    @PropertySpecific
    public static class Cell {
        private int col;
        private int row;
        public boolean hidden;

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    public static class CompositeImageName {
        private Cell cell;
        public String hero;

        public Cell getCell() {
            return cell;
        }

        public void setCell(Cell cell) {
            this.cell = cell;
        }

        public String getHero() {
            return hero;
        }

        public void setHero(String hero) {
            this.hero = hero;
        }
    }    

    List<String> list(String... items) {
        List list = new ArrayList();
        for ( String str : items ) {
            list.add( str );
        }
        return list;
    }

    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, String nodeName) {
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType().getSimpleName().equals( nodeName ) ) {
                return n;
            }
        }
        return null;
    }
}
