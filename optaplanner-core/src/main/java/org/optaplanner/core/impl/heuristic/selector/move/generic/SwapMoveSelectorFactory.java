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
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SwapMoveSelectorConfig> {

    public SwapMoveSelectorFactory(SwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ =
                config.getEntitySelectorConfig() == null ? new EntitySelectorConfig() : config.getEntitySelectorConfig();
        EntitySelector<Solution_> leftEntitySelector = EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                .buildEntitySelector(configPolicy, minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntitySelectorConfig rightEntitySelectorConfig = defaultIfNull(config.getSecondaryEntitySelectorConfig(),
                entitySelectorConfig_);
        EntitySelector<Solution_> rightEntitySelector =
                EntitySelectorFactory.<Solution_> create(rightEntitySelectorConfig)
                        .buildEntitySelector(configPolicy, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                deduceVariableDescriptorList(leftEntitySelector.getEntityDescriptor(), config.getVariableNameIncludeList());
        return new SwapMoveSelector<>(leftEntitySelector, rightEntitySelector, variableDescriptorList,
                randomSelection);
    }

    @Override
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(
            HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> onlyEntityDescriptor = config.getEntitySelectorConfig() == null ? null
                : EntitySelectorFactory.<Solution_> create(config.getEntitySelectorConfig())
                        .extractEntityDescriptor(configPolicy);
        if (config.getSecondaryEntitySelectorConfig() != null) {
            EntityDescriptor<Solution_> onlySecondaryEntityDescriptor =
                    EntitySelectorFactory.<Solution_> create(config.getSecondaryEntitySelectorConfig())
                            .extractEntityDescriptor(configPolicy);
            if (onlyEntityDescriptor != onlySecondaryEntityDescriptor) {
                throw new IllegalArgumentException("The entitySelector (" + config.getEntitySelectorConfig()
                        + ")'s entityClass (" + (onlyEntityDescriptor == null ? null : onlyEntityDescriptor.getEntityClass())
                        + ") and secondaryEntitySelectorConfig (" + config.getSecondaryEntitySelectorConfig()
                        + ")'s entityClass ("
                        + (onlySecondaryEntityDescriptor == null ? null : onlySecondaryEntityDescriptor.getEntityClass())
                        + ") must be the same entity class.");
            }
        }
        if (onlyEntityDescriptor != null) {
            return null;
        }
        Collection<EntityDescriptor<Solution_>> entityDescriptors =
                configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        return buildUnfoldedMoveSelectorConfig(entityDescriptors);
    }

    protected MoveSelectorConfig<?>
            buildUnfoldedMoveSelectorConfig(Collection<EntityDescriptor<Solution_>> entityDescriptors) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(entityDescriptors.size());
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptors) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            SwapMoveSelectorConfig childMoveSelectorConfig = new SwapMoveSelectorConfig();
            EntitySelectorConfig childEntitySelectorConfig = new EntitySelectorConfig(config.getEntitySelectorConfig());
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            if (config.getSecondaryEntitySelectorConfig() != null) {
                EntitySelectorConfig childSecondaryEntitySelectorConfig =
                        new EntitySelectorConfig(config.getSecondaryEntitySelectorConfig());
                if (childSecondaryEntitySelectorConfig.getMimicSelectorRef() == null) {
                    childSecondaryEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                }
                childMoveSelectorConfig.setSecondaryEntitySelectorConfig(childSecondaryEntitySelectorConfig);
            }
            childMoveSelectorConfig.setVariableNameIncludeList(config.getVariableNameIncludeList());
            moveSelectorConfigList.add(childMoveSelectorConfig);
        }

        MoveSelectorConfig unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(config);
        return unfoldedMoveSelectorConfig;
    }
}
