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

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LinkingTest {
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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

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
        wm.flushPropagations();

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
        wm.flushPropagations();

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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

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
        wm.flushPropagations();

        assertEquals( 1, existsBm.getSegmentMemory().getLinkedNodeMask() );  // exists is start of new segment

        wm.retract( fhd );
        wm.flushPropagations();
        assertEquals( 0, existsBm.getSegmentMemory().getLinkedNodeMask() );

        PathMemory rs = (PathMemory) wm.getNodeMemory( rtn );
        assertFalse( rs.isRuleLinked() );

        wm.insert(  new A() );
        wm.flushPropagations();
        assertFalse(rs.isRuleLinked());
        wm.insert(new B());
        wm.flushPropagations();
        assertFalse(rs.isRuleLinked());
        wm.insert(new E());
        wm.flushPropagations();
        assertFalse( rs.isRuleLinked() );

        wm.insert(  new D() );
        wm.flushPropagations();
        assertTrue( rs.isRuleLinked() );

        wm.retract(  fhc );
        wm.flushPropagations();
        assertFalse( rs.isRuleLinked() );

        wm.insert(  new C() );
        wm.flushPropagations();
        assertTrue( rs.isRuleLinked() );
    }

    @Test
    public void testNonReactiveSubNetworkInShareMasks() throws Exception {
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
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 2, liaNode.getSinkPropagator().size() );

        JoinNode bNode = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];
        ExistsNode exists1n = ( ExistsNode) liaNode.getSinkPropagator().getSinks()[1];
        EvalConditionNode evalNode = ( EvalConditionNode) exists1n.getSinkPropagator().getSinks()[0];
        ExistsNode exists2n = ( ExistsNode) exists1n.getSinkPropagator().getSinks()[1];
        JoinNode dNode = ( JoinNode) exists2n.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) dNode.getSinkPropagator().getSinks()[0];

        wm.insert(  new A() );

        PathMemory pmem =  ( PathMemory ) wm.getNodeMemory(rtn);
        assertEquals( 3, pmem.getSegmentMemories().length );
        assertEquals( 7, pmem.getAllLinkedMaskTest() ); // D is in the exists segment


        BetaMemory bm =  ( BetaMemory ) wm.getNodeMemory(dNode);
        assertNull(bm.getSegmentMemory()); // check lazy initialization
        wm.insert(new D());
        wm.flushPropagations();
        assertEquals(2, bm.getSegmentMemory().getAllLinkedMaskTest()); // only D can be linked in
    }

    @Test
    public void testNonReactiveSubNetworkOwnSegmentMasks() throws Exception {
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
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   D() \n";
        str += "then \n";
        str += "end \n";
        str += "rule rule2 when \n";
        str += "   A() \n";
        str += "   exists( B() and C() ) \n";
        str += "   exists( eval(1==1) ) \n";
        str += "   E() \n";
        str += "then \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 2, liaNode.getSinkPropagator().size() );

        JoinNode bNode = ( JoinNode) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];
        ExistsNode exists1n = ( ExistsNode) liaNode.getSinkPropagator().getSinks()[1];
        EvalConditionNode evalNode = ( EvalConditionNode) exists1n.getSinkPropagator().getSinks()[0];
        ExistsNode exists2n = ( ExistsNode) exists1n.getSinkPropagator().getSinks()[1];
        JoinNode dNode = ( JoinNode) exists2n.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode) dNode.getSinkPropagator().getSinks()[0];

        wm.insert(  new A() );

        PathMemory pmem =  ( PathMemory ) wm.getNodeMemory(rtn);
        assertEquals( 4, pmem.getSegmentMemories().length );
        assertEquals( 11, pmem.getAllLinkedMaskTest() ); // the exists eval segment does not need to be linked in

        RightInputAdapterNode.RiaNodeMemory riaMem =  (RightInputAdapterNode.RiaNodeMemory) wm.getNodeMemory((MemoryFactory) exists1n.getRightInput());
        assertEquals( 2, riaMem.getRiaPathMemory().getAllLinkedMaskTest() ); // second segment must be linked in

        wm.insert(  new B() );
        wm.insert(  new C() );
        assertEquals( 2, riaMem.getRiaPathMemory().getSegmentMemories().length );

        riaMem =  (RightInputAdapterNode.RiaNodeMemory) wm.getNodeMemory((MemoryFactory) exists2n.getRightInput());
        assertEquals( 0, riaMem.getRiaPathMemory().getAllLinkedMaskTest() ); // no segments to be linked in
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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

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
        wm.flushPropagations();
        assertTrue( rs.isRuleLinked() );

        wm.retract( fhE1 );
        wm.flushPropagations();
        assertTrue( rs.isRuleLinked() );

        wm.retract( fhE2 );
        wm.flushPropagations();
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
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

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
        wm.flushPropagations();

        LiaNodeMemory liaMem = ( LiaNodeMemory ) wm.getNodeMemory( liaNode );
        BetaMemory bMem = ( BetaMemory )   wm.getNodeMemory( bNode );
        BetaMemory exists1Mem = ( BetaMemory ) wm.getNodeMemory( exists1n );
        BetaMemory cMem = ( BetaMemory )   wm.getNodeMemory( cNode );
        BetaMemory dMem = ( BetaMemory )   wm.getNodeMemory( dNode );
        BetaMemory exists2Mem = ( BetaMemory ) wm.getNodeMemory( exists2n );
        BetaMemory eMem = ( BetaMemory )   wm.getNodeMemory( eNode );
        BetaMemory fMem = ( BetaMemory )   wm.getNodeMemory( fNode );
        BetaMemory gMem = ( BetaMemory )   wm.getNodeMemory( gNode );

        RightInputAdapterNode.RiaNodeMemory riaMem1 = (RightInputAdapterNode.RiaNodeMemory) wm.getNodeMemory( riaNode1 );
        RightInputAdapterNode.RiaNodeMemory riaMem2 = (RightInputAdapterNode.RiaNodeMemory) wm.getNodeMemory( riaNode2 );

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
        wm.flushPropagations();

        assertEquals( 1, exists2Mem.getNodePosMaskBit() );
        assertEquals( 6, riaMem1.getRiaPathMemory().getAllLinkedMaskTest() ); // only cares that the segment for c, E and exists1 are set, ignores the outer first segment
        assertEquals( 6, riaMem1.getRiaPathMemory().getLinkedSegmentMask() ); // E and F are inerted, so 6
        wm.delete(fhE1);
        wm.delete(fhF1);
        wm.flushPropagations();
        assertEquals(2, riaMem1.getRiaPathMemory().getLinkedSegmentMask()); // E deleted

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
        wm.flushPropagations();

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

        wm.delete( fhE1);
        wm.flushPropagations();

        // retest bits
        assertFalse( rs.isRuleLinked() );

        assertEquals( 3, cMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 2, riaMem1.getRiaPathMemory().getLinkedSegmentMask() );

        assertEquals( 2, eMem.getSegmentMemory().getLinkedNodeMask() );
        assertEquals( 0, riaMem2.getRiaPathMemory().getLinkedSegmentMask() );
    }
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, B.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, C.class );

        InternalWorkingMemory wm = (InternalWorkingMemory)kbase.newStatefulKnowledgeSession();
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
        wm.flushPropagations();
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        JoinNode bNode = ( JoinNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        LiaNodeMemory amem = ( LiaNodeMemory ) wm.getNodeMemory( aNode );
        BetaMemory bmem = ( BetaMemory ) wm.getNodeMemory( bNode );
        BetaMemory cmem = ( BetaMemory ) wm.getNodeMemory( cNode );
        
        // amem.getSegmentMemory().getStagedLeftTuples().insertSize() == 3
        assertNotNull( amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst() );
        assertNotNull( amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNotNull( amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNull( amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext() );

        //assertEquals( 3, bmem.getStagedRightTuples().insertSize() );
        assertNotNull( bmem.getStagedRightTuples().getInsertFirst() );
        assertNotNull( bmem.getStagedRightTuples().getInsertFirst().getStagedNext() );
        assertNotNull( bmem.getStagedRightTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNull( bmem.getStagedRightTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        
        assertNull( amem.getSegmentMemory().getStagedLeftTuples().getInsertFirst() );
        assertNull( bmem.getStagedRightTuples().getInsertFirst() );
        assertNull( cmem.getStagedRightTuples().getInsertFirst() );
        
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );


        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new A(i) );
        }        
        
        for ( int i = 0; i < 3; i++ ) {
            wm.insert(  new C(i) );
        }         
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        NotNode bNode = ( NotNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        SegmentUtilities.createSegmentMemory( cNode, wm );
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
        wm.retract( fhb);
        wm.flushPropagations();
        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );
        wm.fireAllRules();
        assertEquals( 1, list.size() ); 
        
        // Now try with lots of facthandles on NotNode
        
        list.clear();
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for ( int i = 0; i < 5; i++ ) {
            handles.add(  wm.insert(  new B() ) );
        }
        wm.fireAllRules();
        assertEquals( 0, list.size() );
        
        assertEquals( 5, amem.getSegmentMemory().getLinkedNodeMask() );
        for ( FactHandle fh : handles ) {
            wm.retract( fh );
        }
        wm.flushPropagations();

        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        LeftInputAdapterNode aNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];                        
        NotNode bNode = ( NotNode) aNode.getSinkPropagator().getSinks()[0];        
        JoinNode cNode = ( JoinNode) bNode.getSinkPropagator().getSinks()[0];                
        
        SegmentUtilities.createSegmentMemory( cNode, wm );
        LiaNodeMemory amem = ( LiaNodeMemory ) wm.getNodeMemory( aNode );  
        
        // Only NotNode is linked in
        assertEquals( 2, amem.getSegmentMemory().getLinkedNodeMask() );
        
        FactHandle fha = wm.insert(  new A() );
        FactHandle fhb = wm.insert(  new B(1) );
        FactHandle fhc = wm.insert(  new C() );
        wm.flushPropagations();

        // All nodes are linked in
        assertEquals( 7, amem.getSegmentMemory().getLinkedNodeMask() );
        
        // NotNode does not unlink, due to variable constraint
        wm.retract( fhb);
        wm.flushPropagations();
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 1, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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
    public void testNotNodeMasksWithConstraints() throws Exception {
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
        str += "   not( B( value == $a.value ) ) \n";
        str += "   C() \n";
        str += "then \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 1, liaNode.getSinkPropagator().size() );

        wm.insert( new A() );
        wm.flushPropagations();

        NotNode notNode = ( NotNode) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode) notNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) cNode.getSinkPropagator().getSinks()[0];

        PathMemory pmem =  ( PathMemory ) wm.getNodeMemory(rtn);
        assertEquals( 1, pmem.getSegmentMemories().length );
        assertEquals( 1, pmem.getAllLinkedMaskTest() );

        SegmentMemory sm = pmem.getSegmentMemories()[0];
        assertEquals( 5, sm.getAllLinkedMaskTest() );

        assertEquals( 3, sm.getLinkedNodeMask() );
        assertFalse( sm.isSegmentLinked() );
        assertFalse( pmem.isRuleLinked() );

        wm.insert( new C());
        wm.flushPropagations();
        assertEquals( 7, sm.getLinkedNodeMask() );  // only 5 is needed to link, the 'not' turns on but it has no unfleunce either way
        assertTrue( sm.isSegmentLinked() );
        assertTrue( pmem.isRuleLinked() );
    }

    @Test
    public void testNotNodeMasksWithoutConstraints() throws Exception {
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
        str += "   not( B( ) ) \n";
        str += "   C() \n";
        str += "then \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode node = getObjectTypeNode(kbase, A.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        assertEquals( 1, liaNode.getSinkPropagator().size() );

        wm.insert( new A() );
        wm.flushPropagations();

        NotNode notNode = ( NotNode) liaNode.getSinkPropagator().getSinks()[0];
        JoinNode cNode = ( JoinNode) notNode.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) cNode.getSinkPropagator().getSinks()[0];

        PathMemory pmem =  ( PathMemory ) wm.getNodeMemory(rtn);
        assertEquals( 1, pmem.getSegmentMemories().length );
        assertEquals( 1, pmem.getAllLinkedMaskTest() );

        SegmentMemory sm = pmem.getSegmentMemories()[0];
        assertEquals( 7, sm.getAllLinkedMaskTest() );

        assertEquals( 3, sm.getLinkedNodeMask() );
        assertFalse( sm.isSegmentLinked() );
        assertFalse( pmem.isRuleLinked() );

        wm.insert( new C() );
        wm.flushPropagations();
        assertEquals( 7, sm.getLinkedNodeMask() );
        assertTrue( sm.isSegmentLinked() );
        assertTrue( pmem.isRuleLinked() );
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
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
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 1, list.size() );
        
        wm = kbase.newStatefulKnowledgeSession();
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.fireAllRules();        
        assertEquals( 0, list.size() );
        
        wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        
        wm.insert( new A() );
        wm.insert( new B() );
        for ( int i = 0; i < 28; i++ ) {
            wm.insert( new C() );
        }
        wm.insert( new D() );
        wm.flushPropagations();

        InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();
        InternalAgendaGroup group = (InternalAgendaGroup) agenda.getNextFocus();
        AgendaItem item = (AgendaItem) group.remove();
        int count = ((RuleAgendaItem)item).getRuleExecutor().evaluateNetworkAndFire(wm, null, 0, -1);
        //assertEquals(3, count );
        
        wm.fireAllRules();
        assertEquals( 1, list.size() );             
        
        wm.fireAllRules();
        assertEquals( 1, list.size() ); // check it doesn't double fire        
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

        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode botn = getObjectTypeNode(kbase, A.class );
        ObjectTypeNode cotn = getObjectTypeNode(kbase, A.class );

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
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
        
//        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
//        InternalAgendaGroup group = (InternalAgendaGroup) agenda.getNextFocus();
//        AgendaItem item = (AgendaItem) group.remove();
//        int count = ((RuleAgendaItem)item).evaluateNetworkAndFire( wm );
//        //assertEquals(7, count ); // proves we correctly track nested sub network staged propagations
//                
//        agenda.addActivation( item, true );
//        agenda = ( DefaultAgenda ) wm.getAgenda();
//        group = (InternalAgendaGroup) agenda.getNextFocus();
//        item = (AgendaItem) group.remove();
//        
//        agenda.fireActivation( item );
//        assertEquals( 1, list.size() );        
//        
//        agenda = ( DefaultAgenda ) wm.getAgenda();
//        group = (InternalAgendaGroup) agenda.getNextFocus();
//        item = (AgendaItem) group.remove();
//        count = ((RuleAgendaItem)item).evaluateNetworkAndFire( wm );
//        //assertEquals(0, count );        
        
        wm.fireAllRules();
        assertEquals( 1, list.size() );
        
        wm.fireAllRules();
        assertEquals( 1, list.size() );         
    }      
    
    public static ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }


}

