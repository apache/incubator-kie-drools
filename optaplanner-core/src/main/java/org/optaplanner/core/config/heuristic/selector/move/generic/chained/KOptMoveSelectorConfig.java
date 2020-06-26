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
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.KOptMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * THIS IS VERY EXPERIMENTAL. It's NOT DOCUMENTED because we'll only document it when it actually works in more than 1 use case.
 * It's riddled with TODOs.
 * Do not use.
 *
 * @see TailChainSwapMoveSelectorConfig
 */
@XStreamAlias("kOptMoveSelector")
public class KOptMoveSelectorConfig extends MoveSelectorConfig<KOptMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "kOptMoveSelector";

    public static final int K = 3;

    @XmlElement(name = "entitySelector")
    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    /**
     * Like {@link TailChainSwapMoveSelectorConfig#valueSelectorConfig} but used multiple times to create 1 move.
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
        ValueSelector[] valueSelectors = new ValueSelector[K - 1];
        for (int i = 0; i < valueSelectors.length; i++) {
            valueSelectors[i] = valueSelectorConfig_.buildValueSelector(configPolicy,
                    entitySelector.getEntityDescriptor(),
                    minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));

        }
        return new KOptMoveSelector(entitySelector, valueSelectors, randomSelection);
    }

    @Override
    public KOptMoveSelectorConfig inherit(KOptMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        return this;
    }

    @Override
    public KOptMoveSelectorConfig copyConfig() {
        return new KOptMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
