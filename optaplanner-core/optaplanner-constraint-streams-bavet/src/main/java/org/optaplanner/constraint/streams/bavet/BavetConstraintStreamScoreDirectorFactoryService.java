package org.optaplanner.constraint.streams.bavet;

import static org.optaplanner.core.api.score.stream.ConstraintStreamImplType.DROOLS;

import java.util.function.Supplier;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactoryService;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;

public final class BavetConstraintStreamScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        extends AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_> {

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.CONSTRAINT_STREAMS;
    }

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
        ConstraintStreamImplType constraintStreamImplType_ = config.getConstraintStreamImplType();
        if (constraintStreamImplType_ == DROOLS) {
            return null;
        }
        if (config.getConstraintProviderClass() != null) {
            if (!ConstraintProvider.class.isAssignableFrom(config.getConstraintProviderClass())) {
                throw new IllegalArgumentException(
                        "The constraintProviderClass (" + config.getConstraintProviderClass()
                                + ") does not implement " + ConstraintProvider.class.getSimpleName() + ".");
            }
            return () -> {
                ConstraintProvider constraintProvider = ConfigUtils.newInstance(config,
                        "constraintProviderClass", config.getConstraintProviderClass());
                ConfigUtils.applyCustomProperties(constraintProvider, "constraintProviderClass",
                        config.getConstraintProviderCustomProperties(), "constraintProviderCustomProperties");
                return buildScoreDirectorFactory(solutionDescriptor, constraintProvider, false);
            };
        } else {
            if (config.getConstraintProviderCustomProperties() != null) {
                throw new IllegalStateException("If there is no constraintProviderClass (" + config.getConstraintProviderClass()
                        + "), then there can be no constraintProviderCustomProperties ("
                        + config.getConstraintProviderCustomProperties() + ") either.");
            }
            return null;
        }
    }

    @Override
    public boolean supportsImplType(ConstraintStreamImplType constraintStreamImplType) {
        return constraintStreamImplType == ConstraintStreamImplType.BAVET;
    }

    @Override
    public AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor, ConstraintProvider constraintProvider,
            boolean droolsAlphaNetworkCompilationEnabled) {
        if (droolsAlphaNetworkCompilationEnabled) {
            throw new IllegalStateException("With Constraint Streams " + ConstraintStreamImplType.BAVET +
                    ", there can be no droolsAlphaNetworkCompilationEnabled (" + droolsAlphaNetworkCompilationEnabled + ").");
        }
        return new BavetConstraintStreamScoreDirectorFactory<>(solutionDescriptor, constraintProvider);
    }

    @Override
    public ConstraintFactory buildConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        return new BavetConstraintFactory<>(solutionDescriptor);
    }
}
