package org.optaplanner.core.api.solver;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.change.ProblemChange;

/**
 * This interface is deprecated.
 * A ProblemFactChange represents a change in 1 or more problem facts of a {@link PlanningSolution}.
 * Problem facts used by a {@link Solver} must not be changed while it is solving,
 * but by scheduling this command to the {@link Solver}, you can change them when the time is right.
 * <p>
 * Note that the {@link Solver} clones a {@link PlanningSolution} at will.
 * So any change must be done on the problem facts and planning entities referenced by the {@link PlanningSolution}
 * of the {@link ScoreDirector}. On each change it should also notify the {@link ScoreDirector} accordingly.
 *
 * @deprecated in favor of {@link ProblemChange}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface ProblemFactChange<Solution_> {

    /**
     * Does the change on the {@link PlanningSolution} of the {@link ScoreDirector}
     * and notifies the {@link ScoreDirector} accordingly.
     * Every modification to the {@link PlanningSolution}, must be correctly notified to the {@link ScoreDirector},
     * otherwise the {@link Score} calculation will be corrupted.
     *
     * @param scoreDirector never null
     *        Contains the {@link PlanningSolution working solution} which contains the problem facts
     *        (and {@link PlanningEntity planning entities}) to change.
     *        Also needs to get notified of those changes.
     */
    void doChange(ScoreDirector<Solution_> scoreDirector);

}
