/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.config.heuristic.selector.move;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.drools.planner.core.move.DummyMove;
import org.junit.Ignore;
import org.junit.Test;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoveSelectorConfigTest {

    @Test
    public void originalCacheTypeStep() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
                    SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {
                assertEquals(SelectionOrder.ORIGINAL, resolvedSelectionOrder);
                assertEquals(SelectionCacheType.STEP, resolvedCacheType);
                return baseMoveSelector;
            }
        };
        SolutionDescriptor solutionDescriptor = SelectorTestUtils.mockSolutionDescriptor();
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionOrder.ORIGINAL, SelectionCacheType.STEP);
        assertTrue(moveSelector instanceof CachingMoveSelector);
        moveSelector = ((CachingMoveSelector) moveSelector).getChildMoveSelector();
        assertSame(baseMoveSelector, moveSelector);
    }

    @Test
    public void originalJustInTime() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
                    SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {
                assertEquals(SelectionOrder.ORIGINAL, resolvedSelectionOrder);
                assertEquals(SelectionCacheType.JUST_IN_TIME, resolvedCacheType);
                return baseMoveSelector;
            }
        };
        SolutionDescriptor solutionDescriptor = SelectorTestUtils.mockSolutionDescriptor();
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionOrder.ORIGINAL, SelectionCacheType.JUST_IN_TIME);
        assertSame(baseMoveSelector, moveSelector);
    }

    @Test
    public void randomCacheTypeStep() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
                    SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {
                assertEquals(SelectionOrder.ORIGINAL, resolvedSelectionOrder);
                assertEquals(SelectionCacheType.STEP, resolvedCacheType);
                return baseMoveSelector;
            }
        };
        SolutionDescriptor solutionDescriptor = SelectorTestUtils.mockSolutionDescriptor();
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionOrder.RANDOM, SelectionCacheType.STEP);
        assertTrue(moveSelector instanceof ShufflingMoveSelector);
        moveSelector = ((ShufflingMoveSelector) moveSelector).getChildMoveSelector();
        assertSame(baseMoveSelector, moveSelector);
    }

    @Test
    public void randomJustInTime() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
                    SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {
                assertEquals(SelectionOrder.RANDOM, resolvedSelectionOrder);
                assertEquals(SelectionCacheType.JUST_IN_TIME, resolvedCacheType);
                return baseMoveSelector;
            }
        };
        SolutionDescriptor solutionDescriptor = SelectorTestUtils.mockSolutionDescriptor();
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionOrder.RANDOM, SelectionCacheType.JUST_IN_TIME);
        assertSame(baseMoveSelector, moveSelector);
    }

}
