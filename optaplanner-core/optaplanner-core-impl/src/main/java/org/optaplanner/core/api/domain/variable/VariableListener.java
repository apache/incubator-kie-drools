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

package org.optaplanner.core.api.domain.variable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * A listener sourced on a basic {@link PlanningVariable}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> @{@link PlanningEntity} on which the source variable is declared
 */
public interface VariableListener<Solution_, Entity_> extends AbstractVariableListener<Solution_, Entity_> {

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity);
}
