package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.quarkus.testdata.superclass.constraints.DummyConstraintProvider;
import org.optaplanner.quarkus.testdata.superclass.domain.TestdataEntity;
import org.optaplanner.quarkus.testdata.superclass.domain.TestdataSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorPlanningIdTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addPackage("org.optaplanner.quarkus.testdata.superclass.domain") // Cannot reference a non-public class.
                    .addClasses(DummyConstraintProvider.class));

    @Inject
    SolverFactory<TestdataSolution> solverFactory;

    @Test
    public void buildSolver() {
        TestdataSolution problem = new TestdataSolution();
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .collect(Collectors.toList()));
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataEntity(i))
                .collect(Collectors.toList()));

        TestdataSolution solution = solverFactory.buildSolver().solve(problem);
        assertNotNull(solution);
    }
}
