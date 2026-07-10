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

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierConstraintProvider;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierExtendedSolution;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierFirstEntity;
import org.optaplanner.test.api.score.stream.testdata.TestdataConstraintVerifierSecondEntity;

class MultiConstraintAssertionTest {

    private final ConstraintVerifier<TestdataConstraintVerifierConstraintProvider, TestdataConstraintVerifierExtendedSolution> constraintVerifier =
            ConstraintVerifier.build(new TestdataConstraintVerifierConstraintProvider(),
                    TestdataConstraintVerifierExtendedSolution.class,
                    TestdataConstraintVerifierFirstEntity.class,
                    TestdataConstraintVerifierSecondEntity.class);

    @Test
    void checksScore() {
        TestdataConstraintVerifierExtendedSolution solution = TestdataConstraintVerifierExtendedSolution.generateSolution(4, 5);

        assertThatCode(() -> constraintVerifier.verifyThat()
                .givenSolution(solution)
                .scores(HardSoftScore.of(-15, 3), "There should be no penalties"))
                .doesNotThrowAnyException();
        assertThatCode(() -> constraintVerifier.verifyThat()
                .givenSolution(solution)
                .scores(HardSoftScore.of(1, 1), "There should be penalties"))
                .hasMessageContaining("There should be penalties");
    }

}
