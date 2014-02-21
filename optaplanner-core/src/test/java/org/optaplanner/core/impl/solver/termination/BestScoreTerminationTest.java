/*
 * Copyright 2014 JBoss Inc
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
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BestScoreTerminationTest {

    @Test
    public void solveTermination() {
        Termination termination = new BestScoreTermination(SimpleScore.valueOf(-1000));
        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(solverScope.isBestSolutionInitialized()).thenReturn(true);
        when(solverScope.getStartingInitializedScore()).thenReturn(SimpleScore.valueOf(-1100));

        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1100));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1040));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1040));
        assertEquals(false, termination.isSolverTerminated(solverScope));
        assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1000));
        assertEquals(true, termination.isSolverTerminated(solverScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.valueOf(-900));
        assertEquals(true, termination.isSolverTerminated(solverScope));
        assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        Termination termination = new BestScoreTermination(SimpleScore.valueOf(-1000));
        AbstractSolverPhaseScope phaseScope = mock(AbstractSolverPhaseScope.class);
        when(phaseScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(phaseScope.isBestSolutionInitialized()).thenReturn(true);
        when(phaseScope.getStartingScore()).thenReturn(SimpleScore.valueOf(-1100));

        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1100));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1040));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1040));
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-1000));
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getBestScore()).thenReturn(SimpleScore.valueOf(-900));
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

}
