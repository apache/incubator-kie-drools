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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.valuerange.descriptor.EntityIndependentValueRangeDescriptor;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.FromEntityPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.DowncastingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.EntityDependentSortingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.FilteringValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.InitializedValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ReinitializeVariableValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.SelectedCountLimitValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.SortingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicRecordingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.ValueMimicRecorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("valueSelector")
public class ValueSelectorConfig extends SelectorConfig<ValueSelectorConfig> {

    @XmlAttribute
    @XStreamAsAttribute
    protected String id = null;
    @XmlAttribute
    @XStreamAsAttribute
    protected String mimicSelectorRef = null;

    protected Class<?> downcastEntityClass = null;
    @XmlAttribute
    @XStreamAsAttribute // Works with a nested element input too, which is a BC req in 7.x, but undesired in 8.0
    protected String variableName = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    @XmlElement(name = "nearbySelection")
    @XStreamAlias("nearbySelection")
    protected NearbySelectionConfig nearbySelectionConfig = null;

    protected Class<? extends SelectionFilter> filterClass = null;

    protected ValueSorterManner sorterManner = null;
    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    protected Long selectedCountLimit = null;

    public ValueSelectorConfig() {
    }

    public ValueSelectorConfig(String variableName) {
        this.variableName = variableName;
    }

    public ValueSelectorConfig(ValueSelectorConfig inheritedConfig) {
        if (inheritedConfig != null) {
            inherit(inheritedConfig);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimicSelectorRef() {
        return mimicSelectorRef;
    }

    public void setMimicSelectorRef(String mimicSelectorRef) {
        this.mimicSelectorRef = mimicSelectorRef;
    }

    public Class<?> getDowncastEntityClass() {
        return downcastEntityClass;
    }

    public void setDowncastEntityClass(Class<?> downcastEntityClass) {
        this.downcastEntityClass = downcastEntityClass;
    }

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

    public NearbySelectionConfig getNearbySelectionConfig() {
        return nearbySelectionConfig;
    }

    public void setNearbySelectionConfig(NearbySelectionConfig nearbySelectionConfig) {
        this.nearbySelectionConfig = nearbySelectionConfig;
    }

    public Class<? extends SelectionFilter> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends SelectionFilter> filterClass) {
        this.filterClass = filterClass;
    }

    public ValueSorterManner getSorterManner() {
        return sorterManner;
    }

    public void setSorterManner(ValueSorterManner sorterManner) {
        this.sorterManner = sorterManner;
    }

    public Class<? extends Comparator> getSorterComparatorClass() {
        return sorterComparatorClass;
    }

    public void setSorterComparatorClass(Class<? extends Comparator> sorterComparatorClass) {
        this.sorterComparatorClass = sorterComparatorClass;
    }

    public Class<? extends SelectionSorterWeightFactory> getSorterWeightFactoryClass() {
        return sorterWeightFactoryClass;
    }

    public void setSorterWeightFactoryClass(Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass) {
        this.sorterWeightFactoryClass = sorterWeightFactoryClass;
    }

    public SelectionSorterOrder getSorterOrder() {
        return sorterOrder;
    }

    public void setSorterOrder(SelectionSorterOrder sorterOrder) {
        this.sorterOrder = sorterOrder;
    }

    public Class<? extends SelectionSorter> getSorterClass() {
        return sorterClass;
    }

    public void setSorterClass(Class<? extends SelectionSorter> sorterClass) {
        this.sorterClass = sorterClass;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getProbabilityWeightFactoryClass() {
        return probabilityWeightFactoryClass;
    }

    public void setProbabilityWeightFactoryClass(
            Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
    }

    public Long getSelectedCountLimit() {
        return selectedCountLimit;
    }

    public void setSelectedCountLimit(Long selectedCountLimit) {
        this.selectedCountLimit = selectedCountLimit;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public GenuineVariableDescriptor extractVariableDescriptor(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor) {
        entityDescriptor = downcastEntityDescriptor(configPolicy, entityDescriptor);
        if (variableName != null) {
            GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has a variableName (" + variableName
                        + ") which is not a valid planning variable on entityClass ("
                        + entityDescriptor.getEntityClass() + ").\n"
                        + entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
            }
            return variableDescriptor;
        } else if (mimicSelectorRef != null) {
            return configPolicy.getValueMimicRecorder(mimicSelectorRef).getVariableDescriptor();
        } else {
            return null;
        }
    }

    /**
     *
     * @param configPolicy never null
     * @param entityDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public ValueSelector buildValueSelector(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        return buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder,
                configPolicy.isReinitializeVariableFilterEnabled());
    }

    public ValueSelector buildValueSelector(HeuristicConfigPolicy configPolicy, EntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder,
            boolean applyReinitializeVariableFiltering) {
        if (mimicSelectorRef != null) {
            ValueSelector valueSelector = buildMimicReplaying(configPolicy);
            if (applyReinitializeVariableFiltering) {
                valueSelector = new ReinitializeVariableValueSelector(valueSelector);
            }
            valueSelector = applyDowncasting(configPolicy, valueSelector);
            return valueSelector;
        }
        entityDescriptor = downcastEntityDescriptor(configPolicy, entityDescriptor);
        GenuineVariableDescriptor variableDescriptor = deduceVariableDescriptor(entityDescriptor, variableName);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder,
                inheritedSelectionOrder);

        if (nearbySelectionConfig != null) {
            nearbySelectionConfig.validateNearby(resolvedCacheType, resolvedSelectionOrder);
        }
        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        // baseValueSelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        ValueSelector valueSelector = buildBaseValueSelector(configPolicy, variableDescriptor,
                SelectionCacheType.max(minimumCacheType, resolvedCacheType),
                determineBaseRandomSelection(variableDescriptor, resolvedCacheType, resolvedSelectionOrder));

        if (nearbySelectionConfig != null) {
            // TODO Static filtering (such as movableEntitySelectionFilter) should affect nearbySelection too
            valueSelector = nearbySelectionConfig.applyNearbyValueSelector(configPolicy,
                    minimumCacheType, resolvedCacheType, resolvedSelectionOrder, valueSelector);
        }
        valueSelector = applyFiltering(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyInitializedChainedValueFilter(configPolicy, variableDescriptor,
                resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applySelectedLimit(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyMimicRecording(configPolicy, valueSelector);
        if (applyReinitializeVariableFiltering) {
            valueSelector = new ReinitializeVariableValueSelector(valueSelector);
        }
        valueSelector = applyDowncasting(configPolicy, valueSelector);
        return valueSelector;
    }

    protected ValueSelector buildMimicReplaying(HeuristicConfigPolicy configPolicy) {
        if (id != null
                || variableName != null
                || cacheType != null
                || selectionOrder != null
                || nearbySelectionConfig != null
                || filterClass != null
                || sorterManner != null
                || sorterComparatorClass != null
                || sorterWeightFactoryClass != null
                || sorterOrder != null
                || sorterClass != null
                || probabilityWeightFactoryClass != null
                || selectedCountLimit != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with mimicSelectorRef (" + mimicSelectorRef
                    + ") has another property that is not null.");
        }
        ValueMimicRecorder valueMimicRecorder = configPolicy.getValueMimicRecorder(mimicSelectorRef);
        if (valueMimicRecorder == null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has a mimicSelectorRef (" + mimicSelectorRef
                    + ") for which no valueSelector with that id exists (in its solver phase).");
        }
        return new MimicReplayingValueSelector(valueMimicRecorder);
    }

    protected EntityDescriptor downcastEntityDescriptor(HeuristicConfigPolicy configPolicy, EntityDescriptor entityDescriptor) {
        if (downcastEntityClass != null) {
            Class<?> parentEntityClass = entityDescriptor.getEntityClass();
            if (!parentEntityClass.isAssignableFrom(downcastEntityClass)) {
                throw new IllegalStateException("The downcastEntityClass (" + downcastEntityClass
                        + ") is not a subclass of the parentEntityClass (" + parentEntityClass
                        + ") configured by the " + EntitySelector.class.getSimpleName() + ".");
            }
            SolutionDescriptor solutionDescriptor = configPolicy.getSolutionDescriptor();
            entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(downcastEntityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has an downcastEntityClass (" + downcastEntityClass
                        + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + downcastEntityClass.getSimpleName()
                        + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your Solution implementation's annotated methods too.");
            }
        }
        return entityDescriptor;
    }

    protected boolean determineBaseRandomSelection(GenuineVariableDescriptor variableDescriptor,
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
                return resolvedCacheType.isNotCached()
                        || (isBaseInherentlyCached(variableDescriptor) && !hasFiltering(variableDescriptor));
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached(GenuineVariableDescriptor variableDescriptor) {
        return variableDescriptor.isValueRangeEntityIndependent();
    }

    private ValueSelector buildBaseValueSelector(
            HeuristicConfigPolicy configPolicy, GenuineVariableDescriptor variableDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
        // TODO minimumCacheType SOLVER is only a problem if the valueRange includes entities or custom weird cloning
        if (minimumCacheType == SelectionCacheType.SOLVER) {
            // TODO Solver cached entities are not compatible with DroolsScoreCalculator and IncrementalScoreDirector
            // because between phases the entities get cloned and the KieSession/Maps contains those clones afterwards
            // https://issues.redhat.com/browse/PLANNER-54
            throw new IllegalArgumentException("The minimumCacheType (" + minimumCacheType
                    + ") is not yet supported. Please use " + SelectionCacheType.PHASE + " instead.");
        }
        if (valueRangeDescriptor.isEntityIndependent()) {
            return new FromSolutionPropertyValueSelector(
                    (EntityIndependentValueRangeDescriptor) valueRangeDescriptor, minimumCacheType, randomSelection);
        } else {
            // TODO Do not allow PHASE cache on FromEntityPropertyValueSelector, except if the moveSelector is PHASE cached too.
            return new FromEntityPropertyValueSelector(valueRangeDescriptor, randomSelection);
        }
    }

    private boolean hasFiltering(GenuineVariableDescriptor variableDescriptor) {
        return filterClass != null || variableDescriptor.hasMovableChainedTrailingValueFilter();
    }

    private ValueSelector applyFiltering(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        if (hasFiltering(variableDescriptor)) {
            List<SelectionFilter> filterList = new ArrayList<>(filterClass == null ? 1 : 2);
            if (filterClass != null) {
                filterList.add(ConfigUtils.newInstance(this, "filterClass", filterClass));
            }
            // Filter out pinned entities
            if (variableDescriptor.hasMovableChainedTrailingValueFilter()) {
                filterList.add(variableDescriptor.getMovableChainedTrailingValueFilter());
            }
            valueSelector = FilteringValueSelector.create(valueSelector, filterList);
        }
        return valueSelector;
    }

    protected ValueSelector applyInitializedChainedValueFilter(HeuristicConfigPolicy configPolicy,
            GenuineVariableDescriptor variableDescriptor,
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (configPolicy.isInitializedChainedValueFilterEnabled()
                && variableDescriptor.isChained()) {
            valueSelector = InitializedValueSelector.create(valueSelector);
        }
        return valueSelector;
    }

    private void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((sorterManner != null || sorterComparatorClass != null || sorterWeightFactoryClass != null
                || sorterOrder != null || sorterClass != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with sorterManner (" + sorterManner
                    + ") and sorterComparatorClass (" + sorterComparatorClass
                    + ") and sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and sorterOrder (" + sorterOrder
                    + ") and sorterClass (" + sorterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (sorterManner != null && sorterComparatorClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterComparatorClass (" + sorterComparatorClass + ").");
        }
        if (sorterManner != null && sorterWeightFactoryClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
        }
        if (sorterManner != null && sorterClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterManner != null && sorterOrder != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with sorterManner (" + sorterManner
                    + ") has a non-null sorterOrder (" + sorterOrder + ").");
        }
        if (sorterComparatorClass != null && sorterWeightFactoryClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
        }
        if (sorterComparatorClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterWeightFactoryClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") has both a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterClass != null && sorterOrder != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with sorterClass (" + sorterClass
                    + ") has a non-null sorterOrder (" + sorterOrder + ").");
        }
    }

    private ValueSelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter sorter;
            if (sorterManner != null) {
                GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
                if (!hasSorter(sorterManner, variableDescriptor)) {
                    return valueSelector;
                }
                sorter = determineSorter(sorterManner, variableDescriptor);
            } else if (sorterComparatorClass != null) {
                Comparator<Object> sorterComparator = ConfigUtils.newInstance(this,
                        "sorterComparatorClass", sorterComparatorClass);
                sorter = new ComparatorSelectionSorter(sorterComparator,
                        SelectionSorterOrder.resolve(sorterOrder));
            } else if (sorterWeightFactoryClass != null) {
                SelectionSorterWeightFactory sorterWeightFactory = ConfigUtils.newInstance(this,
                        "sorterWeightFactoryClass", sorterWeightFactoryClass);
                sorter = new WeightFactorySelectionSorter(sorterWeightFactory,
                        SelectionSorterOrder.resolve(sorterOrder));
            } else if (sorterClass != null) {
                sorter = ConfigUtils.newInstance(this, "sorterClass", sorterClass);
            } else {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterManner (" + sorterManner
                        + ") or a sorterComparatorClass (" + sorterComparatorClass
                        + ") or a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                        + ") or a sorterClass (" + sorterClass + ").");
            }
            if (!valueSelector.getVariableDescriptor().isValueRangeEntityIndependent()
                    && resolvedCacheType == SelectionCacheType.STEP) {
                valueSelector = new EntityDependentSortingValueSelector(valueSelector,
                        resolvedCacheType, sorter);
            } else {
                if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                    throw new IllegalArgumentException("The valueSelectorConfig (" + this
                            + ") with resolvedCacheType (" + resolvedCacheType
                            + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                            + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
                }
                valueSelector = new SortingValueSelector((EntityIndependentValueSelector) valueSelector,
                        resolvedCacheType, sorter);
            }
        }
        return valueSelector;
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
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
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
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
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
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            valueSelector = new CachingValueSelector((EntityIndependentValueSelector) valueSelector, resolvedCacheType,
                    resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return valueSelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (selectedCountLimit != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + this
                    + ") with selectedCountLimit (" + selectedCountLimit
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private ValueSelector applySelectedLimit(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            ValueSelector valueSelector) {
        if (selectedCountLimit != null) {
            valueSelector = new SelectedCountLimitValueSelector(valueSelector, selectedCountLimit);
        }
        return valueSelector;
    }

    private ValueSelector applyMimicRecording(HeuristicConfigPolicy configPolicy, ValueSelector valueSelector) {
        if (id != null) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") has an empty id (" + id + ").");
            }
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + this
                        + ") with id (" + id
                        + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            MimicRecordingValueSelector mimicRecordingValueSelector = new MimicRecordingValueSelector(
                    (EntityIndependentValueSelector) valueSelector);
            configPolicy.addValueMimicRecorder(id, mimicRecordingValueSelector);
            valueSelector = mimicRecordingValueSelector;
        }
        return valueSelector;
    }

    private ValueSelector applyDowncasting(HeuristicConfigPolicy configPolicy, ValueSelector valueSelector) {
        if (downcastEntityClass != null) {
            valueSelector = new DowncastingValueSelector(valueSelector, downcastEntityClass);
        }
        return valueSelector;
    }

    @Override
    public ValueSelectorConfig inherit(ValueSelectorConfig inheritedConfig) {
        id = ConfigUtils.inheritOverwritableProperty(id, inheritedConfig.getId());
        mimicSelectorRef = ConfigUtils.inheritOverwritableProperty(mimicSelectorRef,
                inheritedConfig.getMimicSelectorRef());
        downcastEntityClass = ConfigUtils.inheritOverwritableProperty(downcastEntityClass,
                inheritedConfig.getDowncastEntityClass());
        variableName = ConfigUtils.inheritOverwritableProperty(variableName, inheritedConfig.getVariableName());
        nearbySelectionConfig = ConfigUtils.inheritConfig(nearbySelectionConfig, inheritedConfig.getNearbySelectionConfig());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        sorterManner = ConfigUtils.inheritOverwritableProperty(
                sorterManner, inheritedConfig.getSorterManner());
        sorterComparatorClass = ConfigUtils.inheritOverwritableProperty(
                sorterComparatorClass, inheritedConfig.getSorterComparatorClass());
        sorterWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                sorterWeightFactoryClass, inheritedConfig.getSorterWeightFactoryClass());
        sorterOrder = ConfigUtils.inheritOverwritableProperty(
                sorterOrder, inheritedConfig.getSorterOrder());
        sorterClass = ConfigUtils.inheritOverwritableProperty(
                sorterClass, inheritedConfig.getSorterClass());
        probabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                probabilityWeightFactoryClass, inheritedConfig.getProbabilityWeightFactoryClass());
        selectedCountLimit = ConfigUtils.inheritOverwritableProperty(
                selectedCountLimit, inheritedConfig.getSelectedCountLimit());
        return this;
    }

    @Override
    public ValueSelectorConfig copyConfig() {
        return new ValueSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableName + ")";
    }

    public static boolean hasSorter(ValueSorterManner valueSorterManner, GenuineVariableDescriptor variableDescriptor) {
        switch (valueSorterManner) {
            case NONE:
                return false;
            case INCREASING_STRENGTH:
            case DECREASING_STRENGTH:
                return true;
            case INCREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getIncreasingStrengthSorter() != null;
            case DECREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getDecreasingStrengthSorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + valueSorterManner + ") is not implemented.");
        }
    }

    public static SelectionSorter determineSorter(ValueSorterManner valueSorterManner,
            GenuineVariableDescriptor variableDescriptor) {
        SelectionSorter sorter;
        switch (valueSorterManner) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case INCREASING_STRENGTH:
            case INCREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getIncreasingStrengthSorter();
                break;
            case DECREASING_STRENGTH:
            case DECREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getDecreasingStrengthSorter();
                break;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + valueSorterManner + ") is not implemented.");
        }
        if (sorter == null) {
            throw new IllegalArgumentException("The sorterManner (" + valueSorterManner
                    + ") on entity class (" + variableDescriptor.getEntityDescriptor().getEntityClass()
                    + ")'s variable (" + variableDescriptor.getVariableName()
                    + ") fails because that variable getter's " + PlanningVariable.class.getSimpleName()
                    + " annotation does not declare any strength comparison.");
        }
        return sorter;
    }

}
