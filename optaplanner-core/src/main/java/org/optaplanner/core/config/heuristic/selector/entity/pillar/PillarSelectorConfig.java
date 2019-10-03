/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.heuristic.selector.entity.pillar;

import java.util.Comparator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SubPillarType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.DefaultPillarSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@XStreamAlias("pillarSelector")
public class PillarSelectorConfig extends SelectorConfig<PillarSelectorConfig> {

    @XStreamAlias("entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;

    protected Boolean subPillarEnabled = null;
    protected Integer minimumSubPillarSize = null;
    protected Integer maximumSubPillarSize = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    /**
     * @deprecated in favor of SubPillarType
     * @see SubPillarType and its uses in pillar move selectors.
     * @return Null when not set.
     */
    @Deprecated(/* forRemoval = true */)
    public Boolean getSubPillarEnabled() {
        return subPillarEnabled;
    }

    /**
     * @param subPillarEnabled true to enable, false to disable, null to leave unset.
     * @deprecated in favor of SubPillarType
     * @see SubPillarType and its uses in pillar move selectors.
     */
    @Deprecated(/* forRemoval = true */)
    public void setSubPillarEnabled(Boolean subPillarEnabled) {
        this.subPillarEnabled = subPillarEnabled;
    }

    public Integer getMinimumSubPillarSize() {
        return minimumSubPillarSize;
    }

    public void setMinimumSubPillarSize(Integer minimumSubPillarSize) {
        this.minimumSubPillarSize = minimumSubPillarSize;
    }

    public Integer getMaximumSubPillarSize() {
        return maximumSubPillarSize;
    }

    public void setMaximumSubPillarSize(Integer maximumSubPillarSize) {
        this.maximumSubPillarSize = maximumSubPillarSize;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     * @param configPolicy never null
     * @param subPillarType if null, defaults to {@link SubPillarType#ALL} for backwards compatibility reasons.
     * @param subPillarSequenceComparatorClass if not null, will force entites in the pillar to come in this order
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @param variableNameIncludeList sometimes null
     * @return never null
     */
    public PillarSelector buildPillarSelector(HeuristicConfigPolicy configPolicy, SubPillarType subPillarType,
            Class<? extends Comparator> subPillarSequenceComparatorClass, SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder, List<String> variableNameIncludeList) {
        if (subPillarEnabled != null && subPillarType != null) {
            throw new IllegalArgumentException("Property subPillarEnabled (" + subPillarEnabled +
                    ") on pillarSelectorConfig (" + this + ") must not be present when subPillarType (" +
                    subPillarType + ") is set on the parent MoveSelectorConfig.");
        }
        if (subPillarType != SubPillarType.SEQUENCE && subPillarSequenceComparatorClass != null) {
            throw new IllegalArgumentException("Subpillar type (" + subPillarType + ") on pillarSelectorConfig (" + this +
                    ") is not " + SubPillarType.SEQUENCE + ", yet subPillarSequenceComparatorClass (" +
                    subPillarSequenceComparatorClass + ") is provided.");
        }
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
            throw new IllegalArgumentException("The pillarSelectorConfig (" + this
                    + ")'s minimumCacheType (" + minimumCacheType
                    + ") must not be higher than " + SelectionCacheType.STEP
                    + " because the pillars change every step.");
        }
        boolean subPillarActuallyEnabled =
                subPillarEnabled != null ? subPillarEnabled : subPillarType != SubPillarType.NONE;
        // EntitySelector uses SelectionOrder.ORIGINAL because a DefaultPillarSelector STEP caches the values
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.ORIGINAL);
        List<GenuineVariableDescriptor> variableDescriptors = deduceVariableDescriptorList(
                entitySelector.getEntityDescriptor(), variableNameIncludeList);
        if (!subPillarActuallyEnabled
                && (minimumSubPillarSize != null || maximumSubPillarSize != null)) {
            throw new IllegalArgumentException("The pillarSelectorConfig (" + this
                    + ") must not disable subpillars while providing minimumSubPillarSize (" + minimumSubPillarSize
                    + ") or maximumSubPillarSize (" + maximumSubPillarSize + ").");
        }

        SubPillarConfigPolicy subPillarPolicy = subPillarActuallyEnabled ?
                configureSubPillars(subPillarType, subPillarSequenceComparatorClass, entitySelector, minimumSubPillarSize,
                        maximumSubPillarSize) :
                SubPillarConfigPolicy.withoutSubpillars();
        return new DefaultPillarSelector(entitySelector, variableDescriptors,
                inheritedSelectionOrder.toRandomSelectionBoolean(), subPillarPolicy);
    }

    private SubPillarConfigPolicy configureSubPillars(SubPillarType pillarType,
            Class<? extends Comparator> pillarOrderComparatorClass, EntitySelector entitySelector,
            Integer minimumSubPillarSize, Integer maximumSubPillarSize) {
        int actualMinimumSubPillarSize = defaultIfNull(minimumSubPillarSize, 1);
        int actualMaximumSubPillarSize = defaultIfNull(maximumSubPillarSize, Integer.MAX_VALUE);
        if (pillarType == null) { // for backwards compatibility reasons
            return SubPillarConfigPolicy.withSubpillars(actualMinimumSubPillarSize, actualMaximumSubPillarSize);
        }
        switch (pillarType) {
            case ALL:
                return SubPillarConfigPolicy.withSubpillars(actualMinimumSubPillarSize, actualMaximumSubPillarSize);
            case SEQUENCE:
                if (pillarOrderComparatorClass == null) {
                    Class<?> entityClass = entitySelector.getEntityDescriptor().getEntityClass();
                    boolean isComparable = Comparable.class.isAssignableFrom(entityClass);
                    if (!isComparable) {
                        throw new IllegalArgumentException("Pillar type (" + pillarType + ") on pillarSelectorConfig (" +
                                this + ") does not provide pillarOrderComparatorClass while the entity (" +
                                entityClass.getCanonicalName() + ") does not implement Comparable.");
                    }
                    Comparator<Comparable> comparator = Comparable::compareTo;
                    return SubPillarConfigPolicy.sequential(actualMinimumSubPillarSize, actualMaximumSubPillarSize,
                            comparator);
                } else {
                    Comparator<Object> comparator = ConfigUtils.newInstance(this, "pillarOrderComparatorClass", pillarOrderComparatorClass);
                    return SubPillarConfigPolicy.sequential(actualMinimumSubPillarSize, actualMaximumSubPillarSize,
                            comparator);
                }
            default:
                throw new IllegalStateException("Subpillars can not be enabled and disabled at the same time.");
        }
    }

    @Override
    public void inherit(PillarSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        subPillarEnabled = ConfigUtils.inheritOverwritableProperty(subPillarEnabled,
                inheritedConfig.getSubPillarEnabled());
        minimumSubPillarSize = ConfigUtils.inheritOverwritableProperty(minimumSubPillarSize,
                inheritedConfig.getMinimumSubPillarSize());
        maximumSubPillarSize = ConfigUtils.inheritOverwritableProperty(maximumSubPillarSize,
                inheritedConfig.getMaximumSubPillarSize());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ")";
    }
}
