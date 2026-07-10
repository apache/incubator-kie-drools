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

package org.optaplanner.benchmark.impl.statistic.scorecalculationspeed;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class ScoreCalculationSpeedSubSingleStatisticTest
        extends
        AbstractSubSingleStatisticTest<ScoreCalculationSpeedStatisticPoint, ScoreCalculationSpeedSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, ScoreCalculationSpeedSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return ScoreCalculationSpeedSubSingleStatistic::new;
    }

    @Override
    protected List<ScoreCalculationSpeedStatisticPoint> getInputPoints() {
        return Collections.singletonList(new ScoreCalculationSpeedStatisticPoint(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<ScoreCalculationSpeedStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> s.getScoreCalculationSpeed() == Long.MAX_VALUE, "Score calculation speeds do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
