package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.FactHandle;
import org.drools.base.ClassObjectType;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalRuleBase;
import org.drools.common.RuleNetworkEvaluatorActivation;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.integrationtests.SubNetworkLinkingTest.A;
import org.drools.integrationtests.SubNetworkLinkingTest.B;
import org.drools.integrationtests.SubNetworkLinkingTest.C;
import org.drools.integrationtests.SubNetworkLinkingTest.D;
import org.drools.integrationtests.SubNetworkLinkingTest.E;
import org.drools.integrationtests.SubNetworkLinkingTest.F;
import org.drools.integrationtests.SubNetworkLinkingTest.G;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.RuleTerminalNode;
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

public class LinkingTest {
    
    @Test
    public void testJoinNodes() throws Exception {
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
        str += "   $a : A() \n";
        str += "   $b : B() \n";
        str += "   $c : C() \n";
        str += "then \n";
        str += "  list.add( $a.getValue() + \":\"+ $b.getValue() + \":\" + $c.getValue() ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new B(i) );
        }
        
        for ( int i = 0; i < 29; i++ ) {
            wm.insert(  new C(i) );
        }        
        //StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        JoinNode bNode = ( JoinNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        LiaNodeMemory amem = ( LiaNodeMemory ) wm.getNodeMemory( aNode );
        BetaMemory bmem = ( BetaMemory ) wm.getNodeMemory( bNode );
        BetaMemory cmem = ( BetaMemory ) wm.getNodeMemory( cNode );
        
        assertEquals( 3, amem.getStagedLeftTupleList().size() );
        assertEquals( 3, bmem.getStagedAssertRightTupleList().size() );
        assertEquals( 29, cmem.getStagedAssertRightTupleList().size() );
        
        wm.fireAllRules();
        
        assertEquals( 0, amem.getStagedLeftTupleList().size() );
        assertEquals( 0, bmem.getStagedAssertRightTupleList().size() );
        assertEquals( 0, cmem.getStagedAssertRightTupleList().size() );        
        
        assertEquals( 261, list.size() );
        
        assertTrue( list.contains( "2:2:14") );
        assertTrue( list.contains( "1:0:6") );
        assertTrue( list.contains( "0:1:1") );
        assertTrue( list.contains( "2:2:14") );
        assertTrue( list.contains( "0:0:25") );    
    }    
    
    @Test
    public void testExistsNodes1() throws Exception {
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
        str += "   exists A() \n";
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        FactHandle fh = wm.insert( new A(1) );
        wm.fireAllRules();        
        assertEquals( 1, list.size() );  
        
        wm.retract( fh );
        wm.fireAllRules();        
        assertEquals( 1, list.size() );        
    }      
    
    @Test
    public void testExistsNodes2() throws Exception {
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
        str += "   $a : A() \n";
        str += "   exists B() \n";
        str += "   $c : C() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }        
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new C(i) );
        }         
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }       
        
        FactHandle fh = wm.insert(  new B(1) );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new C(i) );
        }      
        wm.fireAllRules();        
        assertEquals( 9, list.size() );        
        
        wm.retract( fh );
        wm.fireAllRules();        
        assertEquals( 9, list.size() );            
    }   
    
    @Test
    public void testNotNodeUnlinksWithNoConstriants() {
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
        str += "   $a : A() \n";
        str += "   not B() \n";
        str += "   $c : C() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        NotNode bNode = ( NotNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        BetaNode.createNodeSegmentMemory( cNode, wm );
        LiaNodeMemory amem = ( LiaNodeMemory ) wm.getNodeMemory( aNode );  
        
        // Only NotNode is linked in
        assertEquals( 2, amem.getSegmentMemory().getLinkedNodeMask() );
        
        FactHandle fha = wm.insert(  new A() );
        FactHandle fhb = wm.insert(  new B() );
        FactHandle fhc = wm.insert(  new C() );        
        wm.fireAllRules();
        assertEquals( 0, list.size() );
        
        // NotNode unlinks, which is allowed because it has no variable constraints
        assertEquals( 5, amem.getSegmentMemory().getLinkedNodeMask() );
        
        
        // NotNode links back in again, which is allowed because it has no variable constraints
        wm.retract( fhb );
        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );
        wm.fireAllRules();
        assertEquals( 1, list.size() ); 
        
        // Now try with lots of facthandles on NotNode
        
        list.clear();
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for ( int i = 0; i < 1; i++ ) {
            handles.add(  wm.insert(  new B() ) );
        }
        wm.fireAllRules();
        assertEquals( 0, list.size() );
        
        for ( FactHandle fh : handles ) {
            wm.retract( fh );
        }
        wm.fireAllRules();
        assertEquals( 1, list.size() );        
    }
    
    @Test
    public void testNotNodeDoesNotUnlinksWithConstriants() {
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
        str += "   $a : A() \n";
        str += "   not B( value == $a.value ) \n";
        str += "   $c : C() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        NotNode bNode = ( NotNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        BetaNode.createNodeSegmentMemory( cNode, wm );
        LiaNodeMemory amem = ( LiaNodeMemory ) wm.getNodeMemory( aNode );  
        
        // Only NotNode is linked in
        assertEquals( 2, amem.getSegmentMemory().getLinkedNodeMask() );
        
        FactHandle fha = wm.insert(  new A() );
        FactHandle fhb = wm.insert(  new B(1) );
        FactHandle fhc = wm.insert(  new C() );
        
        // All nodes are linked in
        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );
        
        // NotNode does not unlink, due to variable constraint
        wm.retract( fhb );
        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );        
    }    
    
    @Test
    public void testNotNodes1() throws Exception {
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
        str += "   not A() \n";
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 1, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        FactHandle fh = wm.insert( new A(1) );
        wm.fireAllRules();        
        assertEquals( 0, list.size() );  
        
        wm.retract( fh );
        wm.fireAllRules();        
        assertEquals( 1, list.size() );        
    }  
    
    
    @Test
    public void testNotNodes2() throws Exception {
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
        str += "   $a : A() \n";
        str += "   not B() \n";
        str += "   $c : C() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }        
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new C(i) );
        }         
        
        wm.fireAllRules();        
        assertEquals( 9, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }       
        
        FactHandle fh = wm.insert(  new B(1) );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new C(i) );
        }      
        wm.fireAllRules();        
        assertEquals( 0, list.size() );        
        
        wm.retract( fh );
        wm.fireAllRules();        
        assertEquals( 9, list.size() );            
    }      
    
    @Test
    public void testForallNodes() throws Exception {
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
        str += "   $a : A() \n";
        str += "   forall( B() )\n";
        str += "   $c : C() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
//        wm.setGlobal( "list", list );
//        
//        for ( int i = 0; i < 3; i++ ) {
//            wm.insert(  new A(i) );
//        }        
//        
//        wm.insert(  new B(2) );
//        
//        for ( int i = 0; i < 3; i++ ) {
//            wm.insert(  new C(i) );
//        }         
//        
//        wm.fireAllRules();        
//        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 2; i++ ) {
            wm.insert(  new A(i) );
        }       
                
        for ( int i = 0; i < 27; i++ ) {
            wm.insert(  new B(1) );
        }                
        
        for ( int i = 0; i < 2; i++ ) {
            wm.insert(  new C(i) );
        }      
        wm.fireAllRules();        
        assertEquals( 4, list.size() );        
        
//        wm.retract( fh );
//        wm.fireAllRules();        
//        assertEquals( 9, list.size() );            
    }      
    
    @Test
    public void testAccumulateNodes1() throws Exception {
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
        str += "   accumulate( $a : A(); $l : collectList( $a ) ) \n";
        str += "then \n";
        str += "  list.add( $l.size() ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 1, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        FactHandle fh1 = wm.insert( new A(1) );
        FactHandle fh2 = wm.insert( new A(2) );
        FactHandle fh3 = wm.insert( new A(3) );
        FactHandle fh4 = wm.insert( new A(4) );
        wm.fireAllRules();        
        assertEquals( 4, list.get(0));        
    }       
    
    @Test
    public void testAccumulateNodes2() throws Exception {
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
        str += "   accumulate( $a : B(); $l : collectList( $a ) ) \n";
        str += "   C() \n";        
        str += "then \n";
        str += "  list.add( $l.size() ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        //kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        list = new ArrayList();
        wm.setGlobal( "list", list );
        
        FactHandle fh1 = wm.insert( new B(1) );
        FactHandle fh2 = wm.insert( new B(2) );
        FactHandle fh3 = wm.insert( new B(3) );
        FactHandle fh4 = wm.insert( new B(4) );
        
        FactHandle fha = wm.insert( new A(1) );
        FactHandle fhc = wm.insert( new C(1) );
        wm.fireAllRules();        
        assertEquals( 4, list.get(0));        
    } 
    
    
    @Test
    public void testSubnetwork() throws Exception {
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
        str += "   $a : A() \n";
        str += "   exists ( B() and C() ) \n";
        str += "   $e : D() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.insert( new A() );
        wm.insert( new B() );
        for ( int i = 0; i < 28; i++ ) {
            wm.insert( new C() );
        }
        wm.insert( new D() );
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        InternalAgendaGroup group = (InternalAgendaGroup) agenda.getNextFocus();
        AgendaItem item = (AgendaItem) group.getNext();
        int count = ((RuleNetworkEvaluatorActivation)item).evaluateNetwork( wm );
        assertEquals(3, count );
                
        agenda.addActivation( item, true );
        agenda = ( DefaultAgenda ) wm.getAgenda();
        group = (InternalAgendaGroup) agenda.getNextFocus();
        item = (AgendaItem) group.getNext();
        
        agenda.fireActivation( item );
        assertEquals( 1, list.size() );        
        
        agenda = ( DefaultAgenda ) wm.getAgenda();
        group = (InternalAgendaGroup) agenda.getNextFocus();
        item = (AgendaItem) group.getNext();
        count = ((RuleNetworkEvaluatorActivation)item).evaluateNetwork( wm );
        assertEquals(0, count );        
        
        wm.fireAllRules();
        assertEquals( 1, list.size() );        
    }
    
    @Test
    public void testNestedSubnetwork() throws Exception {
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
        str += "   $a : A() \n";
        str += "   exists ( B() and exists( C() and D() ) and E() ) \n";
        str += "   $f : F() \n";        
        str += "then \n";
        str += "  list.add( 'x' ); \n";
        str += "end \n";                  
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.insert( new A() );
        wm.insert( new B() );
        for ( int i = 0; i < 28; i++ ) {
            wm.insert( new C() );
        }
        for ( int i = 0; i < 29; i++ ) {
            wm.insert( new D() );
        }
        wm.insert( new E() );
        wm.insert( new F() );
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        InternalAgendaGroup group = (InternalAgendaGroup) agenda.getNextFocus();
        AgendaItem item = (AgendaItem) group.getNext();
        int count = ((RuleNetworkEvaluatorActivation)item).evaluateNetwork( wm );
        assertEquals(7, count ); // proves we correctly track nested sub network staged propagations
                
        agenda.addActivation( item, true );
        agenda = ( DefaultAgenda ) wm.getAgenda();
        group = (InternalAgendaGroup) agenda.getNextFocus();
        item = (AgendaItem) group.getNext();
        
        agenda.fireActivation( item );
        assertEquals( 1, list.size() );        
        
        agenda = ( DefaultAgenda ) wm.getAgenda();
        group = (InternalAgendaGroup) agenda.getNextFocus();
        item = (AgendaItem) group.getNext();
        count = ((RuleNetworkEvaluatorActivation)item).evaluateNetwork( wm );
        assertEquals(0, count );        
        
        wm.fireAllRules();
        assertEquals( 1, list.size() );        
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

