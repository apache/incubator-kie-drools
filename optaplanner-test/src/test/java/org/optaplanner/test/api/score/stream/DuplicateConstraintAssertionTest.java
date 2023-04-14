package org.optaplanner.test.api.score.stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierDuplicateConstraintProvider;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierExtendedSolution;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierFirstEntity;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierSecondEntity;

class DuplicateConstraintAssertionTest {

    private final ConstraintVerifier<TestdataConstraintVerifierDuplicateConstraintProvider, TestdataConstraintVerifierExtendedSolution> constraintVerifier =
            ConstraintVerifier.build(new TestdataConstraintVerifierDuplicateConstraintProvider(),
                    TestdataConstraintVerifierExtendedSolution.class,
                    TestdataConstraintVerifierFirstEntity.class,
                    TestdataConstraintVerifierSecondEntity.class);

    @Test
    void throwsExceptionOnDuplicateConstraintId() {
        assertThatThrownBy(
                () -> constraintVerifier.verifyThat(TestdataConstraintVerifierDuplicateConstraintProvider::penalizeEveryEntity))
                .hasMessageContaining("Penalize every standard entity");
    }

}
