package org.optaplanner.examples.nqueens.optional.benchmark;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensSolutionFileIO;

class BrokenNQueensBenchmarkTest extends PlannerBenchmarkTest {

    BrokenNQueensBenchmarkTest() {
        super(NQueensApp.SOLVER_CONFIG);
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test
    @Timeout(100)
    void benchmarkBroken8queens() {
        NQueens problem = new NQueensSolutionFileIO()
                .read(new File("data/nqueens/unsolved/8queens.json"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        benchmarkConfig.setWarmUpSecondsSpentLimit(0L);
        benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getTerminationConfig()
                .setStepCountLimit(-100); // Intentionally crash the solver
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        assertThatExceptionOfType(PlannerBenchmarkException.class).isThrownBy(benchmark::benchmark);
    }

}
