/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.localsearch.decider.acceptor.lateacceptance;

import java.util.Arrays;
import java.util.Collection;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.PlanningEntityTabuAcceptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LateAcceptanceAcceptorTest {

    @Test
    public void lateAcceptanceSize() {
        LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
        acceptor.setLateAcceptanceSize(3);

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(-1000));
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(solverPhaseScope);

        // lateScore = -1000
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        MoveScope moveScope1 = buildMoveScope(stepScope0, -500);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -800)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -1000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope0.setStep(moveScope1.getMove());
        solverScope.setBestScore(moveScope1.getScore());
        acceptor.stepEnded(stepScope0);

        // lateScore = -1000
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
        stepScope1.setStepIndex(1);
        MoveScope moveScope2 = buildMoveScope(stepScope1, -700);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -900)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -2000)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -1000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -1001)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope1.setStep(moveScope2.getMove());
        // bestScore unchanged
        acceptor.stepEnded(stepScope1);

        // lateScore = -1000
        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
        stepScope2.setStepIndex(2);
        MoveScope moveScope4 = buildMoveScope(stepScope1, -400);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -900)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -1001)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -1000)));
        assertEquals(true, acceptor.isAccepted(moveScope4));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope2.setStep(moveScope4.getMove());
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope2);

        // lateScore = -500
        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(solverPhaseScope);
        stepScope3.setStepIndex(3);
        MoveScope moveScope3 = buildMoveScope(stepScope1, -200);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, -900)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, -500)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, -501)));
        assertEquals(true, acceptor.isAccepted(moveScope3));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope3.setStep(moveScope3.getMove());
        solverScope.setBestScore(moveScope3.getScore());
        acceptor.stepEnded(stepScope3);

        // lateScore = -500 (not -700!)
        LocalSearchStepScope stepScope4 = new LocalSearchStepScope(solverPhaseScope);
        stepScope4.setStepIndex(4);
        MoveScope moveScope1Again = buildMoveScope(stepScope1, -300);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, -501)));
        assertEquals(true, acceptor.isAccepted(moveScope1Again));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, -500)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, -600)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope4.setStep(moveScope1Again.getMove());
        // bestScore unchanged
        acceptor.stepEnded(stepScope4);

        // lateScore = -400
        LocalSearchStepScope stepScope5 = new LocalSearchStepScope(solverPhaseScope);
        stepScope5.setStepIndex(5);
        MoveScope moveScope2Again = buildMoveScope(stepScope1, -300);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope5, -401)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope5, -400)));
        assertEquals(true, acceptor.isAccepted(moveScope2Again));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope5, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope5, -600)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope5.setStep(moveScope2Again.getMove());
        // bestScore unchanged
        acceptor.stepEnded(stepScope5);
        
        acceptor.phaseEnded(solverPhaseScope);
    }

    private MoveScope buildMoveScope(LocalSearchStepScope stepScope, int score) {
        MoveScope moveScope = new MoveScope(stepScope);
        Move move = mock(Move.class);
        moveScope.setMove(move);
        moveScope.setScore(new DefaultSimpleScore(score));
        return moveScope;
    }

}
