package org.optaplanner.test.api.score.stream;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.planningid.TestdataStringPlanningIdEntity;
import org.optaplanner.core.impl.testdata.domain.planningid.TestdataStringPlanningIdSolution;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintProvider;

class SingleConstraintAssertionTest {

    private final ConstraintVerifier<TestdataConstraintProvider, TestdataStringPlanningIdSolution> constraintVerifier =
            ConstraintVerifier.build(new TestdataConstraintProvider(), TestdataStringPlanningIdSolution.class,
                    TestdataEntity.class,
                    TestdataStringPlanningIdEntity.class);

    @Test
    void penalizesAndDoesNotReward() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes("There should be penalties.")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards("There should be rewards")).hasMessageContaining("There should be rewards")
                        .hasMessageContaining("Expected reward");
    }

    @Test
    void rewardsButDoesNotPenalize() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards("There should be rewards")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes("There should be penalties.")).hasMessageContaining("There should be penalties")
                        .hasMessageContaining("Expected penalty");
    }

    @Test
    void impacts() {
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given()
                .penalizes(0, "There should be no penalties"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizes(0, "There should be no penalties"))
                        .hasMessageContaining("There should be no penalties")
                        .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizes(1, "There should be penalties"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizes(2, "There should only be one penalty"))
                        .hasMessageContaining("There should only be one penalty")
                        .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .rewards(1, "There should not be rewards"))
                        .hasMessageContaining("There should not be rewards")
                        .hasMessageContaining("Expected reward");

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given()
                .rewards(0, "There should be no rewards"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewards(0, "There should be no rewards"))
                        .hasMessageContaining("There should be no rewards")
                        .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewards(1, "There should be rewards"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewards(2, "There should only be one reward"))
                        .hasMessageContaining("There should only be one reward")
                        .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .penalizes(1, "There should not be penalties"))
                        .hasMessageContaining("There should not be penalties")
                        .hasMessageContaining("Expected penalty");
    }

    @Test
    void impactsBy() {
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given()
                .penalizesBy(0, "There should no penalties"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizesBy(0, "There should be no penalties"))
                        .hasMessageContaining("There should be no penalties")
                        .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizesBy(1, "There should be penalties"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .penalizesBy(2, "There should only be one penalty"))
                        .hasMessageContaining("There should only be one penalty")
                        .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("A", new TestdataValue()))
                .rewardsWith(1, "There should not be rewards"))
                        .hasMessageContaining("There should not be rewards")
                        .hasMessageContaining("Expected reward");

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given()
                .rewardsWith(0, "There should no rewards"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewardsWith(0, "There should be no rewards"))
                        .hasMessageContaining("There should be no rewards")
                        .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewardsWith(1, "There should be rewards"))
                        .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .rewardsWith(2, "There should only be one reward"))
                        .hasMessageContaining("There should only be one reward")
                        .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::impactEveryEntity)
                .given(new TestdataEntity("B", new TestdataValue()))
                .penalizesBy(1, "There should not be penalties"))
                        .hasMessageContaining("There should not be penalties")
                        .hasMessageContaining("Expected penalty");
    }

    @Test
    void penalizesByCountAndDoesNotReward() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes(3, "There should be penalties.")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards(1, "There should be rewards")).hasMessageContaining("There should be rewards")
                        .hasMessageContaining("Expected reward");
    }

    @Test
    void rewardsByCountButDoesNotPenalize() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards(3, "There should be rewards")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes(1, "There should be penalties.")).hasMessageContaining("There should be penalties")
                        .hasMessageContaining("Expected penalty");
    }

    @Test
    void uniquePairShouldWorkOnStringPlanningId() {
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::differentStringEntityHaveDifferentValues)
                .given(new TestdataStringPlanningIdEntity("A", "1"),
                        new TestdataStringPlanningIdEntity("B", "1"))
                .penalizes(1, "There should be penalties")).doesNotThrowAnyException();

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintProvider::differentStringEntityHaveDifferentValues)
                .given(new TestdataStringPlanningIdEntity("A", "1"),
                        new TestdataStringPlanningIdEntity("B", "1"))
                .rewards(1, "There should be rewards")).hasMessageContaining("There should be rewards")
                        .hasMessageContaining("Expected reward");
    }
}
