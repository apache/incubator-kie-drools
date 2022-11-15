package org.optaplanner.examples.vehiclerouting.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.HubSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;

class VehicleRoutingSolutionFileIOTest {

    private static final String SEGMENTED_VRP_RESOURCE = "vehiclerouting-segmented.json";

    @Test
    void deserializeSegmentedLocations() throws IOException {
        VehicleRoutingSolution deserializedVehicleRoutingSolution = readSolution(SEGMENTED_VRP_RESOURCE);

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

    private static VehicleRoutingSolution readSolution(String resource) throws IOException {
        URL resourceUrl = VehicleRoutingSolutionFileIOTest.class.getResource(resource);
        try {
            File file = Paths.get(Objects.requireNonNull(resourceUrl).toURI()).toFile();
            VehicleRoutingSolutionFileIO solutionFileIO = new VehicleRoutingSolutionFileIO();
            return solutionFileIO.read(file);
        } catch (URISyntaxException e) {
            throw new IOException("Unable to read the test resource ( " + resource + " ).", e);
        }
    }
}
