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
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("queuedEntityPlacer")
public class QueuedEntityPlacerConfig extends EntityPlacerConfig<QueuedEntityPlacerConfig> {

    public static QueuedEntityPlacerConfig unfoldNew(HeuristicConfigPolicy configPolicy,
            List<MoveSelectorConfig> templateMoveSelectorConfigList) {
        QueuedEntityPlacerConfig config = new QueuedEntityPlacerConfig();
        config.entitySelectorConfig = config.buildEntitySelectorConfig(configPolicy);
        config.moveSelectorConfigList = new ArrayList<>(templateMoveSelectorConfigList.size());
        List<MoveSelectorConfig> leafMoveSelectorConfigList = new ArrayList<>(templateMoveSelectorConfigList.size());
        for (MoveSelectorConfig templateMoveSelectorConfig : templateMoveSelectorConfigList) {
            MoveSelectorConfig moveSelectorConfig = (MoveSelectorConfig) templateMoveSelectorConfig.copyConfig();
            moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
            config.moveSelectorConfigList.add(moveSelectorConfig);
        }
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
                        + ") without explicitly configuring the <queuedEntityPlacer>.");
            }
            changeMoveSelectorConfig.setEntitySelectorConfig(
                    EntitySelectorConfig.newMimicSelectorConfig(config.entitySelectorConfig.getId()));
        }
        return config;
    }

    @XmlElement(name = "entitySelector")
    @XStreamAlias("entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;

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
    protected List<MoveSelectorConfig> moveSelectorConfigList = null;

    public QueuedEntityPlacerConfig() {
    }

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public QueuedEntityPlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy) {
        EntitySelectorConfig entitySelectorConfig_ = buildEntitySelectorConfig(configPolicy);
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(configPolicy,
                SelectionCacheType.PHASE, SelectionOrder.ORIGINAL);

        List<MoveSelectorConfig> moveSelectorConfigList_;
        if (ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
            EntityDescriptor entityDescriptor = entitySelector.getEntityDescriptor();
            Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor.getGenuineVariableDescriptors();
            List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<>(
                    variableDescriptors.size());
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
                subMoveSelectorConfigList.add(buildChangeMoveSelectorConfig(
                        configPolicy, entitySelectorConfig_.getId(), variableDescriptor));
            }
            MoveSelectorConfig subMoveSelectorConfig;
            if (subMoveSelectorConfigList.size() > 1) {
                // Default to cartesian product (not a queue) of planning variables.
                subMoveSelectorConfig = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
            } else {
                subMoveSelectorConfig = subMoveSelectorConfigList.get(0);
            }
            moveSelectorConfigList_ = Collections.singletonList(subMoveSelectorConfig);
        } else {
            moveSelectorConfigList_ = moveSelectorConfigList;
        }
        List<MoveSelector> moveSelectorList = new ArrayList<>(moveSelectorConfigList_.size());
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList_) {
            moveSelectorList.add(moveSelectorConfig.buildMoveSelector(
                    configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL));
        }
        return new QueuedEntityPlacer(entitySelector, moveSelectorList);
    }

    private EntitySelectorConfig buildEntitySelectorConfig(HeuristicConfigPolicy configPolicy) {
        EntitySelectorConfig entitySelectorConfig_;
        if (entitySelectorConfig == null) {
            entitySelectorConfig_ = new EntitySelectorConfig();
            EntityDescriptor entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), null);
            Class<?> entityClass = entityDescriptor.getEntityClass();
            entitySelectorConfig_.setId(entityClass.getName());
            entitySelectorConfig_.setEntityClass(entityClass);
            if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
                entitySelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                entitySelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                entitySelectorConfig_.setSorterManner(configPolicy.getEntitySorterManner());
            }
        } else {
            entitySelectorConfig_ = entitySelectorConfig;
        }
        if (entitySelectorConfig_.getCacheType() != null
                && entitySelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The queuedEntityPlacer (" + this
                    + ") cannot have an entitySelectorConfig (" + entitySelectorConfig_
                    + ") with a cacheType (" + entitySelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return entitySelectorConfig_;
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
    public QueuedEntityPlacerConfig inherit(QueuedEntityPlacerConfig inheritedConfig) {
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        moveSelectorConfigList = ConfigUtils.inheritMergeableListConfig(
                moveSelectorConfigList, inheritedConfig.getMoveSelectorConfigList());
        return this;
    }

    @Override
    public QueuedEntityPlacerConfig copyConfig() {
        return new QueuedEntityPlacerConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + moveSelectorConfigList + ")";
    }

}
