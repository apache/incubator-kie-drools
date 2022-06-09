package org.optaplanner.benchmark.quarkus;

import java.util.concurrent.ExecutionException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerBenchmarkProcessorMissingSpentLimitPerBenchmarkTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.test.flat-class-path", "true")
            .overrideConfigKey("quarkus.optaplanner.benchmark.solver-benchmark-config-xml",
                    "solverBenchmarkConfigSpentLimitPerBenchmarkNoTermination.xml")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("solverBenchmarkConfigSpentLimitPerBenchmarkNoTermination.xml"));

    @Test
    void benchmark() throws ExecutionException, InterruptedException {
        PlannerBenchmarkConfig benchmarkConfig =
                PlannerBenchmarkConfig.createFromXmlResource("solverBenchmarkConfigSpentLimitPerBenchmarkNoTermination.xml");
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            new OptaPlannerBenchmarkRecorder().benchmarkConfigSupplier(benchmarkConfig).get();
        });
        Assertions.assertEquals(
                "The following " + SolverBenchmarkConfig.class.getSimpleName() + " do not " +
                        "have termination configured: [First Fit and Local Search without Termination]. " +
                        "At least one of the properties " +
                        "quarkus.optaplanner.benchmark.solver.termination.spent-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.best-score-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.unimproved-spent-limit " +
                        "is required if termination is not configured in a solver benchmark and the " +
                        "inherited solver benchmark config.",
                exception.getMessage());
    }

}
