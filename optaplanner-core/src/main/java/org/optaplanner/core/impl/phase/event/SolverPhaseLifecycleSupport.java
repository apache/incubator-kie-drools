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

package org.optaplanner.core.impl.phase.event;

import java.util.Iterator;

import org.drools.core.event.AbstractEventSupport;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Internal API.
 */
public class SolverPhaseLifecycleSupport extends AbstractEventSupport<SolverPhaseLifecycleListener> {

    public void fireSolvingStarted(DefaultSolverScope solverScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().solvingStarted(solverScope);
        }
    }

    public void firePhaseStarted(AbstractSolverPhaseScope phaseScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().phaseStarted(phaseScope);
        }
    }

    public void fireStepStarted(AbstractStepScope stepScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().stepStarted(stepScope);
        }
    }

    public void fireStepEnded(AbstractStepScope stepScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().stepEnded(stepScope);
        }
    }

    public void firePhaseEnded(AbstractSolverPhaseScope phaseScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().phaseEnded(phaseScope);
        }
    }

    public void fireSolvingEnded(DefaultSolverScope solverScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().solvingEnded(solverScope);
        }
    }

}
