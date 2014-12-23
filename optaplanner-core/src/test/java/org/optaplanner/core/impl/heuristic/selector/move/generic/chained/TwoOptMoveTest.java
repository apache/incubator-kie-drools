/*
 * Copyright 2014 JBoss Inc
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

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableDemand;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TwoOptMoveTest {

    @Test
    public void isMoveDoable() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        scoreDirector.setWorkingSolution(solution);
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        assertEquals(true, new TwoOptMove(variableDescriptor, anchorVariableSupply, a2, b1).isMoveDoable(scoreDirector));
        assertEquals(true, new TwoOptMove(variableDescriptor, anchorVariableSupply, b1, a2).isMoveDoable(scoreDirector));
        assertEquals(true, new TwoOptMove(variableDescriptor, anchorVariableSupply, a1, a3).isMoveDoable(scoreDirector));
        assertEquals(true, new TwoOptMove(variableDescriptor, anchorVariableSupply, a3, a1).isMoveDoable(scoreDirector));
        assertEquals(false, new TwoOptMove(variableDescriptor, anchorVariableSupply, a1, a2).isMoveDoable(scoreDirector));
        assertEquals(false, new TwoOptMove(variableDescriptor, anchorVariableSupply, a2, a1).isMoveDoable(scoreDirector));

    }

    @Test
    public void doMove() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        scoreDirector.setWorkingSolution(solution);
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);

        TwoOptMove move = new TwoOptMove(variableDescriptor, anchorVariableSupply, a2, b1);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, b1);
        SelectorTestUtils.assertChain(b0, a2, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void doMoveInSameChain() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, a5));

        scoreDirector.setWorkingSolution(solution);
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);

        TwoOptMove move = new TwoOptMove(variableDescriptor, anchorVariableSupply, a1, a4);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a3, a2, a1, a4, a5);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);

        move = new TwoOptMove(variableDescriptor, anchorVariableSupply, a4, a1);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a3, a2, a1, a4, a5);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
    }

}
