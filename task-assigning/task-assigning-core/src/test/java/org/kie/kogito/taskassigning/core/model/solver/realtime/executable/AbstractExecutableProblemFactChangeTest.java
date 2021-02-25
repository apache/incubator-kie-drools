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
package org.kie.kogito.taskassigning.core.model.solver.realtime.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.kie.kogito.taskassigning.core.AbstractTaskAssigningCoreTest;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractExecutableProblemFactChangeTest extends AbstractTaskAssigningCoreTest {

    final Random random = new Random();
    private static final AtomicInteger changeIds = new AtomicInteger(1);

    protected static int nextChangeId() {
        return changeIds.getAndIncrement();
    }

    protected class ProgrammedProblemFactChange<C extends ProblemFactChange<TaskAssigningSolution>> {

        int id;

        private TaskAssigningSolution solutionAfterChange;

        private C change;

        public ProgrammedProblemFactChange() {
            this.id = nextChangeId();
        }

        public ProgrammedProblemFactChange(C change) {
            this.change = change;
        }

        public TaskAssigningSolution getSolutionAfterChange() {
            return solutionAfterChange;
        }

        public void setSolutionAfterChange(TaskAssigningSolution solutionAfterChange) {
            this.solutionAfterChange = solutionAfterChange;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public C getChange() {
            return change;
        }

        public void setChange(C change) {
            this.change = change;
        }
    }

    protected TaskAssigningSolution executeSequentialChanges(TaskAssigningSolution solution, List<? extends ProgrammedProblemFactChange> changes) {
        Solver<TaskAssigningSolution> solver = createDaemonSolver();

        //store the first solution that was produced by the solver for knowing how things looked like at the very
        //beginning before any change was produced.
        final TaskAssigningSolution[] initialSolution = { null };
        final AtomicInteger lastExecutedChangeId = new AtomicInteger(-1);

        final Semaphore programNextChange = new Semaphore(0);
        final Semaphore allChangesWereProduced = new Semaphore(0);

        //prepare the list of changes to program
        List<ProgrammedProblemFactChange> programmedChanges = new ArrayList<>(changes);
        List<ProgrammedProblemFactChange> scheduledChanges = new ArrayList<>();

        int totalProgrammedChanges = programmedChanges.size();
        int[] pendingChanges = { programmedChanges.size() };

        solver.addEventListener(event -> {
            if (initialSolution[0] == null) {
                //store the first produced solution for knowing how things looked like at the very beginning.
                initialSolution[0] = event.getNewBestSolution();
                //let the problem fact changes start being produced.
                programNextChange.release();
            } else if (event.isEveryProblemFactChangeProcessed() && !scheduledChanges.isEmpty()) {
                ProgrammedProblemFactChange programmedChange = scheduledChanges.get(scheduledChanges.size() - 1);
                if (lastExecutedChangeId.compareAndSet(programmedChange.getId(), -1)) {
                    programmedChange.setSolutionAfterChange(event.getNewBestSolution());
                    if (pendingChanges[0] > 0) {
                        //let the Programmed changes producer produce next change
                        programNextChange.release();
                    } else {
                        solver.terminateEarly();
                        allChangesWereProduced.release();
                    }
                }
            }
        });

        //Programmed changes producer Thread.
        CompletableFuture.runAsync(() -> {
            boolean hasMoreChanges = true;
            while (hasMoreChanges) {
                try {
                    //wait until next problem fact change can be added to the solver.
                    //by construction the lock is only released when no problem fact change is in progress.
                    programNextChange.acquire();
                    ProgrammedProblemFactChange programmedChange = programmedChanges.remove(0);
                    hasMoreChanges = !programmedChanges.isEmpty();
                    pendingChanges[0] = programmedChanges.size();
                    scheduledChanges.add(programmedChange);
                    solver.addProblemFactChange(scoreDirector -> {
                        lastExecutedChangeId.set(programmedChange.getId());
                        programmedChange.getChange().doChange(scoreDirector);
                    });
                } catch (InterruptedException e) {
                    LOGGER.error("It looks like the test Future was interrupted.", e);
                }
            }
            try {
                //wait until the solver listener has processed all the changes.
                allChangesWereProduced.acquire();
            } catch (InterruptedException e) {
                LOGGER.error("It looks like the test Future was interrupted while waiting to finish.", e);
            }
        });

        solver.solve(solution);

        assertThat(programmedChanges.isEmpty()).isTrue();
        assertThat(scheduledChanges.size()).isEqualTo(totalProgrammedChanges);
        assertThat(pendingChanges[0]).isZero();
        return initialSolution[0];
    }

    protected <T extends ProgrammedProblemFactChange> void writeProblemFactChangesTestFiles(TaskAssigningSolution initialSolution,
            String solutionResource,
            String filePrefix,
            String testType,
            List<T> programmedChanges,
            Function<T, String> solutionBeforeChange,
            Function<T, String> solutionAfterChange) throws Exception {

        String resourceName = solutionResource.substring(solutionResource.lastIndexOf("/") + 1);
        writeToTempFile(buildTestFileName(filePrefix, testType, "InitialSolution", resourceName, 0), printSolution(initialSolution));
        for (int i = 0; i < programmedChanges.size(); i++) {
            T scheduledChange = programmedChanges.get(i);
            try {
                writeToTempFile(buildTestFileName(filePrefix, testType, "WorkingSolutionBeforeChange", resourceName, i), solutionBeforeChange.apply(scheduledChange));
                writeToTempFile(buildTestFileName(filePrefix, testType, "SolutionAfterChange", resourceName, i), solutionAfterChange.apply(scheduledChange));
            } catch (Exception e) {
                LOGGER.error("An error was produced during test files writing.", e);
            }
        }
    }

    private static String buildTestFileName(String filePrefix, String testType, String solutionName, String resourceName, int changeNumber) {
        return filePrefix + "." + testType + "." + solutionName + "_" + resourceName + "_" + changeNumber + "__";
    }
}
