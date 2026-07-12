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

package org.optaplanner.core.impl.heuristic.selector.move.factory;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class MoveIteratorFactoryFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, MoveIteratorFactoryConfig> {

    public MoveIteratorFactoryFactory(MoveIteratorFactoryConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (config.getMoveIteratorFactoryClass() == null) {
            throw new IllegalArgumentException("The moveIteratorFactoryConfig (" + config
                    + ") lacks a moveListFactoryClass (" + config.getMoveIteratorFactoryClass() + ").");
        }
        MoveIteratorFactory moveIteratorFactory = ConfigUtils.newInstance(config,
                "moveIteratorFactoryClass", config.getMoveIteratorFactoryClass());
        ConfigUtils.applyCustomProperties(moveIteratorFactory, "moveIteratorFactoryClass",
                config.getMoveIteratorFactoryCustomProperties(), "moveIteratorFactoryCustomProperties");
        return new MoveIteratorFactoryToMoveSelectorBridge<>(moveIteratorFactory, randomSelection);
    }
}
