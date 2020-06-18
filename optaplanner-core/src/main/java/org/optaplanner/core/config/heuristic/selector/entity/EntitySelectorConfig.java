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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.FilteringEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ProbabilityEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.SelectedCountLimitEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.SortingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.EntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("entitySelector")
public class EntitySelectorConfig extends SelectorConfig<EntitySelectorConfig> {

    public static EntitySelectorConfig newMimicSelectorConfig(String mimicSelectorRef) {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setMimicSelectorRef(mimicSelectorRef);
        return entitySelectorConfig;
    }

    @XmlAttribute
    @XStreamAsAttribute
    protected String id = null;
    @XmlAttribute
    @XStreamAsAttribute
    protected String mimicSelectorRef = null;

    protected Class<?> entityClass = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    @XmlElement(name = "nearbySelection")
    @XStreamAlias("nearbySelection")
    protected NearbySelectionConfig nearbySelectionConfig = null;

    protected Class<? extends SelectionFilter> filterClass = null;

    protected EntitySorterManner sorterManner = null;
    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    protected Long selectedCountLimit = null;

    public EntitySelectorConfig() {
    }

    public EntitySelectorConfig(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EntitySelectorConfig(EntitySelectorConfig inheritedConfig) {
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

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
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

    public EntitySorterManner getSorterManner() {
        return sorterManner;
    }

    public void setSorterManner(EntitySorterManner sorterManner) {
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

    public EntityDescriptor extractEntityDescriptor(HeuristicConfigPolicy configPolicy) {
        if (entityClass != null) {
            SolutionDescriptor solutionDescriptor = configPolicy.getSolutionDescriptor();
            EntityDescriptor entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(entityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has an entityClass (" + entityClass + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + entityClass.getSimpleName()
                        + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your " + PlanningSolution.class.getSimpleName()
                        + " implementation's annotated methods too.");
            }
            return entityDescriptor;
        } else if (mimicSelectorRef != null) {
            return configPolicy.getEntityMimicRecorder(mimicSelectorRef).getEntityDescriptor();
        } else {
            return null;
        }
    }

    /**
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public EntitySelector buildEntitySelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        if (mimicSelectorRef != null) {
            return buildMimicReplaying(configPolicy);
        }
        EntityDescriptor entityDescriptor = deduceEntityDescriptor(
                configPolicy.getSolutionDescriptor(), entityClass);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);

        if (nearbySelectionConfig != null) {
            nearbySelectionConfig.validateNearby(resolvedCacheType, resolvedSelectionOrder);
        }
        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        // baseEntitySelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        EntitySelector entitySelector = buildBaseEntitySelector(configPolicy, entityDescriptor,
                SelectionCacheType.max(minimumCacheType, resolvedCacheType),
                determineBaseRandomSelection(entityDescriptor, resolvedCacheType, resolvedSelectionOrder));
        if (nearbySelectionConfig != null) {
            // TODO Static filtering (such as movableEntitySelectionFilter) should affect nearbySelection
            entitySelector = nearbySelectionConfig.applyNearbyEntitySelector(configPolicy,
                    minimumCacheType, resolvedCacheType, resolvedSelectionOrder, entitySelector);
        }
        entitySelector = applyFiltering(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applySorting(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applySelectedLimit(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyMimicRecording(configPolicy, entitySelector);
        return entitySelector;
    }

    protected EntitySelector buildMimicReplaying(HeuristicConfigPolicy configPolicy) {
        if (id != null
                || entityClass != null
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
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with mimicSelectorRef (" + mimicSelectorRef
                    + ") has another property that is not null.");
        }
        EntityMimicRecorder entityMimicRecorder = configPolicy.getEntityMimicRecorder(mimicSelectorRef);
        if (entityMimicRecorder == null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has a mimicSelectorRef (" + mimicSelectorRef
                    + ") for which no entitySelector with that id exists (in its solver phase).");
        }
        return new MimicReplayingEntitySelector(entityMimicRecorder);
    }

    protected boolean determineBaseRandomSelection(EntityDescriptor entityDescriptor,
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
                        || (isBaseInherentlyCached() && !hasFiltering(entityDescriptor));
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached() {
        return true;
    }

    private EntitySelector buildBaseEntitySelector(
            HeuristicConfigPolicy configPolicy, EntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (minimumCacheType == SelectionCacheType.SOLVER) {
            // TODO Solver cached entities are not compatible with DroolsScoreCalculator and IncrementalScoreDirector
            // because between phases the entities get cloned and the KieSession/Maps contains those clones afterwards
            // https://issues.redhat.com/browse/PLANNER-54
            throw new IllegalArgumentException("The minimumCacheType (" + minimumCacheType
                    + ") is not yet supported. Please use " + SelectionCacheType.PHASE + " instead.");
        }
        // FromSolutionEntitySelector has an intrinsicCacheType STEP
        return new FromSolutionEntitySelector(entityDescriptor, minimumCacheType, randomSelection);
    }

    private boolean hasFiltering(EntityDescriptor entityDescriptor) {
        return filterClass != null || entityDescriptor.hasEffectiveMovableEntitySelectionFilter();
    }

    private EntitySelector applyFiltering(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        EntityDescriptor entityDescriptor = entitySelector.getEntityDescriptor();
        if (hasFiltering(entityDescriptor)) {
            List<SelectionFilter> filterList = new ArrayList<>(filterClass == null ? 1 : 2);
            if (filterClass != null) {
                filterList.add(ConfigUtils.newInstance(this, "filterClass", filterClass));
            }
            // Filter out pinned entities
            if (entityDescriptor.hasEffectiveMovableEntitySelectionFilter()) {
                filterList.add(entityDescriptor.getEffectiveMovableEntitySelectionFilter());
            }
            // Do not filter out initialized entities here for CH and ES, because they can be partially initialized
            // Instead, ValueSelectorConfig.applyReinitializeVariableFiltering() does that.
            entitySelector = new FilteringEntitySelector(entitySelector, filterList);
        }
        return entitySelector;
    }

    private void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((sorterManner != null || sorterComparatorClass != null || sorterWeightFactoryClass != null
                || sorterOrder != null || sorterClass != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with sorterManner (" + sorterManner
                    + ") and sorterComparatorClass (" + sorterComparatorClass
                    + ") and sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and sorterOrder (" + sorterOrder
                    + ") and sorterClass (" + sorterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (sorterManner != null && sorterComparatorClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterComparatorClass (" + sorterComparatorClass + ").");
        }
        if (sorterManner != null && sorterWeightFactoryClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
        }
        if (sorterManner != null && sorterClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterManner (" + sorterManner
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterManner != null && sorterOrder != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with sorterManner (" + sorterManner
                    + ") has a non-null sorterOrder (" + sorterOrder + ").");
        }
        if (sorterComparatorClass != null && sorterWeightFactoryClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
        }
        if (sorterComparatorClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterWeightFactoryClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") has both a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterClass != null && sorterOrder != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with sorterClass (" + sorterClass
                    + ") has a non-null sorterOrder (" + sorterOrder + ").");
        }
    }

    private EntitySelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter sorter;
            if (sorterManner != null) {
                EntityDescriptor entityDescriptor = entitySelector.getEntityDescriptor();
                if (!hasSorter(sorterManner, entityDescriptor)) {
                    return entitySelector;
                }
                sorter = determineSorter(sorterManner, entityDescriptor);
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
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterManner (" + sorterManner
                        + ") or a sorterComparatorClass (" + sorterComparatorClass
                        + ") or a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                        + ") or a sorterClass (" + sorterClass + ").");
            }
            entitySelector = new SortingEntitySelector(entitySelector, resolvedCacheType, sorter);
        }
        return entitySelector;
    }

    private void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (probabilityWeightFactoryClass != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with probabilityWeightFactoryClass (" + probabilityWeightFactoryClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    private EntitySelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (probabilityWeightFactoryClass == null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ").");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(this,
                    "probabilityWeightFactoryClass", probabilityWeightFactoryClass);
            entitySelector = new ProbabilityEntitySelector(entitySelector,
                    resolvedCacheType, probabilityWeightFactory);
        }
        return entitySelector;
    }

    private EntitySelector applyShuffling(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            entitySelector = new ShufflingEntitySelector(entitySelector, resolvedCacheType);
        }
        return entitySelector;
    }

    private EntitySelector applyCaching(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(entitySelector.getCacheType()) > 0) {
            entitySelector = new CachingEntitySelector(entitySelector, resolvedCacheType,
                    resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return entitySelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (selectedCountLimit != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + this
                    + ") with selectedCountLimit (" + selectedCountLimit
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private EntitySelector applySelectedLimit(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (selectedCountLimit != null) {
            entitySelector = new SelectedCountLimitEntitySelector(entitySelector,
                    resolvedSelectionOrder.toRandomSelectionBoolean(), selectedCountLimit);
        }
        return entitySelector;
    }

    private EntitySelector applyMimicRecording(HeuristicConfigPolicy configPolicy, EntitySelector entitySelector) {
        if (id != null) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has an empty id (" + id + ").");
            }
            MimicRecordingEntitySelector mimicRecordingEntitySelector = new MimicRecordingEntitySelector(entitySelector);
            configPolicy.addEntityMimicRecorder(id, mimicRecordingEntitySelector);
            entitySelector = mimicRecordingEntitySelector;
        }
        return entitySelector;
    }

    @Override
    public EntitySelectorConfig inherit(EntitySelectorConfig inheritedConfig) {
        id = ConfigUtils.inheritOverwritableProperty(id, inheritedConfig.getId());
        mimicSelectorRef = ConfigUtils.inheritOverwritableProperty(mimicSelectorRef,
                inheritedConfig.getMimicSelectorRef());
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass,
                inheritedConfig.getEntityClass());
        nearbySelectionConfig = ConfigUtils.inheritConfig(nearbySelectionConfig, inheritedConfig.getNearbySelectionConfig());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        filterClass = ConfigUtils.inheritOverwritableProperty(
                filterClass, inheritedConfig.getFilterClass());
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
    public EntitySelectorConfig copyConfig() {
        return new EntitySelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityClass + ")";
    }

    public static boolean hasSorter(EntitySorterManner entitySorterManner, EntityDescriptor entityDescriptor) {
        switch (entitySorterManner) {
            case NONE:
                return false;
            case DECREASING_DIFFICULTY:
                return true;
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                return entityDescriptor.getDecreasingDifficultySorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

    public static SelectionSorter determineSorter(EntitySorterManner entitySorterManner, EntityDescriptor entityDescriptor) {
        SelectionSorter sorter;
        switch (entitySorterManner) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case DECREASING_DIFFICULTY:
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                sorter = entityDescriptor.getDecreasingDifficultySorter();
                if (sorter == null) {
                    throw new IllegalArgumentException("The sorterManner (" + entitySorterManner
                            + ") on entity class (" + entityDescriptor.getEntityClass()
                            + ") fails because that entity class's @" + PlanningEntity.class.getSimpleName()
                            + " annotation does not declare any difficulty comparison.");
                }
                return sorter;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

}
