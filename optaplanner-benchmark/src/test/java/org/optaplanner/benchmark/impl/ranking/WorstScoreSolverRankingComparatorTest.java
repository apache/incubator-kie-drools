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

public class WorstScoreSolverRankingComparatorTest extends AbstractSolverRankingComparatorTest {

    private BenchmarkReport benchmarkReport;
    private WorstScoreSolverRankingComparator comparator;
    private SolverBenchmarkResult a;
    private SolverBenchmarkResult b;
    private List<SingleBenchmarkResult> aSingleBenchmarkResultList;
    private List<SingleBenchmarkResult> bSingleBenchmarkResultList;

    @Before
    public void setUp() {
        benchmarkReport = mock(BenchmarkReport.class);
        comparator = new WorstScoreSolverRankingComparator();
        a = new SolverBenchmarkResult(null);
        b = new SolverBenchmarkResult(null);
        aSingleBenchmarkResultList = new ArrayList<>();
        bSingleBenchmarkResultList = new ArrayList<>();
    }

    @Test
    public void normal() {
        addSingleBenchmark(aSingleBenchmarkResultList, -100, -30, -2001);
        addSingleBenchmark(aSingleBenchmarkResultList, -2001, -30, -2001);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -2001);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        addSingleBenchmark(bSingleBenchmarkResultList, -900, -30, -2000);
        addSingleBenchmark(bSingleBenchmarkResultList, -2000, -30, -2000);
        addSingleBenchmark(bSingleBenchmarkResultList, -30, -30, -2000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertCompareToOrder(comparator, a, b);
    }

    @Test
    public void worstIsEqual() {
        addSingleBenchmark(aSingleBenchmarkResultList, -101, -30, -2000);
        addSingleBenchmark(aSingleBenchmarkResultList, -2000, -30, -2000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -2000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        addSingleBenchmark(bSingleBenchmarkResultList, -100, -40, -2000);
        addSingleBenchmark(bSingleBenchmarkResultList, -2000, -40, -2000);
        addSingleBenchmark(bSingleBenchmarkResultList, -40, -40, -2000);
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
        b0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, -1000));
        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, 400));
        a.accumulateResults(benchmarkReport);
        b.accumulateResults(benchmarkReport);
        // B is worse on uninitialized variable count in the second worst score
        assertCompareToOrder(comparator, b, a);

        a0.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-101, -1000));
        a.accumulateResults(benchmarkReport);
        // uninitialized variable count in a better score is bigger in A
        assertCompareToOrder(comparator, a, b);

        // uninitialized variable counts are equal, A is worse on score
        b0.setAverageAndTotalScoreForTesting(SimpleScore.of(-1000));
        b1.setAverageAndTotalScoreForTesting(SimpleScore.ofUninitialized(-100, 400));
        b.accumulateResults(benchmarkReport);
        assertCompareToOrder(comparator, a, b);
    }

}
