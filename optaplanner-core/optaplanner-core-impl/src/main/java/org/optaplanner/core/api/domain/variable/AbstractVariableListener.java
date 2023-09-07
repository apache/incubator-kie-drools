/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.api.domain.variable;

import java.io.Closeable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Common ancestor for specialized planning variable listeners.
 * <p>
 * <strong>Do not implement this interface directly.</strong>
 * Implement either {@link VariableListener} or {@link ListVariableListener}.
 *
 * @see VariableListener
 * @see ListVariableListener
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> @{@link PlanningEntity} on which the source variable is declared
 */
public interface AbstractVariableListener<Solution_, Entity_> extends Closeable {

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * Called when the entire working solution changes. In this event, the other before..()/after...() methods will not
     * be called.
     * At this point, implementations should clear state, if any.
     *
     * @param scoreDirector never null
     */
    default void resetWorkingSolution(ScoreDirector<Solution_> scoreDirector) {
        // No need to do anything for stateless implementations.
    }

    /**
     * Called before this {@link AbstractVariableListener} is thrown away and not used anymore.
     */
    @Override
    default void close() {
        // No need to do anything for stateless implementations.
    }
}
