package org.optaplanner.examples.vehiclerouting.swingui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JTabbedPane;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VehicleRoutingPanel extends SolutionPanel<VehicleRoutingSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/vehiclerouting/swingui/vehicleRoutingLogo.png";

    private final VehicleRoutingWorldPanel vehicleRoutingWorldPanel;
    private final Random demandRandom = new Random(37);

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
            if (highestLocationId < location.getId()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }

    @Override
    public void updatePanel(VehicleRoutingSolution solution) {
        vehicleRoutingWorldPanel.updatePanel(solution);
    }

    public void insertLocationAndCustomer(double longitude, double latitude) {
        final Location newLocation;
        switch (getSolution().getDistanceType()) {
            case AIR_DISTANCE:
                newLocation = new AirLocation(nextLocationId, latitude, longitude);
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
        nextLocationId++;
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        doProblemChange((vehicleRoutingSolution, problemChangeDirector) -> {
            // A SolutionCloner does not clone problem fact lists (such as locationList)
            // Shallow clone the locationList so only workingSolution is affected, not bestSolution or guiSolution
            vehicleRoutingSolution.setLocationList(new ArrayList<>(vehicleRoutingSolution.getLocationList()));
            // Add the problem fact itself
            problemChangeDirector.addProblemFact(newLocation, vehicleRoutingSolution.getLocationList()::add);

            Customer newCustomer = createCustomer(vehicleRoutingSolution, newLocation);
            // A SolutionCloner clones planning entity lists (such as customerList), so no need to clone the customerList here
            // Add the planning entity itself
            problemChangeDirector.addProblemFact(newCustomer, vehicleRoutingSolution.getCustomerList()::add);
        });
    }

    protected Customer createCustomer(VehicleRoutingSolution solution, Location newLocation) {
        int demand = demandRandom.nextInt(10) + 1; // Demand must not be 0.
        if (solution instanceof TimeWindowedVehicleRoutingSolution) {
            TimeWindowedDepot timeWindowedDepot = (TimeWindowedDepot) solution.getDepotList().get(0);
            long windowTime = (timeWindowedDepot.getDueTime() - timeWindowedDepot.getReadyTime()) / 4L;
            long readyTime = demandRandom.longs(0, windowTime * 3L)
                    .findAny()
                    .orElseThrow();
            return new TimeWindowedCustomer(newLocation.getId(), newLocation, demand,
                    readyTime, readyTime + windowTime, Math.min(10000L, windowTime / 2L));
        } else {
            return new Customer(newLocation.getId(), newLocation, demand);
        }
    }

}
