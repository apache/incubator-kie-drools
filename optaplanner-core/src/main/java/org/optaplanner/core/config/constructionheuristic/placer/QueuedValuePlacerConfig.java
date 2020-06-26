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

package org.optaplanner.core.config.constructionheuristic.placer;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.KOptMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacer;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("queuedValuePlacer")
public class QueuedValuePlacerConfig extends EntityPlacerConfig<QueuedValuePlacerConfig> {

    public static QueuedValuePlacerConfig unfoldNew(HeuristicConfigPolicy configPolicy,
            MoveSelectorConfig templateMoveSelectorConfig) {
        throw new UnsupportedOperationException("The <constructionHeuristic> contains a moveSelector ("
                + templateMoveSelectorConfig + ") and the <queuedValuePlacer> does not support unfolding those yet.");
    }

    protected Class<?> entityClass = null;

    @XmlElement(name = "valueSelector")
    @XStreamAlias("valueSelector")
    protected ValueSelectorConfig valueSelectorConfig = null;

    // TODO This is a List due to XStream limitations. With JAXB it could be just a MoveSelectorConfig instead.
    @XmlElements({
            @XmlElement(name = CartesianProductMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = CartesianProductMoveSelectorConfig.class),
            @XmlElement(name = ChangeMoveSelectorConfig.XML_ELEMENT_NAME, type = ChangeMoveSelectorConfig.class),
            @XmlElement(name = KOptMoveSelectorConfig.XML_ELEMENT_NAME, type = KOptMoveSelectorConfig.class),
            @XmlElement(name = MoveIteratorFactoryConfig.XML_ELEMENT_NAME, type = MoveIteratorFactoryConfig.class),
            @XmlElement(name = MoveListFactoryConfig.XML_ELEMENT_NAME, type = MoveListFactoryConfig.class),
            @XmlElement(name = PillarChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = PillarChangeMoveSelectorConfig.class),
            @XmlElement(name = PillarSwapMoveSelectorConfig.XML_ELEMENT_NAME, type = PillarSwapMoveSelectorConfig.class),
            @XmlElement(name = SubChainChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainChangeMoveSelectorConfig.class),
            @XmlElement(name = SubChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainSwapMoveSelectorConfig.class),
            @XmlElement(name = SwapMoveSelectorConfig.XML_ELEMENT_NAME, type = SwapMoveSelectorConfig.class),
            @XmlElement(name = TailChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = TailChainSwapMoveSelectorConfig.class),
            @XmlElement(name = UnionMoveSelectorConfig.XML_ELEMENT_NAME, type = UnionMoveSelectorConfig.class)
    })
    @XStreamImplicit()
    private List<MoveSelectorConfig> moveSelectorConfigList = null;

    public QueuedValuePlacerConfig() {
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    public MoveSelectorConfig getMoveSelectorConfig() {
        return moveSelectorConfigList == null ? null : moveSelectorConfigList.get(0);
    }

    public void setMoveSelectorConfig(MoveSelectorConfig moveSelectorConfig) {
        this.moveSelectorConfigList = moveSelectorConfig == null ? null : Collections.singletonList(moveSelectorConfig);
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public QueuedValuePlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy) {
        EntityDescriptor entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), entityClass);
        ValueSelectorConfig valueSelectorConfig_ = buildValueSelectorConfig(configPolicy, entityDescriptor);
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy, entityDescriptor,
                SelectionCacheType.PHASE, SelectionOrder.ORIGINAL, false);

        MoveSelectorConfig moveSelectorConfig;
        if (ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
            moveSelectorConfig = buildChangeMoveSelectorConfig(configPolicy,
                    valueSelectorConfig_.getId(), valueSelector.getVariableDescriptor());
        } else if (moveSelectorConfigList.size() == 1) {
            moveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            // TODO moveSelectorConfigList is only a List because of XStream limitations.
            throw new IllegalArgumentException("The moveSelectorConfigList (" + moveSelectorConfigList
                    + ") must be a singleton or empty. Use a single " + UnionMoveSelectorConfig.class.getSimpleName()
                    + " or " + CartesianProductMoveSelectorConfig.class.getSimpleName()
                    + " element to nest multiple MoveSelectors.");
        }
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(
                configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The queuedValuePlacer (" + this
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return new QueuedValuePlacer((EntityIndependentValueSelector) valueSelector, moveSelector);
    }

    private ValueSelectorConfig buildValueSelectorConfig(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor) {
        ValueSelectorConfig valueSelectorConfig_;
        if (valueSelectorConfig == null) {
            valueSelectorConfig_ = new ValueSelectorConfig();
            Class<?> entityClass = entityDescriptor.getEntityClass();
            GenuineVariableDescriptor variableDescriptor = deduceVariableDescriptor(entityDescriptor, null);
            valueSelectorConfig_.setId(entityClass.getName() + "." + variableDescriptor.getVariableName());
            valueSelectorConfig_.setVariableName(variableDescriptor.getVariableName());
            if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
                valueSelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                valueSelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                valueSelectorConfig_.setSorterManner(configPolicy.getValueSorterManner());
            }
        } else {
            valueSelectorConfig_ = valueSelectorConfig;
        }
        if (valueSelectorConfig_.getCacheType() != null
                && valueSelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The queuedValuePlacer (" + this
                    + ") cannot have a valueSelectorConfig (" + valueSelectorConfig_
                    + ") with a cacheType (" + valueSelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return valueSelectorConfig_;
    }

    private ChangeMoveSelectorConfig buildChangeMoveSelectorConfig(HeuristicConfigPolicy configPolicy,
            String valueSelectorConfigId, GenuineVariableDescriptor variableDescriptor) {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        EntitySelectorConfig changeEntitySelectorConfig = new EntitySelectorConfig();
        EntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
        changeEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
        if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
            changeEntitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
            changeEntitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            changeEntitySelectorConfig.setSorterManner(configPolicy.getEntitySorterManner());
        }
        changeMoveSelectorConfig.setEntitySelectorConfig(changeEntitySelectorConfig);
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
        changeValueSelectorConfig.setMimicSelectorRef(valueSelectorConfigId);
        changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
        return changeMoveSelectorConfig;
    }

    @Override
    public QueuedValuePlacerConfig inherit(QueuedValuePlacerConfig inheritedConfig) {
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass, inheritedConfig.getEntityClass());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        setMoveSelectorConfig(ConfigUtils.inheritOverwritableProperty(
                getMoveSelectorConfig(), inheritedConfig.getMoveSelectorConfig()));
        return this;
    }

    @Override
    public QueuedValuePlacerConfig copyConfig() {
        return new QueuedValuePlacerConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ", " + moveSelectorConfigList + ")";
    }

}
