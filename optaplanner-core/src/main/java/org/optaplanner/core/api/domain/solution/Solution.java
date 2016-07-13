/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.domain.solution;

import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;

/**
 * Retained for backwards compatibility with 6.x. This interface will be removed in 8.0.
 * <p>
 * A solution represents a problem and a possible solution of that problem.
 * A possible solution does not need to be optimal or even feasible.
 * A Solution's variables do not even have to be initialized.
 * <p>
 * A Solution is mutable.
 * For scalability reasons, the same Solution instance, called the working solution, is continuously modified.
 * It's cloned to recall the best solution.
 * <p>
 * This annotation described declarative properties of the planning solution.
 * The planning solution class must implement this interface which is needed to get/set state.
 * But the planning solution class must also be annotated with {@link PlanningSolution}
 * describes declarative properties.
 * @param <S> the {@link Score} type used by this use case
 * @deprecated Use {@link PlanningScore}, {@link ProblemFactCollectionProperty} and {@link ProblemFactProperty} instead. Will be removed in 8.0.
 */
@Deprecated
public interface Solution<S extends Score> {

    /**
     * Returns the {@link Score} of this Solution.
     * @return null if the Solution is uninitialized
     *         or the last calculated {@link Score} is dirty the new {@link Score} has not yet been recalculated
     */
    S getScore();

    /**
     * Called by the {@link Solver} when the {@link Score} of this Solution has been calculated.
     * @param score sometimes null
     */
    void setScore(S score);

    /**
     * Called by the {@link DroolsScoreDirector} when the {@link PlanningSolution} needs to be inserted
     * into an empty {@link KieSession}.
     * These facts can be used by the score rules.
     * They don't change during planning (except through {@link ProblemFactChange} events).
     * <p>
     * Do not include the planning entities as problem facts:
     * they are automatically inserted into the {@link KieSession}.
     * @return never null (although an empty collection is allowed),
     *         all the facts of this solution except for the planning entities
     */
    Collection<? extends Object> getProblemFacts();

}
