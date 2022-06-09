package org.optaplanner.core.impl.solver.termination;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.change.ProblemChangeAdapter;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

/**
 * Concurrency notes:
 * Condition predicate on ({@link #problemFactChangeQueue} is not empty or {@link #terminatedEarly} is true).
 */
public class BasicPlumbingTermination<Solution_> extends AbstractTermination<Solution_> {

    protected final boolean daemon;

    protected boolean terminatedEarly = false;

    protected BlockingQueue<ProblemChangeAdapter<Solution_>> problemFactChangeQueue = new LinkedBlockingQueue<>();

    protected boolean problemFactChangesBeingProcessed = false;

    public BasicPlumbingTermination(boolean daemon) {
        this.daemon = daemon;
    }

    // ************************************************************************
    // Plumbing worker methods
    // ************************************************************************

    /**
     * This method is thread-safe.
     */
    public synchronized void resetTerminateEarly() {
        terminatedEarly = false;
    }

    /**
     * This method is thread-safe.
     * <p>
     * Concurrency note: unblocks {@link #waitForRestartSolverDecision()}.
     *
     * @return true if successful
     */
    public synchronized boolean terminateEarly() {
        boolean terminationEarlySuccessful = !terminatedEarly;
        terminatedEarly = true;
        notifyAll();
        return terminationEarlySuccessful;
    }

    /**
     * This method is thread-safe.
     */
    public synchronized boolean isTerminateEarly() {
        return terminatedEarly;
    }

    /**
     * If this returns true, then the problemFactChangeQueue is definitely not empty.
     * <p>
     * Concurrency note: Blocks until {@link #problemFactChangeQueue} is not empty or {@link #terminatedEarly} is true.
     *
     * @return true if the solver needs to be restarted
     */
    public synchronized boolean waitForRestartSolverDecision() {
        if (!daemon) {
            return !problemFactChangeQueue.isEmpty() && !terminatedEarly;
        } else {
            while (problemFactChangeQueue.isEmpty() && !terminatedEarly) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Solver thread was interrupted during Object.wait().", e);
                }
            }
            return !terminatedEarly;
        }
    }

    /**
     * Concurrency note: unblocks {@link #waitForRestartSolverDecision()}.
     *
     * @param problemChange never null
     * @return as specified by {@link Collection#add}
     */
    public synchronized boolean addProblemChange(ProblemChangeAdapter<Solution_> problemChange) {
        boolean added = problemFactChangeQueue.add(problemChange);
        notifyAll();
        return added;
    }

    /**
     * Concurrency note: unblocks {@link #waitForRestartSolverDecision()}.
     *
     * @param problemChangeList never null
     * @return as specified by {@link Collection#add}
     */
    public synchronized boolean addProblemChanges(List<ProblemChangeAdapter<Solution_>> problemChangeList) {
        boolean added = problemFactChangeQueue.addAll(problemChangeList);
        notifyAll();
        return added;
    }

    public synchronized BlockingQueue<ProblemChangeAdapter<Solution_>> startProblemFactChangesProcessing() {
        problemFactChangesBeingProcessed = true;
        return problemFactChangeQueue;
    }

    public synchronized void endProblemFactChangesProcessing() {
        problemFactChangesBeingProcessed = false;
    }

    public synchronized boolean isEveryProblemFactChangeProcessed() {
        return problemFactChangeQueue.isEmpty() && !problemFactChangesBeingProcessed;
    }

    // ************************************************************************
    // Termination worker methods
    // ************************************************************************

    @Override
    public synchronized boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        // Destroying a thread pool with solver threads will only cause it to interrupt those solver threads,
        // it won't call Solver.terminateEarly()
        if (Thread.currentThread().isInterrupted() // Does not clear the interrupted flag
                // Avoid duplicate log message because this method is called twice:
                // - in the phase step loop (every phase termination bridges to the solver termination)
                // - in the solver's phase loop
                && !terminatedEarly) {
            logger.info("The solver thread got interrupted, so this solver is terminating early.");
            terminatedEarly = true;
        }
        return terminatedEarly || !problemFactChangeQueue.isEmpty();
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        throw new IllegalStateException(BasicPlumbingTermination.class.getSimpleName()
                + " configured only as solver termination."
                + " It is always bridged to phase termination.");
    }

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        return -1.0; // Not supported
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        throw new IllegalStateException(BasicPlumbingTermination.class.getSimpleName()
                + " configured only as solver termination."
                + " It is always bridged to phase termination.");
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        return this;
    }

    @Override
    public String toString() {
        return "BasicPlumbing()";
    }

}
