/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.common.InternalWorkingMemory;
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

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.*;

public class AddRuleTest {

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
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertEquals(1, sm.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals(0, sm.getStagedLeftTuples().insertSize());
        assertEquals(1, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
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

        ObjectTypeNode aotn = getObjectTypeNode(kbase, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];

        LiaNodeMemory lm = ( LiaNodeMemory ) wm.getNodeMemory(liaNode);
        SegmentMemory sm = lm.getSegmentMemory();
        assertEquals(0, sm.getStagedLeftTuples().insertSize());

        SegmentMemory subSm = sm.getFirst();
        SegmentMemory mainSm = subSm.getNext();


        assertEquals(2, subSm.getStagedLeftTuples().insertSize());
        assertEquals(2, mainSm.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals(0, subSm.getStagedLeftTuples().insertSize());
        assertEquals(0, mainSm.getStagedLeftTuples().insertSize());
        assertEquals(2, list.size() );

        assertEquals( "r1", ((Match)list.get(0)).getRule().getName() );
    }

    @Test
    public void testPopulatedRuleMidwayShare() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   a : A() B() C(1;) D() E()\n");
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
        assertEquals( 3, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a : A() B() C(2;) D() E()\n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
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
        assertEquals(3, c2Smem.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertEquals( 0, c2Smem.getStagedLeftTuples().insertSize());
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

        wm.fireAllRules();
        assertEquals( 3, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a:A() B() eval(1==1) eval(1==1) C(2;) \n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
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
        assertEquals(3, c2Smem.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals( 3, c2Mem.getLeftTupleMemory().size() );
        assertEquals( 1, c2Mem.getRightTupleMemory().size() );
        assertEquals( 0, c2Smem.getStagedLeftTuples().insertSize());
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

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   a : A() B(2;) C() D() E()\n") );

        ObjectTypeNode aotn = getObjectTypeNode(kbase1, A.class );
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) aotn.getSinkPropagator().getSinks()[0];
        JoinNode bNode1 = (JoinNode) liaNode.getSinkPropagator().getFirstLeftTupleSink();
        JoinNode bNode2 = (JoinNode) liaNode.getSinkPropagator().getLastLeftTupleSink();

        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory(bNode2);
        SegmentMemory sm = bm.getSegmentMemory();
        assertEquals(3, sm.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals(0, sm.getStagedLeftTuples().insertSize());
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

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A() B() C() D() E()\n") );

        ObjectTypeNode eotn = getObjectTypeNode(kbase1, E.class );
        JoinNode eNode = (JoinNode) eotn.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = ( RuleTerminalNode ) eNode.getSinkPropagator().getLastLeftTupleSink();

        PathMemory pm = (PathMemory) wm.getNodeMemory(rtn);
        SegmentMemory sm = pm.getSegmentMemory();
        assertEquals(2, sm.getStagedLeftTuples().insertSize());

        wm.fireAllRules();
        assertEquals(0, sm.getStagedLeftTuples().insertSize());
        assertEquals(4, list.size() );

        System.out.println( list );

        assertEquals("r1", ((Match) list.get(0)).getRule().getName());
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(2)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
    }

    @Test
    public void testPopulatedMultipleShares() throws Exception {
        KnowledgeBase kbase1 = buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) D() E()\n" );
        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
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
        wm.insert(new D(1));
        wm.insert(new E(1));

        wm.fireAllRules();
        assertEquals( 2, list.size() );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(2;) D() E()\n") );

        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(3;) B(1;) B(2;) C(2;) D() E()\n") );


        wm.fireAllRules();
        assertEquals( 5, list.size() );

        assertEquals("r1", ((Match) list.get(0)).getRule().getName());
        assertEquals( "r1", ((Match)list.get(1)).getRule().getName() );
        assertEquals( "r3", ((Match)list.get(2)).getRule().getName() ); // only one A3
        assertEquals( "r2", ((Match)list.get(3)).getRule().getName() );
        assertEquals( "r2", ((Match)list.get(4)).getRule().getName() );
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


    @Test
    public void testSplitOneBeforeCreatedSegment() throws Exception {
        KnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n" );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new D(1));
        wm.insert(new D(2));
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

        kbase1.addKnowledgePackages( buildKnowledgePackage("r5",  "   A(1;)  A(2;) B(1;) B(2;) \n") );

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

    @Test
    public void testSplitOnCreatedSegment() throws Exception {
        KnowledgeBase kbase1 =          buildKnowledgeBase("r1", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n" );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r2", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;) E(1;) E(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r3", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(2;)\n") );
        kbase1.addKnowledgePackages( buildKnowledgePackage("r4", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) \n") );

        InternalWorkingMemory wm = ((InternalWorkingMemory)kbase1.newStatefulKnowledgeSession());
        List list = new ArrayList();
        wm.setGlobal("list", list);

        wm.insert(new D(1));
        wm.insert(new D(2));
        wm.flushPropagations();

        RuleTerminalNode rtn1 = getRtn( "org.kie.r1", kbase1 );

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

        kbase1.addKnowledgePackages( buildKnowledgePackage("r5", "   A(1;)  A(2;) B(1;) B(2;) C(1;) C(2;) D(1;) D(3;)\n") );

        smems = pm1.getSegmentMemories();
        assertEquals(5, smems.length);
        assertNull( smems[0]);
        assertNull( smems[3]);
        assertNull( smems[4]);
        sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );
        assertEquals( 6, pm1.getLinkedSegmentMask() );

        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 6, pm1.getLinkedSegmentMask() );

        RuleTerminalNode rtn5 = getRtn( "org.kie.r5", kbase1 );
        PathMemory pm5 = (PathMemory) wm.getNodeMemory(rtn5);
        smems = pm5.getSegmentMemories();
        assertEquals(3, smems.length);
        assertNull( smems[0]);
        sm = smems[1];
        assertEquals( 1, sm.getPos() );
        assertEquals( 2, sm.getSegmentPosMaskBit() );
        assertEquals( 6, pm5.getLinkedSegmentMask() );
        sm = smems[2];
        assertEquals( 2, sm.getPos() );
        assertEquals( 4, sm.getSegmentPosMaskBit() );
        assertEquals( 6, pm5.getLinkedSegmentMask() );
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
