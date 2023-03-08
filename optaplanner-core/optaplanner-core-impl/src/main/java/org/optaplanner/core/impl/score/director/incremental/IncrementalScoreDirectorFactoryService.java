package org.optaplanner.core.impl.score.director.incremental;

import java.util.function.Supplier;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;

public final class IncrementalScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.INCREMENTAL;
    }

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config,
            EnvironmentMode environmentMode) {
        if (config.getIncrementalScoreCalculatorClass() != null) {
            if (!IncrementalScoreCalculator.class.isAssignableFrom(config.getIncrementalScoreCalculatorClass())) {
                throw new IllegalArgumentException(
                        "The incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + ") does not implement " + IncrementalScoreCalculator.class.getSimpleName() + ".");
            }
            return () -> new IncrementalScoreDirectorFactory<>(solutionDescriptor, () -> {
                IncrementalScoreCalculator<Solution_, Score_> incrementalScoreCalculator = ConfigUtils.newInstance(config,
                        "incrementalScoreCalculatorClass", config.getIncrementalScoreCalculatorClass());
                ConfigUtils.applyCustomProperties(incrementalScoreCalculator, "incrementalScoreCalculatorClass",
                        config.getIncrementalScoreCalculatorCustomProperties(), "incrementalScoreCalculatorCustomProperties");
                return incrementalScoreCalculator;
            });
        } else {
            if (config.getIncrementalScoreCalculatorCustomProperties() != null) {
                throw new IllegalStateException(
                        "If there is no incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + "), then there can be no incrementalScoreCalculatorCustomProperties ("
                                + config.getIncrementalScoreCalculatorCustomProperties() + ") either.");
            }
            return null;
        }
    }
}
