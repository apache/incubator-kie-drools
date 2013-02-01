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

package org.drools.planner.core.localsearch.decider.acceptor.greatdeluge;

import java.util.Random;

import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.scope.LocalSearchMoveScope;
import org.drools.planner.core.localsearch.scope.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.scope.LocalSearchStepScope;
import org.drools.planner.core.move.DummyMove;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.score.buildin.simple.SimpleScoreDefinition;
import org.drools.planner.core.score.director.drools.DroolsScoreDirectorFactory;
import org.drools.planner.core.solver.scope.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataSolution;
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
        LocalSearchMoveScope a1 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-2000));
        LocalSearchMoveScope a2 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-1300));
        LocalSearchMoveScope a3 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-1200));
        LocalSearchMoveScope b1 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-1200));
        LocalSearchMoveScope b2 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-100));
        LocalSearchMoveScope c1 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-1100));
        LocalSearchMoveScope c2 = createMoveScope(localSearchStepScope, DefaultSimpleScore.valueOf(-120));
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
        solverScope.setBestScore(DefaultSimpleScore.valueOf(-1000));
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(phaseScope);
        lastLocalSearchStepScope.setScore(DefaultSimpleScore.valueOf(-1000));
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
