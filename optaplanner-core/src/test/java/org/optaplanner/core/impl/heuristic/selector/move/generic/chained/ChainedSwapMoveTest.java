/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.*;

public class ChainedSwapMoveTest {

    @Test
    public void noTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[]{a1, a2, a3, b1});

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                Collections.singletonList(variableDescriptor), Collections.singletonList(inverseVariableSupply),
                a3, b1);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2, b1);
        SelectorTestUtils.assertChain(b0, a3);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, b0);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b1, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void oldAndNewTrailing() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[]{a1, a2, a3, b1, b2});

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                Collections.singletonList(variableDescriptor), Collections.singletonList(inverseVariableSupply),
                a2, b1);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, b1, a3);
        SelectorTestUtils.assertChain(b0, a2, b2);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a2, b0);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, b1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b1, a1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, b2, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
    }

    @Test
    public void sameChain() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[]{a1, a2, a3, a4});

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                Collections.singletonList(variableDescriptor), Collections.singletonList(inverseVariableSupply),
                a2, a3);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a3, a2, a4);

        verify(scoreDirector).changeVariableFacade(variableDescriptor, a2, a3);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a3, a1);
        verify(scoreDirector).changeVariableFacade(variableDescriptor, a4, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);

        move = new ChainedSwapMove<>(
                Collections.singletonList(variableDescriptor), Collections.singletonList(inverseVariableSupply),
                a3, a2);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a3, a2, a4);

        verify(scoreDirector, times(2)).changeVariableFacade(variableDescriptor, a2, a3);
        verify(scoreDirector, times(2)).changeVariableFacade(variableDescriptor, a3, a1);
        verify(scoreDirector, times(2)).changeVariableFacade(variableDescriptor, a4, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
    }

    @Test
    public void rebase() {
        EntityDescriptor<TestdataChainedSolution> entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataChainedSolution>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", null);

        TestdataChainedAnchor destinationA0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity destinationA1 = new TestdataChainedEntity("a1", destinationA0);
        TestdataChainedEntity destinationA2 = new TestdataChainedEntity("a2", destinationA1);
        TestdataChainedAnchor destinationB0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity destinationC1 = new TestdataChainedEntity("c1", null);

        ScoreDirector<TestdataChainedSolution> destinationScoreDirector = mockRebasingScoreDirector(
                entityDescriptor.getSolutionDescriptor(), new Object[][]{
                        {a0, destinationA0},
                        {a1, destinationA1},
                        {a2, destinationA2},
                        {b0, destinationB0},
                        {c1, destinationC1},
                });
        List<SingletonInverseVariableSupply> inverseVariableSupplyList = Collections.singletonList(
                mock(SingletonInverseVariableSupply.class));

        assertSameProperties(destinationA1, destinationA2,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a1, a2).rebase(destinationScoreDirector));
        assertSameProperties(destinationA1, destinationC1,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a1, c1).rebase(destinationScoreDirector));
        assertSameProperties(destinationA2, destinationC1,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a2, c1).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object leftEntity, Object rightEntity, ChainedSwapMove<?> move) {
        assertSame(leftEntity, move.getLeftEntity());
        assertSame(rightEntity, move.getRightEntity());
    }

}
