package org.optaplanner.persistence.jackson.api;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJacksonRoundTripTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected static <W> W serializeAndDeserialize(W input) {
        return serializeAndDeserialize(new ObjectMapper(), input);
    }

    protected static <W> W serializeAndDeserialize(ObjectMapper objectMapper, W input) {
        String jsonString;
        W output;
        try {
            jsonString = objectMapper.writeValueAsString(input);
            output = (W) objectMapper.readValue(jsonString, input.getClass());
        } catch (IOException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }
        return output;
    }

}
