/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;

public class MoveSelectorConfigTest extends AbstractSelectorConfigTest {

    public static class AssertingMoveSelectorConfig extends MoveSelectorConfig<AssertingMoveSelectorConfig> {

        private final MoveSelector baseMoveSelector;
        private final SelectionCacheType expectedMinimumCacheType;
        private final boolean expectedRandomSelection;

        public AssertingMoveSelectorConfig(MoveSelector baseMoveSelector,
                SelectionCacheType expectedMinimumCacheType, boolean expectedRandomSelection) {
            this.baseMoveSelector = baseMoveSelector;
            this.expectedMinimumCacheType = expectedMinimumCacheType;
            this.expectedRandomSelection = expectedRandomSelection;
        }

        @Override
        protected MoveSelector buildBaseMoveSelector(
                HeuristicConfigPolicy configPolicy,
                SelectionCacheType minimumCacheType, boolean randomSelection) {
            assertThat(minimumCacheType).isEqualTo(expectedMinimumCacheType);
            assertThat(randomSelection).isEqualTo(expectedRandomSelection);
            return baseMoveSelector;
        }

        @Override
        public AssertingMoveSelectorConfig copyConfig() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void phaseOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.PHASE, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class);
        assertThat(moveSelector)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void stepOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.STEP, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class);
        assertThat(moveSelector)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void justInTimeOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.JUST_IN_TIME, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isSameAs(baseMoveSelector);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.JUST_IN_TIME);
    }

    @Test
    public void phaseRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.PHASE, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class);
        assertThat(moveSelector)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void stepRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.STEP, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class);
        assertThat(moveSelector)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void justInTimeRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.JUST_IN_TIME, true);
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isSameAs(baseMoveSelector);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.JUST_IN_TIME);
    }

    @Test
    public void phaseShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.PHASE, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((ShufflingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void stepShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new AssertingMoveSelectorConfig(
                baseMoveSelector, SelectionCacheType.STEP, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((ShufflingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    public void justInTimeShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(
                    HeuristicConfigPolicy configPolicy,
                    SelectionCacheType minimumCacheType, boolean randomSelection) {
                return baseMoveSelector;
            }

            @Override
            public AbstractConfig inherit(AbstractConfig inheritedConfig) {
                throw new UnsupportedOperationException();
            }

            @Override
            public AbstractConfig copyConfig() {
                throw new UnsupportedOperationException();
            }
        };
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        assertThatIllegalArgumentException().isThrownBy(() -> moveSelectorConfig.buildMoveSelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM));
    }

}
