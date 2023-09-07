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

package org.optaplanner.core.api.domain.valuerange;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.valuerange.anonymous.TestdataAnonymousArraySolution;
import org.optaplanner.core.impl.testdata.domain.valuerange.anonymous.TestdataAnonymousListSolution;
import org.optaplanner.core.impl.testdata.domain.valuerange.anonymous.TestdataAnonymousValueRangeEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.anonymous.TestdataAnonymousValueRangeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class AnonymousValueRangeFactoryTest {

    @Test
    void solveValueRange() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataAnonymousValueRangeSolution.class, TestdataAnonymousValueRangeEntity.class);

        TestdataAnonymousValueRangeSolution solution = new TestdataAnonymousValueRangeSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataAnonymousValueRangeEntity("e1"),
                new TestdataAnonymousValueRangeEntity("e2")));

        TestdataAnonymousValueRangeSolution result = PlannerTestUtils.solve(solverConfig, solution);
        TestdataAnonymousValueRangeEntity entity1 = result.getEntityList().get(0);
        TestdataAnonymousValueRangeEntity entity2 = result.getEntityList().get(1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getScore()).isEqualTo(SimpleScore.ZERO);
            assertEntity(softly, entity1);
            assertEntity(softly, entity2);
        });
        assertThat(solution).isNotNull();
    }

    @Test
    void solveArray() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataAnonymousArraySolution.class, TestdataAnonymousValueRangeEntity.class);

        TestdataAnonymousArraySolution solution = new TestdataAnonymousArraySolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataAnonymousValueRangeEntity("e1"),
                new TestdataAnonymousValueRangeEntity("e2")));

        TestdataAnonymousArraySolution result = PlannerTestUtils.solve(solverConfig, solution);
        TestdataAnonymousValueRangeEntity entity1 = result.getEntityList().get(0);
        TestdataAnonymousValueRangeEntity entity2 = result.getEntityList().get(1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getScore()).isEqualTo(SimpleScore.ZERO);
            assertEntity(softly, entity1);
            assertEntity(softly, entity2);
        });
        assertThat(solution).isNotNull();
    }

    @Test
    void solveList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataAnonymousListSolution.class, TestdataAnonymousValueRangeEntity.class);

        TestdataAnonymousListSolution solution = new TestdataAnonymousListSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataAnonymousValueRangeEntity("e1"),
                new TestdataAnonymousValueRangeEntity("e2")));

        TestdataAnonymousListSolution result = PlannerTestUtils.solve(solverConfig, solution);
        TestdataAnonymousValueRangeEntity entity1 = result.getEntityList().get(0);
        TestdataAnonymousValueRangeEntity entity2 = result.getEntityList().get(1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getScore()).isEqualTo(SimpleScore.ZERO);
            assertEntity(softly, entity1);
            assertEntity(softly, entity2);
        });
        assertThat(solution).isNotNull();
    }

    private void assertEntity(SoftAssertions softly, TestdataAnonymousValueRangeEntity entity) {
        softly.assertThat(entity.getNumberValue()).isNotNull();
        softly.assertThat(entity.getIntegerValue()).isNotNull();
        softly.assertThat(entity.getLongValue()).isNotNull();
        softly.assertThat(entity.getBigIntegerValue()).isNotNull();
        softly.assertThat(entity.getBigDecimalValue()).isNotNull();
    }

}
