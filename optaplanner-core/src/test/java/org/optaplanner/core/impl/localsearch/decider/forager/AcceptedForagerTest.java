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

package org.optaplanner.core.impl.localsearch.decider.forager;

import java.util.Random;

import org.optaplanner.core.impl.localsearch.decider.deciderscorecomparator.NaturalDeciderScoreComparatorFactory;
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
import static org.mockito.Mockito.*;

public class AcceptedForagerTest {

    @Test
    public void pickMoveMaxScoreAccepted() {
        // Setup
        Forager forager = new AcceptedForager(PickEarlyType.NEVER, Integer.MAX_VALUE);
        ((AcceptedForager) forager).setDeciderScoreComparatorFactory(new NaturalDeciderScoreComparatorFactory()); // TODO
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        forager.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = createStepScope(localSearchSolverPhaseScope);
        forager.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope b = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope c = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope d = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-2), true);
        LocalSearchMoveScope e = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-300), true);
        // Do stuff
        forager.addMove(a);
        assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        assertFalse(forager.isQuitEarly());
        LocalSearchMoveScope pickedScope = forager.pickMove(localSearchStepScope);
        // Post conditions
        assertSame(d, pickedScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    @Test
    public void pickMoveMaxScoreUnaccepted() {
        // Setup
        Forager forager = new AcceptedForager(PickEarlyType.NEVER, Integer.MAX_VALUE);
        ((AcceptedForager) forager).setDeciderScoreComparatorFactory(new NaturalDeciderScoreComparatorFactory()); // TODO
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        forager.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = createStepScope(localSearchSolverPhaseScope);
        forager.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope b = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope c = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope d = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-2), false);
        LocalSearchMoveScope e = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-300), false);
        // Do stuff
        forager.addMove(a);
        assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        assertFalse(forager.isQuitEarly());
        LocalSearchMoveScope pickedScope = forager.pickMove(localSearchStepScope);
        // Post conditions
        assertSame(b, pickedScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    @Test
    public void pickMoveFirstBestScoreImproving() {
        // Setup
        Forager forager = new AcceptedForager(PickEarlyType.FIRST_BEST_SCORE_IMPROVING, Integer.MAX_VALUE);
        ((AcceptedForager) forager).setDeciderScoreComparatorFactory(new NaturalDeciderScoreComparatorFactory()); // TODO
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        forager.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = createStepScope(localSearchSolverPhaseScope);
        forager.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope b = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope c = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-300), true);
        LocalSearchMoveScope d = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), true);
        // Do stuff
        forager.addMove(a);
        assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(localSearchStepScope);
        assertSame(d, pickedScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    @Test
    public void pickMoveFirstLastStepScoreImproving() {
        // Setup
        Forager forager = new AcceptedForager(PickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING, Integer.MAX_VALUE);
        ((AcceptedForager) forager).setDeciderScoreComparatorFactory(new NaturalDeciderScoreComparatorFactory()); // TODO
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        forager.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = createStepScope(localSearchSolverPhaseScope);
        forager.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope b = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-300), true);
        LocalSearchMoveScope c = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-4000), true);
        LocalSearchMoveScope d = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), true);
        // Do stuff
        forager.addMove(a);
        assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(localSearchStepScope);
        assertSame(d, pickedScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    @Test
    public void pickMoveAcceptedRandomly() {
        // Setup
        Forager forager = new AcceptedForager(PickEarlyType.NEVER, 3);
        ((AcceptedForager) forager).setDeciderScoreComparatorFactory(new NaturalDeciderScoreComparatorFactory()); // TODO
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        forager.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = createStepScope(localSearchSolverPhaseScope);
        forager.stepStarted(localSearchStepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope b = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope c = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope d = createMoveScope(localSearchStepScope, SimpleScore.valueOf(-20), true);
        // Do stuff
        forager.addMove(a);
        assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(localSearchStepScope);
        assertSame(b, pickedScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    private LocalSearchSolverPhaseScope createLocalSearchSolverPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        DroolsScoreDirectorFactory scoreDirectorFactory = new DroolsScoreDirectorFactory();
        scoreDirectorFactory.setSolutionDescriptor(TestdataSolution.buildSolutionDescriptor());
        scoreDirectorFactory.setScoreDefinition(new SimpleScoreDefinition());
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector());
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(2)).thenReturn(0);
        solverScope.setWorkingRandom(workingRandom);
        solverScope.setBestScore(SimpleScore.valueOf(-10));
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        lastLocalSearchStepScope.setScore(SimpleScore.valueOf(-100));
        localSearchSolverPhaseScope.setLastCompletedStepScope(lastLocalSearchStepScope);
        return localSearchSolverPhaseScope;
    }

    private LocalSearchStepScope createStepScope(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        return localSearchStepScope;
    }

    public LocalSearchMoveScope createMoveScope(LocalSearchStepScope localSearchStepScope, Score score, boolean accepted) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(localSearchStepScope);
        moveScope.setMove(new DummyMove());
        moveScope.setScore(score);
        moveScope.setAccepted(accepted);
        return moveScope;
    }

}
