package org.optaplanner.benchmark.quarkus;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerBenchmarkProcessorSpentLimitConfiguredPerBenchmarkTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.test.flat-class-path", "true")
            .overrideConfigKey("quarkus.optaplanner.benchmark.solver-benchmark-config-xml",
                    "solverBenchmarkConfigSpentLimitPerBenchmark.xml")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("solverBenchmarkConfigSpentLimitPerBenchmark.xml"));

    @Inject
    PlannerBenchmarkFactory plannerBenchmarkFactory;

    @Test
    void benchmark() throws ExecutionException, InterruptedException {
        TestdataQuarkusSolution problem = new TestdataQuarkusSolution();
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .collect(Collectors.toList()));
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataQuarkusEntity())
                .collect(Collectors.toList()));
        plannerBenchmarkFactory.buildPlannerBenchmark(problem).benchmark();
    }

}
