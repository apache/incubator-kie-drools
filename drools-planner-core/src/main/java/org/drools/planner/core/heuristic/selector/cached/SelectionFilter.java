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

package org.drools.planner.core.heuristic.selector.cached;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.core.heuristic.selector.Selector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.solution.Solution;

/**
 * Decide on keeping or discarding a selection
 * (which is a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}).
 * <p/>
 * A filtered selection is considered as not selected, it does not count as an unaccepted selection.
 */
public interface SelectionFilter<S extends Solution, T> {

    /**
     * @param solution never null, the {@link Solution} to which the selection belongs or applies to
     * @param selection never null, a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}
     * to create the probabilityWeight for
     * @return true if the selection is accepted, false if the selection should be discarded
     */
    boolean accept(S solution, T selection);

}
