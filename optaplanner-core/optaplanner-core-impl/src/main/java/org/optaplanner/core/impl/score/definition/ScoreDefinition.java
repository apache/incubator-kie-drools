package org.optaplanner.core.impl.score.definition;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * A ScoreDefinition knows how to compare {@link Score}s and what the perfect maximum/minimum {@link Score} is.
 *
 * @see AbstractScoreDefinition
 * @see HardSoftScoreDefinition
 * @param <Score_> the {@link Score} type
 */
public interface ScoreDefinition<Score_ extends Score<Score_>> {

    /**
     * Returns the label for {@link Score#getInitScore()}.
     *
     * @return never null
     * @see #getLevelLabels()
     */
    String getInitLabel();

    /**
     * Returns the length of {@link Score#toLevelNumbers()} for every {@link Score} of this definition.
     * For example: returns 2 on {@link HardSoftScoreDefinition}.
     *
     * @return at least 1
     */
    int getLevelsSize();

    /**
     * Returns the number of levels of {@link Score#toLevelNumbers()}.
     * that are used to determine {@link Score#isFeasible()}.
     *
     * @return at least 0, at most {@link #getLevelsSize()}
     */
    int getFeasibleLevelsSize();

    /**
     * Returns a label for each score level. Each label includes the suffix "score" and must start in lower case.
     * For example: returns {@code {"hard score", "soft score "}} on {@link HardSoftScoreDefinition}.
     * <p>
     * It does not include the {@link #getInitLabel()}.
     *
     * @return never null, array with length of {@link #getLevelsSize()}, each element is never null
     */
    String[] getLevelLabels();

    /**
     * Returns the {@link Class} of the actual {@link Score} implementation.
     * For example: returns {@link HardSoftScore HardSoftScore.class} on {@link HardSoftScoreDefinition}.
     *
     * @return never null
     */
    Class<Score_> getScoreClass();

    /**
     * The score that represents zero.
     *
     * @return never null
     */
    Score_ getZeroScore();

    /**
     * The score that represents the softest possible one.
     *
     * @return never null
     */
    Score_ getOneSoftestScore();

    /**
     * @param score never null
     * @return true if the score is higher or equal to {@link #getZeroScore()}
     */
    default boolean isPositiveOrZero(Score_ score) {
        return score.compareTo(getZeroScore()) >= 0;
    }

    /**
     * @param score never null
     * @return true if the score is lower or equal to {@link #getZeroScore()}
     */
    default boolean isNegativeOrZero(Score_ score) {
        return score.compareTo(getZeroScore()) <= 0;
    }

    /**
     * Returns a {@link String} representation of the {@link Score}.
     *
     * @param score never null
     * @return never null
     * @see #parseScore(String)
     */
    String formatScore(Score_ score);

    /**
     * Parses the {@link String} and returns a {@link Score}.
     *
     * @param scoreString never null
     * @return never null
     * @see #formatScore(Score)
     */
    Score_ parseScore(String scoreString);

    /**
     * The opposite of {@link Score#toLevelNumbers()}.
     *
     * @param initScore {@code <= 0}, managed by OptaPlanner, needed as a parameter in the {@link Score}'s creation
     *        method, see {@link Score#getInitScore()}
     * @param levelNumbers never null
     * @return never null
     */
    Score_ fromLevelNumbers(int initScore, Number[] levelNumbers);

    /**
     * Builds a {@link Score} which is equal or better than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     *
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     *        equal to {@link #getLevelsSize()}.
     * @param score never null, with {@link Score#getInitScore()} {@code 0}.
     * @return never null
     */
    Score_ buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, Score_ score);

    /**
     * Builds a {@link Score} which is equal or worse than any other {@link Score} with more variables initialized
     * (while the already variables don't change).
     *
     * @param initializingScoreTrend never null, with {@link InitializingScoreTrend#getLevelsSize()}
     *        equal to {@link #getLevelsSize()}.
     * @param score never null, with {@link Score#getInitScore()} {@code 0}
     * @return never null
     */
    Score_ buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, Score_ score);

    /**
     * Return {@link Score} whose every level is the result of dividing the matching levels in this and the divisor.
     * When rounding is needed, it is floored (as defined by {@link Math#floor(double)}).
     * <p>
     * If any of the levels in the divisor are equal to zero, the method behaves as if they were equal to one instead.
     *
     * @param divisor value by which this Score is to be divided
     * @return this / divisor
     */
    Score_ divideBySanitizedDivisor(Score_ dividend, Score_ divisor);

    /**
     * @param score never null
     * @return true if the otherScore is accepted as a parameter of {@link Score#add(Score)},
     *         {@link Score#subtract(Score)} and {@link Score#compareTo(Object)} for scores of this score definition.
     */
    boolean isCompatibleArithmeticArgument(Score score);

}
