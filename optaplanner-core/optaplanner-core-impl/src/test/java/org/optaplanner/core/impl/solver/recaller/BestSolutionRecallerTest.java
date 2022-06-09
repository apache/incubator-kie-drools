package org.optaplanner.core.impl.solver.recaller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class BestSolutionRecallerTest {

    private static <Solution_> SolverScope<Solution_> createSolverScope() {
        SolverScope<Solution_> solverScope = new SolverScope<>();
        InnerScoreDirector<Solution_, ?> scoreDirector = mock(InnerScoreDirector.class);
        SolutionDescriptor<Solution_> solutionDescriptor = mock(SolutionDescriptor.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        solverScope.setScoreDirector(scoreDirector);
        return solverScope;
    }

    private static <Solution_> ConstructionHeuristicStepScope<Solution_> setupConstructionHeuristics(
            SolverScope<Solution_> solverScope) {
        ConstructionHeuristicPhaseScope<Solution_> phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope<Solution_> stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        return stepScope;
    }

    private static <Solution_> BestSolutionRecaller<Solution_> createBestSolutionRecaller() {
        BestSolutionRecaller<Solution_> recaller = new BestSolutionRecaller<>();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        return recaller;
    }

    @Test
    void unimprovedUninitializedProcessWorkingSolutionDuringStep() {
        SimpleScore originalBestScore = SimpleScore.ofUninitialized(-1, -300);
        SimpleScore stepScore = SimpleScore.ofUninitialized(-2, 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    void unimprovedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of(0);
        Score stepScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    void improvedUninitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.ofUninitialized(-2, 0);
        Score stepScore = SimpleScore.ofUninitialized(-1, 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    @Test
    void improvedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of(-1);
        Score stepScore = SimpleScore.of(0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    protected void doProcessWorkingSolutionDuringStep(Score originalBestScore, Score stepScore,
            boolean stepImprovesBestSolution) {
        SolverScope<TestdataSolution> solverScope = createSolverScope();
        TestdataSolution originalBestSolution = mock(TestdataSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(originalBestSolution))
                .thenReturn(originalBestScore);
        solverScope.setBestSolution(originalBestSolution);
        solverScope.setBestScore(originalBestScore);

        ConstructionHeuristicStepScope<TestdataSolution> stepScope = setupConstructionHeuristics(solverScope);
        TestdataSolution stepSolution = mock(TestdataSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(stepSolution)).thenReturn(stepScore);
        when(stepScope.getScore()).thenReturn(stepScore);
        when(stepScope.createOrGetClonedSolution()).thenReturn(stepSolution);

        BestSolutionRecaller<TestdataSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        if (stepImprovesBestSolution) {
            assertThat(solverScope.getBestSolution()).isEqualTo(stepSolution);
            assertThat(solverScope.getBestScore()).isEqualTo(stepScore);
        } else {
            assertThat(solverScope.getBestSolution()).isEqualTo(originalBestSolution);
            assertThat(solverScope.getBestScore()).isEqualTo(originalBestScore);
        }
    }

    @Test
    void unimprovedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(-10);
        Score moveScore = SimpleScore.ofUninitialized(-1, -1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }

    @Test
    void unimprovedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(0);
        Score moveScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }

    @Test
    void improvedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.ofUninitialized(-1, 0);
        SimpleScore moveScore = SimpleScore.of(-2);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }

    @Test
    void improvedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(-2);
        Score moveScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }

    protected void doProcessWorkingSolutionDuringMove(Score originalBestScore, Score moveScore,
            boolean moveImprovesBestSolution) {
        SolverScope<TestdataSolution> solverScope = createSolverScope();
        TestdataSolution originalBestSolution = mock(TestdataSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(originalBestSolution))
                .thenReturn(originalBestScore);
        solverScope.setBestSolution(originalBestSolution);
        solverScope.setBestScore(originalBestScore);

        ConstructionHeuristicStepScope<TestdataSolution> stepScope = setupConstructionHeuristics(solverScope);

        TestdataSolution moveSolution = mock(TestdataSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(moveSolution))
                .thenReturn(moveScore);
        when(solverScope.getScoreDirector().cloneWorkingSolution()).thenReturn(moveSolution);

        BestSolutionRecaller<TestdataSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(moveScore, stepScope);
        if (moveImprovesBestSolution) {
            assertThat(solverScope.getBestSolution()).isEqualTo(moveSolution);
            assertThat(solverScope.getBestScore()).isEqualTo(moveScore);
        } else {
            assertThat(solverScope.getBestSolution()).isEqualTo(originalBestSolution);
            assertThat(solverScope.getBestScore()).isEqualTo(originalBestScore);
        }
    }

}
