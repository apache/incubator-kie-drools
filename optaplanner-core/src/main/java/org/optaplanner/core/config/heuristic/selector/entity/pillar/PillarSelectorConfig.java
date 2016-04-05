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

import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.BooleanUtils;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.DefaultPillarSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;

import static org.apache.commons.lang3.ObjectUtils.*;

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

    public Boolean getSubPillarEnabled() {
        return subPillarEnabled;
    }

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
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @param variableNameIncludeList sometimes null
     * @return never null
     */
    public PillarSelector buildPillarSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder,
            List<String> variableNameIncludeList) {
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
            throw new IllegalArgumentException("The pillarSelectorConfig (" + this
                    + ")'s minimumCacheType (" + minimumCacheType
                    + ") must not be higher than " + SelectionCacheType.STEP
                    + " because the pillars change every step.");
        }
        // EntitySelector uses SelectionOrder.ORIGINAL because a DefaultPillarSelector STEP caches the values
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.ORIGINAL);
        Collection<GenuineVariableDescriptor> variableDescriptors = deduceVariableDescriptorList(
                entitySelector.getEntityDescriptor(), variableNameIncludeList);
        if (BooleanUtils.isFalse(subPillarEnabled)
                && (minimumSubPillarSize != null || maximumSubPillarSize != null)) {
            throw new IllegalArgumentException("The pillarSelectorConfig (" + this
                    + ") must not have subPillarEnabled (" + subPillarEnabled
                    + ") with minimumSubPillarSize (" + minimumSubPillarSize
                    + ") and maximumSubPillarSize (" + maximumSubPillarSize + ").");
        }
        return new DefaultPillarSelector(entitySelector, variableDescriptors,
                inheritedSelectionOrder.toRandomSelectionBoolean(),
                defaultIfNull(subPillarEnabled, true),
                defaultIfNull(minimumSubPillarSize, 1),
                defaultIfNull(maximumSubPillarSize, Integer.MAX_VALUE));
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
