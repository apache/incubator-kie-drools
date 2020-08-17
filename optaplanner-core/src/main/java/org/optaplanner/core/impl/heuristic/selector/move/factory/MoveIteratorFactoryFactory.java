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
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class MoveIteratorFactoryFactory extends AbstractMoveSelectorFactory<MoveIteratorFactoryConfig> {

    public MoveIteratorFactoryFactory(MoveIteratorFactoryConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType,
            boolean randomSelection) {
        if (moveSelectorConfig.getMoveIteratorFactoryClass() == null) {
            throw new IllegalArgumentException("The moveIteratorFactoryConfig (" + moveSelectorConfig
                    + ") lacks a moveListFactoryClass (" + moveSelectorConfig.getMoveIteratorFactoryClass() + ").");
        }
        MoveIteratorFactory moveIteratorFactory = ConfigUtils.newInstance(moveSelectorConfig,
                "moveIteratorFactoryClass", moveSelectorConfig.getMoveIteratorFactoryClass());
        ConfigUtils.applyCustomProperties(moveIteratorFactory, "moveIteratorFactoryClass",
                moveSelectorConfig.getMoveIteratorFactoryCustomProperties(), "moveIteratorFactoryCustomProperties");
        return new MoveIteratorFactoryToMoveSelectorBridge(moveIteratorFactory, randomSelection);
    }
}
