/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director;

import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.score.constraint.ScoreConstraintMatchTotal;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solution.Solution;

/**
 * The ScoreDirector holds the {@link Solution workingSolution}
 * and calculates the {@link Score} for that {@link Solution}.
 */
public interface ScoreDirector {

    /**
     * @return never null
     */
    ScoreDirectorFactory getScoreDirectorFactory();

    /**
     * @return never null
     */
    SolutionDescriptor getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition getScoreDefinition();

    /**
     * The {@link Solution} that is used to calculate the {@link Score}.
     * <p/>
     * Because a {@link Score} is best calculated incrementally (by delta's),
     * the {@link ScoreDirector} needs to be notified when it's {@link Solution workingSolution} changes.
     * <p/>
     * If the {@link Solution} has been changed since {@link #calculateScore} has been called,
     * the {@link Solution#getScore()} of this {@link Solution} won't be correct.
     * @return never null
     */
    Solution getWorkingSolution();

    /**
     * The {@link Solution workingSolution} must never be the same instance as the {@link Solution bestSolution},
     * it should be a (un)changed clone.
     * @param workingSolution never null
     */
    void setWorkingSolution(Solution workingSolution);

    Solution cloneWorkingSolution();

    void beforeEntityAdded(Object entity);

    void afterEntityAdded(Object entity);

    void beforeAllVariablesChanged(Object entity);

    void afterAllVariablesChanged(Object entity);

    void beforeVariableChanged(Object entity, String variableName);

    void afterVariableChanged(Object entity, String variableName);

    void beforeEntityRemoved(Object entity);

    void afterEntityRemoved(Object entity);

    // TODO extract this set of methods into a separate interface, only used by ProblemFactChange

    void beforeProblemFactAdded(Object problemFact);

    void afterProblemFactAdded(Object problemFact);

    void beforeProblemFactChanged(Object problemFact);

    void afterProblemFactChanged(Object problemFact);

    void beforeProblemFactRemoved(Object problemFact);

    void afterProblemFactRemoved(Object problemFact);


    /**
     * @return never null: an empty list if there are none
     */
    List<Object> getWorkingPlanningEntityList();

    int countWorkingSolutionUninitializedVariables();

    /**
     * @return true if the {@link Solution workingSolution} is initialized
     */
    boolean isWorkingSolutionInitialized();

    /**
     * Calculates the {@link Score} and updates the {@link Solution workingSolution} accordingly.
     * @return never null, the {@link Score} of the {@link Solution workingSolution}
     */
    Score calculateScore();

    /**
     * @return at least 0L
     */
    long getCalculateCount();

    /**
     * @return true if {@link #getConstraintMatchTotals()} can be called
     */
    boolean isConstraintMatchEnabled();

    /**
     * @return never null
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} returns false
     */
    Collection<ScoreConstraintMatchTotal> getConstraintMatchTotals();

    /**
     * Clones this {@link ScoreDirector} and its {@link Solution workingSolution}.
     * Use {@link #getWorkingSolution()} to retrieve the {@link Solution workingSolution} of that clone.
     * <p/>
     * This is heavy method, because it usually breaks incremental score calculation. Use it sparingly.
     * Therefore it's best to clone lazily by delaying the clone call as long as possible.
     * @return never null
     */
    ScoreDirector clone();

    /**
     * @param chainedVariableDescriptor never null, must be {@link PlanningVariableDescriptor#isChained()} true
     * and known to the {@link SolutionDescriptor}
     * @param planningValue sometimes null
     * @return never null
     */
    Object getTrailingEntity(PlanningVariableDescriptor chainedVariableDescriptor, Object planningValue);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link Solution workingSolution}
     * in the current {@link ScoreDirector} (with possibly incremental calculation residue),
     * it is equal to the parameter {@link Score expectedWorkingScore}.
     * <p/>
     * Used to assert that skipping {@link #calculateScore()} (when the score is otherwise determined) is correct,
     * @param expectedWorkingScore never null
     */
    void assertExpectedWorkingScore(Score expectedWorkingScore);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link Solution workingSolution}
     * in a fresh {@link ScoreDirector} (with no incremental calculation residue),
     * it is equal to the parameter {@link Score workingScore}.
     * <p/>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     * @param workingScore never null
     * @see ScoreDirectorFactory#assertScoreFromScratch(Solution)
     */
    void assertWorkingScoreFromScratch(Score workingScore);

    /**
     * Needs to be called after use because some implementations needs to clean up their resources.
     */
    void dispose();

}
