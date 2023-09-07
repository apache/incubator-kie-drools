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
