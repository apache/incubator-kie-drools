package org.optaplanner.examples.vehiclerouting.persistence;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.HubSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;

public class VehicleRoutingSolutionFileIO extends AbstractJsonSolutionFileIO<VehicleRoutingSolution> {

    public VehicleRoutingSolutionFileIO() {
        super(VehicleRoutingSolution.class);
    }

    @Override
    public VehicleRoutingSolution read(File inputSolutionFile) {
        VehicleRoutingSolution vehicleRoutingSolution = super.read(inputSolutionFile);

        if (vehicleRoutingSolution.getDistanceType() == DistanceType.ROAD_DISTANCE) {
            deduplicateRoadLocations(vehicleRoutingSolution);
        } else if (vehicleRoutingSolution.getDistanceType() == DistanceType.SEGMENTED_ROAD_DISTANCE) {
            deduplicateRoadSegments(vehicleRoutingSolution);
        }

        return vehicleRoutingSolution;
    }

    private void deduplicateRoadLocations(VehicleRoutingSolution vehicleRoutingSolution) {
        var roadLocationList = vehicleRoutingSolution.getLocationList().stream()
                .filter(location -> location instanceof RoadLocation)
                .map(location -> (RoadLocation) location)
                .collect(Collectors.toList());
        var locationsById = roadLocationList.stream()
                .collect(Collectors.toMap(RoadLocation::getId, Function.identity()));
        /*
         * Replace the duplicate RoadLocation instances in the travelDistanceMap by references to instances from
         * the locationList.
         */
        for (RoadLocation roadLocation : roadLocationList) {
            var newTravelDistanceMap = deduplicateMap(roadLocation.getTravelDistanceMap(),
                    locationsById, RoadLocation::getId);
            roadLocation.setTravelDistanceMap(newTravelDistanceMap);
        }
    }

    private void deduplicateRoadSegments(VehicleRoutingSolution vehicleRoutingSolution) {
        var hubSegmentLocationList = vehicleRoutingSolution.getLocationList().stream()
                .filter(location -> location instanceof HubSegmentLocation)
                .map(location -> (HubSegmentLocation) location)
                .collect(Collectors.toList());
        var roadSegmentLocationList = vehicleRoutingSolution.getLocationList().stream()
                .filter(location -> location instanceof RoadSegmentLocation)
                .map(location -> (RoadSegmentLocation) location)
                .collect(Collectors.toList());
        var hubSegmentLocationsById = hubSegmentLocationList.stream()
                .collect(Collectors.toMap(HubSegmentLocation::getId, Function.identity()));
        var roadSegmentLocationsById = roadSegmentLocationList.stream()
                .collect(Collectors.toMap(RoadSegmentLocation::getId, Function.identity()));

        for (HubSegmentLocation hubSegmentLocation : hubSegmentLocationList) {
            var newHubTravelDistanceMap = deduplicateMap(hubSegmentLocation.getHubTravelDistanceMap(),
                    hubSegmentLocationsById, HubSegmentLocation::getId);
            var newNearbyTravelDistanceMap = deduplicateMap(hubSegmentLocation.getNearbyTravelDistanceMap(),
                    roadSegmentLocationsById, RoadSegmentLocation::getId);
            hubSegmentLocation.setHubTravelDistanceMap(newHubTravelDistanceMap);
            hubSegmentLocation.setNearbyTravelDistanceMap(newNearbyTravelDistanceMap);
        }

        for (RoadSegmentLocation roadSegmentLocation : roadSegmentLocationList) {
            var newHubTravelDistanceMap = deduplicateMap(roadSegmentLocation.getHubTravelDistanceMap(),
                    hubSegmentLocationsById, HubSegmentLocation::getId);
            var newNearbyTravelDistanceMap = deduplicateMap(roadSegmentLocation.getNearbyTravelDistanceMap(),
                    roadSegmentLocationsById, RoadSegmentLocation::getId);
            roadSegmentLocation.setHubTravelDistanceMap(newHubTravelDistanceMap);
            roadSegmentLocation.setNearbyTravelDistanceMap(newNearbyTravelDistanceMap);
        }
    }
}
