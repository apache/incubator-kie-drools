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

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("subChainChangeMoveSelector")
public class SubChainChangeMoveSelectorConfig extends MoveSelectorConfig<SubChainChangeMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "subChainChangeMoveSelector";

    private Class<?> entityClass = null;
    @XmlElement(name = "subChainSelector")
    @XStreamAlias("subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = null;
    @XmlElement(name = "valueSelector")
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;

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

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
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
        SubChainSelector subChainSelector = subChainSelectorConfig_.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ = valueSelectorConfig == null ? new ValueSelectorConfig()
                : valueSelectorConfig;
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        return new SubChainChangeMoveSelector(subChainSelector, (EntityIndependentValueSelector) valueSelector,
                randomSelection, defaultIfNull(selectReversingMoveToo, true));
    }

    @Override
    public SubChainChangeMoveSelectorConfig inherit(SubChainChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass, inheritedConfig.getEntityClass());
        subChainSelectorConfig = ConfigUtils.inheritConfig(subChainSelectorConfig, inheritedConfig.getSubChainSelectorConfig());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
        return this;
    }

    @Override
    public SubChainChangeMoveSelectorConfig copyConfig() {
        return new SubChainChangeMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
