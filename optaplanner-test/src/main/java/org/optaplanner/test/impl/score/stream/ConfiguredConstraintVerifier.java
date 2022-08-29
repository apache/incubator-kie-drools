package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * Represents a {@link org.optaplanner.test.api.score.stream.ConstraintVerifier} with pre-set values
 * for {@link #getConstraintStreamImplType()} and {@link #getDroolsAlphaNetworkCompilationEnabled()}.
 * A new instance of this class will be created should either of these change.
 *
 * <p>
 * This class still needs to be thread-safe,
 * as it handles {@link java.util.ServiceLoader} and score director factories.
 *
 * @param <ConstraintProvider_>
 * @param <Solution_>
 * @param <Score_>
 */
final class ConfiguredConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_, Score_ extends Score<Score_>> {

    /**
     * Exists so that people can not, even by accident, pick the same constraint ID as the default cache key.
     */
    private final String defaultScoreDirectorFactoryMapKey = UUID.randomUUID().toString();

    private final ConstraintProvider_ constraintProvider;
    /**
     * {@link java.util.ServiceLoader} is sensitive to classloaders.
     * Therefore we ensure that all SPI operations are bound to the current thread's classloader.
     * This also allows us to avoid thread safety concerns in the {@link ScoreDirectorFactoryCache}.
     */
    @SuppressWarnings("java:S5164") // Suppress SonarCloud warning; this is safe in the context of tests.
    private final ThreadLocal<ScoreDirectorFactoryCache<ConstraintProvider_, Solution_, Score_>> scoreDirectorFactoryContainerThreadLocal;

    private final ConstraintStreamImplType constraintStreamImplType;
    private final boolean droolsAlphaNetworkCompilationEnabled;

    public ConfiguredConstraintVerifier(ConstraintProvider_ constraintProvider,
            SolutionDescriptor<Solution_> solutionDescriptor, ConstraintStreamImplType constraintStreamImplType,
            boolean droolsAlphaNetworkCompilationEnabled) {
        this.constraintProvider = constraintProvider;
        this.scoreDirectorFactoryContainerThreadLocal =
                ThreadLocal.withInitial(() -> new ScoreDirectorFactoryCache<>(this, solutionDescriptor));
        this.constraintStreamImplType = constraintStreamImplType;
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return constraintStreamImplType;
    }

    public boolean getDroolsAlphaNetworkCompilationEnabled() {
        return droolsAlphaNetworkCompilationEnabled;
    }

    public DefaultSingleConstraintVerification<Solution_, Score_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        requireNonNull(constraintFunction);
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                scoreDirectorFactoryContainerThreadLocal.get().getScoreDirectorFactory(constraintFunction, constraintProvider);
        return new DefaultSingleConstraintVerification<>(scoreDirectorFactory);
    }

    public DefaultMultiConstraintVerification<Solution_, Score_> verifyThat() {
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                scoreDirectorFactoryContainerThreadLocal.get()
                        .getScoreDirectorFactory(defaultScoreDirectorFactoryMapKey, constraintProvider);
        return new DefaultMultiConstraintVerification<>(scoreDirectorFactory, constraintProvider);
    }

}
