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
    public void doMove3OptDifferentAnchors() {
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
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, c2);
        SelectorTestUtils.assertChain(b0, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, b1, b2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);

        // To tail value
        move = new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[]{b2, c2});
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, c2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);
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
