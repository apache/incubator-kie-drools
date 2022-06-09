package org.optaplanner.core.impl.score.director.easy;

import java.util.Map;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;

/**
 * Easy java implementation of {@link ScoreDirector}, which recalculates the {@link Score}
 * of the {@link PlanningSolution working solution} every time. This is non-incremental calculation, which is slow.
 * This score director implementation does not support {@link ScoreExplanation#getConstraintMatchTotalMap()} and
 * {@link ScoreExplanation#getIndictmentMap()}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 * @see ScoreDirector
 */
public class EasyScoreDirector<Solution_, Score_ extends Score<Score_>>
        extends AbstractScoreDirector<Solution_, Score_, EasyScoreDirectorFactory<Solution_, Score_>> {

    private final EasyScoreCalculator<Solution_, Score_> easyScoreCalculator;

    public EasyScoreDirector(EasyScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory,
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference,
            EasyScoreCalculator<Solution_, Score_> easyScoreCalculator) {
        super(scoreDirectorFactory, lookUpEnabled, constraintMatchEnabledPreference);
        this.easyScoreCalculator = easyScoreCalculator;
    }

    public EasyScoreCalculator<Solution_, Score_> getEasyScoreCalculator() {
        return easyScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public Score_ calculateScore() {
        variableListenerSupport.assertNotificationQueuesAreEmpty();
        Score_ score = easyScoreCalculator.calculateScore(workingSolution);
        if (score == null) {
            throw new IllegalStateException("The easyScoreCalculator (" + easyScoreCalculator.getClass()
                    + ") must return a non-null score (" + score + ") in the method calculateScore().");
        } else if (!score.isSolutionInitialized()) {
            throw new IllegalStateException("The score (" + this + ")'s initScore (" + score.getInitScore()
                    + ") should be 0.\n"
                    + "Maybe the score calculator (" + easyScoreCalculator.getClass() + ") is calculating "
                    + "the initScore too, although it's the score director's responsibility.");
        }
        if (workingInitScore != 0) {
            score = score.withInitScore(workingInitScore);
        }
        setCalculatedScore(score);
        return score;
    }

    /**
     * Always false, {@link ConstraintMatchTotal}s are not supported by this {@link ScoreDirector} implementation.
     *
     * @return false
     */
    @Override
    public boolean isConstraintMatchEnabled() {
        return false;
    }

    /**
     * {@link ConstraintMatch}s are not supported by this {@link ScoreDirector} implementation.
     *
     * @throws IllegalStateException always
     * @return throws {@link IllegalStateException}
     */
    @Override
    public Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        throw new IllegalStateException(ConstraintMatch.class.getSimpleName()
                + " is not supported by " + EasyScoreDirector.class.getSimpleName() + ".");
    }

    /**
     * {@link ConstraintMatch}s are not supported by this {@link ScoreDirector} implementation.
     *
     * @throws IllegalStateException always
     * @return throws {@link IllegalStateException}
     */
    @Override
    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        throw new IllegalStateException(ConstraintMatch.class.getSimpleName()
                + " is not supported by " + EasyScoreDirector.class.getSimpleName() + ".");
    }

    @Override
    public boolean requiresFlushing() {
        return false; // Every score calculation starts from scratch; nothing is saved.
    }

}
