package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class ChildThreadPlumbingTermination<Solution_> extends AbstractTermination<Solution_> {

    protected boolean terminateChildren = false;

    // ************************************************************************
    // Plumbing worker methods
    // ************************************************************************

    /**
     * This method is thread-safe.
     *
     * @return true if termination hasn't been requested previously
     */
    public synchronized boolean terminateChildren() {
        boolean terminationEarlySuccessful = !terminateChildren;
        terminateChildren = true;
        return terminationEarlySuccessful;
    }

    // ************************************************************************
    // Termination worker methods
    // ************************************************************************

    @Override
    public synchronized boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        // Destroying a thread pool with solver threads will only cause it to interrupt those child solver threads
        if (Thread.currentThread().isInterrupted()) { // Does not clear the interrupted flag
            logger.info("A child solver thread got interrupted, so these child solvers are terminating early.");
            terminateChildren = true;
        }
        return terminateChildren;
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        throw new IllegalStateException(ChildThreadPlumbingTermination.class.getSimpleName()
                + " configured only as solver termination."
                + " It is always bridged to phase termination.");
    }

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        return -1.0; // Not supported
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        throw new IllegalStateException(ChildThreadPlumbingTermination.class.getSimpleName()
                + " configured only as solver termination."
                + " It is always bridged to phase termination.");
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return this;
    }

    @Override
    public String toString() {
        return "ChildThreadPlumbing()";
    }

}
