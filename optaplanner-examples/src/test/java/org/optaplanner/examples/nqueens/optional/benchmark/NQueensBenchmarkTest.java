package org.optaplanner.examples.nqueens.optional.benchmark;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

class NQueensBenchmarkTest extends PlannerBenchmarkTest {

    NQueensBenchmarkTest() {
        super(NQueensApp.SOLVER_CONFIG);
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Timeout(600)
    void benchmark64queens() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        addAllStatistics(benchmarkConfig);
        benchmarkConfig.setParallelBenchmarkCount("AUTO");
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        benchmark.benchmark();
    }

    @Test
    @Timeout(600)
    void benchmark64queensSingleThread() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        addAllStatistics(benchmarkConfig);
        benchmarkConfig.setParallelBenchmarkCount("1");
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        benchmark.benchmark();
    }

    protected void addAllStatistics(PlannerBenchmarkConfig benchmarkConfig) {
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        problemBenchmarksConfig.setSingleStatisticTypeList(Arrays.asList(SingleStatisticType.values()));
        problemBenchmarksConfig.setProblemStatisticTypeList(Arrays.asList(ProblemStatisticType.values()));
        benchmarkConfig.getInheritedSolverBenchmarkConfig().setProblemBenchmarksConfig(problemBenchmarksConfig);
    }

    @Test
    void benchmarkDirectoryNameDuplication() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/4queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(benchmarkConfig);
        DefaultPlannerBenchmark plannerBenchmark = (DefaultPlannerBenchmark) benchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmarkingStarted();
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(benchmarkConfig.getBenchmarkDirectory());
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(benchmarkConfig.getBenchmarkDirectory());
    }

}
