/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.director.incremental;

import java.util.Collection;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Allows a {@link IncrementalScoreCalculator} to report {@link ConstraintMatchTotal}s
 * for explaining a score (= which score constraints match for how much)
 * and also for score corruption analysis.
 * @param <Solution_> Subclass of {@link Solution}
 * @see IncrementalScoreCalculator
 */
public interface ConstraintMatchAwareIncrementalScoreCalculator<Solution_ extends Solution>
        extends IncrementalScoreCalculator<Solution_> {

    /**
     * Allows for increased performance by tracking only if constraintMatchEnabled is true.
     * <p>
     * Every implementation should call {@link #resetWorkingSolution(Solution)}
     * and only handle the constraintMatchEnabled parameter specifically (or ignore it).
     * @param workingSolution never null, to pass to {@link #resetWorkingSolution(Solution)}.
     * @param constraintMatchEnabled true if {@link #getConstraintMatchTotals()} might be called.
     */
    void resetWorkingSolution(Solution_ workingSolution, boolean constraintMatchEnabled);

    /**
     * @return never null
     * @throws IllegalStateException if {@link #resetWorkingSolution(Solution, boolean)}'s
     * constraintMatchEnabled parameter was false
     * @see ScoreDirector#getConstraintMatchTotals()
     */
    Collection<ConstraintMatchTotal> getConstraintMatchTotals();

}
