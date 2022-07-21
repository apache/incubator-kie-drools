package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;
import static org.optaplanner.core.api.score.stream.ConstraintStreamImplType.DROOLS;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactoryService;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

public final class DefaultConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_, Score_ extends Score<Score_>>
        implements ConstraintVerifier<ConstraintProvider_, Solution_> {

    private final ServiceLoader<ScoreDirectorFactoryService> serviceLoader;
    private final ConstraintProvider_ constraintProvider;
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private ConstraintStreamImplType constraintStreamImplType;
    private Boolean droolsAlphaNetworkCompilationEnabled;

    public DefaultConstraintVerifier(ConstraintProvider_ constraintProvider, SolutionDescriptor<Solution_> solutionDescriptor) {
        this.serviceLoader = ServiceLoader.load(ScoreDirectorFactoryService.class);
        this.constraintProvider = constraintProvider;
        this.solutionDescriptor = solutionDescriptor;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return constraintStreamImplType;
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType) {
        requireNonNull(constraintStreamImplType);
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    public boolean isDroolsAlphaNetworkCompilationEnabled() {
        return Objects.requireNonNullElse(droolsAlphaNetworkCompilationEnabled, !ConfigUtils.isNativeImage());
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withDroolsAlphaNetworkCompilationEnabled(
            boolean droolsAlphaNetworkCompilationEnabled) {
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
        return this;
    }

    // ************************************************************************
    // Verify methods
    // ************************************************************************

    @Override
    public DefaultSingleConstraintVerification<Solution_, Score_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        requireNonNull(constraintFunction);
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                createScoreDirectorFactory(constraintFunction);
        return new DefaultSingleConstraintVerification<>(scoreDirectorFactory);
    }

    private AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        ConstraintProvider actualConstraintProvider = constraintFactory -> new Constraint[] {
                constraintFunction.apply(constraintProvider, constraintFactory)
        };
        return createScoreDirectorFactory(actualConstraintProvider);
    }

    private AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(
            ConstraintProvider constraintProvider) {
        List<AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_>> services = serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(s -> s.getSupportedScoreDirectorType() == ScoreDirectorType.CONSTRAINT_STREAMS)
                .map(s -> (AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_>) s)
                .filter(s -> {
                    ConstraintStreamImplType implType = constraintStreamImplType;
                    return implType == null || s.supportsImplType(implType);
                })
                .sorted(Comparator
                        .comparingInt(
                                (AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_> service) -> service
                                        .getPriority())
                        .reversed()) // CS-D will be picked if both are available.
                .collect(Collectors.toList());
        if (services.isEmpty()) {
            throw new IllegalStateException(
                    "Constraint Streams implementation was not found on the classpath.\n"
                            + "Maybe include org.optaplanner:optaplanner-constraint-streams-drools dependency "
                            + "or org.optaplanner:optaplanner-constraint-streams-bavet in your project?\n"
                            + "Maybe ensure your uberjar bundles META-INF/services from included JAR files?");
        }
        AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_> service = services.get(0);
        boolean isDroolsAlphaNetworkCompilationEnabled =
                droolsAlphaNetworkCompilationEnabled == null
                        ? service.supportsImplType(DROOLS) && isDroolsAlphaNetworkCompilationEnabled()
                        : droolsAlphaNetworkCompilationEnabled;
        return service.buildScoreDirectorFactory(solutionDescriptor, constraintProvider,
                isDroolsAlphaNetworkCompilationEnabled);
    }

    @Override
    public DefaultMultiConstraintVerification<Solution_, Score_> verifyThat() {
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                createScoreDirectorFactory(constraintProvider);
        return new DefaultMultiConstraintVerification<>(scoreDirectorFactory, constraintProvider);
    }

}
