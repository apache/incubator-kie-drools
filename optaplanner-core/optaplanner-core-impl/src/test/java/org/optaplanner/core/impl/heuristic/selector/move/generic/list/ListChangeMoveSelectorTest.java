package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.ElementRef.elementRef;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockDestinationSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingDestinationSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

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
                mockEntityIndependentValueSelector(v3, v1, v2),
                mockDestinationSelector(
                        elementRef(a, 0),
                        elementRef(b, 0),
                        elementRef(c, 0),
                        elementRef(c, 1),
                        elementRef(a, 2),
                        elementRef(a, 1)),
                false);

        solvingStarted(moveSelector, scoreDirector);

        // Value order: [3, 1, 2]
        // Entity order: [A, B, C]
        // Initial state:
        // - A [2, 1]
        // - B []
        // - C [3]

        assertAllCodesOfMoveSelector(moveSelector,
                // Moving 3 from C[0]
                "3 {C[0]->A[0]}",
                "3 {C[0]->B[0]}",
                "3 {C[0]->C[0]}", // noop
                "3 {C[0]->C[1]}", // undoable
                "3 {C[0]->A[2]}",
                "3 {C[0]->A[1]}",
                // Moving 1 from A[1]
                "1 {A[1]->A[0]}",
                "1 {A[1]->B[0]}",
                "1 {A[1]->C[0]}",
                "1 {A[1]->C[1]}",
                "1 {A[1]->A[2]}", // undoable
                "1 {A[1]->A[1]}", // noop
                // Moving 2 from A[0]
                "2 {A[0]->A[0]}", // noop
                "2 {A[0]->B[0]}",
                "2 {A[0]->C[0]}",
                "2 {A[0]->C[1]}",
                "2 {A[0]->A[2]}", // undoable
                "2 {A[0]->A[1]}");
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
                mockNeverEndingEntityIndependentValueSelector(v2, v1, v3, v3, v3),
                mockNeverEndingDestinationSelector(
                        elementRef(b, 0),
                        elementRef(a, 2),
                        elementRef(a, 0),
                        elementRef(a, 1),
                        elementRef(a, 2)),
                true);

        solvingStarted(moveSelector, scoreDirector);

        // Initial state:
        // - A [1, 2]
        // - B []
        // - C [3]

        // The moved values (2, 1, 3, 3, 3) are supplied by the source value selector and their source positions
        // are deduced using inverse relation and index supplies. The destinations (B[0], A[2], ...) are supplied
        // by the destination selector.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "2 {A[1]->B[0]}",
                "1 {A[0]->A[2]}",
                "3 {C[0]->A[0]}",
                "3 {C[0]->A[1]}",
                "3 {C[0]->A[2]}");
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
                mockEntityIndependentValueSelector(v3, v1, v4, v2, v5),
                mockDestinationSelector(
                        elementRef(a, 0),
                        elementRef(b, 0),
                        elementRef(c, 0),
                        elementRef(c, 1)),
                false);

        solvingStarted(moveSelector, scoreDirector);

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
