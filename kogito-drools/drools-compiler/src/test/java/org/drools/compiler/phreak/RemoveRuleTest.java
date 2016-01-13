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

package org.drools.compiler.phreak;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NodeMemories;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.*;

public class RemoveRuleTest {

    @Test
    public void testPopulatedSingleRuleNoSharing() throws Exception {
        KieBaseConfiguration kconf = ( KieBaseConfiguration ) KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newStatefulKnowledgeSession());

        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));

        wm.fireAllRules();


        kbase.addKnowledgePackages( buildKnowledgePackage("r1", "   A() B() C(object == 2) D() E()\n") );
        List list = new ArrayList();
        wm.setGlobal("list", list);

        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertNotNull(sm.getStagedLeftTuples().getInsertFirst());

        wm.fireAllRules();

        BetaMemory bMem = ( BetaMemory ) sm.getNodeMemories().get(1);
        assertEquals( 1, bMem.getLeftTupleMemory().size() );
        assertEquals( 1, bMem.getRightTupleMemory().size() );

        BetaMemory eMem = ( BetaMemory ) sm.getNodeMemories().get(4);
        assertEquals( 1, eMem.getLeftTupleMemory().size() );
        assertEquals( 1, eMem.getRightTupleMemory().size() );

        NodeMemories nms = wm.getNodeMemories();
        assertEquals( 12, countNodeMemories(nms));

        assertNull(sm.getStagedLeftTuples().getInsertFirst());
        assertEquals(1, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );

        kbase.removeRule("org.kie", "r1");

        assertEquals( 6, countNodeMemories(nms)); // still has OTN
    }

    @Test
    public void testPopulatedSingleRuleNoSharingWithSubnetworkAtStart() throws Exception {
        KieBaseConfiguration kconf = ( KieBaseConfiguration ) KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newStatefulKnowledgeSession());

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new D(1));
        wm.insert(new E(1));

        wm.insert(new C(2));
        wm.fireAllRules();


        kbase.addKnowledgePackages( buildKnowledgePackage("r1", "   A() not( B() and C() ) D() E()\n") );
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.fireAllRules();
        assertEquals(2, list.size() );
        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );

        kbase.removeRule("org.kie", "r1");
        wm.insert(new A(1));
        wm.fireAllRules();
        assertEquals(2, list.size() );
    }

    private int countNodeMemories(NodeMemories nms) {
        int count = 0;
        for ( int i = 0; i < nms.length(); i++ ) {
            if ( nms.peekNodeMemory(i) != null ) {
                System.out.println(nms.peekNodeMemory(i) );
                count++;
            }
        }
        return count;
    }

    @Test
    public void testPopulatedRuleMidwayShare() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B() C(1;) D() E()\n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();

        assertEquals( 7, countNodeMemories(wm.getNodeMemories()));

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a : A() B() C(2;) D() E()\n") );
        wm.fireAllRules();

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();

        JoinNode c1Node = (JoinNode) bNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode c2Node = (JoinNode) bNode.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory c1Mem = ( BetaMemory ) wm.getNodeMemory(c1Node);
        assertSame( sm.getFirst(), c1Mem.getSegmentMemory());
        assertEquals( 3, c1Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c1Mem.getRightTupleMemory().size() );

        BetaMemory c2Mem = ( BetaMemory ) wm.getNodeMemory(c2Node);
        SegmentMemory c2Smem =  sm.getFirst().getNext();
        assertSame( c2Smem, c2Mem.getSegmentMemory());
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertEquals(6, list.size() );


        kbase1.removeRule("org.kie", "r2");
        assertEquals( 10, countNodeMemories(wm.getNodeMemories()));

        assertNull( sm.getFirst());

        assertSame( sm, c1Mem.getSegmentMemory()); // c1SMem repoints back to original Smem

        wm.insert(new A(1));
        wm.fireAllRules();

        assertEquals( "r1", ((Match)list.get(6)).getRule().getName() );
        assertEquals(7, list.size() ); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedRuleWithEvals() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a:A() B() eval(1==1) eval(1==1) C(1;) \n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new D(1));
        wm.insert(new E(1));
        wm.fireAllRules();

        assertEquals( 7, countNodeMemories(wm.getNodeMemories()));

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a:A() B() eval(1==1) eval(1==1) C(2;) \n") );
        wm.fireAllRules();

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();

        EvalConditionNode e1 = (EvalConditionNode) bNode.getSinkPropagator().getFirstLeftTupleSink();
        EvalConditionNode e2 = (EvalConditionNode) e1.getSinkPropagator().getFirstLeftTupleSink();

        JoinNode c1Node = (JoinNode) e2.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode c2Node = (JoinNode) e2.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory c1Mem = ( BetaMemory ) wm.getNodeMemory(c1Node);
        assertSame( sm.getFirst(), c1Mem.getSegmentMemory());
        assertEquals( 3, c1Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c1Mem.getRightTupleMemory().size() );

        BetaMemory c2Mem = ( BetaMemory ) wm.getNodeMemory(c2Node);
        SegmentMemory c2Smem =  sm.getFirst().getNext();
        assertSame( c2Smem, c2Mem.getSegmentMemory());
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertEquals(6, list.size() );


        kbase1.removeRule("org.kie", "r2");
        assertEquals( 8, countNodeMemories(wm.getNodeMemories()));

        assertNull( sm.getFirst());

        assertSame( sm, c1Mem.getSegmentMemory()); // c1SMem repoints back to original Smem

        wm.insert(new A(1));
        wm.fireAllRules();

        assertEquals( "r1", ((Match)list.get(6)).getRule().getName() );
        assertEquals(7, list.size() ); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedSharedLiaNode() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B(1;) C() D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new B(2));
        wm.insert(new C(1));
        wm.insert(new D(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 3, list.size() );
        assertEquals( 7, countNodeMemories(wm.getNodeMemories()));

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a : A() B(2;) C() D() E()\n") );
        wm.fireAllRules();
        assertEquals( 17, countNodeMemories(wm.getNodeMemories()));

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode b1Node = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode b2Node = (JoinNode) liaNode.getSinkPropagator().getLastLeftTupleSink();
        JoinNode c1Node = (JoinNode) b1Node.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory b1Mem = ( BetaMemory ) wm.getNodeMemory(b1Node);
        assertSame( sm.getFirst(), b1Mem.getSegmentMemory());
        assertEquals( 3, b1Mem.getLeftTupleMemory().size() );
        assertEquals( 1, b1Mem.getRightTupleMemory().size() );

        BetaMemory b2Mem = ( BetaMemory ) wm.getNodeMemory(b2Node);
        SegmentMemory b2Smem =  sm.getFirst().getNext();
        assertSame( b2Smem, b2Mem.getSegmentMemory());
        assertEquals( 3, b2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, b2Mem.getRightTupleMemory().size() );
        assertEquals(6, list.size() );

        BetaMemory c1Mem = ( BetaMemory ) wm.getNodeMemory(c1Node);
        assertSame( b1Mem.getSegmentMemory(), c1Mem.getSegmentMemory() );
        assertNotSame(b1Mem.getSegmentMemory(), b2Mem.getSegmentMemory());

        wm.fireAllRules();
        assertEquals(6, list.size() );
        assertEquals( 17, countNodeMemories(wm.getNodeMemories()));

        kbase1.removeRule("org.kie", "r2");
        assertEquals( 12, countNodeMemories(wm.getNodeMemories()));

        assertSame( sm, b1Mem.getSegmentMemory());
        assertSame( sm, c1Mem.getSegmentMemory());
        assertNull(sm.getFirst());
        assertEquals( 3, b1Mem.getLeftTupleMemory().size() );
        assertEquals( 1, b1Mem.getRightTupleMemory().size() );

        //SegmentMemory b2Smem =  sm.getFirst().remove();
        assertSame( b2Smem, b2Mem.getSegmentMemory());

        wm.insert(new A(1));
        wm.fireAllRules();

        assertEquals( "r1", ((Match)list.get(6)).getRule().getName() );
        assertEquals(7, list.size() ); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedSharedLiaNodeNoBeta() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        A a1 = new A(1);
        InternalFactHandle fh1 = (InternalFactHandle) wm.insert(a1);
        A a2 = new A(2);
        InternalFactHandle fh2 = (InternalFactHandle) wm.insert(a2);

        wm.fireAllRules();
        assertEquals( 2, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a : A()\n") );
        wm.fireAllRules();
        assertEquals( 4, list.size() );

        kbase1.removeRule("org.kie", "r1");
        kbase1.removeRule("org.kie", "r2");
        list.clear();

        assertNull( fh1.getFirstLeftTuple() );
        assertNull( fh1.getLastLeftTuple() );
        assertNull( fh2.getFirstLeftTuple() );
        assertNull( fh2.getLastLeftTuple() );
        wm.update( fh1,a1 );
        wm.update( fh2,a2 );
        wm.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test
    public void testPopulatedSharedToRtn() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B() C() D() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new D(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( 7, countNodeMemories(wm.getNodeMemories()));

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A() B() C() D() E()\n") );
        wm.fireAllRules();
        assertEquals( 8, countNodeMemories(wm.getNodeMemories()));
        assertEquals(4, list.size() );

        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        RuleTerminalNode rtn2 = getRtn("org.kie.r2", kbase1);
        PathMemory pmem1 = ( PathMemory ) wm.getNodeMemory(rtn1);
        PathMemory pmem2 = ( PathMemory ) wm.getNodeMemory(rtn2);

        SegmentMemory[] smems1 = pmem1.getSegmentMemories();
        SegmentMemory[] smems2 = pmem2.getSegmentMemories();
        assertEquals(2, smems1.length );
        assertEquals(2, smems2.length );

        assertSame( smems1[0], smems2[0] );
        assertNotSame( smems1[1], smems2[1] );

        SegmentMemory sm = smems1[0];
        assertEquals( smems1[1], sm.getFirst() );

        JoinNode eNode1 = ( JoinNode ) rtn1.getLeftTupleSource();
        JoinNode eNode2 = ( JoinNode ) rtn2.getLeftTupleSource();
        assertSame( eNode1, eNode2 );

        pmem1 = ( PathMemory ) wm.getNodeMemory(rtn1);
        kbase1.removeRule("org.kie", "r2");
        System.out.println( "---" );
        assertEquals( 7, countNodeMemories(wm.getNodeMemories()));
        assertNull( sm.getFirst() );

        pmem1 = ( PathMemory ) wm.getNodeMemory(rtn1);
        smems1 = pmem1.getSegmentMemories();
        assertEquals(1, smems1.length );
        assertSame( sm, smems1[0]);

        wm.insert(new A(1));
        wm.fireAllRules();

        assertEquals( "r1", ((Match)list.get(4)).getRule().getName() );
        assertEquals(5, list.size() ); // only one more added, as second rule as removed

    }

    @Test
         public void testPopulatedMultipleSharesRemoveFirst() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) D() E()\n" );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        InternalFactHandle fh1 = ( InternalFactHandle ) wm.insert(new A(1));
        InternalFactHandle fh2 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh3 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh4 = ( InternalFactHandle ) wm.insert(new A(3));
        InternalFactHandle fh5 =  (InternalFactHandle) wm.insert(new B(1));
        InternalFactHandle fh6 =  (InternalFactHandle) wm.insert(new B(2));
        InternalFactHandle fh7 =  (InternalFactHandle) wm.insert(new C(1));
        InternalFactHandle fh8 =  (InternalFactHandle) wm.insert(new C(2));
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new D(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 2, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) D() E()\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) D() E()\n") );

        wm.fireAllRules();
        assertEquals( 5, list.size() );


        kbase1.removeRule("org.kie", "r1");
        list.clear();

        wm.update( fh1, fh1.getObject() );
        wm.update( fh2, fh2.getObject() );
        wm.update( fh3, fh3.getObject() );
        wm.update( fh4, fh4.getObject() );
        wm.update( fh5, fh5.getObject() );
        wm.update( fh6, fh6.getObject() );
        wm.update( fh7, fh7.getObject() );
        wm.update( fh8, fh8.getObject() );
        wm.update( fh9, fh9.getObject() );
        wm.update( fh10, fh10.getObject() );

        wm.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test
    public void testPopulatedMultipleSharesRemoveMid() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) D() E()\n" );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        InternalFactHandle fh1 = ( InternalFactHandle ) wm.insert(new A(1));
        InternalFactHandle fh2 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh3 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh4 = ( InternalFactHandle ) wm.insert(new A(3));
        InternalFactHandle fh5 =  (InternalFactHandle) wm.insert(new B(1));
        InternalFactHandle fh6 =  (InternalFactHandle) wm.insert(new B(2));
        InternalFactHandle fh7 =  (InternalFactHandle) wm.insert(new C(1));
        InternalFactHandle fh8 =  (InternalFactHandle) wm.insert(new C(2));
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new D(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 2, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) D() E()\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) D() E()\n") );

        wm.fireAllRules();
        assertEquals( 5, list.size() );


        kbase1.removeRule("org.kie", "r2");
        list.clear();

        wm.update( fh1, fh1.getObject() );
        wm.update( fh2, fh2.getObject() );
        wm.update( fh3, fh3.getObject() );
        wm.update( fh4, fh4.getObject() );
        wm.update( fh5, fh5.getObject() );
        wm.update( fh6, fh6.getObject() );
        wm.update( fh7, fh7.getObject() );
        wm.update( fh8, fh8.getObject() );
        wm.update( fh9, fh9.getObject() );
        wm.update( fh10, fh10.getObject() );

        wm.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test
    public void testPopulatedMultipleSharesRemoveLast() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) D() E()\n" );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        InternalFactHandle fh1 = ( InternalFactHandle ) wm.insert(new A(1));
        InternalFactHandle fh2 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh3 = ( InternalFactHandle ) wm.insert(new A(2));
        InternalFactHandle fh4 = ( InternalFactHandle ) wm.insert(new A(3));
        InternalFactHandle fh5 =  (InternalFactHandle) wm.insert(new B(1));
        InternalFactHandle fh6 =  (InternalFactHandle) wm.insert(new B(2));
        InternalFactHandle fh7 =  (InternalFactHandle) wm.insert(new C(1));
        InternalFactHandle fh8 =  (InternalFactHandle) wm.insert(new C(2));
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new D(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 2, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) D() E()\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) D() E()\n") );

        wm.fireAllRules();
        assertEquals( 5, list.size() );


        kbase1.removeRule("org.kie", "r3");
        list.clear();

        wm.update( fh1, fh1.getObject() );
        wm.update( fh2, fh2.getObject() );
        wm.update( fh3, fh3.getObject() );
        wm.update( fh4, fh4.getObject() );
        wm.update( fh5, fh5.getObject() );
        wm.update( fh6, fh6.getObject() );
        wm.update( fh7, fh7.getObject() );
        wm.update( fh8, fh8.getObject() );
        wm.update( fh9, fh9.getObject() );
        wm.update( fh10, fh10.getObject() );

        wm.fireAllRules();
        assertEquals( 4, list.size() );
    }

    @Test
    public void testPathMemorySizeAfterSegmentMerge() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;) B(1;)\n" );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)\n") );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        // trigger segment initialization
        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.fireAllRules();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );

        assertEquals( 2, wm.getNodeMemory(rtn1).getSegmentMemories().length );
        assertEquals( 2, wm.getNodeMemory(rtn2).getSegmentMemories().length );

        kbase1.removeRule("org.kie", "r2");
        assertEquals( 1, wm.getNodeMemory(rtn1).getSegmentMemories().length );
    }

    @Test
    public void testPathMemorySizeAfterSegmentMergeNonInitialized() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;) B(1;)\n" );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)\n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );

        assertEquals( 2, wm.getNodeMemory(rtn1).getSegmentMemories().length );
        assertEquals( 2, wm.getNodeMemory(rtn2).getSegmentMemories().length );

        kbase1.removeRule("org.kie", "r2");
        assertEquals( 1, wm.getNodeMemory(rtn1).getSegmentMemories().length );
    }

    @Test
    public void testSplitTwoBeforeCreatedSegment() throws Exception {
        KnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n" );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new E(1));
        wm.insert(new E(2));
        wm.flushPropagations();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );
        RuleTerminalNode rtn3 = getRtn( "org.kie.r3", kbase1 );
        RuleTerminalNode rtn4 = getRtn( "org.kie.r4", kbase1 );

        PathMemory pm1 = (PathMemory) wm.getNodeMemory(rtn1);
        SegmentMemory[] smems = pm1.getSegmentMemories();
        assertEquals(4, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
        assertNull( smems[3]);
        SegmentMemory sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 4, pm1.getLinkedSegmentMask() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

        smems = pm1.getSegmentMemories();
        assertEquals(5, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
        assertNull( smems[2]);

        sm = smems[3];
        assertEquals( 3, sm.getPos() );
        assertEquals( 8, sm.getSegmentPosMaskBit() );
        assertEquals( 8, pm1.getLinkedSegmentMask() );

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        smems = pm5.getSegmentMemories();
        assertEquals(2, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
    }

    private RuleTerminalNode getRtn(String ruleName, KnowledgeBase kbase) {
        return ( RuleTerminalNode ) ((KnowledgeBaseImpl) kbase).getReteooBuilder().getTerminalNodes(ruleName)[0];
    }

    private KnowledgeBase buildKnowledgeBase(String ruleName, String rule) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        int i = 0;
        str += "rule " + ruleName + "  when \n";
        str +=  rule;
        str += "then \n";
        str += " list.add( kcontext.getMatch() );\n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( RuleEngineOption.PHREAK );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private Collection<KnowledgePackage> buildKnowledgePackage(String ruleName, String rule) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + D.class.getCanonicalName() + "\n" ;
        str += "import " + E.class.getCanonicalName() + "\n" ;
        str += "global java.util.List list \n";

        int i = 0;
        str += "rule " + ruleName + "  when \n";
        str +=  rule;
        str += "then \n";
        str += " list.add( kcontext.getMatch() );\n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        return kbuilder.getKnowledgePackages();
    }

    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }
}
