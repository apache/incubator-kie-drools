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
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorConstraintsDrlDefaultTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class, TestdataQuarkusSolution.class)
                    .addAsResource("org/optaplanner/quarkus/constraints/defaultConstraints.drl", "constraints.drl"));

    @Inject
    SolverConfig solverConfig;

    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void constraintsDrl_default() {
        assertEquals(Collections.singletonList("constraints.drl"),
                solverConfig.getScoreDirectorFactoryConfig().getScoreDrlList());
        assertNotNull(solverFactory.buildSolver());
    }

}
