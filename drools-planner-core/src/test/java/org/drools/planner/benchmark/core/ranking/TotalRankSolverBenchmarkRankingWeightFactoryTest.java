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

package org.drools.planner.benchmark.core.ranking;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TotalRankSolverBenchmarkRankingWeightFactoryTest {

    @Test
    public void normal() {
        TotalRankSolverBenchmarkRankingWeightFactory factory = new TotalRankSolverBenchmarkRankingWeightFactory();
        List<SolverBenchmark> solverBenchmarkList = new ArrayList<SolverBenchmark>();
        SolverBenchmark a = new SolverBenchmark();
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(aSingleBenchmarkList, -1000);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -300);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -40);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark();
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(bSingleBenchmarkList, -2000); // Loses vs a
        addPlannerBenchmarkResult(bSingleBenchmarkList, -200); // Wins vs a
        addPlannerBenchmarkResult(bSingleBenchmarkList, -30); // Wins vs a
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
        SolverBenchmark a = new SolverBenchmark();
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(aSingleBenchmarkList, -5000);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -900);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -90);
        a.setSingleBenchmarkList(aSingleBenchmarkList); // 0 wins - 1 equals - 5 losses
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark();
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(bSingleBenchmarkList, -1000); // Wins vs a - wins vs c
        addPlannerBenchmarkResult(bSingleBenchmarkList, -200); // Wins vs a - loses vs c
        addPlannerBenchmarkResult(bSingleBenchmarkList, -20); // Wins vs a - loses vs c
        b.setSingleBenchmarkList(bSingleBenchmarkList); // 4 wins - 0 equals - 2 losses
        b.benchmarkingEnded();
        solverBenchmarkList.add(b);
        SolverBenchmark c = new SolverBenchmark();
        List<SingleBenchmark> cSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(cSingleBenchmarkList, -5000); // Loses vs b, Equals vs a
        addPlannerBenchmarkResult(cSingleBenchmarkList, -100); // Wins vs a - wins vs b
        addPlannerBenchmarkResult(cSingleBenchmarkList, -10); // Wins vs a - wins vs b
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

    private void addPlannerBenchmarkResult(List<SingleBenchmark> singleBenchmarkList, int score) {
        SingleBenchmark result = new SingleBenchmark();
        result.setScore(DefaultSimpleScore.valueOf(score));
        singleBenchmarkList.add(result);
    }

}
