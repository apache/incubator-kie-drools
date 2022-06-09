package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestRandom;

class ListChangeMoveSelectorTest {

    @Test
    void original() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v2, v1);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListChangeMoveSelector<TestdataListSolution> moveSelector = new ListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b, c),
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
                // Moving 3 from C[0]
                "3 {C[0]->A[0]}",
                "3 {C[0]->A[1]}",
                "3 {C[0]->A[2]}",
                "3 {C[0]->B[0]}",
                "3 {C[0]->C[0]}", // noop
                "3 {C[0]->C[1]}", // undoable
                // Moving 1 from A[1]
                "1 {A[1]->A[0]}",
                "1 {A[1]->A[1]}", // noop
                "1 {A[1]->A[2]}", // undoable
                "1 {A[1]->B[0]}",
                "1 {A[1]->C[0]}",
                "1 {A[1]->C[1]}",
                // Moving 2 from A[0]
                "2 {A[0]->A[0]}", // noop
                "2 {A[0]->A[1]}",
                "2 {A[0]->A[2]}", // undoable
                "2 {A[0]->B[0]}",
                "2 {A[0]->C[0]}",
                "2 {A[0]->C[1]}");
    }

    @Test
    void random() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListChangeMoveSelector<TestdataListSolution> moveSelector = new ListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b, c),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v2, v1, v3, v3, v3, v1),
                true);

        TestRandom random = new TestRandom(3, 2, 0, 1, 2, 2); // global destination indexes
        final int destinationIndexRange = 6; // value count + entity count

        SolverScope<TestdataListSolution> solverScope = mock(SolverScope.class);
        when(solverScope.<SimpleScore> getScoreDirector()).thenReturn(scoreDirector);
        when(solverScope.getWorkingRandom()).thenReturn(random);
        moveSelector.solvingStarted(solverScope);

        // Initial state:
        // - A [1, 2]
        // - B []
        // - C [3]

        // The moved values (2, 1, 3, 3, 3) and their source positions are supplied by the mocked value selector.
        // The test is focused on the destinations (B[0], A[2], ...), which reflect the numbers supplied by the test random.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "2 {A[1]->B[0]}",
                "1 {A[0]->A[2]}",
                "3 {C[0]->A[0]}",
                "3 {C[0]->A[1]}",
                "3 {C[0]->A[2]}");

        random.assertIntBoundJustRequested(destinationIndexRange);
    }

    @Test
    void constructionHeuristic() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListEntity a = new TestdataListEntity("A");
        TestdataListEntity b = new TestdataListEntity("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListChangeMoveSelector<TestdataListSolution> moveSelector = new ListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b, c),
                mockEntityIndependentValueSelector(v3, v1, v4, v2, v5),
                false);

        SolverScope<TestdataListSolution> solverScope = mock(SolverScope.class);
        when(solverScope.<SimpleScore> getScoreDirector()).thenReturn(scoreDirector);
        moveSelector.solvingStarted(solverScope);

        assertAllCodesOfMoveSelector(moveSelector,
                // Assigning 3
                "3 {null->A[0]}",
                "3 {null->B[0]}",
                "3 {null->C[0]}",
                "3 {null->C[1]}",
                // Assigning 1
                "1 {null->A[0]}",
                "1 {null->B[0]}",
                "1 {null->C[0]}",
                "1 {null->C[1]}",
                // Assigning 4
                "4 {null->A[0]}",
                "4 {null->B[0]}",
                "4 {null->C[0]}",
                "4 {null->C[1]}",
                // Assigning 2
                "2 {null->A[0]}",
                "2 {null->B[0]}",
                "2 {null->C[0]}",
                "2 {null->C[1]}",
                // 5 is already assigned, so ListChangeMoves are selected.
                "5 {C[0]->A[0]}",
                "5 {C[0]->B[0]}",
                "5 {C[0]->C[0]}",
                "5 {C[0]->C[1]}");
    }
}
