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

package org.optaplanner.core.impl.heuristic.selector.entity.nearby;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockEntitySelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingEntitySelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingOfEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testutil.TestNearbyRandom;
import org.optaplanner.core.impl.testutil.TestRandom;

class NearEntityNearbyEntitySelectorTest {

    @Test
    void randomSelection() {
        final TestdataEntity morocco = new TestdataEntity("Morocco");
        final TestdataEntity spain = new TestdataEntity("Spain");
        final TestdataEntity australia = new TestdataEntity("Australia");
        final TestdataEntity brazil = new TestdataEntity("Brazil");

        EntitySelector<TestdataSolution> childEntitySelector = mockEntitySelector(TestdataEntity.buildEntityDescriptor(),
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataEntity, TestdataEntity> meter = (origin, destination) -> {
            if (origin == morocco) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == spain) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == australia) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == brazil) {
                if (destination == morocco) {
                    return 55.0;
                } else if (destination == spain) {
                    return 53.0;
                } else if (destination == australia) {
                    return 61.0;
                } else if (destination == brazil) {
                    return 0.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };

        MimicReplayingEntitySelector<TestdataSolution> mimicReplayingEntitySelector =
                // The last entity () is not used, it just makes the selector appear never ending.
                mockReplayingEntitySelector(TestdataEntity.buildEntityDescriptor(), morocco, spain, australia, brazil, morocco);

        NearEntityNearbyEntitySelector<TestdataSolution> entitySelector = new NearEntityNearbyEntitySelector<>(
                childEntitySelector, mimicReplayingEntitySelector, meter, new TestNearbyRandom(), true);

        TestRandom workingRandom = new TestRandom(0, 1, 2, 0);

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        SolverScope<TestdataSolution> solverScope = solvingStarted(entitySelector, scoreDirector, workingRandom);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(entitySelector, solverScope);
        AbstractStepScope<TestdataSolution> stepScopeA1 = stepStarted(entitySelector, phaseScopeA);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, childEntitySelector.getSize() - 1,
                "Spain", "Brazil", "Spain", "Spain");
        entitySelector.stepEnded(stepScopeA1);
        entitySelector.phaseEnded(phaseScopeA);
        entitySelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childEntitySelector, 1, 1, 1);
    }
}
