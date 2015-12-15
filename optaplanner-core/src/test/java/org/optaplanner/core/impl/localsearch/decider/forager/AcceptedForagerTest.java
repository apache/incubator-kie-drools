/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchPickEarlyType;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.HighestScoreFinalistPodium;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AcceptedForagerTest {

    @Test
    public void pickMoveMaxScoreAccepted() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-2), true);
        LocalSearchMoveScope e = createMoveScope(stepScope, SimpleScore.valueOf(-300), true);
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
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveMaxScoreUnaccepted() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-2), false);
        LocalSearchMoveScope e = createMoveScope(stepScope, SimpleScore.valueOf(-300), false);
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
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        assertSame(b, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstBestScoreImproving() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.FIRST_BEST_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-300), true);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
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
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstLastStepScoreImproving() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-1), false);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-300), true);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-4000), true);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-20), true);
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
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieRandomly() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, 4, true);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope e = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
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
        assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertSame(c, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieFirst() {
        // Setup
        Forager forager = new AcceptedForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, 4, false);
        LocalSearchPhaseScope phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = createStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope a = createMoveScope(stepScope, SimpleScore.valueOf(-20), false);
        LocalSearchMoveScope b = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope c = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
        LocalSearchMoveScope d = createMoveScope(stepScope, SimpleScore.valueOf(-20), true);
        LocalSearchMoveScope e = createMoveScope(stepScope, SimpleScore.valueOf(-1), true);
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
        assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertSame(b, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    private LocalSearchPhaseScope createPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(solverScope);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(TestdataSolution.buildSolutionDescriptor());
        when(scoreDirector.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        solverScope.setScoreDirector(scoreDirector);
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(3)).thenReturn(1);
        solverScope.setWorkingRandom(workingRandom);
        solverScope.setBestScore(SimpleScore.valueOf(-10));
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(phaseScope);
        lastLocalSearchStepScope.setScore(SimpleScore.valueOf(-100));
        phaseScope.setLastCompletedStepScope(lastLocalSearchStepScope);
        return phaseScope;
    }

    private LocalSearchStepScope createStepScope(LocalSearchPhaseScope phaseScope) {
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        return stepScope;
    }

    public LocalSearchMoveScope createMoveScope(LocalSearchStepScope stepScope, Score score, boolean accepted) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        moveScope.setMove(new DummyMove());
        moveScope.setScore(score);
        moveScope.setAccepted(accepted);
        return moveScope;
    }

}
