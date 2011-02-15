/**
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

package org.drools.planner.core.localsearch.termination;

import org.drools.planner.core.localsearch.LocalSearchSolver;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Termination}.
 * @author Geoffrey De Smet
 */
public abstract class AbstractTermination implements Termination {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected LocalSearchSolver localSearchSolver;

    public void setLocalSearchSolver(LocalSearchSolver localSearchSolver) {
        this.localSearchSolver = localSearchSolver;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

}
