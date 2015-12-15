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

package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.extractSingleton;

public class StrategicOscillationByLevelFinalistPodiumTest {

    @Test
    public void referenceLastStepScore() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(false);

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(HardSoftScore.valueOf(-200, -5000));
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(solverScope);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope moveScope0 = buildMoveScope(stepScope0, -100, -7000);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000));
        assertSame(moveScope0, extractSingleton(finalistPodium.getFinalistList()));
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope1, -120, -4000);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000));
        assertSame(moveScope1, extractSingleton(finalistPodium.getFinalistList()));
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope moveScope2 = buildMoveScope(stepScope2, -150, -1000);
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -4000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -5000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500));
        assertSame(moveScope2, extractSingleton(finalistPodium.getFinalistList()));
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
    }

    @Test
    public void referenceBestScore() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(true);

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(HardSoftScore.valueOf(-200, -5000));
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(solverScope);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope moveScope0 = buildMoveScope(stepScope0, -100, -7000);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000));
        assertSame(moveScope0, extractSingleton(finalistPodium.getFinalistList()));
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);
        solverScope.setBestScore(stepScope0.getScore());

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope1, -120, -4000);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000));
        assertSame(moveScope1, extractSingleton(finalistPodium.getFinalistList()));
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);
        // do not change bestScore

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope moveScope2 = buildMoveScope(stepScope2, -110, -6000);
        finalistPodium.addMove(buildMoveScope(stepScope2, -110, -8000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -1000));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500));
        assertSame(moveScope2, extractSingleton(finalistPodium.getFinalistList()));
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
        // do not change bestScore
    }

    protected LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope, int hardScore, int softScore) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        Move move = mock(Move.class);
        moveScope.setAccepted(true);
        moveScope.setMove(move);
        moveScope.setScore(HardSoftScore.valueOf(hardScore, softScore));
        return moveScope;
    }

    @Test
    public void referenceLastStepScore3Levels() {
        StrategicOscillationByLevelFinalistPodium finalistPodium = new StrategicOscillationByLevelFinalistPodium(false);

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(HardMediumSoftScore.valueOf(-200, -5000, -10));
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(solverScope);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, -1);
        lastCompletedStepScope.setScore(solverScope.getBestScore());
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        finalistPodium.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope0);
        LocalSearchMoveScope moveScope0 = buildMoveScope(stepScope0, -100, -7000, -20);
        finalistPodium.addMove(buildMoveScope(stepScope0, -150, -2000, -10));
        finalistPodium.addMove(moveScope0);
        finalistPodium.addMove(buildMoveScope(stepScope0, -100, -7100, -5));
        finalistPodium.addMove(buildMoveScope(stepScope0, -200, -1000, -10));
        assertSame(moveScope0, extractSingleton(finalistPodium.getFinalistList()));
        stepScope0.setScore(moveScope0.getScore());
        finalistPodium.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope1);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope1, -120, -4000, -40);
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -8000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope1, -100, -7000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -3000, -10));
        finalistPodium.addMove(moveScope1);
        finalistPodium.addMove(buildMoveScope(stepScope1, -150, -2000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope1, -200, -1000, -10));
        assertSame(moveScope1, extractSingleton(finalistPodium.getFinalistList()));
        stepScope1.setScore(moveScope1.getScore());
        finalistPodium.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        finalistPodium.stepStarted(stepScope2);
        LocalSearchMoveScope moveScope2 = buildMoveScope(stepScope2, -150, -1000, -20);
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -4000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope2, -120, -5000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -3000, -10));
        finalistPodium.addMove(moveScope2);
        finalistPodium.addMove(buildMoveScope(stepScope2, -150, -2000, -10));
        finalistPodium.addMove(buildMoveScope(stepScope2, -160, -500, -10));
        assertSame(moveScope2, extractSingleton(finalistPodium.getFinalistList()));
        stepScope2.setScore(moveScope2.getScore());
        finalistPodium.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);
    }

    protected LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope,
            int hardScore, int mediumScore, int softScore) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        Move move = mock(Move.class);
        moveScope.setAccepted(true);
        moveScope.setMove(move);
        moveScope.setScore(HardMediumSoftScore.valueOf(hardScore, mediumScore, softScore));
        return moveScope;
    }

}
