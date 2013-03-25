/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.solution;

import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;

/**
 * A Solution represents a problem and a possible solution of that problem.
 * A possible solution does not need to optimal or even feasible.
 * A Solution does not even have to be initialized with a possible solution.
 * <p/>
 * A Solution is mutable.
 * For scalability reasons, the same Solution instance will be continuously modified.
 * It's cloned to recall the best solution.
 */
public interface Solution<S extends Score> {

    /**
     * Returns the Score of this Solution.
     * @return null if the Solution is uninitialized
     *         or the last calculated Score is dirty the new Score has not yet been recalculated
     */
    S getScore();

    /**
     * Called by the {@link Solver} when the Score of this Solution has been calculated.
     * @param score null if the Solution has changed and the new Score has not yet been recalculated
     */
    void setScore(S score);

    /**
     * Called by the {@link DroolsScoreDirector} when the {@link Solution} needs to be inserted
     * into an empty {@link KieSession}.
     * These facts can be used by the score rules.
     * They don't change during planning (except through {@link ProblemFactChange} events).
     * <p/>
     * Do not include the planning entities as problem facts:
     * they are automatically inserted into the {@link KieSession} if and only if they are initialized.
     * When they are initialized later, they are also automatically inserted.
     * @return never null (although an empty collection is allowed),
     *         all the facts of this solution except for the planning entities
     */
    Collection<? extends Object> getProblemFacts();

}
