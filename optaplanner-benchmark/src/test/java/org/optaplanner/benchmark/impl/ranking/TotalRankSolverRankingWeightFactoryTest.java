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

public class TotalRankSolverRankingWeightFactoryTest extends AbstractSolverRankingComparatorTest {

    @Test
    public void normal() {
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        TotalRankSolverRankingWeightFactory factory = new TotalRankSolverRankingWeightFactory();
        List<SolverBenchmarkResult> solverBenchmarkResultList = new ArrayList<SolverBenchmarkResult>();
        SolverBenchmarkResult a = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(aSingleBenchmarkResultList, -1000, -40, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -300, -40, -1000);
        addSingleBenchmark(aSingleBenchmarkResultList, -40, -40, -1000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList);
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        SolverBenchmarkResult b = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(bSingleBenchmarkResultList, -2000, -30, -2000); // Loses vs a
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -30, -2000); // Wins vs a
        addSingleBenchmark(bSingleBenchmarkResultList, -30, -30, -2000); // Wins vs a
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList);
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);

        assertEquals(-1, aWeight.compareTo(bWeight));
        assertEquals(1, bWeight.compareTo(aWeight));
    }

    @Test
    public void equalCount() {
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        TotalRankSolverRankingWeightFactory factory = new TotalRankSolverRankingWeightFactory();
        List<SolverBenchmarkResult> solverBenchmarkResultList = new ArrayList<SolverBenchmarkResult>();
        SolverBenchmarkResult a = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> aSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(aSingleBenchmarkResultList, -5000, -90, -5000);
        addSingleBenchmark(aSingleBenchmarkResultList, -900, -90, -5000);
        addSingleBenchmark(aSingleBenchmarkResultList, -90, -90, -5000);
        a.setSingleBenchmarkResultList(aSingleBenchmarkResultList); // 0 wins - 1 equals - 5 losses
        a.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(a);
        SolverBenchmarkResult b = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> bSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(bSingleBenchmarkResultList, -1000, -20, -1000); // Wins vs a - wins vs c
        addSingleBenchmark(bSingleBenchmarkResultList, -200, -20, -1000); // Wins vs a - loses vs c
        addSingleBenchmark(bSingleBenchmarkResultList, -20, -20, -1000); // Wins vs a - loses vs c
        b.setSingleBenchmarkResultList(bSingleBenchmarkResultList); // 4 wins - 0 equals - 2 losses
        b.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(b);
        SolverBenchmarkResult c = new SolverBenchmarkResult(null);
        List<SingleBenchmarkResult> cSingleBenchmarkResultList = new ArrayList<SingleBenchmarkResult>();
        addSingleBenchmark(cSingleBenchmarkResultList, -5000, -10, -5000); // Loses vs b, Equals vs a
        addSingleBenchmark(cSingleBenchmarkResultList, -100, -10, -5000); // Wins vs a - wins vs b
        addSingleBenchmark(cSingleBenchmarkResultList, -10, -10, -5000); // Wins vs a - wins vs b
        c.setSingleBenchmarkResultList(cSingleBenchmarkResultList); // 4 wins - 1 equals - 1 losses
        c.accumulateResults(benchmarkReport);
        solverBenchmarkResultList.add(c);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkResultList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkResultList, b);
        Comparable cWeight = factory.createRankingWeight(solverBenchmarkResultList, c);

        assertEquals(-1, aWeight.compareTo(bWeight));
        assertEquals(1, bWeight.compareTo(aWeight));
        assertEquals(-1, aWeight.compareTo(cWeight));
        assertEquals(1, cWeight.compareTo(aWeight));
        assertEquals(-1, bWeight.compareTo(cWeight));
        assertEquals(1, cWeight.compareTo(bWeight));
    }

}
