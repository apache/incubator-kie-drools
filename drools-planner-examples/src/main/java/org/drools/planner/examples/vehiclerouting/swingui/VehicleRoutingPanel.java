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

package org.drools.planner.examples.vehiclerouting.swingui;

import java.awt.BorderLayout;
import java.util.Random;
import javax.swing.JTabbedPane;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.common.swingui.SolverAndPersistenceFrame;
import org.drools.planner.examples.tsp.domain.Visit;
import org.drools.planner.examples.vehiclerouting.domain.VrpCustomer;
import org.drools.planner.examples.vehiclerouting.domain.VrpLocation;
import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;

/**
 * TODO this code is highly unoptimized
 */
public class VehicleRoutingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/drools/planner/examples/vehiclerouting/swingui/vehicleRoutingLogo.png";

    private VehicleRoutingWorldPanel vehicleRoutingWorldPanel;

    private Random demandRandom = new Random(37);
    private Long nextLocationId = null;

    public VehicleRoutingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        vehicleRoutingWorldPanel = new VehicleRoutingWorldPanel(this);
        vehicleRoutingWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", vehicleRoutingWorldPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public VrpSchedule getSchedule() {
        return (VrpSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        VrpSchedule schedule = (VrpSchedule) solution;
        vehicleRoutingWorldPanel.resetPanel(schedule);
        resetNextLocationId();
    }

    private void resetNextLocationId() {
        long highestLocationId = 0L;
        for (VrpLocation location : getSchedule().getLocationList()) {
            if (highestLocationId < location.getId().longValue()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }

    @Override
    public void updatePanel(Solution solution) {
        VrpSchedule schedule = (VrpSchedule) solution;
        vehicleRoutingWorldPanel.updatePanel(schedule);
    }

    public void doMove(Move move) {
        solutionBusiness.doMove(move);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertLocationAndCustomer(double longitude, double latitude) {
        final VrpLocation newLocation = new VrpLocation();
        newLocation.setId(nextLocationId);
        nextLocationId++;
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                VrpSchedule solution = (VrpSchedule) scoreDirector.getWorkingSolution();
                scoreDirector.beforeProblemFactAdded(newLocation);
                solution.getLocationList().add(newLocation);
                scoreDirector.afterProblemFactAdded(newLocation);
                VrpCustomer newCustomer = new VrpCustomer();
                newCustomer.setId(newLocation.getId());
                newCustomer.setLocation(newLocation);
                // Demand must not be 0
                newCustomer.setDemand(demandRandom.nextInt(10) + 1);
                scoreDirector.beforeEntityAdded(newCustomer);
                solution.getCustomerList().add(newCustomer);
                scoreDirector.afterEntityAdded(newCustomer);
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

}
