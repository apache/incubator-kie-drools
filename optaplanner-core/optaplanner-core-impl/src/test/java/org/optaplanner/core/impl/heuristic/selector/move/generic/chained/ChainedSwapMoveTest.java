package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static java.util.Arrays.asList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class ChainedSwapMoveTest {

    private final GenuineVariableDescriptor<TestdataChainedSolution> chainedVariableDescriptor = TestdataChainedEntity
            .buildVariableDescriptorForChainedObject();
    private final GenuineVariableDescriptor<TestdataChainedSolution> unchainedVariableDescriptor = TestdataChainedEntity
            .buildVariableDescriptorForUnchainedValue();

    @Test
    void noTrailing() {
        InnerScoreDirector<TestdataChainedSolution, SimpleScore> scoreDirector = PlannerTestUtils.mockScoreDirector(
                chainedVariableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        a1.setUnchainedValue(new TestdataValue(a1.getCode()));
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        a2.setUnchainedValue(new TestdataValue(a2.getCode()));
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        a3.setUnchainedValue(new TestdataValue(a3.getCode()));

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        b1.setUnchainedValue(new TestdataValue(b1.getCode()));

        TestdataValue originalA1UnchainedObject = a1.getUnchainedValue();
        TestdataValue originalA2UnchainedObject = a2.getUnchainedValue();
        TestdataValue originalA3UnchainedObject = a3.getUnchainedValue();
        TestdataValue originalB1UnchainedObject = b1.getUnchainedValue();

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, b1 });

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                asList(chainedVariableDescriptor, unchainedVariableDescriptor),
                asList(inverseVariableSupply, null),
                a3, b1);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalB1UnchainedObject);
            softly.assertThat(b1.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a2, b1);
        SelectorTestUtils.assertChain(b0, a3);

        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a3, b0);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, b1, a2);

        undoMove.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(b1.getUnchainedValue()).isEqualTo(originalB1UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);

    }

    @Test
    void oldAndNewTrailing() {
        InnerScoreDirector<TestdataChainedSolution, SimpleScore> scoreDirector = PlannerTestUtils.mockScoreDirector(
                chainedVariableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        a1.setUnchainedValue(new TestdataValue(a1.getCode()));
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        a2.setUnchainedValue(new TestdataValue(a2.getCode()));
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        a3.setUnchainedValue(new TestdataValue(a3.getCode()));

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        b1.setUnchainedValue(new TestdataValue(b1.getCode()));
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        b2.setUnchainedValue(new TestdataValue(b2.getCode()));

        TestdataValue originalA1UnchainedObject = a1.getUnchainedValue();
        TestdataValue originalA2UnchainedObject = a2.getUnchainedValue();
        TestdataValue originalA3UnchainedObject = a3.getUnchainedValue();
        TestdataValue originalB1UnchainedObject = b1.getUnchainedValue();
        TestdataValue originalB2UnchainedObject = b2.getUnchainedValue();

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, b1, b2 });

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                asList(chainedVariableDescriptor, unchainedVariableDescriptor),
                asList(inverseVariableSupply, null),
                a2, b1);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalB1UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(b1.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(b2.getUnchainedValue()).isEqualTo(originalB2UnchainedObject);
        });

        SelectorTestUtils.assertChain(a0, a1, b1, a3);
        SelectorTestUtils.assertChain(b0, a2, b2);

        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a2, b0);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a3, b1);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, b1, a1);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, b2, a2);

        undoMove.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(b1.getUnchainedValue()).isEqualTo(originalB1UnchainedObject);
            softly.assertThat(b2.getUnchainedValue()).isEqualTo(originalB2UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
    }

    @Test
    void sameChain() {
        InnerScoreDirector<TestdataChainedSolution, SimpleScore> scoreDirector = PlannerTestUtils.mockScoreDirector(
                chainedVariableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        a1.setUnchainedValue(new TestdataValue(a1.getCode()));
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        a2.setUnchainedValue(new TestdataValue(a2.getCode()));
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        a3.setUnchainedValue(new TestdataValue(a3.getCode()));
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        a4.setUnchainedValue(new TestdataValue(a4.getCode()));

        TestdataValue originalA1UnchainedObject = a1.getUnchainedValue();
        TestdataValue originalA2UnchainedObject = a2.getUnchainedValue();
        TestdataValue originalA3UnchainedObject = a3.getUnchainedValue();
        TestdataValue originalA4UnchainedObject = a4.getUnchainedValue();

        SingletonInverseVariableSupply inverseVariableSupply = SelectorTestUtils.mockSingletonInverseVariableSupply(
                new TestdataChainedEntity[] { a1, a2, a3, a4 });

        ChainedSwapMove<TestdataChainedSolution> move = new ChainedSwapMove<>(
                asList(chainedVariableDescriptor, unchainedVariableDescriptor),
                asList(inverseVariableSupply, null),
                a2, a3);
        ChainedSwapMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a4.getUnchainedValue()).isEqualTo(originalA4UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a3, a2, a4);

        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a2, a3);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a3, a1);
        verify(scoreDirector).changeVariableFacade(chainedVariableDescriptor, a4, a2);

        undoMove.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(a4.getUnchainedValue()).isEqualTo(originalA4UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);

        move = new ChainedSwapMove<>(
                asList(chainedVariableDescriptor, unchainedVariableDescriptor),
                asList(inverseVariableSupply, null),
                a3, a2);
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a4.getUnchainedValue()).isEqualTo(originalA4UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a3, a2, a4);

        verify(scoreDirector, times(2)).changeVariableFacade(chainedVariableDescriptor, a2, a3);
        verify(scoreDirector, times(2)).changeVariableFacade(chainedVariableDescriptor, a3, a1);
        verify(scoreDirector, times(2)).changeVariableFacade(chainedVariableDescriptor, a4, a2);

        undoMove.doMove(scoreDirector);

        assertSoftly(softly -> {
            softly.assertThat(a1.getUnchainedValue()).isEqualTo(originalA1UnchainedObject);
            softly.assertThat(a2.getUnchainedValue()).isEqualTo(originalA2UnchainedObject);
            softly.assertThat(a3.getUnchainedValue()).isEqualTo(originalA3UnchainedObject);
            softly.assertThat(a4.getUnchainedValue()).isEqualTo(originalA4UnchainedObject);
        });
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
    }

    @Test
    void rebase() {
        EntityDescriptor<TestdataChainedSolution> entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataChainedSolution>> variableDescriptorList = entityDescriptor
                .getGenuineVariableDescriptorList();

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
                entityDescriptor.getSolutionDescriptor(), new Object[][] {
                        { a0, destinationA0 },
                        { a1, destinationA1 },
                        { a2, destinationA2 },
                        { b0, destinationB0 },
                        { c1, destinationC1 },
                });
        List<SingletonInverseVariableSupply> inverseVariableSupplyList = Collections.singletonList(
                mock(SingletonInverseVariableSupply.class));

        assertSameProperties(destinationA1, destinationA2,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a1, a2)
                        .rebase(destinationScoreDirector));
        assertSameProperties(destinationA1, destinationC1,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a1, c1)
                        .rebase(destinationScoreDirector));
        assertSameProperties(destinationA2, destinationC1,
                new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, a2, c1)
                        .rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object leftEntity, Object rightEntity, ChainedSwapMove<?> move) {
        assertSoftly(softly -> {
            softly.assertThat(move.getLeftEntity()).isSameAs(leftEntity);
            softly.assertThat(move.getRightEntity()).isSameAs(rightEntity);
        });
    }

}
