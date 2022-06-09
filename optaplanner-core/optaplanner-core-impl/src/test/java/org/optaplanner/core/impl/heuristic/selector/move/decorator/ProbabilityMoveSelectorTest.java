package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestRandom;

class ProbabilityMoveSelectorTest {

    @Test
    void randomSelection() {
        MoveSelector<TestdataSolution> childMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("e1"), new DummyMove("e2"), new DummyMove("e3"), new DummyMove("e4"));

        SelectionProbabilityWeightFactory<TestdataSolution, DummyMove> probabilityWeightFactory =
                (scoreDirector, move) -> {
                    switch (move.getCode()) {
                        case "e1":
                            return 1000.0;
                        case "e2":
                            return 200.0;
                        case "e3":
                            return 30.0;
                        case "e4":
                            return 4.0;
                        default:
                            throw new IllegalStateException("Unknown move (" + move + ").");
                    }
                };
        MoveSelector<TestdataSolution> moveSelector = new ProbabilityMoveSelector<>(childMoveSelector,
                SelectionCacheType.STEP, probabilityWeightFactory);

        Random workingRandom = new TestRandom(
                1222.0 / 1234.0,
                111.0 / 1234.0,
                0.0,
                1230.0 / 1234.0,
                1199.0 / 1234.0);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 4L, "e3", "e1", "e1", "e4", "e2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelector, 1, 1, 1);
        verify(childMoveSelector, times(1)).iterator();
    }

}
