package org.optaplanner.examples.vehiclerouting.domain.location;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class RoadLocationKeySerializer extends JsonSerializer<RoadLocation> {

    @Override
    public void serialize(RoadLocation roadLocation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeFieldId(roadLocation.getId());
    }
}
