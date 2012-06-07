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

package org.drools.planner.core.heuristic.selector.entity.cached;

import org.drools.planner.core.solution.Solution;

/**
 * Create a selectionProbabilityWeight for a PlanningEntity.
 * A selectionProbabilityWeight represents the random chance that a PlanningEntity will be selected.
 * Some use cases benefit from focusing moves more actively on some planning entities..
 */
public interface PlanningEntitySelectionProbabilityWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningEntity never null, the planningEntity to create the selectionProbabilityWeight for
     * @return 0.0 <= returnValue < {@link Double#POSITIVE_INFINITY}
     */
    double createSelectionProbabilityWeight(Solution solution, Object planningEntity);

}
