/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableDemand;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class KOptMoveTest {

    @Test
    public void doMove3OptWith3Chains() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);
        TestdataChainedEntity c2 = new TestdataChainedEntity("c2", c1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0, c0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1, b2, c1, c2));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);

        KOptMove move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b0, c1});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, c2);
        SelectorTestUtils.assertChain(b0, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, b1, b2);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);

        // To tail value
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b2, c2});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, c2);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);
    }

    @Test
    public void doMove3OptWithOnly2Chains() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, b1, b2, b3));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        KOptMove move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a4, new Object[]{a1, b2});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // Same move, different order
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b2, a3});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // Same move, yet another order
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, b3, new Object[]{a3, a1});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // These moves would create a loop
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{a3, b2});
        assertEquals(false, move.isMoveDoable(scoreDirector));
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a4, new Object[]{b2, a1});
        assertEquals(false, move.isMoveDoable(scoreDirector));
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, b3, new Object[]{a1, a3});
        assertEquals(false, move.isMoveDoable(scoreDirector));
    }

    @Test @Ignore("Valid 1 chain moves aren't supported yet") // TODO
    public void doMove3OptWithOnly1Chain() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, a5, a6));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        KOptMove move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a6, new Object[]{a3, a1});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // Same move, different order
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a4, new Object[]{a1, a5});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // Same move, yet another order
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{a5, a3});
        assertEquals(true, move.isMoveDoable(scoreDirector));
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertEquals(true, undoMove.isMoveDoable(scoreDirector));
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // These moves would create a loop
        // TODO
    }

    @Test
    public void toStringTest() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);
        TestdataChainedEntity c2 = new TestdataChainedEntity("c2", c1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0, c0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1, b2, c1, c2));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        assertEquals("a2 {a1} -kOpt-> b1 {b0} -kOpt-> c2 {c1}", new KOptMove(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b0, c1}).toString());
        assertEquals("a2 {a1} -kOpt-> null {b2} -kOpt-> null {c2}", new KOptMove(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b2, c2}).toString());
    }

}
