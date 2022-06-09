package org.optaplanner.examples.tennis.app;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.persistence.TennisGenerator;

class TennisBenchmarkTest extends LoggingTest {

    @Test
    void benchmark() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(TennisApp.SOLVER_CONFIG);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(
                solverConfig, new File("target/test/data/tennis"));
        benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig()
                .setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(1000L));
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(benchmarkConfig);

        TennisSolution problem = new TennisGenerator().createTennisSolution();
        PlannerBenchmark plannerBenchmark = benchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

}
