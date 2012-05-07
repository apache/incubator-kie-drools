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

public class WorstScoreSolverBenchmarkRankingComparatorTest {

    @Test
    public void normal() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(aSingleBenchmarkList, -100);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -2001);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -30);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(bSingleBenchmarkList, -900);
        addPlannerBenchmarkResult(bSingleBenchmarkList, -2000);
        addPlannerBenchmarkResult(bSingleBenchmarkList, -30);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void worstIsEqual() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(aSingleBenchmarkList, -101);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -2000);
        addPlannerBenchmarkResult(aSingleBenchmarkList, -30);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addPlannerBenchmarkResult(bSingleBenchmarkList, -100);
        addPlannerBenchmarkResult(bSingleBenchmarkList, -2000);
        addPlannerBenchmarkResult(bSingleBenchmarkList, -40);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    private void addPlannerBenchmarkResult(List<SingleBenchmark> singleBenchmarkList, int score) {
        SingleBenchmark result = new SingleBenchmark(null, null);
        result.setScore(DefaultSimpleScore.valueOf(score));
        singleBenchmarkList.add(result);
    }

}
