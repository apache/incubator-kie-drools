package org.optaplanner.core.impl.score.director;

import java.util.Map;
import java.util.function.Consumer;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 */
public interface InnerScoreDirector<Solution_, Score_ extends Score<Score_>>
        extends ScoreDirector<Solution_>, AutoCloseable {

    /**
     * The {@link PlanningSolution working solution} must never be the same instance as the
     * {@link PlanningSolution best solution}, it should be a (un)changed clone.
     *
     * @param workingSolution never null
     */
    void setWorkingSolution(Solution_ workingSolution);

    /**
     * Calculates the {@link Score} and updates the {@link PlanningSolution working solution} accordingly.
     *
     * @return never null, the {@link Score} of the {@link PlanningSolution working solution}
     */
    Score_ calculateScore();

    /**
     * @return true if {@link #getConstraintMatchTotalMap()} and {@link #getIndictmentMap} can be called
     */
    boolean isConstraintMatchEnabled();

    /**
     * Explains the {@link Score} of {@link #calculateScore()} by splitting it up per {@link Constraint}.
     * <p>
     * The sum of {@link ConstraintMatchTotal#getScore()} equals {@link #calculateScore()}.
     * <p>
     * Call {@link #calculateScore()} before calling this method,
     * unless that method has already been called since the last {@link PlanningVariable} changes.
     *
     * @return never null, the key is the {@link ConstraintMatchTotal#getConstraintId() constraintId}
     *         (to create one, use {@link ConstraintMatchTotal#composeConstraintId(String, String)}).
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} returns false
     * @see #getIndictmentMap()
     */
    Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap();

    /**
     * Explains the impact of each planning entity or problem fact on the {@link Score}.
     * An {@link Indictment} is basically the inverse of a {@link ConstraintMatchTotal}:
     * it is a {@link Score} total for each {@link ConstraintMatch#getJustification() constraint justification}.
     * <p>
     * The sum of {@link ConstraintMatchTotal#getScore()} differs from {@link #calculateScore()}
     * because each {@link ConstraintMatch#getScore()} is counted
     * for each {@link ConstraintMatch#getJustification() constraint justification}.
     * <p>
     * Call {@link #calculateScore()} before calling this method,
     * unless that method has already been called since the last {@link PlanningVariable} changes.
     *
     * @return never null, the key is a {@link ProblemFactCollectionProperty problem fact} or a
     *         {@link PlanningEntity planning entity}
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} returns false
     * @see #getConstraintMatchTotalMap()
     */
    Map<Object, Indictment<Score_>> getIndictmentMap();

    /**
     * @param constraintMatchEnabledPreference false if a {@link ScoreDirector} implementation
     *        should not do {@link ConstraintMatch} tracking even if it supports it.
     */
    void overwriteConstraintMatchEnabledPreference(boolean constraintMatchEnabledPreference);

    /**
     * @return used to check {@link #isWorkingEntityListDirty(long)} later on
     */
    long getWorkingEntityListRevision();

    /**
     * @param move never null
     * @param assertMoveScoreFromScratch true will hurt performance
     * @return never null
     */
    Score_ doAndProcessMove(Move<Solution_> move, boolean assertMoveScoreFromScratch);

    /**
     * @param move never null
     * @param assertMoveScoreFromScratch true will hurt performance
     * @param moveProcessor never null, use this to store the score as well as call the acceptor and forager
     */
    void doAndProcessMove(Move<Solution_> move, boolean assertMoveScoreFromScratch, Consumer<Score_> moveProcessor);

    /**
     * @param expectedWorkingEntityListRevision an
     * @return true if the entityList might have a different set of instances now
     */
    boolean isWorkingEntityListDirty(long expectedWorkingEntityListRevision);

    /**
     * Some score directors (such as the Drools-based) keep a set of changes
     * that they only apply when {@link #calculateScore()} is called.
     * Until that happens, this set accumulates and could possibly act as a memory leak.
     *
     * @return true if the score director can potentially cause a memory leak due to unflushed changes.
     */
    boolean requiresFlushing();

    /**
     * @return never null
     */
    InnerScoreDirectorFactory<Solution_, Score_> getScoreDirectorFactory();

    /**
     * @return never null
     */
    SolutionDescriptor<Solution_> getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition<Score_> getScoreDefinition();

    /**
     * Returns a planning clone of the solution,
     * which is not a shallow clone nor a deep clone nor a partition clone.
     *
     * @return never null, planning clone
     */
    Solution_ cloneWorkingSolution();

    /**
     * Returns a planning clone of the solution,
     * which is not a shallow clone nor a deep clone nor a partition clone.
     *
     * @param originalSolution never null
     * @return never null, planning clone
     */
    Solution_ cloneSolution(Solution_ originalSolution);

    /**
     * @return at least 0L
     */
    long getCalculationCount();

    void resetCalculationCount();

    /**
     * @return never null
     */
    SupplyManager getSupplyManager();

    /**
     * Clones this {@link ScoreDirector} and its {@link PlanningSolution working solution}.
     * Use {@link #getWorkingSolution()} to retrieve the {@link PlanningSolution working solution} of that clone.
     * <p>
     * This is heavy method, because it usually breaks incremental score calculation. Use it sparingly.
     * Therefore it's best to clone lazily by delaying the clone call as long as possible.
     *
     * @return never null
     */
    InnerScoreDirector<Solution_, Score_> clone();

    InnerScoreDirector<Solution_, Score_> createChildThreadScoreDirector(ChildThreadType childThreadType);

    /**
     * Do not waste performance by propagating changes to step (or higher) mechanisms.
     *
     * @param allChangesWillBeUndoneBeforeStepEnds true if all changes will be undone
     */
    void setAllChangesWillBeUndoneBeforeStepEnds(boolean allChangesWillBeUndoneBeforeStepEnds);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in the current {@link ScoreDirector} (with possibly incremental calculation residue),
     * it is equal to the parameter {@link Score expectedWorkingScore}.
     * <p>
     * Used to assert that skipping {@link #calculateScore()} (when the score is otherwise determined) is correct.
     *
     * @param expectedWorkingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     *        is included in the exception message
     */
    void assertExpectedWorkingScore(Score_ expectedWorkingScore, Object completedAction);

    /**
     * Asserts that if all {@link VariableListener}s are forcibly triggered,
     * and therefore all shadow variables are updated if needed,
     * that none of the shadow variables of the {@link PlanningSolution working solution} change,
     * Then also asserts that the {@link Score} calculated for the {@link PlanningSolution working solution} afterwards
     * is equal to the parameter {@link Score expectedWorkingScore}.
     * <p>
     * Used to assert that the shadow variables' state is consistent with the genuine variables' state.
     *
     * @param expectedWorkingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     *        is included in the exception message
     */
    void assertShadowVariablesAreNotStale(Score_ expectedWorkingScore, Object completedAction);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in a fresh {@link ScoreDirector} (with no incremental calculation residue),
     * it is equal to the parameter {@link Score workingScore}.
     * <p>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     *
     * @param workingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     *        is included in the exception message
     * @see InnerScoreDirectorFactory#assertScoreFromScratch
     */
    void assertWorkingScoreFromScratch(Score_ workingScore, Object completedAction);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in a fresh {@link ScoreDirector} (with no incremental calculation residue),
     * it is equal to the parameter {@link Score predictedScore}.
     * <p>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     *
     * @param predictedScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     *        is included in the exception message
     * @see InnerScoreDirectorFactory#assertScoreFromScratch
     */
    void assertPredictedScoreFromScratch(Score_ predictedScore, Object completedAction);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in the current {@link ScoreDirector} (with incremental calculation residue),
     * it is equal to the parameter {@link Score beforeMoveScore}.
     * <p>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     *
     * @param move never null
     * @param beforeMoveScore never null
     */
    void assertExpectedUndoMoveScore(Move<Solution_> move, Score_ beforeMoveScore);

    /**
     * Asserts that none of the planning facts from {@link SolutionDescriptor#getAllFacts(Object)} for
     * {@link #getWorkingSolution()} have {@link PlanningId}s with a null value.
     */
    void assertNonNullPlanningIds();

    /**
     * Needs to be called after use because some implementations need to clean up their resources.
     */
    @Override
    void close();

    // ************************************************************************
    // Basic variable
    // ************************************************************************

    void beforeVariableChanged(VariableDescriptor<Solution_> variableDescriptor, Object entity);

    void afterVariableChanged(VariableDescriptor<Solution_> variableDescriptor, Object entity);

    void changeVariableFacade(VariableDescriptor<Solution_> variableDescriptor, Object entity, Object newValue);

    // ************************************************************************
    // List variable
    // ************************************************************************

    /**
     * Call this for each element that will be assigned (added to a list variable of one entity without being removed
     * from a list variable of another entity).
     *
     * @param variableDescriptor the list variable descriptor
     * @param element the assigned element
     */
    void beforeListVariableElementAssigned(ListVariableDescriptor<Solution_> variableDescriptor, Object element);

    /**
     * Call this for each element that was assigned (added to a list variable of one entity without being removed
     * from a list variable of another entity).
     *
     * @param variableDescriptor the list variable descriptor
     * @param element the assigned element
     */
    void afterListVariableElementAssigned(ListVariableDescriptor<Solution_> variableDescriptor, Object element);

    /**
     * Call this for each element that will be unassigned (removed from a list variable of one entity without being added
     * to a list variable of another entity).
     *
     * @param variableDescriptor the list variable descriptor
     * @param element the unassigned element
     */
    void beforeListVariableElementUnassigned(ListVariableDescriptor<Solution_> variableDescriptor, Object element);

    /**
     * Call this for each element that was unassigned (removed from a list variable of one entity without being added
     * to a list variable of another entity).
     *
     * @param variableDescriptor the list variable descriptor
     * @param element the unassigned element
     */
    void afterListVariableElementUnassigned(ListVariableDescriptor<Solution_> variableDescriptor, Object element);

    /**
     * Notify the score director before a list variable changes.
     * <p>
     * The list variable change includes:
     * <ul>
     * <li>Changing position (index) of one or more elements.</li>
     * <li>Removing one or more elements from the list variable.</li>
     * <li>Adding one or more elements to the list variable.</li>
     * <li>Any mix of the above.</li>
     * </ul>
     * For the sake of variable listeners' efficiency, the change notification requires an index range that contains elements
     * affected by the change. The range starts at {@code fromIndex} (inclusive) and ends at {@code toIndex} (exclusive).
     * <p>
     * The range has to comply with the following contract:
     * <ol>
     * <li>{@code fromIndex} must be greater than or equal to 0; {@code toIndex} must be less than or equal to the list variable
     * size.</li>
     * <li>{@code toIndex} must be greater than or equal to {@code fromIndex}.</li>
     * <li>The range must contain all elements that are going to be changed.</li>
     * <li>The range is allowed to contain elements that are not going to be changed.</li>
     * <li>The range may be empty ({@code fromIndex} equals {@code toIndex}) if none of the existing list variable elements
     * are going to be changed.</li>
     * </ol>
     * <p>
     * {@link #beforeListVariableElementUnassigned} must be called for each element that will be unassigned (removed from a list
     * variable of one entity without being added to a list variable of another entity).
     *
     * @param variableDescriptor descriptor of the list variable being changed
     * @param entity the entity owning the list variable being changed
     * @param fromIndex low endpoint (inclusive) of the changed range
     * @param toIndex high endpoint (exclusive) of the changed range
     */
    void beforeListVariableChanged(ListVariableDescriptor<Solution_> variableDescriptor, Object entity, int fromIndex,
            int toIndex);

    /**
     * Notify the score director after a list variable changes.
     * <p>
     * The list variable change includes:
     * <ul>
     * <li>Changing position (index) of one or more elements.</li>
     * <li>Removing one or more elements from the list variable.</li>
     * <li>Adding one or more elements to the list variable.</li>
     * <li>Any mix of the above.</li>
     * </ul>
     * For the sake of variable listeners' efficiency, the change notification requires an index range that contains elements
     * affected by the change. The range starts at {@code fromIndex} (inclusive) and ends at {@code toIndex} (exclusive).
     * <p>
     * The range has to comply with the following contract:
     * <ol>
     * <li>{@code fromIndex} must be greater than or equal to 0; {@code toIndex} must be less than or equal to the list variable
     * size.</li>
     * <li>{@code toIndex} must be greater than or equal to {@code fromIndex}.</li>
     * <li>The range must contain all elements that have changed.</li>
     * <li>The range is allowed to contain elements that have not changed.</li>
     * <li>The range may be empty ({@code fromIndex} equals {@code toIndex}) if none of the existing list variable elements
     * have changed.</li>
     * </ol>
     * <p>
     * {@link #afterListVariableElementUnassigned} must be called for each element that was unassigned (removed from a list
     * variable of one entity without being added to a list variable of another entity).
     *
     * @param variableDescriptor descriptor of the list variable being changed
     * @param entity the entity owning the list variable being changed
     * @param fromIndex low endpoint (inclusive) of the changed range
     * @param toIndex high endpoint (exclusive) of the changed range
     */
    void afterListVariableChanged(ListVariableDescriptor<Solution_> variableDescriptor, Object entity, int fromIndex,
            int toIndex);

}
