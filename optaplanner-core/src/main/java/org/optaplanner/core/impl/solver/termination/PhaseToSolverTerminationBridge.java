/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class PhaseToSolverTerminationBridge extends AbstractTermination {

    private final Termination solverTermination;

    public PhaseToSolverTerminationBridge(Termination solverTermination) {
        this.solverTermination = solverTermination;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        solverTermination.phaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        solverTermination.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        solverTermination.stepEnded(stepScope);
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        solverTermination.phaseStarted(phaseScope);
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return solverTermination.isSolverTerminated(phaseScope.getSolverScope());
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return solverTermination.calculateSolverTimeGradient(phaseScope.getSolverScope());
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination createChildThreadTermination(
            DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        if (childThreadType == ChildThreadType.PART_THREAD) {
            // Remove of the bridge (which is nested if there's a phase termination), PhaseConfig will add it again
            return solverTermination.createChildThreadTermination(solverScope, childThreadType);
        } else {
            throw new IllegalStateException("The childThreadType (" + childThreadType + ") is not implemented.");
        }
    }

    @Override
    public String toString() {
        return "Bridge(" + solverTermination + ")";
    }

}
