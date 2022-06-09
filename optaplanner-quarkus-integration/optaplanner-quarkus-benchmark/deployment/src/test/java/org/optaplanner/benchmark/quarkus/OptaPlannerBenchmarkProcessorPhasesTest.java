package org.optaplanner.benchmark.quarkus;

import javax.inject.Inject;

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

class OptaPlannerBenchmarkProcessorPhasesTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.benchmark.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("solverConfigWithPhases.xml", "solverConfig.xml")
                    .addAsResource("solverBenchmarkConfigWithPhases.xml", "solverBenchmarkConfig.xml"));

    @Inject
    PlannerBenchmarkConfig plannerBenchmarkConfig;

    @Test
    void doesNotInheritPhasesFromSolverConfig() {
        Assertions.assertEquals(2, plannerBenchmarkConfig.getSolverBenchmarkConfigList().get(0).getSolverConfig()
                .getPhaseConfigList().size());
        Assertions.assertEquals(3, plannerBenchmarkConfig.getSolverBenchmarkConfigList().get(1).getSolverConfig()
                .getPhaseConfigList().size());
    }

}
