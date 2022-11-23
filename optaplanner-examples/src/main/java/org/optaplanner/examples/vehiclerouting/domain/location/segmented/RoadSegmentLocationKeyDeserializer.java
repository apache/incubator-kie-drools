package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;

/**
 * @see VehicleRoutingSolutionFileIO
 */
final class RoadSegmentLocationKeyDeserializer extends AbstractKeyDeserializer<RoadSegmentLocation> {

    public RoadSegmentLocationKeyDeserializer() {
        super(RoadSegmentLocation.class);
    }

    @Override
    protected RoadSegmentLocation createInstance(long id) {
        return new RoadSegmentLocation(id);
    }
}
