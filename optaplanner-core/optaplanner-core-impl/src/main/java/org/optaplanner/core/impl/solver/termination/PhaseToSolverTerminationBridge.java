package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class PhaseToSolverTerminationBridge<Solution_> extends AbstractTermination<Solution_> {

    private final Termination<Solution_> solverTermination;

    public PhaseToSolverTerminationBridge(Termination<Solution_> solverTermination) {
        this.solverTermination = solverTermination;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    // Do not propagate any of the lifecycle events up to the solverTermination,
    // because it already gets the solver events from the DefaultSolver
    // and the phase/step events - if ever needed - should also come through the DefaultSolver

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        return solverTermination.isSolverTerminated(phaseScope.getSolverScope());
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        return solverTermination.calculateSolverTimeGradient(phaseScope.getSolverScope());
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
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
