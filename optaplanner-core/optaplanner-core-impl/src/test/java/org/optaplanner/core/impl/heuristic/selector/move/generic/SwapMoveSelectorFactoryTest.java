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

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListSwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListSwapMoveSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesEntity;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesSolution;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataHerdEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataLeadEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataMultiEntitySolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;

class SwapMoveSelectorFactoryTest {

    @Test
    void deducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withVariableNameIncludes("secondaryValue");
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig)
                .buildMoveSelector(buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void undeducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withVariableNameIncludes("nonExistingValue");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void deducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void undeducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataEntity.class));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList()).hasSize(2);
    }

    @Test
    void deducibleMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class))
                .withSecondaryEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void unswappableMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataLeadEntity.class))
                .withSecondaryEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig())
                .withSecondaryEntitySelectorConfig(new EntitySelectorConfig());
        MoveSelector moveSelector =
                MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList()).hasSize(2);
    }

    @Test
    void mixingBasicAndListVariablesUnsupported() {
        SolutionDescriptor<TestdataMixedVariablesSolution> solutionDescriptor =
                TestdataMixedVariablesSolution.buildSolutionDescriptor();

        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        assertThatIllegalArgumentException().isThrownBy(
                () -> MoveSelectorFactory
                        .<TestdataMixedVariablesSolution> create(moveSelectorConfig)
                        .buildMoveSelector(buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM))
                .withMessageContaining("variableDescriptorList");

        SwapMoveSelectorConfig moveSelectorConfigWithEntitySelector = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataMixedVariablesEntity.class));
        assertThatIllegalArgumentException().isThrownBy(
                () -> MoveSelectorFactory
                        .<TestdataMixedVariablesSolution> create(moveSelectorConfigWithEntitySelector)
                        .buildMoveSelector(buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM))
                .withMessageContaining("variableDescriptorList");
    }

    // ************************************************************************
    // List variable compatibility section
    // ************************************************************************

    @Test
    void unfoldEmptyIntoListSwapMoveSelectorConfig() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig)
                        .buildMoveSelector(buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListSwapMoveSelector.class);
    }

    @Test
    void unfoldConfiguredIntoListSwapMoveSelectorConfig() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();

        SelectionCacheType moveSelectorCacheType = SelectionCacheType.PHASE;
        long selectedCountLimit = 200;
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig(TestdataListEntity.class))
                .withCacheType(moveSelectorCacheType)
                .withSelectedCountLimit(selectedCountLimit);

        SwapMoveSelectorFactory<TestdataListSolution> swapMoveSelectorFactory =
                (SwapMoveSelectorFactory<TestdataListSolution>) MoveSelectorFactory
                        .<TestdataListSolution> create(moveSelectorConfig);

        MoveSelectorConfig<?> unfoldedMoveSelectorConfig =
                swapMoveSelectorFactory.buildUnfoldedMoveSelectorConfig(buildHeuristicConfigPolicy(solutionDescriptor));

        assertThat(unfoldedMoveSelectorConfig).isExactlyInstanceOf(ListSwapMoveSelectorConfig.class);
        ListSwapMoveSelectorConfig listSwapMoveSelectorConfig = (ListSwapMoveSelectorConfig) unfoldedMoveSelectorConfig;

        assertThat(listSwapMoveSelectorConfig.getValueSelectorConfig().getVariableName()).isEqualTo("valueList");
        assertThat(listSwapMoveSelectorConfig.getCacheType()).isEqualTo(moveSelectorCacheType);
        assertThat(listSwapMoveSelectorConfig.getSelectedCountLimit()).isEqualTo(selectedCountLimit);
    }
}
