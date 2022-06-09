package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfValueSelectorForEntity;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class UnassignedValueSelectorTest {

    @Test
    void filterOutAssignedValues() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        // 1 and 3 are assigned, the rest (2, 4, 5) are unassigned.
        TestdataListEntity.createWithValues("A", v1, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntityIndependentValueSelector<TestdataListSolution> childValueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        UnassignedValueSelector<TestdataListSolution> valueSelector = new UnassignedValueSelector<>(childValueSelector);

        SolverScope<TestdataListSolution> solverScope = mock(SolverScope.class);
        valueSelector.solvingStarted(solverScope);

        AbstractPhaseScope<TestdataListSolution> phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        when(phaseScope.<SimpleScore> getScoreDirector()).thenReturn(scoreDirector);
        valueSelector.phaseStarted(phaseScope);

        AbstractStepScope<TestdataListSolution> stepScope = mock(AbstractStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        valueSelector.stepStarted(stepScope);

        assertAllCodesOfValueSelector(valueSelector, "2", "4", "5");

        // Although the entity dependent iterators are not used, they should behave correctly.
        assertAllCodesOfValueSelectorForEntity(valueSelector, null, "2", "4", "5");
        assertAllCodesOfIterator(valueSelector.endingIterator(null), "2", "4", "5");
    }

    @Test
    void requireEndingChildValueSelector() {
        EntityIndependentValueSelector<TestdataListSolution> childValueSelector = mock(EntityIndependentValueSelector.class);

        when(childValueSelector.isNeverEnding()).thenReturn(true);

        assertThatIllegalArgumentException().isThrownBy(() -> new UnassignedValueSelector<>(childValueSelector));
    }
}
