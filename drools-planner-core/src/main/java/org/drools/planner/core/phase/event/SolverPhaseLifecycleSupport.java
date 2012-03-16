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

package org.drools.planner.core.phase.event;

import java.util.Iterator;

import org.drools.event.AbstractEventSupport;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;

/**
 * Internal API.
 */
public class SolverPhaseLifecycleSupport extends AbstractEventSupport<SolverPhaseLifecycleListener> {

    public void firePhaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().phaseStarted(solverPhaseScope);
        }
    }

    public void fireBeforeDeciding(AbstractStepScope stepScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().beforeDeciding(stepScope);
        }
    }

    public void fireStepTaken(AbstractStepScope stepScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().stepTaken(stepScope);
        }
    }

    public void firePhaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        final Iterator<SolverPhaseLifecycleListener> iter = getEventListenersIterator();
        while (iter.hasNext()) {
            iter.next().phaseEnded(solverPhaseScope);
        }
    }

}
