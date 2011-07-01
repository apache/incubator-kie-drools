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

package org.drools.planner.core.localsearch.termination;

import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.solver.AbstractStepScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Termination}.
 */
public abstract class AbstractTermination implements Termination {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void beforeDeciding(AbstractStepScope stepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void stepDecided(AbstractStepScope stepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void stepTaken(AbstractStepScope stepScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        // Hook which can be optionally overwritten by subclasses.
    }

}
