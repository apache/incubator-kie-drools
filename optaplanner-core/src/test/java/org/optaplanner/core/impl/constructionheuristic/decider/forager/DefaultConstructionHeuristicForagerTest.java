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

package org.optaplanner.core.impl.constructionheuristic.decider.forager;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultConstructionHeuristicForagerTest {

    @Test
    public void checkPickEarlyNever() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.NEVER);
        ConstructionHeuristicStepScope stepScope = buildStepScope(SimpleScore.valueOf(-100));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.valueOf(-110)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.valueOf(-100)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.valueOf(-90)));
        assertEquals(false, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstNonDeterioratingScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE);
        ConstructionHeuristicStepScope stepScope = buildStepScope(SimpleScore.valueOf(-100));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.valueOf(-110)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.valueOf(-100)));
        assertEquals(true, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstFeasibleScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE);
        ConstructionHeuristicStepScope stepScope = buildStepScope(HardSoftScore.valueOf(0, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(-1, -110)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(-1, -90)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(0, -110)));
        assertEquals(true, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstFeasibleScoreOrNonDeterioratingHard() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD);
        ConstructionHeuristicStepScope stepScope = buildStepScope(HardSoftScore.valueOf(-10, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(-11, -110)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(-11, -90)));
        assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.valueOf(-10, -110)));
        assertEquals(true, forager.isQuitEarly());
    }


    protected ConstructionHeuristicStepScope buildStepScope(Score lastStepScore) {
        ConstructionHeuristicPhaseScope phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        ConstructionHeuristicStepScope lastCompletedStepScope = mock(ConstructionHeuristicStepScope.class);
        when(lastCompletedStepScope.getPhaseScope()).thenReturn(phaseScope);
        when(lastCompletedStepScope.getScore()).thenReturn(lastStepScore);
        when(phaseScope.getLastCompletedStepScope()).thenReturn(lastCompletedStepScope);

        ConstructionHeuristicStepScope stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        return stepScope;
    }

    protected ConstructionHeuristicMoveScope buildMoveScope(ConstructionHeuristicStepScope stepScope, Score score) {
        ConstructionHeuristicMoveScope moveScope = mock(ConstructionHeuristicMoveScope.class);
        when(moveScope.getStepScope()).thenReturn(stepScope);
        when(moveScope.getScore()).thenReturn(score);
        return moveScope;
    }

}
