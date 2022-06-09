package org.optaplanner.benchmark.quarkus;

import java.util.concurrent.ExecutionException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerBenchmarkProcessorMissingSpentLimitTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.test.flat-class-path", "true")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class));

    @Test
    void benchmark() throws ExecutionException, InterruptedException {
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            new OptaPlannerBenchmarkRecorder().benchmarkConfigSupplier(new PlannerBenchmarkConfig()).get();
        });
        Assertions.assertEquals(
                "At least one of the properties quarkus.optaplanner.benchmark.solver.termination.spent-limit, quarkus.optaplanner.benchmark.solver.termination.best-score-limit, quarkus.optaplanner.benchmark.solver.termination.unimproved-spent-limit is required if termination is not configured in the inherited solver benchmark config and solverBenchmarkBluePrint is used.",
                exception.getMessage());
    }

}
