/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class UnionMoveSelectorFactory<Solution_>
        extends AbstractCompositeMoveSelectorFactory<Solution_, UnionMoveSelectorConfig> {

    public UnionMoveSelectorFactory(UnionMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        List<MoveSelector<Solution_>> moveSelectorList = buildInnerMoveSelectors(config.getMoveSelectorList(),
                configPolicy, minimumCacheType, randomSelection);

        SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory;
        if (config.getSelectorProbabilityWeightFactoryClass() != null) {
            if (!randomSelection) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + config
                        + ") with selectorProbabilityWeightFactoryClass ("
                        + config.getSelectorProbabilityWeightFactoryClass()
                        + ") has non-random randomSelection (" + randomSelection + ").");
            }
            selectorProbabilityWeightFactory = ConfigUtils.newInstance(config,
                    "selectorProbabilityWeightFactoryClass", config.getSelectorProbabilityWeightFactoryClass());
        } else if (randomSelection) {
            Map<MoveSelector<Solution_>, Double> fixedProbabilityWeightMap =
                    new HashMap<>(config.getMoveSelectorList().size());
            for (int i = 0; i < config.getMoveSelectorList().size(); i++) {
                MoveSelectorConfig<?> innerMoveSelectorConfig = config.getMoveSelectorList().get(i);
                MoveSelector<Solution_> moveSelector = moveSelectorList.get(i);
                Double fixedProbabilityWeight = innerMoveSelectorConfig.getFixedProbabilityWeight();
                if (fixedProbabilityWeight != null) {
                    fixedProbabilityWeightMap.put(moveSelector, fixedProbabilityWeight);
                }
            }
            if (fixedProbabilityWeightMap.isEmpty()) { // Will end up using UniformRandomUnionMoveIterator.
                selectorProbabilityWeightFactory = null;
            } else { // Will end up using BiasedRandomUnionMoveIterator.
                selectorProbabilityWeightFactory = new FixedSelectorProbabilityWeightFactory<>(fixedProbabilityWeightMap);
            }
        } else {
            selectorProbabilityWeightFactory = null;
        }
        return new UnionMoveSelector<>(moveSelectorList, randomSelection, selectorProbabilityWeightFactory);
    }
}
