package org.optaplanner.quarkus;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.testdata.gizmo.OnlyMultiArgsConstructorEntity;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorConstraintProvider;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorEntity;
import org.optaplanner.quarkus.testdata.gizmo.PrivateNoArgsConstructorSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorOnlyMultiConstructorTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(PrivateNoArgsConstructorConstraintProvider.class,
                            PrivateNoArgsConstructorSolution.class,
                            PrivateNoArgsConstructorEntity.class,
                            OnlyMultiArgsConstructorEntity.class))
            .assertException(throwable -> {
                Assertions.assertEquals(
                        "Class (" + OnlyMultiArgsConstructorEntity.class.getName()
                                + ") must have a no-args constructor so it can be constructed by OptaPlanner.",
                        throwable.getMessage());
            });

    @Inject
    SolverManager<PrivateNoArgsConstructorSolution, Long> solverManager;

    @Test
    void canConstructBeansWithPrivateConstructors() throws ExecutionException, InterruptedException {
    }

}
