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
import org.drools.planner.core.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.drools.planner.core.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;

@XStreamAlias("valueSelector")
public class ValueSelectorConfig extends SelectorConfig {

    private String planningVariableName = null;
    private SelectionOrder selectionOrder = null;
    private SelectionCacheType cacheType = null;
    // TODO filterClass
    private Class<? extends SelectionProbabilityWeightFactory> valueProbabilityWeightFactoryClass = null;
    // TODO sorterClass, increasingStrength

    public String getPlanningVariableName() {
        return planningVariableName;
    }

    public void setPlanningVariableName(String planningVariableName) {
        this.planningVariableName = planningVariableName;
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

    public Class<? extends SelectionProbabilityWeightFactory> getValueProbabilityWeightFactoryClass() {
        return valueProbabilityWeightFactoryClass;
    }

    public void setValueProbabilityWeightFactoryClass(Class<? extends SelectionProbabilityWeightFactory> valueProbabilityWeightFactoryClass) {
        this.valueProbabilityWeightFactoryClass = valueProbabilityWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param inheritedSelectionOrder never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param entityDescriptor never null
     * @return never null
     */
    public ValueSelector buildValueSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedSelectionOrder, SelectionCacheType minimumCacheType,
            PlanningEntityDescriptor entityDescriptor) {
        PlanningVariableDescriptor variableDescriptor;
        if (planningVariableName != null) {
            variableDescriptor = entityDescriptor.getPlanningVariableDescriptor(planningVariableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The variableSelectorConfig (" + this
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
                throw new IllegalArgumentException("The variableSelectorConfig (" + this
                        + ") has no configured planningVariableName ("
                        + planningVariableName + ") for planningEntityClass ("
                        + entityDescriptor.getPlanningEntityClass()
                        + ") and because there are multiple in the planningVariableNameSet ("
                        + entityDescriptor.getPlanningVariableNameSet()
                        + "), it can not be deducted automatically.");
            }
            variableDescriptor = planningVariableDescriptors.iterator().next();
        }
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder,
                inheritedSelectionOrder);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        minimumCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        boolean randomSelection = resolvedSelectionOrder == SelectionOrder.RANDOM
                && valueProbabilityWeightFactoryClass == null;
        // FromSolutionPropertyValueSelector caches by design, so they use the minimumCacheType
        if (minimumCacheType.compareTo(SelectionCacheType.PHASE) < 0) {
            // TODO we probably want to default this to SelectionCacheType.JUST_IN_TIME
            minimumCacheType = SelectionCacheType.PHASE;
        }
        ValueSelector valueSelector = new FromSolutionPropertyValueSelector(variableDescriptor, randomSelection,
                minimumCacheType);

        // TODO filterclass

        if (valueProbabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The variableSelectorConfig (" + this
                        + ") with valueProbabilityWeightFactoryClass ("
                        + valueProbabilityWeightFactoryClass + ") has a non-random resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ").");
            }
            SelectionProbabilityWeightFactory valueProbabilityWeightFactory;
            try {
                valueProbabilityWeightFactory = valueProbabilityWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("valueProbabilityWeightFactoryClass ("
                        + valueProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("valueProbabilityWeightFactoryClass ("
                        + valueProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            valueSelector = new ProbabilityValueSelector(valueSelector,
                    resolvedCacheType, valueProbabilityWeightFactory);
        }
        return valueSelector;
    }

    public void inherit(ValueSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (planningVariableName == null) {
            planningVariableName = inheritedConfig.getPlanningVariableName();
        }
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        valueProbabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                valueProbabilityWeightFactoryClass, inheritedConfig.getValueProbabilityWeightFactoryClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + planningVariableName + ")";
    }

}
