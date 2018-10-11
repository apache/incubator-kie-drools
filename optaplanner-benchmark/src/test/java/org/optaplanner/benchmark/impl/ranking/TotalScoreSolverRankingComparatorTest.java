/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class TotalScoreSolverRankingComparatorTest extends AbstractSolverRankingComparatorTest {

    private BenchmarkReport benchmarkReport;
    private TotalScoreSolverRankingComparator comparator;
    private SolverBenchmarkResult a;
    private SolverBenchmarkResult b;
    private List<SingleBenchmarkResult> aSingleBenchmarkResultList;
    private List<SingleBenchmarkResult> bSingleBenchmarkResultList;

    @Before
    public void setUp() {
        benchmarkReport = mock(BenchmarkReport.class);
        comparator = new TotalScoreSolverRankingComparator();
        a = new SolverBenchmarkResult(null);
        b = new SolverBenchmarkResult(null);
        aSingleBenchmarkResultList = new ArrayList<>();
        bSingleBenchmarkResultList = new ArrayList<>();
    }

    @Test
    public void normal() {
        addSingleBenchmark(aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        addSingleBenchmark(bSingleBenchmarkResultList, -1000, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -50, -50, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertCompareToOrder(comparator, a, b);
    }

    @Test
    public void totalIsEqual() {
        addSingleBenchmark(aSingleBenchmarkResultList, -1005, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkResultList, -200, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1005);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        addSingleBenchmark(bSingleBenchmarkResultList, -1000, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -35, -35, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertCompareToOrder(comparator, a, b);
    }

    @Test
    public void differentScoreDefinitions() {
        addSingleBenchmark(aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, 0, -1000, 0, -50, -10, -1000);
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, 0, -200, 0, -50, -10, -1000);
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, -7, -50, 0, -50, -10, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertCompareToOrder(comparator, a, b);
    }

    @Test
    public void uninitializedSingleBenchmarks() {
        SingleBenchmarkResult a0 = addSingleBenchmark(aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        SingleBenchmarkResult b0 = addSingleBenchmark(bSingleBenchmarkResultList, -1000, -30, -1000);
        SingleBenchmarkResult b1 = addSingleBenchmark(bSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -30, -30, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertCompareToEquals(comparator, a, b);

        a0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -1000));
        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -400));
        a.accumulateResults(benchmarkReport);
        b.accumulateResults(benchmarkReport);
        // uninitialized variable count and total score are equal, A is worse on worst score (tie-breaker)
        assertCompareToOrder(comparator, a, b);

        b0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -1000));
        b.accumulateResults(benchmarkReport);
        // uninitialized variable count is bigger in B
        assertCompareToOrder(comparator, b, a);

        b0.setAverageAndTotalScoreForTesting(SimpleScore.of(-1000));
        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-99, -400));
        b.accumulateResults(benchmarkReport);
        // uninitialized variable count is bigger in A
        assertCompareToOrder(comparator, a, b);
    }

}
