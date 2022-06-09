package org.optaplanner.examples.common.app;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.common.TurtleTest;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class RealTimePlanningTurtleTest<Solution_> {

    public static final int FREQUENCY = 300;
    public static final long SPENT_LIMIT = 5000L;
    protected Solver<Solution_> solver;

    @TurtleTest
    public void realTimePlanning() throws InterruptedException, ExecutionException {
        final SolverFactory<Solution_> solverFactory = buildSolverFactory();
        final Solution_ problem = readProblem();
        solver = solverFactory.buildSolver();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> solveFuture = executorService.submit(() -> runSolve(solver, problem));
        Future<?> changesFuture = executorService.submit(this::runChanges);
        solveFuture.get();
        changesFuture.get();
    }

    protected SolverFactory<Solution_> buildSolverFactory() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(createSolverConfigResource());
        solverConfig.setDaemon(true);
        solverConfig.setTerminationConfig(new TerminationConfig().withMillisecondsSpentLimit(SPENT_LIMIT));
        return SolverFactory.create(solverConfig);
    }

    protected abstract String createSolverConfigResource();

    protected abstract Solution_ readProblem();

    protected void runSolve(Solver<Solution_> solver, Solution_ problem) {
        solver.solve(problem);
    }

    protected void runChanges() {
        Random random = new Random(37);
        long startSystemTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - startSystemTimeMillis < 600_000L) {
            ProblemChange<Solution_> factChange = nextProblemChange(random);
            solver.addProblemChange(factChange);
            long sleepMillis = random.nextInt(FREQUENCY);
            if (sleepMillis <= (FREQUENCY / 100)) {
                sleepMillis = SPENT_LIMIT + 500L;
            } else if (sleepMillis <= (FREQUENCY / 10)) {
                sleepMillis = 0;
            }
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                solver.terminateEarly();
                throw new IllegalStateException("runChanges() interrupted.", e);
            }
        }
        solver.terminateEarly();
    }

    protected abstract ProblemChange<Solution_> nextProblemChange(Random random);

}
