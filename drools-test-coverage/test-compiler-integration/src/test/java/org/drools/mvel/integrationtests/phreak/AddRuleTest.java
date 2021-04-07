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
import org.drools.core.impl.KnowledgeBaseFactory;
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
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
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
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        assertNotNull(sm.getStagedLeftTuples().getInsertFirst());

        wm.fireAllRules();
        assertNull(sm.getStagedLeftTuples().getInsertFirst());
        assertEquals(1, list.size());

        assertEquals("r1", list.get(0).getRule().getName());
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
        assertNull( sm.getStagedLeftTuples().getInsertFirst() );

        SegmentMemory subSm = sm.getFirst();
        SegmentMemory mainSm = subSm.getNext();


        assertNotNull( subSm.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( subSm.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNull( subSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNotNull( mainSm.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNull( mainSm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        assertNull(subSm.getStagedLeftTuples().getInsertFirst());
        assertNull(mainSm.getStagedLeftTuples().getInsertFirst());
        assertEquals(2, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 3, list.size() );

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B() C(2;) X() E()\n") );

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
        assertEquals( 0, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 0, c2Mem.getRightTupleMemory().size() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertNull(c2Smem.getStagedLeftTuples().getInsertFirst());
        assertEquals(6, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(2)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
        assertEquals( 3, ((A)((Match)list.get(3)).getDeclarationValue("a")).getObject() );
        assertEquals( "r2", ((Match)list.get(4)).getRule().getName() );
        assertEquals( 2, ((A)((Match)list.get(4)).getDeclarationValue("a")).getObject() );
        assertEquals( "r2", ((Match)list.get(5)).getRule().getName() );
        assertEquals( 1, ((A)((Match)list.get(5)).getDeclarationValue("a")).getObject() );
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 3, list.size() );

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
        assertSame( sm.getFirst(), c1Mem.getSegmentMemory());
        assertEquals( 3, c1Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c1Mem.getRightTupleMemory().size() );

        BetaMemory c2Mem = ( BetaMemory ) wm.getNodeMemory(c2Node);
        SegmentMemory c2Smem =  sm.getFirst().getNext();
        assertSame( c2Smem, c2Mem.getSegmentMemory());
        assertEquals( 0, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 0, c2Mem.getRightTupleMemory().size() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNotNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNull( c2Smem.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertNull( c2Smem.getStagedLeftTuples().getInsertFirst() );
        assertEquals(6, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(2)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
        assertEquals( 3, ((A)((Match)list.get(3)).getDeclarationValue("a")).getObject() );
        assertEquals( "r2", ((Match)list.get(4)).getRule().getName() );
        assertEquals( 2, ((A)((Match)list.get(4)).getDeclarationValue("a")).getObject() );
        assertEquals( "r2", ((Match)list.get(5)).getRule().getName() );
        assertEquals( 1, ((A)((Match)list.get(5)).getDeclarationValue("a")).getObject() );
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 3, list.size() );

        kbase1.addPackages( buildKnowledgePackage("r2", "   a : A() B(2;) C() X() E()\n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getObjectSinkPropagator().getSinks()[0];
        JoinNode bNode1 = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode bNode2 = (JoinNode) liaNode.getSinkPropagator().getLastLeftTupleSink();

        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory(bNode2);
        SegmentMemory sm = bm.getSegmentMemory();
        assertNotNull( sm.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( sm.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNotNull( sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );
        assertNull( sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        assertNull( sm.getStagedLeftTuples().getInsertFirst() );
        assertEquals(6, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r1", ((Match)list.get(2)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(4)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(5)).getRule().getName() );

        List results = new ArrayList();
        results.add(((A)((Match)list.get(3)).getDeclarationValue("a")).getObject());
        results.add(((A)((Match)list.get(4)).getDeclarationValue("a")).getObject());
        results.add(((A)((Match)list.get(5)).getDeclarationValue("a")).getObject());
        assertTrue(results.containsAll(asList(1, 2, 3)));
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 2, list.size() );

        kbase1.addPackages( buildKnowledgePackage("r2", "   A() B() C() X() E()\n") );

        ObjectTypeNode eotn = getObjectTypeNode(kbase1, E.class );
        JoinNode eNode = (JoinNode) eotn.getObjectSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) eNode.getSinkPropagator().getLastLeftTupleSink();

        PathMemory pm = (PathMemory) wm.getNodeMemory(rtn);
        SegmentMemory sm = pm.getSegmentMemory();
        assertNotNull( sm.getStagedLeftTuples().getInsertFirst() );
        assertNotNull( sm.getStagedLeftTuples().getInsertFirst().getStagedNext() );
        assertNull( sm.getStagedLeftTuples().getInsertFirst().getStagedNext().getStagedNext() );

        wm.fireAllRules();
        assertNull( sm.getStagedLeftTuples().getInsertFirst() );
        assertEquals(4, list.size() );

        assertEquals("r1", ((Match) list.get(0)).getRule().getName());
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(2)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 2, list.size() );

        kbase1.addPackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) X() E()\n") );

        kbase1.addPackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) X() E()\n") );


        wm.fireAllRules();
        System.out.println(list);
        assertEquals( 5, list.size() );

        assertEquals("r1", ((Match) list.get(0)).getRule().getName());
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r3", ((Match)list.get(2)).getRule().getName() ); // only one A3
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(4)).getRule().getName() );
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals(4, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
        assertNull( smems[3]);
        SegmentMemory sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 4, pm1.getLinkedSegmentMask() );

        kbase1.addPackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

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


    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals(4, smems.length);
        assertNull( smems[0]);
        assertNull( smems[2]);
        assertNull( smems[3]);
        SegmentMemory sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );
        assertEquals( 2, pm1.getLinkedSegmentMask() );

        PathMemory pm3 = (PathMemory) wm.getNodeMemory(rtn3);
        SegmentMemory[] smemsP3 = pm3.getSegmentMemories();
        assertEquals(3, smemsP3.length);
        assertNull( smemsP3[0]);
        assertNull( smemsP3[2]);
        sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );
        assertEquals( 2, pm1.getLinkedSegmentMask() );

        kbase1.addPackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

        smems = pm1.getSegmentMemories();
        assertEquals(5, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
        assertNull( smems[3]);
        assertNull( smems[4]);
        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 4, pm1.getLinkedSegmentMask() );

        smems = pm3.getSegmentMemories();
        assertEquals(4, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
        assertNull( smems[3]);
        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 4, pm1.getLinkedSegmentMask() );

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        smems = pm5.getSegmentMemories();
        assertEquals(2, smems.length);
        assertNull( smems[0]);
        assertNull( smems[1]);
    }

    // TODO: EM Not really using the exec model, need to migrate those to incremental compilation
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
        assertEquals( 2, pm1.getLinkedSegmentMask() );
        SegmentMemory[] smems = pm1.getSegmentMemories();
        assertEquals(4, smems.length);
        assertNull( smems[0]);
        assertNull( smems[2]);
        assertNull( smems[3]);
        SegmentMemory sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );


        kbase1.addPackages( buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) X(1;) X(3;)\n") );
        wm.fireAllRules();

        assertEquals( 6, pm1.getLinkedSegmentMask() );
        smems = pm1.getSegmentMemories();
        assertEquals(5, smems.length);
        assertNull( smems[0]);
        assertNull( smems[3]);
        assertNull( smems[4]);
        sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );

        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        assertEquals( 6, pm5.getLinkedSegmentMask() );

        smems = pm5.getSegmentMemories();
        assertEquals(3, smems.length);
        assertNull( smems[0]);
        sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );

        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
    }


    private RuleTerminalNode getRtn(String ruleName, KieBase kbase) {
        return ( RuleTerminalNode ) ((KnowledgeBaseImpl) kbase).getReteooBuilder().getTerminalNodes(ruleName)[0];
    }

    private InternalKnowledgeBase buildKnowledgeBase(String ruleName, String rule) {
        String str = "";
        str += "package org.kie \n";
        str += "import " + A.class.getCanonicalName() + "\n" ;
        str += "import " + B.class.getCanonicalName() + "\n" ;
        str += "import " + C.class.getCanonicalName() + "\n" ;
        str += "import " + X.class.getCanonicalName() + "\n" ;
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

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        return kbase;
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

        int i = 0;
        str += "rule " + ruleName + "  when \n";
        str += rule;
        str += "then \n";
        str += " list.add( kcontext.getMatch() );\n";
        str += "end \n";

        return str;
    }

    private Collection<KiePackage> buildKnowledgePackage(String ruleName, String rule) {
        String str = buildKnowledgePackageDrl(ruleName, rule);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(), kbuilder.hasErrors() );

        return kbuilder.getKnowledgePackages();
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
