/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.swingui;

import java.awt.BorderLayout;
import java.util.Random;
import javax.swing.JTabbedPane;

import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VehicleRoutingPanel extends SolutionPanel<VehicleRoutingSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/vehiclerouting/swingui/vehicleRoutingLogo.png";

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
    public void resetPanel(VehicleRoutingSolution solution) {
        vehicleRoutingWorldPanel.resetPanel(solution);
        resetNextLocationId();
    }

    private void resetNextLocationId() {
        long highestLocationId = 0L;
        for (Location location : getSolution().getLocationList()) {
            if (highestLocationId < location.getId().longValue()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }

    @Override
    public void updatePanel(VehicleRoutingSolution solution) {
        vehicleRoutingWorldPanel.updatePanel(solution);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertLocationAndCustomer(double longitude, double latitude) {
        final Location newLocation;
        switch (getSolution().getDistanceType()) {
            case AIR_DISTANCE:
                newLocation = new AirLocation();
                break;
            case ROAD_DISTANCE:
                logger.warn("Adding locations for a road distance dataset is not supported.");
                return;
            case SEGMENTED_ROAD_DISTANCE:
                logger.warn("Adding locations for a segmented road distance dataset is not supported.");
                return;
            default:
                throw new IllegalStateException("The distanceType (" + getSolution().getDistanceType()
                        + ") is not implemented.");
        }
        newLocation.setId(nextLocationId);
        nextLocationId++;
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        doProblemFactChange(scoreDirector -> {
            VehicleRoutingSolution solution = scoreDirector.getWorkingSolution();
            scoreDirector.beforeProblemFactAdded(newLocation);
            solution.getLocationList().add(newLocation);
            scoreDirector.afterProblemFactAdded(newLocation);
            Customer newCustomer = createCustomer(solution, newLocation);
            scoreDirector.beforeEntityAdded(newCustomer);
            solution.getCustomerList().add(newCustomer);
            scoreDirector.afterEntityAdded(newCustomer);
            scoreDirector.triggerVariableListeners();
        });
    }

    protected Customer createCustomer(VehicleRoutingSolution solution, Location newLocation) {
        Customer newCustomer;
        if (solution instanceof TimeWindowedVehicleRoutingSolution) {
            TimeWindowedCustomer newTimeWindowedCustomer = new TimeWindowedCustomer();
            TimeWindowedDepot timeWindowedDepot = (TimeWindowedDepot) solution.getDepotList().get(0);
            long windowTime = (timeWindowedDepot.getDueTime() - timeWindowedDepot.getReadyTime()) / 4L;
            long readyTime = RandomUtils.nextLong(demandRandom, windowTime * 3L);
            newTimeWindowedCustomer.setReadyTime(readyTime);
            newTimeWindowedCustomer.setDueTime(readyTime + windowTime);
            newTimeWindowedCustomer.setServiceDuration(Math.min(10000L, windowTime / 2L));
            newCustomer = newTimeWindowedCustomer;
        } else {
            newCustomer = new Customer();
        }
        newCustomer.setId(newLocation.getId());
        newCustomer.setLocation(newLocation);
        // Demand must not be 0
        newCustomer.setDemand(demandRandom.nextInt(10) + 1);
        return newCustomer;
    }

}
