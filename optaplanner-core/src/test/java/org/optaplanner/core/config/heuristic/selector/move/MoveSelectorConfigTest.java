/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.heuristic.selector.move;

import org.junit.Test;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertSame;

public class MoveSelectorConfigTest extends AbstractSelectorConfigTest {

    @Test
    public void phaseOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.PHASE, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(CachingMoveSelector.class, moveSelector);
        assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.PHASE, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((CachingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test
    public void stepOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.STEP, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(CachingMoveSelector.class, moveSelector);
        assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.STEP, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((CachingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test
    public void justInTimeOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.JUST_IN_TIME, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertSame(baseMoveSelector, moveSelector);
        assertEquals(SelectionCacheType.JUST_IN_TIME, moveSelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.PHASE, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(CachingMoveSelector.class, moveSelector);
        assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.PHASE, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((CachingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test
    public void stepRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.STEP, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(CachingMoveSelector.class, moveSelector);
        assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.STEP, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((CachingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test
    public void justInTimeRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.JUST_IN_TIME, minimumCacheType);
                assertEquals(true, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertSame(baseMoveSelector, moveSelector);
        assertEquals(SelectionCacheType.JUST_IN_TIME, moveSelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.PHASE, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.PHASE, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((ShufflingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test
    public void stepShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                assertEquals(SelectionCacheType.STEP, minimumCacheType);
                assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(ShufflingMoveSelector.class, moveSelector);
        assertEquals(SelectionCacheType.STEP, moveSelector.getCacheType());
        assertSame(baseMoveSelector, ((ShufflingMoveSelector) moveSelector).getChildMoveSelector());
    }

    @Test(expected = IllegalArgumentException.class)
    public void justInTimeShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
    }

}
