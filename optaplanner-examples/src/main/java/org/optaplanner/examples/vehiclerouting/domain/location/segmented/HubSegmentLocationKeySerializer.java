package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class HubSegmentLocationKeySerializer extends JsonSerializer<HubSegmentLocation> {

    @Override
    public void serialize(HubSegmentLocation hubSegmentLocation, JsonGenerator jsonGenerator,
            SerializerProvider serializers) throws IOException {
        jsonGenerator.writeFieldId(hubSegmentLocation.getId());
    }
}
