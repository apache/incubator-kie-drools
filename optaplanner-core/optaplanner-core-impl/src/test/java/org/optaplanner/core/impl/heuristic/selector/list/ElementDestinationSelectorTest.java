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

package org.optaplanner.core.impl.heuristic.selector.list;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestRandom;

class ElementDestinationSelectorTest {

    @Test
    void original() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v2, v1);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b, c);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v3, v1, v2);
        long destinationSize = entitySelector.getSize() + valueSelector.getSize();

        ElementDestinationSelector<TestdataListSolution> selector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);

        solvingStarted(selector, scoreDirector);

        // Entity order: [A, B, C]
        // Value order: [3, 1, 2]
        // Initial state:
        // - A [2, 1]
        // - B []
        // - C [3]

        assertAllCodesOfIterableSelector(selector, destinationSize,
                "A[0]",
                "B[0]",
                "C[0]",
                "C[1]",
                "A[2]",
                "A[1]");
    }

    @Test
    void random() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b, c, c);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v3, v2, v1);
        long destinationSize = entitySelector.getSize() + valueSelector.getSize();

        ElementDestinationSelector<TestdataListSolution> selector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, true);

        // <4 => entity selector; >=4 => value selector
        TestRandom random = new TestRandom(
                0, // => A[0]
                4, // => v3 => C[1]
                1, // => B[0]
                5, // => v2 => A[2]
                6, // => v1 => A[1]
                2, // => C[0]
                -1); // (not tested)

        solvingStarted(selector, scoreDirector, random);

        // Initial state:
        // - A [1, 2]
        // - B []
        // - C [3]

        // The destinations (A[0], C[1], ...) depend on the random number, which decides whether entitySelector or valueSelector
        // will be used; and then on the order of entities and values in the mocked selectors.
        assertCodesOfNeverEndingIterableSelector(selector, destinationSize,
                "A[0]",
                "C[1]",
                "B[0]",
                "A[2]",
                "A[1]",
                "C[0]");

        random.assertIntBoundJustRequested((int) destinationSize);
    }

    @Test
    void emptyIfThereAreNoEntities() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector = mockEntityIndependentValueSelector(
                TestdataListEntity.buildVariableDescriptorForValueList(),
                v1, v2, v3);

        ElementDestinationSelector<TestdataListSolution> randomSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, true);
        assertEmptyNeverEndingIterableSelector(randomSelector, 0);

        ElementDestinationSelector<TestdataListSolution> originalSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);
        assertAllCodesOfIterableSelector(originalSelector, 0);
    }

    @Test
    void notEmptyIfThereAreEntities() {
        TestdataListEntity a = TestdataListEntity.createWithValues("A");
        TestdataListEntity b = TestdataListEntity.createWithValues("B");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector));

        ElementDestinationSelector<TestdataListSolution> originalSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);
        assertAllCodesOfIterableSelector(originalSelector, 2, "A[0]", "B[0]");

        ElementDestinationSelector<TestdataListSolution> randomSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, true);
        TestRandom random = new TestRandom(0, 1);

        solvingStarted(randomSelector, scoreDirector, random);
        // Do not assert all codes to prevent exhausting the iterator.
        assertCodesOfNeverEndingIterableSelector(randomSelector, 2, "A[0]");
    }

    @Test
    void phaseLifecycle() {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector));

        ElementDestinationSelector<TestdataListSolution> selector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);

        SolverScope<TestdataListSolution> solverScope = solvingStarted(selector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScope = phaseStarted(selector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScope1 = stepStarted(selector, phaseScope);
        selector.stepEnded(stepScope1);

        AbstractStepScope<TestdataListSolution> stepScope2 = stepStarted(selector, phaseScope);
        selector.stepEnded(stepScope2);

        selector.phaseEnded(phaseScope);
        selector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        verifyPhaseLifecycle(valueSelector, 1, 1, 2);
    }
}
