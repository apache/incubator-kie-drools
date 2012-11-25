package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.MemoryFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.builder.conf.LRUnlinkingOption;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;

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

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
    @Ignore
    public void testSubNetworkSharingMemories() throws Exception {
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

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
        
        RuleMemory rs1 = ( RuleMemory ) wm.getNodeMemory( rtn1 );
        RuleMemory rs2 = ( RuleMemory )  wm.getNodeMemory( rtn2 );
        RuleMemory rs3 = ( RuleMemory )  wm.getNodeMemory( rtn3 );
        
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

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
        
        assertEquals( 4, existsBm.getSegmentMemory().getLinkedNodeMask() );
        
        wm.retract( fhd );
        assertEquals( 0, existsBm.getSegmentMemory().getLinkedNodeMask() );
        
        RuleMemory rs = ( RuleMemory) wm.getNodeMemory( rtn );
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

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
        
        RuleMemory rs = ( RuleMemory) wm.getNodeMemory( rtn );
        assertFalse( rs.isRuleLinked() );  
        
        FactHandle fhE1 = wm.insert(  new E() );
        FactHandle fhE2 = wm.insert(  new E() );
        assertTrue( rs.isRuleLinked() );
        
        wm.retract( fhE1 );
        assertTrue( rs.isRuleLinked() );
        
        wm.retract( fhE2 );
        assertFalse( rs.isRuleLinked() );        
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
