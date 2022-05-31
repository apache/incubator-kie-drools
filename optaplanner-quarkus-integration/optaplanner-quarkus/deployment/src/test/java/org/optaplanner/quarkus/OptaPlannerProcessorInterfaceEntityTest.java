package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.quarkus.testdata.interfaceentity.domain.TestdataInterfaceEntity;
import org.optaplanner.quarkus.testdata.interfaceentity.domain.TestdataInterfaceEntityImplementation;
import org.optaplanner.quarkus.testdata.interfaceentity.domain.TestdataInterfaceEntitySolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorInterfaceEntityTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addPackages(true, "org.optaplanner.quarkus.testdata.interfaceentity"));

    @Inject
    SolverFactory<TestdataInterfaceEntitySolution> solverFactory;

    @Test
    void buildSolver() {
        TestdataInterfaceEntitySolution problem = new TestdataInterfaceEntitySolution();
        List<TestdataInterfaceEntity> entityList = IntStream.range(1, 3)
                .mapToObj(i -> new TestdataInterfaceEntityImplementation())
                .collect(Collectors.toList());

        problem.setValueList(IntStream.range(0, 3)
                .boxed()
                .collect(Collectors.toList()));
        problem.setEntityList(entityList);

        TestdataInterfaceEntitySolution solution = solverFactory.buildSolver().solve(problem);
        assertNotNull(solution);

        assertEquals(entityList.size(), solution.getEntityList().size());
        for (int i = 0; i < entityList.size(); i++) {
            assertNotSame(entityList.get(i), solution.getEntityList().get(i));
        }
    }
}
