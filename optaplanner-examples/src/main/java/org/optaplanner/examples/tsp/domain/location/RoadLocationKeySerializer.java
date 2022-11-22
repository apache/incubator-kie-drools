package org.optaplanner.examples.tsp.domain.location;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class RoadLocationKeySerializer extends JsonSerializer<RoadLocation> {

    private final ObjectIdGenerator<String> idGenerator = new JacksonUniqueIdGenerator();

    @Override
    public void serialize(RoadLocation roadLocation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        Object jsonId = serializerProvider.findObjectId(roadLocation, idGenerator)
                .generateId(roadLocation);
        jsonGenerator.writeFieldName(jsonId.toString());
    }
}
