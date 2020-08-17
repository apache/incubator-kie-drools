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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SwapMoveSelectorFactory extends AbstractMoveSelectorFactory<SwapMoveSelectorConfig> {

    public SwapMoveSelectorFactory(SwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ =
                moveSelectorConfig.getEntitySelectorConfig() == null ? new EntitySelectorConfig()
                        : moveSelectorConfig.getEntitySelectorConfig();
        EntitySelector leftEntitySelector = entitySelectorConfig_.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntitySelectorConfig rightEntitySelectorConfig = defaultIfNull(moveSelectorConfig.getSecondaryEntitySelectorConfig(),
                entitySelectorConfig_);
        EntitySelector rightEntitySelector = rightEntitySelectorConfig.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        List<GenuineVariableDescriptor> variableDescriptorList = leftEntitySelector.getEntityDescriptor()
                .deduceVariableDescriptorList(moveSelectorConfig.getVariableNameIncludeList());
        return new SwapMoveSelector(leftEntitySelector, rightEntitySelector, variableDescriptorList,
                randomSelection);
    }

    @Override
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        EntityDescriptor onlyEntityDescriptor = moveSelectorConfig.getEntitySelectorConfig() == null ? null
                : moveSelectorConfig.getEntitySelectorConfig().extractEntityDescriptor(configPolicy);
        if (moveSelectorConfig.getSecondaryEntitySelectorConfig() != null) {
            EntityDescriptor onlySecondaryEntityDescriptor = moveSelectorConfig.getSecondaryEntitySelectorConfig()
                    .extractEntityDescriptor(configPolicy);
            if (onlyEntityDescriptor != onlySecondaryEntityDescriptor) {
                throw new IllegalArgumentException("The entitySelector (" + moveSelectorConfig.getEntitySelectorConfig()
                        + ")'s entityClass (" + (onlyEntityDescriptor == null ? null : onlyEntityDescriptor.getEntityClass())
                        + ") and secondaryEntitySelectorConfig (" + moveSelectorConfig.getSecondaryEntitySelectorConfig()
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

    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(Collection<EntityDescriptor> entityDescriptors) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(entityDescriptors.size());
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            SwapMoveSelectorConfig childMoveSelectorConfig = new SwapMoveSelectorConfig();
            EntitySelectorConfig childEntitySelectorConfig =
                    new EntitySelectorConfig(moveSelectorConfig.getEntitySelectorConfig());
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            if (moveSelectorConfig.getSecondaryEntitySelectorConfig() != null) {
                EntitySelectorConfig childSecondaryEntitySelectorConfig = new EntitySelectorConfig(
                        moveSelectorConfig.getSecondaryEntitySelectorConfig());
                if (childSecondaryEntitySelectorConfig.getMimicSelectorRef() == null) {
                    childSecondaryEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                }
                childMoveSelectorConfig.setSecondaryEntitySelectorConfig(childSecondaryEntitySelectorConfig);
            }
            childMoveSelectorConfig.setVariableNameIncludeList(moveSelectorConfig.getVariableNameIncludeList());
            moveSelectorConfigList.add(childMoveSelectorConfig);
        }

        MoveSelectorConfig unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(moveSelectorConfig);
        return unfoldedMoveSelectorConfig;
    }
}
