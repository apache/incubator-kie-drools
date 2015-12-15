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
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

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

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringMove() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Score score = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);
        recaller.processWorkingSolutionDuringMove(1, score, stepScope);
        assertEquals(null, solverScope.getBestSolution());
        assertEquals(null, solverScope.getBestScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringMove() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Solution solution = mock(Solution.class);
        Score score2 = SimpleScore.parseScore("0");
        when(solution.getScore()).thenReturn(score2);
        solverScope.setBestScore(score2);
        solverScope.setBestSolution(solution);

        Score score = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);
        recaller.processWorkingSolutionDuringMove(0, score, stepScope);
        assertEquals(solution, solverScope.getBestSolution());
        assertEquals(0, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringMove() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        InnerScoreDirector sd = mock(InnerScoreDirector.class);
        solverScope.setScoreDirector(sd);
        Solution helpSolution = mock(Solution.class);
        when(helpSolution.getScore()).thenReturn(SimpleScore.parseScore("-2"));
        when(sd.cloneWorkingSolution()).thenReturn(helpSolution);

        solverScope.setBestUninitializedVariableCount(1);
        Score score = SimpleScore.parseScore("-1");

        recaller.processWorkingSolutionDuringMove(0, score, stepScope);
        assertEquals(helpSolution, solverScope.getBestSolution());
        assertEquals(-2, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringMove() {
        BestSolutionRecaller recaller = new BestSolutionRecaller();
        recaller.setSolverEventSupport(mock(SolverEventSupport.class));
        DefaultSolverScope solverScope = new DefaultSolverScope();
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        when(phaseScope.getSolverScope()).thenReturn(solverScope);
        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);

        Solution solution = mock(Solution.class);
        Score score = SimpleScore.parseScore("-2");
        when(solution.getScore()).thenReturn(score);
        solverScope.setBestScore(score);
        solverScope.setBestSolution(solution);

        InnerScoreDirector sd = mock(InnerScoreDirector.class);
        solverScope.setScoreDirector(sd);
        Solution helpSolution = mock(Solution.class);
        when(helpSolution.getScore()).thenReturn(SimpleScore.parseScore("0"));
        when(sd.cloneWorkingSolution()).thenReturn(helpSolution);

        Score score2 = SimpleScore.parseScore("-1");
        solverScope.setBestUninitializedVariableCount(0);
        recaller.processWorkingSolutionDuringMove(0, score2, stepScope);
        assertEquals(helpSolution, solverScope.getBestSolution());
        assertEquals(0, ((SimpleScore) solverScope.getBestScore()).getScore());
        assertEquals(0, solverScope.getBestUninitializedVariableCount());
    }

}
