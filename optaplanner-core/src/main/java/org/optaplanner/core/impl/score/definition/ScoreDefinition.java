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

package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * A ScoreDefinition knows how to compare {@link Score}s and what the perfect maximum/minimum {@link Score} is.
 * @see AbstractScoreDefinition
 * @see HardSoftScoreDefinition
 */
public interface ScoreDefinition<S extends Score<S>> {

    /**
     * Returns the label for {@link Score#getInitScore()}.
     * @return never null
     * @see #getLevelLabels()
     */
    String getInitLabel();

    /**
     * Returns the length of {@link Score#toLevelNumbers()} for every {@link Score} of this definition.
     * For example: returns 2 on {@link HardSoftScoreDefinition}.
     * @return at least 1
     */
    int getLevelsSize();

    /**
     * Returns a label for each score level. Each label includes the suffix "score" and must start in lower case.
     * For example: returns {@code {"hard score", "soft score "}} on {@link HardSoftScoreDefinition}.
     * <p>
     * It does not include the {@link #getInitLabel()}.
     * @return never null, array with length of {@link #getLevelsSize()}, each element is never null
     */
    String[] getLevelLabels();

    /**
     * Returns the {@link Class} of the actual {@link Score} implementation.
     * For example: returns {@link HardSoftScore HardSoftScore.class} on {@link HardSoftScoreDefinition}.
     * @return never null
     */
    Class<S> getScoreClass();

    /**
     * The score that represents zero.
     * @return never null
     */
    S getZeroScore();

    /**
     * @param score never null
     * @return true if the score is higher or equal to {@link #getZeroScore()}
     */
    default boolean isPositiveOrZero(S score) {
        return score.compareTo(getZeroScore()) >= 0;
    }

    /**
     * @param score never null
     * @return true if the score is lower or equal to {@link #getZeroScore()}
     */
    default boolean isNegativeOrZero(S score) {
        return score.compareTo(getZeroScore()) <= 0;
    }

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
     * @see ScoreUtils#parseScore(Class, String)
     */
    S parseScore(String scoreString);

    /**
     * The opposite of {@link Score#toLevelNumbers()}.
     * @param initScore {@code <= 0}, managed by OptaPlanner, needed as a parameter in the {@link Score}'s creation
     * method, see {@link Score#getInitScore()}
     * @param levelNumbers never null
     * @return never null
     */
    S fromLevelNumbers(int initScore, Number[] levelNumbers);

    /**
     * Used by {@link BavetConstraintFactory}
     * @param constraintMatchEnabled true if {@link ScoreDirector#isConstraintMatchEnabled()} should be true
     * @return never null
     */
    ScoreInliner<S> buildScoreInliner(boolean constraintMatchEnabled);

    /**
     * Used by {@link DroolsScoreDirector}.
     * @param constraintMatchEnabled true if {@link ScoreDirector#isConstraintMatchEnabled()} should be true
     * @return never null
     */
    ScoreHolder<S> buildScoreHolder(boolean constraintMatchEnabled);

    /**
     * Builds a {@link Score} which is equal or better than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     * equal to {@link #getLevelsSize()}.
     * @param score never null, with {@link Score#getInitScore()} {@code 0}.
     * @return never null
     */
    S buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, S score);

    /**
     * Builds a {@link Score} which is equal or worse than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     * equal to {@link #getLevelsSize()}.
     * @param score never null, with {@link Score#getInitScore()} {@code 0}
     * @return never null
     */
    S buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, S score);

}
