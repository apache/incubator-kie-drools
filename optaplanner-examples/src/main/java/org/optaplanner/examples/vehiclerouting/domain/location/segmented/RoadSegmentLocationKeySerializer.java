package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class RoadSegmentLocationKeySerializer extends JsonSerializer<RoadSegmentLocation> {

    @Override
    public void serialize(RoadSegmentLocation roadSegmentLocation, JsonGenerator jsonGenerator,
            SerializerProvider serializers) throws IOException {
        jsonGenerator.writeFieldId(roadSegmentLocation.getId());
    }
}
