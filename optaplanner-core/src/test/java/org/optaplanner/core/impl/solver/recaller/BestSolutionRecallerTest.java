package org.optaplanner.core.impl.solver.recaller;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BestSolutionRecallerTest {

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringStep() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Solution solution = mock(Solution.class);
        Score score = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution);

        when(stepScope.getUninitializedVariableCount()).thenReturn(2);
        solverScope.setBestUninitializedVariableCount(1);
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(null, solverScope.getBestSolution());
        assertEquals(null, solverScope.getBestScore());
        assertEquals(1, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringStep() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Solution solution = mock(Solution.class);
        Score score = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(score);

        Solution solution2 = mock(Solution.class);
        Score score2 = SimpleScore.parseScore("-1");
        when(solution2.getScore()).thenReturn(score2);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution2);
        when(stepScope.getScore()).thenReturn(score2);

        when(stepScope.getUninitializedVariableCount()).thenReturn(0);
        solverScope.setBestUninitializedVariableCount(0);
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(score, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDurintStep() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Solution solution = mock(Solution.class);
        Score score = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution);

        when(stepScope.getUninitializedVariableCount()).thenReturn(1);
        solverScope.setBestUninitializedVariableCount(2);
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(score, solverScope.getBestScore());
        assertEquals(1, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringStep() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        
        Solution solution = mock(Solution.class);
        Score score = SimpleScore.parseScore("-1");
        when(solution.getScore()).thenReturn(score);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(score);

        Solution solution2 = mock(Solution.class);
        Score score2 = SimpleScore.parseScore("0");
        when(solution2.getScore()).thenReturn(score2);
        when(stepScope.getScore()).thenReturn(score2);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution2);

        when(stepScope.getUninitializedVariableCount()).thenReturn(0);
        solverScope.setBestUninitializedVariableCount(0);
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution2, solverScope.getBestSolution());
        assertEquals(score2, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

}
