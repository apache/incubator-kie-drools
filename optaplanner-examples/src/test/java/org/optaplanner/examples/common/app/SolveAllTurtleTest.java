package org.optaplanner.examples.common.app;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testutil.DisabledInProductizationCheck;
import org.optaplanner.examples.common.TestSystemProperties;
import org.optaplanner.examples.common.TurtleTest;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolveAllTurtleTest<Solution_> extends LoggingTest {

    interface ProblemFactory<Solution_> extends Function<File, Solution_> {

        default Solution_ loadProblem(File f) {
            return apply(f);
        }
    }

    private static final String MOVE_THREAD_COUNT_OVERRIDE = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNT);

    protected abstract List<File> getSolutionFiles(CommonApp<Solution_> commonApp);

    protected abstract CommonApp<Solution_> createCommonApp();

    protected abstract ProblemFactory<Solution_> createProblemFactory(CommonApp<Solution_> commonApp);

    @TestFactory
    @TurtleTest
    Stream<DynamicTest> runFastAndFullAssert() {
        CommonApp<Solution_> commonApp = createCommonApp();
        ProblemFactory<Solution_> problemFactory = createProblemFactory(commonApp);
        return getSolutionFiles(commonApp).stream()
                .map(solutionFile -> dynamicTest(solutionFile.getName(), () -> runFastAndFullAssert(
                        buildSolverConfig(commonApp.getSolverConfigResource()),
                        problemFactory.loadProblem(solutionFile))));
    }

    public void runFastAndFullAssert(SolverConfig solverConfig, Solution_ problem) {
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.FAST_ASSERT, problem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 3L);
    }

    private static SolverConfig buildSolverConfig(String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        if (solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass() != null) {
            ConstraintStreamImplType constraintStreamImplType = resolveConstraintStreamType();
            if (constraintStreamImplType == ConstraintStreamImplType.BAVET && DisabledInProductizationCheck.isProductized()) {
                throw new UnsupportedOperationException("Bavet not supported in a productized profile.");
            }
            solverConfig.getScoreDirectorFactoryConfig().setConstraintStreamImplType(constraintStreamImplType);
        }
        // buildAndSolve() fills in minutesSpentLimit
        solverConfig.setTerminationConfig(new TerminationConfig());
        if (MOVE_THREAD_COUNT_OVERRIDE != null) {
            solverConfig.setMoveThreadCount(MOVE_THREAD_COUNT_OVERRIDE);
        }
        return solverConfig;
    }

    private static ConstraintStreamImplType resolveConstraintStreamType() {
        String csImplProperty = System.getProperty(TestSystemProperties.CONSTRAINT_STREAM_IMPL_TYPE,
                ConstraintStreamImplType.DROOLS.name()).trim().toUpperCase();
        return csImplProperty.equals(ConstraintStreamImplType.DROOLS.name()) ? ConstraintStreamImplType.DROOLS
                : ConstraintStreamImplType.BAVET;
    }

    private Solution_ buildAndSolve(SolverConfig solverConfig, EnvironmentMode environmentMode,
            Solution_ problem, long maximumMinutesSpent) {
        solverConfig.getTerminationConfig().setMinutesSpentLimit(maximumMinutesSpent);
        solverConfig.setEnvironmentMode(environmentMode);
        Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = overwritingEasyScoreCalculatorClass();
        if (easyScoreCalculatorClass != null && environmentMode.isAsserted()) {
            ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            assertionScoreDirectorFactoryConfig.setEasyScoreCalculatorClass(easyScoreCalculatorClass);
            solverConfig.getScoreDirectorFactoryConfig().setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactoryConfig);
        }
        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        Solver<Solution_> solver = solverFactory.buildSolver();
        return solver.solve(problem);
    }

    protected Class<? extends EasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return null;
    }
}
