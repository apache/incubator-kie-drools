package org.optaplanner.core.impl.solver.termination;

import java.util.List;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class AndCompositeTermination<Solution_> extends AbstractCompositeTermination<Solution_> {

    public AndCompositeTermination(List<Termination<Solution_>> terminationList) {
        super(terminationList);
    }

    public AndCompositeTermination(Termination<Solution_>... terminations) {
        super(terminations);
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    /**
     * @param solverScope never null
     * @return true if all the Terminations are terminated.
     */
    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        for (Termination<Solution_> termination : terminationList) {
            if (!termination.isSolverTerminated(solverScope)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param phaseScope never null
     * @return true if all the Terminations are terminated.
     */
    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        for (Termination<Solution_> termination : terminationList) {
            if (!termination.isPhaseTerminated(phaseScope)) {
                return false;
            }
        }
        return true;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     *
     * @param solverScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        double timeGradient = 1.0;
        for (Termination<Solution_> termination : terminationList) {
            double nextTimeGradient = termination.calculateSolverTimeGradient(solverScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

    /**
     * Calculates the minimum timeGradient of all Terminations.
     * Not supported timeGradients (-1.0) are ignored.
     *
     * @param phaseScope never null
     * @return the minimum timeGradient of the Terminations.
     */
    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        double timeGradient = 1.0;
        for (Termination<Solution_> termination : terminationList) {
            double nextTimeGradient = termination.calculatePhaseTimeGradient(phaseScope);
            if (nextTimeGradient >= 0.0) {
                timeGradient = Math.min(timeGradient, nextTimeGradient);
            }
        }
        return timeGradient;
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public AndCompositeTermination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return new AndCompositeTermination<>(createChildThreadTerminationList(solverScope, childThreadType));
    }

    @Override
    public String toString() {
        return "And(" + terminationList + ")";
    }

}
