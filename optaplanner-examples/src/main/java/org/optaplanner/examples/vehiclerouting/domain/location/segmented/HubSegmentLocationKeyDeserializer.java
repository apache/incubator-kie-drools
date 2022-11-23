package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;

/**
 * @see VehicleRoutingSolutionFileIO
 */
final class HubSegmentLocationKeyDeserializer extends AbstractKeyDeserializer<HubSegmentLocation> {

    public HubSegmentLocationKeyDeserializer() {
        super(HubSegmentLocation.class);
    }

    @Override
    protected HubSegmentLocation createInstance(long id) {
        return new HubSegmentLocation(id);
    }
}
