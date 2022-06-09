package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class ListSwapMoveSelectorTest {

    @Test
    void original() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity.createWithValues("A", v2, v1);
        TestdataListEntity.createWithValues("B");
        TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListSwapMoveSelector<TestdataListSolution> moveSelector = new ListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntityIndependentValueSelector(v3, v1, v2),
                mockEntityIndependentValueSelector(v3, v1, v2),
                false);

        SolverScope<TestdataListSolution> solverScope = mock(SolverScope.class);
        when(solverScope.<SimpleScore> getScoreDirector()).thenReturn(scoreDirector);
        moveSelector.solvingStarted(solverScope);

        // Value order: [3, 1, 2]
        // Entity order: [A, B, C]
        // Initial state:
        // - A [2, 1]
        // - B []
        // - C [3]

        assertAllCodesOfMoveSelector(moveSelector,
                "3 {C[0]} <-> 3 {C[0]}", // undoable
                "3 {C[0]} <-> 1 {A[1]}",
                "3 {C[0]} <-> 2 {A[0]}",
                "1 {A[1]} <-> 3 {C[0]}", // redundant
                "1 {A[1]} <-> 1 {A[1]}", // undoable
                "1 {A[1]} <-> 2 {A[0]}",
                "2 {A[0]} <-> 3 {C[0]}", // redundant
                "2 {A[0]} <-> 1 {A[1]}", // redundant
                "2 {A[0]} <-> 2 {A[0]}" // undoable
        );
    }

    @Test
    void random() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity.createWithValues("B");
        TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListSwapMoveSelector<TestdataListSolution> moveSelector = new ListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                // Value selectors are longer than the number of expected codes because they're expected
                // to be never ending, so they must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v2, v3, v2, v3, v2, v3, v1, v1, v1, v1),
                mockEntityIndependentValueSelector(v1, v2, v3, v1, v2, v3, v1, v2, v3, v1),
                true);

        SolverScope<TestdataListSolution> solverScope = mock(SolverScope.class);
        when(solverScope.<SimpleScore> getScoreDirector()).thenReturn(scoreDirector);
        moveSelector.solvingStarted(solverScope);

        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "2 {A[1]} <-> 1 {A[0]}",
                "3 {C[0]} <-> 2 {A[1]}",
                "2 {A[1]} <-> 3 {C[0]}",
                "3 {C[0]} <-> 1 {A[0]}",
                "2 {A[1]} <-> 2 {A[1]}",
                "3 {C[0]} <-> 3 {C[0]}",
                "1 {A[0]} <-> 1 {A[0]}",
                "1 {A[0]} <-> 2 {A[1]}",
                "1 {A[0]} <-> 3 {C[0]}");
    }
}
