/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingValueSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestDistanceMeter;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestNearbyRandom;
import org.optaplanner.core.impl.testutil.TestRandom;

class NearValueNearbyValueSelectorTest {

    @Test
    void originalSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // Used to populate the distance matrix with destinations.
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        // The replaying selector determines the destination matrix origin.
        MimicReplayingValueSelector<TestdataListSolution> mockReplayingValueSelector =
                mockReplayingValueSelector(valueSelector.getVariableDescriptor(), v3, v3, v3, v3, v3, v3, v3);

        NearValueNearbyValueSelector<TestdataListSolution> nearbyValueSelector =
                new NearValueNearbyValueSelector<>(valueSelector, mockReplayingValueSelector, new TestDistanceMeter(), null,
                        false);

        // A[0]=v1(10)
        // A[1]=v2(45)
        // A[2]=v3(50) <= origin
        // A[3]=v4(60)
        // B[0]=v5(75)

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyValueSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyValueSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyValueSelector, phaseScopeA);
        assertAllCodesOfIterableSelector(nearbyValueSelector, valueSelector.getSize(), "50", "45", "60", "75", "10");
        nearbyValueSelector.stepEnded(stepScopeA1);
        nearbyValueSelector.phaseEnded(phaseScopeA);
        nearbyValueSelector.solvingEnded(solverScope);
    }

    @Test
    void randomSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // Used to populate the distance matrix with destinations.
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        // The replaying selector determines the destination matrix origin.
        MimicReplayingValueSelector<TestdataListSolution> mockReplayingValueSelector =
                mockReplayingValueSelector(valueSelector.getVariableDescriptor(), v3, v3, v3, v3, v3, v3);

        NearValueNearbyValueSelector<TestdataListSolution> nearbyValueSelector =
                new NearValueNearbyValueSelector<>(valueSelector, mockReplayingValueSelector, new TestDistanceMeter(),
                        new TestNearbyRandom(), true);

        TestRandom testRandom = new TestRandom(3, 2, 1, 4, 0); // nearbyIndices (=> destinations)

        // A[0]=v1(10)
        // A[1]=v2(45)
        // A[2]=v3(50) <= origin
        // A[3]=v4(60)
        // B[0]=v5(75)

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyValueSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyValueSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyValueSelector, phaseScopeA);
        //                                                        3     2     1     4     0
        assertCodesOfNeverEndingIterableSelector(nearbyValueSelector, valueSelector.getSize(), "75", "60", "45", "10", "50");
        nearbyValueSelector.stepEnded(stepScopeA1);
        nearbyValueSelector.phaseEnded(phaseScopeA);
        nearbyValueSelector.solvingEnded(solverScope);
    }
}
