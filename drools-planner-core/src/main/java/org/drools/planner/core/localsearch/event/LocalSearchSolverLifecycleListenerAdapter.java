/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.localsearch.event;

import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;

public abstract class LocalSearchSolverLifecycleListenerAdapter implements LocalSearchSolverLifecycleListener {

    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        // Hook method
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        // Hook method
    }

    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        // Hook method
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        // Hook method
    }

    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        // Hook method
    }

}
