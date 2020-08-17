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

package org.optaplanner.core.impl.heuristic.selector.move.factory;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class MoveListFactoryFactory extends AbstractMoveSelectorFactory<MoveListFactoryConfig> {

    public MoveListFactoryFactory(MoveListFactoryConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (moveSelectorConfig.getMoveListFactoryClass() == null) {
            throw new IllegalArgumentException("The moveListFactoryConfig (" + moveSelectorConfig
                    + ") lacks a moveListFactoryClass (" + moveSelectorConfig.getMoveListFactoryClass() + ").");
        }
        MoveListFactory moveListFactory = ConfigUtils.newInstance(moveSelectorConfig,
                "moveListFactoryClass", moveSelectorConfig.getMoveListFactoryClass());
        ConfigUtils.applyCustomProperties(moveListFactory, "moveListFactoryClass",
                moveSelectorConfig.getMoveListFactoryCustomProperties(), "moveListFactoryCustomProperties");
        // MoveListFactoryToMoveSelectorBridge caches by design, so it uses the minimumCacheType
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) < 0) {
            // cacheType upgrades to SelectionCacheType.STEP (without shuffling) because JIT is not supported
            minimumCacheType = SelectionCacheType.STEP;
        }
        return new MoveListFactoryToMoveSelectorBridge(moveListFactory, minimumCacheType, randomSelection);
    }

    @Override
    protected boolean isBaseInherentlyCached() {
        return true;
    }
}
