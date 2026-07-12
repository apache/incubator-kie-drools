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

package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingValueSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.ManualValueMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestDistanceMeter;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestNearbyRandom;
import org.optaplanner.core.impl.testutil.TestRandom;

class NearValueNearbyDestinationSelectorTest {

    @Test
    void randomSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(e1, e2);
        when(entitySelector.getEntityDescriptor()).thenReturn(TestdataListEntity.buildEntityDescriptor());

        // Used to populate the distance matrix with destinations.
        ElementDestinationSelector<TestdataListSolution> childDestinationSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, true);

        MimicReplayingValueSelector<TestdataListSolution> mockReplayingValueSelector =
                mockReplayingValueSelector(valueSelector.getVariableDescriptor(), v3, v3, v3, v3, v3, v3, v3, v3);

        NearValueNearbyDestinationSelector<TestdataListSolution> nearbyDestinationSelector =
                new NearValueNearbyDestinationSelector<>(childDestinationSelector, mockReplayingValueSelector,
                        new TestDistanceMeter(), new TestNearbyRandom(), true);

        TestRandom testRandom = new TestRandom(0, 1, 2, 3, 4, 5, 6);

        // A[0]=v1(10)
        // A[1]=v2(45)
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75)

        // IMPORTANT: For example, when v4(60) is returned from the distance matrix, the ElementRef is A[4]
        // although v4 is at A[3]. It's because the destination is "after" the nearby value (so its index + 1).

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyDestinationSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyDestinationSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyDestinationSelector, phaseScopeA);
        assertCodesOfNeverEndingIterableSelector(nearbyDestinationSelector, entitySelector.getSize() + valueSelector.getSize(),
                // 50      45      60      75      10      0       0
                "A[3]", "A[2]", "A[4]", "B[1]", "A[1]", "A[0]", "B[0]");
        nearbyDestinationSelector.stepEnded(stepScopeA1);
        nearbyDestinationSelector.phaseEnded(phaseScopeA);
        nearbyDestinationSelector.solvingEnded(solverScope);
    }

    @Test
    void originalSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        ManualValueMimicRecorder<TestdataListSolution> valueMimicRecorder = new ManualValueMimicRecorder<>(valueSelector);

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(e1, e2);
        when(entitySelector.getEntityDescriptor()).thenReturn(TestdataListEntity.buildEntityDescriptor());

        ElementDestinationSelector<TestdataListSolution> childDestinationSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);

        NearValueNearbyDestinationSelector<TestdataListSolution> nearbyDestinationSelector =
                new NearValueNearbyDestinationSelector<>(childDestinationSelector,
                        new MimicReplayingValueSelector<>(valueMimicRecorder), new TestDistanceMeter(), null, false);

        // A[0]=v1(10)
        // A[1]=v2(45)
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75)

        // IMPORTANT: For example, when v4(60) is returned from the distance matrix, the ElementRef is A[4]
        // although v4 is at A[3]. It's because the destination is "after" the nearby value (so its index + 1).

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyDestinationSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyDestinationSelector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyDestinationSelector, phaseScopeA);
        valueMimicRecorder.setRecordedValue(v3);
        assertAllCodesOfIterableSelector(nearbyDestinationSelector, entitySelector.getSize() + valueSelector.getSize(),
                // 50      45      60      75      10      0       0
                "A[3]", "A[2]", "A[4]", "B[1]", "A[1]", "A[0]", "B[0]");
        nearbyDestinationSelector.stepEnded(stepScopeA1);

        AbstractStepScope<TestdataListSolution> stepScopeA2 = stepStarted(nearbyDestinationSelector, phaseScopeA);
        valueMimicRecorder.setRecordedValue(v5);
        assertAllCodesOfIterableSelector(nearbyDestinationSelector, entitySelector.getSize() + valueSelector.getSize(),
                // 75      60      50      45      10      0       0
                "B[1]", "A[4]", "A[3]", "A[2]", "A[1]", "A[0]", "B[0]");
        nearbyDestinationSelector.stepEnded(stepScopeA2);

        nearbyDestinationSelector.phaseEnded(phaseScopeA);
        nearbyDestinationSelector.solvingEnded(solverScope);
    }
}
