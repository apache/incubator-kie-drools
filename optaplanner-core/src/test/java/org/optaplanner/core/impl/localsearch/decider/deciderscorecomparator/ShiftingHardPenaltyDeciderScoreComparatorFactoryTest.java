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

package org.optaplanner.core.impl.localsearch.decider.deciderscorecomparator;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.comparator.NaturalScoreComparator;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.comparator.FlatteningHardSoftScoreComparator;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;

public class ShiftingHardPenaltyDeciderScoreComparatorFactoryTest {

    @Test
    public void shiftingPenaltyActiveAndHardWeight() {
        // Setup
        ShiftingHardPenaltyDeciderScoreComparatorFactory deciderScoreComparatorFactory
                = new ShiftingHardPenaltyDeciderScoreComparatorFactory();
        deciderScoreComparatorFactory.setHardScoreActivationThreshold(-10);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeMinimum(1);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeMaximum(3);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeRepetitionMultiplicand(5.0);
        deciderScoreComparatorFactory.setHardWeightSurvivalRatio(0.9);

        LocalSearchSolverPhaseScope phaseScope = createPhaseScope();
        deciderScoreComparatorFactory.phaseStarted(phaseScope);
        LocalSearchStepScope stepScope = phaseScope.getLastCompletedStepScope();
        // Under hardScoreActivationThreshold 1
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        stepScope.setScore(HardSoftScore.valueOf(-11, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Under hardScoreActivationThreshold 2
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 0
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 1
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertEquals(1000, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 2
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertEquals(900, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 3
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertEquals(810, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 4
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
        // Above hardScoreActivationThreshold 5
        stepScope = nextStepScope(stepScope);
        deciderScoreComparatorFactory.stepStarted(stepScope);
        assertEquals(1000, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        stepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(stepScope);
    }

    private LocalSearchStepScope nextStepScope(LocalSearchStepScope lastCompletedStepScope) {
        LocalSearchSolverPhaseScope phaseScope = lastCompletedStepScope.getPhaseScope();
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        return new LocalSearchStepScope(phaseScope);
    }

    private LocalSearchSolverPhaseScope createPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(HardSoftScore.valueOf(-11, -200));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        phaseScope.setBestSolutionStepIndex(1000);
        LocalSearchStepScope lastCompletedStepScope = new LocalSearchStepScope(phaseScope, 1000);
        lastCompletedStepScope.setScore(HardSoftScore.valueOf(-11, -200));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        return phaseScope;
    }

}
