/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestRandom;

class UnionMoveSelectorTest {

    @Test
    void originSelection() {
        ArrayList<MoveSelector<TestdataSolution>> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, false);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3", "b1", "b2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyOriginSelection() {
        ArrayList<MoveSelector<TestdataSolution>> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, false);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfMoveSelector(moveSelector);

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void biasedRandomSelection() {
        ArrayList<MoveSelector<TestdataSolution>> childMoveSelectorList = new ArrayList<>();
        Map<MoveSelector<TestdataSolution>, Double> fixedProbabilityWeightMap = new HashMap<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        fixedProbabilityWeightMap.put(childMoveSelectorList.get(0), 1000.0);
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        fixedProbabilityWeightMap.put(childMoveSelectorList.get(1), 20.0);
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, true,
                        new FixedSelectorProbabilityWeightFactory<>(fixedProbabilityWeightMap));

        Random workingRandom = new TestRandom(
                1.0 / 1020.0,
                1019.0 / 1020.0,
                1000.0 / 1020.0,
                0.0,
                999.0 / 1020.0);
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        // A union of ending MoveSelectors does end, even with randomSelection
        assertAllCodesOfMoveSelector(moveSelector, "a1", "b1", "b2", "a2", "a3");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void uniformRandomSelection() {
        List<MoveSelector<TestdataSolution>> childMoveSelectorList = List.of(
                SelectorTestUtils.mockMoveSelector(DummyMove.class, new DummyMove("a1"), new DummyMove("a2"),
                        new DummyMove("a3")),
                SelectorTestUtils.mockMoveSelector(DummyMove.class, new DummyMove("b1"), new DummyMove("b2")));
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, true, null);

        Random workingRandom = new TestRandom(0, 1, 1, 0, 0);
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        // A union of ending MoveSelectors does end, even with randomSelection
        assertAllCodesOfMoveSelector(moveSelector, "a1", "b1", "b2", "a2", "a3");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyUniformRandomSelection() {
        ArrayList<MoveSelector<TestdataSolution>> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, true, null);

        Random workingRandom = new TestRandom(1);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        // A union of ending MoveSelectors does end, even with randomSelection
        assertAllCodesOfMoveSelector(moveSelector);

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyBiasedRandomSelection() {
        ArrayList<MoveSelector<TestdataSolution>> childMoveSelectorList = new ArrayList<>();
        Map<MoveSelector<TestdataSolution>, Double> fixedProbabilityWeightMap = new HashMap<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        fixedProbabilityWeightMap.put(childMoveSelectorList.get(0), 1000.0);
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        fixedProbabilityWeightMap.put(childMoveSelectorList.get(1), 20.0);
        UnionMoveSelector<TestdataSolution> moveSelector =
                new UnionMoveSelector<>(childMoveSelectorList, true,
                        new FixedSelectorProbabilityWeightFactory<>(fixedProbabilityWeightMap));

        Random workingRandom = new TestRandom(1);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope<TestdataSolution> stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        // A union of ending MoveSelectors does end, even with randomSelection
        assertAllCodesOfMoveSelector(moveSelector);

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

}
