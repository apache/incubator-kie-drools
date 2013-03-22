/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import java.util.Random;

import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.DummyMove;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.junit.Test;

import static org.junit.Assert.*;

public class GreatDelugeAcceptorTest {

    @Test
    public void testIsAccepted() {
        // Setup
        Acceptor acceptor = new GreatDelugeAcceptor(1.20, 0.01);
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        acceptor.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        localSearchStepScope.setStepIndex(0);
        acceptor.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a1 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-2000));
        LocalSearchMoveScope a2 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1300));
        LocalSearchMoveScope a3 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1200));
        LocalSearchMoveScope b1 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1200));
        LocalSearchMoveScope b2 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-100));
        LocalSearchMoveScope c1 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1100));
        LocalSearchMoveScope c2 = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-120));
        // Do stuff
        assertEquals(false, acceptor.isAccepted(a1));
        assertEquals(false, acceptor.isAccepted(a2));
        assertEquals(true, acceptor.isAccepted(a3));
        // TODO reable a thorough test of great deluge
//        acceptor.stepEnded(localSearchStepScope);
//        assertEquals(false, acceptor.isAccepted(b1));
//        assertEquals(true, acceptor.isAccepted(b2));
//        acceptor.stepEnded(localSearchStepScope);
//        assertEquals(false, acceptor.isAccepted(c1));
//        acceptor.stepEnded(localSearchStepScope);
//        assertEquals(true, acceptor.isAccepted(c2));
//        acceptor.stepEnded(localSearchStepScope);
//        // Post conditions
//        acceptor.phaseEnded(localSearchSolverPhaseScope);
    }

    private LocalSearchSolverPhaseScope createLocalSearchSolverPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        DroolsScoreDirectorFactory scoreDirectorFactory = new DroolsScoreDirectorFactory();
        scoreDirectorFactory.setSolutionDescriptor(TestdataSolution.buildSolutionDescriptor());
        scoreDirectorFactory.setScoreDefinition(new SimpleScoreDefinition());
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector());
        solverScope.setWorkingRandom(new Random() {
            public double nextDouble() {
                return 0.2;
            }
        });
        solverScope.setBestScore(SimpleScore.valueOf(-1000));
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(phaseScope);
        lastLocalSearchStepScope.setScore(SimpleScore.valueOf(-1000));
        phaseScope.setLastCompletedStepScope(lastLocalSearchStepScope);
        return phaseScope;
    }

    public LocalSearchMoveScope createMoveScope(LocalSearchStepScope localSearchStepScope, Score score) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(localSearchStepScope);
        moveScope.setMove(new DummyMove());
        moveScope.setScore(score);
        return moveScope;
    }

}
