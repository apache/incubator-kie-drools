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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

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
import org.optaplanner.core.impl.constructionheuristic.placer.PooledEntityPlacer;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pooledEntityPlacer")
public class PooledEntityPlacerConfig extends EntityPlacerConfig<PooledEntityPlacerConfig> {

    public static PooledEntityPlacerConfig unfoldNew(HeuristicConfigPolicy configPolicy,
            MoveSelectorConfig templateMoveSelectorConfig) {
        PooledEntityPlacerConfig config = new PooledEntityPlacerConfig();
        List<MoveSelectorConfig> leafMoveSelectorConfigList = new ArrayList<>();
        MoveSelectorConfig moveSelectorConfig = (MoveSelectorConfig) templateMoveSelectorConfig.copyConfig();
        moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
        config.setMoveSelectorConfig(moveSelectorConfig);

        EntitySelectorConfig entitySelectorConfig = null;
        for (MoveSelectorConfig leafMoveSelectorConfig : leafMoveSelectorConfigList) {
            if (!(leafMoveSelectorConfig instanceof ChangeMoveSelectorConfig)) {
                throw new IllegalStateException("The <constructionHeuristic> contains a moveSelector ("
                        + leafMoveSelectorConfig + ") that isn't a <changeMoveSelector>, a <unionMoveSelector>"
                        + " or a <cartesianProductMoveSelector>.\n"
                        + "Maybe you're using a moveSelector in <constructionHeuristic>"
                        + " that's only supported for <localSearch>.");
            }
            ChangeMoveSelectorConfig changeMoveSelectorConfig = (ChangeMoveSelectorConfig) leafMoveSelectorConfig;
            if (changeMoveSelectorConfig.getEntitySelectorConfig() != null) {
                throw new IllegalStateException("The <constructionHeuristic> contains a changeMoveSelector ("
                        + changeMoveSelectorConfig + ") that contains an entitySelector ("
                        + changeMoveSelectorConfig.getEntitySelectorConfig()
                        + ") without explicitly configuring the <pooledEntityPlacer>.");
            }
            if (entitySelectorConfig == null) {
                EntityDescriptor entityDescriptor = config.deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), null);
                entitySelectorConfig = config.buildEntitySelectorConfig(configPolicy, entityDescriptor);
                changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
            }
            changeMoveSelectorConfig.setEntitySelectorConfig(
                    EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfig.getId()));
        }
        return config;
    }

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

    public PooledEntityPlacerConfig() {
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
    public PooledEntityPlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy) {
        MoveSelectorConfig moveSelectorConfig;
        if (ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
            moveSelectorConfig = buildMoveSelectorConfig(configPolicy);
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
        return new PooledEntityPlacer(moveSelector);
    }

    private MoveSelectorConfig buildMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        EntityDescriptor entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), null);
        EntitySelectorConfig entitySelectorConfig = buildEntitySelectorConfig(configPolicy, entityDescriptor);

        Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor.getGenuineVariableDescriptors();
        List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<>(
                variableDescriptors.size());
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
            subMoveSelectorConfigList.add(buildChangeMoveSelectorConfig(
                    configPolicy, entitySelectorConfig.getId(), variableDescriptor));
        }
        // The first entitySelectorConfig must be the mimic recorder, not the mimic replayer
        ((ChangeMoveSelectorConfig) subMoveSelectorConfigList.get(0)).setEntitySelectorConfig(entitySelectorConfig);
        MoveSelectorConfig moveSelectorConfig;
        if (subMoveSelectorConfigList.size() > 1) {
            // Default to cartesian product (not a union) of planning variables.
            moveSelectorConfig = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
        } else {
            moveSelectorConfig = subMoveSelectorConfigList.get(0);
        }
        return moveSelectorConfig;
    }

    private EntitySelectorConfig buildEntitySelectorConfig(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor) {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        Class<?> entityClass = entityDescriptor.getEntityClass();
        entitySelectorConfig.setId(entityClass.getName());
        entitySelectorConfig.setEntityClass(entityClass);
        if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
            entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
            entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            entitySelectorConfig.setSorterManner(configPolicy.getEntitySorterManner());
        }
        return entitySelectorConfig;
    }

    private ChangeMoveSelectorConfig buildChangeMoveSelectorConfig(HeuristicConfigPolicy configPolicy,
            String entitySelectorConfigId, GenuineVariableDescriptor variableDescriptor) {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        changeMoveSelectorConfig.setEntitySelectorConfig(
                EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfigId));
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
        changeValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
        if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
            if (variableDescriptor.isValueRangeEntityIndependent()) {
                changeValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
            } else {
                changeValueSelectorConfig.setCacheType(SelectionCacheType.STEP);
            }
            changeValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            changeValueSelectorConfig.setSorterManner(configPolicy.getValueSorterManner());
        }
        changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
        return changeMoveSelectorConfig;
    }

    @Override
    public PooledEntityPlacerConfig inherit(PooledEntityPlacerConfig inheritedConfig) {
        setMoveSelectorConfig(ConfigUtils.inheritOverwritableProperty(
                getMoveSelectorConfig(), inheritedConfig.getMoveSelectorConfig()));
        return this;
    }

    @Override
    public PooledEntityPlacerConfig copyConfig() {
        return new PooledEntityPlacerConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getMoveSelectorConfig() + ")";
    }

}
