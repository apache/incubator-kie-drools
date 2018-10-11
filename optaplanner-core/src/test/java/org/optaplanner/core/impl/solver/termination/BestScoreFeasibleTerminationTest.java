/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.solver.termination;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.definition.FeasibilityScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BestScoreFeasibleTerminationTest {

    @Test
    public void solveTermination() {
        FeasibilityScoreDefinition scoreDefinition = mock(FeasibilityScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[]{});
        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        when(solverScope.getStartingInitializedScore()).thenReturn(HardSoftScore.of(-100, -100));
        when(solverScope.isBestSolutionInitialized()).thenReturn(true);

        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-100, -100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-80, -100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.2, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-60, -100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.4, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-40, -100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(-20, -100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.8, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(0, -100));
        assertEquals(true, termination.isSolverTerminated(solverScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        FeasibilityScoreDefinition scoreDefinition = mock(FeasibilityScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[]{});
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        when(phaseScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        when(phaseScope.getStartingScore()).thenReturn(HardSoftScore.of(-100, -100));
        when(phaseScope.isBestSolutionInitialized()).thenReturn(true);

        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-100, -100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-80, -100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.2, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-60, -100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.4, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-40, -100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(-20, -100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.8, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(0, -100));
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

    @Test
    public void calculateTimeGradientBendableScoreHHSSS() {
        FeasibilityScoreDefinition scoreDefinition = mock(FeasibilityScoreDefinition.class);
        when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(2);
        BestScoreFeasibleTermination termination = new BestScoreFeasibleTermination(scoreDefinition,
                new double[]{0.75});

        // Normal cases
        // Smack in the middle
        assertEquals(0.6, termination.calculateFeasibilityTimeGradient(
                BendableScore.of(new int[]{-10, -100}, new int[]{-50, -60, -70}),
                BendableScore.of(new int[]{-4, -40}, new int[]{-50, -60, -70})), 0.0);
    }

}
