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

package org.drools.planner.config.heuristic.selector.entity;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.entity.EntitySelectorConfig;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.FromSolutionEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.drools.planner.core.testdata.domain.TestdataSolution;
import org.junit.Test;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;

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
        assertInstanceOf(CachingEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((CachingEntitySelector) entitySelector).getChildEntitySelector());
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
        assertInstanceOf(CachingEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((CachingEntitySelector) entitySelector).getChildEntitySelector());
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
        assertInstanceOf(CachingEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((CachingEntitySelector) entitySelector).getChildEntitySelector());
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
        assertInstanceOf(CachingEntitySelector.class, entitySelector);
        assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        assertInstanceOf(FromSolutionEntitySelector.class,
                ((CachingEntitySelector) entitySelector).getChildEntitySelector());
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
