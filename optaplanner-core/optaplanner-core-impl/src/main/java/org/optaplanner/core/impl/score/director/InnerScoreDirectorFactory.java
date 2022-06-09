package org.optaplanner.core.impl.score.director;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 */
public interface InnerScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        extends ScoreDirectorFactory<Solution_> {

    /**
     * @return never null
     */
    SolutionDescriptor<Solution_> getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition<Score_> getScoreDefinition();

    @Override
    InnerScoreDirector<Solution_, Score_> buildScoreDirector();

    /**
     * Like {@link #buildScoreDirector()}, but optionally disables {@link ConstraintMatch} tracking and look up
     * for more performance (presuming the {@link ScoreDirector} implementation actually supports it to begin with).
     *
     * @param lookUpEnabled true if a {@link ScoreDirector} implementation should track all working objects
     *        for {@link ScoreDirector#lookUpWorkingObject(Object)}
     * @param constraintMatchEnabledPreference false if a {@link ScoreDirector} implementation
     *        should not do {@link ConstraintMatch} tracking even if it supports it.
     * @return never null
     * @see InnerScoreDirector#isConstraintMatchEnabled()
     * @see InnerScoreDirector#getConstraintMatchTotalMap()
     */
    InnerScoreDirector<Solution_, Score_> buildScoreDirector(boolean lookUpEnabled,
            boolean constraintMatchEnabledPreference);

    /**
     * @return never null
     */
    InitializingScoreTrend getInitializingScoreTrend();

    /**
     * Asserts that if the {@link Score} is calculated for the parameter solution,
     * it would be equal to the score of that parameter.
     *
     * @param solution never null
     * @see InnerScoreDirector#assertWorkingScoreFromScratch(Score, Object)
     */
    void assertScoreFromScratch(Solution_ solution);

}
