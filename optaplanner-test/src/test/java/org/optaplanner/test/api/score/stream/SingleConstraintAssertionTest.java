/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.test.api.score.stream;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierConstraintProvider;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierExtendedSolution;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierFirstEntity;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierSecondEntity;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierSolution;

class SingleConstraintAssertionTest {

    private final ConstraintVerifier<TestdataConstraintVerifierConstraintProvider, TestdataConstraintVerifierExtendedSolution> constraintVerifier =
            ConstraintVerifier.build(new TestdataConstraintVerifierConstraintProvider(),
                    TestdataConstraintVerifierExtendedSolution.class,
                    TestdataConstraintVerifierFirstEntity.class,
                    TestdataConstraintVerifierSecondEntity.class);

    @Test
    void penalizesAndDoesNotReward() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes("There should be penalties.")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards("There should be rewards")).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }

    @Test
    void rewardsButDoesNotPenalize() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards("There should be rewards")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes("There should be penalties.")).hasMessageContaining("There should be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void impacts() {
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given()
                .penalizes(0, "There should be no penalties"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizes(0, "There should be no penalties"))
                .hasMessageContaining("There should be no penalties")
                .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizes(1, "There should be penalties"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizes(2, "There should only be one penalty"))
                .hasMessageContaining("There should only be one penalty")
                .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .rewards(1, "There should not be rewards"))
                .hasMessageContaining("There should not be rewards")
                .hasMessageContaining("Expected reward");

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given()
                .rewards(0, "There should be no rewards"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewards(0, "There should be no rewards"))
                .hasMessageContaining("There should be no rewards")
                .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewards(1, "There should be rewards"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewards(2, "There should only be one reward"))
                .hasMessageContaining("There should only be one reward")
                .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .penalizes(1, "There should not be penalties"))
                .hasMessageContaining("There should not be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void impactsBy() {
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given()
                .penalizesBy(0, "There should no penalties"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizesBy(0, "There should be no penalties"))
                .hasMessageContaining("There should be no penalties")
                .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizesBy(1, "There should be penalties"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .penalizesBy(2, "There should only be one penalty"))
                .hasMessageContaining("There should only be one penalty")
                .hasMessageContaining("Expected penalty");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("A", new TestdataValue()))
                .rewardsWith(1, "There should not be rewards"))
                .hasMessageContaining("There should not be rewards")
                .hasMessageContaining("Expected reward");

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given()
                .rewardsWith(0, "There should no rewards"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewardsWith(0, "There should be no rewards"))
                .hasMessageContaining("There should be no rewards")
                .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewardsWith(1, "There should be rewards"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .rewardsWith(2, "There should only be one reward"))
                .hasMessageContaining("There should only be one reward")
                .hasMessageContaining("Expected reward");
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::impactEveryEntity)
                .given(new TestdataConstraintVerifierFirstEntity("B", new TestdataValue()))
                .penalizesBy(1, "There should not be penalties"))
                .hasMessageContaining("There should not be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void penalizesByCountAndDoesNotReward() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes(3, "There should be penalties.")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards(1, "There should be rewards")).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }

    @Test
    void penalizesByBigDecimal() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizesBy(BigDecimal.valueOf(3), "There should be penalties.")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::penalizeEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizesBy(new BigDecimal("3.01"), "There should be penalties."))
                .hasMessageContaining("There should be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void rewardsByCountButDoesNotPenalize() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewards(3, "There should be rewards")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .penalizes(1, "There should be penalties.")).hasMessageContaining("There should be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void rewardsByBigDecimal() {
        TestdataConstraintVerifierSolution solution = TestdataConstraintVerifierSolution.generateSolution(2, 3);

        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewardsWith(BigDecimal.valueOf(3), "There should be rewards")).doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat(TestdataConstraintVerifierConstraintProvider::rewardEveryEntity)
                .given(solution.getEntityList().toArray())
                .rewardsWith(new BigDecimal("3.01"), "There should be rewards."))
                .hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }

    @Test
    void uniquePairShouldWorkOnStringPlanningId() {
        assertThatCode(() -> constraintVerifier
                .verifyThat(TestdataConstraintVerifierConstraintProvider::differentStringEntityHaveDifferentValues)
                .given(new TestdataConstraintVerifierSecondEntity("A", "1"),
                        new TestdataConstraintVerifierSecondEntity("B", "1"))
                .penalizes(1, "There should be penalties")).doesNotThrowAnyException();

        assertThatCode(() -> constraintVerifier
                .verifyThat(TestdataConstraintVerifierConstraintProvider::differentStringEntityHaveDifferentValues)
                .given(new TestdataConstraintVerifierSecondEntity("A", "1"),
                        new TestdataConstraintVerifierSecondEntity("B", "1"))
                .rewards(1, "There should be rewards")).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }
}
