package org.optaplanner.examples.tennis.optional.benchmark;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.tennis.app.TennisApp;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.persistence.TennisGenerator;

public class TennisBenchmarkApp extends LoggingMain {

    public static void main(String[] args) {
        new TennisBenchmarkApp().benchmark();
    }

    private final PlannerBenchmarkFactory benchmarkFactory;

    public TennisBenchmarkApp() {
        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                TennisApp.SOLVER_CONFIG, new File("local/data/tennis"));
    }

    public void benchmark() {
        TennisSolution problem = new TennisGenerator().createTennisSolution();
        PlannerBenchmark plannerBenchmark = benchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

}
