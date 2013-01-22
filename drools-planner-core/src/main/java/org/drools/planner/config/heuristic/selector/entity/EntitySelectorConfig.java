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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.drools.planner.core.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.FromSolutionEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.FilteringEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.ProbabilityEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.SortingEntitySelector;

@XStreamAlias("entitySelector")
public class EntitySelectorConfig extends SelectorConfig {

    protected Class<?> planningEntityClass = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    @XStreamImplicit(itemFieldName = "filterClass")
    protected List<Class<? extends SelectionFilter>> filterClassList = null;

    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    public Class<?> getPlanningEntityClass() {
        return planningEntityClass;
    }

    public void setPlanningEntityClass(Class<?> planningEntityClass) {
        this.planningEntityClass = planningEntityClass;
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

    public List<Class<? extends SelectionFilter>> getFilterClassList() {
        return filterClassList;
    }

    public void setFilterClassList(List<Class<? extends SelectionFilter>> filterClassList) {
        this.filterClassList = filterClassList;
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
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public EntitySelector buildEntitySelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        PlanningEntityDescriptor entityDescriptor = fetchEntityDescriptor(solutionDescriptor);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        minimumCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);

        // baseEntitySelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        EntitySelector entitySelector = buildBaseEntitySelector(environmentMode, entityDescriptor,
                minimumCacheType, resolvedCacheType.isCached() ? SelectionOrder.ORIGINAL : resolvedSelectionOrder);

        entitySelector = applyFiltering(entityDescriptor, resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applySorting(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        return entitySelector;
    }

    private EntitySelector applyFiltering(PlanningEntityDescriptor entityDescriptor,
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (!CollectionUtils.isEmpty(filterClassList)
                || entityDescriptor.hasMovableEntitySelectionFilter()) {
            List<SelectionFilter> filterList = new ArrayList<SelectionFilter>(
                    filterClassList == null ? 1 : filterClassList.size() + 1);
            if (filterClassList != null) {
                for (Class<? extends SelectionFilter> filterClass : filterClassList) {
                    filterList.add(ConfigUtils.newInstance(this, "filterClass", filterClass));
                }
            }
            if (entityDescriptor.hasMovableEntitySelectionFilter()) {
                filterList.add(entityDescriptor.getMovableEntitySelectionFilter());
            }
            entitySelector = new FilteringEntitySelector(entitySelector, filterList);
        }
        return entitySelector;
    }

    private EntitySelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (sorterComparatorClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with sorterComparatorClass (" + sorterComparatorClass
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") that is not " + SelectionOrder.ORIGINAL + ".");
            }
            Comparator<Object> sorterComparator = ConfigUtils.newInstance(this,
                    "sorterComparatorClass", sorterComparatorClass);
            SelectionSorter sorter = new ComparatorSelectionSorter(sorterComparator,
                    SelectionSorterOrder.resolve(sorterOrder));
            entitySelector = new SortingEntitySelector(entitySelector, resolvedCacheType, sorter);
        }
        if (sorterWeightFactoryClass != null) {
            if (sorterComparatorClass != null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has both an sorterComparatorClass (" + sorterComparatorClass
                        + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
            }
            if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with sorterWeightFactoryClass (" + sorterWeightFactoryClass
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") that is not " + SelectionOrder.ORIGINAL + ".");
            }
            SelectionSorterWeightFactory sorterWeightFactory = ConfigUtils.newInstance(this,
                    "sorterWeightFactoryClass", sorterWeightFactoryClass);
            SelectionSorter sorter = new WeightFactorySelectionSorter(sorterWeightFactory,
                    SelectionSorterOrder.resolve(sorterOrder));
            entitySelector = new SortingEntitySelector(entitySelector, resolvedCacheType, sorter);
        }
        if (sorterClass != null) {
            if (sorterComparatorClass != null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has both an sorterComparatorClass (" + sorterComparatorClass
                        + ") and a sorterClass (" + sorterClass + ").");
            }
            if (sorterWeightFactoryClass != null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has both an sorterWeightFactoryClass (" + sorterWeightFactoryClass
                        + ") and a sorterClass (" + sorterClass + ").");
            }
            if (sorterOrder != null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has both an sorterClass (" + sorterClass
                        + ") but the sorterOrder (" + sorterOrder + ") should be null.");
            }
            if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with sorterClass (" + sorterClass
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") that is not " + SelectionOrder.ORIGINAL + ".");
            }
            SelectionSorter sorter = ConfigUtils.newInstance(this,
                    "sorterClass", sorterClass);
            entitySelector = new SortingEntitySelector(entitySelector, resolvedCacheType, sorter);
        }
        return entitySelector;
    }

    private EntitySelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            EntitySelector entitySelector) {
        if (probabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ") has a resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ") that is not " + SelectionOrder.RANDOM + ".");
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
                    resolvedSelectionOrder == SelectionOrder.RANDOM);
        }
        return entitySelector;
    }

    private PlanningEntityDescriptor fetchEntityDescriptor(SolutionDescriptor solutionDescriptor) {
        PlanningEntityDescriptor entityDescriptor;
        if (planningEntityClass != null) {
            entityDescriptor = solutionDescriptor.getPlanningEntityDescriptorStrict(planningEntityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this + ") has a planningEntityClass ("
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
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") has no configured planningEntityClass ("
                        + planningEntityClass + ") and because there are multiple in the planningEntityClassSet ("
                        + solutionDescriptor.getPlanningEntityClassSet()
                        + "), it can not be deducted automatically.");
            }
            entityDescriptor = planningEntityDescriptors.iterator().next();
        }
        return entityDescriptor;
    }

    private EntitySelector buildBaseEntitySelector(
            EnvironmentMode environmentMode, PlanningEntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder resolvedSelectionOrder) {
        // FromSolutionEntitySelector caches by design, so it uses the minimumCacheType
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) < 0) {
            // cacheType upgrades to SelectionCacheType.STEP (without shuffling) because JIT is not supported
            minimumCacheType = SelectionCacheType.STEP;
        }
        if (minimumCacheType == SelectionCacheType.SOLVER) {
            // TODO Solver cached entities are not compatible with DroolsScoreCalculator
            // because between phases the entities get cloned and the WorkingMemory contains those clones afterwards
            // https://issues.jboss.org/browse/JBRULES-3557
            throw new IllegalArgumentException("The minimumCacheType (" + minimumCacheType
                    + ") is not yet supported. Please use " + SelectionCacheType.PHASE + " instead.");
        }
        return new FromSolutionEntitySelector(entityDescriptor,
                minimumCacheType, resolvedSelectionOrder == SelectionOrder.RANDOM
        );
    }

    public void inherit(EntitySelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        planningEntityClass = ConfigUtils.inheritOverwritableProperty(planningEntityClass,
                inheritedConfig.getPlanningEntityClass());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        filterClassList = ConfigUtils.inheritOverwritableProperty
                (filterClassList, inheritedConfig.getFilterClassList());
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
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + planningEntityClass + ")";
    }

}
