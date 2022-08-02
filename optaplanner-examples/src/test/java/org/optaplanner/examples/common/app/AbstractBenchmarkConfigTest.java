package org.optaplanner.examples.common.app;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

public abstract class AbstractBenchmarkConfigTest {

    protected abstract CommonBenchmarkApp getBenchmarkApp();

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    Stream<DynamicTest> testBenchmarkApp() {
        return getBenchmarkApp().getArgOptions().stream()
                .map(argOption -> dynamicTest(argOption.toString(), () -> buildPlannerBenchmark(argOption)));
    }

    private static void buildPlannerBenchmark(CommonBenchmarkApp.ArgOption argOption) {
        String benchmarkConfigResource = argOption.getBenchmarkConfigResource();
        PlannerBenchmarkFactory benchmarkFactory;
        if (!argOption.isTemplate()) {
            benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(benchmarkConfigResource);
        } else {
            benchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(benchmarkConfigResource);
        }
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        buildEverySolver(benchmark);
    }

    private static void buildEverySolver(PlannerBenchmark plannerBenchmark) {
        PlannerBenchmarkResult plannerBenchmarkResult = ((DefaultPlannerBenchmark) plannerBenchmark)
                .getPlannerBenchmarkResult();
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            SolverConfig solverConfig = solverBenchmarkResult.getSolverConfig();
            SolverFactory.create(solverConfig).buildSolver();
        }
    }
}
