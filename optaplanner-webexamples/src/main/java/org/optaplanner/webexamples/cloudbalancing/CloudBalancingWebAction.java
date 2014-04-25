/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.webexamples.cloudbalancing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpSession;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;

import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;

public class CloudBalancingWebAction {

    private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);

    public void setup(HttpSession session) {
        terminateEarly(session);

        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        session.setAttribute(CloudBalancingSessionAttributeName.SOLVER, solver);

        // Load a problem with 40 computers and 120 processes
        CloudBalance unsolvedSolution = new CloudBalancingGenerator(true).createCloudBalance(100, 300);
        session.setAttribute(CloudBalancingSessionAttributeName.SHOWN_SOLUTION, unsolvedSolution);
    }

    public void solve(final HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(CloudBalancingSessionAttributeName.SOLVER);
        final CloudBalance unsolvedSolution = (CloudBalance) session.getAttribute(CloudBalancingSessionAttributeName.SHOWN_SOLUTION);

        solver.addEventListener(new SolverEventListener<CloudBalance>() {
            public void bestSolutionChanged(BestSolutionChangedEvent<CloudBalance> event) {
                CloudBalance bestSolution = event.getNewBestSolution();
                session.setAttribute(CloudBalancingSessionAttributeName.SHOWN_SOLUTION, bestSolution);
            }
        });
        solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve(unsolvedSolution);
            }
        });
    }

    public void terminateEarly(HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(CloudBalancingSessionAttributeName.SOLVER);
        if (solver != null) {
            solver.terminateEarly();
            session.setAttribute(CloudBalancingSessionAttributeName.SOLVER, null);
        }
    }

}
