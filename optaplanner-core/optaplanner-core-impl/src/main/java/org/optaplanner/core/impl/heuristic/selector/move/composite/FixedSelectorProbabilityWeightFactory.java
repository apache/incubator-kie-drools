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

import java.util.Map;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.Selector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;

final class FixedSelectorProbabilityWeightFactory<Solution_, Selector_ extends Selector>
        implements SelectionProbabilityWeightFactory<Solution_, Selector_> {

    private final Map<Selector_, Double> fixedProbabilityWeightMap;

    public FixedSelectorProbabilityWeightFactory(Map<Selector_, Double> fixedProbabilityWeightMap) {
        this.fixedProbabilityWeightMap = fixedProbabilityWeightMap;
    }

    @Override
    public double createProbabilityWeight(ScoreDirector<Solution_> scoreDirector, Selector_ selector) {
        return fixedProbabilityWeightMap.getOrDefault(selector, 1.0);
    }

}
