/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service;

import java.util.List;

import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.service.RunnableBase.Status.DESTROYED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTING;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STOPPED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STOPPING;

/**
 * This class is intended to manage the solver life-cycle in a multi-threaded environment.
 */
public class SolverExecutor extends RunnableBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolverExecutor.class);

    private Solver<TaskAssigningSolution> solver;
    private TaskAssigningSolution solution;
    private SolverFactory<TaskAssigningSolution> solverFactory;
    private SolverEventListener<TaskAssigningSolution> eventListener;

    public SolverExecutor(SolverFactory<TaskAssigningSolution> solverFactory,
            SolverEventListener<TaskAssigningSolution> eventListener) {
        this.solverFactory = solverFactory;
        this.eventListener = eventListener;
    }

    /**
     * This method is invoked from a different thread for starting the solver and must be invoked only one time.
     * If the solver was already started an exception is thrown so callers of this method should call the isStarted()
     * method to verify. This method is not thread-safe so it's expected that any synchronization required between the
     * isStarted(), start() and stop() methods is performed by the callers. However it's normally not expected that multiple
     * threads might try to start the same solver runner instance.
     * 
     * @param solution a valid solution for starting the solver with.
     */
    public void start(final TaskAssigningSolution solution) {
        startCheck();
        this.solution = solution;
        try {
            this.solver = solverFactory.buildSolver();
        } catch (Exception e) {
            status.set(STOPPED);
            throw new SolverExecutorException(e.getMessage(), e);
        }

        solver.addEventListener(event -> {
            if (isAlive() && isStarted()) {
                eventListener.bestSolutionChanged(event);
            }
        });
        startPermit.release();
    }

    /**
     * @return true if the solver has been started, false in any other case.
     */
    public boolean isStarted() {
        return status.get() == STARTED;
    }

    public boolean isStopped() {
        return status.get() == STOPPED;
    }

    /**
     * Stops the solver.
     */
    public void stop() {
        if (!isDestroyed()) {
            Status previousStatus = status.getAndSet(STOPPING);
            if (previousStatus == STARTED) {
                solver.terminateEarly();
            } else {
                status.set(STOPPED);
            }
        }
    }

    /**
     * This method programmes the subsequent finalization of the solver (if it was started) that will be produced as
     * soon as possible by invoking the solver.terminateEarly() method. If the solver wasn't started it just finalizes
     * current thread.
     */
    @Override
    public void destroy() {
        if (status.getAndSet(DESTROYED) == STARTED) {
            solver.terminateEarly();
        } else {
            startPermit.release(); //un-lock in case it was waiting for a solution to start.
        }
    }

    public void addProblemFactChanges(final List<ProblemFactChange<TaskAssigningSolution>> changes) {
        if (isStarted()) {
            solver.addProblemFactChanges(changes);
        } else {
            throw new SolverExecutorException("SolverExecutor has not been started. Be sure it's started and not " +
                    "stopped or destroyed prior to executing this method");
        }
    }

    @Override
    public void run() {
        while (isAlive()) {
            try {
                LOGGER.debug("SolverExecutor is waiting for a start(solution) method invocation for starting the Solver.");
                startPermit.acquire();
                LOGGER.debug("SolverExecutor, the Solver will be started.");
                if (isAlive() && status.compareAndSet(STARTING, STARTED)) {
                    solver.solve(solution);
                    if (isAlive()) {
                        status.set(STOPPED);
                        LOGGER.debug("Solver has been stopped. It can be restarted with the start(solution) method.");
                    } else {
                        LOGGER.debug("SolverExecutor has been destroyed. No more invocations can be done on this instance.");
                    }
                }
            } catch (InterruptedException e) {
                super.destroy();
                Thread.currentThread().interrupt();
                if (solver != null && solver.isSolving()) {
                    solver.isTerminateEarly();
                }
                LOGGER.error("SolverExecutor was interrupted.", e);
            }
        }
        super.destroy();
        LOGGER.debug("SolverExecutor finished.");
    }

    public static class SolverExecutorException extends RuntimeException {

        public SolverExecutorException(String message) {
            super(message);
        }

        public SolverExecutorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
