/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.solver.recaller;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.domain.solution.AbstractSolution;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BestSolutionRecallerTest {

    private static <Solution_> DefaultSolverScope<Solution_> createSolverScope() {
        DefaultSolverScope<Solution_> solverScope = new DefaultSolverScope<>();
        InnerScoreDirector<Solution_> scoreDirector = mock(InnerScoreDirector.class);
        SolutionDescriptor<Solution_> solutionDescriptor = mock(SolutionDescriptor.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        solverScope.setScoreDirector(scoreDirector);
        return solverScope;
    }

    private static <Solution_> ConstructionHeuristicStepScope<Solution_> setupConstrunctionHeuristics(
            DefaultSolverScope<Solution_> solverScope) {
        ConstructionHeuristicPhaseScope<Solution_>  phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope<Solution_>  stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        return stepScope;
    }

    private static <Solution_> BestSolutionRecaller<Solution_> createBestSolutionRecaller() {
        BestSolutionRecaller<Solution_> recaller = new BestSolutionRecaller<>();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        return recaller;
    }

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringStep() {
        DefaultSolverScope<AbstractSolution> solverScope = new DefaultSolverScope<>();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution);

        when(stepScope.getUninitializedVariableCount()).thenReturn(2);
        solverScope.setBestUninitializedVariableCount(1);

        BestSolutionRecaller recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(null, solverScope.getBestSolution());
        assertEquals(null, solverScope.getBestScore());
        assertEquals(1, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringStep() {
        DefaultSolverScope<AbstractSolution> solverScope = new DefaultSolverScope<>();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(score);

        AbstractSolution solution2 = mock(AbstractSolution.class);
        Score score2 = SimpleScore.parseScore("-1");
        when(solution2.getScore()).thenReturn(score2);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution2);
        when(stepScope.getScore()).thenReturn(score2);

        when(stepScope.getUninitializedVariableCount()).thenReturn(0);
        solverScope.setBestUninitializedVariableCount(0);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(score, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringStep() {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score = SimpleScore.parseScore("0");
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(solution)).thenReturn(score);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution);

        when(stepScope.getUninitializedVariableCount()).thenReturn(1);
        solverScope.setBestUninitializedVariableCount(2);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(score, solverScope.getBestScore());
        assertEquals(1, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringStep() {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score = SimpleScore.parseScore("-1");
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(solution)).thenReturn(score);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(score);

        AbstractSolution solution2 = mock(AbstractSolution.class);
        Score score2 = SimpleScore.parseScore("0");
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(solution2)).thenReturn(score2);
        when(stepScope.getScore()).thenReturn(score2);
        when(stepScope.createOrGetClonedSolution()).thenReturn(solution2);

        when(stepScope.getUninitializedVariableCount()).thenReturn(0);
        solverScope.setBestUninitializedVariableCount(0);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        assertEquals(solution2, solverScope.getBestSolution());
        assertEquals(score2, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringMove() {
        DefaultSolverScope<AbstractSolution> solverScope = new DefaultSolverScope<>();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        Score score = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(1, score, stepScope);
        assertEquals(null, solverScope.getBestSolution());
        assertEquals(null, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }


    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringMove() {
        DefaultSolverScope<AbstractSolution> solverScope = new DefaultSolverScope<>();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score2 = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score2);
        solverScope.setBestScore(score2);
        solverScope.setBestSolution(solution);

        Score score = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(0, score, stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(0, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringMove() {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution helpSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(helpSolution))
                .thenReturn(SimpleScore.parseScore("-2"));
        when(solverScope.getScoreDirector().cloneWorkingSolution()).thenReturn(helpSolution);

        solverScope.setBestUninitializedVariableCount(1);
        Score score = SimpleScore.parseScore("-1");

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(0, score, stepScope);
        assertEquals(helpSolution, solverScope.getBestSolution());
        assertEquals(-2, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringMove() {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution solution = mock(AbstractSolution.class);
        Score score = SimpleScore.parseScore("-2");
        when(solution.getScore()).thenReturn(score);
        solverScope.setBestScore(score);
        solverScope.setBestSolution(solution);

        AbstractSolution helpSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(helpSolution))
                .thenReturn(SimpleScore.parseScore("0"));
        when(solverScope.getScoreDirector().cloneWorkingSolution()).thenReturn(helpSolution);

        Score score2 = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(0, score2, stepScope);
        assertEquals(helpSolution, solverScope.getBestSolution());
        assertEquals(0, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

}
