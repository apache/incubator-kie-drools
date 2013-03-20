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

package org.optaplanner.core.config.heuristic.selector.value;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.value.FromEntityPropertyPlanningValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.FromEntityPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;

@XStreamAlias("valueSelector")
public class ValueSelectorConfig extends SelectorConfig {

    protected String variableName = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    // TODO filterClass

    // TODO sorterClass, ...

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
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
        PlanningVariableDescriptor variableDescriptor = deduceVariableDescriptor(entityDescriptor, variableName);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder,
                inheritedSelectionOrder);

        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
//        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);

        // baseValueSelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        ValueSelector valueSelector = buildBaseValueSelector(environmentMode, variableDescriptor,
                SelectionCacheType.max(minimumCacheType, resolvedCacheType),
                determineBaseRandomSelection(variableDescriptor, resolvedCacheType, resolvedSelectionOrder));

//        valueSelector = applyFiltering(variableDescriptor, resolvedCacheType, resolvedSelectionOrder, valueSelector);
//        valueSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        return valueSelector;
    }

    protected boolean determineBaseRandomSelection(PlanningVariableDescriptor variableDescriptor,
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case ORIGINAL:
                return false;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                // baseValueSelector and lower should be ORIGINAL if they are going to get cached completely
                return false;
            case RANDOM:
                // Predict if caching will occur
                return resolvedCacheType.isNotCached() || (isBaseInherentlyCached(variableDescriptor) && !hasFiltering());
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached(PlanningVariableDescriptor variableDescriptor) {
        return !variableDescriptor.getValueRangeDescriptor().isEntityDependent();
    }

    private ValueSelector buildBaseValueSelector(
            EnvironmentMode environmentMode, PlanningVariableDescriptor variableDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (variableDescriptor.getValueRangeDescriptor().isEntityDependent()) {
            FromEntityPropertyPlanningValueRangeDescriptor valueRangeDescriptor
                    = (FromEntityPropertyPlanningValueRangeDescriptor) variableDescriptor.getValueRangeDescriptor();
            // TODO should we ignore the minimumCacheType so it can be cached on changeMoves too?
            return new FromEntityPropertyValueSelector(valueRangeDescriptor, minimumCacheType, randomSelection);
        } else {
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
            return new FromSolutionPropertyValueSelector(variableDescriptor, minimumCacheType, randomSelection);
        }
    }

    private boolean hasFiltering() {
        return false; // NOT yet implemented
    }

    private void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (probabilityWeightFactoryClass != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with probabilityWeightFactoryClass (" + probabilityWeightFactoryClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    private ValueSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (probabilityWeightFactoryClass == null) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ").");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(this,
                    "probabilityWeightFactoryClass", probabilityWeightFactoryClass);
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on a EntityIndependentValueSelector."
                        + " Check your @" + ValueRange.class.getSimpleName() + " annotations.");
            }
            valueSelector = new ProbabilityValueSelector((EntityIndependentValueSelector) valueSelector,
                    resolvedCacheType, probabilityWeightFactory);
        }
        return valueSelector;
    }

    private ValueSelector applyShuffling(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on a EntityIndependentValueSelector."
                        + " Check your @" + ValueRange.class.getSimpleName() + " annotations.");
            }
            valueSelector = new ShufflingValueSelector((EntityIndependentValueSelector) valueSelector,
                    resolvedCacheType);
        }
        return valueSelector;
    }

    private ValueSelector applyCaching(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(valueSelector.getCacheType()) > 0) {
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on a EntityIndependentValueSelector."
                        + " Check your @" + ValueRange.class.getSimpleName() + " annotations.");
            }
            valueSelector = new CachingValueSelector((EntityIndependentValueSelector) valueSelector, resolvedCacheType,
                    resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return valueSelector;
    }

    public void inherit(ValueSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (variableName == null) {
            variableName = inheritedConfig.getVariableName();
        }
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        probabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                probabilityWeightFactoryClass, inheritedConfig.getProbabilityWeightFactoryClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableName + ")";
    }

}
