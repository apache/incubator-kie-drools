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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.RandomSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestRandom;

class RandomSubListSwapMoveSelectorTest {

    @Test
    void sameEntityUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 10;

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 1,
                0, 2,
                0, 3,
                0, 4,
                0, 5,
                0, 6,
                0, 7,
                0, 8,
                0, 9,
                1, 8,
                2, 7,
                3, 6,
                4, 5,
                5, 4,
                6, 3,
                7, 2,
                8, 1,
                9, 0,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount,
                "{A[0+4]} <-> {A[0+4]}",
                "{A[0+4]} <-> {A[0+3]}",
                "{A[0+4]} <-> {A[1+3]}",
                "{A[0+4]} <-> {A[0+2]}",
                "{A[0+4]} <-> {A[1+2]}",
                "{A[0+4]} <-> {A[2+2]}",
                "{A[0+4]} <-> {A[0+1]}",
                "{A[0+4]} <-> {A[1+1]}",
                "{A[0+4]} <-> {A[2+1]}",
                "{A[0+4]} <-> {A[3+1]}",
                "{A[0+3]} <-> {A[2+1]}",
                "{A[1+3]} <-> {A[1+1]}",
                "{A[0+2]} <-> {A[0+1]}",
                "{A[1+2]} <-> {A[2+2]}",
                "{A[1+2]} <-> {A[2+2]}", // equivalent to {A[2+2]} <-> {A[1+2]}
                "{A[0+1]} <-> {A[0+2]}",
                "{A[1+1]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[2+1]}", // equivalent to {A[2+1]} <-> {A[0+3]}
                "{A[0+4]} <-> {A[3+1]}"); // equivalent to {A[3+1]} <-> {A[0+4]}

        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void reversing() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B", v5, v6);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 6 + 3;

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);
        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor, v1),
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor, v5),
                        minimumSubListSize,
                        maximumSubListSize),
                true);

        // Each row is consumed by 1 createUpcomingSelection() call.
        // Columns are: left subList index, right subList index, reversing flag.
        TestRandom random = new TestRandom(
                0, 2, 1,
                0, 1, 0,
                0, 0, 1,
                1, 0, 0,
                2, 0, 1,
                3, 0, 1,
                99, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount * 2,
                "{A[0+3]} <-reversing-> {B[1+1]}",
                "{A[0+3]} <-> {B[0+1]}",
                "{A[0+3]} <-reversing-> {B[0+2]}",
                "{A[0+2]} <-> {B[0+2]}",
                "{A[1+2]} <-reversing-> {B[0+2]}",
                "{A[0+1]} <-reversing-> {B[0+2]}");
    }

    @Test
    void sameEntityWithSubListSizeBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 3;
        int subListCount = 5;

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 1,
                0, 2,
                0, 3,
                0, 4,
                1, 3,
                2, 2,
                3, 1,
                4, 0,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount,
                "{A[0+3]} <-> {A[0+3]}",
                "{A[0+3]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[0+2]}",
                "{A[0+3]} <-> {A[1+2]}",
                "{A[0+3]} <-> {A[2+2]}",
                "{A[1+3]} <-> {A[1+2]}",
                "{A[0+2]} <-> {A[0+2]}",
                "{A[1+2]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[2+2]}"); // equivalent to {A[2+2]} <-> {A[0+3]}

        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void emptyWhenMinimumSubListSizeGreaterThanListSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 100;
        int maximumSubListSize = Integer.MAX_VALUE;

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        solvingStarted(moveSelector, scoreDirector);

        assertEmptyNeverEndingMoveSelector(moveSelector);
    }

    @Test
    void skipSubListsSmallerThanMinimumSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 2;
        int subListCount = 2;

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);
        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b, c);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor, v1, v4),
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor, v4, v1),
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 0,
                0, 1,
                1, 0,
                1, 1,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount,
                "{A[0+2]} <-> {A[0+2]}",
                "{A[0+2]} <-> {A[0+2]}",
                "{A[0+2]} <-> {A[1+2]}",
                "{A[0+2]} <-> {A[1+2]}", // normalized from {A[1+2]} <-> {A[0+2]}
                "{A[1+2]} <-> {A[1+2]}");

        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void sizeUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 6 + 1;

        // The entity selector must be complete; it affects subList calculation and the move selector size.
        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b, c);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount);
    }

    @Test
    void sizeWithBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListValue v11 = new TestdataListValue("11");
        TestdataListValue v12 = new TestdataListValue("12");
        TestdataListValue v13 = new TestdataListValue("13");
        TestdataListValue v21 = new TestdataListValue("21");
        TestdataListValue v22 = new TestdataListValue("22");
        TestdataListValue v23 = new TestdataListValue("23");
        TestdataListValue v24 = new TestdataListValue("24");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4, v5, v6, v7);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v11, v12, v13);
        TestdataListEntity d = TestdataListEntity.createWithValues("D", v21, v22, v23, v24);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 3;
        int maximumSubListSize = 5;
        int subListCount = 12 + 1 + 3;

        // The entity selector must be complete; it affects subList calculation and the move selector size.
        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(a, b, c, d);
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1);
        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * subListCount);
    }

    @Test
    void phaseLifecycle() {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> leftValueSelector =
                mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor);
        EntityIndependentValueSelector<TestdataListSolution> rightValueSelector =
                mockNeverEndingEntityIndependentValueSelector(listVariableDescriptor);
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        leftValueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                new RandomSubListSelector<>(
                        entitySelector,
                        rightValueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                false);

        SolverScope<TestdataListSolution> solverScope = solvingStarted(moveSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScope = phaseStarted(moveSelector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScope1 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope1);

        AbstractStepScope<TestdataListSolution> stepScope2 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope2);

        moveSelector.phaseEnded(phaseScope);
        moveSelector.solvingEnded(solverScope);

        // The invocation counts are multiplied for the entity selector because it is used by both left and right
        // subList selectors and each registers the entity selector to its phaseLifecycleSupport.
        verifyPhaseLifecycle(entitySelector, 2, 2, 4);
        verifyPhaseLifecycle(leftValueSelector, 1, 1, 2);
        verifyPhaseLifecycle(rightValueSelector, 1, 1, 2);
    }
}
