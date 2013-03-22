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

import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.comparator.FlatteningHardSoftScoreComparator;
import org.optaplanner.core.api.score.comparator.NaturalScoreComparator;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.junit.Test;

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

        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        deciderScoreComparatorFactory.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = localSearchSolverPhaseScope.getLastCompletedStepScope();
        // Under hardScoreActivationThreshold 1
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        localSearchStepScope.setScore(HardSoftScore.valueOf(-11, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Under hardScoreActivationThreshold 2
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        localSearchSolverPhaseScope.setBestSolutionStepIndex(localSearchStepScope.getStepIndex());
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 0
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 1
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertEquals(1000, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 2
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertEquals(900, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 3
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertEquals(810, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 4
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
        // Above hardScoreActivationThreshold 5
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        assertEquals(1000, ((FlatteningHardSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        localSearchStepScope.setScore(HardSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
    }

    private LocalSearchStepScope nextStepScope(LocalSearchStepScope lastLocalSearchStepScope) {
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(lastLocalSearchStepScope.getPhaseScope());
        lastLocalSearchStepScope.getPhaseScope().setLastCompletedStepScope(lastLocalSearchStepScope);
        localSearchStepScope.setStepIndex(lastLocalSearchStepScope.getStepIndex() + 1);
        return localSearchStepScope;
    }

    private LocalSearchSolverPhaseScope createLocalSearchSolverPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        solverScope.setBestScore(HardSoftScore.valueOf(-11, -200));
        localSearchSolverPhaseScope.setBestSolutionStepIndex(1000);
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        lastLocalSearchStepScope.setStepIndex(1000);
        lastLocalSearchStepScope.setScore(HardSoftScore.valueOf(-11, -200));
        localSearchSolverPhaseScope.setLastCompletedStepScope(lastLocalSearchStepScope);
        return localSearchSolverPhaseScope;
    }

}
