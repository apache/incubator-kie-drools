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

package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Random;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Placer}.
 * @see Placer
 */
public abstract class AbstractPlacer implements Placer {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverPhaseLifecycleSupport solverPhaseLifecycleSupport = new SolverPhaseLifecycleSupport();

    protected Random workingRandom = null;

    public void solvingStarted(DefaultSolverScope solverScope) {
        workingRandom = solverScope.getWorkingRandom();
        solverPhaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        solverPhaseLifecycleSupport.firePhaseStarted(solverPhaseScope);
    }

    public void stepStarted(AbstractStepScope stepScope) {
        solverPhaseLifecycleSupport.fireStepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        solverPhaseLifecycleSupport.fireStepEnded(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        solverPhaseLifecycleSupport.firePhaseEnded(solverPhaseScope);
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        solverPhaseLifecycleSupport.fireSolvingEnded(solverScope);
        workingRandom = null;
    }

}
