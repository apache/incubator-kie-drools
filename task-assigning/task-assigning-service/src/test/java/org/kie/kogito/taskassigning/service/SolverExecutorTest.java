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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SolverExecutorTest extends RunnableBaseTest<SolverExecutor> {

    private Solver<TaskAssigningSolution> solver;

    @Mock
    private TaskAssigningSolution solution;

    @Mock
    private SolverFactory<TaskAssigningSolution> solverFactory;

    @Mock
    private SolverEventListener<TaskAssigningSolution> eventListener;

    @Captor
    private ArgumentCaptor<SolverEventListener<TaskAssigningSolution>> eventListenerCaptor;

    @Mock
    private BestSolutionChangedEvent<TaskAssigningSolution> event;

    private CountDownLatch startFinished = new CountDownLatch(1);

    @Override
    protected SolverExecutor createRunnableBase() {
        solver = spy(new SolverMock());
        return spy(new SolverExecutor(solverFactory, eventListener));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void start() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();
        doReturn(solver).when(solverFactory).buildSolver();
        runnableBase.start(solution);

        // wait for the start initialization to finish
        startFinished.await();

        assertThat(runnableBase.isStarted()).isTrue();

        verify(solver).addEventListener(eventListenerCaptor.capture());
        eventListenerCaptor.getValue().bestSolutionChanged(event);
        verify(eventListener).bestSolutionChanged(event);

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();
    }

    @Test
    void startWithFailure() {
        doReturn(solver).when(solverFactory).buildSolver();
        runnableBase.start(solution);
        Assertions.assertThatThrownBy(() -> runnableBase.start(solution))
                .hasMessage("start method can only be invoked when the status is STOPPED");
    }

    @Test
    void startWithBuildFailure() {
        RuntimeException error = new RuntimeException("An error was produced...!");
        doThrow(error).when(solverFactory).buildSolver();
        Assertions.assertThatThrownBy(() -> runnableBase.start(solution))
                .hasMessage(error.getMessage());

        assertThat(runnableBase.isStopped()).isTrue();
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void stopWithSolverStarted() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();
        doReturn(solver).when(solverFactory).buildSolver();
        runnableBase.start(solution);
        // wait for the start initialization to finish
        startFinished.await();

        assertThat(runnableBase.isStarted()).isTrue();

        verify(solver).addEventListener(eventListenerCaptor.capture());
        eventListenerCaptor.getValue().bestSolutionChanged(event);
        verify(eventListener).bestSolutionChanged(event);

        runnableBase.stop();

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();
        verify(solver).terminateEarly();
    }

    @Test
    void stopWithSolverNotStarted() {
        runnableBase.stop();
        runnableBase.destroy();
        assertThat(runnableBase.isDestroyed()).isTrue();
        verify(solver, never()).terminateEarly();
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void addProblemFactChanges() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();
        doReturn(solver).when(solverFactory).buildSolver();
        runnableBase.start(solution);

        // wait for the start initialization to finish
        startFinished.await();

        List<ProblemFactChange<TaskAssigningSolution>> changes = Collections.emptyList();
        runnableBase.addProblemFactChanges(changes);
        verify(solver).addProblemFactChanges(changes);

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();
    }

    @Test
    void addProblemFactChangesWithFailure() {
        List<ProblemFactChange<TaskAssigningSolution>> changes = Collections.emptyList();
        Assertions.assertThatThrownBy(() -> runnableBase.addProblemFactChanges(changes))
                .hasMessage("SolverExecutor has not been started. Be sure it's started and not stopped or destroyed prior to executing this method");
    }

    @AfterEach
    void cleanUp() {
        disposeSolver();
    }

    private void disposeSolver() {
        //ensure the emulated solver dies in cases where we the solver termination wasn't explicitly executed as part of test.
        ((SolverMock) solver).dispose();
    }

    private class SolverMock implements Solver<TaskAssigningSolution> {

        private final Semaphore finishSolverWork = new Semaphore(0);
        private CompletableFuture<Void> action;

        public void dispose() {
            finishSolverWork.release();
        }

        @Override
        public TaskAssigningSolution solve(TaskAssigningSolution problem) {
            startFinished.countDown();
            action = CompletableFuture.runAsync(() -> {
                try {
                    // emulate a solver working in demon mode.
                    finishSolverWork.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.debug(e.getMessage());
                }
            });
            try {
                action.get();
            } catch (ExecutionException e) {
                LOGGER.debug(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug(e.getMessage());
            }
            return null;
        }

        @Override
        public boolean terminateEarly() {
            // emulate solver termination.
            finishSolverWork.release();
            return true;
        }

        @Override
        public boolean isSolving() {
            return false;
        }

        @Override
        public boolean isTerminateEarly() {
            return false;
        }

        @Override
        public boolean addProblemFactChange(ProblemFactChange<TaskAssigningSolution> problemFactChange) {
            return false;
        }

        @Override
        public boolean addProblemFactChanges(List<ProblemFactChange<TaskAssigningSolution>> problemFactChanges) {
            return false;
        }

        @Override
        public boolean isEveryProblemFactChangeProcessed() {
            return false;
        }

        @Override
        public void addEventListener(SolverEventListener<TaskAssigningSolution> eventListener) {

        }

        @Override
        public void removeEventListener(SolverEventListener<TaskAssigningSolution> eventListener) {

        }
    }
}
