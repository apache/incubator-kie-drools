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

package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SegmentCreationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SegmentCreationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }
    
    @Test
    public void testSingleEmptyLhs() throws Exception {
        KieBase kbase = buildKnowledgeBase(" ");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];  
        

        wm.insert( new LinkingTest.A() );
        wm.flushPropagations();

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
        KieBase kbase = buildKnowledgeBase( " ", " ");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, InitialFactImpl.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[1];
        
        wm.insert( new LinkingTest.A() );
        wm.flushPropagations();

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
        KieBase kbase = buildKnowledgeBase("   A() \n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) liaNode.getSinkPropagator().getSinks()[0];  
        

        wm.insert(new LinkingTest.A());
        wm.flushPropagations();

        // LiaNode and Rule are in same segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertNull( smem );
    }
    
    @Test
    public void testSingleSharedPattern() throws Exception {
        KieBase kbase = buildKnowledgeBase( "   A() B()\n",
                                                  "   A() B()\n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode beta = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) beta.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) beta.getSinkPropagator().getSinks()[1];
        
        wm.insert(new LinkingTest.A());
        wm.insert(new LinkingTest.B());
        wm.fireAllRules();

        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode ); 
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( liaNode, smem.getRootNode() );
        assertEquals( beta, smem.getTipNode() );
        
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
        KieBase kbase = buildKnowledgeBase( " X() A() \n",
                                                  "  X()  A() B() \n",
                                                  "  X() A() B() C() \n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode dotn = getObjectTypeNode(kbase, LinkingTest.X.class );
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode lian = (LeftInputAdapterNode) dotn.getObjectSinkPropagator().getSinks()[0];

        JoinNode beta = (JoinNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) beta.getSinkPropagator().getSinks()[0];
        JoinNode bNode = ( JoinNode ) beta.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) bNode.getSinkPropagator().getSinks()[0];
        
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn3 = ( RuleTerminalNode) cNode.getSinkPropagator().getSinks()[0];        
                
        wm.insert( new LinkingTest.X() );
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert(new LinkingTest.C());
        wm.flushPropagations();

        // LiaNode  is in it's own segment
        BetaMemory betaMem = (BetaMemory ) wm.getNodeMemory( beta );
        SegmentMemory smem = betaMem.getSegmentMemory();
        assertEquals( lian, smem.getRootNode() );
        assertEquals( beta, smem.getTipNode() );
        
        // child segment is not yet initialised, so null
        assertNull( smem.getFirst() );
        
        // there is no next
        assertNull( smem.getNext() );
        
        wm.fireAllRules(); // child segments should now be initialised
        wm.flushPropagations();

        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );

        SegmentMemory bSmem = rtnSmem1.getNext();
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( bNode, bSmem.getTipNode() );

        SegmentMemory rtnSmem2 = bSmem.getFirst();
        assertEquals( rtn2, rtnSmem2.getRootNode() );
        assertEquals( rtn2, rtnSmem2.getTipNode() ); 
        
        SegmentMemory cSmem = rtnSmem2.getNext();
        assertEquals( cNode, cSmem.getRootNode() );
        assertEquals( rtn3, cSmem.getTipNode() ); // note rtn3 is in the same segment as C
    }       
  
    @Test
    public void testSubnetworkNoSharing() throws Exception {
        KieBase kbase = buildKnowledgeBase( " A()  not ( B() and C() ) \n" );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[0];
        
        NotNode notNode = ( NotNode ) liaNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
            
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        wm.flushPropagations();

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
        KieBase kbase = buildKnowledgeBase( "  X() A() \n",
                                                  "   X() A()  not ( B() and C() ) \n" );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );
        ObjectTypeNode dotn = getObjectTypeNode(kbase, LinkingTest.X.class );

        LeftInputAdapterNode lian = (LeftInputAdapterNode) dotn.getObjectSinkPropagator().getSinks()[0];

        JoinNode joinNode = (JoinNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) joinNode.getSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) joinNode.getSinkPropagator().getSinks()[1];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[0];
        
        NotNode notNode = ( NotNode ) joinNode.getSinkPropagator().getSinks()[2];
        RuleTerminalNode rtn2 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
               
        wm.insert( new LinkingTest.X() );
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        wm.fireAllRules();

        BetaMemory liaMem = ( BetaMemory ) wm.getNodeMemory( joinNode );
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( lian, smem.getRootNode() );
        assertEquals( joinNode, smem.getTipNode() );
        
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
        KieBase kbase = buildKnowledgeBase( "  X() A() \n",
                                                  "   X() A() B() C() \n",
                                                  "   X() A()  not ( B() and C() ) \n" );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );
        ObjectTypeNode dotn = getObjectTypeNode(kbase, LinkingTest.X.class );

        LeftInputAdapterNode lian = (LeftInputAdapterNode) dotn.getObjectSinkPropagator().getSinks()[0];

        JoinNode beta = (JoinNode) aotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn1 = ( RuleTerminalNode) beta.getSinkPropagator().getSinks()[0];
        
        JoinNode bNode = ( JoinNode ) beta.getSinkPropagator().getSinks()[1];
        JoinNode cNode = ( JoinNode ) bNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode ) cNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) cNode.getSinkPropagator().getSinks()[1];
        
        NotNode notNode = ( NotNode ) beta.getSinkPropagator().getSinks()[2];
        RuleTerminalNode rtn3 = ( RuleTerminalNode) notNode.getSinkPropagator().getSinks()[0];
               
        wm.insert( new LinkingTest.X() );
        wm.insert( new LinkingTest.A() );
        wm.insert( new LinkingTest.B() );
        wm.insert( new LinkingTest.C() );
        wm.flushPropagations();

        // LiaNode  is in it's own segment
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( lian );
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( lian, smem.getRootNode() );
        assertEquals( beta, smem.getTipNode() );

        assertNull(  smem.getFirst() ); // segment is not initialized yet

        wm.fireAllRules();

        SegmentMemory rtnSmem1 = smem.getFirst();
        assertEquals( rtn1, rtnSmem1.getRootNode() );
        assertEquals( rtn1, rtnSmem1.getTipNode() );

        SegmentMemory bSmem = rtnSmem1.getNext();
        assertEquals( bNode, bSmem.getRootNode() );
        assertEquals( cNode, bSmem.getTipNode() );

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

    @Test
    public void testBranchCESingleSegment() throws Exception {
        KieBase kbase = buildKnowledgeBase( "   $a : A() \n" +
                                                  "   if ( $a != null ) do[t1] \n" +
                                                  "   B() \n" );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());

        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];

        ConditionalBranchNode cen1Node = ( ConditionalBranchNode ) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode bNode = ( JoinNode ) cen1Node.getSinkPropagator().getSinks()[0];

        RuleTerminalNode rtn1 = ( RuleTerminalNode ) bNode.getSinkPropagator().getSinks()[0];

        FactHandle bFh = wm.insert( new LinkingTest.B() );
        wm.flushPropagations();

        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode );
        SegmentMemory smem = liaMem.getSegmentMemory();
        assertEquals( 1, smem.getAllLinkedMaskTest() );
        assertEquals( 4, smem.getLinkedNodeMask() ); // B links, but it will not trigger mask
        assertFalse( smem.isSegmentLinked() );

        PathMemory pmem = ( PathMemory ) wm.getNodeMemory(rtn1);
        assertEquals( 1, pmem.getAllLinkedMaskTest() );
        assertEquals( 0, pmem.getLinkedSegmentMask() );
        assertFalse( pmem.isRuleLinked() );

        wm.insert(new LinkingTest.A());
        wm.flushPropagations();

        assertEquals( 5, smem.getLinkedNodeMask() ); // A links in segment
        assertTrue( smem.isSegmentLinked() );

        assertEquals( 1, pmem.getLinkedSegmentMask() );
        assertTrue( pmem.isRuleLinked() );

        wm.delete(bFh); // retract B does not unlink the rule
        wm.flushPropagations();

        assertEquals( 1, pmem.getLinkedSegmentMask() );
        assertTrue( pmem.isRuleLinked() );
    }

    @Test
    public void testBranchCEMultipleSegments() throws Exception {
        KieBase kbase = buildKnowledgeBase( "  X() $a : A() \n", // r1
                                                  "  X()  $a : A() \n" +
                                                  "   if ( $a != null ) do[t1] \n" +
                                                  "   B() \n", // r2
                                                  "  X() $a : A() \n"+
                                                  "   if ( $a != null ) do[t1] \n" +
                                                  "   B() \n" +
                                                  "   C() \n" // r3
                                                );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());

        ObjectTypeNode aotn = getObjectTypeNode(kbase, LinkingTest.A.class );

        LeftTupleSource liaNode = (LeftTupleSource) aotn.getObjectSinkPropagator().getSinks()[0];

        ConditionalBranchNode cen1Node = ( ConditionalBranchNode ) liaNode.getSinkPropagator().getSinks()[1];
        JoinNode bNode = ( JoinNode ) cen1Node.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode ) bNode.getSinkPropagator().getSinks()[0];

        JoinNode cNode =  ( JoinNode ) bNode.getSinkPropagator().getSinks()[1];
        RuleTerminalNode rtn3 = ( RuleTerminalNode ) cNode.getSinkPropagator().getSinks()[0];

        FactHandle bFh = wm.insert( new LinkingTest.B() );
        FactHandle cFh = wm.insert( new LinkingTest.C() );
        wm.flushPropagations();

        BetaMemory bNodeBm = ( BetaMemory ) wm.getNodeMemory( bNode );
        SegmentMemory bNodeSmem = bNodeBm.getSegmentMemory();
        assertEquals( 0, bNodeSmem.getAllLinkedMaskTest() ); // no beta nodes before branch CE, so never unlinks
        assertEquals( 2, bNodeSmem.getLinkedNodeMask() );

        PathMemory pmemr2 = ( PathMemory ) wm.getNodeMemory(rtn2);
        assertEquals( 1, pmemr2.getAllLinkedMaskTest() );
        assertEquals( 2, pmemr2.getLinkedSegmentMask() );
        assertEquals( 3, pmemr2.getSegmentMemories().length );
        assertFalse( pmemr2.isRuleLinked() );

        PathMemory pmemr3 = ( PathMemory ) wm.getNodeMemory(rtn3);
        assertEquals( 1, pmemr3.getAllLinkedMaskTest() );  // notice only the first segment links
        assertEquals( 3, pmemr3.getSegmentMemories().length );
        assertFalse( pmemr3.isRuleLinked() );

        BetaMemory cNodeBm = ( BetaMemory ) wm.getNodeMemory( cNode );
        SegmentMemory cNodeSmem = cNodeBm.getSegmentMemory();

        assertEquals( 1, cNodeSmem.getAllLinkedMaskTest() );
        assertEquals( 1, cNodeSmem.getLinkedNodeMask() );

        wm.insert(new LinkingTest.X());
        wm.insert(new LinkingTest.A());
        wm.flushPropagations();

        assertTrue( pmemr2.isRuleLinked() );
        assertTrue( pmemr3.isRuleLinked() );

        wm.delete(bFh); // retract B does not unlink the rule
        wm.delete(cFh); // retract C does not unlink the rule
        wm.flushPropagations();

        assertEquals( 3, pmemr2.getLinkedSegmentMask() ); // b segment never unlinks, as it has no impact on path unlinking anyway
        assertTrue( pmemr2.isRuleLinked() );

        assertEquals( 3, pmemr3.getLinkedSegmentMask() ); // b segment never unlinks, as it has no impact on path unlinking anyway
        assertTrue( pmemr3.isRuleLinked() );
    }

    private KieBase buildKnowledgeBase(String... rules) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + LinkingTest.A.class.getCanonicalName() + "\n" ;
        str += "import " + LinkingTest.B.class.getCanonicalName() + "\n" ;
        str += "import " + LinkingTest.C.class.getCanonicalName() + "\n" ;
        str += "import " + LinkingTest.X.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        int i = 0;
        for ( String lhs : rules) {
            str += "rule rule" + (i++) +"  when \n";
            str +=  lhs;
            str += "then \n";
            str += "then[t1] \n";
            str += "end \n";            
        }

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        return kbase;
    }      

    public ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }    
}

