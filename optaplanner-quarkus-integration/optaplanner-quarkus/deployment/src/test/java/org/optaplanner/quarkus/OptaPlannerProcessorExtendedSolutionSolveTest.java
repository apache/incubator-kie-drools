package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.solver.DefaultSolutionManager;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.solver.DefaultSolverManager;
import org.optaplanner.quarkus.testdata.extended.TestdataExtendedQuarkusSolution;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorExtendedSolutionSolveTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataExtendedQuarkusSolution.class,
                            TestdataQuarkusConstraintProvider.class));

    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;
    @Inject
    SolverManager<TestdataQuarkusSolution, Long> solverManager;
    @Inject
    ScoreManager<TestdataQuarkusSolution, SimpleScore> scoreManager;
    @Inject
    SolutionManager<TestdataQuarkusSolution, SimpleScore> solutionManager;

    @Test
    void singletonSolverFactory() {
        assertNotNull(solverFactory);
        // There is only one ScoreDirectorFactory instance
        assertSame(((DefaultSolverFactory<TestdataQuarkusSolution>) solverFactory).getScoreDirectorFactory(),
                ((DefaultSolutionManager<TestdataQuarkusSolution, SimpleScore>) solutionManager).getScoreDirectorFactory());
        assertNotNull(solverManager);
        // There is only one SolverFactory instance
        assertSame(solverFactory, ((DefaultSolverManager<TestdataQuarkusSolution, Long>) solverManager).getSolverFactory());
        assertNotNull(scoreManager);
    }

    @Test
    void solve() throws ExecutionException, InterruptedException {
        TestdataQuarkusSolution problem = new TestdataExtendedQuarkusSolution("Extra Data");
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .collect(Collectors.toList()));
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataQuarkusEntity())
                .collect(Collectors.toList()));
        SolverJob<TestdataQuarkusSolution, Long> solverJob = solverManager.solve(1L, problem);
        TestdataExtendedQuarkusSolution solution = (TestdataExtendedQuarkusSolution) solverJob.getFinalBestSolution();
        assertNotNull(solution);
        assertTrue(solution.getScore().score() >= 0);
        assertEquals("Extra Data", solution.getExtraData());
    }

}
