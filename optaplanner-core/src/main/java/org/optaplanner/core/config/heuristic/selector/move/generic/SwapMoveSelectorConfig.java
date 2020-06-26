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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMoveSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("swapMoveSelector")
public class SwapMoveSelectorConfig extends MoveSelectorConfig<SwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "swapMoveSelector";

    @XmlElement(name = "entitySelector")
    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    @XmlElement(name = "secondaryEntitySelector")
    @XStreamAlias("secondaryEntitySelector")
    private EntitySelectorConfig secondaryEntitySelectorConfig = null;

    @XmlElementWrapper(name = "variableNameIncludes")
    @XmlElement(name = "variableNameInclude")
    @XStreamImplicit(itemFieldName = "variableNameInclude")
    //    @XStreamAlias("variableNameIncludes")
    //    @XStreamConverter(value = NamedCollectionConverter.class,
    //            strings = {"variableNameInclude"}, types = {String.class}, useImplicitType = false)
    private List<String> variableNameIncludeList = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public EntitySelectorConfig getSecondaryEntitySelectorConfig() {
        return secondaryEntitySelectorConfig;
    }

    public void setSecondaryEntitySelectorConfig(EntitySelectorConfig secondaryEntitySelectorConfig) {
        this.secondaryEntitySelectorConfig = secondaryEntitySelectorConfig;
    }

    public List<String> getVariableNameIncludeList() {
        return variableNameIncludeList;
    }

    public void setVariableNameIncludeList(List<String> variableNameIncludeList) {
        this.variableNameIncludeList = variableNameIncludeList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector leftEntitySelector = entitySelectorConfig_.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntitySelectorConfig rightEntitySelectorConfig = defaultIfNull(secondaryEntitySelectorConfig,
                entitySelectorConfig_);
        EntitySelector rightEntitySelector = rightEntitySelectorConfig.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        List<GenuineVariableDescriptor> variableDescriptorList = deduceVariableDescriptorList(
                leftEntitySelector.getEntityDescriptor(), variableNameIncludeList);
        return new SwapMoveSelector(leftEntitySelector, rightEntitySelector, variableDescriptorList,
                randomSelection);
    }

    @Override
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        EntityDescriptor onlyEntityDescriptor = entitySelectorConfig == null ? null
                : entitySelectorConfig.extractEntityDescriptor(configPolicy);
        if (secondaryEntitySelectorConfig != null) {
            EntityDescriptor onlySecondaryEntityDescriptor = secondaryEntitySelectorConfig
                    .extractEntityDescriptor(configPolicy);
            if (onlyEntityDescriptor != onlySecondaryEntityDescriptor) {
                throw new IllegalArgumentException("The entitySelector (" + entitySelectorConfig
                        + ")'s entityClass (" + (onlyEntityDescriptor == null ? null : onlyEntityDescriptor.getEntityClass())
                        + ") and secondaryEntitySelectorConfig (" + secondaryEntitySelectorConfig
                        + ")'s entityClass ("
                        + (onlySecondaryEntityDescriptor == null ? null : onlySecondaryEntityDescriptor.getEntityClass())
                        + ") must be the same entity class.");
            }
        }
        if (onlyEntityDescriptor != null) {
            return null;
        }
        Collection<EntityDescriptor> entityDescriptors = configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        return buildUnfoldedMoveSelectorConfig(entityDescriptors);
    }

    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(
            Collection<EntityDescriptor> entityDescriptors) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(entityDescriptors.size());
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            SwapMoveSelectorConfig childMoveSelectorConfig = new SwapMoveSelectorConfig();
            EntitySelectorConfig childEntitySelectorConfig = new EntitySelectorConfig(entitySelectorConfig);
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            if (secondaryEntitySelectorConfig != null) {
                EntitySelectorConfig childSecondaryEntitySelectorConfig = new EntitySelectorConfig(
                        secondaryEntitySelectorConfig);
                if (childSecondaryEntitySelectorConfig.getMimicSelectorRef() == null) {
                    childSecondaryEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                }
                childMoveSelectorConfig.setSecondaryEntitySelectorConfig(childSecondaryEntitySelectorConfig);
            }
            childMoveSelectorConfig.setVariableNameIncludeList(variableNameIncludeList);
            moveSelectorConfigList.add(childMoveSelectorConfig);
        }

        MoveSelectorConfig unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(this);
        return unfoldedMoveSelectorConfig;
    }

    @Override
    public SwapMoveSelectorConfig inherit(SwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        secondaryEntitySelectorConfig = ConfigUtils.inheritConfig(secondaryEntitySelectorConfig,
                inheritedConfig.getSecondaryEntitySelectorConfig());
        variableNameIncludeList = ConfigUtils.inheritMergeableListProperty(
                variableNameIncludeList, inheritedConfig.getVariableNameIncludeList());
        return this;
    }

    @Override
    public SwapMoveSelectorConfig copyConfig() {
        return new SwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig
                + (secondaryEntitySelectorConfig == null ? "" : ", " + secondaryEntitySelectorConfig) + ")";
    }

}
