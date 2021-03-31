/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.test.api.score.stream;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.planningid.TestdataStringPlanningIdEntity;
import org.optaplanner.core.impl.testdata.domain.planningid.TestdataStringPlanningIdSolution;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintProvider;

public class SingleConstraintAssertionTest {

    private final ConstraintVerifier<TestdataConstraintProvider, TestdataStringPlanningIdSolution> constraintVerifier =
            ConstraintVerifier.build(new TestdataConstraintProvider(), TestdataStringPlanningIdSolution.class,
                    TestdataEntity.class,
                    TestdataStringPlanningIdEntity.class);

    @Test
    void penalizesAndDoesNotReward() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .penalizes("There should be penalties.");
        }).doesNotThrowAnyException();
        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .rewards("There should be rewards");
        }).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }

    @Test
    void rewardsButDoesNotPenalize() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .rewards("There should be rewards");
        }).doesNotThrowAnyException();
        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .penalizes("There should be penalties.");
        }).hasMessageContaining("There should be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void penalizesByCountAndDoesNotReward() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .penalizes(3, "There should be penalties.");
        }).doesNotThrowAnyException();
        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::penalizeEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .rewards(1, "There should be rewards");
        }).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }

    @Test
    void rewardsByCountButDoesNotPenalize() {
        TestdataSolution solution = TestdataSolution.generateSolution(2, 3);

        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .rewards(3, "There should be rewards");
        }).doesNotThrowAnyException();
        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::rewardEveryEntity)
                    .given(solution.getEntityList().toArray())
                    .penalizes(1, "There should be penalties.");
        }).hasMessageContaining("There should be penalties")
                .hasMessageContaining("Expected penalty");
    }

    @Test
    void uniquePairShouldWorkOnStringPlanningId() {
        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::differentStringEntityHaveDifferentValues)
                    .given(new TestdataStringPlanningIdEntity("A", "1"),
                            new TestdataStringPlanningIdEntity("B", "1"))
                    .penalizes(1, "There should be penalties");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            constraintVerifier.verifyThat(TestdataConstraintProvider::differentStringEntityHaveDifferentValues)
                    .given(new TestdataStringPlanningIdEntity("A", "1"),
                            new TestdataStringPlanningIdEntity("B", "1"))
                    .rewards(1, "There should be rewards");
        }).hasMessageContaining("There should be rewards")
                .hasMessageContaining("Expected reward");
    }
}
