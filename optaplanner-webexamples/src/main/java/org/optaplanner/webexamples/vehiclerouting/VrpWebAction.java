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

package org.optaplanner.webexamples.vehiclerouting;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpSession;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

public class VrpWebAction {

    private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);

    public void setup(HttpSession session) {
        terminateEarly(session);

        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        session.setAttribute(VrpSessionAttributeName.SOLVER, solver);

        URL unsolvedSolutionURL = getClass().getResource("/org/optaplanner/webexamples/vehiclerouting/A-n33-k6.vrp");
        VehicleRoutingSolution unsolvedSolution = (VehicleRoutingSolution) new VehicleRoutingImporter(true)
                .readSolution(unsolvedSolutionURL);
        session.setAttribute(VrpSessionAttributeName.SHOWN_SOLUTION, unsolvedSolution);
    }

    public void solve(final HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(VrpSessionAttributeName.SOLVER);
        final VehicleRoutingSolution unsolvedSolution = (VehicleRoutingSolution) session.getAttribute(VrpSessionAttributeName.SHOWN_SOLUTION);

        solver.addEventListener(new SolverEventListener<VehicleRoutingSolution>() {
            public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> event) {
                VehicleRoutingSolution bestSolution = event.getNewBestSolution();
                session.setAttribute(VrpSessionAttributeName.SHOWN_SOLUTION, bestSolution);
            }
        });
        solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve(unsolvedSolution);
            }
        });
    }

    public void terminateEarly(HttpSession session) {
        final Solver solver = (Solver) session.getAttribute(VrpSessionAttributeName.SOLVER);
        if (solver != null) {
            solver.terminateEarly();
            session.setAttribute(VrpSessionAttributeName.SOLVER, null);
        }
    }

}
