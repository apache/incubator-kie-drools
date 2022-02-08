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

package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.AbstractEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Internal API.
 */
public class PhaseLifecycleSupport<Solution_> extends AbstractEventSupport<PhaseLifecycleListener<Solution_>> {

    public void fireSolvingStarted(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.solvingStarted(solverScope);
        }
    }

    public void firePhaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.phaseStarted(phaseScope);
        }
    }

    public void fireStepStarted(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.stepStarted(stepScope);
        }
    }

    public void fireStepEnded(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.stepEnded(stepScope);
        }
    }

    public void firePhaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.phaseEnded(phaseScope);
        }
    }

    public void fireSolvingEnded(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.solvingEnded(solverScope);
        }
    }

}
