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

import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.TailChainSwapMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Also known as a 2-opt move selector config.
 */
@XStreamAlias("tailChainSwapMoveSelector")
public class TailChainSwapMoveSelectorConfig extends MoveSelectorConfig<TailChainSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "tailChainSwapMoveSelector";

    @XmlElement(name = "entitySelector")
    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    /**
     * Uses a valueSelector instead of a secondaryEntitySelector because
     * the secondary entity might not exist if the value is a buoy (= the last entity in a chain)
     * and also because with nearby selection, it's more important that the value is near (instead of the secondary entity).
     */
    @XmlElement(name = "valueSelector")
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ = valueSelectorConfig == null ? new ValueSelectorConfig()
                : valueSelectorConfig;
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(),
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new TailChainSwapMoveSelector(entitySelector, valueSelector, randomSelection);
    }

    @Override
    public TailChainSwapMoveSelectorConfig inherit(TailChainSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        return this;
    }

    @Override
    public TailChainSwapMoveSelectorConfig copyConfig() {
        return new TailChainSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
