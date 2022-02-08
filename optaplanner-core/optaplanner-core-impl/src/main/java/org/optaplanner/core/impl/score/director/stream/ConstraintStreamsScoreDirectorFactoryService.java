package org.optaplanner.core.impl.score.director.stream;

import java.util.Objects;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;

public final class ConstraintStreamsScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.CONSTRAINT_STREAMS;
    }

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
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
                ConstraintStreamImplType constraintStreamImplType_ =
                        Objects.requireNonNullElse(config.getConstraintStreamImplType(), ConstraintStreamImplType.DROOLS);
                switch (constraintStreamImplType_) {
                    case BAVET:
                        return new BavetConstraintStreamScoreDirectorFactory<>(solutionDescriptor, constraintProvider);
                    case DROOLS:
                        boolean isDroolsAlphaNetworkEnabled = config.isDroolsAlphaNetworkCompilationEnabled();
                        if (config.getGizmoKieBaseSupplier() != null) {
                            return new DroolsConstraintStreamScoreDirectorFactory<>(solutionDescriptor,
                                    (KieBaseDescriptor<Solution_>) config.getGizmoKieBaseSupplier(),
                                    isDroolsAlphaNetworkEnabled);
                        }
                        return new DroolsConstraintStreamScoreDirectorFactory<>(solutionDescriptor, constraintProvider,
                                isDroolsAlphaNetworkEnabled);
                    default:
                        throw new IllegalStateException(
                                "The constraintStreamImplType (" + constraintStreamImplType_ + ") is not implemented.");
                }
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
}
