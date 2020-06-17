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

package org.optaplanner.core.config.heuristic.selector.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;

public class EntitySelectorConfigTest extends AbstractSelectorConfigTest {

    @Test
    public void phaseOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    public void justInTimeOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    public void justInTimeRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(ShufflingEntitySelector.class);
        assertThat(((ShufflingEntitySelector) entitySelector).getChildEntitySelector())
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    public void stepShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(ShufflingEntitySelector.class);
        assertThat(((ShufflingEntitySelector) entitySelector).getChildEntitySelector())
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    public void justInTimeShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        assertThatIllegalArgumentException().isThrownBy(() -> entitySelectorConfig.buildEntitySelector(
                buildHeuristicConfigPolicy(),
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM));
    }

}
