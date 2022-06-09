package org.optaplanner.core.impl.solver.change;

import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Provides a layer of abstraction over {@link org.optaplanner.core.api.solver.change.ProblemChange} and the
 * deprecated {@link org.optaplanner.core.api.solver.ProblemFactChange} to preserve backward compatibility.
 */
public interface ProblemChangeAdapter<Solution_> {

    void doProblemChange(SolverScope<Solution_> solverScope);

    static <Solution_> ProblemChangeAdapter<Solution_> create(ProblemFactChange<Solution_> problemFactChange) {
        return (solverScope) -> problemFactChange.doChange(solverScope.getScoreDirector());
    }

    static <Solution_> ProblemChangeAdapter<Solution_> create(ProblemChange<Solution_> problemChange) {
        return (solverScope) -> {
            problemChange.doChange(solverScope.getWorkingSolution(), solverScope.getProblemChangeDirector());
            solverScope.getScoreDirector().triggerVariableListeners();
        };
    }
}
