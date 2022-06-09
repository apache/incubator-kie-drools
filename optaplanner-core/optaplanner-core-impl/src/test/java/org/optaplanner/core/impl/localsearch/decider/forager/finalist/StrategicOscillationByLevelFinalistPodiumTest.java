package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.extractSingleton;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class StrategicOscillationByLevelFinalistPodiumTest {

    @Test
    void referenceLastStepScore() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(false);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(HardSoftScore.of(-200, -5000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -100, -7000);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope0);
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -120, -4000);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope1);
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope2, -150, -1000);
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -4000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -5000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope2);
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
    }

    @Test
    void referenceBestScore() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(true);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(HardSoftScore.of(-200, -5000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -100, -7000);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope0);
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);
        solverScope.setBestScore(stepScope0.getScore());

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -120, -4000);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope1);
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);
        // do not change bestScore

        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope2, -110, -6000);
        finalistPodium.addMove(buildMoveScope(stepScope2, -110, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -1000));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope2);
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
        // do not change bestScore
    }

    protected <Solution_> LocalSearchMoveScope<Solution_> buildMoveScope(
            LocalSearchStepScope<Solution_> stepScope, int hardScore, int softScore) {
        Move<Solution_> move = mock(Move.class);
        LocalSearchMoveScope<Solution_> moveScope = new LocalSearchMoveScope<>(stepScope, 0, move);
        moveScope.setScore(HardSoftScore.of(hardScore, softScore));
        moveScope.setAccepted(true);
        return moveScope;
    }

    @Test
    void referenceLastStepScore3Levels() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(false);

        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(HardMediumSoftScore.of(-200, -5000, -10));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -100, -7000, -20);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000, -10));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100, -5));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000, -10));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope0);
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -120, -4000, -40);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000, -30));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000, -10));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000, -10));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope1);
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope2, -150, -1000, -20);
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -4000, -50));
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -5000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000, -10));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500, -10));
        assertThat(extractSingleton(finalistPodium.getFinalistList())).isSameAs(moveScope2);
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
    }

    @Test
    void alwaysPickImprovingMove() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(false);

        // Reference score is [0, -2, -3]
        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        solverScope.setBestScore(HardMediumSoftScore.of(-0, -2, -3));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        // Have two moves, scores [-1, -1, 3] and [0, -2, -1]
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -1, -1, -3);
        finalistPodium.addMove(moveScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope0, 0, -2, -1);
        finalistPodium.addMove(moveScope1);

        // The better is picked
        assertThat(finalistPodium.getFinalistList()).containsOnly(moveScope1);
    }

    protected <Solution_> LocalSearchMoveScope<Solution_> buildMoveScope(
            LocalSearchStepScope<Solution_> stepScope, int hardScore, int mediumScore, int softScore) {
        Move<Solution_> move = mock(Move.class);
        LocalSearchMoveScope<Solution_> moveScope = new LocalSearchMoveScope<>(stepScope, 0, move);
        moveScope.setScore(HardMediumSoftScore.of(hardScore, mediumScore, softScore));
        moveScope.setAccepted(true);
        return moveScope;
    }

}
