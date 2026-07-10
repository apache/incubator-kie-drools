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

package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.AbstractEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Internal API.
 */
public final class PhaseLifecycleSupport<Solution_> extends AbstractEventSupport<PhaseLifecycleListener<Solution_>> {

    public void fireSolvingStarted(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingStarted(solverScope);
        }
    }

    public void firePhaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.phaseStarted(phaseScope);
        }
    }

    public void fireStepStarted(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.stepStarted(stepScope);
        }
    }

    public void fireStepEnded(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.stepEnded(stepScope);
        }
    }

    public void firePhaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.phaseEnded(phaseScope);
        }
    }

    public void fireSolvingEnded(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingEnded(solverScope);
        }
    }

    public void fireSolvingError(SolverScope<Solution_> solverScope, Exception exception) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingError(solverScope, exception);
        }
    }
}
