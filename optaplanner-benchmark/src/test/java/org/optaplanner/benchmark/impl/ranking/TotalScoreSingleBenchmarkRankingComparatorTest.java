/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCompareToOrder;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;

public class TotalScoreSingleBenchmarkRankingComparatorTest {

    @Test
    public void compareTo() {
        SolverBenchmarkResult solverBenchmarkResult = mock(SolverBenchmarkResult.class);
        when(solverBenchmarkResult.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        TotalScoreSingleBenchmarkRankingComparator comparator = new TotalScoreSingleBenchmarkRankingComparator();
        SingleBenchmarkResult a = new SingleBenchmarkResult(solverBenchmarkResult, mock(ProblemBenchmarkResult.class));
        a.setFailureCount(1);
        a.setAverageAndTotalScoreForTesting(null);
        SingleBenchmarkResult b = new SingleBenchmarkResult(solverBenchmarkResult, mock(ProblemBenchmarkResult.class));
        b.setFailureCount(0);
        b.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-7, -1));
        SingleBenchmarkResult c = new SingleBenchmarkResult(solverBenchmarkResult, mock(ProblemBenchmarkResult.class));
        c.setFailureCount(0);
        c.setAverageAndTotalScoreForTesting(SimpleScore.of(-300));
        when(solverBenchmarkResult.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        SingleBenchmarkResult d = new SingleBenchmarkResult(solverBenchmarkResult, mock(ProblemBenchmarkResult.class));
        d.setFailureCount(0);
        d.setAverageAndTotalScoreForTesting(SimpleScore.of(-20));
        assertCompareToOrder(comparator, a, b, c, d);
    }

}
