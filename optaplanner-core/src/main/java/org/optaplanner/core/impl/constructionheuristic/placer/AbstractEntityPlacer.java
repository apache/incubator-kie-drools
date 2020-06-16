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

package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link EntityPlacer}.
 *
 * @see EntityPlacer
 */
public abstract class AbstractEntityPlacer {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected PhaseLifecycleSupport phaseLifecycleSupport = new PhaseLifecycleSupport();

    public void solvingStarted(SolverScope solverScope) {
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    public void phaseStarted(AbstractPhaseScope phaseScope) {
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    public void stepStarted(AbstractStepScope stepScope) {
        phaseLifecycleSupport.fireStepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        phaseLifecycleSupport.fireStepEnded(stepScope);
    }

    public void phaseEnded(AbstractPhaseScope phaseScope) {
        phaseLifecycleSupport.firePhaseEnded(phaseScope);
    }

    public void solvingEnded(SolverScope solverScope) {
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

}
