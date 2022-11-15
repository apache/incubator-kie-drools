package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * @see VehicleRoutingSolutionFileIO
 */
final class RoadSegmentLocationKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return new RoadSegmentLocation(Long.parseLong(key));
    }
}
