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

package org.optaplanner.core.config.heuristic.selector.entity;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.junit.Test;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class EntitySelectorConfigTest {

    @Test
    public void phaseOriginal() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertEquals(SelectionCacheType.PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepOriginal() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertEquals(SelectionCacheType.STEP, entitySelector.getCacheType());
    }

    @Test
    public void justInTimeOriginal() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertEquals(SelectionCacheType.PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepRandom() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertEquals(SelectionCacheType.STEP, entitySelector.getCacheType());
    }

    @Test
    public void justInTimeRandom() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((ShufflingEntitySelector) entitySelector).getChildEntitySelector());
        assertEquals(SelectionCacheType.PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepShuffled() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((ShufflingEntitySelector) entitySelector).getChildEntitySelector());
        assertEquals(SelectionCacheType.STEP, entitySelector.getCacheType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void justInTimeShuffled() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
    }

}
