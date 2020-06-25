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
import static org.mockito.Mockito.verify;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertListElementsSameExactly;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SubChainReversingSwapMoveTest {

    @Test
    public void noTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, b1, b2, b3 });

        SubChainReversingSwapMove<TestdataChainedSolution> move = new SubChainReversingSwapMove<>(variableDescriptor,
                inverseVariableSupply,
                new SubChain(Arrays.asList(a3, a4, a5)),
                new SubChain(Arrays.asList(b2, b3)));
        SubChainReversingSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2, b3, b2);
        SelectorTestUtils.assertChain(b0, b1, a5, a4, a3);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a5, b1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, a5);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a4);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b3, a2);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b2, b3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);
    }

    @Test
    public void oldAndNewTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, b1, b2, b3 });

        SubChainReversingSwapMove<TestdataChainedSolution> move = new SubChainReversingSwapMove<>(variableDescriptor,
                inverseVariableSupply,
                new SubChain(Arrays.asList(a2, a3, a4)),
                new SubChain(Arrays.asList(b1, b2)));
        SubChainReversingSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, b2, b1, a5);
        SelectorTestUtils.assertChain(b0, a4, a3, a2, b3);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, b0);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a4);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a2, a3);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b3, a2);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b2, a1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b1, b2);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a5, b1);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);
    }

    @Test
    public void sameChainInPlaceNoTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);
        TestdataChainedEntity a7 = new TestdataChainedEntity("a7", a6);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, a6, a7 });

        SubChainReversingSwapMove<TestdataChainedSolution> move = new SubChainReversingSwapMove<>(variableDescriptor,
                inverseVariableSupply,
                new SubChain(Arrays.asList(a3, a4, a5)),
                new SubChain(Arrays.asList(a6, a7)));
        SubChainReversingSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2, a7, a6, a5, a4, a3);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a7, a2);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a6, a7);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a5, a6);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, a5);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a4);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);
    }

    @Test
    public void sameChainInPlaceOldAndNewTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);
        TestdataChainedEntity a7 = new TestdataChainedEntity("a7", a6);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, a6, a7 });

        SubChainReversingSwapMove<TestdataChainedSolution> move = new SubChainReversingSwapMove<>(variableDescriptor,
                inverseVariableSupply,
                new SubChain(Arrays.asList(a2, a3, a4)),
                new SubChain(Arrays.asList(a5, a6)));
        SubChainReversingSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a6, a5, a4, a3, a2, a7);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a6, a1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a5, a6);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, a5);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a4);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a2, a3);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a7, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);
    }

    @Test
    public void sameChainInPlaceOldAndNewTrailingOppositeParameterOrder() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);
        TestdataChainedEntity a7 = new TestdataChainedEntity("a7", a6);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, a6, a7 });

        SubChainReversingSwapMove<TestdataChainedSolution> move = new SubChainReversingSwapMove<>(variableDescriptor,
                inverseVariableSupply,
                new SubChain(Arrays.asList(a5, a6)), // Opposite parameter order
                new SubChain(Arrays.asList(a2, a3, a4)));
        SubChainReversingSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a6, a5, a4, a3, a2, a7);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a6, a1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a5, a6);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, a5);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a4);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a2, a3);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a7, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6, a7);
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

        assertSameProperties(Arrays.asList(destinationA1, destinationA2, destinationA3), Arrays.asList(destinationC1),
                new SubChainReversingSwapMove<>(variableDescriptor, inverseVariableSupply,
                        new SubChain(Arrays.asList(a1, a2, a3)), new SubChain(Arrays.asList(c1)))
                                .rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationA1, destinationA2), Arrays.asList(destinationA3),
                new SubChainReversingSwapMove<>(variableDescriptor, inverseVariableSupply,
                        new SubChain(Arrays.asList(a1, a2)), new SubChain(Arrays.asList(a3)))
                                .rebase(destinationScoreDirector));
    }

    public void assertSameProperties(List<Object> leftEntityList, List<Object> rightEntityList,
            SubChainReversingSwapMove move) {
        assertListElementsSameExactly(leftEntityList, move.getLeftSubChain().getEntityList());
        assertListElementsSameExactly(rightEntityList, move.getRightSubChain().getEntityList());
    }

    @Test
    public void toStringTest() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4, a5, b1, b2, b3 });

        assertThat(new SubChainReversingSwapMove<>(variableDescriptor, inverseVariableSupply,
                new SubChain(Arrays.asList(a2, a3, a4)), new SubChain(Arrays.asList(b1, b2, b3)))
                        .toString()).isEqualTo("[a2..a4] {a1} <-reversing-> [b1..b3] {b0}");
        assertThat(new SubChainReversingSwapMove<>(variableDescriptor, inverseVariableSupply,
                new SubChain(Arrays.asList(a1, a2)), new SubChain(Arrays.asList(a4, a5))).toString())
                        .isEqualTo("[a1..a2] {a0} <-reversing-> [a4..a5] {a3}");
        assertThat(new SubChainReversingSwapMove<>(variableDescriptor, inverseVariableSupply,
                new SubChain(Arrays.asList(a3)), new SubChain(Arrays.asList(b2))).toString())
                        .isEqualTo("[a3..a3] {a2} <-reversing-> [b2..b2] {b1}");
    }

}
