package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.SegmentMemory;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.conf.LRUnlinkingOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class SegmentCreationTest {
    
    @Test
    public void testSingleEmptyLhs() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase(" ");

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        RuleTerminalNode rtn = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];  
        

        wm.insert( new LinkingTest.A() );
        
        // LiaNode and Rule are in same segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( rtn, smem.getTipNode() );
        assertNull( smem.getNext() );
        assertNull( smem.getFirst() );
    }
  
    @Test
    public void testSingleSharedEmptyLhs() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( " ", " ");

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[1];
        
        wm.insert( new LinkingTest.A() );
        
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        
        // each RTN is in it's own segment
        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );
        
        SegmentMemory rtnSmem2 = rtnSmem1.getNext();
        assertEquals( rtn2, rtnSmem2.getRootNode() );
        assertEquals( rtn2, rtnSmem2.getTipNode() );
    }    
    
    @Test
    public void testSinglePattern() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase("   A() \n");

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        RuleTerminalNode rtn = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];  
        

        wm.insert( new LinkingTest.A() );
        
        // LiaNode and Rule are in same segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( rtn, smem.getTipNode() );
        assertNull( smem.getNext() );
        assertNull( smem.getFirst() );
    }
    
    @Test
    public void testSingleSharedPattern() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( "   A() \n", 
                                                  "   A() \n");

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[1];
        
        wm.insert( new LinkingTest.A() );
        
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        
        // each RTN is in it's own segment
        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );
        
        SegmentMemory rtnSmem2 = rtnSmem1.getNext();
        assertEquals( rtn2, rtnSmem2.getRootNode() );
        assertEquals( rtn2, rtnSmem2.getTipNode() );        
    }     
    
    @Test
    public void testMultiSharedPattern() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( "   A() \n", 
                                                  "   A() B() \n",
                                                  "   A() B() C() \n");

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];        
        JoinNode bNode = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) bNode.getSinkPropagator().getSinks()[0];
        
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn3 = ( RuleTerminalNode) cNode.getSinkPropagator().getSinks()[0];        
                
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        
        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );
        
        SegmentMemory bSmem = rtnSmem1.getNext();
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( bNode, bSmem.getTipNode() );  
        
        // child segment is not yet initialised, so null
        assertNull( bSmem.getFirst() );
        
        // there is no next
        assertNull( bSmem.getNext() );
        
        wm.fireAllRules(); // child segments should now be initialised
  
        SegmentMemory rtnSmem2 = bSmem.getFirst();
        assertEquals( rtn2, rtnSmem2.getRootNode() );
        assertEquals( rtn2, rtnSmem2.getTipNode() ); 
        
        SegmentMemory cSmem = rtnSmem2.getNext();
        assertEquals( cNode, cSmem.getRootNode() );
        assertEquals( rtn3, cSmem.getTipNode() ); // note rtn3 is in the same segment as C
    }       
  
    @Test
    public void testSubnetworkNoSharing() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( " A()  not ( B() and C() ) \n" );

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[0];
        
        NotNode notNode = ( NotNode ) liaNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
            
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        
        // LiaNode is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        assertNull( smem.getNext() );
        smem =  smem.getFirst();
        
        SegmentMemory bSmem = wm.getNodeMemory( bNode ).getSegmentMemory(); // it's nested inside of smem, so lookup from wm
        assertEquals( smem, bSmem );
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( riaNode, bSmem.getTipNode() ); 
        
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( notNode );
        assertEquals( bm.getSegmentMemory(), smem.getNext() );
        assertEquals(bSmem, bm.getRiaRuleMemory().getSegmentMemory() ); // check subnetwork ref was made
    }        

    
    @Test
    public void tesSubnetworkAfterShare() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( "   A() \n", 
                                                  "   A()  not ( B() and C() ) \n" );

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[1];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[0];
        
        NotNode notNode = ( NotNode ) liaNode.getSinkPropagator().getSinks()[2];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
               
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        
        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );
        
        SegmentMemory bSmem = rtnSmem1.getNext();
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( riaNode, bSmem.getTipNode() );
        
        SegmentMemory notSmem = bSmem.getNext();
        assertEquals( notNode, notSmem.getRootNode() );
        assertEquals( rtn2, notSmem.getTipNode() );    
        
        // child segment is not yet initialised, so null
        assertNull( bSmem.getFirst() );
    }    
    
    @Test
    public void tesShareInSubnetwork() throws Exception {
        KnowledgeBase kbase = buildKnowledgeBase( "   A() \n", 
                                                  "   A() B() C() \n",
                                                  "   A()  not ( B() and C() ) \n" );

        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[1];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode ) cNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[1];
        
        NotNode notNode = ( NotNode ) liaNode.getSinkPropagator().getSinks()[2];
        RuleTerminalNode rtn3 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
               
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        
        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( liaNode, smem.getTipNode() );
        
        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );
        
        SegmentMemory bSmem = rtnSmem1.getNext();
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( cNode, bSmem.getTipNode() );
        
        assertNull(  bSmem.getFirst() ); // segment is not initialized yet
        
        wm.fireAllRules();
        
        SegmentMemory rtn2Smem = bSmem.getFirst();
        assertEquals( rtn2, rtn2Smem.getRootNode() );
        assertEquals( rtn2, rtn2Smem.getTipNode() ); 
        
        SegmentMemory riaSmem = rtn2Smem.getNext();
        assertEquals( riaNode, riaSmem.getRootNode() );
        assertEquals( riaNode, riaSmem.getTipNode() );        
        
        SegmentMemory notSmem = bSmem.getNext();
        assertEquals( notNode, notSmem.getRootNode() );
        assertEquals( rtn3, notSmem.getTipNode() );     
    }       

    private KnowledgeBase buildKnowledgeBase(String... rules) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + LinkingTest.A.class.getCanonicalName() + "\n" ;
        str += "import " + LinkingTest.B.class.getCanonicalName() + "\n" ;
        str += "import " + LinkingTest.C.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        int i = 0;
        for ( String lhs : rules) {
            str += "rule rule" + (i++) +"  when \n";
            str +=  lhs;
            str += "then \n";
            str += "end \n";            
        }
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }      

    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }    
}

