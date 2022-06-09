package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

class RoadSegmentLocationTest {

    @Test
    void getDistance() {
        long id = 0;
        RoadSegmentLocation a = new RoadSegmentLocation(id++, 0.0, 0.0);
        RoadSegmentLocation b = new RoadSegmentLocation(id++, 0.0, 4.0);
        RoadSegmentLocation c = new RoadSegmentLocation(id++, 2.0, 0.0);
        RoadSegmentLocation d = new RoadSegmentLocation(id++, 100.0, 2.0);
        HubSegmentLocation x = new HubSegmentLocation(id++, 1.0, 0.0);
        HubSegmentLocation y = new HubSegmentLocation(id++, 1.0, 3.0);
        HubSegmentLocation z = new HubSegmentLocation(id++, 99.0, 3.0);
        a.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(a, b, c));
        a.setHubTravelDistanceMap(createHubTravelDistanceMap(a, x, y));
        b.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(b, a));
        b.setHubTravelDistanceMap(createHubTravelDistanceMap(b, x, y));
        c.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(c, a));
        c.setHubTravelDistanceMap(createHubTravelDistanceMap(c, x, y));
        d.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(d));
        d.setHubTravelDistanceMap(createHubTravelDistanceMap(d, z));
        x.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(x, a, b, c));
        x.setHubTravelDistanceMap(createHubTravelDistanceMap(x, y, z));
        y.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(y, a, b, c));
        y.setHubTravelDistanceMap(createHubTravelDistanceMap(y, x, z));
        z.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(z, d));
        z.setHubTravelDistanceMap(createHubTravelDistanceMap(z, x, y));

        assertThat(a.getDistanceTo(b)).isEqualTo(sumOfArcs(a, b));
        assertThat(a.getDistanceTo(c)).isEqualTo(sumOfArcs(a, c));
        assertThat(a.getDistanceTo(d)).isEqualTo(sumOfArcs(a, x, z, d));
        assertThat(b.getDistanceTo(a)).isEqualTo(sumOfArcs(b, a));
        assertThat(b.getDistanceTo(c)).isEqualTo(sumOfArcs(b, y, c));
        assertThat(b.getDistanceTo(d)).isEqualTo(sumOfArcs(b, y, z, d));
        assertThat(c.getDistanceTo(a)).isEqualTo(sumOfArcs(c, a));
        assertThat(c.getDistanceTo(b)).isEqualTo(sumOfArcs(c, y, b));
        assertThat(c.getDistanceTo(d)).isEqualTo(sumOfArcs(c, x, z, d));
        assertThat(d.getDistanceTo(a)).isEqualTo(sumOfArcs(d, z, x, a));
        assertThat(d.getDistanceTo(b)).isEqualTo(sumOfArcs(d, z, y, b));
        assertThat(d.getDistanceTo(c)).isEqualTo(sumOfArcs(d, z, x, c));
    }

    protected int sumOfArcs(Location fromLocation, Location... stopLocations) {
        Location previousLocation = fromLocation;
        int distance = 0;
        for (Location stopLocation : stopLocations) {
            distance += (int) (previousLocation.getAirDistanceDoubleTo(stopLocation) * 1000.0 + 0.5);
            previousLocation = stopLocation;
        }
        return distance;
    }

    private Map<HubSegmentLocation, Double> createHubTravelDistanceMap(Location fromLocation,
            HubSegmentLocation... toLocations) {
        Map<HubSegmentLocation, Double> map = new LinkedHashMap<>(toLocations.length);
        for (HubSegmentLocation toLocation : toLocations) {
            map.put(toLocation, fromLocation.getAirDistanceDoubleTo(toLocation));
        }
        return map;
    }

    protected Map<RoadSegmentLocation, Double> createNearbyTravelDistanceMap(Location fromLocation,
            RoadSegmentLocation... toLocations) {
        Map<RoadSegmentLocation, Double> map = new LinkedHashMap<>(toLocations.length);
        for (RoadSegmentLocation toLocation : toLocations) {
            map.put(toLocation, fromLocation.getAirDistanceDoubleTo(toLocation));
        }
        return map;
    }

}
