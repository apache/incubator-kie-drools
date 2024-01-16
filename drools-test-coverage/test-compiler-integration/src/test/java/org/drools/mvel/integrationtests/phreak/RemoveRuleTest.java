/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NodeMemories;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.rule.Match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.phreak.PhreakBuilder.isEagerSegmentCreation;

@RunWith(Parameterized.class)
public class RemoveRuleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RemoveRuleTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testPopulatedSingleRuleNoSharing() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());

        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.fireAllRules();


        kbase.addPackages( buildKnowledgePackage("r1", "   A() B() C(object == 2) X() E()\n") );
        List list = new ArrayList();
        wm.setGlobal("list", list);

        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNotNull();

        wm.fireAllRules();

        BetaMemory bMem = ( BetaMemory ) sm.getNodeMemories()[1];
        assertThat(bMem.getLeftTupleMemory().size()).isEqualTo(1);
        assertThat(bMem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory eMem = ( BetaMemory ) sm.getNodeMemories()[4];
        assertThat(eMem.getLeftTupleMemory().size()).isEqualTo(1);
        assertThat(eMem.getRightTupleMemory().size()).isEqualTo(1);

        NodeMemories nms = wm.getNodeMemories();
        assertThat(countNodeMemories(nms)).isEqualTo(6);

        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(1);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");

        kbase.removeRule("org.kie", "r1");

        assertThat(countNodeMemories(nms)).isEqualTo(0);
    }

    @Test
    public void testPopulatedSingleRuleNoSharingWithSubnetworkAtStart() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase.newKieSession());

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.insert(new C(2));
        wm.fireAllRules();


        kbase.addPackages( buildKnowledgePackage("r1", "   A() not( B() and C() ) X() E()\n") );
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");

        kbase.removeRule("org.kie", "r1");
        wm.insert(new A(1));
        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
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
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B() C(1;) X() E()\n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new X(1));
        wm.insert(new E(1));
        wm.fireAllRules();

        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B() C(2;) X() E()\n") );
        wm.fireAllRules();

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();

        JoinNode c1Node = (JoinNode) bNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode c2Node = (JoinNode) bNode.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory c1Mem = (BetaMemory) wm.getNodeMemory(c1Node);
        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm.getFirst());
        assertThat(c1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c1Mem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory c2Mem  = (BetaMemory) wm.getNodeMemory(c2Node);
        SegmentMemory  c2Smem =  sm.getFirst().getNext();
        assertThat(c2Mem.getSegmentMemory()).isSameAs(c2Smem);
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(6);


        kbase1.removeRule("org.kie", "r2");
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        assertThat(sm.getFirst()).isNull();

        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm); // c1SMem repoints back to original Smem

        wm.insert(new A(1));
        wm.fireAllRules();

        assertThat(((Match) list.get(6)).getRule().getName()).isEqualTo("r1");
        assertThat(list.size()).isEqualTo(7); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedRuleWithEvals() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a:A() B() eval(1==1) eval(1==1) C(1;) \n");

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new X(1));
        wm.insert(new E(1));
        wm.fireAllRules();

        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a:A() B() eval(1==1) eval(1==1) C(2;) \n") );
        wm.fireAllRules();

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();

        EvalConditionNode e1 = (EvalConditionNode) bNode.getSinkPropagator().getFirstLeftTupleSink();
        EvalConditionNode e2 = (EvalConditionNode) e1.getSinkPropagator().getFirstLeftTupleSink();

        JoinNode c1Node = (JoinNode) e2.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode c2Node = (JoinNode) e2.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory c1Mem = (BetaMemory) wm.getNodeMemory(c1Node);
        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm.getFirst());
        assertThat(c1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c1Mem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory c2Mem  = (BetaMemory) wm.getNodeMemory(c2Node);
        SegmentMemory  c2Smem =  sm.getFirst().getNext();
        assertThat(c2Mem.getSegmentMemory()).isSameAs(c2Smem);
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(6);


        kbase1.removeRule("org.kie", "r2");
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        assertThat(sm.getFirst()).isNull();

        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm); // c1SMem repoints back to original Smem

        wm.insert(new A(1));
        wm.fireAllRules();

        assertThat(((Match) list.get(6)).getRule().getName()).isEqualTo("r1");
        assertThat(list.size()).isEqualTo(7); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedSharedLiaNode() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B(1;) C() X() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new B(2));
        wm.insert(new C(1));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B(2;) C() X() E()\n") );
        wm.fireAllRules();
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(11);

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode b1Node = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode b2Node = (JoinNode) liaNode.getSinkPropagator().getLastLeftTupleSink();
        JoinNode c1Node = (JoinNode) b1Node.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory b1Mem = (BetaMemory) wm.getNodeMemory(b1Node);
        assertThat(b1Mem.getSegmentMemory()).isSameAs(sm.getFirst());
        assertThat(b1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(b1Mem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory b2Mem  = (BetaMemory) wm.getNodeMemory(b2Node);
        SegmentMemory  b2Smem =  sm.getFirst().getNext();
        assertThat(b2Mem.getSegmentMemory()).isSameAs(b2Smem);
        assertThat(b2Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(b2Mem.getRightTupleMemory().size()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(6);

        BetaMemory c1Mem = (BetaMemory) wm.getNodeMemory(c1Node);
        assertThat(c1Mem.getSegmentMemory()).isSameAs(b1Mem.getSegmentMemory());
        assertThat(b2Mem.getSegmentMemory()).isNotSameAs(b1Mem.getSegmentMemory());

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(6);
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(11);

        kbase1.removeRule("org.kie", "r2");
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        assertThat(b1Mem.getSegmentMemory()).isSameAs(sm);
        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm);
        assertThat(sm.getFirst()).isNull();
        assertThat(b1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(b1Mem.getRightTupleMemory().size()).isEqualTo(1);

        //SegmentMemory b2Smem =  sm.getFirst().remove();
        assertThat(b2Mem.getSegmentMemory()).isSameAs(b2Smem);

        wm.insert(new A(1));
        wm.fireAllRules();

        assertThat(((Match) list.get(6)).getRule().getName()).isEqualTo("r1");
        assertThat(list.size()).isEqualTo(7); // only one more added, as second rule as removed
    }

    @Test
    public void testPopulatedSharedLiaNodeNoBeta() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        A a1 = new A(1);
        InternalFactHandle fh1 = (InternalFactHandle) wm.insert(a1);
        A a2 = new A(2);
        InternalFactHandle fh2 = (InternalFactHandle) wm.insert(a2);

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A()\n") );
        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(4);

        kbase1.removeRule("org.kie", "r1");
        kbase1.removeRule("org.kie", "r2");
        list.clear();

        assertThat(fh1.getFirstLeftTuple()).isNull();
        assertThat(fh2.getFirstLeftTuple()).isNull();
        wm.update( fh1,a1 );
        wm.update( fh2,a2 );
        wm.fireAllRules();

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void testAlphaTerminalNodesDontShareWithLian() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", " A(1;)\n");
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;) B(1;) C(1;)\n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());

        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        RuleTerminalNode rtn2 = getRtn("org.kie.r2", kbase1);
        PathMemory pmem1 = wm.getNodeMemory(rtn1);
        PathMemory pmem2 = wm.getNodeMemory(rtn2);

        if (isEagerSegmentCreation()) {
            assertThat(pmem1.getPathEndNode().getSegmentPrototypes().length).isEqualTo(1);
            assertThat(pmem2.getPathEndNode().getSegmentPrototypes().length).isEqualTo(1);
        }
    }

    @Test
    public void testPopulatedSharedToRtn() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A() B() C() X() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);

        kbase1.addPackages( buildKnowledgePackage("r2", "   A() B() C() X() E()\n") );
        wm.fireAllRules();
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(7);
        assertThat(list.size()).isEqualTo(4);

        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        RuleTerminalNode rtn2 = getRtn("org.kie.r2", kbase1);
        PathMemory pmem1 = wm.getNodeMemory(rtn1);
        PathMemory pmem2 = wm.getNodeMemory(rtn2);

        SegmentMemory[] smems1 = pmem1.getSegmentMemories();
        SegmentMemory[] smems2 = pmem2.getSegmentMemories();
        assertThat(smems1.length).isEqualTo(2);
        assertThat(smems2.length).isEqualTo(2);

        assertThat(smems2[0]).isSameAs(smems1[0]);
        assertThat(smems2[1]).isNotSameAs(smems1[1]);

        SegmentMemory sm = smems1[0];
        assertThat(sm.getFirst()).isEqualTo(smems1[1]);

        JoinNode eNode1 = ( JoinNode ) rtn1.getLeftTupleSource();
        JoinNode eNode2 = ( JoinNode ) rtn2.getLeftTupleSource();
        assertThat(eNode2).isSameAs(eNode1);

        pmem1 = wm.getNodeMemory(rtn1);
        kbase1.removeRule("org.kie", "r2");
        System.out.println( "---" );
        assertThat(countNodeMemories(wm.getNodeMemories())).isEqualTo(6);
        assertThat(sm.getFirst()).isNull();

        pmem1 = wm.getNodeMemory(rtn1);
        smems1 = pmem1.getSegmentMemories();
        assertThat(smems1.length).isEqualTo(1);
        assertThat(smems1[0]).isSameAs(sm);

        wm.insert(new A(1));
        wm.fireAllRules();

        assertThat(((Match) list.get(4)).getRule().getName()).isEqualTo("r1");
        assertThat(list.size()).isEqualTo(5); // only one more added, as second rule as removed

    }

    @Test
         public void testPopulatedMultipleSharesRemoveFirst() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) X() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
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
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new X(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) X() E()\n") );
        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) X() E()\n") );

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(5);


        kbase1.removeRule("org.kie", "r1");
        list.clear();

        update10Facts(wm, fh1, fh2, fh3, fh4, fh5, fh6, fh7, fh8, fh9, fh10);

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void testPopulatedMultipleSharesRemoveMid() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) X() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
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
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new X(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) X() E()\n") );
        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) X() E()\n") );

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(5);


        kbase1.removeRule("org.kie", "r2");
        list.clear();

        update10Facts(wm, fh1, fh2, fh3, fh4, fh5, fh6, fh7, fh8, fh9, fh10);

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void testPopulatedMultipleSharesRemoveLast() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) X() E()\n");
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
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
        InternalFactHandle fh9 =  (InternalFactHandle) wm.insert(new X(1));
        InternalFactHandle fh10 =  (InternalFactHandle) wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
        RuleTerminalNode rtn1 = getRtn("org.kie.r1", kbase1);
        PathMemory pmem1 = wm.getNodeMemory(rtn1);

        assertThat(pmem1.getSegmentMemories().length).isEqualTo(1);
        assertSegmentMemory(pmem1, 0, 127, 127, 127);

        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) X() E()\n") );

        list.clear();
        update10Facts(wm, fh1, fh2, fh3, fh4, fh5, fh6, fh7, fh8, fh9, fh10);
        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(4);

        RuleTerminalNode rtn2 = getRtn("org.kie.r1", kbase1);
        PathMemory pmem2 = wm.getNodeMemory(rtn2);

        assertThat(pmem1.getSegmentMemories().length).isEqualTo(2);
        assertSegmentMemory(pmem1, 0, 15, 15, 15);
        assertSegmentMemory(pmem1, 1, 7, 7, 7);

        assertThat(pmem2.getSegmentMemories().length).isEqualTo(2);
        assertSegmentMemory(pmem2, 0, 15, 15, 15);
        assertSegmentMemory(pmem2, 1, 7, 7, 7);

        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) X() E()\n") );

        list.clear();
        update10Facts(wm, fh1, fh2, fh3, fh4, fh5, fh6, fh7, fh8, fh9, fh10);
        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(5);

        RuleTerminalNode rtn3 = getRtn("org.kie.r3", kbase1);
        PathMemory pmem3 = wm.getNodeMemory(rtn3);

        assertThat(pmem1.getSegmentMemories().length).isEqualTo(3);
        assertSegmentMemory(pmem1, 0, 1, 1, 1);
        assertSegmentMemory(pmem1, 1, 7, 7, 7);
        assertSegmentMemory(pmem1, 2, 7, 7, 7);

        assertThat(pmem2.getSegmentMemories().length).isEqualTo(3);
        assertSegmentMemory(pmem2, 0, 1, 1, 1);
        assertSegmentMemory(pmem2, 1, 7, 7, 7);
        assertSegmentMemory(pmem2, 2, 7, 7, 7);

        assertThat(pmem3.getSegmentMemories().length).isEqualTo(2);
        assertSegmentMemory(pmem3, 0, 1, 1, 1);
        assertSegmentMemory(pmem3, 1, 63, 63, 63);


        kbase1.removeRule("org.kie", "r3");
        list.clear();

        update10Facts(wm, fh1, fh2, fh3, fh4, fh5, fh6, fh7, fh8, fh9, fh10);
        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(4);

        assertThat(pmem1.getSegmentMemories().length).isEqualTo(2);
        assertSegmentMemory(pmem1, 0, 15, 15, 15);
        assertSegmentMemory(pmem1, 1, 7, 7, 7);

        assertThat(pmem2.getSegmentMemories().length).isEqualTo(2);
        assertSegmentMemory(pmem2, 0, 15, 15, 15);
        assertSegmentMemory(pmem2, 1, 7, 7, 7);
    }

    private void assertSegmentMemory(PathMemory pmem, int segmentPos, int linkedMask, int dirtyMask, int allMask) {
        if (isEagerSegmentCreation()) {
            assertThat(pmem.getSegmentMemories()[segmentPos].getLinkedNodeMask()).isEqualTo(linkedMask);
            assertThat(pmem.getSegmentMemories()[segmentPos].getDirtyNodeMask()).isEqualTo(dirtyMask);
            assertThat(pmem.getSegmentMemories()[segmentPos].getAllLinkedMaskTest()).isEqualTo(allMask);
        }
    }

    private static void update10Facts(InternalWorkingMemory wm, InternalFactHandle fh1, InternalFactHandle fh2, InternalFactHandle fh3, InternalFactHandle fh4, InternalFactHandle fh5, InternalFactHandle fh6, InternalFactHandle fh7, InternalFactHandle fh8, InternalFactHandle fh9, InternalFactHandle fh10) {
        wm.update(fh1, fh1.getObject());
        wm.update(fh2, fh2.getObject());
        wm.update(fh3, fh3.getObject());
        wm.update(fh4, fh4.getObject());
        wm.update(fh5, fh5.getObject());
        wm.update(fh6, fh6.getObject());
        wm.update(fh7, fh7.getObject());
        wm.update(fh8, fh8.getObject());
        wm.update(fh9, fh9.getObject());
        wm.update(fh10, fh10.getObject());
    }

    @Test
    public void testPathMemorySizeAfterSegmentMerge() throws Exception {
        // The two A(1;) are not actually shared, as r2 creates an AlphaTerminalNode
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;) B(1;)\n");
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)\n") );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        // trigger segment initialization
        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.fireAllRules();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );

        assertThat(wm.getNodeMemory(rtn1).getSegmentMemories().length).isEqualTo(1);
        assertThat(wm.getNodeMemory(rtn2).getSegmentMemories().length).isEqualTo(1);

        kbase1.removeRule("org.kie", "r2");
        assertThat(wm.getNodeMemory(rtn1).getSegmentMemories().length).isEqualTo(1);
    }

    @Test
    public void testPathMemorySizeAfterSegmentMergeNonInitialized() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;) B(1;)\n");
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)\n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );

        assertThat(wm.getNodeMemory(rtn1).getSegmentMemories().length).isEqualTo(1);
        assertThat(wm.getNodeMemory(rtn2).getSegmentMemories().length).isEqualTo(1);

        kbase1.removeRule("org.kie", "r2");
        assertThat(wm.getNodeMemory(rtn1).getSegmentMemories().length).isEqualTo(1);
    }

    @Test
    public void testSplitTwoBeforeCreatedSegment() throws Exception {
        InternalKnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n");
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new E(1));
        wm.insert(new E(2));
        wm.flushPropagations();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );
        RuleTerminalNode rtn3 = getRtn( "org.kie.r3", kbase1 );
        RuleTerminalNode rtn4 = getRtn( "org.kie.r4", kbase1 );

        PathMemory pm1 = wm.getNodeMemory(rtn1);
        SegmentMemory[] smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(4);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
        assertThat(smems[3]).isNull();
        SegmentMemory sm = smems[2];
        assertThat(sm.getPos()).isEqualTo(2);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(4);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(4);

        kbase1.addPackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

        smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(5);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
        assertThat(smems[2]).isNull();

        sm = smems[3];
        assertThat(sm.getPos()).isEqualTo(3);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(8);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(8);

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = wm.getNodeMemory(rtn5);

        if (isEagerSegmentCreation()) {
            assertThat(pm5.getPathEndNode().getSegmentPrototypes().length).isEqualTo(2);
        }
    }

    private RuleTerminalNode getRtn(String ruleName, InternalKnowledgeBase kbase) {
        return ( RuleTerminalNode ) kbase.getReteooBuilder().getTerminalNodes(ruleName)[0];
    }

    private String buildKnowledgePackageDrl(String ruleName, String rule) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n";
        str += "import " + B.class.getCanonicalName() + "\n";
        str += "import " + C.class.getCanonicalName() + "\n";
        str += "import " + X.class.getCanonicalName() + "\n";
        str += "import " + E.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";

        str += "rule " + ruleName + "  when \n";
        str += rule;
        str += "then \n";
        str += " list.add( kcontext.getMatch() );\n";
        str += "end \n";

        return str;
    }

    private String addRule(String ruleName, String rule) {
        String str = "";

        str += "rule " + ruleName + "  when \n";
        str += rule;
        str += "then \n";
        str += " list.add( kcontext.getMatch() );\n";
        str += "end \n";

        return str;
    }

    private InternalKnowledgeBase buildKnowledgeBase(String ruleName, String rule) {
        return (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, buildKnowledgePackageDrl(ruleName, rule));
    }

    private Collection<KiePackage> buildKnowledgePackage(String ruleName, String rule) {
        return KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, buildKnowledgePackageDrl(ruleName, rule)).getKiePackages();
    }

    public ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((InternalRuleBase)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }
}
