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
        SimpleScore originalBestScore = SimpleScore.ofUninitialized(-1, -300);
        SimpleScore stepScore = SimpleScore.ofUninitialized(-2, 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of(0);
        Score stepScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.ofUninitialized(-2, 0);
        Score stepScore = SimpleScore.ofUninitialized(-1, 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of(-1);
        Score stepScore = SimpleScore.of(0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    protected void doProcessWorkingSolutionDuringStep(Score originalBestScore, Score stepScore,
            boolean stepImprovesBestSolution) {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        AbstractSolution originalBestSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(originalBestSolution)).thenReturn(originalBestScore);
        solverScope.setBestSolution(originalBestSolution);
        solverScope.setBestScore(originalBestScore);

        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);
        AbstractSolution stepSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(stepSolution)).thenReturn(stepScore);
        when(stepScope.getScore()).thenReturn(stepScore);
        when(stepScope.createOrGetClonedSolution()).thenReturn(stepSolution);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringStep(stepScope);
        if (stepImprovesBestSolution) {
            assertEquals(stepSolution, solverScope.getBestSolution());
            assertEquals(stepScore, solverScope.getBestScore());
        } else {
            assertEquals(originalBestSolution, solverScope.getBestSolution());
            assertEquals(originalBestScore, solverScope.getBestScore());
        }
    }

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(-10);
        Score moveScore = SimpleScore.ofUninitialized(-1, -1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }


    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(0);
        Score moveScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.ofUninitialized(-1, 0);
        SimpleScore moveScore = SimpleScore.of(-2);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(-2);
        Score moveScore = SimpleScore.of(-1);
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }

    protected void doProcessWorkingSolutionDuringMove(Score originalBestScore, Score moveScore,
            boolean moveImprovesBestSolution) {
        DefaultSolverScope<AbstractSolution> solverScope = createSolverScope();
        AbstractSolution originalBestSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(originalBestSolution)).thenReturn(originalBestScore);
        solverScope.setBestSolution(originalBestSolution);
        solverScope.setBestScore(originalBestScore);

        ConstructionHeuristicStepScope<AbstractSolution> stepScope = setupConstrunctionHeuristics(solverScope);

        AbstractSolution moveSolution = mock(AbstractSolution.class);
        when(solverScope.getScoreDirector().getSolutionDescriptor().getScore(moveSolution))
                .thenReturn(moveScore);
        when(solverScope.getScoreDirector().cloneWorkingSolution()).thenReturn(moveSolution);

        BestSolutionRecaller<AbstractSolution> recaller = createBestSolutionRecaller();
        recaller.processWorkingSolutionDuringMove(moveScore, stepScope);
        if (moveImprovesBestSolution) {
            assertEquals(moveSolution, solverScope.getBestSolution());
            assertEquals(moveScore, solverScope.getBestScore());
        } else {
            assertEquals(originalBestSolution, solverScope.getBestSolution());
            assertEquals(originalBestScore, solverScope.getBestScore());
        }
    }

}
