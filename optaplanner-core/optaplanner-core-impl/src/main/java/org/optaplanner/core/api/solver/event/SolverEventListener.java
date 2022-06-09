package org.optaplanner.core.api.solver.event;

import java.util.EventListener;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.change.ProblemChange;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@FunctionalInterface
public interface SolverEventListener<Solution_> extends EventListener {

    /**
     * Called once every time when a better {@link PlanningSolution} is found.
     * The {@link PlanningSolution} is guaranteed to be initialized.
     * Early in the solving process it's usually called more frequently than later on.
     * <p>
     * Called from the solver thread.
     * <b>Should return fast, because it steals time from the {@link Solver}.</b>
     * <p>
     * In real-time planning
     * If {@link Solver#addProblemChange(ProblemChange)} has been called once or more,
     * all {@link ProblemChange}s in the queue will be processed and this method is called only once.
     * In that case, the former best {@link PlanningSolution} is considered stale,
     * so it doesn't matter whether the new {@link Score} is better than that or not.
     *
     * @param event never null
     */
    void bestSolutionChanged(BestSolutionChangedEvent<Solution_> event);

}
