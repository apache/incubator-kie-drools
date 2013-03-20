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

package org.optaplanner.core.impl.termination;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class PhaseToSolverTerminationBridge extends AbstractTermination {

    private Termination solverTermination;

    public PhaseToSolverTerminationBridge(Termination solverTermination) {
        this.solverTermination = solverTermination;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        // Do not delegate the event to the solverTermination, because it already gets the event from the DefaultSolver
    }

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                "PhaseToSolverTerminationBridge can only be used for phase termination.");
    }

    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        return solverTermination.isSolverTerminated(phaseScope.getSolverScope());
    }

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                "PhaseToSolverTerminationBridge can only be used for phase termination.");
    }

    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        return solverTermination.calculateSolverTimeGradient(phaseScope.getSolverScope());
    }

}
