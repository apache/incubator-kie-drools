package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorXMLPropertyTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver-config-xml", "org/optaplanner/quarkus/customSolverConfig.xml")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("org/optaplanner/quarkus/customSolverConfig.xml"));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void solverConfigXml_property() {
        assertNotNull(solverConfig);
        assertEquals(DomainAccessType.GIZMO, solverConfig.getDomainAccessType());
        assertEquals(TestdataQuarkusSolution.class, solverConfig.getSolutionClass());
        assertEquals(Collections.singletonList(TestdataQuarkusEntity.class), solverConfig.getEntityClassList());
        assertEquals(TestdataQuarkusConstraintProvider.class,
                solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass());
        // Properties defined in solverConfig.xml
        assertEquals(3L, solverConfig.getTerminationConfig().getSecondsSpentLimit().longValue());
        assertNotNull(solverFactory);
        assertNotNull(solverFactory.buildSolver());
    }

}
