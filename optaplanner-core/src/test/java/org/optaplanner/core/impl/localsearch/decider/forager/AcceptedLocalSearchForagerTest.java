/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Test;
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
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class AcceptedLocalSearchForagerTest {

    @Test
    public void pickMoveMaxScoreAccepted() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-20), true);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-1), false);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-20), false);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-2), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of(-300), true);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(e);
        assertThat(forager.isQuitEarly()).isFalse();
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        assertThat(pickedScope).isSameAs(d);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveMaxScoreUnaccepted() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-20), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-1), false);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-20), false);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-2), false);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of(-300), false);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(e);
        assertThat(forager.isQuitEarly()).isFalse();
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        assertThat(pickedScope).isSameAs(b);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstBestScoreImproving() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.FIRST_BEST_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-1), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-20), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-300), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-1), true);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isTrue();
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertThat(pickedScope).isSameAs(d);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstLastStepScoreImproving() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-1), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-300), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-4000), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-20), true);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isTrue();
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertThat(pickedScope).isSameAs(d);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieRandomly() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, 4, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-20), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-1), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-1), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-20), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of(-1), true);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(e);
        assertThat(forager.isQuitEarly()).isTrue();
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertThat(pickedScope).isSameAs(c);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieFirst() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(),
                LocalSearchPickEarlyType.NEVER, 4, false);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope<>(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of(-20), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of(-1), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of(-1), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of(-20), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of(-1), true);
        // Do stuff
        forager.addMove(a);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(b);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(c);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(d);
        assertThat(forager.isQuitEarly()).isFalse();
        forager.addMove(e);
        assertThat(forager.isQuitEarly()).isTrue();
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        assertThat(pickedScope).isSameAs(b);
        forager.phaseEnded(phaseScope);
    }

    private LocalSearchPhaseScope<TestdataSolution> createPhaseScope() {
        SolverScope<TestdataSolution> solverScope = new SolverScope<>();
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(TestdataSolution.buildSolutionDescriptor());
        when(scoreDirector.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        solverScope.setScoreDirector(scoreDirector);
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(3)).thenReturn(1);
        solverScope.setWorkingRandom(workingRandom);
        solverScope.setBestScore(SimpleScore.of(-10));
        LocalSearchStepScope<TestdataSolution> lastLocalSearchStepScope = new LocalSearchStepScope<>(phaseScope);
        lastLocalSearchStepScope.setScore(SimpleScore.of(-100));
        phaseScope.setLastCompletedStepScope(lastLocalSearchStepScope);
        return phaseScope;
    }

    public LocalSearchMoveScope<TestdataSolution> createMoveScope(LocalSearchStepScope<TestdataSolution> stepScope,
            Score score, boolean accepted) {
        LocalSearchMoveScope<TestdataSolution> moveScope = new LocalSearchMoveScope<>(stepScope, 0, new DummyMove());
        moveScope.setScore(score);
        moveScope.setAccepted(accepted);
        return moveScope;
    }

}
