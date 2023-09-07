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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListChangeMoveSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataHerdEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataMultiEntitySolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;

class ChangeMoveSelectorFactoryTest {

    @Test
    void deducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("secondaryValue"));
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ChangeMoveSelector.class);
    }

    @Test
    void undeducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("nonExistingValue"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList()).hasSize(3);
    }

    @Test
    void deducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(ChangeMoveSelector.class);
    }

    @Test
    void undeducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataEntity.class));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList()).hasSize(2);
    }

    // ************************************************************************
    // List variable compatibility section
    // ************************************************************************

    @Test
    void unfoldEmptyIntoListChangeMoveSelectorConfig() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig)
                        .buildMoveSelector(buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListChangeMoveSelector.class);
    }

    @Test
    void unfoldConfiguredIntoListChangeMoveSelectorConfig() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();

        SelectionCacheType moveSelectorCacheType = SelectionCacheType.PHASE;
        SelectionOrder moveSelectorSelectionOrder = SelectionOrder.ORIGINAL;
        long selectedCountLimit = 200;
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataListEntity.class)
                        .withSorterComparatorClass(DummyEntityComparator.class))
                .withValueSelectorConfig(new ValueSelectorConfig("valueList"))
                .withCacheType(moveSelectorCacheType)
                .withSelectionOrder(moveSelectorSelectionOrder)
                .withSelectedCountLimit(selectedCountLimit);

        ChangeMoveSelectorFactory<TestdataListSolution> changeMoveSelectorFactory =
                (ChangeMoveSelectorFactory<TestdataListSolution>) MoveSelectorFactory
                        .<TestdataListSolution> create(moveSelectorConfig);

        MoveSelectorConfig<?> unfoldedMoveSelectorConfig =
                changeMoveSelectorFactory.buildUnfoldedMoveSelectorConfig(buildHeuristicConfigPolicy(solutionDescriptor));

        assertThat(unfoldedMoveSelectorConfig).isExactlyInstanceOf(ListChangeMoveSelectorConfig.class);
        ListChangeMoveSelectorConfig listChangeMoveSelectorConfig = (ListChangeMoveSelectorConfig) unfoldedMoveSelectorConfig;

        assertThat(listChangeMoveSelectorConfig.getValueSelectorConfig().getVariableName()).isEqualTo("valueList");
        assertThat(listChangeMoveSelectorConfig.getCacheType()).isEqualTo(moveSelectorCacheType);
        assertThat(listChangeMoveSelectorConfig.getSelectionOrder()).isEqualTo(moveSelectorSelectionOrder);
        assertThat(listChangeMoveSelectorConfig.getSelectedCountLimit()).isEqualTo(selectedCountLimit);

        DestinationSelectorConfig destinationSelectorConfig = listChangeMoveSelectorConfig.getDestinationSelectorConfig();
        EntitySelectorConfig entitySelectorConfig = destinationSelectorConfig.getEntitySelectorConfig();
        assertThat(entitySelectorConfig.getEntityClass()).isEqualTo(TestdataListEntity.class);
        assertThat(entitySelectorConfig.getSorterComparatorClass()).isEqualTo(DummyEntityComparator.class);
        ValueSelectorConfig valueSelectorConfig = destinationSelectorConfig.getValueSelectorConfig();
        assertThat(valueSelectorConfig.getVariableName()).isEqualTo("valueList");
    }

    static class DummyEntityComparator implements Comparator<TestdataListEntity> {
        @Override
        public int compare(TestdataListEntity e1, TestdataListEntity e2) {
            return 0;
        }
    }
}
