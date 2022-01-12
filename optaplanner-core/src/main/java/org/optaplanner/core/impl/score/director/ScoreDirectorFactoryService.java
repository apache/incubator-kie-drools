package org.optaplanner.core.impl.score.director;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * All {@link ScoreDirectorFactory} implementations must provide an implementation of this interface,
 * as well as an entry in META-INF/services/org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService file.
 * This makes it available for discovery in {@link ScoreDirectorFactoryFactory} via {@link java.util.ServiceLoader}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 */
public interface ScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>> {

    /**
     *
     * @return never null, the score director type that is implemented by the factory
     */
    ScoreDirectorType getSupportedScoreDirectorType();

    /**
     * Returns a new factory for the score director defined by {@link #getSupportedScoreDirectorType()}.
     *
     * @param classLoader
     * @param solutionDescriptor never null, solution descriptor provided by the solver
     * @param config never null, configuration to use for instantiating the factory
     * @return null when this type is not configured
     * @throws IllegalStateException if the configuration has an issue
     */
    AbstractScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config);

}
