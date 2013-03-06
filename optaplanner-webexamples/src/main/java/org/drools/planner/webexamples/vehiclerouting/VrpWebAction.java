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

package org.drools.planner.webexamples.vehiclerouting;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpSession;

import org.drools.planner.config.SolverFactory;
import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.event.BestSolutionChangedEvent;
import org.drools.planner.core.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.VrpSchedule;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionImporter;

public class VrpWebAction {

    private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);

    public void setup(HttpSession session) {
        SolverFactory solverFactory = new XmlSolverFactory(
                "/org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        session.setAttribute(VrpSessionAttributeName.SOLVER, solver);

        URL unsolvedSolutionURL = getClass().getResource("/org/drools/planner/webexamples/vehiclerouting/A-n33-k6.vrp");
        VrpSchedule unsolvedSolution = (VrpSchedule) new VehicleRoutingSolutionImporter()
                .readSolution(unsolvedSolutionURL);
        session.setAttribute(VrpSessionAttributeName.SHOWN_SOLUTION, unsolvedSolution);
    }


    public void solve(final HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(VrpSessionAttributeName.SOLVER);
        VrpSchedule unsolvedSolution = (VrpSchedule) session.getAttribute(VrpSessionAttributeName.SHOWN_SOLUTION);

        solver.setPlanningProblem(unsolvedSolution);
        solver.addEventListener(new SolverEventListener() {
            public void bestSolutionChanged(BestSolutionChangedEvent event) {
                VrpSchedule bestSolution = (VrpSchedule) event.getNewBestSolution();
                session.setAttribute(VrpSessionAttributeName.SHOWN_SOLUTION, bestSolution);
            }
        });
        solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve();
            }
        });
    }

    public void terminateEarly(HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(VrpSessionAttributeName.SOLVER);
        solver.terminateEarly();
    }

}
