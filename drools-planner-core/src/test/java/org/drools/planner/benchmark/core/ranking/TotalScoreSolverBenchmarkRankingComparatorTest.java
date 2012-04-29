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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.planner.benchmark.core.PlannerBenchmarkResult;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.PlanningEntityTabuAcceptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.score.buildin.simple.SimpleScore;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TotalScoreSolverBenchmarkRankingComparatorTest {

    @Test
    public void normal() {
        TotalScoreSolverBenchmarkRankingComparator comparator = new TotalScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark();
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -1000);
        addPlannerBenchmarkResult(aResultList, -400);
        addPlannerBenchmarkResult(aResultList, -30);
        a.setPlannerBenchmarkResultList(aResultList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -1000);
        addPlannerBenchmarkResult(bResultList, -200);
        addPlannerBenchmarkResult(bResultList, -50);
        b.setPlannerBenchmarkResultList(bResultList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void totalIsEqual() {
        TotalScoreSolverBenchmarkRankingComparator comparator = new TotalScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark();
        List<PlannerBenchmarkResult> aResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(aResultList, -1005);
        addPlannerBenchmarkResult(aResultList, -200);
        addPlannerBenchmarkResult(aResultList, -30);
        a.setPlannerBenchmarkResultList(aResultList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark();
        List<PlannerBenchmarkResult> bResultList = new ArrayList<PlannerBenchmarkResult>();
        addPlannerBenchmarkResult(bResultList, -1000);
        addPlannerBenchmarkResult(bResultList, -200);
        addPlannerBenchmarkResult(bResultList, -35);
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
