package org.optaplanner.examples.common.score;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests using {@link org.optaplanner.test.api.score.stream.ConstraintVerifier} should use this annotation
 * instead of @{@link org.junit.jupiter.api.Test}.
 * This brings several benefits, such as parallel execution and testing on both Bavet and Drools.
 *
 * <p>
 * Each such test expects exactly one argument of type {@link org.optaplanner.test.api.score.stream.ConstraintVerifier}.
 * Values for that argument are read from {@link AbstractConstraintProviderTest#getDroolsAndBavetConstraintVerifierImpls()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Execution(ExecutionMode.CONCURRENT)
@ParameterizedTest(name = "constraintStreamImplType = {0}")
@MethodSource("getDroolsAndBavetConstraintVerifierImpls")
public @interface ConstraintProviderTest {
}
