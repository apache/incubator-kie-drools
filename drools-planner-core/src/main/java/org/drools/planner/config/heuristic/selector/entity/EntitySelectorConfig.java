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

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.FromSolutionEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.cached.PlanningEntitySelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.entity.cached.ProbabilityEntitySelector;

@XStreamAlias("entitySelector")
public class EntitySelectorConfig extends SelectorConfig {

    private Class<?> planningEntityClass = null;
    private SelectionOrder selectionOrder = null;
    private SelectionCacheType cacheType = null;
    // TODO filterClass
    private Class<? extends PlanningEntitySelectionProbabilityWeightFactory> selectionProbabilityWeightFactoryClass
            = null;
    // TODO sorterClass, decreasingDifficulty

    public Class<?> getPlanningEntityClass() {
        return planningEntityClass;
    }

    public void setPlanningEntityClass(Class<?> planningEntityClass) {
        this.planningEntityClass = planningEntityClass;
    }

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Class<? extends PlanningEntitySelectionProbabilityWeightFactory> getSelectionProbabilityWeightFactoryClass() {
        return selectionProbabilityWeightFactoryClass;
    }

    public void setSelectionProbabilityWeightFactoryClass(Class<? extends PlanningEntitySelectionProbabilityWeightFactory> selectionProbabilityWeightFactoryClass) {
        this.selectionProbabilityWeightFactoryClass = selectionProbabilityWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public EntitySelector buildEntitySelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedResolvedSelectionOrder) {
        PlanningEntityDescriptor entityDescriptor;
        if (planningEntityClass != null) {
            entityDescriptor = solutionDescriptor.getPlanningEntityDescriptorStrict(planningEntityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The entitySelector has a planningEntityClass ("
                        + planningEntityClass + ") that is not configured as a planningEntity.\n" +
                        "If that class (" + planningEntityClass.getSimpleName() + ") is not a " +
                        "planningEntityClass (" + solutionDescriptor.getPlanningEntityClassSet()
                        + "), check your Solution implementation's annotated methods.\n" +
                        "If it is, check your solver configuration.");
            }
        } else {
            Collection<PlanningEntityDescriptor> planningEntityDescriptors = solutionDescriptor
                    .getPlanningEntityDescriptors();
            if (planningEntityDescriptors.size() != 1) {
                throw new IllegalArgumentException("The entitySelector has no configured planningEntityClass ("
                        + planningEntityClass + ") and because there are multiple in the planningEntityClassSet ("
                        + solutionDescriptor.getPlanningEntityClassSet()
                        + "), it can not be deducted automatically.");
            }
            entityDescriptor = planningEntityDescriptors.iterator().next();
        }
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolveSelectionOrder(selectionOrder,
                inheritedResolvedSelectionOrder);
        boolean randomSelection = resolvedSelectionOrder == SelectionOrder.RANDOM
                && selectionProbabilityWeightFactoryClass == null;
        // cacheType defaults to SelectionCacheType.STEP because JIT is pointless and an entity can be added in a step
        SelectionCacheType resolvedCacheType = cacheType == null ? SelectionCacheType.STEP : cacheType;
        EntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, randomSelection,
                resolvedCacheType);

        // TODO filterclass

        if (selectionProbabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The entitySelector with selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass + ") has a non-random resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ").");
            }
            PlanningEntitySelectionProbabilityWeightFactory selectionProbabilityWeightFactory;
            try {
                selectionProbabilityWeightFactory = selectionProbabilityWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            ProbabilityEntitySelector probabilityEntitySelector = new ProbabilityEntitySelector(resolvedCacheType,
                    selectionProbabilityWeightFactory);
            probabilityEntitySelector.setChildEntitySelector(entitySelector);
            entitySelector = probabilityEntitySelector;
        }
        return entitySelector;
    }

    public void inherit(EntitySelectorConfig inheritedConfig) {
        if (planningEntityClass == null) {
            planningEntityClass = inheritedConfig.getPlanningEntityClass();
        }
        if (selectionOrder == null) {
            selectionOrder = inheritedConfig.getSelectionOrder();
        }
        if (cacheType == null) {
            cacheType = inheritedConfig.getCacheType();
        }
        if (selectionProbabilityWeightFactoryClass == null) {
            selectionProbabilityWeightFactoryClass = inheritedConfig.getSelectionProbabilityWeightFactoryClass();
        }
    }

}
