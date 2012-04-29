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

public class WorstScoreSolverBenchmarkRankingComparatorTest {

    @Test
    public void normal() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark();
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -100);
        addPlannerBenchmarkResult(aResultList, -2001);
        addPlannerBenchmarkResult(aResultList, -30);
        a.setPlannerBenchmarkResultList(aResultList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -900);
        addPlannerBenchmarkResult(bResultList, -2000);
        addPlannerBenchmarkResult(bResultList, -30);
        b.setPlannerBenchmarkResultList(bResultList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void worstIsEqual() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark();
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -101);
        addPlannerBenchmarkResult(aResultList, -2000);
        addPlannerBenchmarkResult(aResultList, -30);
        a.setPlannerBenchmarkResultList(aResultList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -100);
        addPlannerBenchmarkResult(bResultList, -2000);
        addPlannerBenchmarkResult(bResultList, -40);
        b.setPlannerBenchmarkResultList(bResultList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    private void addPlannerBenchmarkResult(List<PlannerBenchmarkResult> plannerBenchmarkResultList, int score) {
        PlannerBenchmarkResult result = new PlannerBenchmarkResult();
        result.setScore(DefaultSimpleScore.valueOf(score));
        plannerBenchmarkResultList.add(result);
    }

}
