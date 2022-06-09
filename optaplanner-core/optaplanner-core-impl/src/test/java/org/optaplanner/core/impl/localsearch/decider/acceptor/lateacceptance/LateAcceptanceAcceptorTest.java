package org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptorTest;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class LateAcceptanceAcceptorTest extends AbstractAcceptorTest {

    @Test
    void lateAcceptanceSize() {
        LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
        acceptor.setLateAcceptanceSize(3);
        acceptor.setHillClimbingEnabled(false);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(SimpleScore.of(Integer.MIN_VALUE));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // lateScore = -1000
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -500);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        assertThat(acceptor.isAccepted(moveScope0)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -800))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -1000))).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        stepScope0.setStep(moveScope0.getMove());
        stepScope0.setScore(moveScope0.getScore());
        solverScope.setBestScore(moveScope0.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        // lateScore = -1000
        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -700);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -900))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -2000))).isFalse();
        assertThat(acceptor.isAccepted(moveScope1)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -1000))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -1001))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        stepScope1.setStep(moveScope1.getMove());
        stepScope1.setScore(moveScope1.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        // lateScore = -1000
        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -900))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -1001))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -1000))).isTrue();
        assertThat(acceptor.isAccepted(moveScope2)).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        stepScope2.setStep(moveScope2.getMove());
        stepScope2.setScore(moveScope2.getScore());
        solverScope.setBestScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        // lateScore = -500
        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope1, -200);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -900))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -500))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -501))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -2000))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        solverScope.setBestScore(moveScope3.getScore());
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        // lateScore = -700 (not the best score of -500!)
        LocalSearchStepScope<TestdataSolution> stepScope4 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope4 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -700))).isTrue();
        assertThat(acceptor.isAccepted(moveScope4)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -500))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -701))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -700))).isTrue();
        stepScope4.setStep(moveScope4.getMove());
        stepScope4.setScore(moveScope4.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        // lateScore = -400
        LocalSearchStepScope<TestdataSolution> stepScope5 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope5 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -401))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -400))).isTrue();
        assertThat(acceptor.isAccepted(moveScope5)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -600))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -401))).isFalse();
        stepScope5.setStep(moveScope5.getMove());
        stepScope5.setScore(moveScope5.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope5);
        phaseScope.setLastCompletedStepScope(stepScope5);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void hillClimbingEnabled() {
        LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
        acceptor.setLateAcceptanceSize(2);
        acceptor.setHillClimbingEnabled(true);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // lateScore = -1000, lastCompletedStepScore = Integer.MIN_VALUE
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -500);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        assertThat(acceptor.isAccepted(moveScope0)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -800))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -1000))).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        stepScope0.setStep(moveScope0.getMove());
        stepScope0.setScore(moveScope0.getScore());
        solverScope.setBestScore(moveScope0.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        // lateScore = -1000, lastCompletedStepScore = -500
        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -700);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -900))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -2000))).isFalse();
        assertThat(acceptor.isAccepted(moveScope1)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -1000))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope1, -1001))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isTrue();
        stepScope1.setStep(moveScope1.getMove());
        stepScope1.setScore(moveScope1.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        // lateScore = -500, lastCompletedStepScore = -700
        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -700))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -701))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -600))).isTrue();
        assertThat(acceptor.isAccepted(moveScope2)).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -700))).isTrue();
        stepScope2.setStep(moveScope2.getMove());
        stepScope2.setScore(moveScope2.getScore());
        solverScope.setBestScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        // lateScore = -700, lastCompletedStepScore = -400
        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope1, -200);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -900))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -700))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -701))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -2000))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        solverScope.setBestScore(moveScope3.getScore());
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        // lateScore = -400 (not the best score of -200!), lastCompletedStepScore = -200
        LocalSearchStepScope<TestdataSolution> stepScope4 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope4 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -400))).isTrue();
        assertThat(acceptor.isAccepted(moveScope4)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -500))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope4, -401))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -400))).isTrue();
        stepScope4.setStep(moveScope4.getMove());
        stepScope4.setScore(moveScope4.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        // lateScore = -200, lastCompletedStepScore = -300
        LocalSearchStepScope<TestdataSolution> stepScope5 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope5 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -301))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -400))).isFalse();
        assertThat(acceptor.isAccepted(moveScope5)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -600))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -301))).isFalse();
        stepScope5.setStep(moveScope5.getMove());
        stepScope5.setScore(moveScope5.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope5);
        phaseScope.setLastCompletedStepScope(stepScope5);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void zeroLateAcceptanceSize() {
        LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
        acceptor.setLateAcceptanceSize(0);
        assertThatIllegalArgumentException().isThrownBy(() -> acceptor.phaseStarted(null));
    }

    @Test
    void negativeLateAcceptanceSize() {
        LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
        acceptor.setLateAcceptanceSize(-1);
        assertThatIllegalArgumentException().isThrownBy(() -> acceptor.phaseStarted(null));
    }
}
