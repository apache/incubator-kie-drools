package org.optaplanner.examples.tsp.domain.location;

import java.util.Objects;

import org.optaplanner.examples.tsp.persistence.TspSolutionFileIO;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * @see TspSolutionFileIO
 */
final class RoadLocationKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String value, DeserializationContext deserializationContext) {
        String[] parts = value.split("#");
        String className = parts[0];
        if (!Objects.equals(className, RoadLocation.class.getSimpleName())) {
            throw new IllegalStateException("Impossible state: not the correct type (" + value + ").");
        }
        String id = parts[1];
        return new RoadLocation(Long.parseLong(id)); // Need to be de-duplicated in solution IO.
    }
}
