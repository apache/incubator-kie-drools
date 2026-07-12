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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSwapMoveSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SubListSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubListSwapMoveSelectorConfig> {

    public SubListSwapMoveSelectorFactory(SubListSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (!randomSelection) {
            throw new IllegalArgumentException("The subListSwapMoveSelector (" + config
                    + ") only supports random selection order.");
        }

        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);

        EntitySelector<Solution_> entitySelector = EntitySelectorFactory
                .<Solution_> create(new EntitySelectorConfig())
                .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);

        SubListSelectorConfig subListSelectorConfig =
                Objects.requireNonNullElseGet(config.getSubListSelectorConfig(), SubListSelectorConfig::new);
        SubListSelectorConfig secondarySubListSelectorConfig =
                Objects.requireNonNullElse(config.getSecondarySubListSelectorConfig(), subListSelectorConfig);

        // minimum -> subListSelector
        SubListConfigUtil.transferDeprecatedMinimumSubListSize(
                config,
                SubListSwapMoveSelectorConfig::getMinimumSubListSize,
                "subListSelector", subListSelectorConfig);
        // maximum -> subListSelector
        SubListConfigUtil.transferDeprecatedMaximumSubListSize(
                config,
                SubListSwapMoveSelectorConfig::getMaximumSubListSize,
                "subListSelector", subListSelectorConfig);
        if (subListSelectorConfig != secondarySubListSelectorConfig) {
            // minimum -> secondarySubListSelector
            SubListConfigUtil.transferDeprecatedMinimumSubListSize(
                    config,
                    SubListSwapMoveSelectorConfig::getMinimumSubListSize,
                    "secondarySubListSelector", secondarySubListSelectorConfig);
            // maximum -> secondarySubListSelector
            SubListConfigUtil.transferDeprecatedMaximumSubListSize(
                    config,
                    SubListSwapMoveSelectorConfig::getMaximumSubListSize,
                    "secondarySubListSelector", secondarySubListSelectorConfig);
        }
        SubListSelector<Solution_> leftSubListSelector = SubListSelectorFactory
                .<Solution_> create(subListSelectorConfig)
                .buildSubListSelector(configPolicy, entitySelector, minimumCacheType, selectionOrder);
        SubListSelector<Solution_> rightSubListSelector = SubListSelectorFactory
                .<Solution_> create(secondarySubListSelectorConfig)
                .buildSubListSelector(configPolicy, entitySelector, minimumCacheType, selectionOrder);

        boolean selectReversingMoveToo = Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true);

        return new RandomSubListSwapMoveSelector<>(leftSubListSelector, rightSubListSelector, selectReversingMoveToo);
    }
}
