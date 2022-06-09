package org.optaplanner.core.impl.score.director.easy;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * Easy implementation of {@link ScoreDirectorFactory}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 * @see EasyScoreDirector
 * @see ScoreDirectorFactory
 */
public class EasyScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        extends AbstractScoreDirectorFactory<Solution_, Score_> {

    private final EasyScoreCalculator<Solution_, Score_> easyScoreCalculator;

    public EasyScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            EasyScoreCalculator<Solution_, Score_> easyScoreCalculator) {
        super(solutionDescriptor);
        this.easyScoreCalculator = easyScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public EasyScoreDirector<Solution_, Score_> buildScoreDirector(
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        return new EasyScoreDirector<>(this, lookUpEnabled, constraintMatchEnabledPreference, easyScoreCalculator);
    }

}
