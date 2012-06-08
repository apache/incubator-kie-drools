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

package org.drools.planner.core.heuristic.selector.value.cached;

import org.drools.planner.core.heuristic.selector.entity.cached.PlanningEntitySelectionProbabilityWeightFactory;
import org.drools.planner.core.solution.Solution;

/**
 * Create a selectionProbabilityWeight for a PlanningValue.
 * A selectionProbabilityWeight represents the random chance that a PlanningValue will be selected.
 * Some use cases benefit from focusing moves more actively on some planning values.
 * @see PlanningEntitySelectionProbabilityWeightFactory
 */
public interface PlanningValueSelectionProbabilityWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningValue belongs
     * @param planningValue never null, the planningValue to create the selectionProbabilityWeight for
     * @return 0.0 <= returnValue < {@link Double#POSITIVE_INFINITY}
     */
    double createSelectionProbabilityWeight(Solution solution, Object planningValue);

}
