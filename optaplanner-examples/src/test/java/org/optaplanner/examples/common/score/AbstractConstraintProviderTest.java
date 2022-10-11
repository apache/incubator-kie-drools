package org.optaplanner.examples.common.score;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.testutil.DisabledInProductizationCheck;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

/**
 * @see ConstraintProviderTest
 */
@TestInstance(PER_CLASS)
@DisplayNameGeneration(SimplifiedTestNameGenerator.class)
public abstract class AbstractConstraintProviderTest<ConstraintProvider_ extends ConstraintProvider, Solution_> {

    private final ConstraintVerifier<ConstraintProvider_, Solution_> bavetConstraintVerifier = createConstraintVerifier()
            .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
    private final ConstraintVerifier<ConstraintProvider_, Solution_> droolsWithoutAncConstraintVerifier =
            createConstraintVerifier()
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                    .withDroolsAlphaNetworkCompilationEnabled(false);
    private final ConstraintVerifier<ConstraintProvider_, Solution_> droolsWithAncConstraintVerifier =
            createConstraintVerifier()
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                    .withDroolsAlphaNetworkCompilationEnabled(true);

    protected abstract ConstraintVerifier<ConstraintProvider_, Solution_> createConstraintVerifier();

    protected final Stream<? extends Arguments> getDroolsAndBavetConstraintVerifierImpls() {
        if (DisabledInProductizationCheck.isProductized()) {
            return Stream.of(
                    arguments(named("DROOLS (without ANC)", droolsWithoutAncConstraintVerifier)),
                    arguments(named("DROOLS (with ANC)", droolsWithAncConstraintVerifier)));
        } else {
            return Stream.of(
                    arguments(named("BAVET", bavetConstraintVerifier)),
                    arguments(named("DROOLS (without ANC)", droolsWithoutAncConstraintVerifier)),
                    arguments(named("DROOLS (with ANC)", droolsWithAncConstraintVerifier)));
        }
    }
}
