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

package org.optaplanner.core.api.solver.event;

import java.util.EventListener;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@FunctionalInterface
public interface SolverEventListener<Solution_> extends EventListener {

    /**
     * Called once every time when a better {@link PlanningSolution} is found.
     * The {@link PlanningSolution} is guaranteed to be initialized.
     * Early in the solving process it's usually called more frequently than later on.
     * <p>
     * Called from the solver thread.
     * <b>Should return fast, because it steals time from the {@link Solver}.</b>
     * <p>
     * In real-time planning
     * If {@link Solver#addProblemFactChange(ProblemFactChange)} has been called once or more,
     * all {@link ProblemFactChange}s in the queue will be processed and this method is called only once.
     * In that case, the former best {@link PlanningSolution} is considered stale,
     * so it doesn't matter whether the new {@link Score} is better than that or not.
     *
     * @param event never null
     */
    void bestSolutionChanged(BestSolutionChangedEvent<Solution_> event);

}
