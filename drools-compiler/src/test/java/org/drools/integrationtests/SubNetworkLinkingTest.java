package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.drools.FactHandle;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.*;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.reteoo.PathMemory;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.conf.LRUnlinkingOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class SubNetworkLinkingTest {
    public static class A {       
        private int value;
        
        public A() {
            
        }
        
        public A(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        } 
    }
    
    public static class B {     
        private int value;
        
        public B() {
            
        }
        
        public B(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }
    
    public static class C {
        private int value;
        
        public C() {
            
        }
        
        public C(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }
    
    public static class D {
        private int value;
        
        public D() {
            
        }
        
        public D(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }

    public static class E {
        private int value;
        
        public E() {
            
        }
        
        public E(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }
    
    public static class F {
        private int value;
        
        public F() {
            
        }
        
        public F(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }    
    
    public static class G {
        private int value;
        
        public G() {
            
        }
        
        public G(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }         
    }    
    
    @Test
    public void testSubNetworkSharing() throws Exception {
        // Checks the network is correctly formed, with sharing
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";
        
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   C() \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";  
                
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        
        str += "rule rule3 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";                      
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 3, liaNode.getSinkPropagator().size() );
        
        ExistsNode existsNode2 = ( ExistsNode) liaNode.getSinkPropagator().getSinks()[1];
       
        ExistsNode existsNode3 = ( ExistsNode) liaNode.getSinkPropagator().getSinks()[2];
        
        JoinNode joinNodeB = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        assertSame( joinNodeB.getRightInput(), getObjectTypeNode(kbase, B.class ) );
        
        JoinNode joinNodeC = ( JoinNode) joinNodeB.getSinkPropagator().getSinks()[0];
        assertSame( joinNodeC.getRightInput(), getObjectTypeNode(kbase, C.class ) );           
        assertEquals( 2, joinNodeC.getSinkPropagator().size() );
        
        JoinNode joinNodeD = ( JoinNode) joinNodeC.getSinkPropagator().getSinks()[0];
        assertSame( joinNodeD.getRightInput(), getObjectTypeNode(kbase, D.class ) );   
        assertEquals( 2, joinNodeD.getSinkPropagator().size() );        

        
        assertSame( existsNode2, (( RightInputAdapterNode) joinNodeC.getSinkPropagator().getSinks()[1]).getSinkPropagator().getSinks()[0] );
        
        assertSame( existsNode3, (( RightInputAdapterNode) joinNodeD.getSinkPropagator().getSinks()[1]).getSinkPropagator().getSinks()[0] );        
    }
    
    @Test
    public void testSubNetworkSharingMemories() throws Exception {
        // checks the memory sharing works, and linking, uses the already checked network from testSubNetworkSharing
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";
        
        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   C() \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";  
                
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        
        str += "rule rule3 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";                      
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        LeftInputAdapterNode liaNodeA = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];        
        ExistsNode existsNode2 = ( ExistsNode) liaNodeA.getSinkPropagator().getSinks()[1];       
        ExistsNode existsNode3 = ( ExistsNode) liaNodeA.getSinkPropagator().getSinks()[2];        
        JoinNode joinNodeB = ( JoinNode) liaNodeA.getSinkPropagator().getSinks()[0];        
        JoinNode joinNodeC = ( JoinNode) joinNodeB.getSinkPropagator().getSinks()[0]; 
        
        JoinNode joinNodeD1 = ( JoinNode) joinNodeC.getSinkPropagator().getSinks()[0];        
        JoinNode joinNodeD2 = ( JoinNode) existsNode2.getSinkPropagator().getSinks()[0];
        
        JoinNode joinNodeE = ( JoinNode) existsNode3.getSinkPropagator().getSinks()[0];
        
        RuleTerminalNode rtn1 = ( RuleTerminalNode ) joinNodeD1.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn2 = ( RuleTerminalNode ) joinNodeD2.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn3 = ( RuleTerminalNode ) joinNodeE.getSinkPropagator().getSinks()[0];
        
        FactHandle fha = wm.insert( new A() );
        wm.insert( new B() );
        wm.insert( new C() );
        wm.insert( new D() );
        
        BetaMemory bm = null;                
        
        LiaNodeMemory liam = ( LiaNodeMemory ) wm.getNodeMemory( liaNodeA );
        
        BetaMemory bm1 = ( BetaMemory ) wm.getNodeMemory( joinNodeB );
        BetaMemory bm2 = ( BetaMemory ) wm.getNodeMemory( joinNodeC );
        BetaMemory bm3 = ( BetaMemory ) wm.getNodeMemory( joinNodeD1 );         
        assertEquals(1, liam.getNodePosMaskBit() );
        assertEquals(1, bm1.getNodePosMaskBit() );                   
        assertEquals(2, bm2.getNodePosMaskBit() );                                 
        assertEquals(1, bm3.getNodePosMaskBit() );
        
        assertNotSame( liam.getSegmentMemory(), bm1.getSegmentMemory() );
        assertSame( bm1.getSegmentMemory(), bm2.getSegmentMemory() );
        assertNotSame( bm2.getSegmentMemory(), bm3.getSegmentMemory() );
                
        BetaMemory bm4 = ( BetaMemory ) wm.getNodeMemory( existsNode2 );
        BetaMemory bm5 = ( BetaMemory ) wm.getNodeMemory( joinNodeD2 ); 
        assertEquals(1, bm4.getNodePosMaskBit() );               
        assertEquals(2, bm5.getNodePosMaskBit() );           
        assertSame( bm4.getSegmentMemory(), bm5.getSegmentMemory() );      
        
        PathMemory rs1 = (PathMemory) wm.getNodeMemory( rtn1 );
        PathMemory rs2 = (PathMemory)  wm.getNodeMemory( rtn2 );
        PathMemory rs3 = (PathMemory)  wm.getNodeMemory( rtn3 );
        
        assertTrue( rs1.isRuleLinked() );
        assertTrue( rs2.isRuleLinked() );
        assertFalse( rs3.isRuleLinked() ); // no E yet
        
        
        wm.insert( new E() );
        BetaMemory bm6 = ( BetaMemory ) wm.getNodeMemory( existsNode3 );
        BetaMemory bm7 = ( BetaMemory ) wm.getNodeMemory( joinNodeE );          
        assertEquals(1, bm6.getNodePosMaskBit() );        
        assertEquals(2, bm7.getNodePosMaskBit() );  
        assertSame( bm6.getSegmentMemory(), bm7.getSegmentMemory() );
        
        assertTrue( rs1.isRuleLinked() );
        assertTrue( rs2.isRuleLinked() );
        assertTrue( rs3.isRuleLinked() );   
        
        wm.retract( fha );
        wm.fireAllRules(); // need to have rules evalulated, for unlinking to happen
        assertFalse( rs1.isRuleLinked() );
        assertFalse( rs2.isRuleLinked() );
        assertFalse( rs3.isRuleLinked() );
    }
    
    @Test
    public void testSubNetworkRiaLinking() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 1, liaNode.getSinkPropagator().size() );
        
        JoinNode bNode = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( 2, bNode.getSinkPropagator().size() );
        
        ExistsNode exists1n = ( ExistsNode) bNode.getSinkPropagator().getSinks()[1];
       
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];
        JoinNode dNode = ( JoinNode) cNode.getSinkPropagator().getSinks()[0];
        assertEquals( 1, dNode.getSinkPropagator().size() );

        RightInputAdapterNode riaNode1 =  ( RightInputAdapterNode ) dNode.getSinkPropagator().getSinks()[0];
        

        JoinNode eNode = ( JoinNode ) exists1n.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) eNode.getSinkPropagator().getSinks()[0];

        SegmentUtilities.createSegmentMemory( exists1n, wm );
        BetaMemory existsBm = ( BetaMemory ) wm.getNodeMemory( exists1n );
        
        assertEquals( 0, existsBm.getSegmentMemory().getLinkedNodeMask() );

        FactHandle fhc = wm.insert(  new C() );
        FactHandle fhd = wm.insert(  new D() );
        
        assertEquals( 1, existsBm.getSegmentMemory().getLinkedNodeMask() );  // exists is start of new segment
        
        wm.retract( fhd );
        assertEquals( 0, existsBm.getSegmentMemory().getLinkedNodeMask() );
        
        PathMemory rs = (PathMemory) wm.getNodeMemory( rtn );
        assertFalse( rs.isRuleLinked() );               

        wm.insert(  new A() );
        wm.insert(  new B() );
        wm.insert(  new E() );        
        assertFalse( rs.isRuleLinked() );
        
        
        wm.insert(  new D() );
        assertTrue( rs.isRuleLinked() );
        
        wm.retract(  fhc );        
        assertFalse( rs.isRuleLinked() );
        
        wm.insert(  new C() );
        assertTrue( rs.isRuleLinked() );                
    }       
    
    @Test
    public void testNestedSubNetwork() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "import " + F.class.getCanonicalName() + "\n" ;
        str += "import " + G.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() and exists( E() and F() ) ) \n";
        str += "   G() \n";
        str += "then \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;        
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 1, liaNode.getSinkPropagator().size() );
        
        JoinNode bNode = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        assertEquals( 2, bNode.getSinkPropagator().size() );
        
        ExistsNode exists1n = ( ExistsNode) bNode.getSinkPropagator().getSinks()[1];
       
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];
        JoinNode dNode = ( JoinNode) cNode.getSinkPropagator().getSinks()[0];
        assertEquals( 2, dNode.getSinkPropagator().size() );
        
        ExistsNode exists2n = ( ExistsNode) dNode.getSinkPropagator().getSinks()[1];     
        
        JoinNode eNode = ( JoinNode) dNode.getSinkPropagator().getSinks()[0];
        JoinNode fNode = ( JoinNode) eNode.getSinkPropagator().getSinks()[0];
        
        RightInputAdapterNode riaNode2 =  ( RightInputAdapterNode ) fNode.getSinkPropagator().getSinks()[0];        
        assertEquals( exists2n, riaNode2.getSinkPropagator().getSinks()[0] );
        
        RightInputAdapterNode riaNode1 =  ( RightInputAdapterNode ) exists2n.getSinkPropagator().getSinks()[0];        
        assertEquals( exists1n, riaNode1.getSinkPropagator().getSinks()[0] );
        
        JoinNode gNode = ( JoinNode) exists1n.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) gNode.getSinkPropagator().getSinks()[0];
        
        wm.insert(  new A() );
        wm.insert(  new B() );
        wm.insert(  new C() );
        wm.insert(  new D() );
        wm.insert(  new F() );
        wm.insert(  new G() );
        
        PathMemory rs = (PathMemory) wm.getNodeMemory( rtn );
        assertFalse( rs.isRuleLinked() );  
        
        FactHandle fhE1 = wm.insert(  new E() );
        FactHandle fhE2 = wm.insert(  new E() );
        assertTrue( rs.isRuleLinked() );
        
        wm.retract( fhE1 );
        assertTrue( rs.isRuleLinked() );
        
        wm.retract( fhE2 );
        assertFalse( rs.isRuleLinked() );        
    }    
    
    @Test
    public void testNestedSubNetworkMasks() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "import " + F.class.getCanonicalName() + "\n" ;
        str += "import " + G.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        str += "rule rule1 when \n";
        str += "   A() \n";
        str += "   B() \n";
        str += "   exists( C() and D() and exists( E() and F() ) ) \n";
        str += "   G() \n";
        str += "then \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;        
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];        
        JoinNode bNode = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        
        ExistsNode exists1n = ( ExistsNode) bNode.getSinkPropagator().getSinks()[1];
       
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];
        JoinNode dNode = ( JoinNode) cNode.getSinkPropagator().getSinks()[0];
        
        ExistsNode exists2n = ( ExistsNode) dNode.getSinkPropagator().getSinks()[1];     
        
        JoinNode eNode = ( JoinNode) dNode.getSinkPropagator().getSinks()[0];
        JoinNode fNode = ( JoinNode) eNode.getSinkPropagator().getSinks()[0];
        
        RightInputAdapterNode riaNode2 =  ( RightInputAdapterNode ) fNode.getSinkPropagator().getSinks()[0];                
        RightInputAdapterNode riaNode1 =  ( RightInputAdapterNode ) exists2n.getSinkPropagator().getSinks()[0];        
        
        JoinNode gNode = ( JoinNode) exists1n.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) gNode.getSinkPropagator().getSinks()[0];
        
        wm.insert(  new A() );
        wm.insert(  new B() );
        wm.insert(  new C() );
        wm.insert(  new D() );
        wm.insert(  new G() );
        
        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode );
        BetaMemory bMem = ( BetaMemory )   wm.getNodeMemory( bNode );
        BetaMemory exists1Mem = ( BetaMemory ) wm.getNodeMemory( exists1n );
        BetaMemory cMem = ( BetaMemory )   wm.getNodeMemory( cNode );
        BetaMemory dMem = ( BetaMemory )   wm.getNodeMemory( dNode );
        BetaMemory exists2Mem = ( BetaMemory ) wm.getNodeMemory( exists2n );
        BetaMemory eMem = ( BetaMemory )   wm.getNodeMemory( eNode );
        BetaMemory fMem = ( BetaMemory )   wm.getNodeMemory( fNode );
        BetaMemory gMem = ( BetaMemory )   wm.getNodeMemory( gNode );
        
        RiaNodeMemory riaMem1 = ( RiaNodeMemory ) wm.getNodeMemory( riaNode1 );
        RiaNodeMemory riaMem2 = ( RiaNodeMemory ) wm.getNodeMemory( riaNode2 );        
        
        PathMemory rs = (PathMemory) wm.getNodeMemory( rtn );
        
        assertFalse( rs.isRuleLinked() ); //E and F are not inserted yet, so rule is unlinked

        //---
        // assert a and b in same segment
        assertSame( liaMem.getSegmentMemory(), bMem.getSegmentMemory() );

        // exists1 and b not in same segment
        assertNotSame(  bMem.getSegmentMemory(), exists1Mem.getSegmentMemory() );

        // exists1 and b are in same segment
        assertSame(  exists1Mem.getSegmentMemory(), gMem.getSegmentMemory() );

        // check segment masks
        assertEquals( 2, rs.getSegmentMemories().length );
        assertEquals( 3, rs.getAllLinkedMaskTest() );
        assertEquals( 1, rs.getLinkedSegmentMask() );

        assertEquals( 3, liaMem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 1, liaMem.getNodePosMaskBit() );
        assertEquals( 2, bMem.getNodePosMaskBit() );

        assertEquals( 3, exists1Mem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 1, exists1Mem.getNodePosMaskBit() );
        assertEquals( 2, gMem.getNodePosMaskBit() );


        // assert c, d are in the same segment, and that this is the only segment in ria1 memory
        assertSame( dMem.getSegmentMemory(), cMem.getSegmentMemory() );

        // assert d and exists are not in the same segment
        assertNotSame(  exists2Mem.getSegmentMemory(), dMem.getSegmentMemory() );
        assertEquals( 3, riaMem1.getRiaPathMemory().getSegmentMemories().length );
        assertEquals( null, riaMem1.getRiaPathMemory().getSegmentMemories()[0] ); // only needs to know about segments in the subnetwork
        assertEquals( dMem.getSegmentMemory(), riaMem1.getRiaPathMemory().getSegmentMemories()[1] );
        assertEquals( 1, dMem.getSegmentMemory().getPathMemories().size() );
        assertSame( riaMem1.getRiaPathMemory(), cMem.getSegmentMemory().getPathMemories().get(0) );

        assertEquals( 3, cMem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 3, cMem.getSegmentMemory().getLinkedNodeMask() ); // E and F is not yet inserted, so bit is not set
        assertEquals( 1, cMem.getNodePosMaskBit() );
        assertEquals( 2, dMem.getNodePosMaskBit() );

        assertEquals( 0, exists2Mem.getNodePosMaskBit() );
        FactHandle fhE1 = wm.insert(  new E() ); // insert to lazy initialize exists2Mem segment
        FactHandle fhF1 = wm.insert(  new F() );
        assertEquals( 1, exists2Mem.getNodePosMaskBit() );
        assertEquals( 6, riaMem1.getRiaPathMemory().getAllLinkedMaskTest() ); // only cares that the segment for c, E and exists1 are set, ignores the outer first segment
        assertEquals( 6, riaMem1.getRiaPathMemory().getLinkedSegmentMask() ); // E and F are inerted, so 6
        wm.delete(fhE1);
        wm.delete(fhF1);
        assertEquals( 2, riaMem1.getRiaPathMemory().getLinkedSegmentMask() ); // E deleted

        // assert e, f are in the same segment, and that this is the only segment in ria2 memory
        assertNotNull( null, eMem.getSegmentMemory() ); //subnetworks are recursively created, so segment already exists
        assertSame( fMem.getSegmentMemory(), eMem.getSegmentMemory() );

        assertEquals( 3, riaMem2.getRiaPathMemory().getSegmentMemories().length );
        assertEquals( null, riaMem2.getRiaPathMemory().getSegmentMemories()[0] ); // only needs to know about segments in the subnetwork
        assertEquals( null, riaMem2.getRiaPathMemory().getSegmentMemories()[1] ); // only needs to know about segments in the subnetwork
        assertEquals( fMem.getSegmentMemory(), riaMem2.getRiaPathMemory().getSegmentMemories()[2] );
        assertSame( riaMem2.getRiaPathMemory(), eMem.getSegmentMemory().getPathMemories().get(0) );
        assertEquals( 3, eMem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 0, eMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 4, riaMem2.getRiaPathMemory().getAllLinkedMaskTest() ); // only cares that the segment for e and f set, ignores the outer two segment
        assertEquals( 0, riaMem2.getRiaPathMemory().getLinkedSegmentMask() ); // E and F is not yet inserted, so bit is not set

        fhE1 = wm.insert(  new E() );
        wm.insert(  new F() );

        assertTrue( rs.isRuleLinked() ); //E and F are now inserted yet, so rule is linked
        assertEquals( 3, rs.getAllLinkedMaskTest() );
        assertEquals( 3, rs.getLinkedSegmentMask() );

        // retest bits
        assertEquals( 3, cMem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 3, cMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 6, riaMem1.getRiaPathMemory().getAllLinkedMaskTest() );
        assertEquals( 6, riaMem1.getRiaPathMemory().getLinkedSegmentMask() );

        assertEquals( 3, eMem.getSegmentMemory().getAllLinkedMaskTest() );
        assertEquals( 3, eMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 4, riaMem2.getRiaPathMemory().getAllLinkedMaskTest() );
        assertEquals( 4, riaMem2.getRiaPathMemory().getLinkedSegmentMask() );

        wm.delete( fhE1 );

        // retest bits
        assertFalse( rs.isRuleLinked() );

        assertEquals( 3, cMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 2, riaMem1.getRiaPathMemory().getLinkedSegmentMask() );

        assertEquals( 2, eMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 0, riaMem2.getRiaPathMemory().getLinkedSegmentMask() );
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
