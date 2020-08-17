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

package org.optaplanner.core.impl.heuristic.selector.move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ProbabilityMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.SortingMoveSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class MoveSelectorFactoryTest extends AbstractSelectorConfigTest {

    @Test
    void phaseOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.PHASE, false);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void stepOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.STEP, false);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void justInTimeOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.JUST_IN_TIME, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isSameAs(baseMoveSelector);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.JUST_IN_TIME);
    }

    @Test
    void phaseRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.PHASE, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void stepRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.STEP, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(CachingMoveSelector.class)
                .isNotInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((CachingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void justInTimeRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.JUST_IN_TIME, true);
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isSameAs(baseMoveSelector);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.JUST_IN_TIME);
    }

    @Test
    void phaseShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.PHASE, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
        assertThat(((ShufflingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void stepShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        MoveSelectorFactory moveSelectorFactory =
                new AssertingMoveSelectorFactory(moveSelectorConfig, baseMoveSelector, SelectionCacheType.STEP, false);
        moveSelectorConfig.setCacheType(SelectionCacheType.STEP);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelector moveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ShufflingMoveSelector.class);
        assertThat(moveSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
        assertThat(((ShufflingMoveSelector) moveSelector).getChildMoveSelector()).isSameAs(baseMoveSelector);
    }

    @Test
    void justInTimeShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        MoveSelectorFactory moveSelectorFactory = new DummyMoveSelectorFactory(moveSelectorConfig, baseMoveSelector);

        assertThatIllegalArgumentException().isThrownBy(() -> moveSelectorFactory
                .buildMoveSelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM));
    }

    @Test
    void validateSorting_incompatibleSelectionOrder() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setSorterOrder(SelectionSorterOrder.ASCENDING);

        DummyMoveSelectorFactory moveSelectorFactory = new DummyMoveSelectorFactory(moveSelectorConfig, baseMoveSelector);
        assertThatIllegalArgumentException().isThrownBy(() -> moveSelectorFactory.validateSorting(SelectionOrder.RANDOM))
                .withMessageContaining("that is not " + SelectionOrder.SORTED);
    }

    @Test
    void applySorting_withoutAnySortingClass() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setSorterOrder(SelectionSorterOrder.ASCENDING);

        DummyMoveSelectorFactory moveSelectorFactory = new DummyMoveSelectorFactory(moveSelectorConfig, baseMoveSelector);
        assertThatIllegalArgumentException().isThrownBy(
                () -> moveSelectorFactory.applySorting(SelectionCacheType.PHASE, SelectionOrder.SORTED, baseMoveSelector))
                .withMessageContaining("The moveSelectorConfig")
                .withMessageContaining("needs");
    }

    @Test
    void applySorting_withSorterComparatorClass() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setSorterOrder(SelectionSorterOrder.ASCENDING);
        moveSelectorConfig.setSorterComparatorClass(DummyComparator.class);

        DummyMoveSelectorFactory moveSelectorFactory = new DummyMoveSelectorFactory(moveSelectorConfig, baseMoveSelector);
        MoveSelector sortingMoveSelector =
                moveSelectorFactory.applySorting(SelectionCacheType.PHASE, SelectionOrder.SORTED, baseMoveSelector);
        assertThat(sortingMoveSelector).isExactlyInstanceOf(SortingMoveSelector.class);
    }

    @Test
    void applyProbability_withProbabilityWeightFactoryClass() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        DummyMoveSelectorConfig moveSelectorConfig = new DummyMoveSelectorConfig();
        moveSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        moveSelectorConfig.setProbabilityWeightFactoryClass(DummySelectionProbabilityWeightFactory.class);

        DummyMoveSelectorFactory moveSelectorFactory = new DummyMoveSelectorFactory(moveSelectorConfig, baseMoveSelector);
        MoveSelector sortingMoveSelector = moveSelectorFactory.buildMoveSelector(buildHeuristicConfigPolicy(),
                SelectionCacheType.PHASE, SelectionOrder.PROBABILISTIC);
        assertThat(sortingMoveSelector).isExactlyInstanceOf(ProbabilityMoveSelector.class);
    }

    static class DummyMoveSelectorConfig extends MoveSelectorConfig<DummyMoveSelectorConfig> {

        @Override
        public DummyMoveSelectorConfig copyConfig() {
            throw new UnsupportedOperationException();
        }
    }

    static class DummyMoveSelectorFactory extends AbstractMoveSelectorFactory<DummyMoveSelectorConfig> {

        protected final MoveSelector baseMoveSelector;

        DummyMoveSelectorFactory(DummyMoveSelectorConfig moveSelectorConfig, MoveSelector baseMoveSelector) {
            super(moveSelectorConfig);
            this.baseMoveSelector = baseMoveSelector;
        }

        @Override
        protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType,
                boolean randomSelection) {
            return baseMoveSelector;
        }
    }

    static class AssertingMoveSelectorFactory extends DummyMoveSelectorFactory {

        private final SelectionCacheType expectedMinimumCacheType;
        private final boolean expectedRandomSelection;

        AssertingMoveSelectorFactory(DummyMoveSelectorConfig moveSelectorConfig, MoveSelector baseMoveSelector,
                SelectionCacheType expectedMinimumCacheType, boolean expectedRandomSelection) {
            super(moveSelectorConfig, baseMoveSelector);
            this.expectedMinimumCacheType = expectedMinimumCacheType;
            this.expectedRandomSelection = expectedRandomSelection;
        }

        @Override
        protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType,
                boolean randomSelection) {
            assertThat(minimumCacheType).isEqualTo(expectedMinimumCacheType);
            assertThat(randomSelection).isEqualTo(expectedRandomSelection);
            return baseMoveSelector;
        }
    }

    public static class DummyComparator implements Comparator<Object> {

        @Override
        public int compare(Object o, Object t1) {
            return 0;
        }
    }

    public static class DummySelectionProbabilityWeightFactory
            implements SelectionProbabilityWeightFactory<TestdataSolution, MoveSelector> {

        @Override
        public double createProbabilityWeight(ScoreDirector<TestdataSolution> scoreDirector, MoveSelector selection) {
            return 0.0;
        }
    }
}
