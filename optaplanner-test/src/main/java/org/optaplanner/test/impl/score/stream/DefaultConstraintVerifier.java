package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

public final class DefaultConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_, Score_ extends Score<Score_>>
        implements ConstraintVerifier<ConstraintProvider_, Solution_> {

    private final ConstraintProvider_ constraintProvider;
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    /**
     * {@link ConstraintVerifier} is mutable,
     * due to {@link #withConstraintStreamImplType(ConstraintStreamImplType)} and
     * {@link #withDroolsAlphaNetworkCompilationEnabled(boolean)}.
     * Since these methods can be run at any time, possibly invalidating the pre-built score director factories,
     * the easiest way of dealing with the issue is to keep an internal immutable constraint verifier instance
     * and clearing it every time the configuration changes.
     * The code that was using the old configuration will continue running on the old instance,
     * which will eventually be garbage-collected.
     * Any new code will get a new instance with the new configuration applied.
     */
    private final AtomicReference<ConfiguredConstraintVerifier<ConstraintProvider_, Solution_, Score_>> configuredConstraintVerifierRef =
            new AtomicReference<>();
    private final AtomicReference<ConstraintStreamImplType> constraintStreamImplTypeRef = new AtomicReference<>();
    private final AtomicReference<Boolean> droolsAlphaNetworkCompilationEnabledRef = new AtomicReference<>();

    public DefaultConstraintVerifier(ConstraintProvider_ constraintProvider, SolutionDescriptor<Solution_> solutionDescriptor) {
        this.constraintProvider = constraintProvider;
        this.solutionDescriptor = solutionDescriptor;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return constraintStreamImplTypeRef.get();
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType) {
        requireNonNull(constraintStreamImplType);
        var droolsAlphaNetworkCompilationEnabled = this.droolsAlphaNetworkCompilationEnabledRef.get();
        if (droolsAlphaNetworkCompilationEnabled != null &&
                droolsAlphaNetworkCompilationEnabled &&
                constraintStreamImplType != ConstraintStreamImplType.DROOLS) {
            throw new IllegalArgumentException("Can not switch to " + ConstraintStreamImplType.class.getSimpleName()
                    + "." + constraintStreamImplType + " while Drools Alpha Network Compilation enabled.");
        }
        this.constraintStreamImplTypeRef.set(constraintStreamImplType);
        this.configuredConstraintVerifierRef.set(null);
        return this;
    }

    public boolean isDroolsAlphaNetworkCompilationEnabled() {
        return Objects.requireNonNullElse(droolsAlphaNetworkCompilationEnabledRef.get(), !ConfigUtils.isNativeImage());
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withDroolsAlphaNetworkCompilationEnabled(
            boolean droolsAlphaNetworkCompilationEnabled) {
        if (droolsAlphaNetworkCompilationEnabled && getConstraintStreamImplType() == ConstraintStreamImplType.BAVET) {
            throw new IllegalArgumentException("Can not enable Drools Alpha Network Compilation with "
                    + ConstraintStreamImplType.class.getSimpleName() + "." + ConstraintStreamImplType.BAVET + ".");
        }
        this.droolsAlphaNetworkCompilationEnabledRef.set(droolsAlphaNetworkCompilationEnabled);
        this.configuredConstraintVerifierRef.set(null);
        return this;
    }

    // ************************************************************************
    // Verify methods
    // ************************************************************************

    @Override
    public DefaultSingleConstraintVerification<Solution_, Score_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        return getOrCreateConfiguredConstraintVerifier().verifyThat(constraintFunction);
    }

    private ConfiguredConstraintVerifier<ConstraintProvider_, Solution_, Score_> getOrCreateConfiguredConstraintVerifier() {
        return configuredConstraintVerifierRef.updateAndGet(v -> {
            if (v == null) {
                return new ConfiguredConstraintVerifier<>(constraintProvider, solutionDescriptor,
                        getConstraintStreamImplType(), isDroolsAlphaNetworkCompilationEnabled());
            }
            return v;
        });
    }

    @Override
    public DefaultMultiConstraintVerification<Solution_, Score_> verifyThat() {
        return getOrCreateConfiguredConstraintVerifier().verifyThat();
    }

}
