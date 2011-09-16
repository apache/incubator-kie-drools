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

package org.drools.planner.api.domain.variable;

import org.drools.planner.core.solution.Solution;

/**
 * Creates a strengthWeight for a planning variable value.
 * A strengthWeight estimates how strong a planning value is.
 * Some algorithms benefit from planning on weaker planning values first or from focusing on them.
 */
public interface PlanningValueStrengthWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningValue never null, the planning value to create the strengthWeight for
     * @return never null
     */
    Comparable createStrengthWeight(Solution solution, Object planningValue);

}
