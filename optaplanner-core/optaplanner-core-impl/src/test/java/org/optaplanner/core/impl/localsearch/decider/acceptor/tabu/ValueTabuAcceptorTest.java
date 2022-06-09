package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.FixedTabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class ValueTabuAcceptorTest {

    @Test
    void tabuSize() {
        ValueTabuAcceptor acceptor = new ValueTabuAcceptor("");
        acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(2));
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(0));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope0, v1);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0))).isTrue();
        assertThat(acceptor.isAccepted(moveScope1)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v4))).isTrue();
        // repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v2))).isTrue();
        stepScope0.setStep(moveScope1.getMove());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, v2);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v1))).isFalse();
        assertThat(acceptor.isAccepted(moveScope2)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v4))).isTrue();
        // repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v2))).isTrue();
        stepScope1.setStep(moveScope2.getMove());
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope4 = buildMoveScope(stepScope2, v4);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v3))).isTrue();
        assertThat(acceptor.isAccepted(moveScope4)).isTrue();
        // repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v2))).isFalse();
        stepScope2.setStep(moveScope4.getMove());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope3, v3);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v1))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v2))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v4))).isFalse();
        // repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v2))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        LocalSearchStepScope<TestdataSolution> stepScope4 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope1Again = buildMoveScope(stepScope4, v1);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, v0))).isTrue();
        assertThat(acceptor.isAccepted(moveScope1Again)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, v4))).isFalse();
        // repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, v2))).isTrue();
        stepScope4.setStep(moveScope1Again.getMove());
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void tabuSizeMultipleEntitiesPerStep() {
        ValueTabuAcceptor acceptor = new ValueTabuAcceptor("");
        acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(2));
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(0));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v1))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0, v1))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v0, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v1, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v1, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v1, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v2, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v2, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, v3, v4))).isTrue();
        stepScope0.setStep(buildMoveScope(stepScope0, v0, v2).getMove());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v1))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v0, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v1, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v1, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v1, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v2, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v2, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, v3, v4))).isTrue();
        stepScope1.setStep(buildMoveScope(stepScope1, v1).getMove());
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v3))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v4))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v0, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v1, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v1, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v1, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v2, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v2, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, v3, v4))).isTrue();
        stepScope2.setStep(buildMoveScope(stepScope2, v3, v4).getMove());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0, v2))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v0, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v1, v2))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v1, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v1, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v2, v3))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v2, v4))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, v3, v4))).isFalse();
        stepScope3.setStep(buildMoveScope(stepScope3, v0).getMove());
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void aspiration() {
        ValueTabuAcceptor acceptor = new ValueTabuAcceptor("");
        acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(2));
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-100));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        stepScope0.setStep(buildMoveScope(stepScope0, v1).getMove());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -120, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -20, v0))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -120, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -20, v1))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -120, v0, v1))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -20, v0, v1))).isTrue();
        stepScope1.setStep(buildMoveScope(stepScope1, -20, v1).getMove());
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        acceptor.phaseEnded(phaseScope);
    }

    private <Solution_> LocalSearchMoveScope<Solution_> buildMoveScope(
            LocalSearchStepScope<Solution_> stepScope, TestdataValue... values) {
        return buildMoveScope(stepScope, 0, values);
    }

    private <Solution_> LocalSearchMoveScope<Solution_> buildMoveScope(
            LocalSearchStepScope<Solution_> stepScope, int score, TestdataValue... values) {
        Move move = mock(Move.class);
        when(move.getPlanningValues()).thenReturn(Arrays.asList(values));
        LocalSearchMoveScope<Solution_> moveScope = new LocalSearchMoveScope<>(stepScope, 0, move);
        moveScope.setScore(SimpleScore.of(score));
        return moveScope;
    }

}
