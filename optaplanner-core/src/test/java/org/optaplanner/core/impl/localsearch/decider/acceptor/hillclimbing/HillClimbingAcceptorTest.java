/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.hillclimbing;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptorTest;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;

public class HillClimbingAcceptorTest extends AbstractAcceptorTest {

    @Test
    public void hillClimbingEnabled() {
        HillClimbingAcceptor acceptor = new HillClimbingAcceptor();

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(SimpleScore.valueOf(-1000));
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(solverScope);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, -1);
        lastCompletedStepScope.setScore(SimpleScore.valueOf(-1000));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);

        // lastCompletedStepScore = -1000
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope0 = buildMoveScope(stepScope0, -500);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900)));
        assertEquals(true, acceptor.isAccepted(moveScope0));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -800)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -1000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope0.setStep(moveScope0.getMove());
        stepScope0.setScore(moveScope0.getScore());
        solverScope.setBestScore(moveScope0.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        // lastCompletedStepScore = -500
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope1, 600);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -900)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -2000)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -700)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -1000)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -500)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -501)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call
        stepScope1.setStep(moveScope1.getMove());
        stepScope1.setScore(moveScope1.getScore());
        // bestScore unchanged
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        acceptor.phaseEnded(phaseScope);
    }

}
