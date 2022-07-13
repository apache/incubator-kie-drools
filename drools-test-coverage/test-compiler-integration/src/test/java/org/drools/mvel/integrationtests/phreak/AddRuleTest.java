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

package org.drools.mvel.integrationtests.phreak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.spi.Tuple;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.Match;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class AddRuleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AddRuleTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testPopulatedSingleRuleNoSharing() {
        KieServices ks = KieServices.get();
        KieContainer kieContainer = KieUtil.getKieContainerFromDrls(kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO);
        InternalWorkingMemory wm  = (InternalWorkingMemory) kieContainer.newKieSession();

        wm.insert(new A(1));
        wm.insert(new B(1));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.fireAllRules();

        String rule = buildKnowledgePackageDrl("r1", "   A() B() C(object == 2) X() E()\n");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "testPopulatedSingleRuleNoSharing", "2.0.0");

        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), rule);

        kieContainer.updateToVersion(releaseId2);

        List<Match> list = new ArrayList<>();
        wm.setGlobal("list", list);

        ObjectTypeNode aotn = getObjectTypeNode(wm.getKnowledgeBase().getRete(), A.class);
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNotNull();

        wm.fireAllRules();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(1);

        assertThat(list.get(0).getRule().getName()).isEqualTo("r1");
    }

    @Test
    public void testPopulatedSingleRuleNoSharingWithSubnetworkAtStart() throws Exception {
        KieServices ks = KieServices.get();
        KieContainer kieContainer = KieUtil.getKieContainerFromDrls(kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO);
        InternalWorkingMemory wm  = (InternalWorkingMemory) kieContainer.newKieSession();

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.insert(new C(2));
        wm.fireAllRules();


        String rule = buildKnowledgePackageDrl("r1", "   A() not( B() and C() ) X() E()\n");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "testPopulatedSingleRuleNoSharingWithSubnetworkAtStart", "2.0.0");

        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                     new HashMap<>(), rule);
        kieContainer.updateToVersion(releaseId2);

        List list = new ArrayList();
        wm.setGlobal("list", list);

        ObjectTypeNode aotn = getObjectTypeNode(wm.getKnowledgeBase().getRete(), A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNull();

        SegmentMemory subSm = sm.getFirst();
        SegmentMemory mainSm = subSm.getNext();


        assertThat(subSm.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) subSm.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple)subSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNull();
        assertThat(mainSm.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple)mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNull();

        wm.fireAllRules();
        assertThat(subSm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(mainSm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(2);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
    }

    @Test
    public void testPopulatedRuleMidwayShare() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a : A() B() C(1;) X() E()\n");
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
        assertThat(list.size()).isEqualTo(3);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B() C(2;) X() E()\n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();

        JoinNode c1Node = (JoinNode) bNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode c2Node = (JoinNode) bNode.getSinkPropagator().getLastLeftTupleSink();

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();

        BetaMemory c1Mem = ( BetaMemory ) wm.getNodeMemory(c1Node);
        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm.getFirst());
        assertThat(c1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c1Mem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory c2Mem = ( BetaMemory ) wm.getNodeMemory(c2Node);
        SegmentMemory c2Smem =  sm.getFirst().getNext();
        assertThat(c2Mem.getSegmentMemory()).isSameAs(c2Smem);
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(0);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(0);
        assertThat(c2Smem.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext()).isNull();

        wm.fireAllRules();
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(1);
        assertThat(c2Smem.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(6);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(2)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(3)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(3)).getDeclarationValue("a")).getObject()).isEqualTo(3);
        assertThat(((Match) list.get(4)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(4)).getDeclarationValue("a")).getObject()).isEqualTo(2);
        assertThat(((Match) list.get(5)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(5)).getDeclarationValue("a")).getObject()).isEqualTo(1);
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

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(3);

        kbase1.addPackages( buildKnowledgePackage("r2", "   a:A() B() eval(1==1) eval(1==1) C(2;) \n") );

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
        assertThat(c1Mem.getSegmentMemory()).isSameAs(sm.getFirst());
        assertThat(c1Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c1Mem.getRightTupleMemory().size()).isEqualTo(1);

        BetaMemory c2Mem = ( BetaMemory ) wm.getNodeMemory(c2Node);
        SegmentMemory c2Smem =  sm.getFirst().getNext();
        assertThat(c2Mem.getSegmentMemory()).isSameAs(c2Smem);
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(0);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(0);
        assertThat(c2Smem.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNotNull();
        assertThat((Tuple) c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext()).isNull();

        wm.fireAllRules();
        assertThat(c2Mem.getLeftTupleMemory().size()).isEqualTo(3);
        assertThat(c2Mem.getRightTupleMemory().size()).isEqualTo(1);
        assertThat(c2Smem.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(6);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(2)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(3)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(3)).getDeclarationValue("a")).getObject()).isEqualTo(3);
        assertThat(((Match) list.get(4)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(4)).getDeclarationValue("a")).getObject()).isEqualTo(2);
        assertThat(((Match) list.get(5)).getRule().getName()).isEqualTo("r2");
        assertThat(((A) ((Match) list.get(5)).getDeclarationValue("a")).getObject()).isEqualTo(1);
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

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B(2;) C() X() E()\n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode1 = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode bNode2 = (JoinNode) liaNode.getSinkPropagator().getLastLeftTupleSink();

        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory(bNode2);
        SegmentMemory sm = bm.getSegmentMemory();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) sm.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple) sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNotNull();
        assertThat((Tuple) sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext()).isNull();

        wm.fireAllRules();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(6);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(2)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(3)).getRule().getName()).isEqualTo("r2");
        assertThat(((Match) list.get(4)).getRule().getName()).isEqualTo("r2");
        assertThat(((Match) list.get(5)).getRule().getName()).isEqualTo("r2");

        List results = new ArrayList();
        results.add(((A)((Match)list.get(3)).getDeclarationValue("a")).getObject());
        results.add(((A)((Match)list.get(4)).getDeclarationValue("a")).getObject());
        results.add(((A)((Match)list.get(5)).getDeclarationValue("a")).getObject());
        assertThat(results.containsAll(asList(1, 2, 3))).isTrue();
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

        kbase1.addPackages( buildKnowledgePackage("r2", "   A() B() C() X() E()\n") );

        ObjectTypeNode eotn = getObjectTypeNode(kbase1, E.class );
        JoinNode eNode = (JoinNode) eotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) eNode.getSinkPropagator().getLastLeftTupleSink();

        PathMemory pm = (PathMemory) wm.getNodeMemory(rtn);
        SegmentMemory sm = pm.getSegmentMemory();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNotNull();
        assertThat((Tuple) sm.getStagedLeftTuples().getInsertFirst().getStagedNext()).isNotNull();
        assertThat((Tuple) sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext()).isNull();

        wm.fireAllRules();
        assertThat(sm.getStagedLeftTuples().getInsertFirst()).isNull();
        assertThat(list.size()).isEqualTo(4);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(2)).getRule().getName()).isEqualTo("r2");
        assertThat(((Match) list.get(3)).getRule().getName()).isEqualTo("r2");
    }

    @Test
    public void testPopulatedMultipleShares() throws Exception {
        InternalKnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) X() E()\n" );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new A(1));
        wm.insert(new A(2));
        wm.insert(new A(2));
        wm.insert(new A(3));
        wm.insert(new B(1));
        wm.insert(new B(2));
        wm.insert(new C(1));
        wm.insert(new C(2));
        wm.insert(new X(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) X() E()\n") );

        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) X() E()\n") );


        wm.fireAllRules();
        System.out.println(list);
        assertThat(list.size()).isEqualTo(5);

        assertThat(((Match) list.get(0)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(1)).getRule().getName()).isEqualTo("r1");
        assertThat(((Match) list.get(2)).getRule().getName()).isEqualTo("r3"); // only one A3
        assertThat(((Match) list.get(3)).getRule().getName()).isEqualTo("r2");
        assertThat(((Match) list.get(4)).getRule().getName()).isEqualTo("r2");
    }

    @Test
    public void testSplitTwoBeforeCreatedSegment() throws Exception {
        InternalKnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n" );
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

        PathMemory pm1 = (PathMemory) wm.getNodeMemory(rtn1);
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
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        smems = pm5.getSegmentMemories();
        assertThat(smems.length).isEqualTo(2);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
    }


    @Test
    public void testSplitOneBeforeCreatedSegment() throws Exception {
        InternalKnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n" );
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new X(1));
        wm.insert(new X(2));
        wm.flushPropagations();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );
        RuleTerminalNode rtn2 = getRtn( "org.kie.r2", kbase1 );
        RuleTerminalNode rtn3 = getRtn( "org.kie.r3", kbase1 );
        RuleTerminalNode rtn4 = getRtn( "org.kie.r4", kbase1 );

        PathMemory pm1 = (PathMemory) wm.getNodeMemory(rtn1);
        SegmentMemory[] smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(4);
        assertThat(smems[0]).isNull();
        assertThat(smems[2]).isNull();
        assertThat(smems[3]).isNull();
        SegmentMemory sm = smems[1];
        assertThat(sm.getPos()).isEqualTo(1);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(2);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(2);

        PathMemory pm3 = (PathMemory) wm.getNodeMemory(rtn3);
        SegmentMemory[] smemsP3 = pm3.getSegmentMemories();
        assertThat(smemsP3.length).isEqualTo(3);
        assertThat(smemsP3[0]).isNull();
        assertThat(smemsP3[2]).isNull();
        sm = smems[1];
        assertThat(sm.getPos()).isEqualTo(1);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(2);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(2);

        kbase1.addPackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

        smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(5);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
        assertThat(smems[3]).isNull();
        assertThat(smems[4]).isNull();
        sm = smems[2];
        assertThat(sm.getPos()).isEqualTo(2);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(4);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(4);

        smems = pm3.getSegmentMemories();
        assertThat(smems.length).isEqualTo(4);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
        assertThat(smems[3]).isNull();
        sm = smems[2];
        assertThat(sm.getPos()).isEqualTo(2);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(4);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(4);

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        smems = pm5.getSegmentMemories();
        assertThat(smems.length).isEqualTo(2);
        assertThat(smems[0]).isNull();
        assertThat(smems[1]).isNull();
    }

    @Test
    public void testSplitOnCreatedSegment() throws Exception {
        // this test splits D1 and D2 on the later add rule
        InternalKnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n" );
        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;) E(1;) E(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(2;)\n") );
        kbase1.addPackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newKieSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new X(1));
        wm.insert(new X(2));
        wm.insert(new X(3));
        wm.flushPropagations();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );

        PathMemory pm1 = (PathMemory) wm.getNodeMemory(rtn1);
        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(2);
        SegmentMemory[] smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(4);
        assertThat(smems[0]).isNull();
        assertThat(smems[2]).isNull();
        assertThat(smems[3]).isNull();
        SegmentMemory sm = smems[1];
        assertThat(sm.getPos()).isEqualTo(1);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(2);


        kbase1.addPackages( buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(3;)\n") );
        wm.fireAllRules();

        assertThat(pm1.getLinkedSegmentMask()).isEqualTo(6);
        smems = pm1.getSegmentMemories();
        assertThat(smems.length).isEqualTo(5);
        assertThat(smems[0]).isNull();
        assertThat(smems[3]).isNull();
        assertThat(smems[4]).isNull();
        sm = smems[1];
        assertThat(sm.getPos()).isEqualTo(1);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(2);

        sm = smems[2];
        assertThat(sm.getPos()).isEqualTo(2);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(4);

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        assertThat(pm5.getLinkedSegmentMask()).isEqualTo(6);

        smems = pm5.getSegmentMemories();
        assertThat(smems.length).isEqualTo(3);
        assertThat(smems[0]).isNull();
        sm = smems[1];
        assertThat(sm.getPos()).isEqualTo(1);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(2);

        sm = smems[2];
        assertThat(sm.getPos()).isEqualTo(2);
        assertThat(sm.getSegmentPosMaskBit()).isEqualTo(4);
    }


    private RuleTerminalNode getRtn(String ruleName, KieBase kbase) {
        return ( RuleTerminalNode ) ((KnowledgeBaseImpl) kbase).getReteooBuilder().getTerminalNodes(ruleName)[0];
    }

    private InternalKnowledgeBase buildKnowledgeBase(String ruleName, String rule) {
        return (InternalKnowledgeBase)KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, buildKnowledgePackageDrl(ruleName, rule));
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

    private Collection<KiePackage> buildKnowledgePackage(String ruleName, String rule) {
        return KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, buildKnowledgePackageDrl(ruleName, rule)).getKiePackages();
    }

    public ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> nodeClass) {
        return getObjectTypeNode(((KnowledgeBaseImpl) kbase).getRete(), nodeClass);
    }

    public ObjectTypeNode getObjectTypeNode(Rete rete, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = rete.getObjectTypeNodes();
        for (ObjectTypeNode n : nodes) {
            if (n.getObjectType().getClassType() == nodeClass) {
                return n;
            }
        }
        return null;
    }
}
