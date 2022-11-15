package org.optaplanner.examples.vehiclerouting.domain.location;

import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * @see VehicleRoutingSolutionFileIO
 */
final class RoadLocationKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String value, DeserializationContext deserializationContext) {
        return new RoadLocation(Long.parseLong(value)); // Need to be de-duplicated.
    }
}
