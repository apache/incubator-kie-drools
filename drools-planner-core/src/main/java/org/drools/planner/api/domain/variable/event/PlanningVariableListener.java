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

package org.drools.planner.api.domain.variable.event;

import java.util.EventListener;

import org.drools.planner.core.solution.director.SolutionDirector;

public interface PlanningVariableListener extends EventListener {

    /**
     * Called after a variable value has been changed.
     * <p/>
     * Called from the solver thread.
     * Should return fast, as it steals time from the Solver.
     * @param solutionDirector never null
     * @param planningEntity never null
     * @param variableName never null
     * @param oldValue sometimes null
     * @param newValue sometimes null
     */
    void afterChange(SolutionDirector solutionDirector, Object planningEntity, String variableName,
            Object oldValue, Object newValue);

}
