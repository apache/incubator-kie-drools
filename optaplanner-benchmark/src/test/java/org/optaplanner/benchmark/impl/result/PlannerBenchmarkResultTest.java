/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.aggregator.BenchmarkAggregator;
import org.optaplanner.benchmark.impl.loader.FileProblemProvider;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.io.jaxb.JaxbIO;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

public class PlannerBenchmarkResultTest {

    private static final String TEST_PLANNER_BENCHMARK_RESULT = "testPlannerBenchmarkResult.xml";

    private final JaxbIO<PlannerBenchmarkResult> xmlIO = new JaxbIO(PlannerBenchmarkResult.class);

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
        p1SolverX.setSingleBenchmarkResultList(new ArrayList<>());
        SolverBenchmarkResult p1SolverY = new SolverBenchmarkResult(p1);
        p1SolverY.setName("Solver Y");
        SolverConfig p1SolverConfigY = new SolverConfig();
        p1SolverConfigY.setRandomType(RandomType.MERSENNE_TWISTER);
        p1SolverY.setSolverConfig(p1SolverConfigY);
        p1SolverY.setSingleBenchmarkResultList(new ArrayList<>());
        SolverBenchmarkResult p2SolverZ = new SolverBenchmarkResult(p2);
        p2SolverZ.setName("Solver Z");
        SolverConfig p2SolverConfigZ = new SolverConfig();
        p2SolverConfigZ.setRandomType(RandomType.WELL1024A);
        p2SolverZ.setSolverConfig(p2SolverConfigZ);
        p2SolverZ.setSingleBenchmarkResultList(new ArrayList<>());

        ProblemBenchmarkResult p1ProblemA = new ProblemBenchmarkResult(p1);
        p1ProblemA.setProblemProvider(new FileProblemProvider(null, new File("problemA.xml")));
        p1ProblemA.setProblemStatisticList(Collections.emptyList());
        p1ProblemA.setSingleBenchmarkResultList(Collections.emptyList());
        p1ProblemA.setSingleBenchmarkResultList(new ArrayList<>());
        ProblemBenchmarkResult p1ProblemB = new ProblemBenchmarkResult(p1);
        p1ProblemB.setProblemProvider(new FileProblemProvider(null, new File("problemB.xml")));
        p1ProblemB.setProblemStatisticList(Collections.emptyList());
        p1ProblemB.setSingleBenchmarkResultList(Collections.emptyList());
        p1ProblemB.setSingleBenchmarkResultList(new ArrayList<>());
        ProblemBenchmarkResult p2ProblemA = new ProblemBenchmarkResult(p2);
        p2ProblemA.setProblemProvider(new FileProblemProvider(null, new File("problemA.xml")));
        p2ProblemA.setProblemStatisticList(Collections.emptyList());
        p2ProblemA.setSingleBenchmarkResultList(Collections.emptyList());
        p2ProblemA.setSingleBenchmarkResultList(new ArrayList<>());

        SingleBenchmarkResult p1SolverXProblemA = createSingleBenchmarkResult(p1SolverX, p1ProblemA, -1);
        createSubSingleBenchmarkResult(p1SolverXProblemA, 1);
        SingleBenchmarkResult p1SolverXProblemB = createSingleBenchmarkResult(p1SolverX, p1ProblemB, -20);
        SingleBenchmarkResult p1SolverYProblemA = createSingleBenchmarkResult(p1SolverY, p1ProblemA, -300);
        SingleBenchmarkResult p1SolverYProblemB = createSingleBenchmarkResult(p1SolverY, p1ProblemB, -4000);
        SingleBenchmarkResult p2SolverZProblemA = createSingleBenchmarkResult(p2SolverZ, p2ProblemA, -50000);

        PlannerBenchmarkResult mergedResult = PlannerBenchmarkResult.createMergedResult(Arrays.asList(
                p1SolverXProblemA, p1SolverXProblemB, p1SolverYProblemA, p1SolverYProblemB, p2SolverZProblemA));

        assertThat(mergedResult.getAggregation()).isTrue();
        List<ProblemBenchmarkResult> mergedProblemBenchmarkResultList = mergedResult.getUnifiedProblemBenchmarkResultList();
        List<SolverBenchmarkResult> mergedSolverBenchmarkResultList = mergedResult.getSolverBenchmarkResultList();
        assertThat(mergedSolverBenchmarkResultList.size()).isEqualTo(3);
        assertThat(mergedSolverBenchmarkResultList.get(0).getName()).isEqualTo("Solver X");
        assertThat(mergedSolverBenchmarkResultList.get(1).getName()).isEqualTo("Solver Y");
        assertThat(mergedSolverBenchmarkResultList.get(2).getName()).isEqualTo("Solver Z");
        assertThat(mergedProblemBenchmarkResultList.size()).isEqualTo(2);
        assertThat(mergedProblemBenchmarkResultList.get(0).getProblemProvider().getProblemName()).isEqualTo("problemA");
        assertThat(mergedProblemBenchmarkResultList.get(1).getProblemProvider().getProblemName()).isEqualTo("problemB");
    }

    protected SingleBenchmarkResult createSingleBenchmarkResult(
            SolverBenchmarkResult solverBenchmarkResult, ProblemBenchmarkResult problemBenchmarkResult, int score) {
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);
        solverBenchmarkResult.getSingleBenchmarkResultList().add(singleBenchmarkResult);
        problemBenchmarkResult.getSingleBenchmarkResultList().add(singleBenchmarkResult);
        singleBenchmarkResult.setAverageAndTotalScoreForTesting(SimpleScore.of(score));
        singleBenchmarkResult.setSubSingleBenchmarkResultList(new ArrayList<>(1));
        createSubSingleBenchmarkResult(singleBenchmarkResult, 0);
        return singleBenchmarkResult;
    }

    protected SubSingleBenchmarkResult createSubSingleBenchmarkResult(SingleBenchmarkResult parent, int subSingleIndex) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = new SubSingleBenchmarkResult(parent, subSingleIndex);
        subSingleBenchmarkResult.setPureSubSingleStatisticList(Collections.emptyList());
        parent.getSubSingleBenchmarkResultList().add(subSingleBenchmarkResult);
        return subSingleBenchmarkResult;
    }

    @Test
    public void xmlReportRemainsSameAfterReadWrite() throws IOException {
        PlannerBenchmarkResult plannerBenchmarkResult;
        try (Reader reader = new InputStreamReader(
                PlannerBenchmarkResultTest.class.getResourceAsStream(TEST_PLANNER_BENCHMARK_RESULT), "UTF-8")) {
            plannerBenchmarkResult = xmlIO.read(reader);
        }

        Writer stringWriter = new StringWriter();
        xmlIO.write(plannerBenchmarkResult, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(
                PlannerBenchmarkResultTest.class.getResourceAsStream(TEST_PLANNER_BENCHMARK_RESULT), StandardCharsets.UTF_8);

        assertThat(jaxbString.trim()).isEqualToNormalizingNewlines(originalXml.trim());
    }

    @Test
    public void xmlReadBenchmarkResultAggregated() throws URISyntaxException, IOException {
        BenchmarkAggregator benchmarkAggregator = new BenchmarkAggregator();
        benchmarkAggregator.setBenchmarkDirectory(Files.createTempDirectory(getClass().getSimpleName()).toFile());
        benchmarkAggregator.setBenchmarkReportConfig(new BenchmarkReportConfig());

        File plannerBenchmarkResultFile =
                new File(PlannerBenchmarkResultTest.class.getResource(TEST_PLANNER_BENCHMARK_RESULT).toURI());

        BenchmarkResultIO benchmarkResultIO = new BenchmarkResultIO();
        PlannerBenchmarkResult plannerBenchmarkResult =
                benchmarkResultIO.readPlannerBenchmarkResult(plannerBenchmarkResultFile);

        BenchmarkReport benchmarkReport =
                benchmarkAggregator.getBenchmarkReportConfig().buildBenchmarkReport(plannerBenchmarkResult);
        plannerBenchmarkResult.accumulateResults(benchmarkReport);

        PlannerBenchmarkResult aggregatedPlannerBenchmarkResult = benchmarkReport.getPlannerBenchmarkResult();

        assertThat(aggregatedPlannerBenchmarkResult.getSolverBenchmarkResultList()).hasSize(6);
        assertThat(aggregatedPlannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()).hasSize(2);
        assertThat(aggregatedPlannerBenchmarkResult.getFailureCount()).isZero();
    }

    // nested class below are used in the testPlannerBenchmarkResult.xml

    private static abstract class DummyIncrementalScoreCalculator
            extends AbstractIncrementalScoreCalculator<TestdataChainedSolution> {

    }

    private static abstract class DummyDistanceNearbyMeter
            implements NearbyDistanceMeter<TestdataChainedSolution, TestdataChainedEntity> {

    }
}
