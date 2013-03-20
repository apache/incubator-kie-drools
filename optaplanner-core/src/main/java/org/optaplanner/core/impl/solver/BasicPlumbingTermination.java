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

package org.optaplanner.core.impl.solver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.termination.AbstractTermination;

public class BasicPlumbingTermination extends AbstractTermination {

    protected AtomicBoolean terminatedEarly = new AtomicBoolean(false);
    protected BlockingQueue<ProblemFactChange> problemFactChangeQueue = new LinkedBlockingQueue<ProblemFactChange>();

    // ************************************************************************
    // Plumbing worker methods
    // ************************************************************************
    
    public void resetTerminateEarly() {
        terminatedEarly.set(false);
    }

    public boolean terminateEarly() {
        boolean terminationEarlySuccessful = !terminatedEarly.getAndSet(true);
        if (terminationEarlySuccessful) {
            logger.info("Terminating solver early.");
        }
        return terminationEarlySuccessful;
    }

    public boolean isTerminateEarly() {
        return terminatedEarly.get();
    }

    public boolean addProblemFactChange(ProblemFactChange problemFactChange) {
        return problemFactChangeQueue.add(problemFactChange);
    }

    public BlockingQueue<ProblemFactChange> getProblemFactChangeQueue() {
        return problemFactChangeQueue;
    }

    // ************************************************************************
    // Termination worker methods
    // ************************************************************************

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return terminatedEarly.get() || !problemFactChangeQueue.isEmpty();
    }

    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        throw new UnsupportedOperationException("BasicPlumbingTermination can only be used for solver termination.");
    }

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        return -1.0; // Not supported
    }

    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        throw new UnsupportedOperationException("BasicPlumbingTermination can only be used for solver termination.");
    }

}
