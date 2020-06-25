/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
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

public class TailChainSwapMoveTest {

    @Test
    public void isMoveDoable() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

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
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, b0)
                .isMoveDoable(scoreDirector)).isTrue();
        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, b1, a1)
                .isMoveDoable(scoreDirector)).isTrue();
        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1, a2)
                .isMoveDoable(scoreDirector)).isTrue();
        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, a0)
                .isMoveDoable(scoreDirector)).isTrue();
        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1, a1)
                .isMoveDoable(scoreDirector)).isFalse();
        assertThat(new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, a0)
                .isMoveDoable(scoreDirector)).isFalse();
    }

    @Test
    public void doMove() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

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
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);

        TailChainSwapMove<TestdataChainedSolution> move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply,
                anchorVariableSupply, a2, b0);
        TailChainSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, b1);
        SelectorTestUtils.assertChain(b0, a2, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);

        // To tail value
        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, b1);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1);
        SelectorTestUtils.assertChain(b0, b1, a2, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void doMoveInSameChain() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);
        TestdataChainedEntity a7 = new TestdataChainedEntity("a7", a6);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, a5, a6, a7));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        TailChainSwapMove<TestdataChainedSolution> move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply,
                anchorVariableSupply, a4, a1);
        TailChainSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a3, a2, a5, a6, a7);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, a1);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a3, a2, a4, a5, a6, a7);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a7, a1);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a7, a6, a5, a4, a3, a2);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1, a4);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a7, a6, a5, a2, a3, a4, a1);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, a4);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a7, a6, a5, a4, a3, a2, a1);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, a6);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a7, a3, a4, a5, a6, a2, a1);
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);

        // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
        //        // To tail value
        //        move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, a7);
        //        undoMove = move.createUndoMove(scoreDirector);
        //        move.doMove(scoreDirector);
        //        SelectorTestUtils.assertChain(a0, a4, a5, a6, a7, a3, a2, a1);
        //        undoMove.doMove(scoreDirector);
        //        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);
    }

    @Test
    public void rebase() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);

        TestdataChainedAnchor destinationA0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity destinationA1 = new TestdataChainedEntity("a1", destinationA0);
        TestdataChainedEntity destinationA2 = new TestdataChainedEntity("a2", destinationA1);
        TestdataChainedEntity destinationA3 = new TestdataChainedEntity("a3", destinationA2);
        TestdataChainedAnchor destinationB0 = new TestdataChainedAnchor("b0");
        TestdataChainedAnchor destinationC0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity destinationC1 = new TestdataChainedEntity("c1", destinationC0);

        ScoreDirector<TestdataChainedSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { a0, destinationA0 },
                        { a1, destinationA1 },
                        { a2, destinationA2 },
                        { a3, destinationA3 },
                        { b0, destinationB0 },
                        { c0, destinationC0 },
                        { c1, destinationC1 },
                });
        SingletonInverseVariableSupply inverseVariableSupply = mock(SingletonInverseVariableSupply.class);
        AnchorVariableSupply anchorVariableSupply = mock(AnchorVariableSupply.class);

        assertSameProperties(destinationA1, destinationC1,
                new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1, c1)
                        .rebase(destinationScoreDirector));
        assertSameProperties(destinationA3, destinationA0,
                new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, a0)
                        .rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object leftEntity, Object rightValue, TailChainSwapMove move) {
        assertThat(move.getLeftEntity()).isSameAs(leftEntity);
        assertThat(move.getRightValue()).isSameAs(rightValue);
    }

    @Test
    public void toStringTest() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

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
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        assertThat(new TailChainSwapMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a1, b0).toString()).isEqualTo("a1 {a0} <-tailChainSwap-> b1 {b0}");
        assertThat(new TailChainSwapMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a1, b1).toString())
                        .isEqualTo("a1 {a0} <-tailChainSwap-> null {b1}");
        assertThat(new TailChainSwapMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, b1, a0).toString()).isEqualTo("b1 {b0} <-tailChainSwap-> a1 {a0}");
        assertThat(new TailChainSwapMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a1, a3).toString())
                        .isEqualTo("a1 {a0} <-tailChainSwap-> null {a3}");
        assertThat(new TailChainSwapMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a2, a0).toString()).isEqualTo("a2 {a1} <-tailChainSwap-> a1 {a0}");
    }

    @Test
    public void getPlanningEntitiesWithRightEntityNull() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", null);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", null);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, b1));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        TailChainSwapMove move = new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1,
                b0);
        assertThat(move.getPlanningEntities()).doesNotContain((TestdataChainedEntity) null);

        move.doMoveOnGenuineVariables(scoreDirector);
        assertThat(move.getPlanningEntities()).doesNotContain((TestdataChainedEntity) null);

        Move undoMove = move.createUndoMove(scoreDirector);
        assertThat(undoMove.getPlanningEntities()).doesNotContain((TestdataChainedEntity) null);
    }
}
