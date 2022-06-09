package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class TimeMillisSpentTermination<Solution_> extends AbstractTermination<Solution_> {

    private final long timeMillisSpentLimit;

    public TimeMillisSpentTermination(long timeMillisSpentLimit) {
        this.timeMillisSpentLimit = timeMillisSpentLimit;
        if (timeMillisSpentLimit < 0L) {
            throw new IllegalArgumentException("The timeMillisSpentLimit (" + timeMillisSpentLimit
                    + ") cannot be negative.");
        }
    }

    public long getTimeMillisSpentLimit() {
        return timeMillisSpentLimit;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpentUpToNow();
        return isTerminated(solverTimeMillisSpent);
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpentUpToNow();
        return isTerminated(phaseTimeMillisSpent);
    }

    protected boolean isTerminated(long timeMillisSpent) {
        return timeMillisSpent >= timeMillisSpentLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        long solverTimeMillisSpent = solverScope.calculateTimeMillisSpentUpToNow();
        return calculateTimeGradient(solverTimeMillisSpent);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        long phaseTimeMillisSpent = phaseScope.calculatePhaseTimeMillisSpentUpToNow();
        return calculateTimeGradient(phaseTimeMillisSpent);
    }

    protected double calculateTimeGradient(long timeMillisSpent) {
        double timeGradient = timeMillisSpent / ((double) timeMillisSpentLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public TimeMillisSpentTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return new TimeMillisSpentTermination<>(timeMillisSpentLimit);
    }

    @Override
    public String toString() {
        return "TimeMillisSpent(" + timeMillisSpentLimit + ")";
    }

}
