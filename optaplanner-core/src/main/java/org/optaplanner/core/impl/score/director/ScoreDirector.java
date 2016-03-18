/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;

import java.util.Collection;

/**
 * The ScoreDirector holds the {@link Solution_ workingSolution}
 * and calculates the {@link Score} for that {@link Solution_}.
 */
public interface ScoreDirector<Solution_> {

    /**
     * The {@link Solution_} that is used to calculate the {@link Score}.
     * <p>
     * Because a {@link Score} is best calculated incrementally (by delta's),
     * the {@link ScoreDirector} needs to be notified when it's {@link Solution_ workingSolution} changes.
     * <p>
     * If the {@link Solution_} has been changed since {@link #calculateScore} has been called,
     * the score of this {@link Solution_} won't be correct.
     * @return never null
     */
    Solution_ getWorkingSolution();

    /**
     * The {@link Solution_ workingSolution} must never be the same instance as the {@link Solution_ bestSolution},
     * it should be a (un)changed clone.
     * <p>
     * Only call this method on a separate {@link ScoreDirector} instance,
     * build by {@link Solver#getScoreDirectorFactory()},
     * not on the one used inside the {@link Solver} itself.
     * @param workingSolution never null
     */
    void setWorkingSolution(Solution_ workingSolution);

    /**
     * Calculates the {@link Score} and updates the {@link Solution_ workingSolution} accordingly.
     * @return never null, the {@link Score} of the {@link Solution_ workingSolution}
     */
    Score calculateScore();

    /**
     * @return true if {@link #getConstraintMatchTotals()} can be called
     */
    boolean isConstraintMatchEnabled();

    /**
     * @return never null
     * @throws IllegalStateException if {@link #isConstraintMatchEnabled()} returns false
     */
    Collection<ConstraintMatchTotal> getConstraintMatchTotals();

    void beforeEntityAdded(Object entity);

    void afterEntityAdded(Object entity);

    void beforeVariableChanged(Object entity, String variableName);

    void afterVariableChanged(Object entity, String variableName);

    // TODO VariableDescriptor is not likely to go to public API

    void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity);

    void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity);

    void changeVariableFacade(VariableDescriptor variableDescriptor, Object entity, Object newValue);

    void triggerVariableListeners();

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
     * Needs to be called after use because some implementations needs to clean up their resources.
     */
    void dispose();

}
