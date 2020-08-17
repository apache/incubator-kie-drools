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

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.FixedSelectorProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class UnionMoveSelectorFactory extends AbstractCompositeMoveSelectorFactory<UnionMoveSelectorConfig> {

    public UnionMoveSelectorFactory(UnionMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        List<MoveSelector> moveSelectorList = buildInnerMoveSelectors(moveSelectorConfig.getMoveSelectorConfigList(),
                configPolicy, minimumCacheType, randomSelection);

        SelectionProbabilityWeightFactory selectorProbabilityWeightFactory;
        if (moveSelectorConfig.getSelectorProbabilityWeightFactoryClass() != null) {
            if (!randomSelection) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                        + ") with selectorProbabilityWeightFactoryClass ("
                        + moveSelectorConfig.getSelectorProbabilityWeightFactoryClass()
                        + ") has non-random randomSelection (" + randomSelection + ").");
            }
            selectorProbabilityWeightFactory = ConfigUtils.newInstance(moveSelectorConfig,
                    "selectorProbabilityWeightFactoryClass", moveSelectorConfig.getSelectorProbabilityWeightFactoryClass());
        } else if (randomSelection) {
            Map<MoveSelector, Double> fixedProbabilityWeightMap = new HashMap<>(
                    moveSelectorConfig.getMoveSelectorConfigList().size());
            for (int i = 0; i < moveSelectorConfig.getMoveSelectorConfigList().size(); i++) {
                MoveSelectorConfig innerMoveSelectorConfig = moveSelectorConfig.getMoveSelectorConfigList().get(i);
                MoveSelector moveSelector = moveSelectorList.get(i);
                Double fixedProbabilityWeight = innerMoveSelectorConfig.getFixedProbabilityWeight();
                if (fixedProbabilityWeight == null) {
                    // Default to equal probability for each move type => unequal probability for each move instance
                    fixedProbabilityWeight = 1.0;
                }
                fixedProbabilityWeightMap.put(moveSelector, fixedProbabilityWeight);
            }
            selectorProbabilityWeightFactory = new FixedSelectorProbabilityWeightFactory(fixedProbabilityWeightMap);
        } else {
            selectorProbabilityWeightFactory = null;
        }
        return new UnionMoveSelector(moveSelectorList, randomSelection,
                selectorProbabilityWeightFactory);
    }
}
