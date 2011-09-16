/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.api.domain.entity;

import org.drools.planner.core.solution.Solution;

/**
 * Creates a difficultyWeight for a PlanningEntity.
 * A difficultyWeight estimates how hard is to plan a certain PlanningEntity.
 * Some algorithms benefit from planning on more difficult planning entities first or from focusing on them.
 */
public interface PlanningEntityDifficultyWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningEntity never null, the planningEntity to create the difficultyWeight for
     * @return never null
     */
    Comparable createDifficultyWeight(Solution solution, Object planningEntity);

}
