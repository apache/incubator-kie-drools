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

package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.termination.Termination;

/**
 * A ScoreDefinition knows how to compare {@link Score}s and what the perfect maximum/minimum {@link Score} is.
 * @see AbstractScoreDefinition
 * @see HardSoftScoreDefinition
 */
public interface ScoreDefinition<S extends Score> {

    /**
     * The perfect maximum {@link Score} is the {@link Score} of which there is no better in any problem instance.
     * This doesn't mean that the current problem instance, or any problem instance for that matter,
     * could ever attain that {@link Score}.
     * </p>
     * For example, most cases have a perfect maximum {@link Score} of zero, as most use cases only have negative
     * constraints.
     * @return null if not supported
     */
    S getPerfectMaximumScore();

    /**
     * The perfect minimum {@link Score} is the {@link Score} of which there is no worse in any problem instance.
     * This doesn't mean that the current problem instance, or any problem instance for that matter,
     * could ever attain such a bad {@link Score}.
     * </p>
     * For example, most cases have a perfect minimum {@link Score} of negative infinity.
     * @return null if not supported
     */
    S getPerfectMinimumScore();

    /**
     * Returns the {@link Class} of the actual {@link Score} implementation
     * @return never null
     */
    Class<S> getScoreClass();

    /**
     * Returns a {@link String} representation of the {@link Score}.
     * @param score never null
     * @return never null
     * @see #parseScore(String)
     */
    String formatScore(Score score);

    /**
     * Parses the {@link String} and returns a {@link Score}.
     * @param scoreString never null
     * @return never null
     * @see #formatScore(Score)
     */
    Score parseScore(String scoreString);

    /**
     * See explanation in {@link Termination#calculateSolverTimeGradient(DefaultSolverScope)}.
     * @param startScore never null
     * @param endScore never null
     * @param score never null
     * @return between 0.0 and 1.0
     */
    double calculateTimeGradient(S startScore, S endScore, S score);

    /**
     * TODO JBRULES-2238 remove when the rule that sums the final score can be written as a single rule and {@link ScoreHolder} is dead
     * @return never null
     */
    ScoreHolder buildScoreHolder();

}
