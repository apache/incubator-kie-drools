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

package org.drools.planner.core.localsearch.decider.deciderscorecomparator;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.comparator.FlatteningHardAndSoftScoreComparator;
import org.drools.planner.core.score.comparator.NaturalScoreComparator;
import org.drools.planner.core.score.definition.HardAndSoftScoreDefinition;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.junit.Test;

import static org.junit.Assert.*;

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

        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = createLocalSearchSolverPhaseScope();
        deciderScoreComparatorFactory.phaseStarted(localSearchSolverPhaseScope);
        LocalSearchStepScope localSearchStepScope = localSearchSolverPhaseScope.getLastCompletedLocalSearchStepScope();
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
        localSearchSolverPhaseScope.setBestSolutionStepIndex(localSearchStepScope.getStepIndex());
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
        assertEquals(1000, ((FlatteningHardAndSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 2
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(900, ((FlatteningHardAndSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        // Above hardScoreActivationThreshold 3
        localSearchStepScope = nextStepScope(localSearchStepScope);
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        assertEquals(810, ((FlatteningHardAndSoftScoreComparator)
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
        assertEquals(1000, ((FlatteningHardAndSoftScoreComparator)
                deciderScoreComparatorFactory.createDeciderScoreComparator()).getHardWeight());
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        localSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-10, -200));
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
    }

    private LocalSearchStepScope nextStepScope(LocalSearchStepScope lastLocalSearchStepScope) {
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(lastLocalSearchStepScope.getLocalSearchSolverPhaseScope());
        lastLocalSearchStepScope.getLocalSearchSolverPhaseScope().setLastCompletedLocalSearchStepScope(lastLocalSearchStepScope);
        localSearchStepScope.setStepIndex(lastLocalSearchStepScope.getStepIndex() + 1);
        return localSearchStepScope;
    }

    private LocalSearchSolverPhaseScope createLocalSearchSolverPhaseScope() {
        DefaultSolverScope solverScope = new DefaultSolverScope();
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        solverScope.setScoreDefinition(new HardAndSoftScoreDefinition());
        solverScope.setBestScore(DefaultHardAndSoftScore.valueOf(-11, -200));
        localSearchSolverPhaseScope.setBestSolutionStepIndex(1000);
        LocalSearchStepScope lastLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        lastLocalSearchStepScope.setStepIndex(1000);
        lastLocalSearchStepScope.setScore(DefaultHardAndSoftScore.valueOf(-11, -200));
        localSearchSolverPhaseScope.setLastCompletedLocalSearchStepScope(lastLocalSearchStepScope);
        return localSearchSolverPhaseScope;
    }

}
