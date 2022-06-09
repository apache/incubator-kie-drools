package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class UnimprovedStepCountTermination<Solution_> extends AbstractTermination<Solution_> {

    private final int unimprovedStepCountLimit;

    public UnimprovedStepCountTermination(int unimprovedStepCountLimit) {
        this.unimprovedStepCountLimit = unimprovedStepCountLimit;
        if (unimprovedStepCountLimit < 0) {
            throw new IllegalArgumentException("The unimprovedStepCountLimit (" + unimprovedStepCountLimit
                    + ") cannot be negative.");
        }
    }

    public int getUnimprovedStepCountLimit() {
        return unimprovedStepCountLimit;
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
        int unimprovedStepCount = calculateUnimprovedStepCount(phaseScope);
        return unimprovedStepCount >= unimprovedStepCountLimit;
    }

    protected int calculateUnimprovedStepCount(AbstractPhaseScope<Solution_> phaseScope) {
        int bestStepIndex = phaseScope.getBestSolutionStepIndex();
        int lastStepIndex = phaseScope.getLastCompletedStepScope().getStepIndex();
        return lastStepIndex - bestStepIndex;
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
        int unimprovedStepCount = calculateUnimprovedStepCount(phaseScope);
        double timeGradient = unimprovedStepCount / ((double) unimprovedStepCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public UnimprovedStepCountTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return new UnimprovedStepCountTermination<>(unimprovedStepCountLimit);
    }

    @Override
    public String toString() {
        return "UnimprovedStepCount(" + unimprovedStepCountLimit + ")";
    }

}
