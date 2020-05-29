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

package org.optaplanner.core.api.domain.entity;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * Decides on accepting or discarding a {@link PlanningEntity}.
 * A pinned {@link PlanningEntity} is never used in a {@link Move}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> the entity type, the class with the {@link PlanningEntity} annotation
 */
public interface PinningFilter<Solution_, Entity_> {

    /**
     * @param solution working solution to which the entity belongs
     * @param entity never null, a {@link PlanningEntity}
     * @return true if the entity it is pinned, false if the entity is movable.
     */
    boolean accept(Solution_ solution, Entity_ entity);

}
