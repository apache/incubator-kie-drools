package org.optaplanner.core.impl.solver;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of submitted problem, such as {@link Long} or {@link UUID}.
 */
public final class DefaultSolverJob<Solution_, ProblemId_> implements SolverJob<Solution_, ProblemId_>, Callable<Solution_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSolverJob.class);

    private final DefaultSolverManager<Solution_, ProblemId_> solverManager;
    private final DefaultSolver<Solution_> solver;
    private final ProblemId_ problemId;
    private final Function<? super ProblemId_, ? extends Solution_> problemFinder;
    private final Consumer<? super Solution_> bestSolutionConsumer;
    private final Consumer<? super Solution_> finalBestSolutionConsumer;
    private final BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler;

    private volatile SolverStatus solverStatus;
    private final CountDownLatch terminatedLatch;
    private final ReentrantLock solverStatusModifyingLock;
    private Future<Solution_> finalBestSolutionFuture;
    private ConsumerSupport<Solution_, ProblemId_> consumerSupport;
    private final AtomicBoolean terminatedEarly = new AtomicBoolean(false);
    private final BestSolutionHolder<Solution_> bestSolutionHolder = new BestSolutionHolder<>();

    public DefaultSolverJob(
            DefaultSolverManager<Solution_, ProblemId_> solverManager,
            Solver<Solution_> solver, ProblemId_ problemId,
            Function<? super ProblemId_, ? extends Solution_> problemFinder,
            Consumer<? super Solution_> bestSolutionConsumer,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler) {
        this.solverManager = solverManager;
        this.problemId = problemId;
        if (!(solver instanceof DefaultSolver)) {
            throw new IllegalStateException("Impossible state: solver is not instance of " +
                    DefaultSolver.class.getSimpleName() + ".");
        }
        this.solver = (DefaultSolver<Solution_>) solver;
        this.problemFinder = problemFinder;
        this.bestSolutionConsumer = bestSolutionConsumer;
        this.finalBestSolutionConsumer = finalBestSolutionConsumer;
        this.exceptionHandler = exceptionHandler;
        solverStatus = SolverStatus.SOLVING_SCHEDULED;
        terminatedLatch = new CountDownLatch(1);
        solverStatusModifyingLock = new ReentrantLock();
    }

    public void setFinalBestSolutionFuture(Future<Solution_> finalBestSolutionFuture) {
        this.finalBestSolutionFuture = finalBestSolutionFuture;
    }

    @Override
    public ProblemId_ getProblemId() {
        return problemId;
    }

    @Override
    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    @Override
    public Solution_ call() {
        solverStatusModifyingLock.lock();
        if (solverStatus != SolverStatus.SOLVING_SCHEDULED) {
            // This job has been canceled before it started,
            // or it is already solving
            solverStatusModifyingLock.unlock();
            return problemFinder.apply(problemId);
        }
        try {
            solverStatus = SolverStatus.SOLVING_ACTIVE;
            // Create the consumer thread pool only when this solver job is active.
            consumerSupport = new ConsumerSupport<>(getProblemId(), bestSolutionConsumer, finalBestSolutionConsumer,
                    exceptionHandler, bestSolutionHolder);

            Solution_ problem = problemFinder.apply(problemId);
            // add a phase lifecycle listener that unlock the solver status lock when solving started
            solver.addPhaseLifecycleListener(new UnlockLockPhaseLifecycleListener());
            solver.addEventListener(this::onBestSolutionChangedEvent);
            final Solution_ finalBestSolution = solver.solve(problem);
            consumerSupport.consumeFinalBestSolution(finalBestSolution);
            return finalBestSolution;
        } catch (Exception e) {
            exceptionHandler.accept(problemId, e);
            bestSolutionHolder.cancelPendingChanges();
            throw new IllegalStateException("Solving failed for problemId (" + problemId + ").", e);
        } finally {
            if (solverStatusModifyingLock.isHeldByCurrentThread()) {
                // release the lock if we have it (due to solver raising an exception before solving starts);
                // This does not make it possible to do a double terminate in terminateEarly because:
                // 1. The case SOLVING_SCHEDULED is impossible (only set to SOLVING_SCHEDULED in constructor,
                //    and it was set it to SolverStatus.SOLVING_ACTIVE in the method)
                // 2. The case SOLVING_ACTIVE only calls solver.terminateEarly, so it effectively does nothing
                // 3. The case NOT_SOLVING does nothing
                solverStatusModifyingLock.unlock();
            }
            solvingTerminated();
        }
    }

    private void onBestSolutionChangedEvent(BestSolutionChangedEvent<Solution_> bestSolutionChangedEvent) {
        consumerSupport.consumeIntermediateBestSolution(bestSolutionChangedEvent.getNewBestSolution(),
                () -> bestSolutionChangedEvent.isEveryProblemChangeProcessed());
    }

    private void solvingTerminated() {
        solverStatus = SolverStatus.NOT_SOLVING;
        solverManager.unregisterSolverJob(problemId);
        terminatedLatch.countDown();
    }

    // TODO Future features
    //    @Override
    //    public void reloadProblem(Function<? super ProblemId_, Solution_> problemFinder) {
    //        throw new UnsupportedOperationException("The solver is still solving and reloadProblem() is not yet supported.");
    //    }

    @Override
    public CompletableFuture<Void> addProblemChange(ProblemChange<Solution_> problemChange) {
        Objects.requireNonNull(problemChange, () -> "A problem change (" + problemChange + ") must not be null.");
        if (solverStatus == SolverStatus.NOT_SOLVING) {
            throw new IllegalStateException("Cannot add the problem change (" + problemChange
                    + ") because the solver job (" + solverStatus + ") is not solving.");
        }

        return bestSolutionHolder.addProblemChange(solver, problemChange);
    }

    @Override
    public void terminateEarly() {
        terminatedEarly.set(true);
        try {
            solverStatusModifyingLock.lock();
            switch (solverStatus) {
                case SOLVING_SCHEDULED:
                    finalBestSolutionFuture.cancel(false);
                    solvingTerminated();
                    break;
                case SOLVING_ACTIVE:
                    // Indirectly triggers solvingTerminated()
                    // No need to cancel the finalBestSolutionFuture as it will finish normally.
                    solver.terminateEarly();
                    break;
                case NOT_SOLVING:
                    // Do nothing, solvingTerminated() already called
                    break;
                default:
                    throw new IllegalStateException("Unsupported solverStatus (" + solverStatus + ").");
            }
            try {
                // Don't return until bestSolutionConsumer won't be called anymore
                terminatedLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.warn("The terminateEarly() call is interrupted.", e);
            }
        } finally {
            solverStatusModifyingLock.unlock();
        }
    }

    @Override
    public boolean isTerminatedEarly() {
        return terminatedEarly.get();
    }

    @Override
    public Solution_ getFinalBestSolution() throws InterruptedException, ExecutionException {
        try {
            return finalBestSolutionFuture.get();
        } catch (CancellationException cancellationException) {
            LOGGER.debug("The terminateEarly() has been called before the solver job started solving. "
                    + "Retrieving the input problem instead.");
            return problemFinder.apply(problemId);
        }
    }

    @Override
    public Duration getSolvingDuration() {
        SolverScope<Solution_> solverScope = solver.getSolverScope();
        Long startingSystemTimeMillis = solverScope.getStartingSystemTimeMillis();
        if (startingSystemTimeMillis == null) {
            // The solver hasn't started yet
            return Duration.ZERO;
        }
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            // The solver hasn't ended yet
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return Duration.ofMillis(endingSystemTimeMillis - startingSystemTimeMillis);
    }

    void close() {
        if (consumerSupport != null) {
            consumerSupport.close();
            consumerSupport = null;
        }
    }

    /**
     * A listener that unlocks the solverStatusModifyingLock when Solving has started.
     *
     * It prevents the following scenario caused by unlocking before Solving started:
     *
     * Thread 1:
     * solverStatusModifyingLock.unlock()
     * >solver.solve(...) // executes second
     *
     * Thread 2:
     * case SOLVING_ACTIVE:
     * >solver.terminateEarly(); // executes first
     *
     * The solver.solve() call resets the terminateEarly flag, and thus the solver will not be terminated
     * by the call, which means terminatedLatch will not be decremented, causing Thread 2 to wait forever
     * (at least until another Thread calls terminateEarly again).
     *
     * To prevent Thread 2 from potentially waiting forever, we only unlock the lock after the
     * solvingStarted phase lifecycle event is fired, meaning the terminateEarly flag will not be
     * reset and thus the solver will actually terminate.
     */
    private final class UnlockLockPhaseLifecycleListener extends PhaseLifecycleListenerAdapter<Solution_> {
        @Override
        public void solvingStarted(SolverScope<Solution_> solverScope) {
            // The solvingStarted event can be emitted as a result of addProblemChange().
            if (solverStatusModifyingLock.isLocked()) {
                solverStatusModifyingLock.unlock();
            }
        }
    }
}
