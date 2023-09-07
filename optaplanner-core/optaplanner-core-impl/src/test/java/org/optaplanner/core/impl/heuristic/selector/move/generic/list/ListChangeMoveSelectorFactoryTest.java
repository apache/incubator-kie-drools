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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesEntity;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesSolution;

class ListChangeMoveSelectorFactoryTest {

    @Test
    void noUnfolding() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        ListChangeMoveSelectorConfig moveSelectorConfig = new ListChangeMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("valueList"))
                .withDestinationSelectorConfig(new DestinationSelectorConfig()
                        .withEntitySelectorConfig(new EntitySelectorConfig(TestdataListEntity.class))
                        .withValueSelectorConfig(new ValueSelectorConfig("valueList")));
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListChangeMoveSelector.class);
    }

    @Test
    void unfoldedSingleListVariable() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        ListChangeMoveSelectorConfig moveSelectorConfig = new ListChangeMoveSelectorConfig();
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListChangeMoveSelector.class);
    }

    @Test
    void unfoldedConfigInheritsFromFoldedConfig() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();

        SelectionCacheType moveSelectorCacheType = SelectionCacheType.STEP;
        SelectionOrder moveSelectorSelectionOrder = SelectionOrder.ORIGINAL;
        long selectedCountLimit = 200;

        ListChangeMoveSelectorConfig moveSelectorConfig = new ListChangeMoveSelectorConfig()
                .withCacheType(moveSelectorCacheType)
                .withSelectionOrder(moveSelectorSelectionOrder)
                .withSelectedCountLimit(selectedCountLimit);

        ListChangeMoveSelectorFactory<TestdataListSolution> moveSelectorFactory =
                ((ListChangeMoveSelectorFactory<TestdataListSolution>) MoveSelectorFactory
                        .<TestdataListSolution> create(moveSelectorConfig));

        MoveSelectorConfig<?> unfoldedMoveSelectorConfig =
                moveSelectorFactory.buildUnfoldedMoveSelectorConfig(buildHeuristicConfigPolicy(solutionDescriptor));

        assertThat(unfoldedMoveSelectorConfig).isInstanceOf(ListChangeMoveSelectorConfig.class);
        ListChangeMoveSelectorConfig listChangeMoveSelectorConfig = (ListChangeMoveSelectorConfig) unfoldedMoveSelectorConfig;

        assertThat(listChangeMoveSelectorConfig.getValueSelectorConfig().getVariableName()).isEqualTo("valueList");
        assertThat(listChangeMoveSelectorConfig.getCacheType()).isEqualTo(moveSelectorCacheType);
        assertThat(listChangeMoveSelectorConfig.getSelectionOrder()).isEqualTo(moveSelectorSelectionOrder);
        assertThat(listChangeMoveSelectorConfig.getSelectedCountLimit()).isEqualTo(selectedCountLimit);

        DestinationSelectorConfig destinationSelectorConfig = listChangeMoveSelectorConfig.getDestinationSelectorConfig();
        EntitySelectorConfig entitySelectorConfig = destinationSelectorConfig.getEntitySelectorConfig();
        assertThat(entitySelectorConfig.getEntityClass()).isEqualTo(TestdataListEntity.class);
        ValueSelectorConfig valueSelectorConfig = destinationSelectorConfig.getValueSelectorConfig();
        assertThat(valueSelectorConfig.getVariableName()).isEqualTo("valueList");
    }

    @Test
    void unfoldingSkipsBasicVariablesGracefully() {
        // TODO is this supposed to fail because mixing is not yet supported?
        SolutionDescriptor<TestdataMixedVariablesSolution> solutionDescriptor =
                TestdataMixedVariablesSolution.buildSolutionDescriptor();
        ListChangeMoveSelectorConfig moveSelectorConfig = new ListChangeMoveSelectorConfig();
        MoveSelector<TestdataMixedVariablesSolution> moveSelector =
                MoveSelectorFactory.<TestdataMixedVariablesSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListChangeMoveSelector.class);
    }

    @Test
    void unfoldingFailsIfThereIsNoListVariable() {
        ListChangeMoveSelectorConfig config = new ListChangeMoveSelectorConfig();
        ListChangeMoveSelectorFactory<TestdataSolution> moveSelectorFactory = new ListChangeMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataSolution.buildSolutionDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> moveSelectorFactory.buildMoveSelector(heuristicConfigPolicy, SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM))
                .withMessageContaining("cannot unfold");
    }

    @Test
    void explicitConfigMustUseListVariable() {
        ListChangeMoveSelectorConfig config = new ListChangeMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("value"))
                .withDestinationSelectorConfig(new DestinationSelectorConfig()
                        .withEntitySelectorConfig(new EntitySelectorConfig(TestdataMixedVariablesEntity.class))
                        .withValueSelectorConfig(new ValueSelectorConfig("value")));

        ListChangeMoveSelectorFactory<TestdataMixedVariablesSolution> moveSelectorFactory =
                new ListChangeMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataMixedVariablesSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataMixedVariablesSolution.buildSolutionDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> moveSelectorFactory.buildMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("not a planning list variable");
    }
}
