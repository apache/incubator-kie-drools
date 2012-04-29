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

import org.drools.planner.benchmark.core.PlannerBenchmarkResult;
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
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -1000);
        addPlannerBenchmarkResult(aResultList, -300);
        addPlannerBenchmarkResult(aResultList, -40);
        a.setPlannerBenchmarkResultList(aResultList);
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -2000); // Loses vs a
        addPlannerBenchmarkResult(bResultList, -200); // Wins vs a
        addPlannerBenchmarkResult(bResultList, -30); // Wins vs a
        b.setPlannerBenchmarkResultList(bResultList);
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
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -5000);
        addPlannerBenchmarkResult(aResultList, -900);
        addPlannerBenchmarkResult(aResultList, -90);
        a.setPlannerBenchmarkResultList(aResultList); // 0 wins - 1 equals - 5 losses
        a.benchmarkingEnded();
        solverBenchmarkList.add(a);
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -1000); // Wins vs a - wins vs c
        addPlannerBenchmarkResult(bResultList, -200); // Wins vs a - loses vs c
        addPlannerBenchmarkResult(bResultList, -20); // Wins vs a - loses vs c
        b.setPlannerBenchmarkResultList(bResultList); // 4 wins - 0 equals - 2 losses
        b.benchmarkingEnded();
        solverBenchmarkList.add(b);
        SolverBenchmark c = new SolverBenchmark();
        List<PlannerBenchmarkResult> cResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(cResultList, -5000); // Loses vs b, Equals vs a
        addPlannerBenchmarkResult(cResultList, -100); // Wins vs a - wins vs b
        addPlannerBenchmarkResult(cResultList, -10); // Wins vs a - wins vs b
        c.setPlannerBenchmarkResultList(cResultList); // 4 wins - 1 equals - 1 losses
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

    private void addPlannerBenchmarkResult(List<PlannerBenchmarkResult> plannerBenchmarkResultList, int score) {
        PlannerBenchmarkResult result = new PlannerBenchmarkResult();
        result.setScore(DefaultSimpleScore.valueOf(score));
        plannerBenchmarkResultList.add(result);
    }

}
