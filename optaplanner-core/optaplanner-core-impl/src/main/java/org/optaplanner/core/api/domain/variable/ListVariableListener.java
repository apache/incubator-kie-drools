package org.optaplanner.core.api.domain.variable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * A listener sourced on a {@link PlanningListVariable}.
 * <p>
 * Changes shadow variables when a genuine source list variable changes.
 * <p>
 * Important: it must only change the shadow variable(s) for which it's configured!
 * It should never change a genuine variable or a problem fact.
 * It can change its shadow variable(s) on multiple entity instances
 * (for example: an arrivalTime change affects all trailing entities too).
 * <p>
 * It is recommended to keep implementations stateless.
 * If state must be implemented, implementations may need to override the default methods
 * ({@link #resetWorkingSolution(ScoreDirector)}, {@link #close()}).
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> @{@link PlanningEntity} on which the source variable is declared
 * @param <Element_> the type of elements of the source list variable
 */
public interface ListVariableListener<Solution_, Entity_, Element_> extends AbstractVariableListener<Solution_, Entity_> {

    /**
     * The listener must unset all shadow variables it is responsible for when an element is unassigned from the source list
     * variable. For example, a {@code Task}'s {@code startTime} shadow variable must be reset to {@code null} after a task
     * is unassigned from {@code Employee.tasks} when the move that assigned it there is undone during Construction Heuristic
     * phase.
     *
     * @param scoreDirector score director
     * @param element the unassigned element
     */
    void afterListVariableElementUnassigned(ScoreDirector<Solution_> scoreDirector, Element_ element);

    /**
     * Tells the listener that some elements within the range starting at {@code fromIndex} (inclusive) and ending at
     * {@code toIndex} (exclusive) will change.
     * Be aware that the {@link #afterListVariableChanged} call after the change is done often has a different
     * {@code fromIndex} and {@code toIndex} because the number of elements in the list variable can change.
     * <p>
     * The list variable change includes:
     * <ul>
     * <li>Changing position (index) of one or more elements.</li>
     * <li>Removing one or more elements from the list variable.</li>
     * <li>Adding one or more elements to the list variable.</li>
     * <li>Any mix of the above.</li>
     * The range has the following properties:
     * </ul>
     * <li>{@code fromIndex} is greater than or equal to 0; {@code toIndex} is less than or equal to the list variable
     * size.</li>
     * <li>{@code toIndex} is greater than or equal to {@code fromIndex}.</li>
     * <li>The range contains all elements that are going to be changed.</li>
     * <li>The range may contain elements that are not going to be changed.</li>
     * <li>The range may be empty ({@code fromIndex} equals {@code toIndex}) if none of the existing list variable elements
     * are going to be changed.</li>
     * </ol>
     *
     * @param scoreDirector score director
     * @param entity entity with the changed list variable
     * @param fromIndex low endpoint (inclusive) of the changed range
     * @param toIndex high endpoint (exclusive) of the changed range
     */
    void beforeListVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int fromIndex, int toIndex);

    /**
     * Tells the listener that some elements within the range starting at {@code fromIndex} (inclusive) and ending at
     * {@code toIndex} (exclusive) changed.
     * <p>
     * The list variable change includes:
     * <ul>
     * <li>Changing position (index) of one or more elements.</li>
     * <li>Removing one or more elements from the list variable.</li>
     * <li>Adding one or more elements to the list variable.</li>
     * <li>Any mix of the above.</li>
     * The range has the following properties:
     * </ul>
     * <li>{@code fromIndex} is greater than or equal to 0; {@code toIndex} is less than or equal to the list variable
     * size.</li>
     * <li>{@code toIndex} is greater than or equal to {@code fromIndex}.</li>
     * <li>The range contains all elements that have changed.</li>
     * <li>The range may contain elements that have not changed.</li>
     * <li>The range may be empty ({@code fromIndex} equals {@code toIndex}) if none of the existing list variable elements
     * have changed.</li>
     * </ol>
     *
     * @param scoreDirector score director
     * @param entity entity with the changed list variable
     * @param fromIndex low endpoint (inclusive) of the changed range
     * @param toIndex high endpoint (exclusive) of the changed range
     */
    void afterListVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int fromIndex, int toIndex);
}
