package org.optaplanner.quarkus;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorConstraintProvider;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorEntity;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorPrivateConstructorTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(PrivateNoArgsConstructorConstraintProvider.class,
                            PrivateNoArgsConstructorSolution.class,
                            PrivateNoArgsConstructorEntity.class));

    @Inject
    SolverManager<PrivateNoArgsConstructorSolution, Long> solverManager;

    @Test
    void canConstructBeansWithPrivateConstructors() throws ExecutionException, InterruptedException {
        PrivateNoArgsConstructorSolution problem = new PrivateNoArgsConstructorSolution(
                Arrays.asList(
                        new PrivateNoArgsConstructorEntity("1"),
                        new PrivateNoArgsConstructorEntity("2"),
                        new PrivateNoArgsConstructorEntity("3")));
        PrivateNoArgsConstructorSolution solution = solverManager.solve(1L, problem).getFinalBestSolution();
        Assertions.assertEquals(solution.score.getScore(), 0);
    }

}
