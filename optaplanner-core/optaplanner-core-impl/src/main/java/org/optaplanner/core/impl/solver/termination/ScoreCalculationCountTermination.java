package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class ScoreCalculationCountTermination<Solution_> extends AbstractTermination<Solution_> {

    private final long scoreCalculationCountLimit;

    public ScoreCalculationCountTermination(long scoreCalculationCountLimit) {
        this.scoreCalculationCountLimit = scoreCalculationCountLimit;
        if (scoreCalculationCountLimit < 0L) {
            throw new IllegalArgumentException("The scoreCalculationCountLimit (" + scoreCalculationCountLimit
                    + ") cannot be negative.");
        }
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        return isTerminated(solverScope.getScoreDirector());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        return isTerminated(phaseScope.getScoreDirector());
    }

    protected boolean isTerminated(InnerScoreDirector<Solution_, ?> scoreDirector) {
        long scoreCalculationCount = scoreDirector.getCalculationCount();
        return scoreCalculationCount >= scoreCalculationCountLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        return calculateTimeGradient(solverScope.getScoreDirector());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        return calculateTimeGradient(phaseScope.getScoreDirector());
    }

    protected double calculateTimeGradient(InnerScoreDirector<Solution_, ?> scoreDirector) {
        long scoreCalculationCount = scoreDirector.getCalculationCount();
        double timeGradient = scoreCalculationCount / ((double) scoreCalculationCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public ScoreCalculationCountTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        if (childThreadType == ChildThreadType.PART_THREAD) {
            // The ScoreDirector.calculationCount of partitions is maxed, not summed.
            return new ScoreCalculationCountTermination<>(scoreCalculationCountLimit);
        } else {
            throw new IllegalStateException("The childThreadType (" + childThreadType + ") is not implemented.");
        }
    }

    @Override
    public String toString() {
        return "ScoreCalculationCount(" + scoreCalculationCountLimit + ")";
    }

}
