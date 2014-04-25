/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cloudbalancing.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;

public class CloudBalancingDaemonTest {

    private Phaser phaser = new Phaser(2);

    private Queue<CloudProcess> notYetAddedProcessQueue = new LinkedList<CloudProcess>();

    @Test(timeout = 600000)
    public void daemon() { // In main thread
        Solver solver = buildSolver();
        CloudBalance cloudBalance = buildPlanningProblem();
        SolverThread solverThread = new SolverThread(solver, cloudBalance);
        solverThread.start();
        // Wait for the Solver to boot
        phaser.arriveAndAwaitAdvance();

        for (int i = 0; i < 8; i++) {
            CloudProcess process = notYetAddedProcessQueue.poll();
            solver.addProblemFactChange(new AddProcessChange(process));
        }
        // Wait until those AddProcessChanges are processed
        phaser.arriveAndAwaitAdvance();

        while (!notYetAddedProcessQueue.isEmpty()) {
            CloudProcess process = notYetAddedProcessQueue.poll();
            solver.addProblemFactChange(new AddProcessChange(process));
        }
        // Wait until those AddProcessChanges are processed
        phaser.arriveAndAwaitAdvance();

        solver.terminateEarly();
        try {
            // Wait until the solver thread dies.
            solverThread.join();
        } catch (InterruptedException e) {
            throw new IllegalStateException("SolverThread did not die.", e);
        }
    }

    private class SolverThread extends Thread implements SolverEventListener<CloudBalance> {

        private final Solver solver;
        private final CloudBalance cloudBalance;

        private SolverThread(Solver solver, CloudBalance cloudBalance) {
            this.solver = solver;
            this.cloudBalance = cloudBalance;
        }

        @Override
        public void run() { // In solver thread
            solver.addEventListener(this);
            solver.solve(cloudBalance);
        }

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<CloudBalance> event) { // In solver thread
            if (event.isEveryProblemFactChangeProcessed() && event.isNewBestSolutionInitialized()
                    && event.getNewBestSolution().getScore().isFeasible()) {
                phaser.arrive();
            }
        }

    }

    private static class AddProcessChange implements ProblemFactChange {

        private final CloudProcess process;

        private AddProcessChange(CloudProcess process) {
            this.process = process;
        }

        @Override
        public void doChange(ScoreDirector scoreDirector) { // In solver thread
            CloudBalance cloudBalance = (CloudBalance) scoreDirector.getWorkingSolution();
            scoreDirector.beforeEntityAdded(process);
            cloudBalance.getProcessList().add(process);
            scoreDirector.afterEntityAdded(process);
        }

    }

    protected Solver buildSolver() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml");
        solverFactory.getSolverConfig().setDaemon(true);
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setMillisecondsSpentLimit(1500L);
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
        return solverFactory.buildSolver();
    }

    private CloudBalance buildPlanningProblem() {
        CloudBalance cloudBalance = new CloudBalancingGenerator().createCloudBalance(4, 12);
        notYetAddedProcessQueue.addAll(cloudBalance.getProcessList());
        cloudBalance.setProcessList(new ArrayList<CloudProcess>(notYetAddedProcessQueue.size()));
        return cloudBalance;
    }

}
