/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PillarSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, PillarSwapMoveSelectorConfig> {

    public PillarSwapMoveSelectorFactory(PillarSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        PillarSelectorConfig leftPillarSelectorConfig =
                Objects.requireNonNullElseGet(config.getPillarSelectorConfig(), PillarSelectorConfig::new);
        PillarSelectorConfig rightPillarSelectorConfig =
                Objects.requireNonNullElse(config.getSecondaryPillarSelectorConfig(), leftPillarSelectorConfig);
        PillarSelector<Solution_> leftPillarSelector =
                buildPillarSelector(leftPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);
        PillarSelector<Solution_> rightPillarSelector =
                buildPillarSelector(rightPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);

        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                deduceVariableDescriptorList(leftPillarSelector.getEntityDescriptor(), config.getVariableNameIncludeList());
        return new PillarSwapMoveSelector<>(leftPillarSelector, rightPillarSelector, variableDescriptorList, randomSelection);
    }

    private PillarSelector<Solution_> buildPillarSelector(PillarSelectorConfig pillarSelectorConfig,
            HeuristicConfigPolicy<Solution_> configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
        return PillarSelectorFactory.<Solution_> create(pillarSelectorConfig)
                .buildPillarSelector(configPolicy, config.getSubPillarType(),
                        (Class<? extends Comparator<Object>>) config.getSubPillarSequenceComparatorClass(), minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection),
                        config.getVariableNameIncludeList());
    }

}
