/**
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

package org.drools.planner.core.localsearch.decider.deciderscorecomparator;

import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.comparator.NaturalScoreComparator;
import org.drools.planner.core.score.definition.HardAndSoftScoreDefinition;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoffrey De Smet
 */
public class ShiftingHardPenaltyDeciderScoreComparatorFactoryTest {

    @Test
    public void testShiftingPenaltyActiveAndHardWeight() {
        // Setup
        ShiftingHardPenaltyDeciderScoreComparatorFactory deciderScoreComparatorFactory
                = new ShiftingHardPenaltyDeciderScoreComparatorFactory();
        deciderScoreComparatorFactory.setHardScoreActivationThreshold(-10);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeMinimum(1);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeMaximum(3);
        deciderScoreComparatorFactory.setSuccessiveNoHardChangeRepetitionMultiplicand(5.0);
        deciderScoreComparatorFactory.setHardWeightSurvivalRatio(0.9);

        LocalSearchSolverScope localSearchSolverScope = createLocalSearchSolverScope();
        deciderScoreComparatorFactory.solvingStarted(localSearchSolverScope);
        LocalSearchStepScope localSearchStepScope = localSearchSolverScope.getLastCompletedLocalSearchStepScope();
        // Under hardScoreActivationThreshold 1
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-11, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Under hardScoreActivationThreshold 2
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        localSearchSolverScope.setBestSolutionStepIndex(localSearchStepScope.getStepIndex());
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 0
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 1
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(1000, ((HardPenaltyDeciderScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 2
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(900, ((HardPenaltyDeciderScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 3
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(810, ((HardPenaltyDeciderScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 4
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertTrue(deciderScoreComparatorFactory.createDeciderScoreComparator() instanceof NaturalScoreComparator);
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 5
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(1000, ((HardPenaltyDeciderScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
    }

    private LocalSearchStepScope nextStepScope(LocalSearchStepScope lastLocalSearchStepScope) {
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(lastLocalSearchStepScope.getLocalSearchSolverScope());
        lastLocalSearchStepScope.getLocalSearchSolverScope().setLastCompletedLocalSearchStepScope(lastLocalSearchStepScope);
        localSearchStepScope.setStepIndex(lastLocalSearchStepScope.getStepIndex() + 1);
        return localSearchStepScope;
    }

    private LocalSearchSolverScope createLocalSearchSolverScope() {
        LocalSearchSolverScope localSearchSolverScope = new LocalSearchSolverScope();
        localSearchSolverScope.setScoreDefinition(new HardAndSoftScoreDefinition());
        localSearchSolverScope.setBestScore(DefaultHardAndSoftScore.valueOf(-11, -200));
        localSearchSolverScope.setBestSolutionStepIndex(1000);
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverScope);
        lastLocalSearchStepScope.setStepIndex(1000);
        lastLocalSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-11, -200));
        localSearchSolverScope.setLastCompletedLocalSearchStepScope(lastLocalSearchStepScope);
        return localSearchSolverScope;
    }

}
