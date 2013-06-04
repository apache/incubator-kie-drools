/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.latesimulatedannealing;

import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LateSimulatedAnnealingAcceptorTest {

    @Test
    public void lateSimulatedAnnealingSize2() {
        LateSimulatedAnnealingAcceptor acceptor = new LateSimulatedAnnealingAcceptor();
        acceptor.setLateSimulatedAnnealingSize(2);

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(SimpleScore.valueOf(-1000));
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextDouble()).thenReturn(0.3);
        solverScope.setWorkingRandom(workingRandom);
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, -1);
        lastCompletedStepScope.setScore(SimpleScore.valueOf(Integer.MIN_VALUE));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // lateScore = -1000, bestScore = -1000
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope0, -500);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -800)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -1000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope0.setStep(moveScope1.getMove());
        stepScope0.setScore(moveScope1.getScore());
        solverScope.setBestScore(moveScope1.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        // lateScore = -1000, bestScore = -500
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope2 = buildMoveScope(stepScope1, -700);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -900)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -2000)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -1000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -1100)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -1200)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope1.setStep(moveScope2.getMove());
        stepScope1.setScore(moveScope2.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        // lateScore = -500, bestScore = -500
        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope4 = buildMoveScope(stepScope1, -400);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -700)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -701)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -600)));
        assertEquals(true, acceptor.isAccepted(moveScope4));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -700))); // Repeated call
        stepScope2.setStep(moveScope4.getMove());
        stepScope2.setScore(moveScope4.getScore());
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        // lateScore = -700, bestScore = -400
        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope3 = buildMoveScope(stepScope1, -200);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, -900)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, -700)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, -750)));
        assertEquals(true, acceptor.isAccepted(moveScope3));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope3.setStep(moveScope3.getMove());
        stepScope3.setScore(moveScope3.getScore());
        solverScope.setBestScore(moveScope3.getScore());
        acceptor.stepEnded(stepScope3);
        phaseScope.setLastCompletedStepScope(stepScope3);

        acceptor.phaseEnded(phaseScope);
    }

    private LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope, int score) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        Move move = mock(Move.class);
        moveScope.setMove(move);
        moveScope.setScore(SimpleScore.valueOf(score));
        return moveScope;
    }

}
