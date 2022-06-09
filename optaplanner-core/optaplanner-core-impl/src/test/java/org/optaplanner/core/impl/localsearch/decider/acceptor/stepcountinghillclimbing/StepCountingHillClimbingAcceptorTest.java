package org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptorTest;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class StepCountingHillClimbingAcceptorTest extends AbstractAcceptorTest {

    @Test
    void typeStep() {
        StepCountingHillClimbingAcceptor acceptor = new StepCountingHillClimbingAcceptor(2,
                StepCountingHillClimbingType.STEP);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // thresholdScore = -1000, lastCompletedStepScore = Integer.MIN_VALUE
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

        // thresholdScore = -1000, lastCompletedStepScore = -500
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

        // thresholdScore = -700, lastCompletedStepScore = -700
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

        // thresholdScore = -700, lastCompletedStepScore = -400
        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -900))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -700))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -701))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -2000))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        // thresholdScore = -400 (not the best score of -200!), lastCompletedStepScore = -400
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
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        // thresholdScore = -400, lastCompletedStepScore = -300
        LocalSearchStepScope<TestdataSolution> stepScope5 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope5 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -301))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -400))).isTrue();
        assertThat(acceptor.isAccepted(moveScope5)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -600))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -301))).isTrue();
        stepScope5.setStep(moveScope5.getMove());
        stepScope5.setScore(moveScope5.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope5);
        phaseScope.setLastCompletedStepScope(stepScope5);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void typeEqualOrImprovingStep() {
        StepCountingHillClimbingAcceptor acceptor = new StepCountingHillClimbingAcceptor(2,
                StepCountingHillClimbingType.EQUAL_OR_IMPROVING_STEP);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // thresholdScore = -1000, lastCompletedStepScore = Integer.MIN_VALUE
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

        // thresholdScore = -1000, lastCompletedStepScore = -500
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

        // thresholdScore = -1000, lastCompletedStepScore = -700
        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -700))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, 1000))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -1001))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -600))).isTrue();
        assertThat(acceptor.isAccepted(moveScope2)).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -700))).isTrue();
        stepScope2.setStep(moveScope2.getMove());
        stepScope2.setScore(moveScope2.getScore());
        solverScope.setBestScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        // thresholdScore = -400, lastCompletedStepScore = -400
        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -900))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -401))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -2000))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        // thresholdScore = -400, lastCompletedStepScore = -400
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
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        // thresholdScore = -300, lastCompletedStepScore = -300
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
    void typeImprovingStep() {
        StepCountingHillClimbingAcceptor acceptor = new StepCountingHillClimbingAcceptor(2,
                StepCountingHillClimbingType.IMPROVING_STEP);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // thresholdScore = -1000, lastCompletedStepScore = Integer.MIN_VALUE
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

        // thresholdScore = -1000, lastCompletedStepScore = -500
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

        // thresholdScore = -1000, lastCompletedStepScore = -700
        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -700))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, 1000))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -1001))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope2, -600))).isTrue();
        assertThat(acceptor.isAccepted(moveScope2)).isTrue();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -700))).isTrue();
        stepScope2.setStep(moveScope2.getMove());
        stepScope2.setScore(moveScope2.getScore());
        solverScope.setBestScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        // thresholdScore = -400, lastCompletedStepScore = -400
        LocalSearchStepScope<TestdataSolution> stepScope3 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope3 = buildMoveScope(stepScope1, -400);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -900))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -401))).isFalse();
        assertThat(acceptor.isAccepted(moveScope3)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope3, -2000))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -900))).isFalse();
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        // thresholdScore = -400, lastCompletedStepScore = -400
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
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope4);
        phaseScope.setLastCompletedStepScope(stepScope4);

        // thresholdScore = -400, lastCompletedStepScore = -300
        LocalSearchStepScope<TestdataSolution> stepScope5 = new LocalSearchStepScope<>(phaseScope);
        LocalSearchMoveScope<TestdataSolution> moveScope5 = buildMoveScope(stepScope1, -300);
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -301))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -400))).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -401))).isFalse();
        assertThat(acceptor.isAccepted(moveScope5)).isTrue();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -2000))).isFalse();
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope5, -600))).isFalse();
        // Repeated call
        assertThat(acceptor.isAccepted(buildMoveScope(stepScope0, -301))).isTrue();
        stepScope5.setStep(moveScope5.getMove());
        stepScope5.setScore(moveScope5.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope5);
        phaseScope.setLastCompletedStepScope(stepScope5);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    void zeroStepCountingHillClimbingSize() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StepCountingHillClimbingAcceptor(0, StepCountingHillClimbingType.STEP));
    }

    @Test
    void negativeStepCountingHillClimbingSize() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StepCountingHillClimbingAcceptor(-1, StepCountingHillClimbingType.STEP));
    }

}
