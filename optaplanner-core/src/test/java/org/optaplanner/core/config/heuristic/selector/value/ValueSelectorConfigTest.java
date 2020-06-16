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

package org.optaplanner.core.config.heuristic.selector.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

public class ValueSelectorConfigTest extends AbstractSelectorConfigTest {

    @Test
    public void phaseOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector)
                .isNotInstanceOf(ShufflingValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.STEP);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector)
                .isNotInstanceOf(ShufflingValueSelector.class);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void justInTimeOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector)
                .isNotInstanceOf(ShufflingValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.STEP);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector)
                .isNotInstanceOf(ShufflingValueSelector.class);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void justInTimeRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(ShufflingValueSelector.class);
        assertThat(((ShufflingValueSelector) valueSelector).getChildValueSelector())
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.STEP);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(ShufflingValueSelector.class);
        assertThat(((ShufflingValueSelector) valueSelector).getChildValueSelector())
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    public void justInTimeShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        assertThatIllegalArgumentException().isThrownBy(() -> valueSelectorConfig.buildValueSelector(
                configPolicy, entityDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM));
    }

}
