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
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * A ScoreDefinition knows how to compare {@link Score}s and what the perfect maximum/minimum {@link Score} is.
 * @see AbstractScoreDefinition
 * @see HardSoftScoreDefinition
 */
public interface ScoreDefinition<S extends Score> {

    /**
     * Returns the length of {@link Score#toLevelNumbers()} for every {@link Score} of this definition.
     * @return at least 1
     */
    int getLevelsSize();

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
    String formatScore(S score);

    /**
     * Parses the {@link String} and returns a {@link Score}.
     * @param scoreString never null
     * @return never null
     * @see #formatScore(Score)
     */
    S parseScore(String scoreString);

    /**
     * Used by {@link DroolsScoreDirector}.
     * @param constraintMatchEnabled true if {@link ScoreHolder#isConstraintMatchEnabled()} should be true
     * @return never null
     */
    ScoreHolder buildScoreHolder(boolean constraintMatchEnabled);

    /**
     * Builds a {@link Score} which is equal or better than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     * equal to {@link #getLevelsSize()}.
     * @param score never null
     * @return never null
     */
    S buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, S score);

    /**
     * Builds a {@link Score} which is equal or worse than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     * equal to {@link #getLevelsSize()}.
     * @param score never null
     * @return never null
     */
    S buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, S score);

}
