/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.ranking;

import org.junit.Test;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class ScoreSubSingleBenchmarkRankingComparatorTest {

    @Test
    public void compareTo() {
        ScoreSubSingleBenchmarkRankingComparator comparator = new ScoreSubSingleBenchmarkRankingComparator();
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(mock(SolverBenchmarkResult.class), mock(ProblemBenchmarkResult.class));
        SubSingleBenchmarkResult a = new SubSingleBenchmarkResult(singleBenchmarkResult, 0);
        a.setSucceeded(false);
        a.setScore(null);
        SubSingleBenchmarkResult b = new SubSingleBenchmarkResult(singleBenchmarkResult, 1);
        b.setSucceeded(true);
        b.setScore(SimpleScore.ofUninitialized(-7, -1));
        SubSingleBenchmarkResult c = new SubSingleBenchmarkResult(singleBenchmarkResult, 2);
        c.setSucceeded(true);
        c.setScore(SimpleScore.of(-300));
        SubSingleBenchmarkResult d = new SubSingleBenchmarkResult(singleBenchmarkResult, 3);
        d.setSucceeded(true);
        d.setScore(SimpleScore.of(-20));
        assertCompareToOrder(comparator, a, b, c, d);
    }

}
