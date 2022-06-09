package org.optaplanner.core.impl.heuristic.selector.value.chained;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfSubChainSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class DefaultSubChainSelectorTest {

    @Test
    void notChainedVariableDescriptor() {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        when(variableDescriptor.isChained()).thenReturn(false);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DefaultSubChainSelector(valueSelector, true, 1, 1))
                .withMessageContaining("chained");
    }

    @Test
    void neverEndingValueSelector() {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        when(variableDescriptor.isChained()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> new DefaultSubChainSelector(valueSelector, true, 1, 1))
                .withMessageContaining("neverEnding");
    }

    @Test
    void minimumSubChainSizeIsZero() {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        when(variableDescriptor.isChained()).thenReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> new DefaultSubChainSelector(valueSelector, true, 0, 1))
                .withMessageContaining("at least 1");
    }

    @Test
    void minimumSubChainSizeIsGreaterThanMaximumSubChainSize() {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        when(variableDescriptor.isChained()).thenReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> new DefaultSubChainSelector(valueSelector, true, 2, 1))
                .withMessageContaining("at least maximumSubChainSize");
    }

    @Test
    void calculateSubChainSelectionSize() {
        assertCalculateSubChainSelectionSize(4L, 1, 1);
        assertCalculateSubChainSelectionSize(3L, 2, 2);
        assertCalculateSubChainSelectionSize(2L, 3, 3);
        assertCalculateSubChainSelectionSize(1L, 4, 4);

        assertCalculateSubChainSelectionSize(7L, 1, 2);
        assertCalculateSubChainSelectionSize(9L, 1, 3);
        assertCalculateSubChainSelectionSize(10L, 1, 4);
        assertCalculateSubChainSelectionSize(5L, 2, 3);
        assertCalculateSubChainSelectionSize(6L, 2, 4);
        assertCalculateSubChainSelectionSize(3L, 3, 4);

        assertCalculateSubChainSelectionSize(10L, 1, 5);
        assertCalculateSubChainSelectionSize(6L, 2, 5);
        assertCalculateSubChainSelectionSize(3L, 3, 5);
        assertCalculateSubChainSelectionSize(1L, 4, 5);
        assertCalculateSubChainSelectionSize(0L, 5, 5);
    }

    private void assertCalculateSubChainSelectionSize(long expected, int minimumSubChainSize, int maximumSubChainSize) {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);
        DefaultSubChainSelector selector = new DefaultSubChainSelector(
                valueSelector, false, minimumSubChainSize, maximumSubChainSize);
        assertThat(selector.calculateSubChainSelectionSize(
                new SubChain(Arrays.asList(a1, a2, a3, a4)))).isEqualTo(expected);
    }

    @Test
    void original() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, b1, b2));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 1, Integer.MAX_VALUE);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);
        assertAllCodesOfSubChainSelector(subChainSelector,
                "[a1]", "[a1, a2]", "[a1, a2, a3]", "[a1, a2, a3, a4]",
                "[a2]", "[a2, a3]", "[a2, a3, a4]",
                "[a3]", "[a3, a4]",
                "[a4]",
                "[b1]", "[b1, b2]",
                "[b2]");
        subChainSelector.stepEnded(stepScopeA1);

        scoreDirector.changeVariableFacade(variableDescriptor, a4, a2);
        scoreDirector.changeVariableFacade(variableDescriptor, a3, b1);
        scoreDirector.changeVariableFacade(variableDescriptor, b2, a3);
        scoreDirector.triggerVariableListeners();

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA2);
        assertAllCodesOfSubChainSelector(subChainSelector,
                "[a1]", "[a1, a2]", "[a1, a2, a4]",
                "[a2]", "[a2, a4]",
                "[a4]",
                "[b1]", "[b1, a3]", "[b1, a3, b2]",
                "[a3]", "[a3, b2]",
                "[b2]");
        subChainSelector.stepEnded(stepScopeA2);

        subChainSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        subChainSelector.stepStarted(stepScopeB1);
        assertAllCodesOfSubChainSelector(subChainSelector,
                "[a1]", "[a1, a2]", "[a1, a2, a4]",
                "[a2]", "[a2, a4]",
                "[a4]",
                "[b1]", "[b1, a3]", "[b1, a3, b2]",
                "[a3]", "[a3, b2]",
                "[b2]");
        subChainSelector.stepEnded(stepScopeB1);

        subChainSelector.phaseEnded(phaseScopeB);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 2, 3);
    }

    @Test
    void emptyEntitySelectorOriginal() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Collections.emptyList());
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, b0);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 1, Integer.MAX_VALUE);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);
        assertAllCodesOfSubChainSelector(subChainSelector);
        subChainSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA2);
        assertAllCodesOfSubChainSelector(subChainSelector);
        subChainSelector.stepEnded(stepScopeA2);

        subChainSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        subChainSelector.stepStarted(stepScopeB1);
        assertAllCodesOfSubChainSelector(subChainSelector);
        subChainSelector.stepEnded(stepScopeB1);

        subChainSelector.phaseEnded(phaseScopeB);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 2, 3);
    }

    @Test
    void originalMinimum2Maximum3() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, b1, b2));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 2, 3);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);

        assertAllCodesOfSubChainSelector(subChainSelector,
                "[a1, a2]", "[a1, a2, a3]",
                "[a2, a3]", "[a2, a3, a4]",
                "[a3, a4]",
                "[b1, b2]");

        subChainSelector.stepEnded(stepScopeA1);

        subChainSelector.phaseEnded(phaseScopeA);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 1, 1);
    }

    @Test
    void originalMinimum3Maximum3() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, b1, b2));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 3, 3);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);

        assertAllCodesOfSubChainSelector(subChainSelector, "[a1, a2, a3]", "[a2, a3, a4]");

        subChainSelector.stepEnded(stepScopeA1);

        subChainSelector.phaseEnded(phaseScopeA);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 1, 1);
    }

    @Test
    void random() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, true, 1, Integer.MAX_VALUE);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        when(solverScope.getWorkingRandom()).thenReturn(new Random(0L));
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);

        assertContainsCodesOfNeverEndingSubChainSelector(subChainSelector,
                new SubChain(Arrays.asList(a1)),
                new SubChain(Arrays.asList(a2)),
                new SubChain(Arrays.asList(a3)),
                new SubChain(Arrays.asList(a4)),
                new SubChain(Arrays.asList(a1, a2)),
                new SubChain(Arrays.asList(a2, a3)),
                new SubChain(Arrays.asList(a3, a4)),
                new SubChain(Arrays.asList(a1, a2, a3)),
                new SubChain(Arrays.asList(a2, a3, a4)),
                new SubChain(Arrays.asList(a1, a2, a3, a4)));

        subChainSelector.stepEnded(stepScopeA1);

        subChainSelector.phaseEnded(phaseScopeA);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 1, 1);
    }

    @Test
    void randomMinimum2Maximum3() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, true, 2, 3);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        when(solverScope.getWorkingRandom()).thenReturn(new Random(0L));
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);

        assertContainsCodesOfNeverEndingSubChainSelector(subChainSelector,
                new SubChain(Arrays.asList(a1, a2)),
                new SubChain(Arrays.asList(a2, a3)),
                new SubChain(Arrays.asList(a3, a4)),
                new SubChain(Arrays.asList(a1, a2, a3)),
                new SubChain(Arrays.asList(a2, a3, a4)));

        subChainSelector.stepEnded(stepScopeA1);

        subChainSelector.phaseEnded(phaseScopeA);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 1, 1);
    }

    @Test
    void randomMinimum3Maximum3() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4));
        scoreDirector.setWorkingSolution(solution);

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, true, 3, 3);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        when(solverScope.getWorkingRandom()).thenReturn(new Random(0L));
        subChainSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);

        assertContainsCodesOfNeverEndingSubChainSelector(subChainSelector,
                new SubChain(Arrays.asList(a1, a2, a3)),
                new SubChain(Arrays.asList(a2, a3, a4)));

        subChainSelector.stepEnded(stepScopeA1);

        subChainSelector.phaseEnded(phaseScopeA);

        subChainSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(valueSelector, 1, 1, 1);
    }

    private void assertContainsCodesOfNeverEndingSubChainSelector(
            DefaultSubChainSelector subChainSelector, SubChain... subChains) {
        Iterator<SubChain> iterator = subChainSelector.iterator();
        assertThat(iterator).isNotNull();
        int selectionSize = subChains.length;
        Map<SubChain, Integer> subChainCountMap = new HashMap<>(selectionSize);
        for (int i = 0; i < selectionSize * 10; i++) {
            collectNextSubChain(iterator, subChainCountMap);
        }
        for (SubChain subChain : subChains) {
            Integer count = subChainCountMap.remove(subChain);
            assertThat(count)
                    .as("The subChain (" + subChain + ") was not collected.")
                    .isNotNull();
        }
        assertThat(subChainCountMap.isEmpty()).isTrue();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(subChainSelector.isCountable()).isTrue();
        assertThat(subChainSelector.isNeverEnding()).isTrue();
        assertThat(subChainSelector.getSize()).isEqualTo(selectionSize);
    }

    private void collectNextSubChain(Iterator<SubChain> iterator, Map<SubChain, Integer> subChainCountMap) {
        assertThat(iterator.hasNext()).isTrue();
        SubChain subChain = iterator.next();
        Integer count = subChainCountMap.get(subChain);
        if (count == null) {
            subChainCountMap.put(subChain, 1);
        } else {
            subChainCountMap.put(subChain, count + 1);
        }
    }
}
