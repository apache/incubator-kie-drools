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

package org.drools.planner.config.heuristic.selector.value;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.value.decorator.CachingValueSelector;
import org.drools.planner.core.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.drools.planner.core.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.heuristic.selector.value.decorator.ShufflingValueSelector;

@XStreamAlias("valueSelector")
public class ValueSelectorConfig extends SelectorConfig {

    protected String planningVariableName = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    // TODO filterClass

    // TODO sorterClass, increasingStrength

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    public String getPlanningVariableName() {
        return planningVariableName;
    }

    public void setPlanningVariableName(String planningVariableName) {
        this.planningVariableName = planningVariableName;
    }

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getProbabilityWeightFactoryClass() {
        return probabilityWeightFactoryClass;
    }

    public void setProbabilityWeightFactoryClass(Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     *
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param entityDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public ValueSelector buildValueSelector(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, PlanningEntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        PlanningVariableDescriptor variableDescriptor = fetchVariableDescriptor(entityDescriptor);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        minimumCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder,
                inheritedSelectionOrder);

        // baseValueSelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        ValueSelector valueSelector = buildBaseValueSelector(environmentMode, variableDescriptor,
                minimumCacheType, resolvedCacheType.isCached() ? SelectionOrder.ORIGINAL : resolvedSelectionOrder);

        // TODO applyFiltering
        // TODO applySorting
        valueSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        return valueSelector;
    }

    private ValueSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (probabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ") has a non-random resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ").");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(this,
                    "probabilityWeightFactoryClass", probabilityWeightFactoryClass);
            valueSelector = new ProbabilityValueSelector(valueSelector,
                    resolvedCacheType, probabilityWeightFactory);
        }
        return valueSelector;
    }

    private ValueSelector applyShuffling(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            valueSelector = new ShufflingValueSelector(valueSelector, resolvedCacheType);
        }
        return valueSelector;
    }

    private ValueSelector applyCaching(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(valueSelector.getCacheType()) > 0) {
            valueSelector = new CachingValueSelector(valueSelector, resolvedCacheType,
                    resolvedSelectionOrder == SelectionOrder.RANDOM);
        }
        return valueSelector;
    }

    private PlanningVariableDescriptor fetchVariableDescriptor(PlanningEntityDescriptor entityDescriptor) {
        PlanningVariableDescriptor variableDescriptor;
        if (planningVariableName != null) {
            variableDescriptor = entityDescriptor.getPlanningVariableDescriptor(planningVariableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") has a planningVariableName ("
                        + planningVariableName + ") for planningEntityClass ("
                        + entityDescriptor.getPlanningEntityClass()
                        + ") that is not annotated as a planningVariable.\n" +
                        "Check your planningEntity implementation's annotated methods.");
            }
        } else {
            Collection<PlanningVariableDescriptor> planningVariableDescriptors = entityDescriptor
                    .getPlanningVariableDescriptors();
            if (planningVariableDescriptors.size() != 1) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") has no configured planningVariableName ("
                        + planningVariableName + ") for planningEntityClass ("
                        + entityDescriptor.getPlanningEntityClass()
                        + ") and because there are multiple in the planningVariableNameSet ("
                        + entityDescriptor.getPlanningVariableNameSet()
                        + "), it can not be deducted automatically.");
            }
            variableDescriptor = planningVariableDescriptors.iterator().next();
        }
        return variableDescriptor;
    }

    private ValueSelector buildBaseValueSelector(
            EnvironmentMode environmentMode, PlanningVariableDescriptor variableDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder resolvedSelectionOrder) {
        // FromSolutionPropertyValueSelector caches by design, so it uses the minimumCacheType
        if (variableDescriptor.isPlanningValuesCacheable()) {
            if (minimumCacheType.compareTo(SelectionCacheType.PHASE) < 0) {
                // TODO we probably want to default this to SelectionCacheType.JUST_IN_TIME
                minimumCacheType = SelectionCacheType.PHASE;
            }
        } else {
            if (minimumCacheType.compareTo(SelectionCacheType.STEP) < 0) {
                // TODO we probably want to default this to SelectionCacheType.JUST_IN_TIME
                minimumCacheType = SelectionCacheType.STEP;
            }
        }
        return new FromSolutionPropertyValueSelector(variableDescriptor,
                    minimumCacheType, resolvedSelectionOrder == SelectionOrder.RANDOM);
    }

    public void inherit(ValueSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (planningVariableName == null) {
            planningVariableName = inheritedConfig.getPlanningVariableName();
        }
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        probabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                probabilityWeightFactoryClass, inheritedConfig.getProbabilityWeightFactoryClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + planningVariableName + ")";
    }

}
