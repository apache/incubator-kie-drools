/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.event;

import java.util.EventListener;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.ProblemFactChange;

public interface SolverEventListener extends EventListener {

    /**
     * Called once every time when a better {@link Solution} is found.
     * Early in the solving process it's usually called more frequently than later on.
     * <p/>
     * Called from the solver thread.
     * <b>Should return fast, because it steals time from the {@link Solver}.</b>
     * <p/>
     * If {@link Solver#addProblemFactChange(ProblemFactChange)} has been called once or more,
     * all {@link ProblemFactChange}s will be processed and this method is called once.
     * In that case, the former best {@link Solution} is considered stale,
     * so it doesn't matter whether the new {@link Score} is better than that or not.
     * @param event never null
     */
    void bestSolutionChanged(BestSolutionChangedEvent event);

}
