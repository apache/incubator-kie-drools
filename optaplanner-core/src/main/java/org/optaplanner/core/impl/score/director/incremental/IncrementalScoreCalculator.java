/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.director.incremental;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

/**
 * Used for incremental java {@link Score} calculation.
 * This is much faster than {@link EasyScoreCalculator} but requires much more code to implement too.
 * <p>
 * Any implementation is naturally stateful.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see IncrementalScoreDirector
 */
public interface IncrementalScoreCalculator<Solution_> {

    /**
     * There are no {@link #beforeEntityAdded(Object)} and {@link #afterEntityAdded(Object)} calls
     * for entities that are already present in the workingSolution.
     * @param workingSolution never null
     */
    void resetWorkingSolution(Solution_ workingSolution);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void beforeEntityAdded(Object entity);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void afterEntityAdded(Object entity);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     * @param variableName never null, either a genuine or shadow {@link PlanningVariable}
     */
    void beforeVariableChanged(Object entity, String variableName);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     * @param variableName never null, either a genuine or shadow {@link PlanningVariable}
     */
    void afterVariableChanged(Object entity, String variableName);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void beforeEntityRemoved(Object entity);
    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void afterEntityRemoved(Object entity);

    /**
     * This method is only called if the {@link Score} cannot be predicted.
     * The {@link Score} can be predicted for example after an undo {@link Move}.
     * @return never null
     */
    Score calculateScore();

}
