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

import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PillarSwapMoveSelectorFactory extends AbstractMoveSelectorFactory<PillarSwapMoveSelectorConfig> {

    public PillarSwapMoveSelectorFactory(PillarSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        PillarSelectorConfig leftPillarSelectorConfig =
                defaultIfNull(moveSelectorConfig.getPillarSelectorConfig(), new PillarSelectorConfig());
        PillarSelector leftPillarSelector =
                buildPillarSelector(leftPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);
        PillarSelectorConfig rightPillarSelectorConfig =
                defaultIfNull(moveSelectorConfig.getSecondaryPillarSelectorConfig(), leftPillarSelectorConfig);
        PillarSelector rightPillarSelector =
                buildPillarSelector(rightPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);

        List<GenuineVariableDescriptor> variableDescriptorList = leftPillarSelector.getEntityDescriptor()
                .deduceVariableDescriptorList(moveSelectorConfig.getVariableNameIncludeList());
        return new PillarSwapMoveSelector(leftPillarSelector, rightPillarSelector, variableDescriptorList,
                randomSelection);
    }

    private PillarSelector buildPillarSelector(PillarSelectorConfig pillarSelectorConfig,
            HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
        return pillarSelectorConfig.buildPillarSelector(configPolicy, moveSelectorConfig.getSubPillarType(),
                moveSelectorConfig.getSubPillarSequenceComparatorClass(), minimumCacheType,
                SelectionOrder.fromRandomSelectionBoolean(randomSelection), moveSelectorConfig.getVariableNameIncludeList());
    }

}
