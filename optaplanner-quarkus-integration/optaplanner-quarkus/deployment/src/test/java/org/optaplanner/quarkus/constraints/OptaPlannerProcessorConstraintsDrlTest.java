package org.optaplanner.quarkus.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.deployment.config.OptaPlannerBuildTimeConfig;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorConstraintsDrlTest {

    private static final String CONSTRAINTS_DRL = "customConstraints.drl";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class, TestdataQuarkusSolution.class)
                    .addAsResource("org/optaplanner/quarkus/constraints/defaultConstraints.drl", CONSTRAINTS_DRL))
            .overrideConfigKey(OptaPlannerBuildTimeConfig.CONSTRAINTS_DRL_PROPERTY, CONSTRAINTS_DRL);

    @Inject
    SolverConfig solverConfig;

    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void constraintsDrl() {
        assertEquals(Collections.singletonList(CONSTRAINTS_DRL),
                solverConfig.getScoreDirectorFactoryConfig().getScoreDrlList());
        assertNotNull(solverFactory.buildSolver());
    }
}
