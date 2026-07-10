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

package org.optaplanner.benchmark.impl.statistic.subsingle.constraintmatchtotalstepscore;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class ConstraintMatchTotalStepScoreSubSingleStatisticTest
        extends
        AbstractSubSingleStatisticTest<ConstraintMatchTotalStepScoreStatisticPoint, ConstraintMatchTotalStepScoreSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, ConstraintMatchTotalStepScoreSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return ConstraintMatchTotalStepScoreSubSingleStatistic::new;
    }

    @Override
    protected List<ConstraintMatchTotalStepScoreStatisticPoint> getInputPoints() {
        return Collections.singletonList(new ConstraintMatchTotalStepScoreStatisticPoint(Long.MAX_VALUE, "CN", "CP",
                Integer.MAX_VALUE, SimpleScore.of(Integer.MAX_VALUE)));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<ConstraintMatchTotalStepScoreStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> Objects.equals(s.getConstraintId(), "CN/CP"), "Constraint IDs do not match.")
                .matches(s -> s.getConstraintMatchCount() == Integer.MAX_VALUE, "Constraint match counts do not match.")
                .matches(s -> s.getScoreTotal().equals(SimpleScore.of(Integer.MAX_VALUE)), "Scores do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
