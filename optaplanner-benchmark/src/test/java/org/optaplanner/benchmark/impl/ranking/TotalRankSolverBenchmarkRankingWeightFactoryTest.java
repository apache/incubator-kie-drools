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

import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.junit.Test;

import static org.junit.Assert.*;

public class TotalRankSolverBenchmarkRankingWeightFactoryTest extends AbstractRankingComparatorTest {

    @Test
    public void normal() {
        TotalRankSolverBenchmarkRankingWeightFactory factory = new TotalRankSolverBenchmarkRankingWeightFactory();
        List<SolverBenchmark> solverBenchmarkList = new ArrayList<SolverBenchmark>();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -1000, -40, -1000);
        addSingleBenchmark(aSingleBenchmarkList, -300, -40, -1000);
        addSingleBenchmark(aSingleBenchmarkList, -40, -40, -1000);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -2000, -30, -2000); // Loses vs a
        addSingleBenchmark(bSingleBenchmarkList, -200, -30, -2000); // Wins vs a
        addSingleBenchmark(bSingleBenchmarkList, -30, -30, -2000); // Wins vs a
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        solverBenchmarkList.add(b);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkList, b);

        assertEquals(-1, aWeight.compareTo(bWeight));
        assertEquals(1, bWeight.compareTo(aWeight));
    }

    @Test
    public void equalCount() {
        TotalRankSolverBenchmarkRankingWeightFactory factory = new TotalRankSolverBenchmarkRankingWeightFactory();
        List<SolverBenchmark> solverBenchmarkList = new ArrayList<SolverBenchmark>();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -5000, -90, -5000);
        addSingleBenchmark(aSingleBenchmarkList, -900, -90, -5000);
        addSingleBenchmark(aSingleBenchmarkList, -90, -90, -5000);
        a.setSingleBenchmarkList(aSingleBenchmarkList); // 0 wins - 1 equals - 5 losses
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -1000, -20, -1000); // Wins vs a - wins vs c
        addSingleBenchmark(bSingleBenchmarkList, -200, -20, -1000); // Wins vs a - loses vs c
        addSingleBenchmark(bSingleBenchmarkList, -20, -20, -1000); // Wins vs a - loses vs c
        b.setSingleBenchmarkList(bSingleBenchmarkList); // 4 wins - 0 equals - 2 losses
        b.benchmarkingEnded();
        solverBenchmarkList.add(b);
        SolverBenchmark c = new SolverBenchmark(null);
        List<SingleBenchmark> cSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(cSingleBenchmarkList, -5000, -10, -5000); // Loses vs b, Equals vs a
        addSingleBenchmark(cSingleBenchmarkList, -100, -10, -5000); // Wins vs a - wins vs b
        addSingleBenchmark(cSingleBenchmarkList, -10, -10, -5000); // Wins vs a - wins vs b
        c.setSingleBenchmarkList(cSingleBenchmarkList); // 4 wins - 1 equals - 1 losses
        c.benchmarkingEnded();
        solverBenchmarkList.add(c);

        Comparable aWeight = factory.createRankingWeight(solverBenchmarkList, a);
        Comparable bWeight = factory.createRankingWeight(solverBenchmarkList, b);
        Comparable cWeight = factory.createRankingWeight(solverBenchmarkList, c);

        assertEquals(-1, aWeight.compareTo(bWeight));
        assertEquals(1, bWeight.compareTo(aWeight));
        assertEquals(-1, aWeight.compareTo(cWeight));
        assertEquals(1, cWeight.compareTo(aWeight));
        assertEquals(-1, bWeight.compareTo(cWeight));
        assertEquals(1, cWeight.compareTo(bWeight));
    }

}
