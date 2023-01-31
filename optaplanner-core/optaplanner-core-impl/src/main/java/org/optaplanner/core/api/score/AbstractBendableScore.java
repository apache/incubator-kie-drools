package org.optaplanner.core.api.score;

import java.util.function.Predicate;

import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * Abstract superclass for bendable {@link Score} types.
 * <p>
 * Subclasses must be immutable.
 *
 * @deprecated Implement {@link IBendableScore} instead.
 */
@Deprecated(forRemoval = true)
public abstract class AbstractBendableScore<Score_ extends AbstractBendableScore<Score_>>
        extends AbstractScore<Score_>
        implements IBendableScore<Score_> {

    protected static final String HARD_LABEL = ScoreUtil.HARD_LABEL;
    protected static final String SOFT_LABEL = ScoreUtil.SOFT_LABEL;
    protected static final String[] LEVEL_SUFFIXES = ScoreUtil.LEVEL_SUFFIXES;

    protected static String[][] parseBendableScoreTokens(Class<? extends AbstractBendableScore<?>> scoreClass,
            String scoreString) {
        return ScoreUtil.parseBendableScoreTokens(scoreClass, scoreString);
    }

    /**
     * @param initScore see {@link Score#initScore()}
     */
    protected AbstractBendableScore(int initScore) {
        super(initScore);
    }

    protected String buildBendableShortString(Predicate<Number> notZero) {
        return ScoreUtil.buildBendableShortString(this, notZero);
    }

}
