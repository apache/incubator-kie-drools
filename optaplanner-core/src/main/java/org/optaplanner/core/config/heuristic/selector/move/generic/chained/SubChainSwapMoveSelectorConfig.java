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

package org.optaplanner.core.config.heuristic.selector.move.generic.chained;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainSwapMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("subChainSwapMoveSelector")
public class SubChainSwapMoveSelectorConfig extends MoveSelectorConfig<SubChainSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "subChainSwapMoveSelector";

    private Class<?> entityClass = null;
    @XmlElement(name = "subChainSelector")
    @XStreamAlias("subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = null;
    @XmlElement(name = "secondarySubChainSelector")
    @XStreamAlias("secondarySubChainSelector")
    private SubChainSelectorConfig secondarySubChainSelectorConfig = null;

    private Boolean selectReversingMoveToo = null;

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public SubChainSelectorConfig getSubChainSelectorConfig() {
        return subChainSelectorConfig;
    }

    public void setSubChainSelectorConfig(SubChainSelectorConfig subChainSelectorConfig) {
        this.subChainSelectorConfig = subChainSelectorConfig;
    }

    public SubChainSelectorConfig getSecondarySubChainSelectorConfig() {
        return secondarySubChainSelectorConfig;
    }

    public void setSecondarySubChainSelectorConfig(SubChainSelectorConfig secondarySubChainSelectorConfig) {
        this.secondarySubChainSelectorConfig = secondarySubChainSelectorConfig;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor entityDescriptor = deduceEntityDescriptor(
                configPolicy.getSolutionDescriptor(), entityClass);
        SubChainSelectorConfig subChainSelectorConfig_ = subChainSelectorConfig == null ? new SubChainSelectorConfig()
                : subChainSelectorConfig;
        SubChainSelector leftSubChainSelector = subChainSelectorConfig_.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        SubChainSelectorConfig rightSubChainSelectorConfig = defaultIfNull(secondarySubChainSelectorConfig,
                subChainSelectorConfig_);
        SubChainSelector rightSubChainSelector = rightSubChainSelectorConfig.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainSwapMoveSelector(leftSubChainSelector, rightSubChainSelector, randomSelection,
                defaultIfNull(selectReversingMoveToo, true));
    }

    @Override
    public SubChainSwapMoveSelectorConfig inherit(SubChainSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass, inheritedConfig.getEntityClass());
        subChainSelectorConfig = ConfigUtils.inheritConfig(subChainSelectorConfig, inheritedConfig.getSubChainSelectorConfig());
        secondarySubChainSelectorConfig = ConfigUtils.inheritConfig(secondarySubChainSelectorConfig,
                inheritedConfig.getSecondarySubChainSelectorConfig());
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
        return this;
    }

    @Override
    public SubChainSwapMoveSelectorConfig copyConfig() {
        return new SubChainSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig
                + (secondarySubChainSelectorConfig == null ? "" : ", " + secondarySubChainSelectorConfig) + ")";
    }

}
