/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.common.decorator;

import java.util.Map;

import org.drools.planner.core.heuristic.selector.Selector;
import org.drools.planner.core.solution.Solution;

public class FixedSelectorProbabilityWeightFactory implements SelectionProbabilityWeightFactory<Solution, Selector> {

    private final Map<Selector, Double> fixedProbabilityWeightMap;

    public FixedSelectorProbabilityWeightFactory(Map<Selector, Double> fixedProbabilityWeightMap) {
        this.fixedProbabilityWeightMap = fixedProbabilityWeightMap;
    }

    public double createProbabilityWeight(Solution solution, Selector selector) {
        return fixedProbabilityWeightMap.get(selector);
    }

}
