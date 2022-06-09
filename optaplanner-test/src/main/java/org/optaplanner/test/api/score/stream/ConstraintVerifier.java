package org.optaplanner.test.api.score.stream;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.test.impl.score.stream.DefaultConstraintVerifier;

public interface ConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_> {

    /**
     * Entry point to the API.
     *
     * @param constraintProvider never null, {@link PlanningEntity} used by the {@link PlanningSolution}
     * @param planningSolutionClass never null, {@link PlanningSolution}-annotated class associated with the constraints
     * @param entityClasses never null, at least one, {@link PlanningEntity} types used by the {@link PlanningSolution}
     * @param <ConstraintProvider_> type of the {@link ConstraintProvider}
     * @param <Solution_> type of the {@link PlanningSolution}-annotated class
     * @return never null
     */
    static <ConstraintProvider_ extends ConstraintProvider, Solution_> ConstraintVerifier<ConstraintProvider_, Solution_> build(
            ConstraintProvider_ constraintProvider,
            Class<Solution_> planningSolutionClass, Class<?>... entityClasses) {
        requireNonNull(constraintProvider);
        SolutionDescriptor<Solution_> solutionDescriptor = SolutionDescriptor
                .buildSolutionDescriptor(requireNonNull(planningSolutionClass), entityClasses);
        return new DefaultConstraintVerifier<>(constraintProvider, solutionDescriptor);
    }

    /**
     * Uses a {@link SolverConfig} to build a {@link ConstraintVerifier}.
     * Alternative to {@link #build(ConstraintProvider, Class, Class[])}.
     *
     * @param solverConfig never null, must have a {@link PlanningSolution} class, {@link PlanningEntity} classes
     *        and a {@link ConstraintProvider} configured.
     * @param <ConstraintProvider_> type of the {@link ConstraintProvider}
     * @param <Solution_> type of the {@link PlanningSolution}-annotated class
     * @return never null
     */
    static <ConstraintProvider_ extends ConstraintProvider, Solution_> ConstraintVerifier<ConstraintProvider_, Solution_>
            create(SolverConfig solverConfig) {
        requireNonNull(solverConfig);
        SolutionDescriptor<Solution_> solutionDescriptor = SolutionDescriptor
                .buildSolutionDescriptor(requireNonNull((Class<Solution_>) solverConfig.getSolutionClass()),
                        solverConfig.getEntityClassList().toArray(new Class<?>[] {}));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = requireNonNull(solverConfig.getScoreDirectorFactoryConfig());
        ConstraintProvider_ constraintProvider = ConfigUtils.newInstance(null, "constraintProviderClass",
                (Class<ConstraintProvider_>) scoreDirectorFactoryConfig.getConstraintProviderClass());
        ConfigUtils.applyCustomProperties(constraintProvider, "constraintProviderClass",
                scoreDirectorFactoryConfig.getConstraintProviderCustomProperties(), "constraintProviderCustomProperties");

        DefaultConstraintVerifier<ConstraintProvider_, Solution_, ?> constraintVerifier =
                new DefaultConstraintVerifier<>(constraintProvider, solutionDescriptor);
        if (scoreDirectorFactoryConfig.getConstraintStreamImplType() != null) {
            constraintVerifier.withConstraintStreamImplType(
                    scoreDirectorFactoryConfig.getConstraintStreamImplType());
        }
        if (scoreDirectorFactoryConfig.getDroolsAlphaNetworkCompilationEnabled() != null) {
            constraintVerifier.withDroolsAlphaNetworkCompilationEnabled(
                    scoreDirectorFactoryConfig.getDroolsAlphaNetworkCompilationEnabled());
        }
        return constraintVerifier;
    }

    /**
     * All subsequent calls to {@link #verifyThat(BiFunction)} and {@link #verifyThat()}
     * use the given {@link ConstraintStreamImplType}.
     *
     * @param constraintStreamImplType never null
     * @return this
     */
    ConstraintVerifier<ConstraintProvider_, Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType);

    /**
     * Applies only to {@link ConstraintStreamImplType#DROOLS}.
     * Do not enable when running in a native image.
     *
     * @param droolsAlphaNetworkCompilationEnabled true to enable the alpha network compiler
     * @return this
     */
    ConstraintVerifier<ConstraintProvider_, Solution_> withDroolsAlphaNetworkCompilationEnabled(
            boolean droolsAlphaNetworkCompilationEnabled);

    /**
     * Creates a constraint verifier for a given {@link Constraint} of the {@link ConstraintProvider}.
     *
     * @param constraintFunction never null
     * @return never null
     */
    SingleConstraintVerification<Solution_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction);

    /**
     * Creates a constraint verifier for all constraints of the {@link ConstraintProvider}.
     *
     * @return never null
     */
    MultiConstraintVerification<Solution_> verifyThat();

}
