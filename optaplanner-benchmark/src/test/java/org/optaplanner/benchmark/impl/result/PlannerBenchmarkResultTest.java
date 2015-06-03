/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.random.RandomType;

import static org.junit.Assert.*;

public class PlannerBenchmarkResultTest {

    @Test
    public void createMergedResult() {
        PlannerBenchmarkResult p1 = new PlannerBenchmarkResult();
        p1.initSystemProperties();
        PlannerBenchmarkResult p2 = new PlannerBenchmarkResult();
        p2.initSystemProperties();

        SolverBenchmarkResult p1SolverX = new SolverBenchmarkResult(p1);
        p1SolverX.setName("Solver X");
        SolverConfig p1SolverConfigX = new SolverConfig();
        p1SolverConfigX.setRandomType(RandomType.JDK);
        p1SolverX.setSolverConfig(p1SolverConfigX);
        p1SolverX.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());
        SolverBenchmarkResult p1SolverY = new SolverBenchmarkResult(p1);
        p1SolverY.setName("Solver Y");
        SolverConfig p1SolverConfigY = new SolverConfig();
        p1SolverConfigY.setRandomType(RandomType.MERSENNE_TWISTER);
        p1SolverY.setSolverConfig(p1SolverConfigY);
        p1SolverY.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());
        SolverBenchmarkResult p2SolverZ = new SolverBenchmarkResult(p2);
        p2SolverZ.setName("Solver Z");
        SolverConfig p2SolverConfigZ = new SolverConfig();
        p2SolverConfigZ.setRandomType(RandomType.WELL1024A);
        p2SolverZ.setSolverConfig(p2SolverConfigZ);
        p2SolverZ.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());

        ProblemBenchmarkResult p1ProblemA = new ProblemBenchmarkResult(p1);
        p1ProblemA.setInputSolutionFile(new File("problemA.xml"));
        p1ProblemA.setProblemStatisticList(Collections.<ProblemStatistic>emptyList());
        p1ProblemA.setSingleBenchmarkResultList(Collections.<SingleBenchmarkResult>emptyList());
        p1ProblemA.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());
        ProblemBenchmarkResult p1ProblemB = new ProblemBenchmarkResult(p1);
        p1ProblemB.setInputSolutionFile(new File("problemB.xml"));
        p1ProblemB.setProblemStatisticList(Collections.<ProblemStatistic>emptyList());
        p1ProblemB.setSingleBenchmarkResultList(Collections.<SingleBenchmarkResult>emptyList());
        p1ProblemB.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());
        ProblemBenchmarkResult p2ProblemA = new ProblemBenchmarkResult(p2);
        p2ProblemA.setInputSolutionFile(new File("problemA.xml"));
        p2ProblemA.setProblemStatisticList(Collections.<ProblemStatistic>emptyList());
        p2ProblemA.setSingleBenchmarkResultList(Collections.<SingleBenchmarkResult>emptyList());
        p2ProblemA.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());

        SingleBenchmarkResult p1SolverXProblemA = createSingleBenchmarkResult(p1SolverX, p1ProblemA, -1);
        SingleBenchmarkResult p1SolverXProblemB = createSingleBenchmarkResult(p1SolverX, p1ProblemB, -20);
        SingleBenchmarkResult p1SolverYProblemA = createSingleBenchmarkResult(p1SolverY, p1ProblemA, -300);
        SingleBenchmarkResult p1SolverYProblemB = createSingleBenchmarkResult(p1SolverY, p1ProblemB, -4000);
        SingleBenchmarkResult p2SolverZProblemA = createSingleBenchmarkResult(p2SolverZ, p2ProblemA, -50000);

        PlannerBenchmarkResult mergedResult = PlannerBenchmarkResult.createMergedResult(Arrays.asList(
                p1SolverXProblemA, p1SolverXProblemB, p1SolverYProblemA, p1SolverYProblemB, p2SolverZProblemA));

        assertEquals(true, mergedResult.getAggregation());
        List<ProblemBenchmarkResult> mergedProblemBenchmarkResultList = mergedResult.getUnifiedProblemBenchmarkResultList();
        List<SolverBenchmarkResult> mergedSolverBenchmarkResultList = mergedResult.getSolverBenchmarkResultList();
        assertEquals(3, mergedSolverBenchmarkResultList.size());
        assertEquals("Solver X", mergedSolverBenchmarkResultList.get(0).getName());
        assertEquals("Solver Y", mergedSolverBenchmarkResultList.get(1).getName());
        assertEquals("Solver Z", mergedSolverBenchmarkResultList.get(2).getName());
        assertEquals(2, mergedProblemBenchmarkResultList.size());
        assertEquals("problemA.xml", mergedProblemBenchmarkResultList.get(0).getInputSolutionFile().getName());
        assertEquals("problemB.xml", mergedProblemBenchmarkResultList.get(1).getInputSolutionFile().getName());
    }

    protected SingleBenchmarkResult createSingleBenchmarkResult(
            SolverBenchmarkResult solverBenchmarkResult, ProblemBenchmarkResult problemBenchmarkResult, int score) {
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);
        solverBenchmarkResult.getSingleBenchmarkResultList().add(singleBenchmarkResult);
        problemBenchmarkResult.getSingleBenchmarkResultList().add(singleBenchmarkResult);
        singleBenchmarkResult.setScore(SimpleScore.valueOf(score));
        singleBenchmarkResult.setPureSingleStatisticList(Collections.<PureSingleStatistic>emptyList());
        return singleBenchmarkResult;
    }

}
