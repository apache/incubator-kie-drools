package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class StepCountTermination<Solution_> extends AbstractTermination<Solution_> {

    private final int stepCountLimit;

    public StepCountTermination(int stepCountLimit) {
        this.stepCountLimit = stepCountLimit;
        if (stepCountLimit < 0) {
            throw new IllegalArgumentException("The stepCountLimit (" + stepCountLimit
                    + ") cannot be negative.");
        }
    }

    public int getStepCountLimit() {
        return stepCountLimit;
    }

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
        int nextStepIndex = phaseScope.getNextStepIndex();
        return nextStepIndex >= stepCountLimit;
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
        int nextStepIndex = phaseScope.getNextStepIndex();
        double timeGradient = nextStepIndex / ((double) stepCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public StepCountTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return new StepCountTermination<>(stepCountLimit);
    }

    @Override
    public String toString() {
        return "StepCount(" + stepCountLimit + ")";
    }

}
