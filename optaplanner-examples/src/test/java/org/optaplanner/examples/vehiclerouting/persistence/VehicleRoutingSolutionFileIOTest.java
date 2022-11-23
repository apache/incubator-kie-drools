package org.optaplanner.examples.vehiclerouting.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.HubSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;

class VehicleRoutingSolutionFileIOTest {

    private File solutionFile = null;

    @BeforeEach
    void generateTempSolutionFile() throws IOException {
        VehicleRoutingSolution solution = generateSolution();
        solutionFile = Files.createTempFile("optaplanner-", ".json").toFile();
        VehicleRoutingSolutionFileIO solutionFileIO = new VehicleRoutingSolutionFileIO();
        solutionFileIO.write(solution, solutionFile);
    }

    @Test
    void deserializeSegmentedLocations() throws IOException {
        VehicleRoutingSolution deserializedVehicleRoutingSolution = readSolution(solutionFile);

        List<Location> deserializedLocationList = deserializedVehicleRoutingSolution.getLocationList();
        HubSegmentLocation deserializedDepotSegmentLocation = (HubSegmentLocation) deserializedLocationList.get(0);
        HubSegmentLocation deserializedCustomerSegmentLocation = (HubSegmentLocation) deserializedLocationList.get(1);
        RoadSegmentLocation deserializedRoadSegmentLocation = (RoadSegmentLocation) deserializedLocationList.get(2);

        assertThat(deserializedDepotSegmentLocation.getHubTravelDistanceMap()).hasSize(1);
        assertThat(deserializedDepotSegmentLocation.getNearbyTravelDistanceMap()).hasSize(1);
        assertThat(deserializedCustomerSegmentLocation.getHubTravelDistanceMap()).hasSize(1);
        assertThat(deserializedCustomerSegmentLocation.getNearbyTravelDistanceMap()).hasSize(1);
        assertThat(deserializedRoadSegmentLocation.getHubTravelDistanceMap()).hasSize(2);

        IdentityHashMap<? super Location, ? super Location> identityMap = new IdentityHashMap<>();
        deserializedLocationList.forEach(location -> identityMap.put(location, location));
        deserializedVehicleRoutingSolution.getCustomerList()
                .forEach(customer -> identityMap.put(customer.getLocation(), customer.getLocation()));

        deserializedDepotSegmentLocation.getHubTravelDistanceMap().keySet()
                .forEach(segmentLocation -> identityMap.put(segmentLocation, segmentLocation));
        deserializedDepotSegmentLocation.getNearbyTravelDistanceMap().keySet()
                .forEach(segmentLocation -> identityMap.put(segmentLocation, segmentLocation));

        deserializedCustomerSegmentLocation.getHubTravelDistanceMap().keySet()
                .forEach(segmentLocation -> identityMap.put(segmentLocation, segmentLocation));
        deserializedCustomerSegmentLocation.getNearbyTravelDistanceMap().keySet()
                .forEach(segmentLocation -> identityMap.put(segmentLocation, segmentLocation));

        deserializedRoadSegmentLocation.getHubTravelDistanceMap().keySet()
                .forEach(segmentLocation -> identityMap.put(segmentLocation, segmentLocation));
        // Make sure there are no duplicate Location instances after deserialization.
        assertThat(identityMap).hasSize(3);
    }

    private static VehicleRoutingSolution generateSolution() {
        HubSegmentLocation depotLocation = new HubSegmentLocation(0);
        HubSegmentLocation customerLocation = new HubSegmentLocation(1);
        RoadSegmentLocation roadSegmentLocation = new RoadSegmentLocation(2);

        depotLocation.setNearbyTravelDistanceMap(Map.of(roadSegmentLocation, 1.0));
        depotLocation.setHubTravelDistanceMap(Map.of(customerLocation, 2.0));
        customerLocation.setNearbyTravelDistanceMap(Map.of(roadSegmentLocation, 3.0));
        customerLocation.setHubTravelDistanceMap(Map.of(depotLocation, 4.0));
        roadSegmentLocation.setHubTravelDistanceMap(Map.of(depotLocation, 5.0, customerLocation, 6.0));

        Depot depot = new Depot(0, depotLocation);
        Vehicle vehicle = new Vehicle(0, 1, depot);
        Customer customer = new Customer(0, customerLocation, 1);
        customer.setVehicle(vehicle);
        vehicle.setCustomers(List.of(customer));

        VehicleRoutingSolution vehicleRoutingSolution = new VehicleRoutingSolution();
        vehicleRoutingSolution.setName("Dummy VRP");
        vehicleRoutingSolution.setDistanceType(DistanceType.SEGMENTED_ROAD_DISTANCE);
        vehicleRoutingSolution.setLocationList(List.of(depotLocation, customerLocation, roadSegmentLocation));
        vehicleRoutingSolution.setDepotList(List.of(depot));
        vehicleRoutingSolution.setVehicleList(List.of(vehicle));
        vehicleRoutingSolution.setCustomerList(List.of(customer));
        return vehicleRoutingSolution;
    }

    private static VehicleRoutingSolution readSolution(File solutionFile) throws IOException {
        try {
            VehicleRoutingSolutionFileIO solutionFileIO = new VehicleRoutingSolutionFileIO();
            return solutionFileIO.read(solutionFile);
        } catch (Exception e) {
            throw new IOException("Unable to read the test resource ( " + solutionFile + " ).", e);
        }
    }
}
