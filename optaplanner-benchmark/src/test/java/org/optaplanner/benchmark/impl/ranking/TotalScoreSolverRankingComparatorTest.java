/*
 * Copyright 2012 JBoss Inc
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

import org.junit.Test;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TotalScoreSolverRankingComparatorTest extends AbstractSolverRankingComparatorTest {

    @Test
    public void normal() {
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        TotalScoreSolverRankingComparator comparator = new TotalScoreSolverRankingComparator();
        SolverBenchmarkResult a = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        SolverBenchmarkResult b = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(bSingleBenchmarkResultList, -1000, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -50, -50, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void totalIsEqual() {
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        TotalScoreSolverRankingComparator comparator = new TotalScoreSolverRankingComparator();
        SolverBenchmarkResult a = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(aSingleBenchmarkResultList, -1005, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkResultList, -200, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1005);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        SolverBenchmarkResult b = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(bSingleBenchmarkResultList, -1000, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkResultList, -35, -35, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void differentScoreDefinitions() {
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        TotalScoreSolverRankingComparator comparator = new TotalScoreSolverRankingComparator();
        SolverBenchmarkResult a = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(aSingleBenchmarkResultList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -30, -30, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        SolverBenchmarkResult b = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, 0, -1000, 0, -50, -10, -1000);
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, 0, -200, 0, -50, -10, -1000);
        addSingleBenchmarkWithHardSoftLongScore(bSingleBenchmarkResultList, -7, -50, 0, -50, -10, -1000);
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

}
